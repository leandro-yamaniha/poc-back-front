const StaffController = require('../../src/controllers/StaffController');
const StaffService = require('../../src/services/StaffService');

// Mock dependencies
jest.mock('../../src/services/StaffService');

describe('StaffController', () => {
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

  describe('getAllStaff', () => {
    test('should return all staff successfully', async () => {
      const mockStaff = [
        { id: '1', name: 'João Silva', role: 'Stylist', is_active: true },
        { id: '2', name: 'Maria Santos', role: 'Manager', is_active: true }
      ];
      
      StaffService.getAllStaff.mockResolvedValue(mockStaff);

      await StaffController.getAllStaff(mockReq, mockRes, mockNext);

      expect(StaffService.getAllStaff).toHaveBeenCalled();
      expect(mockRes.status).toHaveBeenCalledWith(200);
      expect(mockRes.json).toHaveBeenCalledWith(mockStaff);
      expect(mockNext).not.toHaveBeenCalled();
    });

    test('should handle service errors', async () => {
      const error = new Error('Service error');
      StaffService.getAllStaff.mockRejectedValue(error);

      await StaffController.getAllStaff(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
      expect(mockRes.status).not.toHaveBeenCalled();
    });
  });

  describe('getStaffById', () => {
    test('should return staff when found', async () => {
      const mockStaff = { id: '1', name: 'João Silva', role: 'Stylist', is_active: true };
      mockReq.params.id = '1';
      
      StaffService.getStaffById.mockResolvedValue(mockStaff);

      await StaffController.getStaffById(mockReq, mockRes, mockNext);

      expect(StaffService.getStaffById).toHaveBeenCalledWith('1');
      expect(mockRes.status).toHaveBeenCalledWith(200);
      expect(mockRes.json).toHaveBeenCalledWith(mockStaff);
    });

    test('should return 404 when staff not found', async () => {
      mockReq.params.id = '1';
      StaffService.getStaffById.mockResolvedValue(null);

      await StaffController.getStaffById(mockReq, mockRes, mockNext);

      expect(mockRes.status).toHaveBeenCalledWith(404);
      expect(mockRes.json).toHaveBeenCalledWith({
        error: 'Not Found',
        message: 'Funcionário não encontrado',
        timestamp: expect.any(String)
      });
    });

    test('should handle service errors', async () => {
      const error = new Error('Service error');
      mockReq.params.id = '1';
      StaffService.getStaffById.mockRejectedValue(error);

      await StaffController.getStaffById(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
    });
  });

  describe('getStaffByRole', () => {
    test('should return staff by role successfully', async () => {
      const mockStaff = [
        { id: '1', name: 'João Silva', role: 'Stylist', is_active: true },
        { id: '2', name: 'Pedro Santos', role: 'Stylist', is_active: false }
      ];
      mockReq.params.role = 'Stylist';
      
      StaffService.getStaffByRole.mockResolvedValue(mockStaff);

      await StaffController.getStaffByRole(mockReq, mockRes, mockNext);

      expect(StaffService.getStaffByRole).toHaveBeenCalledWith('Stylist');
      expect(mockRes.status).toHaveBeenCalledWith(200);
      expect(mockRes.json).toHaveBeenCalledWith(mockStaff);
    });

    test('should return empty array when no staff found for role', async () => {
      mockReq.params.role = 'NonExistentRole';
      
      StaffService.getStaffByRole.mockResolvedValue([]);

      await StaffController.getStaffByRole(mockReq, mockRes, mockNext);

      expect(StaffService.getStaffByRole).toHaveBeenCalledWith('NonExistentRole');
      expect(mockRes.status).toHaveBeenCalledWith(200);
      expect(mockRes.json).toHaveBeenCalledWith([]);
    });

    test('should handle service errors', async () => {
      const error = new Error('Service error');
      mockReq.params.role = 'Stylist';
      StaffService.getStaffByRole.mockRejectedValue(error);

      await StaffController.getStaffByRole(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
    });
  });

  describe('getActiveStaffByRole', () => {
    test('should return active staff by role successfully', async () => {
      const mockStaff = [
        { id: '1', name: 'João Silva', role: 'Stylist', is_active: true },
        { id: '3', name: 'Ana Costa', role: 'Stylist', is_active: true }
      ];
      mockReq.params.role = 'Stylist';
      
      StaffService.getActiveStaffByRole.mockResolvedValue(mockStaff);

      await StaffController.getActiveStaffByRole(mockReq, mockRes, mockNext);

      expect(StaffService.getActiveStaffByRole).toHaveBeenCalledWith('Stylist');
      expect(mockRes.status).toHaveBeenCalledWith(200);
      expect(mockRes.json).toHaveBeenCalledWith(mockStaff);
    });

    test('should return empty array when no active staff found for role', async () => {
      mockReq.params.role = 'Manager';
      
      StaffService.getActiveStaffByRole.mockResolvedValue([]);

      await StaffController.getActiveStaffByRole(mockReq, mockRes, mockNext);

      expect(StaffService.getActiveStaffByRole).toHaveBeenCalledWith('Manager');
      expect(mockRes.status).toHaveBeenCalledWith(200);
      expect(mockRes.json).toHaveBeenCalledWith([]);
    });

    test('should handle service errors', async () => {
      const error = new Error('Service error');
      mockReq.params.role = 'Stylist';
      StaffService.getActiveStaffByRole.mockRejectedValue(error);

      await StaffController.getActiveStaffByRole(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
    });
  });

  describe('createStaff', () => {
    test('should create staff successfully', async () => {
      const staffData = { name: 'New Staff', role: 'Stylist', email: 'new@email.com' };
      const createdStaff = { id: '1', ...staffData };
      mockReq.body = staffData;
      
      StaffService.createStaff.mockResolvedValue(createdStaff);

      await StaffController.createStaff(mockReq, mockRes, mockNext);

      expect(StaffService.createStaff).toHaveBeenCalledWith(staffData);
      expect(mockRes.status).toHaveBeenCalledWith(201);
      expect(mockRes.json).toHaveBeenCalledWith(createdStaff);
    });

    test('should handle service errors', async () => {
      const error = new Error('Creation error');
      mockReq.body = { name: 'New Staff', role: 'Stylist', email: 'new@email.com' };
      StaffService.createStaff.mockRejectedValue(error);

      await StaffController.createStaff(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
    });
  });

  describe('updateStaff', () => {
    test('should update staff successfully', async () => {
      const updateData = { name: 'Updated Staff' };
      const updatedStaff = { id: '1', name: 'Updated Staff', role: 'Stylist', email: 'updated@email.com' };
      mockReq.params.id = '1';
      mockReq.body = updateData;
      
      StaffService.updateStaff.mockResolvedValue(updatedStaff);

      await StaffController.updateStaff(mockReq, mockRes, mockNext);

      expect(StaffService.updateStaff).toHaveBeenCalledWith('1', updateData);
      expect(mockRes.status).toHaveBeenCalledWith(200);
      expect(mockRes.json).toHaveBeenCalledWith(updatedStaff);
    });

    test('should return 404 when staff not found for update', async () => {
      mockReq.params.id = '1';
      mockReq.body = { name: 'Updated Staff' };
      StaffService.updateStaff.mockResolvedValue(null);

      await StaffController.updateStaff(mockReq, mockRes, mockNext);

      expect(mockRes.status).toHaveBeenCalledWith(404);
      expect(mockRes.json).toHaveBeenCalledWith({
        error: 'Not Found',
        message: 'Funcionário não encontrado',
        timestamp: expect.any(String)
      });
    });

    test('should handle service errors', async () => {
      const error = new Error('Update error');
      mockReq.params.id = '1';
      mockReq.body = { name: 'Updated Staff' };
      StaffService.updateStaff.mockRejectedValue(error);

      await StaffController.updateStaff(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
    });
  });

  describe('deleteStaff', () => {
    test('should delete staff successfully', async () => {
      mockReq.params.id = '1';
      StaffService.deleteStaff.mockResolvedValue(true);

      await StaffController.deleteStaff(mockReq, mockRes, mockNext);

      expect(StaffService.deleteStaff).toHaveBeenCalledWith('1');
      expect(mockRes.status).toHaveBeenCalledWith(204);
      expect(mockRes.send).toHaveBeenCalled();
    });

    test('should return 404 when staff not found for deletion', async () => {
      mockReq.params.id = '1';
      StaffService.deleteStaff.mockResolvedValue(false);

      await StaffController.deleteStaff(mockReq, mockRes, mockNext);

      expect(mockRes.status).toHaveBeenCalledWith(404);
      expect(mockRes.json).toHaveBeenCalledWith({
        error: 'Not Found',
        message: 'Funcionário não encontrado',
        timestamp: expect.any(String)
      });
    });

    test('should handle service errors', async () => {
      const error = new Error('Delete error');
      mockReq.params.id = '1';
      StaffService.deleteStaff.mockRejectedValue(error);

      await StaffController.deleteStaff(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
    });
  });
});
