const CustomerController = require('../../src/controllers/CustomerController');
const CustomerService = require('../../src/services/CustomerService');

// Mock dependencies
jest.mock('../../src/services/CustomerService');

describe('CustomerController', () => {
  let mockReq, mockRes, mockNext;

  beforeEach(() => {
    mockReq = {
      params: {},
      query: {},
      body: {}
    };
    mockRes = {
      status: jest.fn().mockReturnThis(),
      json: jest.fn(),
      send: jest.fn()
    };
    mockNext = jest.fn();
    
    jest.clearAllMocks();
  });

  describe('getAllCustomers', () => {
    test('should return all customers successfully', async () => {
      const mockCustomers = [
        { id: '1', name: 'João Silva', email: 'joao@email.com' },
        { id: '2', name: 'Maria Santos', email: 'maria@email.com' }
      ];
      
      CustomerService.getAllCustomers.mockResolvedValue(mockCustomers);

      await CustomerController.getAllCustomers(mockReq, mockRes, mockNext);

      expect(CustomerService.getAllCustomers).toHaveBeenCalled();
      expect(mockRes.status).toHaveBeenCalledWith(200);
      expect(mockRes.json).toHaveBeenCalledWith(mockCustomers);
      expect(mockNext).not.toHaveBeenCalled();
    });

    test('should handle service errors', async () => {
      const error = new Error('Service error');
      CustomerService.getAllCustomers.mockRejectedValue(error);

      await CustomerController.getAllCustomers(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
      expect(mockRes.status).not.toHaveBeenCalled();
    });
  });

  describe('getCustomerById', () => {
    test('should return customer when found', async () => {
      const mockCustomer = { id: '1', name: 'João Silva', email: 'joao@email.com' };
      mockReq.params.id = '1';
      
      CustomerService.getCustomerById.mockResolvedValue(mockCustomer);

      await CustomerController.getCustomerById(mockReq, mockRes, mockNext);

      expect(CustomerService.getCustomerById).toHaveBeenCalledWith('1');
      expect(mockRes.status).toHaveBeenCalledWith(200);
      expect(mockRes.json).toHaveBeenCalledWith(mockCustomer);
    });

    test('should return 404 when customer not found', async () => {
      mockReq.params.id = '1';
      CustomerService.getCustomerById.mockResolvedValue(null);

      await CustomerController.getCustomerById(mockReq, mockRes, mockNext);

      expect(mockRes.status).toHaveBeenCalledWith(404);
      expect(mockRes.json).toHaveBeenCalledWith({
        error: 'Not Found',
        message: 'Cliente não encontrado',
        timestamp: expect.any(String)
      });
    });

    test('should handle service errors', async () => {
      const error = new Error('Service error');
      mockReq.params.id = '1';
      CustomerService.getCustomerById.mockRejectedValue(error);

      await CustomerController.getCustomerById(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
    });
  });

  describe('getCustomerByEmail', () => {
    test('should return customer when found by email', async () => {
      const mockCustomer = { id: '1', name: 'João Silva', email: 'joao@email.com' };
      mockReq.params.email = 'joao@email.com';
      
      CustomerService.getCustomerByEmail.mockResolvedValue(mockCustomer);

      await CustomerController.getCustomerByEmail(mockReq, mockRes, mockNext);

      expect(CustomerService.getCustomerByEmail).toHaveBeenCalledWith('joao@email.com');
      expect(mockRes.status).toHaveBeenCalledWith(200);
      expect(mockRes.json).toHaveBeenCalledWith(mockCustomer);
    });

    test('should return 404 when customer not found by email', async () => {
      mockReq.params.email = 'notfound@email.com';
      CustomerService.getCustomerByEmail.mockResolvedValue(null);

      await CustomerController.getCustomerByEmail(mockReq, mockRes, mockNext);

      expect(mockRes.status).toHaveBeenCalledWith(404);
      expect(mockRes.json).toHaveBeenCalledWith({
        error: 'Not Found',
        message: 'Cliente não encontrado',
        timestamp: expect.any(String)
      });
    });
  });

  describe('searchCustomers', () => {
    test('should search customers by name', async () => {
      const mockCustomers = [{ id: '1', name: 'João Silva' }];
      mockReq.query.name = 'João';
      
      CustomerService.searchCustomersByName.mockResolvedValue(mockCustomers);

      await CustomerController.searchCustomers(mockReq, mockRes, mockNext);

      expect(CustomerService.searchCustomersByName).toHaveBeenCalledWith('João');
      expect(mockRes.status).toHaveBeenCalledWith(200);
      expect(mockRes.json).toHaveBeenCalledWith(mockCustomers);
    });

    test('should return 400 when name parameter is missing', async () => {
      mockReq.query = {}; // No name parameter

      await CustomerController.searchCustomers(mockReq, mockRes, mockNext);

      expect(mockRes.status).toHaveBeenCalledWith(400);
      expect(mockRes.json).toHaveBeenCalledWith({
        error: 'Bad Request',
        message: 'Parâmetro "name" é obrigatório',
        timestamp: expect.any(String)
      });
      expect(CustomerService.searchCustomersByName).not.toHaveBeenCalled();
    });

    test('should handle service errors', async () => {
      const error = new Error('Search error');
      mockReq.query.name = 'João';
      CustomerService.searchCustomersByName.mockRejectedValue(error);

      await CustomerController.searchCustomers(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
    });
  });

  describe('createCustomer', () => {
    test('should create customer successfully', async () => {
      const customerData = { name: 'João Silva', email: 'joao@email.com' };
      const createdCustomer = { id: '1', ...customerData };
      mockReq.body = customerData;
      
      CustomerService.createCustomer.mockResolvedValue(createdCustomer);

      await CustomerController.createCustomer(mockReq, mockRes, mockNext);

      expect(CustomerService.createCustomer).toHaveBeenCalledWith(customerData);
      expect(mockRes.status).toHaveBeenCalledWith(201);
      expect(mockRes.json).toHaveBeenCalledWith(createdCustomer);
    });

    test('should handle service errors', async () => {
      const error = new Error('Creation error');
      mockReq.body = { name: 'João Silva', email: 'joao@email.com' };
      CustomerService.createCustomer.mockRejectedValue(error);

      await CustomerController.createCustomer(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
    });
  });

  describe('updateCustomer', () => {
    test('should update customer successfully', async () => {
      const updateData = { name: 'João Santos' };
      const updatedCustomer = { id: '1', name: 'João Santos', email: 'joao@email.com' };
      mockReq.params.id = '1';
      mockReq.body = updateData;
      
      CustomerService.updateCustomer.mockResolvedValue(updatedCustomer);

      await CustomerController.updateCustomer(mockReq, mockRes, mockNext);

      expect(CustomerService.updateCustomer).toHaveBeenCalledWith('1', updateData);
      expect(mockRes.status).toHaveBeenCalledWith(200);
      expect(mockRes.json).toHaveBeenCalledWith(updatedCustomer);
    });

    test('should return 404 when customer not found for update', async () => {
      mockReq.params.id = '1';
      mockReq.body = { name: 'João Santos' };
      CustomerService.updateCustomer.mockResolvedValue(null);

      await CustomerController.updateCustomer(mockReq, mockRes, mockNext);

      expect(mockRes.status).toHaveBeenCalledWith(404);
      expect(mockRes.json).toHaveBeenCalledWith({
        error: 'Not Found',
        message: 'Cliente não encontrado',
        timestamp: expect.any(String)
      });
    });

    test('should handle service errors', async () => {
      const error = new Error('Update error');
      mockReq.params.id = '1';
      mockReq.body = { name: 'João Santos' };
      CustomerService.updateCustomer.mockRejectedValue(error);

      await CustomerController.updateCustomer(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
    });
  });

  describe('deleteCustomer', () => {
    test('should delete customer successfully', async () => {
      mockReq.params.id = '1';
      CustomerService.deleteCustomer.mockResolvedValue(true);

      await CustomerController.deleteCustomer(mockReq, mockRes, mockNext);

      expect(CustomerService.deleteCustomer).toHaveBeenCalledWith('1');
      expect(mockRes.status).toHaveBeenCalledWith(204);
      expect(mockRes.send).toHaveBeenCalled();
    });

    test('should return 404 when customer not found for deletion', async () => {
      mockReq.params.id = '1';
      CustomerService.deleteCustomer.mockResolvedValue(false);

      await CustomerController.deleteCustomer(mockReq, mockRes, mockNext);

      expect(mockRes.status).toHaveBeenCalledWith(404);
      expect(mockRes.json).toHaveBeenCalledWith({
        error: 'Not Found',
        message: 'Cliente não encontrado',
        timestamp: expect.any(String)
      });
    });

    test('should handle service errors', async () => {
      const error = new Error('Delete error');
      mockReq.params.id = '1';
      CustomerService.deleteCustomer.mockRejectedValue(error);

      await CustomerController.deleteCustomer(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
    });
  });

  describe('getCustomerCount', () => {
    test('should return customer count', async () => {
      const mockCount = 25;
      CustomerService.getCustomerCount.mockResolvedValue(mockCount);

      await CustomerController.getCustomerCount(mockReq, mockRes, mockNext);

      expect(CustomerService.getCustomerCount).toHaveBeenCalled();
      expect(mockRes.status).toHaveBeenCalledWith(200);
      expect(mockRes.json).toHaveBeenCalledWith({ count: mockCount });
    });

    test('should handle service errors', async () => {
      const error = new Error('Count error');
      CustomerService.getCustomerCount.mockRejectedValue(error);

      await CustomerController.getCustomerCount(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
    });
  });
});
