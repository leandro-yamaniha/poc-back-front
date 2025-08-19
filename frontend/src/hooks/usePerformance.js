import { useMemo, useCallback, useRef, useEffect, useState } from 'react';

/**
 * Hook para memoização inteligente de valores computados
 * Evita recálculos desnecessários em operações custosas
 */
export const useSmartMemo = (factory, deps, options = {}) => {
  const { 
    maxAge = 5 * 60 * 1000, // 5 minutos
    compareFunction = null 
  } = options;
  
  const cacheRef = useRef({ value: null, timestamp: 0, deps: [] });
  
  return useMemo(() => {
    const now = Date.now();
    const cache = cacheRef.current;
    
    // Verificar se cache expirou
    if (now - cache.timestamp > maxAge) {
      cache.value = factory();
      cache.timestamp = now;
      cache.deps = deps;
      return cache.value;
    }
    
    // Verificar se deps mudaram
    const depsChanged = compareFunction 
      ? !compareFunction(cache.deps, deps)
      : cache.deps.some((dep, index) => dep !== deps[index]);
    
    if (depsChanged || cache.value === null) {
      cache.value = factory();
      cache.timestamp = now;
      cache.deps = deps;
    }
    
    return cache.value;
  }, deps);
};

/**
 * Hook para callbacks estáveis com cleanup automático
 * Previne re-renders desnecessários em componentes filhos
 */
export const useStableCallback = (callback, deps = []) => {
  const callbackRef = useRef(callback);
  const stableCallback = useRef();
  
  // Atualizar callback ref quando deps mudam
  useEffect(() => {
    callbackRef.current = callback;
  }, [callback, ...deps]);
  
  // Criar callback estável apenas uma vez
  if (!stableCallback.current) {
    stableCallback.current = (...args) => {
      return callbackRef.current(...args);
    };
  }
  
  return stableCallback.current;
};

/**
 * Hook para filtros otimizados com memoização
 * Ideal para listas grandes com múltiplos filtros
 */
export const useOptimizedFilter = (data, filters, options = {}) => {
  const { 
    caseSensitive = false,
    searchFields = [],
    sortBy = null,
    sortOrder = 'asc'
  } = options;
  
  return useMemo(() => {
    if (!data || data.length === 0) return [];
    
    let filtered = data;
    
    // Aplicar filtros
    Object.entries(filters).forEach(([key, value]) => {
      if (value && value.toString().trim()) {
        filtered = filtered.filter(item => {
          if (searchFields.length > 0) {
            // Buscar em campos específicos
            return searchFields.some(field => {
              const fieldValue = item[field]?.toString() || '';
              const searchValue = caseSensitive ? value : value.toLowerCase();
              const itemValue = caseSensitive ? fieldValue : fieldValue.toLowerCase();
              return itemValue.includes(searchValue);
            });
          } else {
            // Buscar no campo específico
            const itemValue = item[key]?.toString() || '';
            const searchValue = caseSensitive ? value : value.toLowerCase();
            const compareValue = caseSensitive ? itemValue : itemValue.toLowerCase();
            return compareValue.includes(searchValue);
          }
        });
      }
    });
    
    // Aplicar ordenação
    if (sortBy) {
      filtered = [...filtered].sort((a, b) => {
        const aValue = a[sortBy];
        const bValue = b[sortBy];
        
        if (aValue < bValue) return sortOrder === 'asc' ? -1 : 1;
        if (aValue > bValue) return sortOrder === 'asc' ? 1 : -1;
        return 0;
      });
    }
    
    return filtered;
  }, [data, filters, caseSensitive, searchFields, sortBy, sortOrder]);
};

/**
 * Hook para paginação otimizada
 * Calcula apenas os itens visíveis na página atual
 */
export const useOptimizedPagination = (data, pageSize = 10) => {
  const [currentPage, setCurrentPage] = useState(1);
  
  const paginationData = useMemo(() => {
    const totalItems = data.length;
    const totalPages = Math.ceil(totalItems / pageSize);
    const startIndex = (currentPage - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    const currentItems = data.slice(startIndex, endIndex);
    
    return {
      currentItems,
      currentPage,
      totalPages,
      totalItems,
      hasNextPage: currentPage < totalPages,
      hasPrevPage: currentPage > 1,
      startIndex: startIndex + 1,
      endIndex: Math.min(endIndex, totalItems)
    };
  }, [data, currentPage, pageSize]);
  
  const goToPage = useCallback((page) => {
    setCurrentPage(Math.max(1, Math.min(page, paginationData.totalPages)));
  }, [paginationData.totalPages]);
  
  const nextPage = useCallback(() => {
    if (paginationData.hasNextPage) {
      setCurrentPage(prev => prev + 1);
    }
  }, [paginationData.hasNextPage]);
  
  const prevPage = useCallback(() => {
    if (paginationData.hasPrevPage) {
      setCurrentPage(prev => prev - 1);
    }
  }, [paginationData.hasPrevPage]);
  
  return {
    ...paginationData,
    goToPage,
    nextPage,
    prevPage,
    setCurrentPage
  };
};

/**
 * Hook para detectar mudanças de performance
 * Monitora re-renders e operações custosas
 */
export const usePerformanceMonitor = (componentName, deps = []) => {
  const renderCount = useRef(0);
  const lastRenderTime = useRef(Date.now());
  const mountTime = useRef(Date.now());
  
  useEffect(() => {
    renderCount.current += 1;
    const now = Date.now();
    const timeSinceLastRender = now - lastRenderTime.current;
    const timeSinceMount = now - mountTime.current;
    
    if (process.env.NODE_ENV === 'development') {
      console.log(`[Performance] ${componentName}:`, {
        renderCount: renderCount.current,
        timeSinceLastRender,
        timeSinceMount,
        deps: deps.length
      });
      
      // Alertar sobre re-renders frequentes
      if (timeSinceLastRender < 100 && renderCount.current > 5) {
        console.warn(`[Performance Warning] ${componentName} está re-renderizando muito frequentemente`);
      }
    }
    
    lastRenderTime.current = now;
  });
  
  return {
    renderCount: renderCount.current,
    timeSinceMount: Date.now() - mountTime.current
  };
};

/**
 * Hook para cleanup automático de recursos
 * Previne memory leaks em componentes
 */
export const useCleanup = () => {
  const cleanupFunctions = useRef([]);
  
  const addCleanup = useCallback((cleanupFn) => {
    cleanupFunctions.current.push(cleanupFn);
  }, []);
  
  const removeCleanup = useCallback((cleanupFn) => {
    const index = cleanupFunctions.current.indexOf(cleanupFn);
    if (index > -1) {
      cleanupFunctions.current.splice(index, 1);
    }
  }, []);
  
  const runCleanup = useCallback(() => {
    cleanupFunctions.current.forEach(fn => {
      try {
        fn();
      } catch (error) {
        console.error('Cleanup error:', error);
      }
    });
    cleanupFunctions.current = [];
  }, []);
  
  // Cleanup automático no unmount
  useEffect(() => {
    return () => {
      runCleanup();
    };
  }, [runCleanup]);
  
  return { addCleanup, removeCleanup, runCleanup };
};

export default {
  useSmartMemo,
  useStableCallback,
  useOptimizedFilter,
  useOptimizedPagination,
  usePerformanceMonitor,
  useCleanup
};
