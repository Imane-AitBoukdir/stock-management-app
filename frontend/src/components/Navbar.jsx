import { NavLink } from 'react-router-dom'
import { Boxes, LayoutDashboard, LogOut, Package, ShieldCheck, Tags, Truck } from 'lucide-react'
import { useAuth } from '../context/AuthContext.jsx'

function Navbar() {
  const { logout, user } = useAuth()

  return (
    <aside className="sidebar">
      <div className="brand-block">
        <div className="brand-mark">SM</div>
        <div>
          <p className="eyebrow">Stock Management</p>
          <h1>Inventory</h1>
        </div>
      </div>

      <nav className="nav-tabs" aria-label="Main navigation">
        <NavLink to="/products">
          <Package size={18} />
          <span>Products</span>
        </NavLink>
        <NavLink to="/categories">
          <Tags size={18} />
          <span>Categories</span>
        </NavLink>
        <NavLink to="/suppliers">
          <Truck size={18} />
          <span>Suppliers</span>
        </NavLink>
      </nav>

      <div className="sidebar-panel">
        <ShieldCheck size={18} />
        <div>
          <strong>{user?.username || 'Authenticated user'}</strong>
          <span>{user?.role === 'ROLE_USER' ? 'JWT session active' : user?.role || 'Protected access'}</span>
        </div>
      </div>

      <div className="sidebar-footer">
        <div className="sidebar-footer-copy">
          <Boxes size={18} />
          <span>Protected inventory routes</span>
        </div>
        <button type="button" className="btn subtle sidebar-logout" onClick={logout}>
          <LogOut size={16} />
          Logout
        </button>
      </div>
    </aside>
  )
}

export default Navbar
