const cassandraClient = require('../config/cassandra');
const fs = require('fs').promises;
const path = require('path');

class MigrationManager {
  constructor() {
    this.migrationsPath = path.join(__dirname, 'scripts');
    this.migrationTable = 'schema_migrations';
  }

  /**
   * Executa todas as migrations pendentes
   */
  async runMigrations() {
    try {
      console.log('🚀 Iniciando sistema de migrations...');
      
      // Primeiro, garante que o keyspace existe
      await this.ensureKeyspaceExists();
      
      // Cria a tabela de controle de migrations
      await this.createMigrationsTable();
      
      // Executa migrations pendentes
      await this.executePendingMigrations();
      
      console.log('✅ Todas as migrations foram executadas com sucesso!');
    } catch (error) {
      console.error('❌ Erro ao executar migrations:', error);
      throw error;
    }
  }

  /**
   * Garante que o keyspace existe
   */
  async ensureKeyspaceExists() {
    const keyspace = process.env.CASSANDRA_KEYSPACE || 'beauty_salon';
    
    console.log(`📊 Verificando keyspace: ${keyspace}`);
    
    const createKeyspaceQuery = `
      CREATE KEYSPACE IF NOT EXISTS ${keyspace}
      WITH REPLICATION = {
        'class': 'SimpleStrategy',
        'replication_factor': 1
      }
    `;
    
    try {
      await cassandraClient.execute(createKeyspaceQuery);
      console.log(`✅ Keyspace '${keyspace}' criado/verificado com sucesso`);
      
      // Muda para o keyspace
      await cassandraClient.execute(`USE ${keyspace}`);
    } catch (error) {
      console.error(`❌ Erro ao criar keyspace '${keyspace}':`, error);
      throw error;
    }
  }

  /**
   * Cria a tabela de controle de migrations
   */
  async createMigrationsTable() {
    const createTableQuery = `
      CREATE TABLE IF NOT EXISTS ${this.migrationTable} (
        version text PRIMARY KEY,
        name text,
        executed_at timestamp,
        checksum text
      )
    `;
    
    try {
      await cassandraClient.execute(createTableQuery);
      console.log(`✅ Tabela de controle '${this.migrationTable}' criada/verificada`);
    } catch (error) {
      console.error('❌ Erro ao criar tabela de migrations:', error);
      throw error;
    }
  }

  /**
   * Executa migrations pendentes
   */
  async executePendingMigrations() {
    const migrationFiles = await this.getMigrationFiles();
    const executedMigrations = await this.getExecutedMigrations();
    
    const pendingMigrations = migrationFiles.filter(
      file => !executedMigrations.includes(file.version)
    );
    
    if (pendingMigrations.length === 0) {
      console.log('ℹ️  Nenhuma migration pendente encontrada');
      return;
    }
    
    console.log(`🔄 Executando ${pendingMigrations.length} migration(s) pendente(s)...`);
    
    for (const migration of pendingMigrations) {
      await this.executeMigration(migration);
    }
  }

  /**
   * Obtém lista de arquivos de migration
   */
  async getMigrationFiles() {
    try {
      const files = await fs.readdir(this.migrationsPath);
      const migrationFiles = files
        .filter(file => file.endsWith('.js'))
        .sort()
        .map(file => {
          const version = file.replace('.js', '');
          return {
            version,
            filename: file,
            path: path.join(this.migrationsPath, file)
          };
        });
      
      return migrationFiles;
    } catch (error) {
      if (error.code === 'ENOENT') {
        console.log('ℹ️  Diretório de migrations não encontrado, criando...');
        await fs.mkdir(this.migrationsPath, { recursive: true });
        return [];
      }
      throw error;
    }
  }

  /**
   * Obtém migrations já executadas
   */
  async getExecutedMigrations() {
    try {
      const result = await cassandraClient.execute(
        `SELECT version FROM ${this.migrationTable}`
      );
      return result.rows.map(row => row.version);
    } catch (error) {
      console.log('ℹ️  Tabela de migrations ainda não existe ou está vazia');
      return [];
    }
  }

  /**
   * Executa uma migration específica
   */
  async executeMigration(migration) {
    console.log(`🔧 Executando migration: ${migration.version}`);
    
    try {
      // Carrega o módulo da migration
      const migrationModule = require(migration.path);
      
      // Executa a migration
      if (typeof migrationModule.up === 'function') {
        await migrationModule.up(cassandraClient);
      } else {
        throw new Error(`Migration ${migration.version} não possui método 'up'`);
      }
      
      // Registra a migration como executada
      await this.recordMigration(migration);
      
      console.log(`✅ Migration ${migration.version} executada com sucesso`);
    } catch (error) {
      console.error(`❌ Erro ao executar migration ${migration.version}:`, error);
      throw error;
    }
  }

  /**
   * Registra uma migration como executada
   */
  async recordMigration(migration) {
    const insertQuery = `
      INSERT INTO ${this.migrationTable} (version, name, executed_at, checksum)
      VALUES (?, ?, ?, ?)
    `;
    
    const checksum = await this.calculateChecksum(migration.path);
    
    await cassandraClient.execute(insertQuery, [
      migration.version,
      migration.filename,
      new Date(),
      checksum
    ]);
  }

  /**
   * Calcula checksum de um arquivo
   */
  async calculateChecksum(filePath) {
    const crypto = require('crypto');
    const content = await fs.readFile(filePath, 'utf8');
    return crypto.createHash('md5').update(content).digest('hex');
  }

  /**
   * Rollback da última migration
   */
  async rollbackLastMigration() {
    try {
      const executedMigrations = await this.getExecutedMigrations();
      
      if (executedMigrations.length === 0) {
        console.log('ℹ️  Nenhuma migration para fazer rollback');
        return;
      }
      
      const lastMigration = executedMigrations.sort().pop();
      console.log(`🔄 Fazendo rollback da migration: ${lastMigration}`);
      
      const migrationPath = path.join(this.migrationsPath, `${lastMigration}.js`);
      const migrationModule = require(migrationPath);
      
      if (typeof migrationModule.down === 'function') {
        await migrationModule.down(cassandraClient);
        
        // Remove o registro da migration
        await cassandraClient.execute(
          `DELETE FROM ${this.migrationTable} WHERE version = ?`,
          [lastMigration]
        );
        
        console.log(`✅ Rollback da migration ${lastMigration} executado com sucesso`);
      } else {
        console.log(`⚠️  Migration ${lastMigration} não possui método 'down' para rollback`);
      }
    } catch (error) {
      console.error('❌ Erro ao fazer rollback:', error);
      throw error;
    }
  }

  /**
   * Lista status das migrations
   */
  async getMigrationsStatus() {
    const migrationFiles = await this.getMigrationFiles();
    const executedMigrations = await this.getExecutedMigrations();
    
    return migrationFiles.map(file => ({
      version: file.version,
      filename: file.filename,
      executed: executedMigrations.includes(file.version),
      executedAt: null // TODO: buscar data de execução
    }));
  }
}

module.exports = MigrationManager;
