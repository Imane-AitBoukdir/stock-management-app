import api from './api';

const authService = {
  /**
   * Register a new user
   * POST /api/auth/register
   */
  register: async (userData) => {
    const response = await api.post('/auth/register', userData);
    return response.data;
  },

  /**
   * Login and store tokens
   * POST /api/auth/login
   */
  login: async (credentials) => {
    const response = await api.post('/auth/login', credentials);
    const { accessToken, refreshToken, user } = response.data.data;

    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    localStorage.setItem('user', JSON.stringify(user));

    return response.data.data;
  },

  /**
   * Logout — clear all stored tokens
   */
  logout: () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
  },

  /**
   * Get the currently stored user object
   */
  getCurrentUser: () => {
    try {
      const user = localStorage.getItem('user');
      return user ? JSON.parse(user) : null;
    } catch {
      return null;
    }
  },

  /**
   * Check if user is authenticated (token exists)
   */
  isAuthenticated: () => {
    return !!localStorage.getItem('accessToken');
  },

  /**
   * Check if user has a specific role
   */
  hasRole: (role) => {
    const user = authService.getCurrentUser();
    return user?.roles?.includes(role) ?? false;
  },

  /**
   * Check if user has any of the given roles
   */
  hasAnyRole: (roles) => {
    const user = authService.getCurrentUser();
    return roles.some((role) => user?.roles?.includes(role));
  },
};

export default authService;