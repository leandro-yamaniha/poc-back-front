// jest-dom adds custom jest matchers for asserting on DOM nodes.
// allows you to do things like:
// expect(element).toHaveTextContent(/react/i)
// learn more: https://github.com/testing-library/jest-dom
import '@testing-library/jest-dom';

// Mock react-toastify
jest.mock('react-toastify', () => ({
  toast: {
    success: jest.fn(),
    error: jest.fn(),
    info: jest.fn(),
    warning: jest.fn()
  },
  ToastContainer: () => null
}));

// Mock react-bootstrap components
jest.mock('react-bootstrap', () => {
  const MockNavbar = ({ children, ...props }) => <nav {...props}>{children}</nav>;
  MockNavbar.Brand = ({ children, ...props }) => <div {...props}>{children}</div>;
  MockNavbar.Toggle = ({ children, ...props }) => <button {...props} role="button" aria-label="Toggle navigation">{children}</button>;
  MockNavbar.Collapse = ({ children, ...props }) => <div {...props} className="collapse navbar-collapse">{children}</div>;
  
  const MockNav = ({ children, ...props }) => <div {...props}>{children}</div>;
  MockNav.Link = ({ children, ...props }) => <a {...props}>{children}</a>;

  return {
    Container: ({ children, ...props }) => <div {...props}>{children}</div>,
    Row: ({ children, ...props }) => <div {...props}>{children}</div>,
    Col: ({ children, ...props }) => <div {...props}>{children}</div>,
    Card: ({ children, ...props }) => <div {...props}>{children}</div>,
    Button: ({ children, onClick, ...props }) => <button onClick={onClick} {...props}>{children}</button>,
    Form: ({ children, onSubmit, ...props }) => <form onSubmit={onSubmit} {...props}>{children}</form>,
    Alert: ({ children, ...props }) => <div {...props}>{children}</div>,
    Table: ({ children, ...props }) => <table {...props}>{children}</table>,
    Modal: ({ children, show, ...props }) => show ? <div {...props}>{children}</div> : null,
    Spinner: (props) => <div {...props}>Loading...</div>,
    Navbar: MockNavbar,
    Nav: MockNav,
    Badge: ({ children, ...props }) => <span {...props}>{children}</span>
  };
});

// Mock react-router-bootstrap
jest.mock('react-router-bootstrap', () => ({
  LinkContainer: ({ children, to, ...props }) => (
    <div {...props} data-to={to}>{children}</div>
  )
}));

// Mock LazyComponents
jest.mock('./components/LazyComponents', () => ({
  __esModule: true,
  default: jest.fn(),
  usePreloadComponent: () => ({
    preloadAll: jest.fn()
  }),
  Dashboard: () => <div>Mock Dashboard</div>,
  Customers: () => <div>Mock Customers</div>,
  Services: () => <div>Mock Services</div>,
  Staff: () => <div>Mock Staff</div>,
  Appointments: () => <div>Mock Appointments</div>
}));

// Global mocks for contexts and hooks
jest.mock('./contexts/LoadingContext', () => ({
  LoadingProvider: ({ children }) => children,
  useLoading: () => ({
    isLoading: jest.fn(() => false),
    setLoading: jest.fn(),
    addLoadingTask: jest.fn(),
    removeLoadingTask: jest.fn(),
    withLoading: jest.fn((taskId, asyncFn) => asyncFn())
  })
}));

jest.mock('./hooks/useDebounce', () => ({
  useDebounce: (value) => value,
  useDebouncedCallback: (callback) => ({
    debouncedCallback: callback,
    cancel: jest.fn(),
    flush: jest.fn()
  }),
  useDebouncedSearch: () => ({
    searchTerm: '',
    isSearching: false,
    handleSearchChange: jest.fn(),
    clearSearch: jest.fn()
  })
}));

jest.mock('./hooks/usePerformance', () => ({
  usePerformanceMonitor: () => ({
    renderCount: 1,
    timeSinceMount: 100
  }),
  useOptimizedFilter: (data) => data,
  useOptimizedPagination: (data) => ({
    currentItems: data.slice(0, 10),
    currentPage: 1,
    totalPages: Math.ceil(data.length / 10),
    totalItems: data.length,
    hasNextPage: false,
    hasPrevPage: false,
    goToPage: jest.fn(),
    nextPage: jest.fn(),
    prevPage: jest.fn()
  })
}));

jest.mock('./hooks/useErrorHandling', () => ({
  useErrorHandling: () => ({
    executeWithRetry: jest.fn().mockResolvedValue('success'),
    handleApiCall: jest.fn().mockResolvedValue('success'),
    categorizeError: jest.fn(),
    showErrorWithActions: jest.fn(),
    cleanup: jest.fn(),
    isRetrying: false,
    retryCount: 0
  })
}));

jest.mock('./hooks/useAccessibility', () => ({
  useAccessibility: () => ({
    useFocusTrap: () => ({ current: null }),
    useKeyboardNavigation: () => ({
      selectedIndex: -1,
      setSelectedIndex: jest.fn(),
      handleKeyDown: jest.fn()
    }),
    announceToScreenReader: jest.fn(),
    createSkipLink: jest.fn(),
    trapFocus: jest.fn()
  }),
  useKeyboardNavigation: () => ({
    selectedIndex: -1,
    setSelectedIndex: jest.fn(),
    handleKeyDown: jest.fn()
  })
}));

jest.mock('./hooks/useFormValidation', () => ({
  __esModule: true,
  default: () => ({
    values: {},
    errors: {},
    touched: {},
    isSubmitting: false,
    isValid: false,
    setValue: jest.fn(),
    setFieldTouched: jest.fn(),
    handleChange: jest.fn(),
    handleBlur: jest.fn(),
    handleSubmit: jest.fn(),
    validateAll: jest.fn(),
    reset: jest.fn(),
    setFormValues: jest.fn()
  })
}));

// Mock window.location
Object.defineProperty(window, 'location', {
  value: {
    reload: jest.fn(),
    href: 'http://localhost:3000'
  },
  writable: true
});

// Mock navigator
Object.defineProperty(navigator, 'userAgent', {
  value: 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36',
  writable: true
});

// Mock dos componentes de erro
jest.mock('./components/ErrorFallbacks', () => ({
  NetworkErrorFallback: ({ onRetry }) => (
    <div data-testid="network-error">
      <button onClick={onRetry}>Tentar Novamente</button>
    </div>
  ),
  EmptyStateWithError: ({ onRetry, onCreate }) => (
    <div data-testid="empty-state">
      <button onClick={onRetry}>Tentar Novamente</button>
      {onCreate && <button onClick={onCreate}>Criar Novo</button>}
    </div>
  ),
  ErrorFallback: ({ error, resetError }) => (
    <div data-testid="error-fallback">
      <h2>Algo deu errado</h2>
      <button onClick={resetError}>Tentar Novamente</button>
      {process.env.NODE_ENV === 'development' && (
        <details>
          <summary>Detalhes do erro</summary>
          <pre>{error?.message}</pre>
        </details>
      )}
    </div>
  )
}));

// Mock API services
jest.mock('./services/api', () => ({
  __esModule: true,
  default: {
    get: jest.fn().mockResolvedValue({ data: [] }),
    post: jest.fn().mockResolvedValue({ data: {} }),
    put: jest.fn().mockResolvedValue({ data: {} }),
    delete: jest.fn().mockResolvedValue({ data: {} })
  },
  customersAPI: {
    getAll: jest.fn().mockResolvedValue([]),
    getById: jest.fn().mockResolvedValue({}),
    create: jest.fn().mockResolvedValue({}),
    update: jest.fn().mockResolvedValue({}),
    delete: jest.fn().mockResolvedValue({})
  },
  servicesAPI: {
    getAll: jest.fn().mockResolvedValue([]),
    getById: jest.fn().mockResolvedValue({}),
    create: jest.fn().mockResolvedValue({}),
    update: jest.fn().mockResolvedValue({}),
    delete: jest.fn().mockResolvedValue({})
  },
  staffAPI: {
    getAll: jest.fn().mockResolvedValue([]),
    getById: jest.fn().mockResolvedValue({}),
    create: jest.fn().mockResolvedValue({}),
    update: jest.fn().mockResolvedValue({}),
    delete: jest.fn().mockResolvedValue({})
  },
  appointmentsAPI: {
    getAll: jest.fn().mockResolvedValue([]),
    getById: jest.fn().mockResolvedValue({}),
    getByDate: jest.fn().mockResolvedValue([]),
    create: jest.fn().mockResolvedValue({}),
    update: jest.fn().mockResolvedValue({}),
    delete: jest.fn().mockResolvedValue({})
  }
}));
