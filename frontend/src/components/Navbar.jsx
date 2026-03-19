import React from 'react';
import { Menu, Search, User, ShoppingCart, LogOut } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <div className="nav-left">
          <Menu className="cursor-pointer" />
          <Link to="/" className="text-2xl font-bold italic tracking-tighter text-white">
            SCHOOL<span className="text-secondary" style={{ color: '#333' }}>SUPPLY</span>
          </Link>
        </div>

        <div className="search-bar">
          <Search className="search-icon w-5 h-5" />
          <input
            type="text"
            className="search-input shadow-inner"
            placeholder="Search for school items..."
          />
        </div>

        <div className="nav-right" style={{ display: 'flex', flexDirection: 'row', alignItems: 'center', gap: '1.5rem' }}>
          {user ? (
            <div className="flex items-center gap-4" style={{ display: 'flex', flexDirection: 'row', alignItems: 'center' }}>
              <div className="flex items-center gap-2 nav-icon-btn" style={{ display: 'flex', flexDirection: 'row', alignItems: 'center' }}>
                <User className="w-6 h-6" />
                <span className="hidden md:inline font-semibold">{user.name}</span>
              </div>
              <button
                onClick={handleLogout}
                className="nav-icon-btn flex items-center gap-1 hover:text-gray-200 transition-colors"
                style={{ display: 'flex', flexDirection: 'row', alignItems: 'center' }}
                title="Logout"
              >
                <LogOut className="w-5 h-5" />
                <span className="hidden lg:inline text-sm">Logout</span>
              </button>
            </div>
          ) : (
            <Link to="/login" className="nav-icon-btn">
              <User className="w-6 h-6" />
              <span className="hidden md:inline">Log In</span>
            </Link>
          )}
          <button className="nav-icon-btn cart-btn">
            <ShoppingCart className="w-6 h-6" />
            <span className="cart-count">0</span>
          </button>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
