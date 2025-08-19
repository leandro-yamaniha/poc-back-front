import { useEffect, useRef, useCallback, useState } from 'react';

/**
 * Hook personalizado para melhorias de acessibilidade
 * Gerencia foco, navegação por teclado e ARIA
 */
export const useAccessibility = () => {
  const trapFocusRef = useRef(null);

  // Trap focus dentro de um elemento (útil para modais)
  const trapFocus = useCallback((element) => {
    if (!element) return;

    const focusableElements = element.querySelectorAll(
      'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
    );
    
    const firstElement = focusableElements[0];
    const lastElement = focusableElements[focusableElements.length - 1];

    const handleTabKey = (e) => {
      if (e.key !== 'Tab') return;

      if (e.shiftKey) {
        if (document.activeElement === firstElement) {
          lastElement.focus();
          e.preventDefault();
        }
      } else {
        if (document.activeElement === lastElement) {
          firstElement.focus();
          e.preventDefault();
        }
      }
    };

    element.addEventListener('keydown', handleTabKey);
    firstElement?.focus();

    return () => {
      element.removeEventListener('keydown', handleTabKey);
    };
  }, []);

  // Gerenciar foco em modais
  const useFocusTrap = (isOpen) => {
    useEffect(() => {
      if (isOpen && trapFocusRef.current) {
        const cleanup = trapFocus(trapFocusRef.current);
        return cleanup;
      }
    }, [isOpen, trapFocus]);

    return trapFocusRef;
  };

  // Navegação por teclado em listas/tabelas
  const useKeyboardNavigation = (items, onSelect) => {
    const [selectedIndex, setSelectedIndex] = useState(-1);

    const handleKeyDown = useCallback((e) => {
      switch (e.key) {
        case 'ArrowDown':
          e.preventDefault();
          setSelectedIndex(prev => 
            prev < items.length - 1 ? prev + 1 : 0
          );
          break;
        case 'ArrowUp':
          e.preventDefault();
          setSelectedIndex(prev => 
            prev > 0 ? prev - 1 : items.length - 1
          );
          break;
        case 'Enter':
        case ' ':
          e.preventDefault();
          if (selectedIndex >= 0 && onSelect) {
            onSelect(items[selectedIndex], selectedIndex);
          }
          break;
        case 'Escape':
          setSelectedIndex(-1);
          break;
      }
    }, [items, selectedIndex, onSelect]);

    return {
      selectedIndex,
      setSelectedIndex,
      handleKeyDown
    };
  };

  // Anunciar mudanças para leitores de tela
  const announceToScreenReader = useCallback((message, priority = 'polite') => {
    const announcement = document.createElement('div');
    announcement.setAttribute('aria-live', priority);
    announcement.setAttribute('aria-atomic', 'true');
    announcement.className = 'sr-only';
    announcement.textContent = message;
    
    document.body.appendChild(announcement);
    
    setTimeout(() => {
      document.body.removeChild(announcement);
    }, 1000);
  }, []);

  // Skip links para navegação rápida
  const createSkipLink = useCallback((targetId, text) => {
    return {
      href: `#${targetId}`,
      className: 'skip-link',
      onClick: (e) => {
        e.preventDefault();
        const target = document.getElementById(targetId);
        if (target) {
          target.focus();
          target.scrollIntoView({ behavior: 'smooth' });
        }
      },
      children: text
    };
  }, []);

  return {
    useFocusTrap,
    useKeyboardNavigation,
    announceToScreenReader,
    createSkipLink,
    trapFocus
  };
};

// Hook específico para gerenciar estado de navegação por teclado
export const useKeyboardNavigation = (items, onSelect) => {
  const [selectedIndex, setSelectedIndex] = useState(-1);

  const handleKeyDown = useCallback((e) => {
    switch (e.key) {
      case 'ArrowDown':
        e.preventDefault();
        setSelectedIndex(prev => 
          prev < items.length - 1 ? prev + 1 : 0
        );
        break;
      case 'ArrowUp':
        e.preventDefault();
        setSelectedIndex(prev => 
          prev > 0 ? prev - 1 : items.length - 1
        );
        break;
      case 'Enter':
      case ' ':
        e.preventDefault();
        if (selectedIndex >= 0 && onSelect) {
          onSelect(items[selectedIndex], selectedIndex);
        }
        break;
      case 'Escape':
        setSelectedIndex(-1);
        break;
      case 'Home':
        e.preventDefault();
        setSelectedIndex(0);
        break;
      case 'End':
        e.preventDefault();
        setSelectedIndex(items.length - 1);
        break;
    }
  }, [items, selectedIndex, onSelect]);

  return {
    selectedIndex,
    setSelectedIndex,
    handleKeyDown
  };
};

export default useAccessibility;
