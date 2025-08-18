import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'react-toastify/dist/ReactToastify.css';
import './App.css';

import { LoadingProvider } from './contexts/LoadingContext';
import SkipLinks from './components/SkipLinks';
import Navbar from './components/Navbar';
import Dashboard from './components/Dashboard';
import Customers from './components/Customers';
import Services from './components/Services';
import Staff from './components/Staff';
import Appointments from './components/Appointments';

function App() {
  return (
    <LoadingProvider>
      <Router>
        <div className="App">
          <SkipLinks />
          <Navbar id="navigation" />
          <main id="main-content" className="container-fluid" tabIndex="-1">
            <Routes>
              <Route path="/" element={<Dashboard />} />
              <Route path="/customers" element={<Customers />} />
              <Route path="/services" element={<Services />} />
              <Route path="/staff" element={<Staff />} />
              <Route path="/appointments" element={<Appointments />} />
            </Routes>
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
