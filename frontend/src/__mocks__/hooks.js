// Mock específico para todos os hooks customizados

// usePerformance hooks
export const usePerformanceMonitor = () => ({
  renderCount: 1,
  timeSinceMount: 100
});

export const useOptimizedFilter = (data) => data;

export const useOptimizedPagination = (data) => ({
  currentItems: data.slice(0, 10),
  currentPage: 1,
  totalPages: Math.ceil(data.length / 10),
  totalItems: data.length,
  hasNextPage: false,
  hasPrevPage: false,
  goToPage: jest.fn(),
  nextPage: jest.fn(),
  prevPage: jest.fn()
});

export const useCleanup = () => ({
  addCleanup: jest.fn(),
  cleanup: jest.fn()
});

// useFormValidation hook
export const useFormValidation = () => ({
  values: {},
  errors: {},
  touched: {},
  isValid: true,
  handleChange: jest.fn(),
  handleSubmit: jest.fn(),
  handleBlur: jest.fn(),
  reset: jest.fn(),
  setFormValues: jest.fn()
});

// useErrorHandling hook
export const useErrorHandling = () => ({
  executeWithRetry: jest.fn().mockResolvedValue('success'),
  handleApiCall: jest.fn().mockResolvedValue('success'),
  categorizeError: jest.fn(),
  showErrorWithActions: jest.fn(),
  cleanup: jest.fn(),
  isRetrying: false,
  retryCount: 0
});

// useAccessibility hooks
export const useAccessibility = () => ({
  useFocusTrap: () => ({ current: null }),
  useKeyboardNavigation: () => ({
    selectedIndex: -1,
    setSelectedIndex: jest.fn(),
    handleKeyDown: jest.fn()
  }),
  announceToScreenReader: jest.fn(),
  createSkipLink: jest.fn(),
  trapFocus: jest.fn()
});

export const useKeyboardNavigation = () => ({
  selectedIndex: -1,
  setSelectedIndex: jest.fn(),
  handleKeyDown: jest.fn()
});

// useDebounce hook
export const useDebounce = (value, delay) => value;

// useLoading hook
export const useLoading = () => ({
  isLoading: jest.fn(() => false),
  setLoading: jest.fn(),
  addLoadingTask: jest.fn(),
  removeLoadingTask: jest.fn(),
  withLoading: jest.fn((taskId, asyncFn) => asyncFn())
});
