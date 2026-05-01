import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { CheckCircle } from 'lucide-react';
import Navbar from '../components/Navbar';
import Footer from '../components/Footer';
import './PaymentCancelledPage.css';

const PaymentSuccessPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const orderData = location.state?.orderData;

  const handleContinueShopping = () => {
    navigate('/');
  };

  return (
    <div className="payment-cancelled-page">
      <Navbar />

      <main className="cancelled-container">
        <div className="cancelled-content">
          <CheckCircle size={80} className="cancelled-icon" />
          <h1>Payment Successful</h1>
          <p className="cancelled-message">
            Your order has been confirmed successfully.
          </p>

          {orderData?.id && (
            <p className="order-id">Order ID: #{orderData.id}</p>
          )}

          <div className="action-buttons">
            <button className="btn-primary" onClick={handleContinueShopping}>
              Back to Home
            </button>
          </div>
        </div>
      </main>

      <Footer />
    </div>
  );
};

export default PaymentSuccessPage;
