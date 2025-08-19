// Mock específico para componentes customizados
import React from 'react';

// LoadingSpinner components
export const LoadingSpinner = ({ text = 'Loading...' }) => (
  <div data-testid="loading-spinner">{text}</div>
);

export const TableLoading = ({ rows = 5 }) => (
  <tbody data-testid="table-loading">
    {Array.from({ length: rows }).map((_, i) => (
      <tr key={i}>
        <td>Loading...</td>
      </tr>
    ))}
  </tbody>
);

// LoadingContext
export const LoadingProvider = ({ children }) => (
  <div data-testid="loading-provider">{children}</div>
);

// LazyComponents
export const LazyComponents = {
  Dashboard: () => <div data-testid="mock-dashboard">Mock Dashboard</div>,
  Customers: () => <div data-testid="mock-customers">Mock Customers</div>,
  Services: () => <div data-testid="mock-services">Mock Services</div>,
  Staff: () => <div data-testid="mock-staff">Mock Staff</div>,
  Appointments: () => <div data-testid="mock-appointments">Mock Appointments</div>,
  usePreloadComponent: () => ({
    preloadAll: jest.fn()
  })
};

// API mocks
export const apiMocks = {
  customersAPI: {
    getAll: jest.fn().mockResolvedValue({ data: [] }),
    create: jest.fn().mockResolvedValue({ data: {} }),
    update: jest.fn().mockResolvedValue({ data: {} }),
    delete: jest.fn().mockResolvedValue({ data: {} }),
    search: jest.fn().mockResolvedValue({ data: [] })
  },
  servicesAPI: {
    getAll: jest.fn().mockResolvedValue({ data: [] }),
    create: jest.fn().mockResolvedValue({ data: {} }),
    update: jest.fn().mockResolvedValue({ data: {} }),
    delete: jest.fn().mockResolvedValue({ data: {} })
  },
  staffAPI: {
    getAll: jest.fn().mockResolvedValue({ data: [] }),
    create: jest.fn().mockResolvedValue({ data: {} }),
    update: jest.fn().mockResolvedValue({ data: {} }),
    delete: jest.fn().mockResolvedValue({ data: {} })
  },
  appointmentsAPI: {
    getByDate: jest.fn().mockResolvedValue({ data: [] }),
    create: jest.fn().mockResolvedValue({ data: {} }),
    update: jest.fn().mockResolvedValue({ data: {} }),
    delete: jest.fn().mockResolvedValue({ data: {} })
  }
};

// Toast mock
export const toastMock = {
  success: jest.fn(),
  error: jest.fn(),
  info: jest.fn(),
  warning: jest.fn()
};
