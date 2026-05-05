// src/components/Navbar.jsx  — Sprint 3: shows logged-in user + logout
import { NavLink, useNavigate } from 'react-router-dom';
import { Boxes, Package, Tags, Truck, LogOut, UserCircle, ShieldCheck } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

function Navbar() {
  const { user, logout, hasRole } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  // Role badge colour
  const roleBadgeClass = hasRole('ROLE_ADMIN')
    ? 'role-badge admin'
    : hasRole('ROLE_MANAGER')
    ? 'role-badge manager'
    : 'role-badge user';

  const displayRole = hasRole('ROLE_ADMIN')
    ? 'Admin'
    : hasRole('ROLE_MANAGER')
    ? 'Manager'
    : 'User';

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

      {/* ── Logged-in user panel ─────────────────────────────── */}
      <div className="sidebar-panel">
        <UserCircle size={22} />
        <div>
          <strong>{user?.username ?? '—'}</strong>
          <span className={roleBadgeClass}>
            <ShieldCheck size={11} style={{ marginRight: 4, verticalAlign: 'middle' }} />
            {displayRole}
          </span>
        </div>
      </div>

      {/* ── Logout ───────────────────────────────────────────── */}
      <div className="sidebar-footer">
        <Boxes size={18} />
        <span style={{ flex: 1 }}>StockManager</span>
        <button
          className="logout-btn"
          onClick={handleLogout}
          title="Sign out"
          aria-label="Sign out"
        >
          <LogOut size={16} />
        </button>
      </div>
    </aside>
  );
}

export default Navbar;
