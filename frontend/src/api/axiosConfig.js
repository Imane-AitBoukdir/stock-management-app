import axios from 'axios'
import { clearStoredAuth, getStoredAuth } from '../auth/authStorage.js'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
})

api.interceptors.request.use((config) => {
  const session = getStoredAuth()

  if (session?.token) {
    config.headers.Authorization = `Bearer ${session.token}`
  }

  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      clearStoredAuth()
      window.dispatchEvent(new Event('stock-auth:logout'))
    }

    return Promise.reject(error)
  },
)

export default api
