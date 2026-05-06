import { useEffect, useMemo, useState } from 'react'
import { Plus, Tags } from 'lucide-react'
import DataTable from '../components/DataTable.jsx'
import SearchBar from '../components/SearchBar.jsx'
import FormModal from '../components/FormModal.jsx'
import ConfirmDeleteModal from '../components/ConfirmDeleteModal.jsx'
import { createCategory, deleteCategory, getCategories, updateCategory } from '../api/categoryApi.js'
import { useAuth } from '../context/Authcontext.jsx'

function CategoriesPage() {
  const { hasAnyRole } = useAuth()
  const canManageCategories = hasAnyRole(['ROLE_ADMIN', 'ROLE_MANAGER'])
  const [categories, setCategories] = useState([])
  const [search, setSearch] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [modalMode, setModalMode] = useState(null)
  const [editingCategory, setEditingCategory] = useState(null)
  const [name, setName] = useState('')
  const [deleteTarget, setDeleteTarget] = useState(null)

  const loadCategories = async () => {
    setLoading(true)
    setError('')
    try {
      const response = await getCategories()
      setCategories(response.data)
    } catch {
      setError('Unable to load categories.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    let active = true

    getCategories()
      .then((response) => {
        if (active) {
          setCategories(response.data)
        }
      })
      .catch(() => {
        if (active) {
          setError('Unable to load categories.')
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

  const filteredCategories = useMemo(() => {
    const term = search.toLowerCase()
    return categories.filter((category) => category.name.toLowerCase().includes(term))
  }, [categories, search])

  const openCreate = () => {
    setName('')
    setEditingCategory(null)
    setModalMode('create')
  }

  const openEdit = (category) => {
    setName(category.name)
    setEditingCategory(category)
    setModalMode('edit')
  }

  const closeModal = () => {
    setModalMode(null)
    setEditingCategory(null)
    setName('')
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    setError('')
    try {
      if (modalMode === 'edit' && editingCategory) {
        await updateCategory(editingCategory.id, { name: name.trim() })
      } else {
        await createCategory({ name: name.trim() })
      }
      closeModal()
      await loadCategories()
    } catch {
      setError('Unable to save category. The name may already exist.')
    }
  }

  const confirmDelete = async () => {
    if (!deleteTarget) return
    setError('')
    try {
      await deleteCategory(deleteTarget.id)
      setDeleteTarget(null)
      await loadCategories()
    } catch {
      setError('Unable to delete category. Remove linked products first.')
    }
  }

  return (
    <section className="page-section">
      <div className="section-header">
        <div>
          <p className="eyebrow">Categories</p>
          <h2>
            <Tags size={24} />
            Product groups
          </h2>
        </div>
        {canManageCategories && (
          <button type="button" className="btn primary" onClick={openCreate}>
            <Plus size={17} />
            Add Category
          </button>
        )}
      </div>

      <div className="toolbar">
        <SearchBar value={search} onChange={setSearch} placeholder="Search categories..." />
      </div>

      {error && <p className="alert error">{error}</p>}
      {loading ? (
        <p className="status-text">Loading categories...</p>
      ) : (
        <DataTable
          columns={[{ key: 'name', label: 'Name' }]}
          data={filteredCategories}
          emptyMessage="No categories found."
          onEdit={canManageCategories ? openEdit : undefined}
          onDelete={canManageCategories ? setDeleteTarget : undefined}
        />
      )}

      {modalMode && (
        <FormModal
          title={modalMode === 'edit' ? 'Edit category' : 'Add category'}
          submitLabel={modalMode === 'edit' ? 'Save changes' : 'Create category'}
          onSubmit={handleSubmit}
          onClose={closeModal}
        >
          <label>
            Name
            <input value={name} onChange={(event) => setName(event.target.value)} required />
          </label>
        </FormModal>
      )}

      {deleteTarget && (
        <ConfirmDeleteModal
          title="Delete category"
          message={`Delete "${deleteTarget.name}"?`}
          onCancel={() => setDeleteTarget(null)}
          onConfirm={confirmDelete}
        />
      )}
    </section>
  )
}

export default CategoriesPage
