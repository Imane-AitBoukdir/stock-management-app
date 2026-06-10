import { useEffect, useState } from 'react'
import {
  AlertTriangle,
  BarChart3,
  Boxes,
  DollarSign,
  Layers,
  Package,
  Tags,
  Truck,
  TrendingUp,
} from 'lucide-react'
import { getDashboard } from '../api/dashboardApi.js'

function formatCurrency(value) {
  return Number(value).toLocaleString('fr-MA', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }) + ' MAD'
}

function DashboardPage() {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let active = true
    getDashboard()
      .then((res) => {
        if (active) setData(res.data)
      })
      .catch(() => {
        if (active) setError('Unable to load dashboard data.')
      })
      .finally(() => {
        if (active) setLoading(false)
      })
    return () => { active = false }
  }, [])

  if (loading) {
    return (
      <section className="page-section">
        <div className="section-header">
          <div>
            <p className="eyebrow">Dashboard</p>
            <h2><BarChart3 size={24} /> Overview</h2>
          </div>
        </div>
        <p className="status-text">Loading dashboard...</p>
      </section>
    )
  }

  if (error) {
    return (
      <section className="page-section">
        <div className="section-header">
          <div>
            <p className="eyebrow">Dashboard</p>
            <h2><BarChart3 size={24} /> Overview</h2>
          </div>
        </div>
        <p className="alert error">{error}</p>
      </section>
    )
  }

  const kpis = [
    {
      label: 'Total Products',
      value: data.totalProducts,
      icon: Package,
      accent: 'kpi-blue',
    },
    {
      label: 'Categories',
      value: data.totalCategories,
      icon: Tags,
      accent: 'kpi-violet',
    },
    {
      label: 'Suppliers',
      value: data.totalSuppliers,
      icon: Truck,
      accent: 'kpi-teal',
    },
    {
      label: 'Stock Units',
      value: data.totalStockUnits.toLocaleString(),
      icon: Boxes,
      accent: 'kpi-amber',
    },
    {
      label: 'Stock Value',
      value: formatCurrency(data.totalStockValue),
      icon: DollarSign,
      accent: 'kpi-green',
    },
    {
      label: 'Low Stock (≤ 5)',
      value: data.lowStockProducts,
      icon: AlertTriangle,
      accent: data.lowStockProducts > 0 ? 'kpi-red' : 'kpi-green',
    },
  ]

  const maxCategoryCount = data.categoryBreakdown.length
    ? Math.max(...data.categoryBreakdown.map((c) => c.productCount))
    : 1

  return (
    <section className="page-section">
      <div className="section-header">
        <div>
          <p className="eyebrow">Dashboard</p>
          <h2><BarChart3 size={24} /> Overview</h2>
        </div>
        <div className="kpi-live-dot">
          <span className="live-dot" />
          Live data
        </div>
      </div>

      {/* ── KPI cards ─────────────────────────────────────── */}
      <div className="kpi-grid">
        {kpis.map((kpi) => (
          <div className={`kpi-card ${kpi.accent}`} key={kpi.label}>
            <div className="kpi-icon">
              <kpi.icon size={20} />
            </div>
            <div className="kpi-body">
              <span className="kpi-label">{kpi.label}</span>
              <span className="kpi-value">{kpi.value}</span>
            </div>
          </div>
        ))}
      </div>

      {/* ── Bottom panels ─────────────────────────────────── */}
      <div className="dashboard-panels">

        {/* Category breakdown */}
        <div className="panel">
          <div className="panel-header">
            <Layers size={18} />
            <h3>Products per Category</h3>
          </div>
          <div className="panel-body">
            {data.categoryBreakdown.length === 0 ? (
              <p className="status-text">No categories yet.</p>
            ) : (
              <ul className="bar-list">
                {data.categoryBreakdown.map((cat) => (
                  <li key={cat.name}>
                    <div className="bar-label">
                      <span>{cat.name}</span>
                      <strong>{cat.productCount}</strong>
                    </div>
                    <div className="bar-track">
                      <div
                        className="bar-fill"
                        style={{ width: `${(cat.productCount / maxCategoryCount) * 100}%` }}
                      />
                    </div>
                  </li>
                ))}
              </ul>
            )}
          </div>
        </div>

        {/* Low stock items */}
        <div className="panel">
          <div className="panel-header">
            <AlertTriangle size={18} />
            <h3>Low Stock Items</h3>
          </div>
          <div className="panel-body">
            {data.lowStockItems.length === 0 ? (
              <div className="empty-state">
                <TrendingUp size={32} />
                <p>All products are well-stocked!</p>
              </div>
            ) : (
              <table className="low-stock-table">
                <thead>
                  <tr>
                    <th>Product</th>
                    <th>Category</th>
                    <th>Qty</th>
                  </tr>
                </thead>
                <tbody>
                  {data.lowStockItems.map((item) => (
                    <tr key={item.id}>
                      <td className="ls-name">{item.name}</td>
                      <td>
                        {item.categoryName
                          ? <span className="badge blue">{item.categoryName}</span>
                          : <span className="status-text">—</span>}
                      </td>
                      <td>
                        <span className={`qty-badge ${item.quantity === 0 ? 'out' : 'low'}`}>
                          {item.quantity}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>
      </div>
    </section>
  )
}

export default DashboardPage
