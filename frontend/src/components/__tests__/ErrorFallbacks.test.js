import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import {
  NetworkErrorFallback,
  NotFoundFallback,
  ServerErrorFallback,
  OperationErrorFallback,
  EmptyStateWithError,
  FormErrorFallback,
  GenericErrorFallback,
  useErrorFallback
} from '../ErrorFallbacks';
import { renderHook } from '@testing-library/react';

describe('NetworkErrorFallback', () => {
  test('renders network error message', () => {
    const onRetry = jest.fn();
    const onOfflineMode = jest.fn();

    render(<NetworkErrorFallback onRetry={onRetry} onOfflineMode={onOfflineMode} />);

    expect(screen.getByText('Sem conexão')).toBeInTheDocument();
    expect(screen.getByText('Não foi possível conectar ao servidor. Verifique sua conexão com a internet.')).toBeInTheDocument();
    expect(screen.getByText('Tentar Novamente')).toBeInTheDocument();
    expect(screen.getByText('Modo Offline')).toBeInTheDocument();
  });

  test('calls onRetry when retry button is clicked', () => {
    const onRetry = jest.fn();

    render(<NetworkErrorFallback onRetry={onRetry} />);

    fireEvent.click(screen.getByText('Tentar Novamente'));
    expect(onRetry).toHaveBeenCalled();
  });

  test('calls onOfflineMode when offline button is clicked', () => {
    const onOfflineMode = jest.fn();

    render(<NetworkErrorFallback onOfflineMode={onOfflineMode} />);

    fireEvent.click(screen.getByText('Modo Offline'));
    expect(onOfflineMode).toHaveBeenCalled();
  });

  test('hides offline button when onOfflineMode is not provided', () => {
    render(<NetworkErrorFallback onRetry={jest.fn()} />);

    expect(screen.queryByText('Modo Offline')).not.toBeInTheDocument();
  });
});

describe('NotFoundFallback', () => {
  test('renders with default props', () => {
    render(<NotFoundFallback />);

    expect(screen.getByText('Dados não encontrados')).toBeInTheDocument();
    expect(screen.getByText('Os dados solicitados não foram encontrados.')).toBeInTheDocument();
  });

  test('renders with custom props', () => {
    const onGoBack = jest.fn();
    const onRefresh = jest.fn();

    render(
      <NotFoundFallback
        title="Custom Title"
        message="Custom message"
        onGoBack={onGoBack}
        onRefresh={onRefresh}
      />
    );

    expect(screen.getByText('Custom Title')).toBeInTheDocument();
    expect(screen.getByText('Custom message')).toBeInTheDocument();
    expect(screen.getByText('Voltar')).toBeInTheDocument();
    expect(screen.getByText('Atualizar')).toBeInTheDocument();
  });

  test('calls onGoBack when back button is clicked', () => {
    const onGoBack = jest.fn();

    render(<NotFoundFallback onGoBack={onGoBack} />);

    fireEvent.click(screen.getByText('Voltar'));
    expect(onGoBack).toHaveBeenCalled();
  });

  test('calls onRefresh when refresh button is clicked', () => {
    const onRefresh = jest.fn();

    render(<NotFoundFallback onRefresh={onRefresh} />);

    fireEvent.click(screen.getByText('Atualizar'));
    expect(onRefresh).toHaveBeenCalled();
  });
});

describe('ServerErrorFallback', () => {
  test('renders server error message', () => {
    const onRetry = jest.fn();
    const onContactSupport = jest.fn();

    render(<ServerErrorFallback onRetry={onRetry} onContactSupport={onContactSupport} />);

    expect(screen.getByText('Erro do servidor')).toBeInTheDocument();
    expect(screen.getByText('Ocorreu um problema no servidor. Nossa equipe foi notificada e está trabalhando na solução.')).toBeInTheDocument();
    expect(screen.getByText('Tentar Novamente')).toBeInTheDocument();
    expect(screen.getByText('Contatar Suporte')).toBeInTheDocument();
  });

  test('calls onRetry when retry button is clicked', () => {
    const onRetry = jest.fn();

    render(<ServerErrorFallback onRetry={onRetry} />);

    fireEvent.click(screen.getByText('Tentar Novamente'));
    expect(onRetry).toHaveBeenCalled();
  });

  test('calls onContactSupport when support button is clicked', () => {
    const onContactSupport = jest.fn();

    render(<ServerErrorFallback onContactSupport={onContactSupport} />);

    fireEvent.click(screen.getByText('Contatar Suporte'));
    expect(onContactSupport).toHaveBeenCalled();
  });
});

describe('OperationErrorFallback', () => {
  test('renders with default operation name', () => {
    const onRetry = jest.fn();
    const onCancel = jest.fn();

    render(<OperationErrorFallback onRetry={onRetry} onCancel={onCancel} />);

    expect(screen.getByText('Falha na operação')).toBeInTheDocument();
    expect(screen.getByText('Não foi possível completar a operação.')).toBeInTheDocument();
  });

  test('renders with custom operation name and error', () => {
    const onRetry = jest.fn();
    const onCancel = jest.fn();
    const error = { message: 'Custom error message' };

    render(
      <OperationErrorFallback
        operation="upload"
        error={error}
        onRetry={onRetry}
        onCancel={onCancel}
      />
    );

    expect(screen.getByText('Falha na upload')).toBeInTheDocument();
    expect(screen.getByText('Custom error message')).toBeInTheDocument();
  });

  test('calls onRetry and onCancel when buttons are clicked', () => {
    const onRetry = jest.fn();
    const onCancel = jest.fn();

    render(<OperationErrorFallback onRetry={onRetry} onCancel={onCancel} />);

    fireEvent.click(screen.getByText('Tentar Novamente'));
    expect(onRetry).toHaveBeenCalled();

    fireEvent.click(screen.getByText('Cancelar'));
    expect(onCancel).toHaveBeenCalled();
  });
});

describe('EmptyStateWithError', () => {
  test('renders with default props', () => {
    const onRetry = jest.fn();

    render(<EmptyStateWithError onRetry={onRetry} />);

    expect(screen.getByText('Nenhum item encontrado')).toBeInTheDocument();
    expect(screen.getByText('Não foi possível carregar os dados.')).toBeInTheDocument();
    expect(screen.getByText('Tentar Novamente')).toBeInTheDocument();
  });

  test('renders create button when onCreate is provided', () => {
    const onRetry = jest.fn();
    const onCreate = jest.fn();

    render(<EmptyStateWithError onRetry={onRetry} onCreate={onCreate} />);

    expect(screen.getByText('Criar Novo')).toBeInTheDocument();

    fireEvent.click(screen.getByText('Criar Novo'));
    expect(onCreate).toHaveBeenCalled();
  });

  test('renders with custom title and message', () => {
    const onRetry = jest.fn();

    render(
      <EmptyStateWithError
        title="Custom Empty Title"
        message="Custom empty message"
        onRetry={onRetry}
      />
    );

    expect(screen.getByText('Custom Empty Title')).toBeInTheDocument();
    expect(screen.getByText('Custom empty message')).toBeInTheDocument();
  });
});

describe('FormErrorFallback', () => {
  test('renders with error list', () => {
    const errors = ['Campo obrigatório', 'Email inválido'];
    const onFixErrors = jest.fn();
    const onReset = jest.fn();

    render(<FormErrorFallback errors={errors} onFixErrors={onFixErrors} onReset={onReset} />);

    expect(screen.getByText('Erro de validação')).toBeInTheDocument();
    expect(screen.getByText('Corrija os seguintes problemas:')).toBeInTheDocument();
    expect(screen.getByText('Campo obrigatório')).toBeInTheDocument();
    expect(screen.getByText('Email inválido')).toBeInTheDocument();
    expect(screen.getByText('Corrigir')).toBeInTheDocument();
    expect(screen.getByText('Resetar')).toBeInTheDocument();
  });

  test('calls onFixErrors and onReset when buttons are clicked', () => {
    const onFixErrors = jest.fn();
    const onReset = jest.fn();

    render(<FormErrorFallback onFixErrors={onFixErrors} onReset={onReset} />);

    fireEvent.click(screen.getByText('Corrigir'));
    expect(onFixErrors).toHaveBeenCalled();

    fireEvent.click(screen.getByText('Resetar'));
    expect(onReset).toHaveBeenCalled();
  });
});

describe('GenericErrorFallback', () => {
  test('renders with default props', () => {
    render(<GenericErrorFallback />);

    expect(screen.getByText('Algo deu errado')).toBeInTheDocument();
    expect(screen.getByText('Ocorreu um erro inesperado.')).toBeInTheDocument();
  });

  test('renders with custom props and actions', () => {
    const actions = [
      { label: 'Action 1', onClick: jest.fn(), icon: 'bi-check' },
      { label: 'Action 2', onClick: jest.fn(), variant: 'primary' }
    ];

    render(
      <GenericErrorFallback
        icon="bi-warning"
        title="Custom Error"
        message="Custom error message"
        variant="danger"
        actions={actions}
      />
    );

    expect(screen.getByText('Custom Error')).toBeInTheDocument();
    expect(screen.getByText('Custom error message')).toBeInTheDocument();
    expect(screen.getByText('Action 1')).toBeInTheDocument();
    expect(screen.getByText('Action 2')).toBeInTheDocument();
  });

  test('calls action handlers when action buttons are clicked', () => {
    const action1Handler = jest.fn();
    const action2Handler = jest.fn();
    const actions = [
      { label: 'Action 1', onClick: action1Handler },
      { label: 'Action 2', onClick: action2Handler }
    ];

    render(<GenericErrorFallback actions={actions} />);

    fireEvent.click(screen.getByText('Action 1'));
    expect(action1Handler).toHaveBeenCalled();

    fireEvent.click(screen.getByText('Action 2'));
    expect(action2Handler).toHaveBeenCalled();
  });
});

describe('useErrorFallback', () => {
  test('returns network error fallback for network errors', () => {
    const { result } = renderHook(() => useErrorFallback());
    const error = { message: 'Network Error' }; // No response property
    const options = { onRetry: jest.fn() };

    const FallbackComponent = result.current.getFallbackComponent(error, options);
    
    // Render the component returned by the function
    render(React.createElement(FallbackComponent));

    expect(screen.getByText('Sem conexão')).toBeInTheDocument();
  });

  test('returns not found fallback for 404 errors', () => {
    const { result } = renderHook(() => useErrorFallback());
    const error = { response: { status: 404 } };
    const options = { onGoBack: jest.fn() };

    const FallbackComponent = result.current.getFallbackComponent(error, options);
    
    // Render the component returned by the function
    render(React.createElement(FallbackComponent));

    expect(screen.getByText('Dados não encontrados')).toBeInTheDocument();
  });

  test('returns server error fallback for 500+ errors', () => {
    const { result } = renderHook(() => useErrorFallback());
    const error = { response: { status: 500 } };
    const options = { onRetry: jest.fn() };

    const FallbackComponent = result.current.getFallbackComponent(error, options);
    
    // Render the component returned by the function
    render(React.createElement(FallbackComponent));

    expect(screen.getByText('Erro do servidor')).toBeInTheDocument();
  });

  test('returns generic fallback for other errors', () => {
    const { result } = renderHook(() => useErrorFallback());
    const error = { 
      response: { 
        status: 400,
        data: { message: 'Bad request' }
      }
    };
    const options = { onRetry: jest.fn() };

    const FallbackComponent = result.current.getFallbackComponent(error, options);
    
    // Render the component returned by the function
    render(React.createElement(FallbackComponent));

    expect(screen.getByText('Erro na operação')).toBeInTheDocument();
    expect(screen.getByText('Bad request')).toBeInTheDocument();
  });
});
