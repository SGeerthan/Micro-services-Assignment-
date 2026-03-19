import React, { useState } from 'react';
import { Eye, EyeOff, ChevronLeft } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import AuthLayout from '../component/AuthLayout';
import { useAuth } from '../AuthContext';

const RegisterPage = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    role: 'ROLE_USER',
  });
  const [showPassword, setShowPassword] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log('Registration submitted:', formData);
    const dummyUser = { name: formData.name, email: formData.email, role: formData.role };
    login(dummyUser, 'dummy-token');
    navigate('/');
  };

  return (
    <AuthLayout>
      <div className="auth-header">
        <Link to="/" className="back-link">
          <ChevronLeft className="w-5 h-5" />
          Back
        </Link>
        <h1 className="auth-title">Create an Account</h1>
      </div>

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label className="form-label">Full Name *</label>
          <input
            type="text"
            className="form-input"
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            required
          />
        </div>

        <div className="form-group">
          <label className="form-label">Email address *</label>
          <input
            type="email"
            className="form-input"
            value={formData.email}
            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
            required
          />
        </div>

        <div className="form-group">
          <label className="form-label">Password *</label>
          <div className="form-input-container">
            <input
              type={showPassword ? "text" : "password"}
              className="form-input"
              value={formData.password}
              onChange={(e) => setFormData({ ...formData, password: e.target.value })}
              required
            />
            <div 
              className="password-toggle"
              onClick={() => setShowPassword(!showPassword)}
            >
              {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
            </div>
          </div>
        </div>

        <div className="form-group">
          <label className="form-label">Select Role *</label>
          <div className="role-selector">
            <div 
              className={`role-option ${formData.role === 'ROLE_USER' ? 'active' : ''}`}
              onClick={() => setFormData({ ...formData, role: 'ROLE_USER' })}
            >
              User
            </div>
            <div 
              className={`role-option ${formData.role === 'ROLE_ADMIN' ? 'active' : ''}`}
              onClick={() => setFormData({ ...formData, role: 'ROLE_ADMIN' })}
            >
              Admin
            </div>
          </div>
        </div>

        <button type="submit" className="btn-auth">
          Register
        </button>

        <div className="mt-8 pt-6 border-t border-gray-100">
          <p className="text-muted-foreground text-sm text-center">
            Already have an account?{' '}
            <Link to="/login" className="link-red">
              Login here
            </Link>
          </p>
        </div>
      </form>
    </AuthLayout>
  );
};

export default RegisterPage;
