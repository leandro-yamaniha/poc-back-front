const cassandra = require('cassandra-driver');

class CassandraClient {
  constructor() {
    this.client = null;
    this.isConnected = false;
  }

  async connect() {
    if (this.isConnected) {
      return this.client;
    }

    const contactPoints = process.env.CASSANDRA_CONTACT_POINTS?.split(',') || ['localhost'];
    const port = parseInt(process.env.CASSANDRA_PORT) || 9042;
    const keyspace = process.env.CASSANDRA_KEYSPACE || 'beauty_salon';
    const localDataCenter = process.env.CASSANDRA_LOCAL_DATACENTER || 'datacenter1';

    const clientOptions = {
      contactPoints: contactPoints,
      localDataCenter: localDataCenter,
      keyspace: keyspace,
      pooling: {
        maxRequestsPerConnection: 1024,
        coreConnectionsPerHost: {
          [cassandra.types.distance.local]: 2,
          [cassandra.types.distance.remote]: 1
        }
      },
      socketOptions: {
        connectTimeout: 5000,
        readTimeout: 12000
      }
    };

    // Add authentication if provided
    if (process.env.CASSANDRA_USERNAME && process.env.CASSANDRA_PASSWORD) {
      clientOptions.authProvider = new cassandra.auth.PlainTextAuthProvider(
        process.env.CASSANDRA_USERNAME,
        process.env.CASSANDRA_PASSWORD
      );
    }

    this.client = new cassandra.Client(clientOptions);

    try {
      await this.client.connect();
      this.isConnected = true;
      console.log(`✅ Connected to Cassandra cluster at ${contactPoints.join(', ')}:${port}`);
      console.log(`📊 Using keyspace: ${keyspace}`);
      return this.client;
    } catch (error) {
      console.error('❌ Failed to connect to Cassandra:', error);
      throw error;
    }
  }

  async execute(query, params = [], options = {}) {
    if (!this.isConnected) {
      await this.connect();
    }

    try {
      const result = await this.client.execute(query, params, options);
      return result;
    } catch (error) {
      console.error('❌ Cassandra query error:', error);
      throw error;
    }
  }

  async shutdown() {
    if (this.client && this.isConnected) {
      await this.client.shutdown();
      this.isConnected = false;
      console.log('✅ Cassandra connection closed');
    }
  }

  getClient() {
    return this.client;
  }

  isClientConnected() {
    return this.isConnected;
  }
}

// Export singleton instance
const cassandraClient = new CassandraClient();
module.exports = cassandraClient;
