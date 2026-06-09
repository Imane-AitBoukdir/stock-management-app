import { createContext, useContext, useEffect, useState } from 'react'
import { loginUser, registerUser } from '../api/authApi.js'
import { clearStoredAuth, getStoredAuth, setStoredAuth } from '../auth/authStorage.js'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [session, setSession] = useState(() => getStoredAuth())

  useEffect(() => {
    const handleForcedLogout = () => {
      clearStoredAuth()
      setSession(null)
    }

    window.addEventListener('stock-auth:logout', handleForcedLogout)
    return () => window.removeEventListener('stock-auth:logout', handleForcedLogout)
  }, [])

  const updateSession = (nextSession) => {
    if (nextSession) {
      setStoredAuth(nextSession)
      setSession(nextSession)
      return
    }

    clearStoredAuth()
    setSession(null)
  }

  const login = async (credentials) => {
    const response = await loginUser(credentials)
    updateSession(response.data)
    return response.data
  }

  const register = async (credentials) => {
    const response = await registerUser(credentials)
    updateSession(response.data)
    return response.data
  }

  const logout = () => {
    updateSession(null)
  }

  return (
    <AuthContext.Provider
      value={{
        isAuthenticated: Boolean(session?.token),
        login,
        logout,
        register,
        user: session,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)

  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }

  return context
}