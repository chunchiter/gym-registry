export default function ModalConfirm({ message, onConfirm, onCancel }) {
  return (
    <div className="modal-overlay">
      <div className="modal" style={{width: '340px', textAlign: 'center'}}>
        <div style={{fontSize: '32px', marginBottom: '12px'}}>⚠️</div>
        <h2 style={{marginBottom: '8px'}}>¿Estás seguro?</h2>
        <p style={{fontSize: '13px', color: '#666', marginBottom: '1.5rem'}}>{message}</p>
        <div className="modal-actions" style={{justifyContent: 'center'}}>
          <button className="btn" onClick={onCancel}>Cancelar</button>
          <button className="btn-delete" style={{padding: '7px 14px', fontSize: '13px'}} onClick={onConfirm}>Eliminar</button>
        </div>
      </div>
    </div>
  )
}