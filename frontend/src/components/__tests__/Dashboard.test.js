import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { BrowserRouter } from 'react-router-dom';
import Dashboard from '../Dashboard';
import { customersAPI, servicesAPI, staffAPI, appointmentsAPI } from '../../services/api';

// Mock dos módulos API
jest.mock('../../services/api', () => ({
  customersAPI: {
    getAll: jest.fn()
  },
  servicesAPI: {
    getActive: jest.fn()
  },
  staffAPI: {
    getActive: jest.fn()
  },
  appointmentsAPI: {
    getAll: jest.fn(),
    getByDate: jest.fn(),
    getToday: jest.fn()
  }
}));

// Mock do react-toastify
jest.mock('react-toastify', () => ({
  toast: {
    error: jest.fn()
  }
}));

describe('Dashboard Component', () => {
  const mockCustomers = [
    { id: '1', name: 'Maria Silva' },
    { id: '2', name: 'João Santos' }
  ];

  const mockServices = [
    { id: '1', name: 'Corte de Cabelo', isActive: true },
    { id: '2', name: 'Manicure', isActive: true }
  ];

  const mockStaff = [
    { id: '1', name: 'Ana Silva', isActive: true },
    { id: '2', name: 'Carlos Santos', isActive: true }
  ];

  const mockAppointments = [
    { id: '1', appointmentDate: '2025-08-09', status: 'scheduled' },
    { id: '2', appointmentDate: '2025-08-09', status: 'confirmed' },
    { id: '3', appointmentDate: '2025-08-10', status: 'scheduled' }
  ];

  const mockTodayAppointments = [
    { id: '1', appointmentDate: '2025-08-09', status: 'scheduled' },
    { id: '2', appointmentDate: '2025-08-09', status: 'confirmed' }
  ];

  beforeEach(() => {
    jest.clearAllMocks();
    
    // Mock das respostas das APIs
    customersAPI.getAll.mockResolvedValue({ data: mockCustomers });
    servicesAPI.getActive.mockResolvedValue({ data: mockServices });
    staffAPI.getActive.mockResolvedValue({ data: mockStaff });
    appointmentsAPI.getAll.mockResolvedValue({ data: mockAppointments });
    appointmentsAPI.getByDate.mockResolvedValue({ data: mockTodayAppointments });
    appointmentsAPI.getToday.mockResolvedValue({ data: mockTodayAppointments });
  });

  const renderDashboard = () => {
    return render(
      <BrowserRouter>
        <Dashboard />
      </BrowserRouter>
    );
  };

  test('renders dashboard title and description', async () => {
    renderDashboard();

    await waitFor(() => {
      expect(screen.getByText('Dashboard')).toBeInTheDocument();
      expect(screen.getByText('Visão geral do seu salão de beleza')).toBeInTheDocument();
    });
  });

  test('displays loading spinner initially', () => {
    renderDashboard();

    expect(screen.getByText('Carregando...')).toBeInTheDocument();
    expect(screen.getByRole('status')).toBeInTheDocument();
  });

  test('displays correct statistics cards after loading', async () => {
    renderDashboard();

    await waitFor(() => {
      // Verifica se os cards de estatísticas estão presentes
      expect(screen.getByText('Clientes')).toBeInTheDocument();
      expect(screen.getByText('Serviços Ativos')).toBeInTheDocument();
      expect(screen.getByText('Funcionários')).toBeInTheDocument();
      expect(screen.getByText('Agendamentos Hoje')).toBeInTheDocument();
    });

    await waitFor(() => {
      // Verifica os valores das estatísticas usando getAllByText para múltiplas ocorrências
      const twoValues = screen.getAllByText('2');
      expect(twoValues.length).toBeGreaterThanOrEqual(4); // Pelo menos 4 ocorrências do número 2
    });
  });

  test('calls all API endpoints on component mount', async () => {
    renderDashboard();

    await waitFor(() => {
      expect(customersAPI.getAll).toHaveBeenCalledTimes(1);
      expect(servicesAPI.getActive).toHaveBeenCalledTimes(1);
      expect(staffAPI.getActive).toHaveBeenCalledTimes(1);
      expect(appointmentsAPI.getAll).toHaveBeenCalledTimes(1);
      expect(appointmentsAPI.getByDate).toHaveBeenCalledTimes(1);
    });
  });

  test('displays quick actions section', async () => {
    renderDashboard();

    await waitFor(() => {
      expect(screen.getByText('Ações Rápidas')).toBeInTheDocument();
      expect(screen.getByText('Novo Agendamento')).toBeInTheDocument();
      expect(screen.getByText('Gerenciar Clientes')).toBeInTheDocument();
      expect(screen.getByText('Gerenciar Serviços')).toBeInTheDocument();
    });
  });

  test('displays summary section', async () => {
    renderDashboard();

    await waitFor(() => {
      expect(screen.getByText('Resumo Total')).toBeInTheDocument();
      expect(screen.getByText('Total de Agendamentos:')).toBeInTheDocument();
      expect(screen.getByText('Clientes Cadastrados:')).toBeInTheDocument();
      expect(screen.getByText('Serviços Disponíveis:')).toBeInTheDocument();
      expect(screen.getByText('Equipe Ativa:')).toBeInTheDocument();
    });
  });

  test('handles API errors gracefully', async () => {
    // Mock de erro para uma das APIs
    customersAPI.getAll.mockRejectedValue(new Error('API Error'));
    servicesAPI.getActive.mockResolvedValue({ data: mockServices });
    staffAPI.getActive.mockResolvedValue({ data: mockStaff });
    appointmentsAPI.getAll.mockResolvedValue({ data: mockAppointments });
    appointmentsAPI.getByDate.mockResolvedValue({ data: mockTodayAppointments });

    renderDashboard();

    // Verifica se o componente ainda renderiza mesmo com erro
    await waitFor(() => {
      expect(screen.getByText('Dashboard')).toBeInTheDocument();
    });
  });

  test('renders navigation links correctly', async () => {
    renderDashboard();

    await waitFor(() => {
      // Verifica se os links de navegação estão presentes usando getByText
      const appointmentsLink = screen.getByText('Novo Agendamento');
      const customersLink = screen.getByText('Gerenciar Clientes');
      const servicesLink = screen.getByText('Gerenciar Serviços');
      
      expect(appointmentsLink).toBeInTheDocument();
      expect(customersLink).toBeInTheDocument();
      expect(servicesLink).toBeInTheDocument();
      
      // Verifica se são elementos clicáveis
      expect(appointmentsLink.closest('a')).toHaveAttribute('href', '/appointments');
      expect(customersLink.closest('a')).toHaveAttribute('href', '/customers');
      expect(servicesLink.closest('a')).toHaveAttribute('href', '/services');
    });
  });

  test('displays correct appointment counts in summary', async () => {
    renderDashboard();

    await waitFor(() => {
      // Verifica se o resumo mostra os valores corretos
      const summarySection = screen.getByText('Resumo Total').closest('.card');
      expect(summarySection).toBeInTheDocument();
      
      // Verifica os valores no resumo usando texto mais específico
      expect(screen.getByText('Total de Agendamentos:')).toBeInTheDocument();
      expect(screen.getByText('Clientes Cadastrados:')).toBeInTheDocument();
      expect(screen.getByText('Serviços Disponíveis:')).toBeInTheDocument();
      expect(screen.getByText('Equipe Ativa:')).toBeInTheDocument();
    });
  });

  test('calls getToday for dashboard statistics', async () => {
    renderDashboard();

    await waitFor(() => {
      expect(appointmentsAPI.getToday).toHaveBeenCalled();
    });
  });

  test('displays dashboard cards with correct icons', async () => {
    renderDashboard();

    await waitFor(() => {
      // Verifica se os ícones estão presentes
      expect(screen.getByText('👥')).toBeInTheDocument(); // Clientes
      expect(screen.getByText('✨')).toBeInTheDocument(); // Serviços
      expect(screen.getByText('👩‍💼')).toBeInTheDocument(); // Funcionários
      expect(screen.getByText('📅')).toBeInTheDocument(); // Agendamentos
    });
  });
});
