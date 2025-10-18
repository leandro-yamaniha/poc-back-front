const CustomerRepository = require('../../src/repositories/CustomerRepository');
const Customer = require('../../src/models/Customer');

// Mock dependencies
jest.mock('../../src/config/cassandra', () => ({
  execute: jest.fn()
}));
jest.mock('../../src/models/Customer');

describe('CustomerRepository', () => {
  let cassandraClient;

  beforeEach(() => {
    cassandraClient = require('../../src/config/cassandra');
    jest.clearAllMocks();
  });

  describe('findAll', () => {
    test('should return all customers', async () => {
      const mockRows = [
        { id: '1', name: 'João Silva', email: 'joao@email.com' },
        { id: '2', name: 'Maria Santos', email: 'maria@email.com' }
      ];
      const mockResult = { rows: mockRows };
      
      cassandraClient.execute.mockResolvedValue(mockResult);
      Customer.fromRow.mockImplementation(row => ({ ...row, fromRow: true }));

      const result = await CustomerRepository.findAll();

      expect(cassandraClient.execute).toHaveBeenCalledWith('SELECT * FROM customers');
      expect(Customer.fromRow).toHaveBeenCalledTimes(2);
      expect(result).toHaveLength(2);
      expect(result[0].fromRow).toBe(true);
    });

    test('should handle database errors', async () => {
      const error = new Error('Database error');
      cassandraClient.execute.mockRejectedValue(error);

      await expect(CustomerRepository.findAll()).rejects.toThrow('Database error');
    });
  });

  describe('findById', () => {
    test('should return customer when found', async () => {
      const customerId = '123e4567-e89b-12d3-a456-426614174000';
      const mockRow = { id: customerId, name: 'João Silva', email: 'joao@email.com' };
      const mockResult = { rows: [mockRow] };
      
      cassandraClient.execute.mockResolvedValue(mockResult);
      Customer.fromRow.mockReturnValue({ ...mockRow, fromRow: true });

      const result = await CustomerRepository.findById(customerId);

      expect(cassandraClient.execute).toHaveBeenCalledWith(
        'SELECT * FROM customers WHERE id = ?',
        [customerId]
      );
      expect(Customer.fromRow).toHaveBeenCalledWith(mockRow);
      expect(result.fromRow).toBe(true);
    });

    test('should return null when customer not found', async () => {
      const customerId = '123e4567-e89b-12d3-a456-426614174000';
      const mockResult = { rows: [] };
      
      cassandraClient.execute.mockResolvedValue(mockResult);

      const result = await CustomerRepository.findById(customerId);

      expect(result).toBeNull();
      expect(Customer.fromRow).not.toHaveBeenCalled();
    });
  });

  describe('findByEmail', () => {
    test('should return customer when found by email', async () => {
      const email = 'joao@email.com';
      const mockRow = { id: '1', name: 'João Silva', email };
      const mockResult = { rows: [mockRow] };
      
      cassandraClient.execute.mockResolvedValue(mockResult);
      Customer.fromRow.mockReturnValue({ ...mockRow, fromRow: true });

      const result = await CustomerRepository.findByEmail(email);

      expect(cassandraClient.execute).toHaveBeenCalledWith(
        'SELECT * FROM customers WHERE email = ? ALLOW FILTERING',
        [email]
      );
      expect(result.fromRow).toBe(true);
    });

    test('should return null when customer not found by email', async () => {
      const email = 'notfound@email.com';
      const mockResult = { rows: [] };
      
      cassandraClient.execute.mockResolvedValue(mockResult);

      const result = await CustomerRepository.findByEmail(email);

      expect(result).toBeNull();
    });
  });

  describe('searchByName', () => {
    test('should search customers by name', async () => {
      const searchName = 'João';
      const mockRows = [
        { id: '1', name: 'João Silva', email: 'joao@email.com' },
        { id: '2', name: 'João Santos', email: 'joao.santos@email.com' }
      ];
      const mockResult = { rows: mockRows };
      
      cassandraClient.execute.mockResolvedValue(mockResult);
      Customer.fromRow.mockImplementation(row => ({ ...row, fromRow: true }));

      const result = await CustomerRepository.searchByName(searchName);

      expect(cassandraClient.execute).toHaveBeenCalledWith(
        'SELECT * FROM customers ALLOW FILTERING'
      );
      expect(result).toHaveLength(2);
    });
  });

  describe('save', () => {
    test('should save customer successfully', async () => {
      const mockCustomer = {
        id: '1',
        name: 'João Silva',
        email: 'joao@email.com',
        phone: '11987654321',
        address: 'Rua das Flores, 123',
        created_at: new Date(),
        updated_at: new Date(),
        toPlainObject: jest.fn().mockReturnValue({
          id: '1',
          name: 'João Silva',
          email: 'joao@email.com',
          phone: '11987654321',
          address: 'Rua das Flores, 123',
          created_at: new Date(),
          updated_at: new Date()
        })
      };
      
      cassandraClient.execute.mockResolvedValue({ rows: [] });

      const result = await CustomerRepository.save(mockCustomer);

      expect(cassandraClient.execute).toHaveBeenCalledWith(
        expect.stringContaining('INSERT INTO customers'),
        expect.any(Array)
      );
      expect(result).toBe(mockCustomer);
    });
  });

  describe('update', () => {
    test('should update customer successfully', async () => {
      const customerId = '1';
      const mockCustomer = {
        name: 'João Santos',
        email: 'joao.santos@email.com',
        phone: '11987654321',
        address: 'Rua das Flores, 123',
        updated_at: new Date(),
        toPlainObject: jest.fn().mockReturnValue({
          name: 'João Santos',
          email: 'joao.santos@email.com',
          phone: '11987654321',
          address: 'Rua das Flores, 123',
          updated_at: new Date()
        })
      };
      
      cassandraClient.execute.mockResolvedValue({ rows: [] });

      const result = await CustomerRepository.update(customerId, mockCustomer);

      expect(cassandraClient.execute).toHaveBeenCalledWith(
        expect.stringContaining('UPDATE customers'),
        expect.any(Array)
      );
      expect(result).toBe(mockCustomer);
    });
  });

  describe('deleteById', () => {
    test('should delete customer successfully', async () => {
      const customerId = '1';
      cassandraClient.execute.mockResolvedValue({ rows: [] });

      const result = await CustomerRepository.deleteById(customerId);

      expect(cassandraClient.execute).toHaveBeenCalledWith(
        'DELETE FROM customers WHERE id = ?',
        [customerId]
      );
      expect(result).toBe(true);
    });

    test('should handle delete errors', async () => {
      const customerId = '1';
      const error = new Error('Delete error');
      cassandraClient.execute.mockRejectedValue(error);

      await expect(CustomerRepository.deleteById(customerId)).rejects.toThrow('Delete error');
    });
  });

  describe('count', () => {
    test('should return customer count', async () => {
      const mockResult = { rows: [{ count: 25 }] };
      cassandraClient.execute.mockResolvedValue(mockResult);

      const result = await CustomerRepository.count();

      expect(cassandraClient.execute).toHaveBeenCalledWith('SELECT COUNT(*) as count FROM customers');
      expect(result).toBe(25);
    });

    test('should return 0 when no customers', async () => {
      const mockResult = { rows: [{ count: 0 }] };
      cassandraClient.execute.mockResolvedValue(mockResult);

      const result = await CustomerRepository.count();

      expect(result).toBe(0);
    });
  });
});
