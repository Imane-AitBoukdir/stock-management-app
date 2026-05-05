// src/context/AuthContext.jsx
import { createContext, useContext, useState, useCallback } from 'react';
import authService from '../services/authService';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => authService.getCurrentUser());
  const [isAuthenticated, setIsAuthenticated] = useState(() => authService.isAuthenticated());

  const login = useCallback(async (credentials) => {
    const data = await authService.login(credentials);
    setUser(data.user);
    setIsAuthenticated(true);
    return data;
  }, []);

  const logout = useCallback(() => {
    authService.logout();
    setUser(null);
    setIsAuthenticated(false);
  }, []);

  const hasRole = useCallback(
    (role) => user?.roles?.includes(role) ?? false,
    [user],
  );

  const hasAnyRole = useCallback(
    (roles) => roles.some((role) => user?.roles?.includes(role)),
    [user],
  );

  return (
    <AuthContext.Provider value={{ user, isAuthenticated, login, logout, hasRole, hasAnyRole }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within an AuthProvider');
  return context;
};

export default AuthContext;
