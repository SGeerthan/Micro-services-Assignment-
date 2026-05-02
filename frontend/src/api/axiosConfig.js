import axios from 'axios';

const GATEWAY_URL = import.meta.env.VITE_API_GATEWAY_URL || 'http://localhost:8080';

// Create an Axios instance
const apiClient = axios.create({
  baseURL: GATEWAY_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Plain client without interceptors for internal validation calls
const plainClient = axios.create({ baseURL: GATEWAY_URL });
// Request interceptor to add the JWT token to headers
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle global errors like 401 Unauthorized
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Avoid immediate logout on transient gateway/back-end failures.
      // Do a one-time token validation call to confirm the token is actually invalid.
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          localStorage.removeItem('token');
          localStorage.removeItem('user');
          window.location.href = '/login';
          return Promise.reject(error);
        }

        // Make a validation request without this interceptor
        return plainClient
          .post('/auth/validate-token', null, { headers: { Authorization: `Bearer ${token}` } })
          .then((res) => {
            if (res && res.data && res.data.valid === true) {
              // Token is valid — original 401 was likely transient or permission-related.
              return Promise.reject(error);
            }

            // Token invalid — clear and redirect
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = '/login';
            return Promise.reject(error);
          })
          .catch(() => {
            // Validation call failed (gateway/back-end issue). Do NOT log the user out.
            return Promise.reject(error);
          });
      } catch (e) {
        return Promise.reject(error);
      }
    }

    return Promise.reject(error);
  }
);

export default apiClient;
