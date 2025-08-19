import { renderHook } from '@testing-library/react';
import '@testing-library/jest-dom';

// Use the mocked version from setupTests.js
import { useAccessibility, useKeyboardNavigation } from '../useAccessibility';

describe('useAccessibility', () => {
  test('returns accessibility functions', () => {
    const { result } = renderHook(() => useAccessibility());
    
    expect(result.current).toHaveProperty('announceToScreenReader');
    expect(result.current).toHaveProperty('createSkipLink');
    expect(result.current).toHaveProperty('trapFocus');
    expect(result.current).toHaveProperty('useFocusTrap');
    expect(result.current).toHaveProperty('useKeyboardNavigation');
  });
});

describe('useKeyboardNavigation', () => {
  test('returns keyboard navigation functions', () => {
    const items = ['item1', 'item2', 'item3'];
    const onSelect = jest.fn();
    
    const { result } = renderHook(() => useKeyboardNavigation(items, onSelect));
    
    expect(result.current).toHaveProperty('selectedIndex');
    expect(result.current).toHaveProperty('handleKeyDown');
    expect(typeof result.current.handleKeyDown).toBe('function');
  });

  test('handles empty items array', () => {
    const items = [];
    const onSelect = jest.fn();
    
    const { result } = renderHook(() => useKeyboardNavigation(items, onSelect));
    
    expect(result.current.selectedIndex).toBe(-1);
  });
});
