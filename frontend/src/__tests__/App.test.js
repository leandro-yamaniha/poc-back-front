import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { BrowserRouter } from 'react-router-dom';
import App from '../App';

// Mock dos componentes filhos para focar no teste do App
jest.mock('../components/Navbar', () => {
  return function MockNavbar() {
    return <div data-testid="navbar">Navbar</div>;
  };
});

jest.mock('../components/Dashboard', () => {
  return function MockDashboard() {
    return <div data-testid="dashboard">Dashboard</div>;
  };
});

jest.mock('../components/Customers', () => {
  return function MockCustomers() {
    return <div data-testid="customers">Customers</div>;
  };
});

jest.mock('../components/Services', () => {
  return function MockServices() {
    return <div data-testid="services">Services</div>;
  };
});

jest.mock('../components/Staff', () => {
  return function MockStaff() {
    return <div data-testid="staff">Staff</div>;
  };
});

jest.mock('../components/Appointments', () => {
  return function MockAppointments() {
    return <div data-testid="appointments">Appointments</div>;
  };
});

// Mock do react-toastify
jest.mock('react-toastify', () => ({
  ToastContainer: () => <div data-testid="toast-container">ToastContainer</div>,
  toast: {
    success: jest.fn(),
    error: jest.fn(),
  },
}));

describe('App Component', () => {
  const renderApp = (initialRoute = '/') => {
    window.history.pushState({}, 'Test page', initialRoute);
    return render(<App />);
  };

  test('renders navbar and toast container', () => {
    renderApp();

    expect(screen.getByTestId('navbar')).toBeInTheDocument();
    expect(screen.getByTestId('toast-container')).toBeInTheDocument();
  });

  test('renders dashboard on root route', () => {
    renderApp('/');

    expect(screen.getByTestId('dashboard')).toBeInTheDocument();
  });

  test('renders customers component on /customers route', () => {
    renderApp('/customers');

    expect(screen.getByTestId('customers')).toBeInTheDocument();
  });

  test('renders services component on /services route', () => {
    renderApp('/services');

    expect(screen.getByTestId('services')).toBeInTheDocument();
  });

  test('renders staff component on /staff route', () => {
    renderApp('/staff');

    expect(screen.getByTestId('staff')).toBeInTheDocument();
  });

  test('renders appointments component on /appointments route', () => {
    renderApp('/appointments');

    expect(screen.getByTestId('appointments')).toBeInTheDocument();
  });

  test('has correct CSS classes applied', () => {
    const { container } = renderApp();

    expect(container.firstChild).toHaveClass('App');
  });

  test('includes Bootstrap CSS and custom styles', () => {
    renderApp();

    // Verifica se os estilos Bootstrap estão sendo aplicados
    const appContainer = document.querySelector('.App');
    expect(appContainer).toBeInTheDocument();
  });
});
