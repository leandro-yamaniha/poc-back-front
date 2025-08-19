import React from 'react';
import { Alert, Button, Card, Container, Row, Col } from 'react-bootstrap';

/**
 * Componentes de fallback para diferentes tipos de erro
 */

// Fallback para erro de conexão
export const NetworkErrorFallback = ({ onRetry, onOfflineMode }) => (
  <Alert variant="warning" className="m-3">
    <Alert.Heading>
      <i className="bi bi-wifi-off me-2"></i>
      Sem conexão
    </Alert.Heading>
    <p>
      Não foi possível conectar ao servidor. Verifique sua conexão com a internet.
    </p>
    <hr />
    <div className="d-flex gap-2">
      <Button variant="outline-warning" size="sm" onClick={onRetry}>
        <i className="bi bi-arrow-clockwise me-1"></i>
        Tentar Novamente
      </Button>
      {onOfflineMode && (
        <Button variant="outline-secondary" size="sm" onClick={onOfflineMode}>
          <i className="bi bi-cloud-slash me-1"></i>
          Modo Offline
        </Button>
      )}
    </div>
  </Alert>
);

// Fallback para dados não encontrados
export const NotFoundFallback = ({ 
  title = "Dados não encontrados",
  message = "Os dados solicitados não foram encontrados.",
  onGoBack,
  onRefresh 
}) => (
  <Container className="text-center py-5">
    <div className="display-1 text-muted mb-3">🔍</div>
    <h3>{title}</h3>
    <p className="text-muted">{message}</p>
    <div className="d-flex gap-2 justify-content-center">
      {onGoBack && (
        <Button variant="outline-primary" onClick={onGoBack}>
          <i className="bi bi-arrow-left me-1"></i>
          Voltar
        </Button>
      )}
      {onRefresh && (
        <Button variant="outline-secondary" onClick={onRefresh}>
          <i className="bi bi-arrow-clockwise me-1"></i>
          Atualizar
        </Button>
      )}
    </div>
  </Container>
);

// Fallback para erro de servidor
export const ServerErrorFallback = ({ onRetry, onContactSupport }) => (
  <Alert variant="danger" className="m-3">
    <Alert.Heading>
      <i className="bi bi-server me-2"></i>
      Erro do servidor
    </Alert.Heading>
    <p>
      Ocorreu um problema no servidor. Nossa equipe foi notificada e está trabalhando na solução.
    </p>
    <hr />
    <div className="d-flex gap-2">
      <Button variant="outline-danger" size="sm" onClick={onRetry}>
        <i className="bi bi-arrow-clockwise me-1"></i>
        Tentar Novamente
      </Button>
      {onContactSupport && (
        <Button variant="outline-secondary" size="sm" onClick={onContactSupport}>
          <i className="bi bi-headset me-1"></i>
          Contatar Suporte
        </Button>
      )}
    </div>
  </Alert>
);

// Fallback para operação em andamento com erro
export const OperationErrorFallback = ({ 
  operation = "operação",
  error,
  onRetry,
  onCancel 
}) => (
  <Card className="border-warning">
    <Card.Body className="text-center">
      <div className="text-warning mb-3">
        <i className="bi bi-exclamation-triangle" style={{ fontSize: '3rem' }}></i>
      </div>
      <h5>Falha na {operation}</h5>
      <p className="text-muted">
        {error?.message || `Não foi possível completar a ${operation}.`}
      </p>
      <div className="d-flex gap-2 justify-content-center">
        <Button variant="warning" size="sm" onClick={onRetry}>
          <i className="bi bi-arrow-clockwise me-1"></i>
          Tentar Novamente
        </Button>
        <Button variant="outline-secondary" size="sm" onClick={onCancel}>
          Cancelar
        </Button>
      </div>
    </Card.Body>
  </Card>
);

// Fallback para lista vazia com erro
export const EmptyStateWithError = ({ 
  title = "Nenhum item encontrado",
  message = "Não foi possível carregar os dados.",
  onRetry,
  onCreate 
}) => (
  <div className="empty-state text-center py-5">
    <div className="display-1 mb-3">📭</div>
    <h3>{title}</h3>
    <p className="text-muted">{message}</p>
    <div className="d-flex gap-2 justify-content-center">
      <Button variant="outline-primary" onClick={onRetry}>
        <i className="bi bi-arrow-clockwise me-1"></i>
        Tentar Novamente
      </Button>
      {onCreate && (
        <Button variant="primary" onClick={onCreate}>
          <i className="bi bi-plus me-1"></i>
          Criar Novo
        </Button>
      )}
    </div>
  </div>
);

// Fallback para formulário com erro de validação
export const FormErrorFallback = ({ 
  errors = [],
  onFixErrors,
  onReset 
}) => (
  <Alert variant="danger">
    <Alert.Heading>
      <i className="bi bi-exclamation-circle me-2"></i>
      Erro de validação
    </Alert.Heading>
    <p>Corrija os seguintes problemas:</p>
    <ul className="mb-3">
      {errors.map((error, index) => (
        <li key={index}>{error}</li>
      ))}
    </ul>
    <hr />
    <div className="d-flex gap-2">
      <Button variant="outline-danger" size="sm" onClick={onFixErrors}>
        <i className="bi bi-pencil me-1"></i>
        Corrigir
      </Button>
      <Button variant="outline-secondary" size="sm" onClick={onReset}>
        <i className="bi bi-arrow-counterclockwise me-1"></i>
        Resetar
      </Button>
    </div>
  </Alert>
);

// Fallback genérico com ações customizáveis
export const GenericErrorFallback = ({ 
  icon = "bi-exclamation-triangle",
  title = "Algo deu errado",
  message = "Ocorreu um erro inesperado.",
  variant = "warning",
  actions = []
}) => (
  <Alert variant={variant} className="m-3">
    <Alert.Heading>
      <i className={`bi ${icon} me-2`}></i>
      {title}
    </Alert.Heading>
    <p>{message}</p>
    {actions.length > 0 && (
      <>
        <hr />
        <div className="d-flex gap-2">
          {actions.map((action, index) => (
            <Button
              key={index}
              variant={action.variant || `outline-${variant}`}
              size="sm"
              onClick={action.onClick}
            >
              {action.icon && <i className={`bi ${action.icon} me-1`}></i>}
              {action.label}
            </Button>
          ))}
        </div>
      </>
    )}
  </Alert>
);

// Hook para selecionar fallback apropriado baseado no tipo de erro
export const useErrorFallback = () => {
  const getFallbackComponent = (error, options = {}) => {
    if (!error.response) {
      return (props) => (
        <NetworkErrorFallback 
          onRetry={options.onRetry}
          onOfflineMode={options.onOfflineMode}
          {...props}
        />
      );
    }

    const status = error.response.status;

    if (status === 404) {
      return (props) => (
        <NotFoundFallback
          onGoBack={options.onGoBack}
          onRefresh={options.onRefresh}
          {...props}
        />
      );
    }

    if (status >= 500) {
      return (props) => (
        <ServerErrorFallback
          onRetry={options.onRetry}
          onContactSupport={options.onContactSupport}
          {...props}
        />
      );
    }

    return (props) => (
      <GenericErrorFallback
        title="Erro na operação"
        message={error.response.data?.message || "Não foi possível completar a operação."}
        actions={[
          {
            label: "Tentar Novamente",
            icon: "bi-arrow-clockwise",
            onClick: options.onRetry
          }
        ]}
        {...props}
      />
    );
  };

  return { getFallbackComponent };
};

export default {
  NetworkErrorFallback,
  NotFoundFallback,
  ServerErrorFallback,
  OperationErrorFallback,
  EmptyStateWithError,
  FormErrorFallback,
  GenericErrorFallback,
  useErrorFallback
};
