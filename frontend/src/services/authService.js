import api from '../api/axiosConfig';

const authService = {
  register: async (userData) => {
    const response = await api.post('/auth/register', userData);
    return response.data;
  },

  login: async (credentials) => {
    const response = await api.post('/auth/login', credentials);
    const payload = response.data.data ?? response.data;
    const { accessToken, refreshToken, username, email, roles } = payload;

    const user = { username, email, roles };

    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    localStorage.setItem('user', JSON.stringify(user));

    return payload;
  },

  logout: () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
  },

  getCurrentUser: () => {
    try {
      const user = localStorage.getItem('user');
      return user ? JSON.parse(user) : null;
    } catch {
      return null;
    }
  },

  isAuthenticated: () => {
    return !!localStorage.getItem('accessToken');
  },

  hasRole: (role) => {
    const user = authService.getCurrentUser();
    return user?.roles?.includes(role) ?? false;
  },

  hasAnyRole: (roles) => {
    const user = authService.getCurrentUser();
    return roles.some((role) => user?.roles?.includes(role));
  },
};

export default authService;