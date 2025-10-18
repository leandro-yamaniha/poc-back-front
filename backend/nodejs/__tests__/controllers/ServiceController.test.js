const ServiceController = require('../../src/controllers/ServiceController');
const ServiceService = require('../../src/services/ServiceService');

// Mock dependencies
jest.mock('../../src/services/ServiceService');

describe('ServiceController', () => {
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

  describe('getAllServices', () => {
    test('should return all services successfully', async () => {
      const mockServices = [
        { id: '1', name: 'Haircut', category: 'Hair', price: 50.00 },
        { id: '2', name: 'Manicure', category: 'Nails', price: 30.00 }
      ];
      
      ServiceService.getAllServices.mockResolvedValue(mockServices);

      await ServiceController.getAllServices(mockReq, mockRes, mockNext);

      expect(ServiceService.getAllServices).toHaveBeenCalled();
      expect(mockRes.status).toHaveBeenCalledWith(200);
      expect(mockRes.json).toHaveBeenCalledWith(mockServices);
      expect(mockNext).not.toHaveBeenCalled();
    });

    test('should handle service errors', async () => {
      const error = new Error('Service error');
      ServiceService.getAllServices.mockRejectedValue(error);

      await ServiceController.getAllServices(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
      expect(mockRes.status).not.toHaveBeenCalled();
    });
  });

  describe('getServiceById', () => {
    test('should return service when found', async () => {
      const mockService = { id: '1', name: 'Haircut', category: 'Hair', price: 50.00 };
      mockReq.params.id = '1';
      
      ServiceService.getServiceById.mockResolvedValue(mockService);

      await ServiceController.getServiceById(mockReq, mockRes, mockNext);

      expect(ServiceService.getServiceById).toHaveBeenCalledWith('1');
      expect(mockRes.status).toHaveBeenCalledWith(200);
      expect(mockRes.json).toHaveBeenCalledWith(mockService);
    });

    test('should return 404 when service not found', async () => {
      mockReq.params.id = '1';
      ServiceService.getServiceById.mockResolvedValue(null);

      await ServiceController.getServiceById(mockReq, mockRes, mockNext);

      expect(mockRes.status).toHaveBeenCalledWith(404);
      expect(mockRes.json).toHaveBeenCalledWith({
        error: 'Not Found',
        message: 'Serviço não encontrado',
        timestamp: expect.any(String)
      });
    });

    test('should handle service errors', async () => {
      const error = new Error('Service error');
      mockReq.params.id = '1';
      ServiceService.getServiceById.mockRejectedValue(error);

      await ServiceController.getServiceById(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
    });
  });

  describe('getServicesByCategory', () => {
    test('should return services by category successfully', async () => {
      const mockServices = [
        { id: '1', name: 'Haircut', category: 'Hair', price: 50.00 },
        { id: '2', name: 'Hair Styling', category: 'Hair', price: 40.00 }
      ];
      mockReq.params.category = 'Hair';
      
      ServiceService.getServicesByCategory.mockResolvedValue(mockServices);

      await ServiceController.getServicesByCategory(mockReq, mockRes, mockNext);

      expect(ServiceService.getServicesByCategory).toHaveBeenCalledWith('Hair');
      expect(mockRes.status).toHaveBeenCalledWith(200);
      expect(mockRes.json).toHaveBeenCalledWith(mockServices);
    });

    test('should handle service errors', async () => {
      const error = new Error('Service error');
      mockReq.params.category = 'Hair';
      ServiceService.getServicesByCategory.mockRejectedValue(error);

      await ServiceController.getServicesByCategory(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
    });
  });

  describe('getActiveServicesByCategory', () => {
    test('should return active services by category successfully', async () => {
      const mockServices = [
        { id: '1', name: 'Haircut', category: 'Hair', price: 50.00, is_active: true },
        { id: '2', name: 'Hair Styling', category: 'Hair', price: 40.00, is_active: true }
      ];
      mockReq.params.category = 'Hair';
      
      ServiceService.getActiveServicesByCategory.mockResolvedValue(mockServices);

      await ServiceController.getActiveServicesByCategory(mockReq, mockRes, mockNext);

      expect(ServiceService.getActiveServicesByCategory).toHaveBeenCalledWith('Hair');
      expect(mockRes.status).toHaveBeenCalledWith(200);
      expect(mockRes.json).toHaveBeenCalledWith(mockServices);
    });

    test('should return empty array when no active services found', async () => {
      mockReq.params.category = 'NonExistent';
      
      ServiceService.getActiveServicesByCategory.mockResolvedValue([]);

      await ServiceController.getActiveServicesByCategory(mockReq, mockRes, mockNext);

      expect(ServiceService.getActiveServicesByCategory).toHaveBeenCalledWith('NonExistent');
      expect(mockRes.status).toHaveBeenCalledWith(200);
      expect(mockRes.json).toHaveBeenCalledWith([]);
    });

    test('should handle service errors', async () => {
      const error = new Error('Service error');
      mockReq.params.category = 'Hair';
      ServiceService.getActiveServicesByCategory.mockRejectedValue(error);

      await ServiceController.getActiveServicesByCategory(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
    });
  });

  describe('createService', () => {
    test('should create service successfully', async () => {
      const serviceData = { name: 'New Service', category: 'Hair', price: 60.00 };
      const createdService = { id: '1', ...serviceData };
      mockReq.body = serviceData;
      
      ServiceService.createService.mockResolvedValue(createdService);

      await ServiceController.createService(mockReq, mockRes, mockNext);

      expect(ServiceService.createService).toHaveBeenCalledWith(serviceData);
      expect(mockRes.status).toHaveBeenCalledWith(201);
      expect(mockRes.json).toHaveBeenCalledWith(createdService);
    });

    test('should handle service errors', async () => {
      const error = new Error('Creation error');
      mockReq.body = { name: 'New Service', category: 'Hair', price: 60.00 };
      ServiceService.createService.mockRejectedValue(error);

      await ServiceController.createService(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
    });
  });

  describe('updateService', () => {
    test('should update service successfully', async () => {
      const updateData = { name: 'Updated Service' };
      const updatedService = { id: '1', name: 'Updated Service', category: 'Hair', price: 60.00 };
      mockReq.params.id = '1';
      mockReq.body = updateData;
      
      ServiceService.updateService.mockResolvedValue(updatedService);

      await ServiceController.updateService(mockReq, mockRes, mockNext);

      expect(ServiceService.updateService).toHaveBeenCalledWith('1', updateData);
      expect(mockRes.status).toHaveBeenCalledWith(200);
      expect(mockRes.json).toHaveBeenCalledWith(updatedService);
    });

    test('should return 404 when service not found for update', async () => {
      mockReq.params.id = '1';
      mockReq.body = { name: 'Updated Service' };
      ServiceService.updateService.mockResolvedValue(null);

      await ServiceController.updateService(mockReq, mockRes, mockNext);

      expect(mockRes.status).toHaveBeenCalledWith(404);
      expect(mockRes.json).toHaveBeenCalledWith({
        error: 'Not Found',
        message: 'Serviço não encontrado',
        timestamp: expect.any(String)
      });
    });

    test('should handle service errors', async () => {
      const error = new Error('Update error');
      mockReq.params.id = '1';
      mockReq.body = { name: 'Updated Service' };
      ServiceService.updateService.mockRejectedValue(error);

      await ServiceController.updateService(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
    });
  });

  describe('deleteService', () => {
    test('should delete service successfully', async () => {
      mockReq.params.id = '1';
      ServiceService.deleteService.mockResolvedValue(true);

      await ServiceController.deleteService(mockReq, mockRes, mockNext);

      expect(ServiceService.deleteService).toHaveBeenCalledWith('1');
      expect(mockRes.status).toHaveBeenCalledWith(204);
      expect(mockRes.send).toHaveBeenCalled();
    });

    test('should return 404 when service not found for deletion', async () => {
      mockReq.params.id = '1';
      ServiceService.deleteService.mockResolvedValue(false);

      await ServiceController.deleteService(mockReq, mockRes, mockNext);

      expect(mockRes.status).toHaveBeenCalledWith(404);
      expect(mockRes.json).toHaveBeenCalledWith({
        error: 'Not Found',
        message: 'Serviço não encontrado',
        timestamp: expect.any(String)
      });
    });

    test('should handle service errors', async () => {
      const error = new Error('Delete error');
      mockReq.params.id = '1';
      ServiceService.deleteService.mockRejectedValue(error);

      await ServiceController.deleteService(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
    });
  });
});
