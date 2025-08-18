import React, { createContext, useContext, useState, useCallback } from 'react';

/**
 * Context para gerenciar estados de loading globalmente
 */
const LoadingContext = createContext();

/**
 * Provider do contexto de loading
 * @param {Object} props - Propriedades do componente
 * @param {React.ReactNode} props.children - Componentes filhos
 */
export const LoadingProvider = ({ children }) => {
  const [loadingStates, setLoadingStates] = useState({});

  /**
   * Define o estado de loading para uma chave específica
   * @param {string} key - Chave identificadora do loading
   * @param {boolean} isLoading - Se está carregando ou não
   */
  const setLoading = useCallback((key, isLoading) => {
    setLoadingStates(prev => ({
      ...prev,
      [key]: isLoading
    }));
  }, []);

  /**
   * Remove um estado de loading específico
   * @param {string} key - Chave do loading a ser removida
   */
  const removeLoading = useCallback((key) => {
    setLoadingStates(prev => {
      const newState = { ...prev };
      delete newState[key];
      return newState;
    });
  }, []);

  /**
   * Verifica se uma chave específica está em loading
   * @param {string} key - Chave a ser verificada
   * @returns {boolean} - Se está carregando
   */
  const isLoading = useCallback((key) => {
    return Boolean(loadingStates[key]);
  }, [loadingStates]);

  /**
   * Verifica se há algum loading ativo
   * @returns {boolean} - Se há algum loading ativo
   */
  const hasAnyLoading = useCallback(() => {
    return Object.values(loadingStates).some(loading => loading);
  }, [loadingStates]);

  /**
   * Executa uma função async com loading automático
   * @param {string} key - Chave do loading
   * @param {Function} asyncFunction - Função assíncrona a ser executada
   * @returns {Promise} - Promise da função executada
   */
  const withLoading = useCallback(async (key, asyncFunction) => {
    try {
      setLoading(key, true);
      const result = await asyncFunction();
      return result;
    } finally {
      setLoading(key, false);
    }
  }, [setLoading]);

  const value = {
    loadingStates,
    setLoading,
    removeLoading,
    isLoading,
    hasAnyLoading,
    withLoading
  };

  return (
    <LoadingContext.Provider value={value}>
      {children}
    </LoadingContext.Provider>
  );
};

/**
 * Hook para usar o contexto de loading
 * @returns {Object} - Funções e estados do loading
 */
export const useLoading = () => {
  const context = useContext(LoadingContext);
  
  if (!context) {
    throw new Error('useLoading deve ser usado dentro de um LoadingProvider');
  }
  
  return context;
};

export default LoadingContext;
