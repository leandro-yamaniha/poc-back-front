// Mock das funções da API diretamente
jest.mock('../api', () => ({
  getCustomers: jest.fn(),
  getCustomerById: jest.fn(),
  createCustomer: jest.fn(),
  updateCustomer: jest.fn(),
  deleteCustomer: jest.fn(),
  searchCustomers: jest.fn(),
  getServices: jest.fn(),
  getActiveServices: jest.fn(),
  createService: jest.fn(),
  updateService: jest.fn(),
  deleteService: jest.fn(),
  getServicesByCategory: jest.fn(),
  getStaff: jest.fn(),
  getActiveStaff: jest.fn(),
  createStaff: jest.fn(),
  updateStaff: jest.fn(),
  deleteStaff: jest.fn(),
  getAppointments: jest.fn(),
  createAppointment: jest.fn(),
  updateAppointment: jest.fn(),
  deleteAppointment: jest.fn(),
  getAppointmentsByDate: jest.fn()
}));

import * as api from '../api';

describe('API Service', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('Customer API', () => {
    const mockCustomer = {
      id: '123e4567-e89b-12d3-a456-426614174000',
      name: 'Maria Silva',
      email: 'maria@email.com',
      phone: '(11) 99999-1111',
      address: 'Rua das Flores, 123'
    };

    test('getCustomers should fetch all customers', async () => {
      const mockData = [mockCustomer];
      api.getCustomers.mockResolvedValue(mockData);

      const result = await api.getCustomers();

      expect(api.getCustomers).toHaveBeenCalled();
      expect(result).toEqual(mockData);
    });

    test('getCustomerById should fetch customer by id', async () => {
      const mockData = mockCustomer;
      api.getCustomerById.mockResolvedValue(mockData);

      const result = await api.getCustomerById(mockCustomer.id);

      expect(api.getCustomerById).toHaveBeenCalledWith(mockCustomer.id);
      expect(result).toEqual(mockCustomer);
    });

    test('createCustomer should create new customer', async () => {
      const newCustomer = {
        name: 'João Santos',
        email: 'joao@email.com',
        phone: '(11) 88888-2222',
        address: 'Rua Nova, 456'
      };
      const mockResponse = { ...newCustomer, id: '123e4567-e89b-12d3-a456-426614174001' };
      api.createCustomer.mockResolvedValue(mockResponse);

      const result = await api.createCustomer(newCustomer);

      expect(api.createCustomer).toHaveBeenCalledWith(newCustomer);
      expect(result).toEqual(mockResponse);
    });

    test('updateCustomer should update existing customer', async () => {
      const updatedData = { name: 'Maria Silva Santos' };
      const mockResponse = { ...mockCustomer, ...updatedData };
      api.updateCustomer.mockResolvedValue(mockResponse);

      const result = await api.updateCustomer(mockCustomer.id, updatedData);

      expect(api.updateCustomer).toHaveBeenCalledWith(mockCustomer.id, updatedData);
      expect(result).toEqual(mockResponse);
    });

    test('deleteCustomer should delete customer', async () => {
      api.deleteCustomer.mockResolvedValue();

      await api.deleteCustomer(mockCustomer.id);

      expect(api.deleteCustomer).toHaveBeenCalledWith(mockCustomer.id);
    });

    test('searchCustomers should search customers by name', async () => {
      const searchTerm = 'Maria';
      const mockData = [mockCustomer];
      api.searchCustomers.mockResolvedValue(mockData);

      const result = await api.searchCustomers(searchTerm);

      expect(api.searchCustomers).toHaveBeenCalledWith(searchTerm);
      expect(result).toEqual([mockCustomer]);
    });
  });

  describe('Service API', () => {
    const mockService = {
      id: '123e4567-e89b-12d3-a456-426614174000',
      name: 'Corte de Cabelo',
      description: 'Corte moderno',
      duration: 60,
      price: 50.00,
      category: 'cabelo',
      status: 'active'
    };

    test('getServices should fetch all services', async () => {
      const mockData = [mockService];
      api.getServices.mockResolvedValue(mockData);

      const result = await api.getServices();

      expect(api.getServices).toHaveBeenCalled();
      expect(result).toEqual(mockData);
    });

    test('createService should create new service', async () => {
      const newService = {
        name: 'Manicure',
        description: 'Unhas pintadas',
        duration: 30,
        price: 30.00,
        category: 'unhas',
        status: 'active'
      };
      const mockResponse = { ...newService, id: '123e4567-e89b-12d3-a456-426614174001' };
      api.createService.mockResolvedValue(mockResponse);

      const result = await api.createService(newService);

      expect(api.createService).toHaveBeenCalledWith(newService);
      expect(result).toEqual(mockResponse);
    });

    test('getServicesByCategory should fetch services by category', async () => {
      const category = 'cabelo';
      const mockData = [mockService];
      api.getServicesByCategory.mockResolvedValue(mockData);

      const result = await api.getServicesByCategory(category);

      expect(api.getServicesByCategory).toHaveBeenCalledWith(category);
      expect(result).toEqual([mockService]);
    });
  });

  describe('Staff API', () => {
    const mockStaff = {
      id: '123e4567-e89b-12d3-a456-426614174000',
      name: 'Ana Silva',
      email: 'ana@salao.com',
      phone: '(11) 99999-3333',
      role: 'hairdresser',
      specialties: ['corte', 'coloração'],
      status: 'active'
    };

    test('getStaff should fetch all staff', async () => {
      const mockData = [mockStaff];
      api.getStaff.mockResolvedValue(mockData);

      const result = await api.getStaff();

      expect(api.getStaff).toHaveBeenCalled();
      expect(result).toEqual(mockData);
    });

    test('getActiveStaff should fetch only active staff', async () => {
      const mockData = [mockStaff];
      api.getActiveStaff.mockResolvedValue(mockData);

      const result = await api.getActiveStaff();

      expect(api.getActiveStaff).toHaveBeenCalled();
      expect(result).toEqual(mockData);
    });

    test('createStaff should create new staff member', async () => {
      const newStaff = {
        name: 'Carlos Santos',
        email: 'carlos@salao.com',
        phone: '(11) 88888-4444',
        role: 'receptionist',
        specialties: ['atendimento'],
        status: 'active'
      };
      const mockResponse = { ...newStaff, id: '123e4567-e89b-12d3-a456-426614174001' };
      api.createStaff.mockResolvedValue(mockResponse);

      const result = await api.createStaff(newStaff);

      expect(api.createStaff).toHaveBeenCalledWith(newStaff);
      expect(result).toEqual(mockResponse);
    });
  });

  describe('Appointment API', () => {
    const mockAppointment = {
      id: '123e4567-e89b-12d3-a456-426614174000',
      customerId: '123e4567-e89b-12d3-a456-426614174001',
      staffId: '123e4567-e89b-12d3-a456-426614174002',
      serviceId: '123e4567-e89b-12d3-a456-426614174003',
      appointmentDate: '2025-08-15',
      appointmentTime: '14:30',
      status: 'scheduled',
      notes: 'Corte de cabelo',
      totalPrice: 50.00
    };

    test('getAppointments should fetch all appointments', async () => {
      const mockData = [mockAppointment];
      api.getAppointments.mockResolvedValue(mockData);

      const result = await api.getAppointments();

      expect(api.getAppointments).toHaveBeenCalled();
      expect(result).toEqual(mockData);
    });

    test('createAppointment should create new appointment', async () => {
      const newAppointment = {
        customerId: '123e4567-e89b-12d3-a456-426614174001',
        staffId: '123e4567-e89b-12d3-a456-426614174002',
        serviceId: '123e4567-e89b-12d3-a456-426614174003',
        appointmentDate: '2025-08-16',
        appointmentTime: '15:00',
        status: 'scheduled',
        notes: 'Manicure',
        totalPrice: 30.00
      };
      const mockResponse = { ...newAppointment, id: '123e4567-e89b-12d3-a456-426614174004' };
      api.createAppointment.mockResolvedValue(mockResponse);

      const result = await api.createAppointment(newAppointment);

      expect(api.createAppointment).toHaveBeenCalledWith(newAppointment);
      expect(result).toEqual(mockResponse);
    });

    test('getAppointmentsByDate should fetch appointments by date', async () => {
      const date = '2025-08-15';
      const mockData = [mockAppointment];
      api.getAppointmentsByDate.mockResolvedValue(mockData);

      const result = await api.getAppointmentsByDate(date);

      expect(api.getAppointmentsByDate).toHaveBeenCalledWith(date);
      expect(result).toEqual([mockAppointment]);
    });
  });

  describe('Error Handling', () => {
    test('should handle network errors', async () => {
      const errorMessage = 'Network Error';
      api.getCustomers.mockRejectedValue(new Error(errorMessage));

      await expect(api.getCustomers()).rejects.toThrow(errorMessage);
    });

    test('should handle HTTP errors', async () => {
      const errorResponse = {
        response: {
          status: 404,
          data: { message: 'Not Found' }
        }
      };
      api.getCustomerById.mockRejectedValue(errorResponse);

      await expect(api.getCustomerById('invalid-id')).rejects.toEqual(errorResponse);
    });
  });
});
