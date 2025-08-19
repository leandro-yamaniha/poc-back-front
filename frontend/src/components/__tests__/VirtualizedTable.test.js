import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { renderHook } from '@testing-library/react';
import VirtualizedTable, { useVirtualizedColumns, VirtualizedList } from '../VirtualizedTable';

// Mock data para testes
const mockData = Array.from({ length: 1000 }, (_, index) => ({
  id: index + 1,
  name: `Item ${index + 1}`,
  email: `item${index + 1}@test.com`,
  status: index % 2 === 0 ? 'Active' : 'Inactive'
}));

const mockColumns = [
  { key: 'id', header: 'ID', width: '80px' },
  { key: 'name', header: 'Nome', width: '200px' },
  { key: 'email', header: 'Email', width: '250px' },
  { 
    key: 'status', 
    header: 'Status', 
    width: '120px',
    render: (value) => (
      <span className={`badge ${value === 'Active' ? 'bg-success' : 'bg-secondary'}`}>
        {value}
      </span>
    )
  }
];

describe('VirtualizedTable', () => {
  test('renders table with headers', () => {
    render(
      <VirtualizedTable
        data={mockData.slice(0, 10)}
        columns={mockColumns}
        containerHeight={400}
        rowHeight={50}
      />
    );

    expect(screen.getByText('ID')).toBeInTheDocument();
    expect(screen.getByText('Nome')).toBeInTheDocument();
    expect(screen.getByText('Email')).toBeInTheDocument();
    expect(screen.getByText('Status')).toBeInTheDocument();
  });

  test('renders only visible rows for performance', () => {
    render(
      <VirtualizedTable
        data={mockData}
        columns={mockColumns}
        containerHeight={400}
        rowHeight={50}
      />
    );

    // Deve renderizar apenas os primeiros itens visíveis
    expect(screen.getByText('Item 1')).toBeInTheDocument();
    expect(screen.getByText('Item 2')).toBeInTheDocument();
    
    // Não deve renderizar itens muito abaixo na lista
    expect(screen.queryByText('Item 100')).not.toBeInTheDocument();
    expect(screen.queryByText('Item 500')).not.toBeInTheDocument();
  });

  test('updates visible items on scroll', async () => {
    const { container } = render(
      <VirtualizedTable
        data={mockData}
        columns={mockColumns}
        containerHeight={400}
        rowHeight={50}
      />
    );

    const scrollContainer = container.querySelector('.virtualized-table-container');
    
    // Scroll para baixo
    fireEvent.scroll(scrollContainer, { target: { scrollTop: 1000 } });

    await waitFor(() => {
      // Após o scroll, deve mostrar itens diferentes
      expect(screen.queryByText('Item 1')).not.toBeInTheDocument();
    });
  });

  test('calls onRowClick when row is clicked', () => {
    const onRowClick = jest.fn();
    
    render(
      <VirtualizedTable
        data={mockData.slice(0, 10)}
        columns={mockColumns}
        onRowClick={onRowClick}
        containerHeight={400}
        rowHeight={50}
      />
    );

    const firstRow = screen.getByText('Item 1').closest('tr');
    fireEvent.click(firstRow);

    expect(onRowClick).toHaveBeenCalledWith(mockData[0], 0);
  });

  test('renders custom cell content using column render function', () => {
    render(
      <VirtualizedTable
        data={mockData.slice(0, 5)}
        columns={mockColumns}
        containerHeight={400}
        rowHeight={50}
      />
    );

    // Verifica se o render customizado do status está funcionando
    expect(screen.getAllByText('Active')).toHaveLength(3);
    expect(screen.getAllByText('Inactive')).toHaveLength(2);
  });

  test('shows scroll indicator with correct range', () => {
    render(
      <VirtualizedTable
        data={mockData}
        columns={mockColumns}
        containerHeight={400}
        rowHeight={50}
      />
    );

    // Deve mostrar indicador de scroll com range correto
    expect(screen.getByText(/1-\d+ de 1000/)).toBeInTheDocument();
  });

  test('handles empty data gracefully', () => {
    render(
      <VirtualizedTable
        data={[]}
        columns={mockColumns}
        containerHeight={400}
        rowHeight={50}
      />
    );

    // Headers devem ainda estar presentes
    expect(screen.getByText('ID')).toBeInTheDocument();
    expect(screen.getByText('Nome')).toBeInTheDocument();
    
    // Não deve mostrar indicador de scroll para dados vazios
    expect(screen.queryByText(/de 0/)).not.toBeInTheDocument();
  });

  test('applies custom className and props', () => {
    const { container } = render(
      <VirtualizedTable
        data={mockData.slice(0, 5)}
        columns={mockColumns}
        className="custom-table"
        data-testid="virtualized-table"
        containerHeight={400}
        rowHeight={50}
      />
    );

    const tableContainer = container.querySelector('.virtualized-table-container');
    expect(tableContainer).toHaveClass('custom-table');
    expect(tableContainer).toHaveAttribute('data-testid', 'virtualized-table');
  });

  test('calculates correct total height', () => {
    const { container } = render(
      <VirtualizedTable
        data={mockData}
        columns={mockColumns}
        containerHeight={400}
        rowHeight={50}
      />
    );

    const innerDiv = container.querySelector('.virtualized-table-container > div');
    expect(innerDiv).toHaveStyle({ height: '50000px' }); // 1000 items * 50px
  });

  test('handles overscan correctly', () => {
    render(
      <VirtualizedTable
        data={mockData}
        columns={mockColumns}
        containerHeight={400}
        rowHeight={50}
        overscan={10}
      />
    );

    // Com overscan maior, deve renderizar mais itens
    expect(screen.getByText('Item 1')).toBeInTheDocument();
    
    // Deve renderizar itens extras devido ao overscan
    const visibleItems = screen.getAllByText(/Item \d+/);
    expect(visibleItems.length).toBeGreaterThan(8); // Mais que o mínimo visível
  });
});

describe('VirtualizedList', () => {
  const mockItems = Array.from({ length: 100 }, (_, index) => ({
    id: index + 1,
    title: `Item ${index + 1}`,
    description: `Description for item ${index + 1}`
  }));

  const renderItem = (item, index) => (
    <div key={item.id} data-testid={`list-item-${index}`}>
      <h4>{item.title}</h4>
      <p>{item.description}</p>
    </div>
  );

  test('renders list items using renderItem function', () => {
    render(
      <VirtualizedList
        items={mockItems.slice(0, 10)}
        itemHeight={40}
        containerHeight={300}
        renderItem={renderItem}
      />
    );

    expect(screen.getByText('Item 1')).toBeInTheDocument();
    expect(screen.getByText('Description for item 1')).toBeInTheDocument();
    expect(screen.getByTestId('list-item-0')).toBeInTheDocument();
  });

  test('virtualizes large lists for performance', () => {
    render(
      <VirtualizedList
        items={mockItems}
        itemHeight={40}
        containerHeight={300}
        renderItem={renderItem}
      />
    );

    // Deve renderizar apenas itens visíveis
    expect(screen.getByText('Item 1')).toBeInTheDocument();
    expect(screen.queryByText('Item 50')).not.toBeInTheDocument();
  });

  test('updates visible items on scroll', async () => {
    const { container } = render(
      <VirtualizedList
        items={mockItems}
        itemHeight={40}
        containerHeight={300}
        renderItem={renderItem}
      />
    );

    const scrollContainer = container.querySelector('.virtualized-list-container');
    
    // Scroll para baixo
    fireEvent.scroll(scrollContainer, { target: { scrollTop: 800 } });

    await waitFor(() => {
      // Após scroll, deve mostrar itens diferentes
      expect(screen.queryByText('Item 1')).not.toBeInTheDocument();
    });
  });

  test('applies custom className', () => {
    const { container } = render(
      <VirtualizedList
        items={mockItems.slice(0, 5)}
        itemHeight={40}
        containerHeight={300}
        renderItem={renderItem}
        className="custom-list"
      />
    );

    const listContainer = container.querySelector('.virtualized-list-container');
    expect(listContainer).toHaveClass('custom-list');
  });

  test('handles empty items array', () => {
    render(
      <VirtualizedList
        items={[]}
        itemHeight={40}
        containerHeight={300}
        renderItem={renderItem}
      />
    );

    // Não deve renderizar nenhum item
    expect(screen.queryByText(/Item \d+/)).not.toBeInTheDocument();
  });

  test('calculates correct total height for list', () => {
    const { container } = render(
      <VirtualizedList
        items={mockItems}
        itemHeight={40}
        containerHeight={300}
        renderItem={renderItem}
      />
    );

    const innerDiv = container.querySelector('.virtualized-list-container > div');
    expect(innerDiv).toHaveStyle({ height: '4000px' }); // 100 items * 40px
  });
});

describe('useVirtualizedColumns', () => {
  test('returns columns with default width when not specified', () => {
    const baseColumns = [
      { key: 'id', header: 'ID' },
      { key: 'name', header: 'Name', width: '200px' }
    ];

    const { result } = renderHook(() => useVirtualizedColumns(baseColumns));

    expect(result.current).toEqual([
      { key: 'id', header: 'ID', width: 'auto' },
      { key: 'name', header: 'Name', width: '200px' }
    ]);
  });

  test('preserves existing column properties', () => {
    const baseColumns = [
      { 
        key: 'status', 
        header: 'Status', 
        width: '120px',
        render: (value) => <span>{value}</span>
      }
    ];

    const { result } = renderHook(() => useVirtualizedColumns(baseColumns));

    expect(result.current[0]).toEqual(expect.objectContaining({
      key: 'status',
      header: 'Status',
      width: '120px',
      render: expect.any(Function)
    }));
  });

  test('memoizes result correctly', () => {
    const baseColumns = [
      { key: 'id', header: 'ID' }
    ];

    const { result, rerender } = renderHook(
      ({ columns }) => useVirtualizedColumns(columns),
      { initialProps: { columns: baseColumns } }
    );

    const firstResult = result.current;

    // Rerender com as mesmas colunas
    rerender({ columns: baseColumns });

    expect(result.current).toBe(firstResult); // Deve ser a mesma referência
  });

  test('updates when base columns change', () => {
    const initialColumns = [{ key: 'id', header: 'ID' }];
    const updatedColumns = [{ key: 'name', header: 'Name' }];

    const { result, rerender } = renderHook(
      ({ columns }) => useVirtualizedColumns(columns),
      { initialProps: { columns: initialColumns } }
    );

    const firstResult = result.current;

    // Rerender com colunas diferentes
    rerender({ columns: updatedColumns });

    expect(result.current).not.toBe(firstResult);
    expect(result.current[0].key).toBe('name');
  });
});
