import { useEffect, useMemo, useState } from 'react'
import { Plus, Truck } from 'lucide-react'
import DataTable from '../components/DataTable.jsx'
import SearchBar from '../components/SearchBar.jsx'
import FormModal from '../components/FormModal.jsx'
import ConfirmDeleteModal from '../components/ConfirmDeleteModal.jsx'
import { createSupplier, deleteSupplier, getSuppliers, updateSupplier } from '../api/supplierApi.js'
import { useAuth } from '../context/Authcontext.jsx'

const emptySupplier = {
  name: '',
  email: '',
  phone: '',
}

function SuppliersPage() {
  const { hasAnyRole } = useAuth()
  const canManageSuppliers = hasAnyRole(['ROLE_ADMIN', 'ROLE_MANAGER'])
  const [suppliers, setSuppliers] = useState([])
  const [search, setSearch] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [modalMode, setModalMode] = useState(null)
  const [editingSupplier, setEditingSupplier] = useState(null)
  const [form, setForm] = useState(emptySupplier)
  const [deleteTarget, setDeleteTarget] = useState(null)

  const loadSuppliers = async () => {
    setLoading(true)
    setError('')
    try {
      const response = await getSuppliers()
      setSuppliers(response.data)
    } catch {
      setError('Unable to load suppliers.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    let active = true

    getSuppliers()
      .then((response) => {
        if (active) {
          setSuppliers(response.data)
        }
      })
      .catch(() => {
        if (active) {
          setError('Unable to load suppliers.')
        }
      })
      .finally(() => {
        if (active) {
          setLoading(false)
        }
      })

    return () => {
      active = false
    }
  }, [])

  const filteredSuppliers = useMemo(() => {
    const term = search.toLowerCase()
    return suppliers.filter((supplier) =>
      [supplier.name, supplier.email, supplier.phone]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(term)),
    )
  }, [suppliers, search])

  const openCreate = () => {
    setForm(emptySupplier)
    setEditingSupplier(null)
    setModalMode('create')
  }

  const openEdit = (supplier) => {
    setForm({
      name: supplier.name || '',
      email: supplier.email || '',
      phone: supplier.phone || '',
    })
    setEditingSupplier(supplier)
    setModalMode('edit')
  }

  const closeModal = () => {
    setModalMode(null)
    setEditingSupplier(null)
    setForm(emptySupplier)
  }

  const handleChange = (event) => {
    const { name, value } = event.target
    setForm((current) => ({ ...current, [name]: value }))
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    const payload = {
      name: form.name.trim(),
      email: form.email.trim(),
      phone: form.phone.trim(),
    }
    setError('')
    try {
      if (modalMode === 'edit' && editingSupplier) {
        await updateSupplier(editingSupplier.id, payload)
      } else {
        await createSupplier(payload)
      }
      closeModal()
      await loadSuppliers()
    } catch {
      setError('Unable to save supplier. The email may already exist.')
    }
  }

  const confirmDelete = async () => {
    if (!deleteTarget) return
    setError('')
    try {
      await deleteSupplier(deleteTarget.id)
      setDeleteTarget(null)
      await loadSuppliers()
    } catch {
      setError('Unable to delete supplier. Remove linked products first.')
    }
  }

  const columns = [
    { key: 'name', label: 'Name' },
    { key: 'email', label: 'Email' },
    { key: 'phone', label: 'Phone' },
  ]

  return (
    <section className="page-section">
      <div className="section-header">
        <div>
          <p className="eyebrow">Suppliers</p>
          <h2>
            <Truck size={24} />
            Supplier directory
          </h2>
        </div>
        {canManageSuppliers && (
          <button type="button" className="btn primary" onClick={openCreate}>
            <Plus size={17} />
            Add Supplier
          </button>
        )}
      </div>

      <div className="toolbar">
        <SearchBar value={search} onChange={setSearch} placeholder="Search suppliers..." />
      </div>

      {error && <p className="alert error">{error}</p>}
      {loading ? (
        <p className="status-text">Loading suppliers...</p>
      ) : (
        <DataTable
          columns={columns}
          data={filteredSuppliers}
          emptyMessage="No suppliers found."
          onEdit={canManageSuppliers ? openEdit : undefined}
          onDelete={canManageSuppliers ? setDeleteTarget : undefined}
        />
      )}

      {modalMode && (
        <FormModal
          title={modalMode === 'edit' ? 'Edit supplier' : 'Add supplier'}
          submitLabel={modalMode === 'edit' ? 'Save changes' : 'Create supplier'}
          onSubmit={handleSubmit}
          onClose={closeModal}
        >
          <div className="form-grid">
            <label>
              Name
              <input name="name" value={form.name} onChange={handleChange} required />
            </label>
            <label>
              Email
              <input name="email" type="email" value={form.email} onChange={handleChange} />
            </label>
            <label>
              Phone
              <input name="phone" value={form.phone} onChange={handleChange} />
            </label>
          </div>
        </FormModal>
      )}

      {deleteTarget && (
        <ConfirmDeleteModal
          title="Delete supplier"
          message={`Delete "${deleteTarget.name}"?`}
          onCancel={() => setDeleteTarget(null)}
          onConfirm={confirmDelete}
        />
      )}
    </section>
  )
}

export default SuppliersPage
