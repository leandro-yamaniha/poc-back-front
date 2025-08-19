import { useState, useCallback, useRef } from 'react';
import { toast } from 'react-toastify';

/**
 * Hook para gerenciamento robusto de erros com retry automático
 * Inclui categorização de erros, retry exponential backoff e fallbacks
 */
export const useErrorHandling = () => {
  const [retryCount, setRetryCount] = useState(0);
  const [isRetrying, setIsRetrying] = useState(false);
  const retryTimeoutRef = useRef(null);

  // Categorizar tipos de erro
  const categorizeError = useCallback((error) => {
    if (!error.response) {
      return {
        type: 'NETWORK_ERROR',
        message: 'Erro de conexão. Verifique sua internet.',
        retryable: true,
        severity: 'high'
      };
    }

    const status = error.response.status;
    
    if (status >= 500) {
      return {
        type: 'SERVER_ERROR',
        message: 'Erro interno do servidor. Tente novamente.',
        retryable: true,
        severity: 'high'
      };
    }
    
    if (status === 429) {
      return {
        type: 'RATE_LIMIT',
        message: 'Muitas tentativas. Aguarde um momento.',
        retryable: true,
        severity: 'medium'
      };
    }
    
    if (status === 409) {
      return {
        type: 'CONFLICT',
        message: error.response.data?.message || 'Conflito de dados.',
        retryable: false,
        severity: 'low'
      };
    }
    
    if (status === 404) {
      return {
        type: 'NOT_FOUND',
        message: 'Recurso não encontrado.',
        retryable: false,
        severity: 'medium'
      };
    }
    
    if (status >= 400 && status < 500) {
      return {
        type: 'CLIENT_ERROR',
        message: error.response.data?.message || 'Erro na solicitação.',
        retryable: false,
        severity: 'low'
      };
    }

    return {
      type: 'UNKNOWN_ERROR',
      message: 'Erro desconhecido. Tente novamente.',
      retryable: true,
      severity: 'medium'
    };
  }, []);

  // Calcular delay para retry com exponential backoff
  const calculateRetryDelay = useCallback((attempt) => {
    const baseDelay = 1000; // 1 segundo
    const maxDelay = 30000; // 30 segundos
    const delay = Math.min(baseDelay * Math.pow(2, attempt), maxDelay);
    
    // Adicionar jitter para evitar thundering herd
    const jitter = Math.random() * 0.1 * delay;
    return delay + jitter;
  }, []);

  // Executar operação com retry automático
  const executeWithRetry = useCallback(async (
    operation,
    options = {}
  ) => {
    const {
      maxRetries = 3,
      retryCondition = (error) => categorizeError(error).retryable,
      onRetry = () => {},
      onError = () => {},
      fallback = null
    } = options;

    let lastError = null;
    
    for (let attempt = 0; attempt <= maxRetries; attempt++) {
      try {
        setRetryCount(attempt);
        
        if (attempt > 0) {
          setIsRetrying(true);
          const delay = calculateRetryDelay(attempt - 1);
          
          toast.info(`Tentativa ${attempt + 1}/${maxRetries + 1}...`, {
            autoClose: 2000
          });
          
          await new Promise(resolve => {
            retryTimeoutRef.current = setTimeout(resolve, delay);
          });
        }
        
        const result = await operation();
        setIsRetrying(false);
        setRetryCount(0);
        
        if (attempt > 0) {
          toast.success('Operação realizada com sucesso!');
        }
        
        return result;
        
      } catch (error) {
        lastError = error;
        const errorInfo = categorizeError(error);
        
        // Log estruturado do erro
        console.error('Error attempt', attempt + 1, {
          error: error.message,
          type: errorInfo.type,
          severity: errorInfo.severity,
          retryable: errorInfo.retryable,
          stack: error.stack
        });
        
        // Se não é retryable ou atingiu max tentativas
        if (!retryCondition(error) || attempt === maxRetries) {
          setIsRetrying(false);
          setRetryCount(0);
          
          // Chamar callback de erro
          onError(error, errorInfo);
          
          // Mostrar notificação de erro
          if (errorInfo.severity === 'high') {
            toast.error(errorInfo.message, {
              autoClose: false,
              closeButton: true
            });
          } else {
            toast.error(errorInfo.message);
          }
          
          // Tentar fallback se disponível
          if (fallback && typeof fallback === 'function') {
            try {
              return await fallback(error);
            } catch (fallbackError) {
              console.error('Fallback failed:', fallbackError);
            }
          }
          
          throw error;
        }
        
        // Chamar callback de retry
        onRetry(error, attempt + 1);
      }
    }
  }, [categorizeError, calculateRetryDelay]);

  // Wrapper para operações de API comuns
  const handleApiCall = useCallback(async (apiCall, options = {}) => {
    return executeWithRetry(apiCall, {
      maxRetries: 3,
      retryCondition: (error) => {
        const errorInfo = categorizeError(error);
        return errorInfo.retryable;
      },
      ...options
    });
  }, [executeWithRetry, categorizeError]);

  // Limpar timeouts ao desmontar
  const cleanup = useCallback(() => {
    if (retryTimeoutRef.current) {
      clearTimeout(retryTimeoutRef.current);
      retryTimeoutRef.current = null;
    }
    setIsRetrying(false);
    setRetryCount(0);
  }, []);

  // Criar notificação de erro personalizada com ações
  const showErrorWithActions = useCallback((error, actions = []) => {
    const errorInfo = categorizeError(error);
    
    const CustomError = ({ closeToast }) => (
      <div>
        <div className="mb-2">
          <strong>{errorInfo.message}</strong>
        </div>
        <div className="d-flex gap-2">
          {actions.map((action, index) => (
            <button
              key={index}
              className="btn btn-sm btn-outline-primary"
              onClick={() => {
                action.handler();
                closeToast();
              }}
            >
              {action.label}
            </button>
          ))}
          <button
            className="btn btn-sm btn-secondary"
            onClick={closeToast}
          >
            Fechar
          </button>
        </div>
      </div>
    );

    toast.error(<CustomError />, {
      autoClose: false,
      closeButton: false
    });
  }, [categorizeError]);

  return {
    executeWithRetry,
    handleApiCall,
    categorizeError,
    showErrorWithActions,
    cleanup,
    isRetrying,
    retryCount
  };
};

export default useErrorHandling;
