import React from 'react';
import { AlertCircle, Loader, ShieldCheck } from 'lucide-react';
import './PaymentConfirmationModal.css';

const PaymentConfirmationModal = ({ isOpen, orderData, loading = false, error = null, onClose, onConfirm }) => {
  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="confirmation-modal">
        {loading ? (
          <div className="modal-loading">
            <Loader size={48} className="animate-spin" />
            <p>Confirming your order...</p>
          </div>
        ) : error ? (
          <div className="modal-content error">
            <AlertCircle size={64} className="modal-icon error-icon" />
            <h2>Order Failed</h2>
            <p className="error-message">{error}</p>
            <button className="btn-primary" onClick={onClose}>
              Try Again
            </button>
          </div>
        ) : orderData ? (
          <div className="modal-content success">
            <ShieldCheck size={64} className="modal-icon success-icon" />
            <h2>Confirm Payment?</h2>
            <p className="confirmation-text">
              You are about to place an order for ${orderData.totalAmount?.toFixed(2)}.
            </p>

            <div className="order-info-box">
              <div className="info-row">
                <span className="label">Items</span>
                <span className="value">{orderData.items?.length || 0}</span>
              </div>
              <div className="info-row">
                <span className="label">Total</span>
                <span className="value">${orderData.totalAmount?.toFixed(2)}</span>
              </div>
            </div>

            <div className="modal-actions">
              <button className="btn-secondary" onClick={onClose}>
                Cancel
              </button>
              <button className="btn-primary" onClick={onConfirm}>
                Confirm Payment
              </button>
            </div>
          </div>
        ) : null}
      </div>
    </div>
  );
};

export default PaymentConfirmationModal;
