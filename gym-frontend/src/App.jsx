import { useState, useEffect } from 'react'
import axios from 'axios'
import './App.css'

export default function App() {
  const [members, setMembers] = useState([])
  const [filter, setFilter] = useState('todos')
  const [search, setSearch] = useState('')
  const [stats, setStats] = useState({ total: 0, alDia: 0, porVencer: 0, vencidos: 0 })

  useEffect(() => {
    fetchMembers()
  }, [])

  const fetchMembers = async () => {
    const res = await axios.get('/api/members')
    setMembers(res.data)
    calcStats(res.data)
  }

  const calcStats = (data) => {
    const today = new Date()
    const soon = new Date()
    soon.setDate(soon.getDate() + 5)
    let alDia = 0, porVencer = 0, vencidos = 0
    data.forEach(m => {
      if (!m.lastMembership) { vencidos++; return }
      const venc = new Date(m.lastMembership.fechaVencimiento)
      if (venc < today) vencidos++
      else if (venc <= soon) porVencer++
      else alDia++
    })
    setStats({ total: data.length, alDia, porVencer, vencidos })
  }

  const getEstado = (member) => {
    if (!member.lastMembership) return 'Sin membresía'
    const today = new Date()
    const soon = new Date()
    soon.setDate(soon.getDate() + 5)
    const venc = new Date(member.lastMembership.fechaVencimiento)
    if (venc < today) return 'Vencido'
    if (venc <= soon) return 'Por vencer'
    return 'Al día'
  }

  const filtered = members.filter(m => {
    const estado = getEstado(m)
    const matchFilter = filter === 'todos' ||
      (filter === 'aldia' && estado === 'Al día') ||
      (filter === 'porvencer' && estado === 'Por vencer') ||
      (filter === 'vencidos' && estado === 'Vencido')
    const matchSearch = m.nombre.toLowerCase().includes(search.toLowerCase())
    return matchFilter && matchSearch
  })

  const downloadExcel = () => {
    window.open('/api/reports/members', '_blank')
  }

  return (
    <div className="app">
      <div className="header">
        <div>
          <p className="subtitle">Gym Manager</p>
          <h1>Panel principal</h1>
        </div>
        <div className="header-actions">
          <button className="btn">+ Nuevo miembro</button>
          <button className="btn" onClick={downloadExcel}>Descargar Excel</button>
        </div>
      </div>

      <div className="stats">
        <div className="stat-card"><p>Total miembros</p><h2>{stats.total}</h2></div>
        <div className="stat-card green"><p>Al día</p><h2>{stats.alDia}</h2></div>
        <div className="stat-card yellow"><p>Por vencer</p><h2>{stats.porVencer}</h2></div>
        <div className="stat-card red"><p>Vencidos</p><h2>{stats.vencidos}</h2></div>
      </div>

      <div className="table-card">
        <div className="table-toolbar">
          <input placeholder="Buscar miembro..." value={search} onChange={e => setSearch(e.target.value)} />
          <select value={filter} onChange={e => setFilter(e.target.value)}>
            <option value="todos">Todos</option>
            <option value="aldia">Al día</option>
            <option value="porvencer">Por vencer</option>
            <option value="vencidos">Vencidos</option>
          </select>
        </div>

        <table>
          <thead>
            <tr>
              <th>Nombre</th><th>Teléfono</th><th>Vencimiento</th><th>Último pago</th><th>Estado</th><th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map(m => {
              const estado = getEstado(m)
              const rowClass = estado === 'Vencido' ? 'row-red' : estado === 'Por vencer' ? 'row-yellow' : ''
              const badgeClass = estado === 'Al día' ? 'badge-green' : estado === 'Por vencer' ? 'badge-yellow' : 'badge-red'
              return (
                <tr key={m.id} className={rowClass}>
                  <td className="bold">{m.nombre}</td>
                  <td>{m.telefono}</td>
                  <td>{m.lastMembership?.fechaVencimiento ?? 'N/A'}</td>
                  <td>${m.lastMembership?.montoPagado ?? 0}</td>
                  <td><span className={`badge ${badgeClass}`}>{estado}</span></td>
                  <td className="actions">
                    <button className="btn-renovar">Renovar</button>
                    <button className="btn-editar">Editar</button>
                  </td>
                </tr>
              )
            })}
          </tbody>
        </table>
      </div>
    </div>
  )
}