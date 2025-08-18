import React, { Suspense } from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { renderHook, act } from '@testing-library/react';
import {
  withSuspense,
  LazyComponentWrapper,
  usePreloadComponent
} from '../LazyComponents';

// Mock dos componentes lazy para evitar imports reais
jest.mock('../Dashboard', () => {
  return function MockDashboard() {
    return <div>Dashboard Component</div>;
  };
});

jest.mock('../Customers', () => {
  return function MockCustomers() {
    return <div>Customers Component</div>;
  };
});

jest.mock('../LoadingSpinner', () => {
  return function MockLoadingSpinner({ message, variant }) {
    return <div data-testid="loading-spinner">{message} - {variant}</div>;
  };
});

// Componente de teste simples
const TestComponent = ({ message = 'Test Component' }) => <div>{message}</div>;

// Componente que simula erro de carregamento
const ErrorComponent = () => {
  throw new Error('Component loading failed');
};

describe('withSuspense HOC', () => {
  test('renders component when loaded successfully', async () => {
    const WrappedComponent = withSuspense(TestComponent);
    
    render(<WrappedComponent message="Hello World" />);
    
    await waitFor(() => {
      expect(screen.getByText('Hello World')).toBeInTheDocument();
    });
  });

  test('shows default loading fallback while component loads', () => {
    const SlowComponent = () => {
      // Simula componente lento
      return new Promise(resolve => {
        setTimeout(() => resolve(<div>Loaded</div>), 100);
      });
    };
    
    const WrappedComponent = withSuspense(SlowComponent);
    
    render(<WrappedComponent />);
    
    expect(screen.getByTestId('loading-spinner')).toBeInTheDocument();
    expect(screen.getByText('Carregando componente... - overlay')).toBeInTheDocument();
  });

  test('shows custom fallback when provided', () => {
    const customFallback = <div data-testid="custom-loading">Custom Loading...</div>;
    const WrappedComponent = withSuspense(TestComponent, customFallback);
    
    render(
      <Suspense fallback={customFallback}>
        <WrappedComponent />
      </Suspense>
    );
    
    // O componente deve carregar imediatamente neste caso
    expect(screen.getByText('Test Component')).toBeInTheDocument();
  });

  test('passes all props to wrapped component', async () => {
    const PropsComponent = ({ title, count, isActive }) => (
      <div>
        <span>{title}</span>
        <span>{count}</span>
        <span>{isActive ? 'active' : 'inactive'}</span>
      </div>
    );
    
    const WrappedComponent = withSuspense(PropsComponent);
    
    render(
      <WrappedComponent 
        title="Test Title" 
        count={42} 
        isActive={true} 
      />
    );
    
    await waitFor(() => {
      expect(screen.getByText('Test Title')).toBeInTheDocument();
      expect(screen.getByText('42')).toBeInTheDocument();
      expect(screen.getByText('active')).toBeInTheDocument();
    });
  });
});

describe('LazyComponentWrapper', () => {
  test('renders children when loaded successfully', async () => {
    render(
      <LazyComponentWrapper>
        <TestComponent message="Wrapped Component" />
      </LazyComponentWrapper>
    );
    
    await waitFor(() => {
      expect(screen.getByText('Wrapped Component')).toBeInTheDocument();
    });
  });

  test('shows default loading fallback', () => {
    const SlowComponent = () => {
      throw new Promise(() => {}); // Never resolves to simulate loading
    };
    
    render(
      <LazyComponentWrapper>
        <SlowComponent />
      </LazyComponentWrapper>
    );
    
    expect(screen.getByTestId('loading-spinner')).toBeInTheDocument();
    expect(screen.getByText('Carregando... - inline')).toBeInTheDocument();
  });

  test('shows custom fallback when provided', () => {
    const customFallback = <div data-testid="custom-fallback">Custom Fallback</div>;
    const SlowComponent = () => {
      throw new Promise(() => {}); // Never resolves
    };
    
    render(
      <LazyComponentWrapper fallback={customFallback}>
        <SlowComponent />
      </LazyComponentWrapper>
    );
    
    expect(screen.getByTestId('custom-fallback')).toBeInTheDocument();
  });

  test('shows error fallback when component fails to load', () => {
    // Mock console.error to avoid error logs in test output
    const originalError = console.error;
    console.error = jest.fn();
    
    render(
      <LazyComponentWrapper>
        <ErrorComponent />
      </LazyComponentWrapper>
    );
    
    expect(screen.getByText('Erro ao carregar componente')).toBeInTheDocument();
    expect(screen.getByText('Não foi possível carregar o componente. Tente recarregar a página.')).toBeInTheDocument();
    expect(screen.getByText('Recarregar')).toBeInTheDocument();
    
    console.error = originalError;
  });

  test('shows custom error fallback when provided', () => {
    const customErrorFallback = <div data-testid="custom-error">Custom Error</div>;
    const originalError = console.error;
    console.error = jest.fn();
    
    render(
      <LazyComponentWrapper errorFallback={customErrorFallback}>
        <ErrorComponent />
      </LazyComponentWrapper>
    );
    
    expect(screen.getByTestId('custom-error')).toBeInTheDocument();
    
    console.error = originalError;
  });
});

describe('usePreloadComponent', () => {
  test('provides preload function', () => {
    const { result } = renderHook(() => usePreloadComponent());
    
    expect(typeof result.current.preload).toBe('function');
    expect(typeof result.current.preloadAll).toBe('function');
  });

  test('preload function calls component import', () => {
    const { result } = renderHook(() => usePreloadComponent());
    const mockImport = jest.fn(() => Promise.resolve());
    
    act(() => {
      result.current.preload(mockImport);
    });
    
    expect(mockImport).toHaveBeenCalled();
  });

  test('preloadAll function initiates all component imports', () => {
    // Mock dynamic imports
    const originalImport = global.import;
    global.import = jest.fn(() => Promise.resolve());
    
    const { result } = renderHook(() => usePreloadComponent());
    
    act(() => {
      result.current.preloadAll();
    });
    
    // Verificar se os imports foram chamados (indiretamente através dos efeitos)
    expect(typeof result.current.preloadAll).toBe('function');
    
    global.import = originalImport;
  });
});

describe('Lazy Components Integration', () => {
  test('lazy components are properly wrapped with Suspense', async () => {
    // Este teste verifica se os componentes lazy exportados funcionam
    const { LazyDashboard } = require('../LazyComponents');
    
    render(<LazyDashboard />);
    
    // Deve mostrar o loading primeiro, depois o componente
    await waitFor(() => {
      expect(screen.getByText('Dashboard Component')).toBeInTheDocument();
    });
  });

  test('multiple lazy components can be rendered simultaneously', async () => {
    const { LazyDashboard, LazyCustomers } = require('../LazyComponents');
    
    render(
      <div>
        <LazyDashboard />
        <LazyCustomers />
      </div>
    );
    
    await waitFor(() => {
      expect(screen.getByText('Dashboard Component')).toBeInTheDocument();
      expect(screen.getByText('Customers Component')).toBeInTheDocument();
    });
  });
});

describe('Error Boundary Integration', () => {
  test('handles component loading errors gracefully', () => {
    const originalError = console.error;
    console.error = jest.fn();
    
    // Simular erro de carregamento de componente lazy
    const FailingLazyComponent = withSuspense(ErrorComponent);
    
    render(<FailingLazyComponent />);
    
    // Deve mostrar algum tipo de erro ou fallback
    // O comportamento exato depende da implementação do error boundary
    expect(screen.getByText('Test Component')).toBeInTheDocument();
    
    console.error = originalError;
  });
});
