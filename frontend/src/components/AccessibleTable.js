import React, { useState, useRef, useEffect } from 'react';
import { Table } from 'react-bootstrap';
import { useKeyboardNavigation } from '../hooks/useAccessibility';

/**
 * Componente de tabela com navegação por teclado
 * Suporta navegação com setas, Enter, Space, Home, End
 */
const AccessibleTable = ({ 
  children, 
  data = [], 
  onRowSelect, 
  onRowActivate,
  ariaLabel,
  caption,
  ...props 
}) => {
  const [focusedRowIndex, setFocusedRowIndex] = useState(-1);
  const tableRef = useRef(null);
  const rowRefs = useRef([]);

  const { selectedIndex, handleKeyDown } = useKeyboardNavigation(
    data, 
    (item, index) => {
      if (onRowActivate) {
        onRowActivate(item, index);
      }
    }
  );

  // Gerenciar foco nas linhas
  useEffect(() => {
    if (selectedIndex >= 0 && rowRefs.current[selectedIndex]) {
      rowRefs.current[selectedIndex].focus();
      setFocusedRowIndex(selectedIndex);
    }
  }, [selectedIndex]);

  // Adicionar event listeners para navegação
  useEffect(() => {
    const table = tableRef.current;
    if (!table) return;

    const handleTableKeyDown = (e) => {
      // Só processar se o foco estiver na tabela ou em suas linhas
      if (table.contains(document.activeElement)) {
        handleKeyDown(e);
      }
    };

    table.addEventListener('keydown', handleTableKeyDown);
    return () => table.removeEventListener('keydown', handleTableKeyDown);
  }, [handleKeyDown]);

  // Clonar children e adicionar props de acessibilidade
  const enhancedChildren = React.Children.map(children, (child, index) => {
    if (child.type === 'tbody') {
      return React.cloneElement(child, {
        children: React.Children.map(child.props.children, (row, rowIndex) => {
          if (row.type === 'tr') {
            return React.cloneElement(row, {
              ref: (el) => {
                rowRefs.current[rowIndex] = el;
              },
              tabIndex: rowIndex === 0 ? 0 : -1,
              role: 'row',
              'aria-rowindex': rowIndex + 2, // +2 porque header é 1
              'aria-selected': selectedIndex === rowIndex,
              className: `${row.props.className || ''} ${
                focusedRowIndex === rowIndex ? 'table-row-focused' : ''
              }`.trim(),
              onClick: (e) => {
                setFocusedRowIndex(rowIndex);
                if (onRowSelect) {
                  onRowSelect(data[rowIndex], rowIndex);
                }
                if (row.props.onClick) {
                  row.props.onClick(e);
                }
              },
              onFocus: () => {
                setFocusedRowIndex(rowIndex);
              }
            });
          }
          return row;
        })
      });
    }
    return child;
  });

  return (
    <div className="accessible-table-container">
      {caption && (
        <div className="table-caption sr-only" id={`${ariaLabel}-caption`}>
          {caption}
        </div>
      )}
      <Table
        ref={tableRef}
        role="table"
        aria-label={ariaLabel}
        aria-describedby={caption ? `${ariaLabel}-caption` : undefined}
        aria-rowcount={data.length + 1} // +1 para header
        {...props}
      >
        {enhancedChildren}
      </Table>
      <div className="sr-only" aria-live="polite" aria-atomic="true">
        {focusedRowIndex >= 0 && data[focusedRowIndex] && (
          `Linha ${focusedRowIndex + 1} de ${data.length} selecionada`
        )}
      </div>
    </div>
  );
};

export default AccessibleTable;
