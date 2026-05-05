import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { getProductById } from '../api/productApi.js'

function ProductDetailPage() {
  const { id } = useParams()
  const [product, setProduct] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const loadProduct = async () => {
      setLoading(true)
      setError('')
      try {
        const response = await getProductById(id)
        setProduct(response.data)
      } catch {
        setError('Product not found or backend unavailable.')
      } finally {
        setLoading(false)
      }
    }

    loadProduct()
  }, [id])

  if (loading) {
    return <p className="status-text">Loading product...</p>
  }

  if (error) {
    return (
      <section className="page-section">
        <p className="alert error">{error}</p>
        <Link className="btn secondary link-button" to="/products">
          Back to products
        </Link>
      </section>
    )
  }

  return (
    <section className="page-section detail-page">
      <div className="section-header">
        <div>
          <p className="eyebrow">Product detail</p>
          <h2>{product.name}</h2>
        </div>
        <Link className="btn secondary link-button" to="/products">
          Back to products
        </Link>
      </div>

      <dl className="detail-grid">
        <div>
          <dt>Description</dt>
          <dd>{product.description || '-'}</dd>
        </div>
        <div>
          <dt>Price</dt>
          <dd>{Number(product.price).toFixed(2)} MAD</dd>
        </div>
        <div>
          <dt>Quantity</dt>
          <dd>{product.quantity}</dd>
        </div>
        <div>
          <dt>Category</dt>
          <dd>{product.category?.name || '-'}</dd>
        </div>
        <div className="wide">
          <dt>Suppliers</dt>
          <dd>{product.suppliers?.map((supplier) => supplier.name).join(', ') || '-'}</dd>
        </div>
      </dl>
    </section>
  )
}

export default ProductDetailPage
