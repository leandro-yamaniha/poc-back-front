const CustomerService = require('../../src/services/CustomerService');
const CustomerRepository = require('../../src/repositories/CustomerRepository');
const Customer = require('../../src/models/Customer');

// Mock dependencies
jest.mock('../../src/repositories/CustomerRepository');
jest.mock('../../src/models/Customer');
jest.mock('node-cache');

describe('CustomerService', () => {
  let mockCache;

  beforeEach(() => {
    // Reset all mocks
    jest.clearAllMocks();
    
    // Mock NodeCache
    mockCache = {
      get: jest.fn(),
      set: jest.fn(),
      del: jest.fn(),
      keys: jest.fn(() => [])
    };
    
    // Mock the cache instance
    CustomerService.cache = mockCache;
  });

  describe('getAllCustomers', () => {
    test('should return cached customers if available', async () => {
      const mockCustomers = [
        { id: '1', name: 'João Silva', email: 'joao@email.com' },
        { id: '2', name: 'Maria Santos', email: 'maria@email.com' }
      ];
      
      mockCache.get.mockReturnValue(mockCustomers);

      const result = await CustomerService.getAllCustomers();

      expect(mockCache.get).toHaveBeenCalledWith('customers:all');
      expect(CustomerRepository.findAll).not.toHaveBeenCalled();
      expect(result).toEqual(mockCustomers);
    });

    test('should fetch from repository and cache if not cached', async () => {
      const mockCustomers = [
        { id: '1', name: 'João Silva', email: 'joao@email.com' }
      ];
      
      mockCache.get.mockReturnValue(null);
      CustomerRepository.findAll.mockResolvedValue(mockCustomers);

      const result = await CustomerService.getAllCustomers();

      expect(mockCache.get).toHaveBeenCalledWith('customers:all');
      expect(CustomerRepository.findAll).toHaveBeenCalled();
      expect(mockCache.set).toHaveBeenCalledWith('customers:all', mockCustomers);
      expect(result).toEqual(mockCustomers);
    });
  });

  describe('getCustomerById', () => {
    test('should return cached customer if available', async () => {
      const mockCustomer = { id: '1', name: 'João Silva', email: 'joao@email.com' };
      const customerId = '1';
      
      mockCache.get.mockReturnValue(mockCustomer);

      const result = await CustomerService.getCustomerById(customerId);

      expect(mockCache.get).toHaveBeenCalledWith(`customer:${customerId}`);
      expect(CustomerRepository.findById).not.toHaveBeenCalled();
      expect(result).toEqual(mockCustomer);
    });

    test('should fetch from repository and cache if not cached', async () => {
      const mockCustomer = { id: '1', name: 'João Silva', email: 'joao@email.com' };
      const customerId = '1';
      
      mockCache.get.mockReturnValue(null);
      CustomerRepository.findById.mockResolvedValue(mockCustomer);

      const result = await CustomerService.getCustomerById(customerId);

      expect(mockCache.get).toHaveBeenCalledWith(`customer:${customerId}`);
      expect(CustomerRepository.findById).toHaveBeenCalledWith(customerId);
      expect(mockCache.set).toHaveBeenCalledWith(`customer:${customerId}`, mockCustomer);
      expect(result).toEqual(mockCustomer);
    });

    test('should not cache if customer not found', async () => {
      const customerId = '1';
      
      mockCache.get.mockReturnValue(null);
      CustomerRepository.findById.mockResolvedValue(null);

      const result = await CustomerService.getCustomerById(customerId);

      expect(CustomerRepository.findById).toHaveBeenCalledWith(customerId);
      expect(mockCache.set).not.toHaveBeenCalled();
      expect(result).toBeNull();
    });
  });

  describe('getCustomerByEmail', () => {
    test('should return cached customer if available', async () => {
      const mockCustomer = { id: '1', name: 'João Silva', email: 'joao@email.com' };
      const email = 'joao@email.com';
      
      mockCache.get.mockReturnValue(mockCustomer);

      const result = await CustomerService.getCustomerByEmail(email);

      expect(mockCache.get).toHaveBeenCalledWith(`customer:email:${email}`);
      expect(result).toEqual(mockCustomer);
    });

    test('should fetch from repository if not cached', async () => {
      const mockCustomer = { id: '1', name: 'João Silva', email: 'joao@email.com' };
      const email = 'joao@email.com';
      
      mockCache.get.mockReturnValue(null);
      CustomerRepository.findByEmail.mockResolvedValue(mockCustomer);

      const result = await CustomerService.getCustomerByEmail(email);

      expect(CustomerRepository.findByEmail).toHaveBeenCalledWith(email);
      expect(mockCache.set).toHaveBeenCalledWith(`customer:email:${email}`, mockCustomer);
      expect(result).toEqual(mockCustomer);
    });
  });

  describe('searchCustomersByName', () => {
    test('should throw error for short search term', async () => {
      await expect(CustomerService.searchCustomersByName('a')).rejects.toThrow(
        'Nome deve ter pelo menos 2 caracteres para busca'
      );
    });

    test('should throw error for empty search term', async () => {
      await expect(CustomerService.searchCustomersByName('')).rejects.toThrow(
        'Nome deve ter pelo menos 2 caracteres para busca'
      );
    });

    test('should return cached results if available', async () => {
      const mockCustomers = [{ id: '1', name: 'João Silva' }];
      const searchName = 'João';
      
      mockCache.get.mockReturnValue(mockCustomers);

      const result = await CustomerService.searchCustomersByName(searchName);

      expect(mockCache.get).toHaveBeenCalledWith(`customers:search:${searchName.toLowerCase()}`);
      expect(result).toEqual(mockCustomers);
    });

    test('should search repository and cache results', async () => {
      const mockCustomers = [{ id: '1', name: 'João Silva' }];
      const searchName = 'João';
      
      mockCache.get.mockReturnValue(null);
      CustomerRepository.searchByName.mockResolvedValue(mockCustomers);

      const result = await CustomerService.searchCustomersByName(searchName);

      expect(CustomerRepository.searchByName).toHaveBeenCalledWith(searchName);
      expect(mockCache.set).toHaveBeenCalledWith(`customers:search:${searchName.toLowerCase()}`, mockCustomers, 300);
      expect(result).toEqual(mockCustomers);
    });
  });

  describe('createCustomer', () => {
    test('should create customer successfully', async () => {
      const customerData = { name: 'João Silva', email: 'joao@email.com' };
      const mockCustomer = { id: '1', ...customerData };
      
      CustomerRepository.findByEmail.mockResolvedValue(null);
      Customer.mockImplementation(() => mockCustomer);
      CustomerRepository.save.mockResolvedValue(mockCustomer);

      const result = await CustomerService.createCustomer(customerData);

      expect(CustomerRepository.findByEmail).toHaveBeenCalledWith(customerData.email);
      expect(CustomerRepository.save).toHaveBeenCalledWith(mockCustomer);
      expect(mockCache.del).toHaveBeenCalledWith('customers:all');
      expect(mockCache.set).toHaveBeenCalledWith(`customer:${mockCustomer.id}`, mockCustomer);
      expect(result).toEqual(mockCustomer);
    });

    test('should throw error if email already exists', async () => {
      const customerData = { name: 'João Silva', email: 'joao@email.com' };
      const existingCustomer = { id: '2', email: 'joao@email.com' };
      
      CustomerRepository.findByEmail.mockResolvedValue(existingCustomer);

      await expect(CustomerService.createCustomer(customerData)).rejects.toThrow(
        'Email já está em uso por outro cliente'
      );
    });
  });

  describe('updateCustomer', () => {
    test('should return null if customer not found', async () => {
      const customerId = '1';
      const updateData = { name: 'João Santos' };
      
      CustomerRepository.findById.mockResolvedValue(null);

      const result = await CustomerService.updateCustomer(customerId, updateData);

      expect(result).toBeNull();
    });

    test('should update customer successfully', async () => {
      const customerId = '1';
      const updateData = { name: 'João Santos' };
      const existingCustomer = { 
        id: customerId, 
        name: 'João Silva', 
        email: 'joao@email.com',
        update: jest.fn()
      };
      const updatedCustomer = { ...existingCustomer, ...updateData };
      
      CustomerRepository.findById.mockResolvedValue(existingCustomer);
      CustomerRepository.update.mockResolvedValue(updatedCustomer);

      const result = await CustomerService.updateCustomer(customerId, updateData);

      expect(existingCustomer.update).toHaveBeenCalledWith(updateData);
      expect(CustomerRepository.update).toHaveBeenCalledWith(customerId, existingCustomer);
      expect(mockCache.del).toHaveBeenCalledWith('customers:all');
      expect(mockCache.del).toHaveBeenCalledWith(`customer:${customerId}`);
      expect(result).toEqual(updatedCustomer);
    });

    test('should throw error if new email already exists', async () => {
      const customerId = '1';
      const updateData = { email: 'maria@email.com' };
      const existingCustomer = { id: customerId, email: 'joao@email.com' };
      const emailOwner = { id: '2', email: 'maria@email.com' };
      
      CustomerRepository.findById.mockResolvedValue(existingCustomer);
      CustomerRepository.findByEmail.mockResolvedValue(emailOwner);

      await expect(CustomerService.updateCustomer(customerId, updateData)).rejects.toThrow(
        'Email já está em uso por outro cliente'
      );
    });
  });

  describe('deleteCustomer', () => {
    test('should return false if customer not found', async () => {
      const customerId = '1';
      
      CustomerRepository.findById.mockResolvedValue(null);

      const result = await CustomerService.deleteCustomer(customerId);

      expect(result).toBe(false);
      expect(CustomerRepository.deleteById).not.toHaveBeenCalled();
    });

    test('should delete customer successfully', async () => {
      const customerId = '1';
      const existingCustomer = { id: customerId, email: 'joao@email.com' };
      
      CustomerRepository.findById.mockResolvedValue(existingCustomer);
      CustomerRepository.deleteById.mockResolvedValue(true);

      const result = await CustomerService.deleteCustomer(customerId);

      expect(CustomerRepository.deleteById).toHaveBeenCalledWith(customerId);
      expect(mockCache.del).toHaveBeenCalledWith('customers:all');
      expect(mockCache.del).toHaveBeenCalledWith(`customer:${customerId}`);
      expect(mockCache.del).toHaveBeenCalledWith(`customer:email:${existingCustomer.email}`);
      expect(result).toBe(true);
    });
  });

  describe('customerExists', () => {
    test('should return true if customer exists', async () => {
      const customerId = '1';
      const mockCustomer = { id: customerId };
      
      // Mock getCustomerById method
      jest.spyOn(CustomerService, 'getCustomerById').mockResolvedValue(mockCustomer);

      const result = await CustomerService.customerExists(customerId);

      expect(result).toBe(true);
    });

    test('should return false if customer does not exist', async () => {
      const customerId = '1';
      
      jest.spyOn(CustomerService, 'getCustomerById').mockResolvedValue(null);

      const result = await CustomerService.customerExists(customerId);

      expect(result).toBe(false);
    });
  });

  describe('getCustomerCount', () => {
    test('should return cached count if available', async () => {
      const mockCount = 10;
      
      mockCache.get.mockReturnValue(mockCount);

      const result = await CustomerService.getCustomerCount();

      expect(mockCache.get).toHaveBeenCalledWith('customers:count');
      expect(result).toBe(mockCount);
    });

    test('should fetch count from repository and cache', async () => {
      const mockCount = 15;
      
      mockCache.get.mockReturnValue(undefined);
      CustomerRepository.count.mockResolvedValue(mockCount);

      const result = await CustomerService.getCustomerCount();

      expect(CustomerRepository.count).toHaveBeenCalled();
      expect(mockCache.set).toHaveBeenCalledWith('customers:count', mockCount, 300);
      expect(result).toBe(mockCount);
    });
  });

  describe('clearCache', () => {
    test('should clear all customer-related cache keys', () => {
      const mockKeys = [
        'customer:1',
        'customer:email:joao@email.com',
        'customers:all',
        'other:key'
      ];
      
      mockCache.keys.mockReturnValue(mockKeys);

      CustomerService.clearCache();

      expect(mockCache.del).toHaveBeenCalledWith('customer:1');
      expect(mockCache.del).toHaveBeenCalledWith('customer:email:joao@email.com');
      expect(mockCache.del).toHaveBeenCalledWith('customers:all');
      expect(mockCache.del).not.toHaveBeenCalledWith('other:key');
    });
  });
});
