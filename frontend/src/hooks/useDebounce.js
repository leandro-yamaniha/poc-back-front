import { useState, useEffect, useRef, useCallback } from 'react';

/**
 * Hook para debounce de valores e funções
 * Otimiza performance evitando execuções desnecessárias
 */
export const useDebounce = (value, delay = 300) => {
  const [debouncedValue, setDebouncedValue] = useState(value);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);

  return debouncedValue;
};

/**
 * Hook para debounce de funções (callback)
 * Útil para otimizar chamadas de API e eventos
 */
export const useDebouncedCallback = (callback, delay = 300, deps = []) => {
  const timeoutRef = useRef(null);
  const callbackRef = useRef(callback);

  // Atualizar callback ref quando deps mudam
  useEffect(() => {
    callbackRef.current = callback;
  }, [callback, ...deps]);

  const debouncedCallback = useCallback((...args) => {
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current);
    }

    timeoutRef.current = setTimeout(() => {
      callbackRef.current(...args);
    }, delay);
  }, [delay]);

  // Cleanup no unmount
  useEffect(() => {
    return () => {
      if (timeoutRef.current) {
        clearTimeout(timeoutRef.current);
      }
    };
  }, []);

  // Função para cancelar debounce pendente
  const cancel = useCallback(() => {
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current);
      timeoutRef.current = null;
    }
  }, []);

  // Função para executar imediatamente
  const flush = useCallback((...args) => {
    cancel();
    callbackRef.current(...args);
  }, [cancel]);

  return { debouncedCallback, cancel, flush };
};

/**
 * Hook para debounce de busca com loading state
 * Combina debounce com estado de loading para UX melhor
 */
export const useDebouncedSearch = (searchFunction, delay = 500) => {
  const [isSearching, setIsSearching] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const searchRef = useRef(null);

  const debouncedSearch = useCallback(async (term) => {
    if (searchRef.current) {
      clearTimeout(searchRef.current);
    }

    if (!term.trim()) {
      setIsSearching(false);
      return;
    }

    setIsSearching(true);

    searchRef.current = setTimeout(async () => {
      try {
        await searchFunction(term);
      } catch (error) {
        console.error('Search error:', error);
      } finally {
        setIsSearching(false);
      }
    }, delay);
  }, [searchFunction, delay]);

  const handleSearchChange = useCallback((term) => {
    setSearchTerm(term);
    debouncedSearch(term);
  }, [debouncedSearch]);

  const clearSearch = useCallback(() => {
    if (searchRef.current) {
      clearTimeout(searchRef.current);
    }
    setSearchTerm('');
    setIsSearching(false);
  }, []);

  // Cleanup
  useEffect(() => {
    return () => {
      if (searchRef.current) {
        clearTimeout(searchRef.current);
      }
    };
  }, []);

  return {
    searchTerm,
    isSearching,
    handleSearchChange,
    clearSearch
  };
};

/**
 * Hook para throttle (limitar frequência de execução)
 * Útil para eventos de scroll, resize, etc.
 */
export const useThrottle = (callback, delay = 300) => {
  const lastRun = useRef(0);
  const timeoutRef = useRef(null);

  const throttledCallback = useCallback((...args) => {
    const now = Date.now();
    
    if (now - lastRun.current >= delay) {
      callback(...args);
      lastRun.current = now;
    } else {
      if (timeoutRef.current) {
        clearTimeout(timeoutRef.current);
      }
      
      timeoutRef.current = setTimeout(() => {
        callback(...args);
        lastRun.current = Date.now();
      }, delay - (now - lastRun.current));
    }
  }, [callback, delay]);

  useEffect(() => {
    return () => {
      if (timeoutRef.current) {
        clearTimeout(timeoutRef.current);
      }
    };
  }, []);

  return throttledCallback;
};

export default useDebounce;
