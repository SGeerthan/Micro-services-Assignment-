import React from 'react';

const AuthLayout = ({ children }) => {
  return (
    <div className="auth-container animate-fade-in">
      {children}
    </div>
  );
};

export default AuthLayout;
