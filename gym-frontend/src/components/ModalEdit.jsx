import { useState } from 'react'
import axios from 'axios'
import ModalConfirm from './ModalConfirm'

export default function ModalEdit({ member, onClose, onSave }) {
  const [form, setForm] = useState({
    nombre: member.nombre,
    telefono: member.telefono,
    email: member.email,
    activo: member.activo
  })
  const [showConfirm, setShowConfirm] = useState(false)

  const handleChange = e => {
    const value = e.target.type === 'checkbox' ? e.target.checked : e.target.value
    setForm({ ...form, [e.target.name]: value })
  }

  const handleSubmit = async () => {
    if (!form.nombre || !form.telefono || !form.email) return alert('Completa todos los campos')
    await axios.put(`/api/members/${member.id}`, form)
    onSave()
    onClose()
  }

  const handleDelete = async () => {
    await axios.delete(`/api/members/${member.id}`)
    onSave()
    onClose()
  }

  return (
    <>
      <div className="modal-overlay">
        <div className="modal">
          <h2>Editar miembro</h2>
          <div className="modal-body">
            <label>Nombre</label>
            <input name="nombre" value={form.nombre} onChange={handleChange} />
            <label>Teléfono</label>
            <input name="telefono" value={form.telefono} onChange={handleChange} />
            <label>Email</label>
            <input name="email" value={form.email} onChange={handleChange} />
            <div style={{display: 'flex', alignItems: 'center', gap: '8px', marginTop: '4px'}}>
              <input type="checkbox" name="activo" checked={form.activo} onChange={handleChange} id="activo" />
              <label htmlFor="activo" style={{marginBottom: 0}}>Miembro activo</label>
            </div>
          </div>
          <div className="modal-actions" style={{justifyContent: 'space-between'}}>
            <button className="btn-delete" onClick={() => setShowConfirm(true)}>Eliminar</button>
            <div style={{display: 'flex', gap: '8px'}}>
              <button className="btn" onClick={onClose}>Cancelar</button>
              <button className="btn-editar" onClick={handleSubmit}>Guardar</button>
            </div>
          </div>
        </div>
      </div>

      {showConfirm && (
        <ModalConfirm
          message={`¿Quieres eliminar a ${member.nombre}? Esta acción no se puede deshacer.`}
          onConfirm={handleDelete}
          onCancel={() => setShowConfirm(false)}
        />
      )}
    </>
  )
}