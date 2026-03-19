import React, { useState } from 'react';
import { Eye, EyeOff, ChevronLeft } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import AuthLayout from '../component/AuthLayout';
import { useAuth } from '../AuthContext';

const LoginPage = () => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });
  const [showPassword, setShowPassword] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log('Login submitted:', formData);
    const dummyUser = { name: formData.email.split('@')[0], email: formData.email, role: 'ROLE_USER' };
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
        <h1 className="auth-title">Login to Your Account</h1>
      </div>

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label className="form-label">Username or email address *</label>
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

        <div className="checkbox-group">
          <input type="checkbox" className="custom-checkbox" id="remember" />
          <label htmlFor="remember">Remember me</label>
        </div>

        <button type="submit" className="btn-auth">
          Log in
        </button>

        <Link to="/forgot-password" title="Lost your password?" className="link-red">
          Lost your password?
        </Link>

        <div className="mt-8 pt-6 border-t border-gray-100">
          <p className="text-muted-foreground text-sm">
            Don't have an account?{' '}
            <Link to="/register" className="link-red">
              Register here
            </Link>
          </p>
        </div>
      </form>
    </AuthLayout>
  );
};

export default LoginPage;
