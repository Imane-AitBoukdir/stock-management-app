import api from './axiosConfig.js'

export const getCategories = () => api.get('/categories')
export const getCategoryById = (id) => api.get(`/categories/${id}`)
export const createCategory = (category) => api.post('/categories', category)
export const updateCategory = (id, category) => api.put(`/categories/${id}`, category)
export const deleteCategory = (id) => api.delete(`/categories/${id}`)
