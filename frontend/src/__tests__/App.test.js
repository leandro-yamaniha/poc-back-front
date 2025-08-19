import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { BrowserRouter, MemoryRouter } from 'react-router-dom';
import App from '../App';

// Mock window.location
const originalLocation = window.location;
beforeAll(() => {
  delete window.location;
  window.location = {
    ...originalLocation,
    href: 'http://localhost/',
    origin: 'http://localhost',
    pathname: '/',
    search: '',
    hash: ''
  };
});

afterAll(() => {
  window.location = originalLocation;
});

// Mock dos componentes lazy loading
jest.mock('../components/LazyComponents', () => ({
  __esModule: true,
  default: jest.fn(),
  usePreloadComponent: () => ({
    preloadAll: jest.fn()
  }),
  Dashboard: () => <div data-testid="dashboard">Dashboard</div>,
  Customers: () => <div data-testid="customers">Customers</div>,
  Services: () => <div data-testid="services">Services</div>,
  Staff: () => <div data-testid="staff">Staff</div>,
  Appointments: () => <div data-testid="appointments">Appointments</div>
}));

// Mock dos componentes filhos para focar no teste do App
jest.mock('../components/Navbar', () => {
  return function MockNavbar() {
    return <div data-testid="navbar">Navbar</div>;
  };
});

// Mock do ErrorBoundary
jest.mock('../components/ErrorBoundary', () => {
  return function MockErrorBoundary({ children }) {
    return <div data-testid="error-boundary">{children}</div>;
  };
});

// Mock do SkipLinks
jest.mock('../components/SkipLinks', () => {
  return function MockSkipLinks() {
    return <div data-testid="skip-links">Skip Links</div>;
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
  const renderApp = () => {
    return render(<App />);
  };

  test('renders navbar and toast container', () => {
    renderApp();

    expect(document.getElementById('navigation')).toBeInTheDocument();
    expect(screen.getByTestId('toast-container')).toBeInTheDocument();
  });

  test('renders dashboard on root route', () => {
    renderApp();

    expect(screen.getByTestId('dashboard')).toBeInTheDocument();
  });

  test('renders main content structure', () => {
    renderApp();

    expect(screen.getByRole('main')).toBeInTheDocument();
    expect(screen.getByTestId('error-boundary')).toBeInTheDocument();
  });


  test('has correct CSS classes applied', () => {
    renderApp();

    const appDiv = document.querySelector('.App');
    expect(appDiv).toBeInTheDocument();
    expect(appDiv).toHaveClass('App');
  });

  test('includes Bootstrap CSS and custom styles', () => {
    renderApp();

    // Check if main container has Bootstrap classes
    const mainContent = screen.getByRole('main');
    expect(mainContent).toHaveClass('container-fluid');
  });
});
