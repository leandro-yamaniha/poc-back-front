import React, { useMemo, useState, useCallback, useRef, useEffect } from 'react';
import { Table } from 'react-bootstrap';

/**
 * Componente de tabela virtualizada para listas grandes
 * Renderiza apenas os itens visíveis para otimizar performance
 */
const VirtualizedTable = React.memo(({
  data = [],
  columns = [],
  rowHeight = 50,
  containerHeight = 400,
  overscan = 5,
  onRowClick = null,
  className = '',
  ...props
}) => {
  const [scrollTop, setScrollTop] = useState(0);
  const containerRef = useRef(null);

  // Calcular itens visíveis
  const visibleRange = useMemo(() => {
    const startIndex = Math.max(0, Math.floor(scrollTop / rowHeight) - overscan);
    const endIndex = Math.min(
      data.length - 1,
      Math.ceil((scrollTop + containerHeight) / rowHeight) + overscan
    );
    
    return { startIndex, endIndex };
  }, [scrollTop, rowHeight, containerHeight, overscan, data.length]);

  // Itens visíveis
  const visibleItems = useMemo(() => {
    return data.slice(visibleRange.startIndex, visibleRange.endIndex + 1);
  }, [data, visibleRange]);

  // Handler de scroll otimizado
  const handleScroll = useCallback((e) => {
    setScrollTop(e.target.scrollTop);
  }, []);

  // Altura total da tabela
  const totalHeight = data.length * rowHeight;

  // Offset do primeiro item visível
  const offsetY = visibleRange.startIndex * rowHeight;

  return (
    <div
      ref={containerRef}
      className={`virtualized-table-container ${className}`}
      style={{
        height: containerHeight,
        overflow: 'auto',
        position: 'relative'
      }}
      onScroll={handleScroll}
      {...props}
    >
      <div style={{ height: totalHeight, position: 'relative' }}>
        <Table
          hover
          style={{
            position: 'absolute',
            top: offsetY,
            width: '100%'
          }}
        >
          <thead
            style={{
              position: 'sticky',
              top: -offsetY,
              backgroundColor: 'white',
              zIndex: 1
            }}
          >
            <tr>
              {columns.map((column, index) => (
                <th key={column.key || index} style={{ width: column.width }}>
                  {column.header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {visibleItems.map((item, index) => {
              const actualIndex = visibleRange.startIndex + index;
              return (
                <tr
                  key={item.id || actualIndex}
                  onClick={onRowClick ? () => onRowClick(item, actualIndex) : undefined}
                  style={{
                    height: rowHeight,
                    cursor: onRowClick ? 'pointer' : 'default'
                  }}
                >
                  {columns.map((column, colIndex) => (
                    <td key={column.key || colIndex}>
                      {column.render 
                        ? column.render(item[column.key], item, actualIndex)
                        : item[column.key]
                      }
                    </td>
                  ))}
                </tr>
              );
            })}
          </tbody>
        </Table>
      </div>
      
      {/* Indicador de scroll */}
      {data.length > 0 && (
        <div
          className="position-absolute bottom-0 end-0 bg-light px-2 py-1 rounded"
          style={{ fontSize: '0.8rem', opacity: 0.8 }}
        >
          {visibleRange.startIndex + 1}-{Math.min(visibleRange.endIndex + 1, data.length)} de {data.length}
        </div>
      )}
    </div>
  );
});

/**
 * Hook para configuração de colunas da tabela virtualizada
 */
export const useVirtualizedColumns = (baseColumns) => {
  return useMemo(() => {
    return baseColumns.map(column => ({
      ...column,
      width: column.width || 'auto'
    }));
  }, [baseColumns]);
};

/**
 * Componente de lista virtualizada simples
 */
export const VirtualizedList = React.memo(({
  items = [],
  itemHeight = 40,
  containerHeight = 300,
  renderItem,
  overscan = 3,
  className = ''
}) => {
  const [scrollTop, setScrollTop] = useState(0);

  const visibleRange = useMemo(() => {
    const startIndex = Math.max(0, Math.floor(scrollTop / itemHeight) - overscan);
    const endIndex = Math.min(
      items.length - 1,
      Math.ceil((scrollTop + containerHeight) / itemHeight) + overscan
    );
    
    return { startIndex, endIndex };
  }, [scrollTop, itemHeight, containerHeight, overscan, items.length]);

  const visibleItems = useMemo(() => {
    return items.slice(visibleRange.startIndex, visibleRange.endIndex + 1);
  }, [items, visibleRange]);

  const handleScroll = useCallback((e) => {
    setScrollTop(e.target.scrollTop);
  }, []);

  const totalHeight = items.length * itemHeight;
  const offsetY = visibleRange.startIndex * itemHeight;

  return (
    <div
      className={`virtualized-list-container ${className}`}
      style={{
        height: containerHeight,
        overflow: 'auto',
        position: 'relative'
      }}
      onScroll={handleScroll}
    >
      <div style={{ height: totalHeight, position: 'relative' }}>
        <div
          style={{
            position: 'absolute',
            top: offsetY,
            width: '100%'
          }}
        >
          {visibleItems.map((item, index) => {
            const actualIndex = visibleRange.startIndex + index;
            return (
              <div
                key={item.id || actualIndex}
                style={{ height: itemHeight }}
              >
                {renderItem(item, actualIndex)}
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
});

VirtualizedTable.displayName = 'VirtualizedTable';
VirtualizedList.displayName = 'VirtualizedList';

export default VirtualizedTable;
