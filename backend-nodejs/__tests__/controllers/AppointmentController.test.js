const AppointmentController = require('../../src/controllers/AppointmentController');
const AppointmentService = require('../../src/services/AppointmentService');

// Mock dependencies
jest.mock('../../src/services/AppointmentService');

describe('AppointmentController', () => {
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

  describe('getAllAppointments', () => {
    test('should return all appointments successfully', async () => {
      const mockAppointments = [
        { id: '1', customer_id: 'c1', staff_id: 's1', service_id: 'sv1', appointment_date: '2025-01-15T10:00:00Z', status: 'scheduled' },
        { id: '2', customer_id: 'c2', staff_id: 's2', service_id: 'sv2', appointment_date: '2025-01-16T14:00:00Z', status: 'completed' }
      ];
      
      AppointmentService.getAllAppointments.mockResolvedValue(mockAppointments);

      await AppointmentController.getAllAppointments(mockReq, mockRes, mockNext);

      expect(AppointmentService.getAllAppointments).toHaveBeenCalled();
      expect(mockRes.status).toHaveBeenCalledWith(200);
      expect(mockRes.json).toHaveBeenCalledWith(mockAppointments);
      expect(mockNext).not.toHaveBeenCalled();
    });

    test('should handle service errors', async () => {
      const error = new Error('Service error');
      AppointmentService.getAllAppointments.mockRejectedValue(error);

      await AppointmentController.getAllAppointments(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
      expect(mockRes.status).not.toHaveBeenCalled();
    });
  });

  describe('getAppointmentById', () => {
    test('should return appointment when found', async () => {
      const mockAppointment = { id: '1', customer_id: 'c1', staff_id: 's1', service_id: 'sv1', appointment_date: '2025-01-15T10:00:00Z', status: 'scheduled' };
      mockReq.params.id = '1';
      
      AppointmentService.getAppointmentById.mockResolvedValue(mockAppointment);

      await AppointmentController.getAppointmentById(mockReq, mockRes, mockNext);

      expect(AppointmentService.getAppointmentById).toHaveBeenCalledWith('1');
      expect(mockRes.status).toHaveBeenCalledWith(200);
      expect(mockRes.json).toHaveBeenCalledWith(mockAppointment);
    });

    test('should return 404 when appointment not found', async () => {
      mockReq.params.id = '1';
      AppointmentService.getAppointmentById.mockResolvedValue(null);

      await AppointmentController.getAppointmentById(mockReq, mockRes, mockNext);

      expect(mockRes.status).toHaveBeenCalledWith(404);
      expect(mockRes.json).toHaveBeenCalledWith({
        error: 'Not Found',
        message: 'Agendamento não encontrado',
        timestamp: expect.any(String)
      });
    });

    test('should handle service errors', async () => {
      const error = new Error('Service error');
      mockReq.params.id = '1';
      AppointmentService.getAppointmentById.mockRejectedValue(error);

      await AppointmentController.getAppointmentById(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
    });
  });

  describe('getAppointmentsByStaffId', () => {
    test('should return appointments by staff ID successfully', async () => {
      const mockAppointments = [
        { id: '1', customer_id: 'c1', staff_id: 's1', service_id: 'sv1', appointment_date: '2025-01-15T10:00:00Z', status: 'scheduled' },
        { id: '2', customer_id: 'c2', staff_id: 's1', service_id: 'sv2', appointment_date: '2025-01-16T14:00:00Z', status: 'completed' }
      ];
      mockReq.params.staffId = 's1';
      
      AppointmentService.getAppointmentsByStaffId.mockResolvedValue(mockAppointments);

      await AppointmentController.getAppointmentsByStaffId(mockReq, mockRes, mockNext);

      expect(AppointmentService.getAppointmentsByStaffId).toHaveBeenCalledWith('s1');
      expect(mockRes.status).toHaveBeenCalledWith(200);
      expect(mockRes.json).toHaveBeenCalledWith(mockAppointments);
    });

    test('should return empty array when no appointments found for staff', async () => {
      mockReq.params.staffId = 's999';
      
      AppointmentService.getAppointmentsByStaffId.mockResolvedValue([]);

      await AppointmentController.getAppointmentsByStaffId(mockReq, mockRes, mockNext);

      expect(AppointmentService.getAppointmentsByStaffId).toHaveBeenCalledWith('s999');
      expect(mockRes.status).toHaveBeenCalledWith(200);
      expect(mockRes.json).toHaveBeenCalledWith([]);
    });

    test('should handle service errors', async () => {
      const error = new Error('Service error');
      mockReq.params.staffId = 's1';
      AppointmentService.getAppointmentsByStaffId.mockRejectedValue(error);

      await AppointmentController.getAppointmentsByStaffId(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
    });
  });

  describe('getAppointmentsByDateAndStaff', () => {
    test('should return appointments by date and staff successfully', async () => {
      const mockAppointments = [
        { id: '1', customer_id: 'c1', staff_id: 's1', service_id: 'sv1', appointment_date: '2025-01-15T10:00:00Z', status: 'scheduled' },
        { id: '2', customer_id: 'c2', staff_id: 's1', service_id: 'sv2', appointment_date: '2025-01-15T14:00:00Z', status: 'scheduled' }
      ];
      mockReq.params.date = '2025-01-15';
      mockReq.params.staffId = 's1';
      
      AppointmentService.getAppointmentsByDateAndStaff.mockResolvedValue(mockAppointments);

      await AppointmentController.getAppointmentsByDateAndStaff(mockReq, mockRes, mockNext);

      expect(AppointmentService.getAppointmentsByDateAndStaff).toHaveBeenCalledWith('2025-01-15', 's1');
      expect(mockRes.status).toHaveBeenCalledWith(200);
      expect(mockRes.json).toHaveBeenCalledWith(mockAppointments);
    });

    test('should return empty array when no appointments found for date and staff', async () => {
      mockReq.params.date = '2025-01-20';
      mockReq.params.staffId = 's1';
      
      AppointmentService.getAppointmentsByDateAndStaff.mockResolvedValue([]);

      await AppointmentController.getAppointmentsByDateAndStaff(mockReq, mockRes, mockNext);

      expect(AppointmentService.getAppointmentsByDateAndStaff).toHaveBeenCalledWith('2025-01-20', 's1');
      expect(mockRes.status).toHaveBeenCalledWith(200);
      expect(mockRes.json).toHaveBeenCalledWith([]);
    });

    test('should handle service errors', async () => {
      const error = new Error('Service error');
      mockReq.params.date = '2025-01-15';
      mockReq.params.staffId = 's1';
      AppointmentService.getAppointmentsByDateAndStaff.mockRejectedValue(error);

      await AppointmentController.getAppointmentsByDateAndStaff(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
    });
  });

  describe('createAppointment', () => {
    test('should create appointment successfully', async () => {
      const appointmentData = { customer_id: 'c1', staff_id: 's1', service_id: 'sv1', appointment_date: '2025-01-15T10:00:00Z' };
      const createdAppointment = { id: '1', ...appointmentData, status: 'scheduled' };
      mockReq.body = appointmentData;
      
      AppointmentService.createAppointment.mockResolvedValue(createdAppointment);

      await AppointmentController.createAppointment(mockReq, mockRes, mockNext);

      expect(AppointmentService.createAppointment).toHaveBeenCalledWith(appointmentData);
      expect(mockRes.status).toHaveBeenCalledWith(201);
      expect(mockRes.json).toHaveBeenCalledWith(createdAppointment);
    });

    test('should handle service errors', async () => {
      const error = new Error('Creation error');
      mockReq.body = { customer_id: 'c1', staff_id: 's1', service_id: 'sv1', appointment_date: '2025-01-15T10:00:00Z' };
      AppointmentService.createAppointment.mockRejectedValue(error);

      await AppointmentController.createAppointment(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
    });
  });

  describe('updateAppointment', () => {
    test('should update appointment successfully', async () => {
      const updateData = { status: 'completed' };
      const updatedAppointment = { id: '1', customer_id: 'c1', staff_id: 's1', service_id: 'sv1', appointment_date: '2025-01-15T10:00:00Z', status: 'completed' };
      mockReq.params.id = '1';
      mockReq.body = updateData;
      
      AppointmentService.updateAppointment.mockResolvedValue(updatedAppointment);

      await AppointmentController.updateAppointment(mockReq, mockRes, mockNext);

      expect(AppointmentService.updateAppointment).toHaveBeenCalledWith('1', updateData);
      expect(mockRes.status).toHaveBeenCalledWith(200);
      expect(mockRes.json).toHaveBeenCalledWith(updatedAppointment);
    });

    test('should return 404 when appointment not found for update', async () => {
      mockReq.params.id = '1';
      mockReq.body = { status: 'completed' };
      AppointmentService.updateAppointment.mockResolvedValue(null);

      await AppointmentController.updateAppointment(mockReq, mockRes, mockNext);

      expect(mockRes.status).toHaveBeenCalledWith(404);
      expect(mockRes.json).toHaveBeenCalledWith({
        error: 'Not Found',
        message: 'Agendamento não encontrado',
        timestamp: expect.any(String)
      });
    });

    test('should handle service errors', async () => {
      const error = new Error('Update error');
      mockReq.params.id = '1';
      mockReq.body = { status: 'completed' };
      AppointmentService.updateAppointment.mockRejectedValue(error);

      await AppointmentController.updateAppointment(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
    });
  });

  describe('deleteAppointment', () => {
    test('should delete appointment successfully', async () => {
      mockReq.params.id = '1';
      AppointmentService.deleteAppointment.mockResolvedValue(true);

      await AppointmentController.deleteAppointment(mockReq, mockRes, mockNext);

      expect(AppointmentService.deleteAppointment).toHaveBeenCalledWith('1');
      expect(mockRes.status).toHaveBeenCalledWith(204);
      expect(mockRes.send).toHaveBeenCalled();
    });

    test('should return 404 when appointment not found for deletion', async () => {
      mockReq.params.id = '1';
      AppointmentService.deleteAppointment.mockResolvedValue(false);

      await AppointmentController.deleteAppointment(mockReq, mockRes, mockNext);

      expect(mockRes.status).toHaveBeenCalledWith(404);
      expect(mockRes.json).toHaveBeenCalledWith({
        error: 'Not Found',
        message: 'Agendamento não encontrado',
        timestamp: expect.any(String)
      });
    });

    test('should handle service errors', async () => {
      const error = new Error('Delete error');
      mockReq.params.id = '1';
      AppointmentService.deleteAppointment.mockRejectedValue(error);

      await AppointmentController.deleteAppointment(mockReq, mockRes, mockNext);

      expect(mockNext).toHaveBeenCalledWith(error);
    });
  });
});
