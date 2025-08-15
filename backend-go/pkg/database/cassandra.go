package database

import (
	"fmt"
	"log"
	"os"
	"strings"
	"time"

	"github.com/gocql/gocql"
)

// CassandraConfig holds Cassandra connection configuration
type CassandraConfig struct {
	Hosts      []string
	Keyspace   string
	Username   string
	Password   string
	Datacenter string
}

// CassandraDB wraps the gocql session
type CassandraDB struct {
	Session *gocql.Session
	Config  *CassandraConfig
}

// NewCassandraDB creates a new Cassandra database connection
func NewCassandraDB(config *CassandraConfig) (*CassandraDB, error) {
	cluster := gocql.NewCluster(config.Hosts...)
	cluster.Keyspace = config.Keyspace
	cluster.Consistency = gocql.Quorum
	cluster.Timeout = 10 * time.Second
	cluster.ConnectTimeout = 10 * time.Second
	cluster.RetryPolicy = &gocql.SimpleRetryPolicy{NumRetries: 3}

	if config.Username != "" && config.Password != "" {
		cluster.Authenticator = gocql.PasswordAuthenticator{
			Username: config.Username,
			Password: config.Password,
		}
	}

	if config.Datacenter != "" {
		cluster.PoolConfig.HostSelectionPolicy = gocql.DCAwareRoundRobinPolicy(config.Datacenter)
	}

	session, err := cluster.CreateSession()
	if err != nil {
		return nil, fmt.Errorf("failed to connect to Cassandra: %w", err)
	}

	db := &CassandraDB{
		Session: session,
		Config:  config,
	}

	// Test connection
	if err := db.Ping(); err != nil {
		session.Close()
		return nil, fmt.Errorf("failed to ping Cassandra: %w", err)
	}

	log.Printf("Successfully connected to Cassandra cluster: %v", config.Hosts)
	return db, nil
}

// NewCassandraDBFromEnv creates a new Cassandra connection from environment variables
func NewCassandraDBFromEnv() (*CassandraDB, error) {
	hostsStr := os.Getenv("CASSANDRA_HOSTS")
	if hostsStr == "" {
		hostsStr = "localhost:9042"
	}
	hosts := strings.Split(hostsStr, ",")

	config := &CassandraConfig{
		Hosts:      hosts,
		Keyspace:   getEnvOrDefault("CASSANDRA_KEYSPACE", "beauty_salon"),
		Username:   os.Getenv("CASSANDRA_USERNAME"),
		Password:   os.Getenv("CASSANDRA_PASSWORD"),
		Datacenter: getEnvOrDefault("CASSANDRA_DATACENTER", "datacenter1"),
	}

	return NewCassandraDB(config)
}

// Ping tests the database connection
func (db *CassandraDB) Ping() error {
	return db.Session.Query("SELECT now() FROM system.local").Exec()
}

// Close closes the database connection
func (db *CassandraDB) Close() {
	if db.Session != nil {
		db.Session.Close()
		log.Println("Cassandra connection closed")
	}
}

// CreateKeyspaceIfNotExists creates the keyspace if it doesn't exist
func (db *CassandraDB) CreateKeyspaceIfNotExists() error {
	query := fmt.Sprintf(`
		CREATE KEYSPACE IF NOT EXISTS %s 
		WITH REPLICATION = {
			'class': 'SimpleStrategy',
			'replication_factor': 1
		}`, db.Config.Keyspace)

	return db.Session.Query(query).Exec()
}

// CreateTables creates all necessary tables
func (db *CassandraDB) CreateTables() error {
	tables := []string{
		`CREATE TABLE IF NOT EXISTS customers (
			id UUID PRIMARY KEY,
			name TEXT,
			email TEXT,
			phone TEXT,
			created_at TIMESTAMP,
			updated_at TIMESTAMP
		)`,
		`CREATE TABLE IF NOT EXISTS services (
			id UUID PRIMARY KEY,
			name TEXT,
			description TEXT,
			price DOUBLE,
			category TEXT,
			created_at TIMESTAMP,
			updated_at TIMESTAMP
		)`,
		`CREATE TABLE IF NOT EXISTS staff (
			id UUID PRIMARY KEY,
			name TEXT,
			email TEXT,
			role TEXT,
			phone TEXT,
			created_at TIMESTAMP,
			updated_at TIMESTAMP
		)`,
		`CREATE TABLE IF NOT EXISTS appointments (
			id UUID PRIMARY KEY,
			customer_id UUID,
			staff_id UUID,
			service_id UUID,
			appointment_date DATE,
			appointment_time TIME,
			status TEXT,
			notes TEXT,
			total_price DOUBLE,
			created_at TIMESTAMP,
			updated_at TIMESTAMP
		)`,
	}

	for _, table := range tables {
		if err := db.Session.Query(table).Exec(); err != nil {
			return fmt.Errorf("failed to create table: %w", err)
		}
	}

	// Create indexes
	indexes := []string{
		"CREATE INDEX IF NOT EXISTS ON customers (email)",
		"CREATE INDEX IF NOT EXISTS ON staff (email)",
		"CREATE INDEX IF NOT EXISTS ON appointments (customer_id)",
		"CREATE INDEX IF NOT EXISTS ON appointments (staff_id)",
		"CREATE INDEX IF NOT EXISTS ON appointments (service_id)",
		"CREATE INDEX IF NOT EXISTS ON appointments (appointment_date)",
	}

	for _, index := range indexes {
		if err := db.Session.Query(index).Exec(); err != nil {
			log.Printf("Warning: failed to create index: %v", err)
		}
	}

	log.Println("Database tables and indexes created successfully")
	return nil
}

// getEnvOrDefault returns environment variable value or default if not set
func getEnvOrDefault(key, defaultValue string) string {
	if value := os.Getenv(key); value != "" {
		return value
	}
	return defaultValue
}
