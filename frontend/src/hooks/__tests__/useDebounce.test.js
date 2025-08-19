import { renderHook, act, waitFor } from '@testing-library/react';
import { useDebounce, useDebouncedCallback, useDebouncedSearch, useThrottle } from '../useDebounce';

// Mock timers
jest.useFakeTimers();

describe('useDebounce', () => {
  afterEach(() => {
    jest.clearAllTimers();
  });

  test('returns initial value immediately', () => {
    const { result } = renderHook(() => useDebounce('initial', 300));
    expect(result.current).toBe('initial');
  });

  test('debounces value changes', async () => {
    const { result, rerender } = renderHook(
      ({ value, delay }) => useDebounce(value, delay),
      { initialProps: { value: 'initial', delay: 300 } }
    );

    expect(result.current).toBe('initial');

    // Mudança rápida de valores
    rerender({ value: 'first', delay: 300 });
    expect(result.current).toBe('initial'); // Ainda não mudou

    rerender({ value: 'second', delay: 300 });
    expect(result.current).toBe('initial'); // Ainda não mudou

    rerender({ value: 'final', delay: 300 });
    expect(result.current).toBe('initial'); // Ainda não mudou

    // Avançar o tempo
    act(() => {
      jest.advanceTimersByTime(300);
    });

    expect(result.current).toBe('final');
  });

  test('resets timer on value change', async () => {
    const { result, rerender } = renderHook(
      ({ value }) => useDebounce(value, 300),
      { initialProps: { value: 'initial' } }
    );

    rerender({ value: 'changed' });

    // Avançar parcialmente
    act(() => {
      jest.advanceTimersByTime(200);
    });

    expect(result.current).toBe('initial');

    // Mudar novamente antes do timer completar
    rerender({ value: 'changed-again' });

    // Avançar mais 200ms (total 400ms desde a primeira mudança)
    act(() => {
      jest.advanceTimersByTime(200);
    });

    expect(result.current).toBe('initial'); // Ainda não mudou

    // Avançar mais 100ms para completar os 300ms da última mudança
    act(() => {
      jest.advanceTimersByTime(100);
    });

    expect(result.current).toBe('changed-again');
  });

  test('uses custom delay', async () => {
    const { result, rerender } = renderHook(
      ({ value, delay }) => useDebounce(value, delay),
      { initialProps: { value: 'initial', delay: 500 } }
    );

    rerender({ value: 'changed', delay: 500 });

    act(() => {
      jest.advanceTimersByTime(300);
    });

    expect(result.current).toBe('initial'); // Ainda não mudou

    act(() => {
      jest.advanceTimersByTime(200);
    });

    expect(result.current).toBe('changed');
  });
});

describe('useDebouncedCallback', () => {
  afterEach(() => {
    jest.clearAllTimers();
  });

  test('debounces callback execution', () => {
    const callback = jest.fn();
    const { result } = renderHook(() => useDebouncedCallback(callback, 300));

    // Múltiplas chamadas rápidas
    act(() => {
      result.current.debouncedCallback('arg1');
      result.current.debouncedCallback('arg2');
      result.current.debouncedCallback('arg3');
    });

    expect(callback).not.toHaveBeenCalled();

    // Avançar o tempo
    act(() => {
      jest.advanceTimersByTime(300);
    });

    expect(callback).toHaveBeenCalledTimes(1);
    expect(callback).toHaveBeenCalledWith('arg3');
  });

  test('cancel function stops pending execution', () => {
    const callback = jest.fn();
    const { result } = renderHook(() => useDebouncedCallback(callback, 300));

    act(() => {
      result.current.debouncedCallback('test');
    });

    act(() => {
      result.current.cancel();
    });

    act(() => {
      jest.advanceTimersByTime(300);
    });

    expect(callback).not.toHaveBeenCalled();
  });

  test('flush function executes immediately', () => {
    const callback = jest.fn();
    const { result } = renderHook(() => useDebouncedCallback(callback, 300));

    act(() => {
      result.current.debouncedCallback('pending');
    });

    act(() => {
      result.current.flush('immediate');
    });

    expect(callback).toHaveBeenCalledWith('immediate');

    // Timer pendente deve ter sido cancelado
    act(() => {
      jest.advanceTimersByTime(300);
    });

    expect(callback).toHaveBeenCalledTimes(1);
  });

  test('updates callback when dependencies change', () => {
    const callback1 = jest.fn();
    const callback2 = jest.fn();

    const { result, rerender } = renderHook(
      ({ callback, deps }) => useDebouncedCallback(callback, 300, deps),
      { initialProps: { callback: callback1, deps: ['dep1'] } }
    );

    act(() => {
      result.current.debouncedCallback('test');
    });

    // Mudar callback e deps
    rerender({ callback: callback2, deps: ['dep2'] });

    act(() => {
      jest.advanceTimersByTime(300);
    });

    expect(callback2).toHaveBeenCalledWith('test');
    expect(callback1).not.toHaveBeenCalled();
  });

  test('cleans up timeout on unmount', () => {
    const callback = jest.fn();
    const { result, unmount } = renderHook(() => useDebouncedCallback(callback, 300));

    act(() => {
      result.current.debouncedCallback('test');
    });

    unmount();

    act(() => {
      jest.advanceTimersByTime(300);
    });

    expect(callback).not.toHaveBeenCalled();
  });
});

describe('useDebouncedSearch', () => {
  afterEach(() => {
    jest.clearAllTimers();
  });

  test('manages search state correctly', () => {
    const searchFunction = jest.fn().mockResolvedValue();
    const { result } = renderHook(() => useDebouncedSearch(searchFunction, 500));

    expect(result.current.searchTerm).toBe('');
    expect(result.current.isSearching).toBe(false);
  });

  test('debounces search function calls', async () => {
    const searchFunction = jest.fn().mockResolvedValue();
    const { result } = renderHook(() => useDebouncedSearch(searchFunction, 500));

    act(() => {
      result.current.handleSearchChange('test');
    });

    expect(result.current.searchTerm).toBe('test');
    expect(result.current.isSearching).toBe(true);
    expect(searchFunction).not.toHaveBeenCalled();

    act(() => {
      jest.advanceTimersByTime(500);
    });

    await waitFor(() => {
      expect(searchFunction).toHaveBeenCalledWith('test');
    });
  });

  test('does not search for empty terms', () => {
    const searchFunction = jest.fn().mockResolvedValue();
    const { result } = renderHook(() => useDebouncedSearch(searchFunction, 500));

    act(() => {
      result.current.handleSearchChange('   '); // Apenas espaços
    });

    expect(result.current.isSearching).toBe(false);

    act(() => {
      jest.advanceTimersByTime(500);
    });

    expect(searchFunction).not.toHaveBeenCalled();
  });

  test('handles search errors gracefully', async () => {
    const searchFunction = jest.fn().mockRejectedValue(new Error('Search failed'));
    const consoleSpy = jest.spyOn(console, 'error').mockImplementation();
    
    const { result } = renderHook(() => useDebouncedSearch(searchFunction, 500));

    act(() => {
      result.current.handleSearchChange('test');
    });

    act(() => {
      jest.advanceTimersByTime(500);
    });

    await waitFor(() => {
      expect(result.current.isSearching).toBe(false);
    });

    expect(consoleSpy).toHaveBeenCalledWith('Search error:', expect.any(Error));
    consoleSpy.mockRestore();
  });

  test('clearSearch resets state', () => {
    const searchFunction = jest.fn().mockResolvedValue();
    const { result } = renderHook(() => useDebouncedSearch(searchFunction, 500));

    act(() => {
      result.current.handleSearchChange('test');
    });

    expect(result.current.searchTerm).toBe('test');

    act(() => {
      result.current.clearSearch();
    });

    expect(result.current.searchTerm).toBe('');
    expect(result.current.isSearching).toBe(false);
  });

  test('cleans up on unmount', () => {
    const searchFunction = jest.fn().mockResolvedValue();
    const { result, unmount } = renderHook(() => useDebouncedSearch(searchFunction, 500));

    act(() => {
      result.current.handleSearchChange('test');
    });

    unmount();

    act(() => {
      jest.advanceTimersByTime(500);
    });

    expect(searchFunction).not.toHaveBeenCalled();
  });
});

describe('useThrottle', () => {
  afterEach(() => {
    jest.clearAllTimers();
  });

  test('throttles callback execution', () => {
    const callback = jest.fn();
    const { result } = renderHook(() => useThrottle(callback, 100));

    // Primeira chamada deve executar imediatamente
    act(() => {
      result.current('first');
    });

    // Aguardar um tick para garantir que a primeira chamada foi processada
    expect(callback).toHaveBeenCalledTimes(1);
    expect(callback).toHaveBeenCalledWith('first');

    // Chamadas subsequentes dentro do delay devem ser throttled
    act(() => {
      result.current('second');
      result.current('third');
    });

    expect(callback).toHaveBeenCalledTimes(1); // Ainda apenas a primeira

    // Avançar o tempo
    act(() => {
      jest.advanceTimersByTime(150);
    });

    expect(callback).toHaveBeenCalledTimes(2);
    expect(callback).toHaveBeenLastCalledWith('third');
  });

  test('allows execution after delay period', () => {
    const callback = jest.fn();
    const { result } = renderHook(() => useThrottle(callback, 100));

    act(() => {
      result.current('first');
    });

    expect(callback).toHaveBeenCalledTimes(1);

    // Avançar o tempo completamente
    act(() => {
      jest.advanceTimersByTime(100);
    });

    // Nova chamada deve executar imediatamente
    act(() => {
      result.current('second');
    });

    expect(callback).toHaveBeenCalledTimes(2);
    expect(callback).toHaveBeenLastCalledWith('second');
  });

  test('schedules final execution if called during throttle period', () => {
    const callback = jest.fn();
    const { result } = renderHook(() => useThrottle(callback, 100));

    act(() => {
      result.current('first');
    });

    // Chamada durante período de throttle
    act(() => {
      jest.advanceTimersByTime(50);
      result.current('second');
    });

    expect(callback).toHaveBeenCalledTimes(1);

    // Avançar o restante do tempo
    act(() => {
      jest.advanceTimersByTime(50);
    });

    expect(callback).toHaveBeenCalledTimes(2);
    expect(callback).toHaveBeenLastCalledWith('second');
  });

  test('cleans up timeout on unmount', () => {
    const callback = jest.fn();
    const { result, unmount } = renderHook(() => useThrottle(callback, 100));

    act(() => {
      result.current('first');
      result.current('second'); // Esta deve ser agendada
    });

    unmount();

    act(() => {
      jest.advanceTimersByTime(100);
    });

    expect(callback).toHaveBeenCalledTimes(1); // Apenas a primeira
  });
});
