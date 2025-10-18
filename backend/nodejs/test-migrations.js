/**
 * Test script for database migrations
 * This script tests the migration system independently
 */

require('dotenv').config();
const cassandraClient = require('./src/config/cassandra');
const MigrationManager = require('./src/migrations/MigrationManager');

async function testMigrations() {
  console.log('🧪 Testing Migration System');
  console.log('=' .repeat(50));
  
  try {
    // Connect to Cassandra
    console.log('🔌 Connecting to Cassandra...');
    await cassandraClient.connect();
    console.log('✅ Connected to Cassandra');
    
    // Initialize migration manager
    const migrationManager = new MigrationManager(cassandraClient);
    
    // Test migration status check
    console.log('\n📋 Checking migration status...');
    const executedMigrations = await migrationManager.getExecutedMigrations();
    console.log(`📊 Found ${executedMigrations.length} executed migrations`);
    
    if (executedMigrations.length > 0) {
      console.log('📝 Executed migrations:');
      executedMigrations.forEach(migration => {
        console.log(`  - ${migration.version}: ${migration.name} (${migration.executed_at})`);
      });
    }
    
    // Test running migrations
    console.log('\n🔄 Running migrations...');
    await migrationManager.runMigrations();
    console.log('✅ Migrations completed successfully');
    
    // Verify tables were created
    console.log('\n🔍 Verifying tables...');
    const tables = ['customers', 'services', 'staff', 'appointments'];
    
    for (const table of tables) {
      try {
        const result = await cassandraClient.execute(`SELECT COUNT(*) as count FROM ${table}`);
        const count = result.rows[0].count.toNumber();
        console.log(`✅ Table '${table}': ${count} records`);
      } catch (error) {
        console.log(`❌ Table '${table}': Error - ${error.message}`);
      }
    }
    
    // Test sample data
    console.log('\n📊 Sample data verification:');
    try {
      const customerResult = await cassandraClient.execute('SELECT name, email FROM customers LIMIT 3');
      console.log('👥 Sample customers:');
      customerResult.rows.forEach(row => {
        console.log(`  - ${row.name} (${row.email})`);
      });
      
      const serviceResult = await cassandraClient.execute('SELECT name, category, price FROM services LIMIT 3');
      console.log('💼 Sample services:');
      serviceResult.rows.forEach(row => {
        console.log(`  - ${row.name} (${row.category}) - R$ ${row.price}`);
      });
      
      const staffResult = await cassandraClient.execute('SELECT name, role FROM staff LIMIT 3');
      console.log('👨‍💼 Sample staff:');
      staffResult.rows.forEach(row => {
        console.log(`  - ${row.name} (${row.role})`);
      });
      
    } catch (error) {
      console.log(`⚠️ Sample data verification failed: ${error.message}`);
    }
    
    console.log('\n🎉 Migration test completed successfully!');
    
  } catch (error) {
    console.error('❌ Migration test failed:', error);
    process.exit(1);
  } finally {
    // Cleanup
    try {
      await cassandraClient.shutdown();
      console.log('✅ Cassandra connection closed');
    } catch (error) {
      console.error('⚠️ Error closing connection:', error);
    }
  }
}

// Run the test
if (require.main === module) {
  testMigrations();
}

module.exports = testMigrations;
