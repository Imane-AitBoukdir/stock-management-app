import { ShieldCheck } from 'lucide-react'
import { useEffect, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

const initialForm = {
  username: '',
  password: '',
  confirmPassword: '',
}

function AuthPage() {
  const navigate = useNavigate()
  const location = useLocation()
  const { isAuthenticated, login, register } = useAuth()
  const [mode, setMode] = useState('login')
  const [form, setForm] = useState(initialForm)
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    if (isAuthenticated) {
      navigate(location.state?.from?.pathname || '/products', { replace: true })
    }
  }, [isAuthenticated, location.state, navigate])

  const handleChange = (event) => {
    const { name, value } = event.target
    setForm((current) => ({ ...current, [name]: value }))
  }

  const handleModeChange = (nextMode) => {
    setMode(nextMode)
    setError('')
    setForm(initialForm)
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    setError('')

    if (mode === 'register' && form.password !== form.confirmPassword) {
      setError('Passwords do not match.')
      return
    }

    setSubmitting(true)
    try {
      const payload = {
        username: form.username.trim(),
        password: form.password,
      }

      if (mode === 'register') {
        await register(payload)
      } else {
        await login(payload)
      }
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'Authentication failed. Please try again.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <section className="auth-shell">
      <article className="auth-hero">
        <div>
          <div className="auth-kicker">
            <ShieldCheck size={16} />
            Sprint 4 security
          </div>
          <h1>Secure access for your stock workspace</h1>
          <p>
            Authentication is now required before reaching products, categories, and suppliers.
            Register a user once, then sign in to manage inventory.
          </p>
        </div>

        <ul className="auth-points">
          <li>JWT-backed sessions for the Spring Boot API.</li>
          <li>Protected React routes with persisted login state.</li>
          <li>Environment-based API and CORS configuration for local and production setups.</li>
        </ul>
      </article>

      <article className="auth-card">
        <div>
          <p className="eyebrow">Authentication</p>
          <h2>{mode === 'login' ? 'Sign in' : 'Create account'}</h2>
          <p className="auth-helper">
            {mode === 'login'
              ? 'Use your existing credentials to open the dashboard.'
              : 'Create the first application user, then continue directly to the dashboard.'}
          </p>
        </div>

        <div className="auth-tabs" role="tablist" aria-label="Authentication mode">
          <button type="button" className={mode === 'login' ? 'active' : ''} onClick={() => handleModeChange('login')}>
            Login
          </button>
          <button type="button" className={mode === 'register' ? 'active' : ''} onClick={() => handleModeChange('register')}>
            Register
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          <label>
            Username
            <input name="username" value={form.username} onChange={handleChange} minLength="3" required />
          </label>

          <label>
            Password
            <input name="password" type="password" value={form.password} onChange={handleChange} minLength="6" required />
          </label>

          {mode === 'register' && (
            <label>
              Confirm password
              <input
                name="confirmPassword"
                type="password"
                value={form.confirmPassword}
                onChange={handleChange}
                minLength="6"
                required
              />
            </label>
          )}

          {error && <p className="alert error">{error}</p>}

          <button type="submit" className="btn primary" disabled={submitting}>
            {submitting ? 'Submitting...' : mode === 'login' ? 'Login' : 'Register and continue'}
          </button>
        </form>
      </article>
    </section>
  )
}

export default AuthPage