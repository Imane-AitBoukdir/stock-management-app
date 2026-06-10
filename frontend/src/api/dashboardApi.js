import api from './axiosConfig.js'

export const getDashboard = () => api.get('/dashboard')
