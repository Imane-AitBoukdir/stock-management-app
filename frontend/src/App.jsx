import { Navigate, Route, Routes, useLocation } from 'react-router-dom'
import Navbar from './components/Navbar.jsx'
import ProductsPage from './pages/ProductsPage.jsx'
import ProductDetailPage from './pages/ProductDetailPage.jsx'
import CategoriesPage from './pages/CategoriesPage.jsx'
import SuppliersPage from './pages/SuppliersPage.jsx'
import LoginPage from './pages/Loginpage.jsx'
import RegisterPage from './pages/Registerpage.jsx'
import { PrivateRoute } from './routes/Privateroute.jsx'
import { ToastContainer } from 'react-toastify'
import 'react-toastify/dist/ReactToastify.css'
import './App.css'

function App() {
  const location = useLocation()
  const authRoute = location.pathname === '/' || location.pathname === '/login' || location.pathname === '/register'

  if (authRoute) {
    return (
      <>
        <ToastContainer position="top-right" autoClose={2500} hideProgressBar />
        <Routes>
          <Route path="/" element={<Navigate to="/register" replace />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
        </Routes>
      </>
    )
  }

  return (
    <div className="app-shell">
      <ToastContainer position="top-right" autoClose={2500} hideProgressBar />
      <Navbar />
      <main className="app-main">
        <Routes>
          <Route path="/" element={<Navigate to="/register" replace />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route
            path="/products"
            element={
              <PrivateRoute>
                <ProductsPage />
              </PrivateRoute>
            }
          />
          <Route
            path="/products/:id"
            element={
              <PrivateRoute>
                <ProductDetailPage />
              </PrivateRoute>
            }
          />
          <Route
            path="/categories"
            element={
              <PrivateRoute>
                <CategoriesPage />
              </PrivateRoute>
            }
          />
          <Route
            path="/suppliers"
            element={
              <PrivateRoute>
                <SuppliersPage />
              </PrivateRoute>
            }
          />
        </Routes>
      </main>
    </div>
  )
}

export default App
