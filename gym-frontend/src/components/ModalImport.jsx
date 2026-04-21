import { useState } from 'react'
import axios from 'axios'

export default function ModalImport({ onClose, onSave }) {
  const [file, setFile] = useState(null)
  const [result, setResult] = useState(null)
  const [loading, setLoading] = useState(false)

  const downloadTemplate = () => window.open('/api/reports/template', '_blank')

  const handleUpload = async () => {
    if (!file) return alert('Selecciona un archivo')
    setLoading(true)
    const formData = new FormData()
    formData.append('file', file)
    const res = await axios.post('/api/reports/import', formData)
    setResult(res.data)
    setLoading(false)
    onSave()
  }

  return (
    <div className="modal-overlay">
      <div className="modal" style={{width: '450px'}}>
        <h2>Importar miembros</h2>
        <p className="modal-subtitle">Sube un Excel con los datos de tus miembros</p>

        <div className="modal-body">
          <div style={{background: '#f9f9f9', border: '1px solid #eee', borderRadius: '8px', padding: '1rem', marginBottom: '8px'}}>
            <p style={{fontSize: '13px', fontWeight: 500, marginBottom: '6px', color: '#111'}}>Paso 1 — Descarga la plantilla</p>
            <p style={{fontSize: '12px', color: '#888', marginBottom: '10px'}}>Llena los datos en el archivo y guárdalo.</p>
            <button className="btn" onClick={downloadTemplate}>Descargar plantilla</button>
          </div>

          <div style={{background: '#f9f9f9', border: '1px solid #eee', borderRadius: '8px', padding: '1rem'}}>
            <p style={{fontSize: '13px', fontWeight: 500, marginBottom: '6px', color: '#111'}}>Paso 2 — Sube el archivo lleno</p>
            <input
              type="file"
              accept=".xlsx"
              onChange={e => setFile(e.target.files[0])}
              style={{fontSize: '13px', color: '#111'}}
            />
          </div>

          {result && (
            <div style={{background: result.errors > 0 ? '#fef2f2' : '#f0fdf4', border: `1px solid ${result.errors > 0 ? '#fecaca' : '#bbf7d0'}`, borderRadius: '8px', padding: '1rem'}}>
              <p style={{fontSize: '13px', fontWeight: 500, color: result.errors > 0 ? '#dc2626' : '#16a34a'}}>
                ✓ Importados: {result.imported} &nbsp;|&nbsp; Errores: {result.errors}
              </p>
              {result.errorDetails?.map((e, i) => (
                <p key={i} style={{fontSize: '12px', color: '#dc2626', marginTop: '4px'}}>{e}</p>
              ))}
            </div>
          )}
        </div>

        <div className="modal-actions">
          <button className="btn" onClick={onClose}>Cerrar</button>
          <button className="btn-renovar" onClick={handleUpload} disabled={loading}>
            {loading ? 'Importando...' : 'Importar'}
          </button>
        </div>
      </div>
    </div>
  )
}