const cassandraConfig = require('../../src/config/cassandra');

// Mock cassandra-driver
jest.mock('cassandra-driver', () => ({
  Client: jest.fn().mockImplementation(() => ({
    connect: jest.fn(),
    execute: jest.fn(),
    shutdown: jest.fn()
  })),
  types: {
    distance: {
      local: 'local',
      remote: 'remote'
    }
  },
  auth: {
    PlainTextAuthProvider: jest.fn()
  }
}));

describe('Cassandra Configuration', () => {
  let mockClient;

  beforeEach(() => {
    jest.clearAllMocks();
    
    // Reset environment variables
    process.env.CASSANDRA_CONTACT_POINTS = 'localhost';
    process.env.CASSANDRA_PORT = '9042';
    process.env.CASSANDRA_LOCAL_DATACENTER = 'datacenter1';
    process.env.CASSANDRA_USERNAME = '';
    process.env.CASSANDRA_PASSWORD = '';
    
    // Mock console methods
    console.log = jest.fn();
    console.error = jest.fn();
  });

  describe('getClient', () => {
    test('should return cassandra client instance', () => {
      const client = cassandraConfig.getClient();
      expect(client).toBeDefined();
    });

    test('should return same client instance on multiple calls', () => {
      const client1 = cassandraConfig.getClient();
      const client2 = cassandraConfig.getClient();
      expect(client1).toBe(client2);
    });
  });

  describe('connect', () => {
    test('should connect to cassandra successfully', async () => {
      const mockClient = {
        connect: jest.fn().mockResolvedValue(),
        keyspace: null
      };
      
      // Mock the cassandra Client constructor
      const cassandra = require('cassandra-driver');
      cassandra.Client.mockImplementation(() => mockClient);
      
      // Reset connection state
      cassandraConfig.client = null;
      cassandraConfig.isConnected = false;

      await cassandraConfig.connect();

      expect(mockClient.connect).toHaveBeenCalled();
      expect(cassandraConfig.isConnected).toBe(true);
      expect(console.log).toHaveBeenCalledWith(expect.stringContaining('✅ Connected to Cassandra cluster'));
    });

    test('should handle connection errors', async () => {
      const error = new Error('Connection failed');
      const mockClient = {
        connect: jest.fn().mockRejectedValue(error)
      };
      
      const cassandra = require('cassandra-driver');
      cassandra.Client.mockImplementation(() => mockClient);
      
      // Reset connection state
      cassandraConfig.client = null;
      cassandraConfig.isConnected = false;

      await expect(cassandraConfig.connect()).rejects.toThrow('Connection failed');
      expect(console.error).toHaveBeenCalledWith('❌ Failed to connect to Cassandra:', error);
    });

    test('should return existing client if already connected', async () => {
      const mockClient = { connect: jest.fn() };
      cassandraConfig.client = mockClient;
      cassandraConfig.isConnected = true;

      const result = await cassandraConfig.connect();

      expect(result).toBe(mockClient);
      expect(mockClient.connect).not.toHaveBeenCalled();
    });
  });

  describe('useKeyspace', () => {
    test('should use keyspace successfully', async () => {
      const keyspace = 'beauty_salon';
      const mockClient = {
        execute: jest.fn().mockResolvedValue()
      };
      
      cassandraConfig.client = mockClient;
      cassandraConfig.isConnected = true;

      await cassandraConfig.useKeyspace(keyspace);

      expect(mockClient.execute).toHaveBeenCalledWith(`USE ${keyspace}`);
      expect(console.log).toHaveBeenCalledWith(`📊 Switched to keyspace: ${keyspace}`);
    });

    test('should handle keyspace use errors', async () => {
      const keyspace = 'beauty_salon';
      const error = new Error('Keyspace not found');
      const mockClient = {
        execute: jest.fn().mockRejectedValue(error)
      };
      
      cassandraConfig.client = mockClient;
      cassandraConfig.isConnected = true;

      await expect(cassandraConfig.useKeyspace(keyspace)).rejects.toThrow('Keyspace not found');
      expect(console.error).toHaveBeenCalledWith(`❌ Failed to use keyspace ${keyspace}:`, error);
    });
  });

  describe('shutdown', () => {
    test('should shutdown cassandra connection successfully', async () => {
      const mockClient = {
        shutdown: jest.fn().mockResolvedValue()
      };
      
      cassandraConfig.client = mockClient;
      cassandraConfig.isConnected = true;

      await cassandraConfig.shutdown();

      expect(mockClient.shutdown).toHaveBeenCalled();
      expect(cassandraConfig.isConnected).toBe(false);
      expect(console.log).toHaveBeenCalledWith('✅ Cassandra connection closed');
    });

    test('should handle shutdown when not connected', async () => {
      cassandraConfig.client = null;
      cassandraConfig.isConnected = false;

      await cassandraConfig.shutdown();

      // Should not throw error and should not call shutdown
      expect(console.log).not.toHaveBeenCalledWith('✅ Cassandra connection closed');
    });
  });

  describe('environment configuration', () => {
    test('should use environment variables for configuration', () => {
      process.env.CASSANDRA_CONTACT_POINTS = 'test-host';
      process.env.CASSANDRA_PORT = '9999';
      process.env.CASSANDRA_LOCAL_DATACENTER = 'test-dc';
      
      // Force reload of the module to pick up new env vars
      jest.resetModules();
      const cassandraConfigReloaded = require('../../src/config/cassandra');
      
      const client = cassandraConfigReloaded.getClient();
      expect(client).toBeDefined();
    });

    test('should use default values when environment variables are not set', () => {
      delete process.env.CASSANDRA_CONTACT_POINTS;
      delete process.env.CASSANDRA_PORT;
      delete process.env.CASSANDRA_LOCAL_DATACENTER;
      
      jest.resetModules();
      const cassandraConfigReloaded = require('../../src/config/cassandra');
      
      const client = cassandraConfigReloaded.getClient();
      expect(client).toBeDefined();
    });
  });
});
