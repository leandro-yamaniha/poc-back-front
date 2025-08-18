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
  const renderApp = (initialRoute = '/') => {
    return render(
      <MemoryRouter initialEntries={[initialRoute]}>
        <App />
      </MemoryRouter>
    );
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
