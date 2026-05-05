function ConfirmDeleteModal({ title, message, onCancel, onConfirm }) {
  return (
    <div className="modal-backdrop" role="presentation">
      <section className="modal compact" role="dialog" aria-modal="true" aria-labelledby="delete-title">
        <div className="modal-header">
          <h2 id="delete-title">{title}</h2>
        </div>
        <p className="confirm-text">{message}</p>
        <div className="form-actions">
          <button type="button" className="btn secondary" onClick={onCancel}>
            Cancel
          </button>
          <button type="button" className="btn danger solid" onClick={onConfirm}>
            Delete
          </button>
        </div>
      </section>
    </div>
  )
}

export default ConfirmDeleteModal
