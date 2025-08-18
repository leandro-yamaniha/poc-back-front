import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import Services from '../Services';
import { servicesAPI } from '../../services/api';
import { LoadingProvider } from '../../contexts/LoadingContext';

// Mock do módulo API correto
jest.mock('../../services/api', () => ({
  servicesAPI: {
    getAll: jest.fn(),
    create: jest.fn(),
    update: jest.fn(),
    delete: jest.fn()
  }
}));

// Mock do react-toastify
jest.mock('react-toastify', () => ({
  toast: {
    success: jest.fn(),
    error: jest.fn(),
    info: jest.fn(),
  },
}));


describe('Services Component', () => {
  const mockServices = [
    {
      id: '123e4567-e89b-12d3-a456-426614174000',
      name: 'Corte de Cabelo',
      description: 'Corte moderno e estiloso',
      duration: 60,
      price: 50.00,
      category: 'Cabelo',
      isActive: true,
      createdAt: '2025-01-01T10:00:00Z',
      updatedAt: '2025-01-01T10:00:00Z'
    },
    {
      id: '123e4567-e89b-12d3-a456-426614174001',
      name: 'Manicure',
      description: 'Cuidado completo das unhas',
      duration: 45,
      price: 30.00,
      category: 'Unhas',
      isActive: true,
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
    servicesAPI.getAll.mockResolvedValue({ data: mockServices });
  });

  test('renders services list correctly', async () => {
    renderWithProviders(<Services />);

    // Wait for services to load
    await screen.findByText('Corte de Cabelo');

    // Verifica se o título está presente
    expect(screen.getByText('Serviços')).toBeInTheDocument();
    expect(screen.getByText('Gerencie os serviços oferecidos pelo seu salão')).toBeInTheDocument();

    // Verifica se o botão correto está presente
    expect(screen.getByText('Novo Serviço')).toBeInTheDocument();

    // Verifica se a tabela está presente com os dados corretos
    expect(screen.getByText('Corte de Cabelo')).toBeInTheDocument();
    expect(screen.getByText('Corte moderno e estiloso')).toBeInTheDocument();
    expect(screen.getByText('60 min')).toBeInTheDocument();
    expect(screen.getByText('R$ 50,00')).toBeInTheDocument();

    expect(screen.getByText('Manicure')).toBeInTheDocument();
    expect(screen.getByText('Cuidado completo das unhas')).toBeInTheDocument();
    expect(screen.getByText('45 min')).toBeInTheDocument();
    expect(screen.getByText('R$ 30,00')).toBeInTheDocument();
  });

  test('shows empty state when no services', async () => {
    servicesAPI.getAll.mockResolvedValue({ data: [] });

    renderWithProviders(<Services />);

    await waitFor(() => {
      expect(screen.getByText('Nenhum serviço encontrado')).toBeInTheDocument();
      expect(screen.getByText('Comece adicionando seus primeiros serviços!')).toBeInTheDocument();
    });
  });

  test('opens add service modal when button is clicked', async () => {
    renderWithProviders(<Services />);

    // Wait for loading to complete
    await screen.findByText('Corte de Cabelo');

    // Clica no botão correto
    fireEvent.click(screen.getByRole('button', { name: 'Novo Serviço' }));

    // Verifica se o modal foi aberto
    await screen.findByText('Novo Serviço', { selector: '.modal-title' });
    expect(screen.getByLabelText('Nome')).toBeInTheDocument();
    expect(screen.getByLabelText('Categoria')).toBeInTheDocument();
    expect(screen.getByLabelText('Descrição')).toBeInTheDocument();
  });

  test('creates new service successfully', async () => {
    const newService = {
      id: '123e4567-e89b-12d3-a456-426614174002',
      name: 'Escova',
      description: 'Escova modeladora',
      duration: 30,
      price: 25.00,
      category: 'Cabelo',
      isActive: true
    };

    servicesAPI.create.mockResolvedValue({ data: newService });

    renderWithProviders(<Services />);

    await waitFor(() => {
      expect(screen.getByText('Corte de Cabelo')).toBeInTheDocument();
    });

    // Abre o modal
    fireEvent.click(screen.getByText('Novo Serviço'));

    // Preenche o formulário usando labels
    fireEvent.change(screen.getByLabelText('Nome'), { target: { value: 'Escova' } });
    fireEvent.change(screen.getByLabelText('Categoria'), { target: { value: 'Cabelo' } });
    fireEvent.change(screen.getByLabelText('Duração (minutos)'), { target: { value: '30' } });
    fireEvent.change(screen.getByLabelText('Preço (R$)'), { target: { value: '25.00' } });

    // Submete o formulário
    fireEvent.click(screen.getByText('Criar'));

    // Verifica se a API foi chamada corretamente
    await waitFor(() => {
      expect(servicesAPI.create).toHaveBeenCalledWith(
        expect.objectContaining({
          name: 'Escova',
          category: 'Cabelo',
          duration: 30,
          price: 25.00
        })
      );
    });
  });

  test('opens edit service modal when edit button is clicked', async () => {
    renderWithProviders(<Services />);

    // Wait for loading to complete
    await screen.findByText('Corte de Cabelo');

    // Clica no botão de editar
    const editButtons = screen.getAllByText('Editar');
    fireEvent.click(editButtons[0]);

    // Verifica se o modal foi aberto com dados preenchidos
    await screen.findByText('Editar Serviço', { selector: '.modal-title' });
    expect(screen.getByDisplayValue('Corte de Cabelo')).toBeInTheDocument();
  });

  test('updates service successfully', async () => {
    const updatedService = {
      ...mockServices[0],
      name: 'Corte Premium',
      price: 80.00
    };

    servicesAPI.update.mockResolvedValue({ data: updatedService });

    renderWithProviders(<Services />);

    // Wait for loading to complete
    await screen.findByText('Corte de Cabelo');

    // Abre o modal de edição
    const editButtons = screen.getAllByText('Editar');
    fireEvent.click(editButtons[0]);

    // Wait for modal to open
    await screen.findByText('Editar Serviço', { selector: '.modal-title' });

    // Modifica os dados
    fireEvent.change(screen.getByLabelText('Nome'), {
      target: { value: 'Corte Premium' }
    });
    fireEvent.change(screen.getByLabelText('Preço (R$)'), {
      target: { value: '80.00' }
    });

    // Submete o formulário
    fireEvent.click(screen.getByText('Atualizar'));

    // Verifica se a API foi chamada corretamente
    await waitFor(() => {
      expect(servicesAPI.update).toHaveBeenCalledWith(
        '123e4567-e89b-12d3-a456-426614174000',
        expect.objectContaining({
          name: 'Corte Premium'
        })
      );
    });
  });

  test('deletes service successfully', async () => {
    servicesAPI.delete.mockResolvedValue({ data: true });
    
    // Mock do window.confirm
    window.confirm = jest.fn(() => true);

    renderWithProviders(<Services />);

    await waitFor(() => {
      expect(screen.getByText('Corte de Cabelo')).toBeInTheDocument();
    });

    // Clica no botão de excluir
    const deleteButtons = screen.getAllByText('Excluir');
    fireEvent.click(deleteButtons[0]);

    // Verifica se a confirmação foi chamada
    expect(window.confirm).toHaveBeenCalledWith(
      'Tem certeza que deseja excluir este serviço?'
    );

    // Verifica se a API foi chamada corretamente
    await waitFor(() => {
      expect(servicesAPI.delete).toHaveBeenCalledWith(
        '123e4567-e89b-12d3-a456-426614174000'
      );
    });
  });

  test('handles API errors gracefully', async () => {
    servicesAPI.getAll.mockRejectedValue(new Error('API Error'));

    renderWithProviders(<Services />);

    // Wait for loading to complete and error to be handled
    await waitFor(() => {
      expect(screen.getByText('Serviços')).toBeInTheDocument();
    });

    // Verifica se o componente não quebra com erro da API
    expect(screen.getByText('Serviços')).toBeInTheDocument();
    expect(screen.getByText('Novo Serviço')).toBeInTheDocument();
  });

  test('closes modal when cancel button is clicked', async () => {
    renderWithProviders(<Services />);

    // Wait for loading to complete
    await screen.findByText('Corte de Cabelo');

    // Abre o modal
    fireEvent.click(screen.getByRole('button', { name: 'Novo Serviço' }));

    // Wait for modal to open
    await screen.findByText('Novo Serviço', { selector: '.modal-title' });

    // Clica em cancelar
    fireEvent.click(screen.getByText('Cancelar'));

    // Verifica se o modal foi fechado
    await waitFor(() => {
      expect(screen.queryByText('Novo Serviço', { selector: '.modal-title' })).not.toBeInTheDocument();
    });
  });

  test('shows loading state initially', () => {
    // Mock que demora para resolver
    servicesAPI.getAll.mockImplementation(() => new Promise(() => {}));

    renderWithProviders(<Services />);

    // Verifica se o loading está sendo mostrado
    expect(screen.getByText('Carregando...')).toBeInTheDocument();
    expect(screen.getByRole('status')).toBeInTheDocument();
  });

  test('formats price correctly', async () => {
    renderWithProviders(<Services />);

    // Wait for services to load
    await screen.findByText('Corte de Cabelo');

    expect(screen.getByText(/R\$\s*50,00/)).toBeInTheDocument();
    expect(screen.getByText(/R\$\s*30,00/)).toBeInTheDocument();
  });

  test('shows service categories as badges', async () => {
    renderWithProviders(<Services />);

    // Wait for services to load
    await screen.findByText('Cabelo');
    
    expect(screen.getByText('Cabelo')).toBeInTheDocument();
    expect(screen.getByText('Unhas')).toBeInTheDocument();
  });
});
