// Jest setup file
// Global test configuration and mocks

// Mock console methods to reduce noise in tests
global.console = {
  ...console,
  log: jest.fn(),
  debug: jest.fn(),
  info: jest.fn(),
  warn: jest.fn(),
  error: jest.fn(),
};

// Mock environment variables
process.env.NODE_ENV = 'test';
process.env.PORT = '3001';
process.env.CASSANDRA_CONTACT_POINTS = 'localhost';
process.env.CASSANDRA_PORT = '9042';
process.env.CASSANDRA_KEYSPACE = 'beauty_salon_test';
process.env.CASSANDRA_LOCAL_DATACENTER = 'datacenter1';

// Global test timeout
jest.setTimeout(10000);
