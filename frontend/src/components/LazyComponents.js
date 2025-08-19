import React, { Suspense, lazy } from 'react';
import LoadingSpinner from './LoadingSpinner';

/**
 * Componentes lazy-loaded para melhorar performance inicial
 * Reduz o bundle size inicial carregando componentes sob demanda
 */

// Lazy loading dos componentes principais
export const LazyDashboard = lazy(() => import('./Dashboard'));
export const LazyCustomers = lazy(() => import('./Customers'));
export const LazyServices = lazy(() => import('./Services'));
export const LazyStaff = lazy(() => import('./Staff'));
export const LazyAppointments = lazy(() => import('./Appointments'));

/**
 * HOC para wrapper de Suspense com loading personalizado
 */
export const withSuspense = (Component, fallback = null) => {
  return function SuspenseWrapper(props) {
    const defaultFallback = (
      <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '400px' }}>
        <LoadingSpinner 
          variant="overlay" 
          message="Carregando componente..."
          aria-label="Carregando componente"
        />
      </div>
    );

    return (
      <Suspense fallback={fallback || defaultFallback}>
        <Component {...props} />
      </Suspense>
    );
  };
};

/**
 * Componente para lazy loading com error boundary
 */
export const LazyComponentWrapper = ({ 
  children, 
  fallback = null,
  errorFallback = null 
}) => {
  const defaultErrorFallback = (
    <div className="alert alert-warning m-3">
      <h5>Erro ao carregar componente</h5>
      <p>Não foi possível carregar o componente. Tente recarregar a página.</p>
      <button 
        className="btn btn-outline-warning btn-sm"
        onClick={() => window.location.reload()}
      >
        Recarregar
      </button>
    </div>
  );

  const defaultFallback = (
    <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '300px' }}>
      <LoadingSpinner variant="inline" message="Carregando..." />
    </div>
  );

  return (
    <Suspense fallback={fallback || defaultFallback}>
      {children}
    </Suspense>
  );
};

/**
 * Hook para preload de componentes lazy
 * Permite carregar componentes antes que sejam necessários
 */
export const usePreloadComponent = () => {
  const preload = (componentImport) => {
    // Força o carregamento do componente
    componentImport();
  };

  const preloadAll = () => {
    // Preload de todos os componentes principais
    import('./Dashboard');
    import('./Customers');
    import('./Services');
    import('./Staff');
    import('./Appointments');
  };

  return { preload, preloadAll };
};

/**
 * Componentes lazy com Suspense já configurado
 */
export const Dashboard = withSuspense(LazyDashboard);
export const Customers = withSuspense(LazyCustomers);
export const Services = withSuspense(LazyServices);
export const Staff = withSuspense(LazyStaff);
export const Appointments = withSuspense(LazyAppointments);

export default {
  Dashboard,
  Customers,
  Services,
  Staff,
  Appointments,
  withSuspense,
  LazyComponentWrapper,
  usePreloadComponent
};
