import { renderHook, act, cleanup } from '@testing-library/react';
import '@testing-library/jest-dom';

// Unmock the hook for this test file
jest.unmock('../useAccessibility');
const { useAccessibility, useKeyboardNavigation } = require('../useAccessibility');

// Mock DOM methods
const mockFocus = jest.fn();
const mockScrollIntoView = jest.fn();
const mockAddEventListener = jest.fn();
const mockRemoveEventListener = jest.fn();

// Create a container for React 18's createRoot
let container = null;

// Setup DOM mocks
beforeEach(() => {
  // Create a proper DOM environment
  container = document.createElement('div');
  container.id = 'root';
  document.body.appendChild(container);
  
  // Mock document methods
  const originalCreateElement = document.createElement;
  document.createElement = jest.fn((tagName) => {
    const element = originalCreateElement.call(document, tagName);
    element.focus = mockFocus;
    element.scrollIntoView = mockScrollIntoView;
    element.addEventListener = mockAddEventListener;
    element.removeEventListener = mockRemoveEventListener;
    element.querySelectorAll = jest.fn();
    return element;
  });

  // Mock getElementById to return our container for 'root'
  document.getElementById = jest.fn((id) => {
    if (id === 'root') return container;
    return null;
  });

  // Clear all mocks before each test
  jest.clearAllMocks();
});

afterEach(() => {
  // Clean up the DOM
  if (container && container.parentNode === document.body) {
    document.body.removeChild(container);
  }
  container = null;
  cleanup();
});

describe('useAccessibility', () => {
  describe('trapFocus', () => {
    test('sets up focus trap for modal elements', () => {
      const { result } = renderHook(() => useAccessibility());

      const mockElement = {
        querySelectorAll: jest.fn(() => [
          { focus: jest.fn() },
          { focus: jest.fn() },
          { focus: jest.fn() }
        ]),
        addEventListener: jest.fn(),
        removeEventListener: jest.fn()
      };

      act(() => {
        const cleanup = result.current.trapFocus(mockElement);
        expect(typeof cleanup).toBe('function');
      });

      expect(mockElement.querySelectorAll).toHaveBeenCalledWith(
        'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
      );
      expect(mockElement.addEventListener).toHaveBeenCalledWith('keydown', expect.any(Function));
    });

    test('handles tab navigation within trapped focus', () => {
      const { result } = renderHook(() => useAccessibility());

      const firstElement = { focus: jest.fn() };
      const lastElement = { focus: jest.fn() };
      const mockElement = {
        querySelectorAll: jest.fn(() => [firstElement, lastElement]),
        addEventListener: jest.fn(),
        removeEventListener: jest.fn()
      };

      let tabHandler;
      mockElement.addEventListener.mockImplementation((event, handler) => {
        if (event === 'keydown') tabHandler = handler;
      });

      act(() => {
        result.current.trapFocus(mockElement);
      });

      // Mock document.activeElement
      Object.defineProperty(document, 'activeElement', {
        value: lastElement,
        writable: true
      });

      // Simulate Tab key press
      const tabEvent = {
        key: 'Tab',
        shiftKey: false,
        preventDefault: jest.fn()
      };

      act(() => {
        tabHandler(tabEvent);
      });

      expect(firstElement.focus).toHaveBeenCalled();
      expect(tabEvent.preventDefault).toHaveBeenCalled();
    });

    test('handles shift+tab navigation within trapped focus', () => {
      const { result } = renderHook(() => useAccessibility());

      const firstElement = { focus: jest.fn() };
      const lastElement = { focus: jest.fn() };
      const mockElement = {
        querySelectorAll: jest.fn(() => [firstElement, lastElement]),
        addEventListener: jest.fn(),
        removeEventListener: jest.fn()
      };

      let tabHandler;
      mockElement.addEventListener.mockImplementation((event, handler) => {
        if (event === 'keydown') tabHandler = handler;
      });

      act(() => {
        result.current.trapFocus(mockElement);
      });

      // Mock document.activeElement
      Object.defineProperty(document, 'activeElement', {
        value: firstElement,
        writable: true
      });

      // Simulate Shift+Tab key press
      const shiftTabEvent = {
        key: 'Tab',
        shiftKey: true,
        preventDefault: jest.fn()
      };

      act(() => {
        tabHandler(shiftTabEvent);
      });

      expect(lastElement.focus).toHaveBeenCalled();
      expect(shiftTabEvent.preventDefault).toHaveBeenCalled();
    });

    test('ignores non-tab keys', () => {
      const { result } = renderHook(() => useAccessibility());

      const mockElement = {
        querySelectorAll: jest.fn(() => [{ focus: jest.fn() }]),
        addEventListener: jest.fn(),
        removeEventListener: jest.fn()
      };

      let keyHandler;
      mockElement.addEventListener.mockImplementation((event, handler) => {
        if (event === 'keydown') keyHandler = handler;
      });

      act(() => {
        result.current.trapFocus(mockElement);
      });

      const enterEvent = {
        key: 'Enter',
        preventDefault: jest.fn()
      };

      act(() => {
        keyHandler(enterEvent);
      });

      expect(enterEvent.preventDefault).not.toHaveBeenCalled();
    });

    test('returns cleanup function that removes event listener', () => {
      const { result } = renderHook(() => useAccessibility());

      const mockElement = {
        querySelectorAll: jest.fn(() => [{ focus: jest.fn() }]),
        addEventListener: jest.fn(),
        removeEventListener: jest.fn()
      };

      let cleanup;
      act(() => {
        cleanup = result.current.trapFocus(mockElement);
      });

      act(() => {
        cleanup();
      });

      expect(mockElement.removeEventListener).toHaveBeenCalledWith('keydown', expect.any(Function));
    });
  });

  describe('useFocusTrap', () => {
    test('activates focus trap when modal is open', () => {
      const { result } = renderHook(() => useAccessibility());

      const mockRef = {
        current: {
          querySelectorAll: jest.fn(() => [{ focus: jest.fn() }]),
          addEventListener: jest.fn(),
          removeEventListener: jest.fn()
        }
      };

      let focusTrapRef;
      act(() => {
        focusTrapRef = result.current.useFocusTrap(true);
        focusTrapRef.current = mockRef.current;
      });

      expect(mockRef.current.addEventListener).toHaveBeenCalled();
    });

    test('does not activate focus trap when modal is closed', () => {
      const { result } = renderHook(() => useAccessibility());

      const mockRef = {
        current: {
          querySelectorAll: jest.fn(() => [{ focus: jest.fn() }]),
          addEventListener: jest.fn(),
          removeEventListener: jest.fn()
        }
      };

      let focusTrapRef;
      act(() => {
        focusTrapRef = result.current.useFocusTrap(false);
        focusTrapRef.current = mockRef.current;
      });

      expect(mockRef.current.addEventListener).not.toHaveBeenCalled();
    });
  });

  describe('announceToScreenReader', () => {
    test('creates announcement element with correct attributes', () => {
      const { result } = renderHook(() => useAccessibility());

      const mockElement = {
        setAttribute: jest.fn(),
        textContent: ''
      };

      document.createElement.mockReturnValue(mockElement);

      act(() => {
        result.current.announceToScreenReader('Test announcement', 'assertive');
      });

      expect(document.createElement).toHaveBeenCalledWith('div');
      expect(mockElement.setAttribute).toHaveBeenCalledWith('aria-live', 'assertive');
      expect(mockElement.setAttribute).toHaveBeenCalledWith('aria-atomic', 'true');
      expect(mockElement.className).toBe('sr-only');
      expect(mockElement.textContent).toBe('Test announcement');
      expect(document.body.appendChild).toHaveBeenCalledWith(mockElement);
    });

    test('uses default priority when not specified', () => {
      const { result } = renderHook(() => useAccessibility());

      const mockElement = {
        setAttribute: jest.fn(),
        textContent: ''
      };

      document.createElement.mockReturnValue(mockElement);

      act(() => {
        result.current.announceToScreenReader('Test announcement');
      });

      expect(mockElement.setAttribute).toHaveBeenCalledWith('aria-live', 'polite');
    });

    test('removes announcement element after timeout', () => {
      jest.useFakeTimers();

      const { result } = renderHook(() => useAccessibility());

      const mockElement = {
        setAttribute: jest.fn(),
        textContent: ''
      };

      document.createElement.mockReturnValue(mockElement);

      act(() => {
        result.current.announceToScreenReader('Test announcement');
      });

      act(() => {
        jest.advanceTimersByTime(1000);
      });

      expect(document.body.removeChild).toHaveBeenCalledWith(mockElement);

      jest.useRealTimers();
    });
  });

  describe('createSkipLink', () => {
    test('creates skip link with correct properties', () => {
      const { result } = renderHook(() => useAccessibility());

      const skipLink = result.current.createSkipLink('main-content', 'Skip to main content');

      expect(skipLink.href).toBe('#main-content');
      expect(skipLink.className).toBe('skip-link');
      expect(skipLink.children).toBe('Skip to main content');
      expect(typeof skipLink.onClick).toBe('function');
    });

    test('skip link onClick focuses target element', () => {
      const { result } = renderHook(() => useAccessibility());

      const mockTarget = {
        focus: jest.fn(),
        scrollIntoView: jest.fn()
      };

      document.getElementById.mockReturnValue(mockTarget);

      const skipLink = result.current.createSkipLink('main-content', 'Skip to main content');

      const mockEvent = {
        preventDefault: jest.fn()
      };

      act(() => {
        skipLink.onClick(mockEvent);
      });

      expect(mockEvent.preventDefault).toHaveBeenCalled();
      expect(document.getElementById).toHaveBeenCalledWith('main-content');
      expect(mockTarget.focus).toHaveBeenCalled();
      expect(mockTarget.scrollIntoView).toHaveBeenCalledWith({ behavior: 'smooth' });
    });

    test('skip link handles missing target gracefully', () => {
      const { result } = renderHook(() => useAccessibility());

      document.getElementById.mockReturnValue(null);

      const skipLink = result.current.createSkipLink('missing-element', 'Skip to missing');

      const mockEvent = {
        preventDefault: jest.fn()
      };

      act(() => {
        skipLink.onClick(mockEvent);
      });

      expect(mockEvent.preventDefault).toHaveBeenCalled();
      // Should not throw error when target is missing
    });
  });
});

describe('useKeyboardNavigation', () => {
  const mockItems = [
    { id: 1, name: 'Item 1' },
    { id: 2, name: 'Item 2' },
    { id: 3, name: 'Item 3' }
  ];

  test('initializes with no selection', () => {
    const { result } = renderHook(() => useKeyboardNavigation(mockItems, jest.fn()));

    expect(result.current.selectedIndex).toBe(-1);
  });

  test('handles arrow down navigation', () => {
    const { result } = renderHook(() => useKeyboardNavigation(mockItems, jest.fn()));

    const event = {
      key: 'ArrowDown',
      preventDefault: jest.fn()
    };

    act(() => {
      result.current.handleKeyDown(event);
    });

    expect(result.current.selectedIndex).toBe(0);
    expect(event.preventDefault).toHaveBeenCalled();
  });

  test('handles arrow up navigation', () => {
    const { result } = renderHook(() => useKeyboardNavigation(mockItems, jest.fn()));

    // Set initial selection
    act(() => {
      result.current.setSelectedIndex(1);
    });

    const event = {
      key: 'ArrowUp',
      preventDefault: jest.fn()
    };

    act(() => {
      result.current.handleKeyDown(event);
    });

    expect(result.current.selectedIndex).toBe(0);
    expect(event.preventDefault).toHaveBeenCalled();
  });

  test('wraps around at boundaries', () => {
    const { result } = renderHook(() => useKeyboardNavigation(mockItems, jest.fn()));

    // Test wrap around at end
    act(() => {
      result.current.setSelectedIndex(2); // Last item
    });

    const downEvent = {
      key: 'ArrowDown',
      preventDefault: jest.fn()
    };

    act(() => {
      result.current.handleKeyDown(downEvent);
    });

    expect(result.current.selectedIndex).toBe(0); // Wrapped to first

    // Test wrap around at beginning
    const upEvent = {
      key: 'ArrowUp',
      preventDefault: jest.fn()
    };

    act(() => {
      result.current.handleKeyDown(upEvent);
    });

    expect(result.current.selectedIndex).toBe(2); // Wrapped to last
  });

  test('handles Enter key selection', () => {
    const onSelect = jest.fn();
    const { result } = renderHook(() => useKeyboardNavigation(mockItems, onSelect));

    act(() => {
      result.current.setSelectedIndex(1);
    });

    const event = {
      key: 'Enter',
      preventDefault: jest.fn()
    };

    act(() => {
      result.current.handleKeyDown(event);
    });

    expect(onSelect).toHaveBeenCalledWith(mockItems[1], 1);
    expect(event.preventDefault).toHaveBeenCalled();
  });

  test('handles Space key selection', () => {
    const onSelect = jest.fn();
    const { result } = renderHook(() => useKeyboardNavigation(mockItems, onSelect));

    act(() => {
      result.current.setSelectedIndex(0);
    });

    const event = {
      key: ' ',
      preventDefault: jest.fn()
    };

    act(() => {
      result.current.handleKeyDown(event);
    });

    expect(onSelect).toHaveBeenCalledWith(mockItems[0], 0);
    expect(event.preventDefault).toHaveBeenCalled();
  });

  test('handles Escape key to clear selection', () => {
    const { result } = renderHook(() => useKeyboardNavigation(mockItems, jest.fn()));

    act(() => {
      result.current.setSelectedIndex(1);
    });

    const event = {
      key: 'Escape',
      preventDefault: jest.fn()
    };

    act(() => {
      result.current.handleKeyDown(event);
    });

    expect(result.current.selectedIndex).toBe(-1);
  });

  test('handles Home key to go to first item', () => {
    const { result } = renderHook(() => useKeyboardNavigation(mockItems, jest.fn()));

    act(() => {
      result.current.setSelectedIndex(2);
    });

    const event = {
      key: 'Home',
      preventDefault: jest.fn()
    };

    act(() => {
      result.current.handleKeyDown(event);
    });

    expect(result.current.selectedIndex).toBe(0);
    expect(event.preventDefault).toHaveBeenCalled();
  });

  test('handles End key to go to last item', () => {
    const { result } = renderHook(() => useKeyboardNavigation(mockItems, jest.fn()));

    const event = {
      key: 'End',
      preventDefault: jest.fn()
    };

    act(() => {
      result.current.handleKeyDown(event);
    });

    expect(result.current.selectedIndex).toBe(2);
    expect(event.preventDefault).toHaveBeenCalled();
  });

  test('does not call onSelect when no item is selected', () => {
    const onSelect = jest.fn();
    const { result } = renderHook(() => useKeyboardNavigation(mockItems, onSelect));

    const event = {
      key: 'Enter',
      preventDefault: jest.fn()
    };

    act(() => {
      result.current.handleKeyDown(event);
    });

    expect(onSelect).not.toHaveBeenCalled();
  });

  test('ignores unknown keys', () => {
    const { result } = renderHook(() => useKeyboardNavigation(mockItems, jest.fn()));

    const initialIndex = result.current.selectedIndex;

    const event = {
      key: 'a',
      preventDefault: jest.fn()
    };

    act(() => {
      result.current.handleKeyDown(event);
    });

    expect(result.current.selectedIndex).toBe(initialIndex);
    expect(event.preventDefault).not.toHaveBeenCalled();
  });

  test('handles empty items array', () => {
    const { result } = renderHook(() => useKeyboardNavigation([], jest.fn()));

    const event = {
      key: 'ArrowDown',
      preventDefault: jest.fn()
    };

    act(() => {
      result.current.handleKeyDown(event);
    });

    expect(result.current.selectedIndex).toBe(0); // Would wrap to 0 with empty array
  });
});
