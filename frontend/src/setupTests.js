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

// Mock React Bootstrap
jest.mock('react-bootstrap', () => {
  const MockNavbar = ({ children, ...props }) => <div {...props}>{children}</div>;
  MockNavbar.Brand = ({ children, ...props }) => <div {...props}>{children}</div>;
  MockNavbar.Toggle = ({ children, ...props }) => <button {...props} role="button" aria-label="Toggle navigation">{children}</button>;
  MockNavbar.Collapse = ({ children, ...props }) => <div {...props} className="collapse navbar-collapse">{children}</div>;
  
  const MockNav = ({ children, ...props }) => <div {...props}>{children}</div>;
  MockNav.Link = ({ children, ...props }) => <a {...props}>{children}</a>;

  const MockCard = ({ children, ...props }) => <div {...props}>{children}</div>;
  MockCard.Header = ({ children, ...props }) => <div {...props}>{children}</div>;
  MockCard.Body = ({ children, ...props }) => <div {...props}>{children}</div>;
  
  const MockModal = ({ children, show, ...props }) => show ? <div role="dialog" {...props}>{children}</div> : null;
  MockModal.Header = ({ children, ...props }) => <div {...props}>{children}</div>;
  MockModal.Title = ({ children, ...props }) => <h4 {...props}>{children}</h4>;
  MockModal.Body = ({ children, ...props }) => <div {...props}>{children}</div>;
  MockModal.Footer = ({ children, ...props }) => <div {...props}>{children}</div>;
  
  const MockForm = ({ children, onSubmit, ...props }) => <form onSubmit={onSubmit} {...props}>{children}</form>;
  MockForm.Group = ({ children, ...props }) => <div {...props}>{children}</div>;
  MockForm.Label = ({ children, ...props }) => <label {...props}>{children}</label>;
  MockForm.Control = ({ value, onChange, name, ...props }) => (
    <input 
      aria-label={name} 
      value={value} 
      onChange={onChange} 
      name={name} 
      {...props} 
    />
  );
  MockForm.Select = ({ value, onChange, children, ...props }) => (
    <select value={value} onChange={onChange} {...props}>{children}</select>
  );
  MockForm.Check = ({ label, checked, onChange, ...props }) => (
    <div {...props}>
      <input type="checkbox" checked={checked} onChange={onChange} />
      <label>{label}</label>
    </div>
  );

  return {
    Container: ({ children, ...props }) => <div {...props}>{children}</div>,
    Row: ({ children, ...props }) => <div {...props}>{children}</div>,
    Col: ({ children, ...props }) => <div {...props}>{children}</div>,
    Card: MockCard,
    Button: ({ children, onClick, ...props }) => <button onClick={onClick} {...props}>{children}</button>,
    Form: MockForm,
    Alert: ({ children, ...props }) => <div {...props}>{children}</div>,
    Table: ({ children, ...props }) => <table {...props}>{children}</table>,
    Modal: MockModal,
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

// Mock LoadingContext
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

// Mock LoadingSpinner
jest.mock('./components/LoadingSpinner', () => ({
  __esModule: true,
  default: ({ text = 'Loading...' }) => <div data-testid="loading-spinner">{text}</div>
}));

// Mock ErrorFallbacks components
jest.mock('./components/ErrorFallbacks', () => ({
  __esModule: true,
  NetworkErrorFallback: ({ onRetry, onOfflineMode }) => (
    <div data-testid="network-error-fallback">
      <div>Sem conexão</div>
      <div>Não foi possível conectar ao servidor. Verifique sua conexão com a internet.</div>
      {onRetry && <button onClick={onRetry}>Tentar Novamente</button>}
      {onOfflineMode && <button onClick={onOfflineMode}>Modo Offline</button>}
    </div>
  ),
  NotFoundFallback: ({ title = "Dados não encontrados", message = "Os dados solicitados não foram encontrados.", onGoBack, onRefresh }) => (
    <div data-testid="not-found-fallback">
      <div>{title}</div>
      <div>{message}</div>
      {onGoBack && <button onClick={onGoBack}>Voltar</button>}
      {onRefresh && <button onClick={onRefresh}>Atualizar</button>}
    </div>
  ),
  ServerErrorFallback: ({ onRetry, onContactSupport }) => (
    <div data-testid="server-error-fallback">
      <div>Erro do servidor</div>
      <div>Ocorreu um problema no servidor. Nossa equipe foi notificada e está trabalhando na solução.</div>
      {onRetry && <button onClick={onRetry}>Tentar Novamente</button>}
      {onContactSupport && <button onClick={onContactSupport}>Contatar Suporte</button>}
    </div>
  ),
  OperationErrorFallback: ({ operation = "operação", error, onRetry, onCancel }) => (
    <div data-testid="operation-error-fallback">
      <div>Falha na {operation}</div>
      <div>{error?.message || `Não foi possível completar a ${operation}.`}</div>
      {onRetry && <button onClick={onRetry}>Tentar Novamente</button>}
      {onCancel && <button onClick={onCancel}>Cancelar</button>}
    </div>
  ),
  EmptyStateWithError: ({ title = "Nenhum item encontrado", message = "Não foi possível carregar os dados.", onRetry, onCreate }) => (
    <div data-testid="empty-state-error">
      <div>{title}</div>
      <div>{message}</div>
      {onRetry && <button onClick={onRetry}>Tentar Novamente</button>}
      {onCreate && <button onClick={onCreate}>Criar Novo</button>}
    </div>
  ),
  FormErrorFallback: ({ errors = [], onFixErrors, onReset }) => (
    <div data-testid="form-error-fallback">
      <div>Erro de validação</div>
      <div>Corrija os seguintes problemas:</div>
      <ul>
        {errors.map((error, index) => (
          <li key={index}>{error}</li>
        ))}
      </ul>
      {onFixErrors && <button onClick={onFixErrors}>Corrigir</button>}
      {onReset && <button onClick={onReset}>Resetar</button>}
    </div>
  ),
  GenericErrorFallback: ({ title = "Algo deu errado", message = "Ocorreu um erro inesperado.", actions = [] }) => (
    <div data-testid="generic-error-fallback">
      <div>{title}</div>
      <div>{message}</div>
      {actions.map((action, index) => (
        <button key={index} onClick={action.onClick}>
          {action.label}
        </button>
      ))}
    </div>
  ),
  useErrorFallback: () => ({
    getFallbackComponent: (error, options = {}) => {
      if (!error.response) {
        return () => (
          <div data-testid="network-error-fallback">
            <div>Sem conexão</div>
            {options.onRetry && <button onClick={options.onRetry}>Tentar Novamente</button>}
          </div>
        );
      }

      const status = error.response.status;

      if (status === 404) {
        return () => (
          <div data-testid="not-found-fallback">
            <div>Dados não encontrados</div>
            {options.onGoBack && <button onClick={options.onGoBack}>Voltar</button>}
          </div>
        );
      }

      if (status >= 500) {
        return () => (
          <div data-testid="server-error-fallback">
            <div>Erro do servidor</div>
            {options.onRetry && <button onClick={options.onRetry}>Tentar Novamente</button>}
          </div>
        );
      }

      return () => (
        <div data-testid="generic-error-fallback">
          <div>Erro na operação</div>
          <div>{error.response.data?.message || "Não foi possível completar a operação."}</div>
          {options.onRetry && <button onClick={options.onRetry}>Tentar Novamente</button>}
        </div>
      );
    }
  })
}));
