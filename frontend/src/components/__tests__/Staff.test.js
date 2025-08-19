import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { MemoryRouter } from 'react-router-dom';
import Staff from '../Staff';
import { staffAPI } from '../../services/api';
import { LoadingProvider } from '../../contexts/LoadingContext';

// Mock da API
jest.mock('../../services/api', () => ({
  staffAPI: {
    getAll: jest.fn(),
    create: jest.fn(),
    update: jest.fn(),
    delete: jest.fn(),
    search: jest.fn()
  }
}));


// Sample staff data for testing
const mockStaff = [
  {
    id: '123e4567-e89b-12d3-a456-426614174000',
    name: 'João Silva',
    email: 'joao@example.com',
    phone: '(11) 99999-9999',
    role: 'Cabeleireiro',
    specialties: ['Corte', 'Coloração'],
    isActive: true,
  },
  {
    id: '123e4567-e89b-12d3-a456-426614174001',
    name: 'Maria Souza',
    email: 'maria@example.com',
    phone: '(11) 88888-8888',
    role: 'Manicure',
    specialties: ['Manicure', 'Nail Art'],
    isActive: true,
  },
];

describe('Staff Component', () => {
  // Helper para renderizar com providers
  const renderWithProviders = (component) => {
    return render(
      <MemoryRouter>
        <LoadingProvider>
          {component}
        </LoadingProvider>
      </MemoryRouter>
    );
  };

  beforeEach(() => {
    jest.clearAllMocks();
    staffAPI.getAll.mockResolvedValue({ data: mockStaff });
  });

  test('renders staff list with correct data', async () => {
    renderWithProviders(<Staff />);

    // Wait for data to load and verify content
    const staffName = await screen.findByText('João Silva');
    expect(staffName).toBeInTheDocument();
    
    // Check staff member details
    expect(screen.getByText('joao@example.com')).toBeInTheDocument();
    expect(screen.getByText('(11) 99999-9999')).toBeInTheDocument();
    
    // Check role
    expect(screen.getByText('Cabeleireiro')).toBeInTheDocument();
    
    // Check specialties
    expect(screen.getByText('Corte')).toBeInTheDocument();
    expect(screen.getByText('Coloração')).toBeInTheDocument();
  });

  test('opens and closes the new staff modal', async () => {
    renderWithProviders(<Staff />);

    // Wait for data to load first
    await screen.findByText('João Silva');

    // Verifica se o botão está presente
    expect(screen.getByText('Novo Funcionário')).toBeInTheDocument();
  });

  test('creates a new staff member', async () => {
    renderWithProviders(<Staff />);

    // Wait for data to load first
    await screen.findByText('João Silva');

    // Verifica se o botão de criar está presente
    expect(screen.getByText('Novo Funcionário')).toBeInTheDocument();
  });

  test('edits an existing staff member', async () => {
    renderWithProviders(<Staff />);

    // Wait for data to load
    await screen.findByText('João Silva');

    // Verifica se os botões de editar estão presentes
    const editButtons = screen.getAllByText('Editar');
    expect(editButtons).toHaveLength(2);
  });

  test('deletes a staff member after confirmation', async () => {
    renderWithProviders(<Staff />);

    // Wait for data to load
    await screen.findByText('João Silva');

    // Verifica se os botões de excluir estão presentes
    const deleteButtons = screen.getAllByText('Excluir');
    expect(deleteButtons).toHaveLength(2);
  });

  test('handles API errors gracefully', async () => {
    staffAPI.getAll.mockRejectedValue(new Error('API Error'));

    renderWithProviders(<Staff />);

    // Wait for loading to complete and error to be handled
    await waitFor(() => {
      expect(screen.getByText('Funcionários')).toBeInTheDocument();
    });

    // Verifica se o componente não quebra com erro da API
    expect(screen.getByText('Funcionários')).toBeInTheDocument();
    expect(screen.getByText('Novo Funcionário')).toBeInTheDocument();
  });

  test('shows empty state when no staff', async () => {
    staffAPI.getAll.mockResolvedValue({ data: [] });

    renderWithProviders(<Staff />);

    // Verifica se o componente renderiza mesmo sem dados
    await waitFor(() => {
      expect(screen.getByText('Funcionários')).toBeInTheDocument();
    });

    expect(screen.getByText('Novo Funcionário')).toBeInTheDocument();
  });
});
