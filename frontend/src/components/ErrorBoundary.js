import React from 'react';
import { Container, Alert, Button, Card } from 'react-bootstrap';

/**
 * Error Boundary para capturar erros React e mostrar fallback UI
 * Inclui logging de erros e opções de recuperação
 */
class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      hasError: false,
      error: null,
      errorInfo: null,
      errorId: null
    };
  }

  static getDerivedStateFromError(error) {
    // Atualiza o state para mostrar a UI de fallback
    return {
      hasError: true,
      errorId: Date.now().toString(36) + Math.random().toString(36).substr(2)
    };
  }

  componentDidCatch(error, errorInfo) {
    // Log do erro para monitoramento
    console.error('ErrorBoundary caught an error:', {
      error: error.message,
      stack: error.stack,
      componentStack: errorInfo.componentStack,
      errorId: this.state.errorId,
      timestamp: new Date().toISOString(),
      userAgent: navigator.userAgent,
      url: window.location.href
    });

    this.setState({
      error,
      errorInfo
    });

    // Aqui você pode enviar o erro para um serviço de monitoramento
    // como Sentry, LogRocket, etc.
    if (this.props.onError) {
      this.props.onError(error, errorInfo);
    }
  }

  handleRetry = () => {
    this.setState({
      hasError: false,
      error: null,
      errorInfo: null,
      errorId: null
    });
  };

  handleReload = () => {
    window.location.reload();
  };

  render() {
    if (this.state.hasError) {
      // UI de fallback customizada
      if (this.props.fallback) {
        return this.props.fallback(this.state.error, this.handleRetry);
      }

      // UI de fallback padrão
      return (
        <Container className="mt-5">
          <Card className="border-danger">
            <Card.Header className="bg-danger text-white">
              <h4 className="mb-0">
                <i className="bi bi-exclamation-triangle me-2"></i>
                Oops! Algo deu errado
              </h4>
            </Card.Header>
            <Card.Body>
              <Alert variant="danger" className="mb-4">
                <Alert.Heading>Erro inesperado</Alert.Heading>
                <p>
                  Ocorreu um erro inesperado na aplicação. Nossa equipe foi notificada 
                  e está trabalhando para resolver o problema.
                </p>
                <hr />
                <div className="d-flex gap-2">
                  <Button 
                    variant="outline-danger" 
                    onClick={this.handleRetry}
                    size="sm"
                  >
                    <i className="bi bi-arrow-clockwise me-1"></i>
                    Tentar Novamente
                  </Button>
                  <Button 
                    variant="outline-secondary" 
                    onClick={this.handleReload}
                    size="sm"
                  >
                    <i className="bi bi-arrow-repeat me-1"></i>
                    Recarregar Página
                  </Button>
                </div>
              </Alert>

              {process.env.NODE_ENV === 'development' && (
                <details className="mt-3">
                  <summary className="btn btn-outline-secondary btn-sm">
                    Detalhes do Erro (Desenvolvimento)
                  </summary>
                  <div className="mt-3">
                    <h6>Erro:</h6>
                    <pre className="bg-light p-2 rounded">
                      {this.state.error && this.state.error.toString()}
                    </pre>
                    
                    <h6 className="mt-3">Stack Trace:</h6>
                    <pre className="bg-light p-2 rounded small">
                      {this.state.errorInfo.componentStack}
                    </pre>
                    
                    <h6 className="mt-3">ID do Erro:</h6>
                    <code>{this.state.errorId}</code>
                  </div>
                </details>
              )}

              <div className="mt-4 text-muted small">
                <p>
                  <strong>O que você pode fazer:</strong>
                </p>
                <ul>
                  <li>Tente recarregar a página</li>
                  <li>Verifique sua conexão com a internet</li>
                  <li>Limpe o cache do navegador</li>
                  <li>Entre em contato com o suporte se o problema persistir</li>
                </ul>
              </div>
            </Card.Body>
          </Card>
        </Container>
      );
    }

    return this.props.children;
  }
}

/**
 * Hook para usar ErrorBoundary de forma funcional
 */
export const withErrorBoundary = (Component, errorBoundaryProps = {}) => {
  return function WrappedComponent(props) {
    return (
      <ErrorBoundary {...errorBoundaryProps}>
        <Component {...props} />
      </ErrorBoundary>
    );
  };
};

/**
 * Componente de erro mais simples para casos específicos
 */
export const ErrorFallback = ({ 
  error, 
  resetError, 
  title = "Algo deu errado",
  message = "Ocorreu um erro inesperado. Tente novamente."
}) => {
  return (
    <Alert variant="danger" className="m-3">
      <Alert.Heading>{title}</Alert.Heading>
      <p>{message}</p>
      {error && process.env.NODE_ENV === 'development' && (
        <details className="mt-2">
          <summary>Detalhes do erro</summary>
          <pre className="mt-2 small">{error.message}</pre>
        </details>
      )}
      <hr />
      <div className="d-flex gap-2">
        <Button variant="outline-danger" size="sm" onClick={resetError}>
          Tentar Novamente
        </Button>
      </div>
    </Alert>
  );
};

export default ErrorBoundary;
