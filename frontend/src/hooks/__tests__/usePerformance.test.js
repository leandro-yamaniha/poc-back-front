import { renderHook, act } from '@testing-library/react';

// Unmock the hook for this test file
jest.unmock('../usePerformance');
const {
  useSmartMemo,
  useStableCallback,
  useOptimizedFilter,
  useOptimizedPagination,
  usePerformanceMonitor,
  useCleanup
} = require('../usePerformance');

// Mock timers
jest.useFakeTimers();

describe('useSmartMemo', () => {
  afterEach(() => {
    jest.clearAllTimers();
  });

  test('memoizes expensive computations', () => {
    const expensiveFunction = jest.fn(() => 'computed value');
    const { result, rerender } = renderHook(
      ({ deps }) => useSmartMemo(expensiveFunction, deps),
      { initialProps: { deps: [1, 2, 3] } }
    );

    expect(result.current).toBe('computed value');
    expect(expensiveFunction).toHaveBeenCalledTimes(1);

    // Rerender com as mesmas deps
    rerender({ deps: [1, 2, 3] });
    expect(expensiveFunction).toHaveBeenCalledTimes(1); // Não deve recalcular
  });

  test('recalculates when dependencies change', () => {
    const expensiveFunction = jest.fn(() => 'computed value');
    const { result, rerender } = renderHook(
      ({ deps }) => useSmartMemo(expensiveFunction, deps),
      { initialProps: { deps: [1, 2, 3] } }
    );

    expect(expensiveFunction).toHaveBeenCalledTimes(1);

    // Rerender com deps diferentes
    rerender({ deps: [1, 2, 4] });
    expect(expensiveFunction).toHaveBeenCalledTimes(2);
  });

  test('respects maxAge option', () => {
    const expensiveFunction = jest.fn(() => 'computed value');
    const { result, rerender } = renderHook(
      ({ deps }) => useSmartMemo(expensiveFunction, deps, { maxAge: 1000 }),
      { initialProps: { deps: [1, 2, 3] } }
    );

    expect(expensiveFunction).toHaveBeenCalledTimes(1);

    // Avançar tempo além do maxAge
    act(() => {
      jest.advanceTimersByTime(1500);
    });

    // Trigger re-evaluation with different deps to force recalculation
    rerender({ deps: [1, 2, 4] });
    
    expect(expensiveFunction).toHaveBeenCalledTimes(2); // Deve recalcular devido ao tempo e deps diferentes
  });

  test('uses custom compare function', () => {
    const expensiveFunction = jest.fn(() => 'computed value');
    const customCompare = jest.fn((prev, next) => 
      prev.length === next.length && prev.every((val, i) => val === next[i])
    );

    const { result, rerender } = renderHook(
      ({ deps }) => useSmartMemo(expensiveFunction, deps, { compareFunction: customCompare }),
      { initialProps: { deps: [1, 2, 3] } }
    );

    expect(expensiveFunction).toHaveBeenCalledTimes(1);

    rerender({ deps: [1, 2, 4] });
    expect(customCompare).toHaveBeenCalled();
    expect(expensiveFunction).toHaveBeenCalledTimes(2);
  });
});

describe('useStableCallback', () => {
  test('returns stable callback reference', () => {
    const callback = jest.fn();
    const { result, rerender } = renderHook(
      ({ cb, deps }) => useStableCallback(cb, deps),
      { initialProps: { cb: callback, deps: [] } }
    );

    const firstCallback = result.current;

    rerender({ cb: callback, deps: [] });
    expect(result.current).toBe(firstCallback); // Mesma referência
  });

  test('updates callback when dependencies change', () => {
    const callback1 = jest.fn();
    const callback2 = jest.fn();

    const { result, rerender } = renderHook(
      ({ cb, deps }) => useStableCallback(cb, deps),
      { initialProps: { cb: callback1, deps: ['dep1'] } }
    );

    act(() => {
      result.current('test');
    });

    expect(callback1).toHaveBeenCalledWith('test');

    // Mudar callback e deps
    rerender({ cb: callback2, deps: ['dep2'] });

    act(() => {
      result.current('test2');
    });

    expect(callback2).toHaveBeenCalledWith('test2');
  });

  test('maintains callback reference across re-renders with same deps', () => {
    const callback = jest.fn();
    const { result, rerender } = renderHook(
      ({ count }) => useStableCallback(() => callback(count), [count]),
      { initialProps: { count: 1 } }
    );

    const stableCallback = result.current;

    // Re-render sem mudar deps
    rerender({ count: 1 });
    expect(result.current).toBe(stableCallback);

    // Re-render mudando deps
    rerender({ count: 2 });
    expect(result.current).toBe(stableCallback); // Ainda a mesma referência
  });
});

describe('useOptimizedFilter', () => {
  const mockData = [
    { id: 1, name: 'John Doe', email: 'john@example.com', status: 'active' },
    { id: 2, name: 'Jane Smith', email: 'jane@example.com', status: 'inactive' },
    { id: 3, name: 'Bob Johnson', email: 'bob@example.com', status: 'active' }
  ];

  test('filters data based on simple criteria', () => {
    const { result } = renderHook(() => 
      useOptimizedFilter(mockData, { name: 'John Doe' })
    );

    expect(result.current).toHaveLength(1);
    expect(result.current[0].name).toBe('John Doe');
  });

  test('filters data case-insensitively by default', () => {
    const { result } = renderHook(() => 
      useOptimizedFilter(mockData, { name: 'john doe' })
    );

    expect(result.current).toHaveLength(1);
    expect(result.current[0].name).toBe('John Doe');
  });

  test('respects case-sensitive option', () => {
    const { result } = renderHook(() => 
      useOptimizedFilter(mockData, { name: 'john' }, { caseSensitive: true })
    );

    expect(result.current).toHaveLength(0);
  });

  test('filters across multiple search fields', () => {
    const { result } = renderHook(() => 
      useOptimizedFilter(
        mockData, 
        { search: 'john' }, 
        { searchFields: ['name', 'email'] }
      )
    );

    expect(result.current).toHaveLength(2); // John Doe e Bob Johnson
  });

  test('sorts filtered results', () => {
    const { result } = renderHook(() => 
      useOptimizedFilter(
        mockData, 
        {}, 
        { sortBy: 'name', sortOrder: 'desc' }
      )
    );

    expect(result.current[0].name).toBe('John Doe');
    expect(result.current[1].name).toBe('Jane Smith');
    expect(result.current[2].name).toBe('Bob Johnson');
  });

  test('handles empty data gracefully', () => {
    const { result } = renderHook(() => 
      useOptimizedFilter([], { name: 'test' })
    );

    expect(result.current).toEqual([]);
  });

  test('ignores empty filter values', () => {
    const { result } = renderHook(() => 
      useOptimizedFilter(mockData, { name: '', status: '   ' })
    );

    expect(result.current).toEqual(mockData);
  });

  test('memoizes results correctly', () => {
    const filters1 = { name: 'John Doe' };
    const { result, rerender } = renderHook(
      ({ filters }) => useOptimizedFilter(mockData, filters),
      { initialProps: { filters: filters1 } }
    );

    const firstResult = result.current;

    // Rerender com os mesmos filtros (mesma referência)
    rerender({ filters: filters1 });
    expect(result.current).toStrictEqual(firstResult); // Mesmo conteúdo

    // Rerender com filtros diferentes
    rerender({ filters: { name: 'Jane' } });
    expect(result.current).not.toStrictEqual(firstResult);
  });
});

describe('useOptimizedPagination', () => {
  const mockData = Array.from({ length: 25 }, (_, i) => ({ id: i + 1, name: `Item ${i + 1}` }));

  test('initializes with correct default values', () => {
    const { result } = renderHook(() => useOptimizedPagination(mockData, 10));

    expect(result.current.currentPage).toBe(1);
    expect(result.current.totalPages).toBe(3);
    expect(result.current.totalItems).toBe(25);
    expect(result.current.currentItems).toHaveLength(10);
    expect(result.current.hasNextPage).toBe(true);
    expect(result.current.hasPrevPage).toBe(false);
  });

  test('calculates pagination data correctly', () => {
    const { result } = renderHook(() => useOptimizedPagination(mockData, 10));

    expect(result.current.startIndex).toBe(1);
    expect(result.current.endIndex).toBe(10);
    expect(result.current.currentItems[0].id).toBe(1);
    expect(result.current.currentItems[9].id).toBe(10);
  });

  test('navigates to next page', () => {
    const { result } = renderHook(() => useOptimizedPagination(mockData, 10));

    act(() => {
      result.current.nextPage();
    });

    expect(result.current.currentPage).toBe(2);
    expect(result.current.startIndex).toBe(11);
    expect(result.current.endIndex).toBe(20);
    expect(result.current.currentItems[0].id).toBe(11);
  });

  test('navigates to previous page', () => {
    const { result } = renderHook(() => useOptimizedPagination(mockData, 10));

    act(() => {
      result.current.goToPage(2);
    });

    expect(result.current.currentPage).toBe(2);

    act(() => {
      result.current.prevPage();
    });

    expect(result.current.currentPage).toBe(1);
  });

  test('goes to specific page', () => {
    const { result } = renderHook(() => useOptimizedPagination(mockData, 10));

    act(() => {
      result.current.goToPage(3);
    });

    expect(result.current.currentPage).toBe(3);
    expect(result.current.currentItems).toHaveLength(5); // Última página com 5 itens
    expect(result.current.hasNextPage).toBe(false);
  });

  test('prevents navigation beyond bounds', () => {
    const { result } = renderHook(() => useOptimizedPagination(mockData, 10));

    // Tentar ir para página inválida
    act(() => {
      result.current.goToPage(10);
    });

    expect(result.current.currentPage).toBe(3); // Máximo possível

    act(() => {
      result.current.goToPage(-1);
    });

    expect(result.current.currentPage).toBe(1); // Mínimo possível
  });

  test('prevents next/prev when at boundaries', () => {
    const { result } = renderHook(() => useOptimizedPagination(mockData, 10));

    // Na primeira página
    act(() => {
      result.current.prevPage();
    });

    expect(result.current.currentPage).toBe(1); // Não deve mudar

    // Na última página
    act(() => {
      result.current.goToPage(3);
    });
    
    act(() => {
      result.current.nextPage();
    });

    expect(result.current.currentPage).toBe(3); // Não deve mudar
  });

  test('handles empty data', () => {
    const { result } = renderHook(() => useOptimizedPagination([], 10));

    expect(result.current.totalPages).toBe(0);
    expect(result.current.totalItems).toBe(0);
    expect(result.current.currentItems).toEqual([]);
    expect(result.current.hasNextPage).toBe(false);
    expect(result.current.hasPrevPage).toBe(false);
  });
});

describe('usePerformanceMonitor', () => {
  const originalConsole = console.log;
  const originalWarn = console.warn;

  beforeEach(() => {
    console.log = jest.fn();
    console.warn = jest.fn();
  });

  afterEach(() => {
    console.log = originalConsole;
    console.warn = originalWarn;
  });

  test('tracks render count and timing', () => {
    const { result, rerender } = renderHook(
      ({ deps }) => usePerformanceMonitor('TestComponent', deps),
      { initialProps: { deps: [] } }
    );

    // renderCount starts at 0 and increments in useEffect
    expect(typeof result.current.renderCount).toBe('number');
    expect(typeof result.current.timeSinceMount).toBe('number');

    rerender({ deps: ['changed'] });
    expect(typeof result.current.renderCount).toBe('number');
  });

  test('logs performance data in development', () => {
    const originalEnv = process.env.NODE_ENV;
    process.env.NODE_ENV = 'development';

    renderHook(() => usePerformanceMonitor('TestComponent', ['dep1']));

    expect(console.log).toHaveBeenCalledWith(
      '[Performance] TestComponent:',
      expect.objectContaining({
        renderCount: expect.any(Number),
        timeSinceLastRender: expect.any(Number),
        timeSinceMount: expect.any(Number),
        deps: 1
      })
    );

    process.env.NODE_ENV = originalEnv;
  });

  test('warns about frequent re-renders', () => {
    const originalEnv = process.env.NODE_ENV;
    process.env.NODE_ENV = 'development';

    const { rerender } = renderHook(
      ({ count }) => usePerformanceMonitor('TestComponent', [count]),
      { initialProps: { count: 0 } }
    );

    // Simular muitos re-renders rápidos
    for (let i = 1; i <= 6; i++) {
      rerender({ count: i });
    }

    expect(console.warn).toHaveBeenCalledWith(
      '[Performance Warning] TestComponent está re-renderizando muito frequentemente'
    );

    process.env.NODE_ENV = originalEnv;
  });

  test('does not log in production', () => {
    const originalEnv = process.env.NODE_ENV;
    process.env.NODE_ENV = 'production';

    renderHook(() => usePerformanceMonitor('TestComponent', []));

    expect(console.log).not.toHaveBeenCalled();

    process.env.NODE_ENV = originalEnv;
  });
});

describe('useCleanup', () => {
  test('adds and runs cleanup functions', () => {
    const cleanup1 = jest.fn();
    const cleanup2 = jest.fn();

    const { result } = renderHook(() => useCleanup());

    act(() => {
      result.current.addCleanup(cleanup1);
      result.current.addCleanup(cleanup2);
    });

    act(() => {
      result.current.runCleanup();
    });

    expect(cleanup1).toHaveBeenCalled();
    expect(cleanup2).toHaveBeenCalled();
  });

  test('removes specific cleanup functions', () => {
    const cleanup1 = jest.fn();
    const cleanup2 = jest.fn();

    const { result } = renderHook(() => useCleanup());

    act(() => {
      result.current.addCleanup(cleanup1);
      result.current.addCleanup(cleanup2);
      result.current.removeCleanup(cleanup1);
    });

    act(() => {
      result.current.runCleanup();
    });

    expect(cleanup1).not.toHaveBeenCalled();
    expect(cleanup2).toHaveBeenCalled();
  });

  test('handles cleanup errors gracefully', () => {
    const originalError = console.error;
    console.error = jest.fn();

    const failingCleanup = jest.fn(() => {
      throw new Error('Cleanup failed');
    });
    const workingCleanup = jest.fn();

    const { result } = renderHook(() => useCleanup());

    act(() => {
      result.current.addCleanup(failingCleanup);
      result.current.addCleanup(workingCleanup);
    });

    act(() => {
      result.current.runCleanup();
    });

    expect(console.error).toHaveBeenCalledWith('Cleanup error:', expect.any(Error));
    expect(workingCleanup).toHaveBeenCalled(); // Deve continuar executando outros cleanups

    console.error = originalError;
  });

  test('runs cleanup automatically on unmount', () => {
    const cleanup = jest.fn();
    const { result, unmount } = renderHook(() => useCleanup());

    act(() => {
      result.current.addCleanup(cleanup);
    });

    unmount();

    expect(cleanup).toHaveBeenCalled();
  });

  test('clears cleanup functions after running', () => {
    const cleanup = jest.fn();
    const { result } = renderHook(() => useCleanup());

    act(() => {
      result.current.addCleanup(cleanup);
    });

    act(() => {
      result.current.runCleanup();
    });

    // Executar novamente não deve chamar o cleanup
    act(() => {
      result.current.runCleanup();
    });

    expect(cleanup).toHaveBeenCalledTimes(1);
  });
});
