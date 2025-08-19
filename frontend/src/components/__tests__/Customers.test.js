import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import Customers from '../Customers';
import { customersAPI } from '../../services/api';
import { LoadingProvider } from '../../contexts/LoadingContext';

// Mock do módulo API correto
jest.mock('../../services/api', () => ({
  customersAPI: {
    getAll: jest.fn(),
    create: jest.fn(),
    update: jest.fn(),
    delete: jest.fn(),
    search: jest.fn()
  }
}));

// Mock do react-toastify
jest.mock('react-toastify', () => ({
  toast: {
    success: jest.fn(),
    error: jest.fn(),
  },
}));

// Mock do LoadingSpinner
jest.mock('../LoadingSpinner', () => ({
  __esModule: true,
  default: ({ text = 'Loading...' }) => <div data-testid="loading-spinner">{text}</div>
}));

// Mock do React Bootstrap
jest.mock('react-bootstrap', () => {
  const MockCard = ({ children, ...props }) => <div {...props}>{children}</div>;
  MockCard.Header = ({ children, ...props }) => <div {...props}>{children}</div>;
  MockCard.Body = ({ children, ...props }) => <div {...props}>{children}</div>;
  
  const MockModal = ({ children, show, ...props }) => show ? <div role="dialog" {...props}>{children}</div> : null;
  MockModal.Header = ({ children, ...props }) => <div {...props}>{children}</div>;
  MockModal.Title = ({ children, ...props }) => <h4 {...props}>{children}</h4>;
  MockModal.Body = ({ children, ...props }) => <div {...props}>{children}</div>;
  MockModal.Footer = ({ children, ...props }) => <div {...props}>{children}</div>;
  
  const MockForm = ({ children, onSubmit, ...props }) => <form onSubmit={onSubmit} {...props}>{children}</form>;
  MockForm.Group = ({ children, ...props }) => <div {...props}>{children}</div>;
  MockForm.Label = ({ children, ...props }) => <label {...props}>{children}</label>;
  MockForm.Control = ({ value, onChange, name, ...props }) => (
    <input 
      aria-label={name} 
      value={value} 
      onChange={onChange} 
      name={name} 
      {...props} 
    />
  );
  
  const MockTable = ({ children, ...props }) => <table {...props}>{children}</table>;
  
  return {
    Container: ({ children, ...props }) => <div {...props}>{children}</div>,
    Row: ({ children, ...props }) => <div {...props}>{children}</div>,
    Col: ({ children, ...props }) => <div {...props}>{children}</div>,
    Card: MockCard,
    Button: ({ children, onClick, ...props }) => <button onClick={onClick} {...props}>{children}</button>,
    Modal: MockModal,
    Form: MockForm,
    Table: MockTable,
    Badge: ({ children, ...props }) => <span {...props}>{children}</span>
  };
});


describe('Customers Component', () => {
  const mockCustomers = [
    {
      id: '123e4567-e89b-12d3-a456-426614174000',
      name: 'Maria Silva',
      email: 'maria@email.com',
      phone: '(11) 99999-1111',
      address: 'Rua das Flores, 123',
      createdAt: '2025-01-01T10:00:00Z',
      updatedAt: '2025-01-01T10:00:00Z'
    },
    {
      id: '123e4567-e89b-12d3-a456-426614174001',
      name: 'João Santos',
      email: 'joao@email.com',
      phone: '(11) 88888-2222',
      address: 'Rua Nova, 456',
      createdAt: '2025-01-01T11:00:00Z',
      updatedAt: '2025-01-01T11:00:00Z'
    }
  ];

  // Helper para renderizar com providers
  const renderWithProviders = (component) => {
    return render(
      <LoadingProvider>
        {component}
      </LoadingProvider>
    );
  };

  beforeEach(() => {
    // Reset dos mocks antes de cada teste
    jest.clearAllMocks();
    customersAPI.getAll.mockResolvedValue({ data: mockCustomers });
  });

  test('renders customers list correctly', async () => {
    renderWithProviders(<Customers />);

    // Wait for customers to load
    await waitFor(() => {
      // Verifica se o título está presente
      expect(screen.getByRole('heading', { name: 'Clientes' })).toBeInTheDocument();
    });

    // Verifica se o botão correto está presente
    expect(screen.getByRole('button', { name: 'Adicionar Cliente' })).toBeInTheDocument();

    // Verifica se os clientes estão sendo exibidos
    expect(screen.getByText('Maria Silva')).toBeInTheDocument();
    expect(screen.getByText('João Santos')).toBeInTheDocument();

    // Verifica se a API foi chamada corretamente
    expect(customersAPI.getAll).toHaveBeenCalledTimes(1);
  });

  test('opens add customer modal when button is clicked', async () => {
    renderWithProviders(<Customers />);

    await waitFor(() => {
      expect(screen.getByText('Maria Silva')).toBeInTheDocument();
    });

    // Clica no botão correto
    fireEvent.click(screen.getByRole('button', { name: 'Adicionar Cliente' }));

    // Verifica se o modal foi aberto
    await waitFor(() => {
      expect(screen.getByRole('dialog')).toBeInTheDocument();
    }, { timeout: 2000 });
  });

  test('creates new customer successfully', async () => {
    const newCustomer = {
      id: '123e4567-e89b-12d3-a456-426614174002',
      name: 'Ana Costa',
      email: 'ana@email.com',
      phone: '(11) 77777-3333',
      address: 'Rua Terceira, 789'
    };

    customersAPI.create.mockResolvedValue({ data: newCustomer });

    renderWithProviders(<Customers />);

    await waitFor(() => {
      expect(screen.getByText('Maria Silva')).toBeInTheDocument();
    });

    // Abre o modal
    fireEvent.click(screen.getByRole('button', { name: 'Adicionar Cliente' }));

    await waitFor(() => {
      expect(screen.getByRole('dialog')).toBeInTheDocument();
    });

    // Preenche o formulário usando aria-label
    fireEvent.change(screen.getByLabelText('name'), {
      target: { value: 'Ana Costa' }
    });
    fireEvent.change(screen.getByLabelText('email'), {
      target: { value: 'ana@email.com' }
    });
    fireEvent.change(screen.getByLabelText('phone'), {
      target: { value: '(11) 77777-3333' }
    });
    fireEvent.change(screen.getByLabelText('address'), {
      target: { value: 'Rua Central, 789' }
    });

    // Submete o formulário
    fireEvent.click(screen.getByRole('button', { name: 'Criar' }));

    // Verifica se a API foi chamada corretamente
    await waitFor(() => {
      expect(customersAPI.create).toHaveBeenCalledWith({
        name: 'Ana Costa',
        email: 'ana@email.com',
        phone: '(11) 77777-3333',
        address: 'Rua Central, 789'
      });
    });
  });

  test('opens edit customer modal when edit button is clicked', async () => {
    renderWithProviders(<Customers />);

    await waitFor(() => {
      expect(screen.getByText('Maria Silva')).toBeInTheDocument();
    });

    // Clica no botão de editar (primeiro da lista)
    const editButtons = screen.getAllByRole('button', { name: 'Editar' });
    fireEvent.click(editButtons[0]);

    // Verifica se o modal foi aberto com dados preenchidos
    await waitFor(() => {
      expect(screen.getByRole('dialog')).toBeInTheDocument();
      expect(screen.getByText('Editar Cliente')).toBeInTheDocument();
      expect(screen.getByDisplayValue('Maria Silva')).toBeInTheDocument();
      expect(screen.getByDisplayValue('maria@email.com')).toBeInTheDocument();
    });
  });

  test('updates customer successfully', async () => {
    const updatedCustomer = {
      ...mockCustomers[0],
      name: 'Maria Silva Santos',
      email: 'maria.santos@email.com'
    };

    customersAPI.update.mockResolvedValue({ data: updatedCustomer });

    renderWithProviders(<Customers />);

    await waitFor(() => {
      expect(screen.getByText('Maria Silva')).toBeInTheDocument();
    });

    // Abre o modal de edição
    const editButtons = screen.getAllByText('Editar');
    fireEvent.click(editButtons[0]);

    await waitFor(() => {
      expect(screen.getByText('Editar Cliente')).toBeInTheDocument();
    });

    // Modifica os dados
    fireEvent.change(screen.getByDisplayValue('Maria Silva'), {
      target: { value: 'Maria Silva Santos' }
    });
    fireEvent.change(screen.getByDisplayValue('maria@email.com'), {
      target: { value: 'maria.santos@email.com' }
    });

    // Submete o formulário
    fireEvent.click(screen.getByRole('button', { name: 'Atualizar' }));

    // Verifica se a API foi chamada corretamente
    await waitFor(() => {
      expect(customersAPI.update).toHaveBeenCalledWith(
        '123e4567-e89b-12d3-a456-426614174000',
        expect.objectContaining({
          name: 'Maria Silva Santos',
          email: 'maria.santos@email.com'
        })
      );
    });
  });

  test('deletes customer successfully', async () => {
    customersAPI.delete.mockResolvedValue({ data: true });
    
    // Mock do window.confirm
    window.confirm = jest.fn(() => true);

    renderWithProviders(<Customers />);

    await waitFor(() => {
      expect(screen.getByText('Maria Silva')).toBeInTheDocument();
    });

    // Clica no botão de excluir
    const deleteButtons = screen.getAllByRole('button', { name: 'Excluir' });
    fireEvent.click(deleteButtons[0]);

    // Verifica se a confirmação foi chamada
    expect(window.confirm).toHaveBeenCalledWith(
      'Tem certeza que deseja excluir este cliente?'
    );

    // Verifica se a API foi chamada corretamente
    await waitFor(() => {
      expect(customersAPI.delete).toHaveBeenCalledWith(
        '123e4567-e89b-12d3-a456-426614174000'
      );
    });
  });

  test('handles API errors gracefully', async () => {
    customersAPI.getAll.mockRejectedValue(new Error('API Error'));

    renderWithProviders(<Customers />);

    // The loading spinner will be shown initially, then the component should handle the error gracefully
    await waitFor(() => {
      // Check that the component doesn't crash and the button is still present
      expect(screen.getByRole('button', { name: 'Adicionar Cliente' })).toBeInTheDocument();
    });
  });

  test('closes modal when cancel button is clicked', async () => {
    renderWithProviders(<Customers />);

    await waitFor(() => {
      expect(screen.getByText('Maria Silva')).toBeInTheDocument();
    });

    // Abre o modal
    fireEvent.click(screen.getByRole('button', { name: 'Adicionar Cliente' }));

    await waitFor(() => {
      expect(screen.getByRole('dialog')).toBeInTheDocument();
    });

    // Clica em cancelar
    fireEvent.click(screen.getByText('Cancelar'));

    // Verifica se o modal foi fechado
    await waitFor(() => {
      expect(screen.queryByRole('dialog')).not.toBeInTheDocument();
    });
  });
});
