import React from 'react';
import { render, screen, waitFor, fireEvent, within } from '@testing-library/react';
import '@testing-library/jest-dom';
import Appointments from '../Appointments';
import { appointmentsAPI, customersAPI, servicesAPI, staffAPI } from '../../services/api';
import { toast } from 'react-toastify';

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

// Mock react-toastify
jest.mock('react-toastify', () => ({
  toast: {
    success: jest.fn(),
    error: jest.fn(),
  },
}));

// Mock window.confirm
global.confirm = jest.fn(() => true);

const mockCustomers = [
  { id: '1', name: 'John Doe' },
  { id: '2', name: 'Jane Smith' },
];

const mockServices = [
  { id: '101', name: 'Corte de Cabelo', price: 50.00 },
  { id: '102', name: 'Manicure', price: 30.00 },
];

const mockStaff = [
  { id: '201', name: 'Carlos', role: 'Cabeleireiro' },
  { id: '202', name: 'Ana', role: 'Manicure' },
];

const mockAppointments = [
  {
    id: '301',
    customerId: '1',
    staffId: '201',
    serviceId: '101',
    appointmentDate: '2024-01-10',
    appointmentTime: '10:00',
    status: 'completed',
    totalPrice: 50.00,
  },
];

describe('Appointments Component', () => {
  beforeEach(() => {
    // Reset mocks before each test
    jest.clearAllMocks();

    // Setup default successful mock responses
    customersAPI.getAll.mockResolvedValue({ data: mockCustomers });
    servicesAPI.getActive.mockResolvedValue({ data: mockServices });
    staffAPI.getActive.mockResolvedValue({ data: mockStaff });
    appointmentsAPI.getByDate.mockResolvedValue({ data: mockAppointments });
  });

  test('should render and load initial data correctly', async () => {
    render(<Appointments />);

    // Check for loading state if any, or wait for data to be loaded
    expect(await screen.findByText('John Doe')).toBeInTheDocument();
    expect(screen.getByText('Corte de Cabelo')).toBeInTheDocument();
    expect(screen.getByText('Carlos')).toBeInTheDocument();
    expect(screen.getByText('Concluído')).toBeInTheDocument();

    // Verify that all necessary APIs were called on initial load
    await waitFor(() => {
      expect(customersAPI.getAll).toHaveBeenCalledTimes(1);
      expect(servicesAPI.getActive).toHaveBeenCalledTimes(1);
      expect(staffAPI.getActive).toHaveBeenCalledTimes(1);
      expect(appointmentsAPI.getByDate).toHaveBeenCalledTimes(1);
    });
  });

  test('should open modal, create a new appointment, and refresh the list', async () => {
    // Mock the create API to resolve successfully
    appointmentsAPI.create.mockResolvedValue({ data: {} });

    render(<Appointments />);

    // Wait for initial data to load to prevent state update issues
    await screen.findByText('John Doe');

    // 1. Click the "New Appointment" button
    const newAppointmentButton = screen.getByRole('button', { name: /novo agendamento/i });
    fireEvent.click(newAppointmentButton);

    // 2. Wait for the modal to appear and verify its title
    // 2. Wait for the modal to appear
    await screen.findByRole('dialog');

    // 3. Fill out the form
    fireEvent.change(screen.getByLabelText(/cliente/i), { target: { value: '2' } }); // Jane Smith
    fireEvent.change(screen.getByLabelText(/funcionário/i), { target: { value: '202' } }); // Ana
    fireEvent.change(screen.getByLabelText(/serviço/i), { target: { value: '102' } }); // Manicure
    fireEvent.change(screen.getByLabelText(/horário/i), { target: { value: '14:00' } });

    // 4. Submit the form
    const createButton = screen.getByRole('button', { name: /criar/i });
    fireEvent.click(createButton);

    // 5. Verify that the create API was called with the correct data
    await waitFor(() => {
      expect(appointmentsAPI.create).toHaveBeenCalledTimes(1);
      expect(appointmentsAPI.create).toHaveBeenCalledWith(
        expect.objectContaining({
          customerId: '2',
          staffId: '202',
          serviceId: '102',
          appointmentTime: '14:00',
          status: 'scheduled',
        })
      );
    });

    // 6. Verify the success toast is shown and the appointment list is reloaded
    await waitFor(() => {
      expect(toast.success).toHaveBeenCalledWith('Agendamento criado com sucesso!');
    });

    // The list reloads, so getByDate is called again
    await waitFor(() => {
      // Initial load + reload after create
      expect(appointmentsAPI.getByDate).toHaveBeenCalledTimes(2);
    });
  });
});
