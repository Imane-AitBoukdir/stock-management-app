import { useState, useEffect } from 'react';
import { useAuth } from '../context/Authcontext';
import userApi from '../api/userApi';
import DataTable from '../components/DataTable';
import { Navigate } from 'react-router-dom';

const AdminPage = () => {
  const { hasRole } = useAuth();
  const isAdmin = hasRole('ROLE_ADMIN');
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (isAdmin) {
      fetchUsers();
    } else {
      setLoading(false);
    }
  }, [isAdmin]);

  const fetchUsers = async () => {
    try {
      const data = await userApi.getAllUsers();
      setUsers(data);
    } catch (err) {
      setError('Failed to fetch users');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleRoleChange = async (userId, newRole) => {
    try {
      const updatedUser = await userApi.updateUserRole(userId, newRole);
      setUsers((currentUsers) =>
        currentUsers.map((user) =>
          user.id === userId ? updatedUser : user
        )
      );
    } catch (err) {
      setError('Failed to update user role');
      console.error(err);
    }
  };

  if (!isAdmin) {
    return <Navigate to="/unauthorized" />;
  }

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;

  const columns = [
    { key: 'id', label: 'ID' },
    { key: 'username', label: 'Username' },
    { key: 'email', label: 'Email' },
    {
      key: 'role',
      label: 'Role',
      render: (user) => (
        <select
          value={user.role}
          onChange={(e) => handleRoleChange(user.id, e.target.value)}
          className="form-select"
        >
          <option value="USER">User</option>
          <option value="MANAGER">Manager</option>
          <option value="ADMIN">Admin</option>
        </select>
      ),
    },
  ];

  return (
    <div className="container mt-4">
      <h1>Admin Panel - User Management</h1>
      <DataTable columns={columns} data={users} />
    </div>
  );
};

export default AdminPage;