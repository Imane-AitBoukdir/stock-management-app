import { NavLink } from 'react-router-dom'
import { Boxes, LayoutDashboard, Package, Tags, Truck } from 'lucide-react'

function Navbar() {
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
        <LayoutDashboard size={18} />
        <div>
          <strong></strong>
          <span></span>
        </div>
      </div>

      <div className="sidebar-footer">
        <Boxes size={18} />
        <span>No security mode</span>
      </div>
    </aside>
  )
}

export default Navbar
