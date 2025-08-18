import React, { useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, NavLink } from 'react-router-dom';
import { Container, Navbar, Nav } from 'react-bootstrap';
import { ToastContainer } from 'react-toastify';
import { LoadingProvider } from './contexts/LoadingContext';
import SkipLinks from './components/SkipLinks';
import ErrorBoundary from './components/ErrorBoundary';
import { 
  Dashboard, 
  Customers, 
  Services, 
  Staff, 
  Appointments,
  usePreloadComponent 
} from './components/LazyComponents';

import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap-icons/font/bootstrap-icons.css';
import 'react-toastify/dist/ReactToastify.css';
import './components/SkipLinks.css';

function App() {
  const { preloadAll } = usePreloadComponent();

  // Preload componentes após carregamento inicial
  useEffect(() => {
    const timer = setTimeout(() => {
      preloadAll();
    }, 2000); // Preload após 2 segundos

    return () => clearTimeout(timer);
  }, [preloadAll]);

  return (
    <LoadingProvider>
      <Router>
        <div className="App">
          <SkipLinks />
          <Navbar id="navigation" />
          <main id="main-content" className="container-fluid" tabIndex="-1">
            <ErrorBoundary>
              <Routes>
                <Route path="/" element={<Dashboard />} />
                <Route path="/customers" element={<Customers />} />
                <Route path="/services" element={<Services />} />
                <Route path="/staff" element={<Staff />} />
                <Route path="/appointments" element={<Appointments />} />
              </Routes>
            </ErrorBoundary>
          </main>
          <ToastContainer
            position="top-right"
            autoClose={3000}
            hideProgressBar={false}
            newestOnTop={false}
            closeOnClick
            rtl={false}
            pauseOnFocusLoss
            draggable
            pauseOnHover
            role="alert"
            aria-live="polite"
          />
        </div>
      </Router>
    </LoadingProvider>
  );
}

export default App;
