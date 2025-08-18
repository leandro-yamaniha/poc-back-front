// jest-dom adds custom jest matchers for asserting on DOM nodes.
// allows you to do things like:
// expect(element).toHaveTextContent(/react/i)
// learn more: https://github.com/testing-library/jest-dom
import '@testing-library/jest-dom';

// Mock global do LoadingContext
jest.mock('./contexts/LoadingContext', () => ({
  LoadingProvider: ({ children }) => children,
  useLoading: () => ({
    isLoading: jest.fn(() => false),
    setLoading: jest.fn(),
    withLoading: jest.fn((fn) => fn())
  })
}));

// Mock global dos hooks personalizados
jest.mock('./hooks/useDebounce', () => ({
  useDebouncedSearch: jest.fn((performSearch) => ({
    searchTerm: '',
    isSearching: false,
    handleSearchChange: jest.fn()
  }))
}));

jest.mock('./hooks/usePerformance', () => ({
  useOptimizedFilter: jest.fn((data, filters) => data || []),
  usePerformanceMonitor: jest.fn(),
  useCleanup: jest.fn(() => ({ addCleanup: jest.fn() }))
}));

jest.mock('./hooks/useErrorHandling', () => ({
  useErrorHandling: jest.fn(() => ({
    handleApiCall: jest.fn((apiCall) => apiCall()),
    showErrorWithActions: jest.fn(),
    isRetrying: false
  }))
}));

jest.mock('./hooks/useAccessibility', () => ({
  useAccessibility: jest.fn(() => ({
    useFocusTrap: jest.fn(() => ({ current: null })),
    announceToScreenReader: jest.fn()
  }))
}));

// Mock global dos componentes de erro
jest.mock('./components/ErrorFallbacks', () => ({
  NetworkErrorFallback: ({ onRetry }) => (
    <div data-testid="network-error">
      <button onClick={onRetry}>Tentar Novamente</button>
    </div>
  ),
  EmptyStateWithError: ({ onRetry, onCreate }) => (
    <div data-testid="empty-state">
      <button onClick={onRetry}>Tentar Novamente</button>
      <button onClick={onCreate}>Criar Novo</button>
    </div>
  )
}));
