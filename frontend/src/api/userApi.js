import api from './axiosConfig';

const userApi = {
  getAllUsers: async () => {
    const response = await api.get('/users');
    return response.data;
  },

  updateUserRole: async (userId, role) => {
    const response = await api.put(`/users/${userId}/role`, { role });
    return response.data;
  },
};

export default userApi;