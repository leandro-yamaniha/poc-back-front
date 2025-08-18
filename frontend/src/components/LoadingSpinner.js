import React from 'react';
import { Spinner } from 'react-bootstrap';

/**
 * Componente de loading spinner reutilizável
 * @param {Object} props - Propriedades do componente
 * @param {string} props.size - Tamanho do spinner (sm, md, lg)
 * @param {string} props.variant - Variante de cor do spinner
 * @param {string} props.text - Texto a ser exibido junto ao spinner
 * @param {boolean} props.overlay - Se deve mostrar como overlay
 * @param {boolean} props.fullPage - Se deve ocupar a página inteira
 * @param {string} props.className - Classes CSS adicionais
 */
const LoadingSpinner = ({ 
  size = 'md', 
  variant = 'primary', 
  text = 'Carregando...', 
  overlay = false,
  fullPage = false,
  className = ''
}) => {
  const getSpinnerSize = () => {
    switch (size) {
      case 'sm': return 'spinner-border-sm';
      case 'lg': return 'spinner-border-lg';
      default: return '';
    }
  };

  const spinnerElement = (
    <div className={`d-flex align-items-center justify-content-center ${className}`}>
      <Spinner 
        animation="border" 
        variant={variant}
        className={getSpinnerSize()}
        role="status"
        aria-hidden="true"
      />
      {text && (
        <span className="ms-2 visually-hidden-focusable" aria-live="polite">
          {text}
        </span>
      )}
    </div>
  );

  if (fullPage) {
    return (
      <div 
        className="d-flex align-items-center justify-content-center position-fixed top-0 start-0 w-100 h-100"
        style={{ 
          backgroundColor: 'rgba(255, 255, 255, 0.9)', 
          zIndex: 9999 
        }}
        role="status"
        aria-label={text}
      >
        {spinnerElement}
      </div>
    );
  }

  if (overlay) {
    return (
      <div 
        className="position-absolute top-0 start-0 w-100 h-100 d-flex align-items-center justify-content-center"
        style={{ 
          backgroundColor: 'rgba(255, 255, 255, 0.8)', 
          zIndex: 10 
        }}
        role="status"
        aria-label={text}
      >
        {spinnerElement}
      </div>
    );
  }

  return spinnerElement;
};

/**
 * Componente de loading para tabelas
 */
export const TableLoading = ({ rows = 5, columns = 4 }) => (
  <tbody>
    {Array.from({ length: rows }).map((_, rowIndex) => (
      <tr key={rowIndex}>
        {Array.from({ length: columns }).map((_, colIndex) => (
          <td key={colIndex}>
            <div 
              className="placeholder-glow"
              style={{ height: '20px' }}
            >
              <span className="placeholder col-8"></span>
            </div>
          </td>
        ))}
      </tr>
    ))}
  </tbody>
);

/**
 * Componente de loading para cards
 */
export const CardLoading = () => (
  <div className="card">
    <div className="card-body">
      <div className="placeholder-glow">
        <h5 className="card-title placeholder col-6"></h5>
        <p className="card-text">
          <span className="placeholder col-7"></span>
          <span className="placeholder col-4"></span>
          <span className="placeholder col-4"></span>
          <span className="placeholder col-6"></span>
        </p>
        <span className="btn btn-primary disabled placeholder col-6"></span>
      </div>
    </div>
  </div>
);

/**
 * HOC para adicionar loading a componentes
 */
export const withLoading = (WrappedComponent, loadingKey) => {
  return function LoadingWrapper(props) {
    const { useLoading } = require('../contexts/LoadingContext');
    const { isLoading } = useLoading();
    const isComponentLoading = isLoading(loadingKey);

    if (isComponentLoading) {
      return <LoadingSpinner overlay fullPage />;
    }

    return <WrappedComponent {...props} />;
  };
};

export default LoadingSpinner;
