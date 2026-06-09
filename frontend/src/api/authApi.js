import api from './axiosConfig.js'

export const loginUser = (credentials) => api.post('/auth/login', credentials)
export const registerUser = (credentials) => api.post('/auth/register', credentials)