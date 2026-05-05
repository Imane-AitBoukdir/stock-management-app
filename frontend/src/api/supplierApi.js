import api from './axiosConfig.js'

export const getSuppliers = () => api.get('/suppliers')
export const getSupplierById = (id) => api.get(`/suppliers/${id}`)
export const createSupplier = (supplier) => api.post('/suppliers', supplier)
export const updateSupplier = (id, supplier) => api.put(`/suppliers/${id}`, supplier)
export const deleteSupplier = (id) => api.delete(`/suppliers/${id}`)
