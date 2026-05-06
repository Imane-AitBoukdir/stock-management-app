import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { PackageSearch, Plus } from 'lucide-react'
import DataTable from '../components/DataTable.jsx'
import SearchBar from '../components/SearchBar.jsx'
import FormModal from '../components/FormModal.jsx'
import ConfirmDeleteModal from '../components/ConfirmDeleteModal.jsx'
import { createProduct, deleteProduct, getProducts, updateProduct } from '../api/productApi.js'
import { getCategories } from '../api/categoryApi.js'
import { getSuppliers } from '../api/supplierApi.js'
import { useAuth } from '../context/Authcontext.jsx'

const emptyProduct = {
  name: '',
  description: '',
  price: '',
  quantity: '',
  categoryId: '',
  supplierIds: [],
}

function toProductForm(product) {
  return {
    name: product.name || '',
    description: product.description || '',
    price: product.price ?? '',
    quantity: product.quantity ?? '',
    categoryId: product.category?.id ? String(product.category.id) : '',
    supplierIds: product.suppliers?.map((supplier) => String(supplier.id)) || [],
  }
}

function toPayload(form) {
  return {
    name: form.name.trim(),
    description: form.description.trim(),
    price: Number(form.price),
    quantity: Number(form.quantity),
    categoryId: form.categoryId ? Number(form.categoryId) : null,
    supplierIds: form.supplierIds.map((id) => Number(id)),
  }
}

function ProductsPage() {
  const navigate = useNavigate()
  const { hasAnyRole } = useAuth()
  const canManageProducts = hasAnyRole(['ROLE_ADMIN', 'ROLE_MANAGER'])
  const [products, setProducts] = useState([])
  const [categories, setCategories] = useState([])
  const [suppliers, setSuppliers] = useState([])
  const [search, setSearch] = useState('')
  const [categoryFilter, setCategoryFilter] = useState('')
  const [minPriceFilter, setMinPriceFilter] = useState('')
  const [maxPriceFilter, setMaxPriceFilter] = useState('')
  const [sort, setSort] = useState('name,asc')
  const [page, setPage] = useState(0)
  const [size, setSize] = useState(10)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [modalMode, setModalMode] = useState(null)
  const [editingProduct, setEditingProduct] = useState(null)
  const [form, setForm] = useState(emptyProduct)
  const [deleteTarget, setDeleteTarget] = useState(null)

  const loadData = async (pageIndex = page, pageSize = size) => {
    setLoading(true)
    setError('')
    try {
      const [productsResponse, categoriesResponse, suppliersResponse] = await Promise.all([
        getProducts({
          page: pageIndex,
          size: pageSize,
          sort,
          name: search || undefined,
          category: categoryFilter || undefined,
          minPrice: minPriceFilter || undefined,
          maxPrice: maxPriceFilter || undefined,
        }),
        getCategories(),
        getSuppliers(),
      ])

      const pageData = productsResponse.data
      setProducts(pageData.content ?? [])
      setPage(pageData.number)
      setSize(pageData.size)
      setTotalPages(pageData.totalPages)
      setTotalElements(pageData.totalElements)
      setCategories(categoriesResponse.data)
      setSuppliers(suppliersResponse.data)
    } catch {
      setError('Unable to load stock data. Check that the Spring Boot backend is running.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    let active = true

    loadData(0)

    return () => {
      active = false
    }
  }, [])

  const filteredProducts = products

  const openCreate = () => {
    setForm(emptyProduct)
    setEditingProduct(null)
    setModalMode('create')
  }

  const openEdit = (product) => {
    setForm(toProductForm(product))
    setEditingProduct(product)
    setModalMode('edit')
  }

  const closeModal = () => {
    setModalMode(null)
    setEditingProduct(null)
    setForm(emptyProduct)
  }

  const handleChange = (event) => {
    const { name, value } = event.target
    setForm((current) => ({ ...current, [name]: value }))
  }

  const handleSupplierChange = (event) => {
    const selected = Array.from(event.target.selectedOptions).map((option) => option.value)
    setForm((current) => ({ ...current, supplierIds: selected }))
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    setError('')
    try {
      if (modalMode === 'edit' && editingProduct) {
        await updateProduct(editingProduct.id, toPayload(form))
      } else {
        await createProduct(toPayload(form))
      }
      closeModal()
      await loadData()
    } catch {
      setError('Unable to save product. Verify required fields and relations.')
    }
  }

  const confirmDelete = async () => {
    if (!deleteTarget) return
    setError('')
    try {
      await deleteProduct(deleteTarget.id)
      setDeleteTarget(null)
      await loadData()
    } catch {
      setError('Unable to delete product.')
    }
  }

  const columns = [
    { key: 'name', label: 'Name' },
    { key: 'description', label: 'Description' },
    {
      key: 'price',
      label: 'Price',
      render: (product) => `${Number(product.price).toFixed(2)} MAD`,
    },
    { key: 'quantity', label: 'Quantity' },
    {
      key: 'category',
      label: 'Category',
      render: (product) =>
        product.category?.name ? <span className="badge blue">{product.category.name}</span> : '-',
    },
    {
      key: 'suppliers',
      label: 'Suppliers',
      render: (product) =>
        product.suppliers?.length
          ? product.suppliers.map((supplier) => (
              <span className="badge teal" key={supplier.id}>
                {supplier.name}
              </span>
            ))
          : '-',
    },
  ]

  return (
    <section className="page-section">
      <div className="section-header">
        <div>
          <p className="eyebrow">Products</p>
          <h2>
            <PackageSearch size={24} />
            Stock items
          </h2>
        </div>
        {canManageProducts && (
          <button type="button" className="btn primary" onClick={openCreate}>
            <Plus size={17} />
            Add Product
          </button>
        )}
      </div>

      <div className="toolbar">
        <SearchBar value={search} onChange={setSearch} placeholder="Search products..." />
        <label className="search-bar">
          <span>Category</span>
          <select value={categoryFilter} onChange={(event) => setCategoryFilter(event.target.value)}>
            <option value="">All categories</option>
            {categories.map((category) => (
              <option key={category.id} value={category.name}>
                {category.name}
              </option>
            ))}
          </select>
        </label>
        <label className="search-bar">
          <span>Min price</span>
          <input
            type="number"
            min="0"
            step="0.01"
            value={minPriceFilter}
            onChange={(event) => setMinPriceFilter(event.target.value)}
            placeholder="0.00"
          />
        </label>
        <label className="search-bar">
          <span>Max price</span>
          <input
            type="number"
            min="0"
            step="0.01"
            value={maxPriceFilter}
            onChange={(event) => setMaxPriceFilter(event.target.value)}
            placeholder="0.00"
          />
        </label>
        <label className="search-bar">
          <span>Sort</span>
          <select value={sort} onChange={(event) => setSort(event.target.value)}>
            <option value="name,asc">Name ▲</option>
            <option value="name,desc">Name ▼</option>
            <option value="price,asc">Price ▲</option>
            <option value="price,desc">Price ▼</option>
            <option value="quantity,asc">Quantity ▲</option>
            <option value="quantity,desc">Quantity ▼</option>
          </select>
        </label>
        <button type="button" className="btn secondary" onClick={() => loadData(0)}>
          Apply
        </button>
        <button
          type="button"
          className="btn subtle"
          onClick={() => {
            setSearch('')
            setCategoryFilter('')
            setMinPriceFilter('')
            setMaxPriceFilter('')
            setSort('name,asc')
            loadData(0)
          }}
        >
          Reset
        </button>
      </div>

      {error && <p className="alert error">{error}</p>}
      {loading ? (
        <p className="status-text">Loading products...</p>
      ) : (
        <>
          <DataTable
            columns={columns}
            data={filteredProducts}
            emptyMessage="No products found."
            onView={(product) => navigate(`/products/${product.id}`)}
            onEdit={canManageProducts ? openEdit : undefined}
            onDelete={canManageProducts ? setDeleteTarget : undefined}
          />
          <div className="pagination-controls">
            <button
              type="button"
              className="btn subtle"
              disabled={page <= 0}
              onClick={() => loadData(page - 1)}
            >
              Previous
            </button>
            <span>
              Page {totalPages ? page + 1 : 0} of {totalPages} · {totalElements} items
            </span>
            <button
              type="button"
              className="btn subtle"
              disabled={page + 1 >= totalPages}
              onClick={() => loadData(page + 1)}
            >
              Next
            </button>
            <label className="search-bar">
              <span>Items</span>
              <select
              value={size}
              onChange={(event) => {
                const newSize = Number(event.target.value)
                setSize(newSize)
                loadData(0, newSize)
              }}
            >
              {[5, 10, 20, 50].map((option) => (
                <option key={option} value={option}>
                  {option}
                </option>
              ))}
            </select>
            </label>
          </div>
        </>
      )}

      {modalMode && (
        <FormModal
          title={modalMode === 'edit' ? 'Edit product' : 'Add product'}
          submitLabel={modalMode === 'edit' ? 'Save changes' : 'Create product'}
          onSubmit={handleSubmit}
          onClose={closeModal}
        >
          <div className="form-grid">
            <label>
              Name
              <input name="name" value={form.name} onChange={handleChange} required />
            </label>
            <label>
              Price
              <input name="price" type="number" min="0" step="0.01" value={form.price} onChange={handleChange} required />
            </label>
            <label>
              Quantity
              <input name="quantity" type="number" min="0" value={form.quantity} onChange={handleChange} required />
            </label>
            <label>
              Category
              <select name="categoryId" value={form.categoryId} onChange={handleChange}>
                <option value="">No category</option>
                {categories.map((category) => (
                  <option key={category.id} value={category.id}>
                    {category.name}
                  </option>
                ))}
              </select>
            </label>
            <label className="wide">
              Suppliers
              <select multiple value={form.supplierIds} onChange={handleSupplierChange}>
                {suppliers.map((supplier) => (
                  <option key={supplier.id} value={supplier.id}>
                    {supplier.name}
                  </option>
                ))}
              </select>
            </label>
            <label className="wide">
              Description
              <textarea name="description" value={form.description} onChange={handleChange} rows="4" />
            </label>
          </div>
        </FormModal>
      )}

      {deleteTarget && (
        <ConfirmDeleteModal
          title="Delete product"
          message={`Delete "${deleteTarget.name}" from stock?`}
          onCancel={() => setDeleteTarget(null)}
          onConfirm={confirmDelete}
        />
      )}
    </section>
  )
}

export default ProductsPage
