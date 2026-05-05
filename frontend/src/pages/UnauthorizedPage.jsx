// src/pages/UnauthorizedPage.jsx
import { useNavigate } from 'react-router-dom';
import { ShieldOff } from 'lucide-react';

const UnauthorizedPage = () => {
  const navigate = useNavigate();

  return (
    <div className="unauthorized-page">
      <ShieldOff size={56} className="unauthorized-icon" />
      <h2>Access Denied</h2>
      <p>You don&apos;t have permission to view this page.</p>
      <button className="btn primary" onClick={() => navigate(-1)}>
        Go Back
      </button>
    </div>
  );
};

export default UnauthorizedPage;
