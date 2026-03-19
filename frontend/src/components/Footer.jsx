import React from 'react';
import { Facebook, Twitter, Instagram, Youtube, Mail, Phone, MapPin } from 'lucide-react';
import { Link } from 'react-router-dom';

const Footer = () => {
  return (
    <footer className="footer-container">
      <div className="footer-content">
        <div className="footer-section">
          <Link to="/" className="text-2xl font-bold italic tracking-tighter text-white mb-6 block">
            SCHOOL SUPPLY<span className="text-secondary" style={{ color: '#333' }}>SUPPLY</span>
          </Link><br />
          <p className="text-gray-300 text-sm max-w-xs mb-6">
            Your specialized destination for all school essentials. From premium pens to durable bags, we provide quality supplies for every student's journey.
          </p>
          <div className="social-links">
            <Facebook className="w-5 h-5 cursor-pointer hover:text-primary transition-colors" />
            <Twitter className="w-5 h-5 cursor-pointer hover:text-primary transition-colors" />
            <Instagram className="w-5 h-5 cursor-pointer hover:text-primary transition-colors" />
            <Youtube className="w-5 h-5 cursor-pointer hover:text-primary transition-colors" />
          </div>
        </div>

        <div className="footer-section">
          <h3 className="footer-heading">Quick Links</h3>
          <ul className="footer-links">
            <li><Link to="/">Home</Link></li>
            <li><Link to="/login">Login</Link></li>
            <li><Link to="/register">Register</Link></li>
          </ul>
        </div>

        <div className="footer-section">
          <h3 className="footer-heading">Categories</h3>
          <ul className="footer-links">
            <li><p className="bg-transparent text-left">Pens & Pencils</p></li>
            <li><p className="bg-transparent text-left">Notebooks</p></li>
            <li><p className="bg-transparent text-left">School Bags</p></li>
            <li><p className="bg-transparent text-left">Stationery Sets</p></li>
          </ul>
        </div>

        <div className="footer-section">
          <h3 className="footer-heading">Contact Us</h3>
          <ul className="footer-contact">
            <li><MapPin className="w-4 h-4 text-primary" /> 123 Education Ave, Colombo</li>
            <li><Phone className="w-4 h-4 text-primary" /> +94 11 234 5678</li>
            <li><Mail className="w-4 h-4 text-primary" /> support@schoolsupply.lk</li>
          </ul>
        </div>
      </div>

      <div className="footer-bottom">
        <p>&copy; {new Date().getFullYear()} School Supply. All rights reserved.</p>
        <div className="footer-bottom-links">
          <span>Privacy Policy</span>
          <span>Terms of Service</span>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
