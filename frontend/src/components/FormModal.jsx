function FormModal({ title, children, submitLabel, onSubmit, onClose }) {
  return (
    <div className="modal-backdrop" role="presentation">
      <section className="modal" role="dialog" aria-modal="true" aria-labelledby="modal-title">
        <div className="modal-header">
          <h2 id="modal-title">{title}</h2>
          <button type="button" className="icon-button" onClick={onClose} aria-label="Close modal">
            X
          </button>
        </div>
        <form onSubmit={onSubmit} className="entity-form">
          {children}
          <div className="form-actions">
            <button type="button" className="btn secondary" onClick={onClose}>
              Cancel
            </button>
            <button type="submit" className="btn primary">
              {submitLabel}
            </button>
          </div>
        </form>
      </section>
    </div>
  )
}

export default FormModal
