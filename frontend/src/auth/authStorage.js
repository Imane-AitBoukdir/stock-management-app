export const AUTH_STORAGE_KEY = 'stock-management-auth'

export function getStoredAuth() {
  if (typeof window === 'undefined') {
    return null
  }

  const rawSession = window.localStorage.getItem(AUTH_STORAGE_KEY)
  if (!rawSession) {
    return null
  }

  try {
    const parsedSession = JSON.parse(rawSession)
    if (!parsedSession?.token || !parsedSession?.username) {
      clearStoredAuth()
      return null
    }
    return parsedSession
  } catch {
    clearStoredAuth()
    return null
  }
}

export function setStoredAuth(session) {
  if (typeof window === 'undefined') {
    return
  }

  window.localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(session))
}

export function clearStoredAuth() {
  if (typeof window === 'undefined') {
    return
  }

  window.localStorage.removeItem(AUTH_STORAGE_KEY)
}