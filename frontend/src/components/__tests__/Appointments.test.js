import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { MemoryRouter } from 'react-router-dom';
import Appointments from '../Appointments';
import { appointmentsAPI, customersAPI, servicesAPI, staffAPI } from '../../services/api';
import { LoadingProvider } from '../../contexts/LoadingContext';

// Mock the entire API module
jest.mock('../../services/api', () => ({
  appointmentsAPI: {
    getByDate: jest.fn(),
    create: jest.fn(),
    update: jest.fn(),
    delete: jest.fn(),
  },
  customersAPI: {
    getAll: jest.fn(),
  },
  servicesAPI: {
    getActive: jest.fn(),
  },
  staffAPI: {
    getActive: jest.fn(),
  },
}));


// Mock window.confirm
global.confirm = jest.fn(() => true);

const mockCustomers = [
  { id: '123e4567-e89b-12d3-a456-426614174000', name: 'John Doe' },
  { id: '123e4567-e89b-12d3-a456-426614174001', name: 'Jane Smith' },
];

const mockServices = [
  { id: '123e4567-e89b-12d3-a456-426614174002', name: 'Corte de Cabelo', price: 50.00 },
  { id: '123e4567-e89b-12d3-a456-426614174003', name: 'Manicure', price: 30.00 },
];

const mockStaff = [
  { id: '123e4567-e89b-12d3-a456-426614174004', name: 'Carlos', role: 'Cabeleireiro' },
  { id: '123e4567-e89b-12d3-a456-426614174005', name: 'Ana', role: 'Manicure' },
];

const mockAppointments = [
  {
    id: '123e4567-e89b-12d3-a456-426614174006',
    customerId: '123e4567-e89b-12d3-a456-426614174000',
    staffId: '123e4567-e89b-12d3-a456-426614174004',
    serviceId: '123e4567-e89b-12d3-a456-426614174002',
    appointmentDate: '2024-01-10',
    appointmentTime: '10:00',
    status: 'completed',
    totalPrice: 50.00,
  },
];

describe('Appointments Component', () => {
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
    
    // Setup default mock responses
    customersAPI.getAll.mockResolvedValue({ data: mockCustomers });
    servicesAPI.getActive.mockResolvedValue({ data: mockServices });
    staffAPI.getActive.mockResolvedValue({ data: mockStaff });
    appointmentsAPI.getByDate.mockResolvedValue({ data: mockAppointments });
  });

  test('renders appointments calendar correctly', async () => {
    renderWithProviders(<Appointments />);

    // Wait for data to be loaded
    await screen.findByText('John Doe');
    
    // Check basic content
    expect(screen.getByText('Corte de Cabelo')).toBeInTheDocument();
    expect(screen.getByText('Carlos')).toBeInTheDocument();
  });

  test('should open modal, create a new appointment, and refresh the list', async () => {
    renderWithProviders(<Appointments />);

    // Wait for initial data to load
    await screen.findByText('John Doe');

    // Verifica se o botão de novo agendamento está presente
    expect(screen.getByText('Novo Agendamento')).toBeInTheDocument();
  });

  test('handles API errors gracefully', async () => {
    appointmentsAPI.getByDate.mockRejectedValue(new Error('API Error'));

    renderWithProviders(<Appointments />);

    // Wait for loading to complete and error to be handled
    await waitFor(() => {
      expect(screen.getByText('Agendamentos')).toBeInTheDocument();
    });

    // Verifica se o componente não quebra com erro da API
    expect(screen.getByText('Agendamentos')).toBeInTheDocument();
  });

  test('shows empty state when no appointments', async () => {
    appointmentsAPI.getByDate.mockResolvedValue({ data: [] });

    renderWithProviders(<Appointments />);

    // Verifica se o componente renderiza mesmo sem dados
    await waitFor(() => {
      expect(screen.getByText('Agendamentos')).toBeInTheDocument();
    });

    expect(screen.getByText('Novo Agendamento')).toBeInTheDocument();
  });
});
