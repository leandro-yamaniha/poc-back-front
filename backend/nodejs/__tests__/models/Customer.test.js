const Customer = require('../../src/models/Customer');

describe('Customer Model', () => {
  describe('Constructor', () => {
    test('should create customer with default values', () => {
      const customer = new Customer();
      
      expect(customer.id).toBeDefined();
      expect(customer.name).toBe('');
      expect(customer.email).toBe('');
      expect(customer.phone).toBe('');
      expect(customer.address).toBe('');
      expect(customer.created_at).toBeInstanceOf(Date);
      expect(customer.updated_at).toBeInstanceOf(Date);
    });

    test('should create customer with provided data', () => {
      const customerData = {
        id: '123e4567-e89b-12d3-a456-426614174000',
        name: 'João Silva',
        email: 'joao@email.com',
        phone: '11987654321',
        address: 'Rua das Flores, 123'
      };

      const customer = new Customer(customerData);
      
      expect(customer.id).toBe(customerData.id);
      expect(customer.name).toBe(customerData.name);
      expect(customer.email).toBe(customerData.email);
      expect(customer.phone).toBe(customerData.phone);
      expect(customer.address).toBe(customerData.address);
    });

    test('should generate UUID if no id provided', () => {
      const customer1 = new Customer();
      const customer2 = new Customer();
      
      expect(customer1.id).toBeDefined();
      expect(customer2.id).toBeDefined();
      expect(customer1.id).not.toBe(customer2.id);
    });
  });

  describe('getValidationRules', () => {
    test('should return validation rules array', () => {
      const rules = Customer.getValidationRules();
      
      expect(Array.isArray(rules)).toBe(true);
      expect(rules.length).toBe(4); // name, email, phone, address
    });
  });

  describe('validateRequest', () => {
    let mockReq, mockRes, mockNext;

    beforeEach(() => {
      mockReq = {};
      mockRes = {
        status: jest.fn().mockReturnThis(),
        json: jest.fn()
      };
      mockNext = jest.fn();
    });

    test('should call next() when validation passes', () => {
      // Mock validationResult to return no errors
      const mockValidationResult = require('express-validator').validationResult;
      jest.doMock('express-validator', () => ({
        ...jest.requireActual('express-validator'),
        validationResult: jest.fn(() => ({ isEmpty: () => true }))
      }));

      Customer.validateRequest(mockReq, mockRes, mockNext);
      
      expect(mockNext).toHaveBeenCalled();
      expect(mockRes.status).not.toHaveBeenCalled();
    });
  });

  describe('update', () => {
    test('should update customer fields', () => {
      const customer = new Customer({
        name: 'João Silva',
        email: 'joao@email.com'
      });

      const originalUpdatedAt = customer.updated_at;
      
      // Wait a bit to ensure timestamp difference
      setTimeout(() => {
        customer.update({
          name: 'João Santos',
          phone: '11987654321'
        });

        expect(customer.name).toBe('João Santos');
        expect(customer.phone).toBe('11987654321');
        expect(customer.email).toBe('joao@email.com'); // unchanged
        expect(customer.updated_at).not.toBe(originalUpdatedAt);
      }, 1);
    });

    test('should not update undefined fields', () => {
      const customer = new Customer({
        name: 'João Silva',
        email: 'joao@email.com'
      });

      customer.update({
        name: 'João Santos'
        // email not provided
      });

      expect(customer.name).toBe('João Santos');
      expect(customer.email).toBe('joao@email.com'); // unchanged
    });
  });

  describe('toPlainObject', () => {
    test('should convert customer to plain object', () => {
      const customerData = {
        id: '123e4567-e89b-12d3-a456-426614174000',
        name: 'João Silva',
        email: 'joao@email.com',
        phone: '11987654321',
        address: 'Rua das Flores, 123'
      };

      const customer = new Customer(customerData);
      const plainObject = customer.toPlainObject();

      expect(plainObject).toEqual({
        id: customer.id,
        name: customer.name,
        email: customer.email,
        phone: customer.phone,
        address: customer.address,
        created_at: customer.created_at,
        updated_at: customer.updated_at
      });
    });
  });

  describe('fromRow', () => {
    test('should create customer from database row', () => {
      const row = {
        id: '123e4567-e89b-12d3-a456-426614174000',
        name: 'João Silva',
        email: 'joao@email.com',
        phone: '11987654321',
        address: 'Rua das Flores, 123',
        created_at: new Date('2023-01-01'),
        updated_at: new Date('2023-01-02')
      };

      const customer = Customer.fromRow(row);

      expect(customer).toBeInstanceOf(Customer);
      expect(customer.id).toBe(row.id);
      expect(customer.name).toBe(row.name);
      expect(customer.email).toBe(row.email);
      expect(customer.phone).toBe(row.phone);
      expect(customer.address).toBe(row.address);
      expect(customer.created_at).toBe(row.created_at);
      expect(customer.updated_at).toBe(row.updated_at);
    });
  });
});
