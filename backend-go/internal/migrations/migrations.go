package migrations

import (
	"fmt"
	"log"
	"time"

	"github.com/gocql/gocql"
)

// Migration represents a database migration
type Migration struct {
	Version     string
	Description string
	Up          string
	Down        string
}

// Migrator handles database migrations
type Migrator struct {
	session *gocql.Session
}

// NewMigrator creates a new migrator instance
func NewMigrator(session *gocql.Session) *Migrator {
	return &Migrator{
		session: session,
	}
}

// GetMigrations returns all available migrations
func GetMigrations() []Migration {
	return []Migration{
		{
			Version:     "001",
			Description: "Create keyspace and initial tables",
			Up: `
-- Create keyspace
CREATE KEYSPACE IF NOT EXISTS beauty_salon
WITH REPLICATION = {
    'class': 'SimpleStrategy',
    'replication_factor': 1
};

-- Create customers table
CREATE TABLE IF NOT EXISTS beauty_salon.customers (
    id UUID PRIMARY KEY,
    name TEXT,
    email TEXT,
    phone TEXT,
    address TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create index for customer email lookup
CREATE INDEX IF NOT EXISTS ON beauty_salon.customers (email);

-- Create services table
CREATE TABLE IF NOT EXISTS beauty_salon.services (
    id UUID PRIMARY KEY,
    name TEXT,
    description TEXT,
    duration INT,
    price DECIMAL,
    category TEXT,
    is_active BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create staff table
CREATE TABLE IF NOT EXISTS beauty_salon.staff (
    id UUID PRIMARY KEY,
    name TEXT,
    email TEXT,
    phone TEXT,
    role TEXT,
    specialties SET<TEXT>,
    is_active BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create appointments table
CREATE TABLE IF NOT EXISTS beauty_salon.appointments (
    id UUID PRIMARY KEY,
    customer_id UUID,
    staff_id UUID,
    service_id UUID,
    appointment_date DATE,
    appointment_time TIME,
    status TEXT,
    notes TEXT,
    total_price DECIMAL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create indexes for appointments
CREATE INDEX IF NOT EXISTS ON beauty_salon.appointments (appointment_date);
CREATE INDEX IF NOT EXISTS ON beauty_salon.appointments (customer_id);
CREATE INDEX IF NOT EXISTS ON beauty_salon.appointments (staff_id);

-- Create payments table
CREATE TABLE IF NOT EXISTS beauty_salon.payments (
    id UUID PRIMARY KEY,
    appointment_id UUID,
    amount DECIMAL,
    payment_method TEXT,
    payment_status TEXT,
    payment_date TIMESTAMP,
    created_at TIMESTAMP
);

-- Create index for payments by appointment
CREATE INDEX IF NOT EXISTS ON beauty_salon.payments (appointment_id);
`,
			Down: `
USE beauty_salon;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS staff;
DROP TABLE IF EXISTS services;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS schema_migrations;
DROP KEYSPACE IF EXISTS beauty_salon;
`,
		},
		{
			Version:     "002",
			Description: "Insert sample data",
			Up: `
-- Sample services
INSERT INTO beauty_salon.services (id, name, description, duration, price, category, is_active, created_at, updated_at)
VALUES (uuid(), 'Corte de Cabelo', 'Corte de cabelo feminino', 60, 50.00, 'Cabelo', true, toTimestamp(now()), toTimestamp(now()));

INSERT INTO beauty_salon.services (id, name, description, duration, price, category, is_active, created_at, updated_at)
VALUES (uuid(), 'Escova', 'Escova modeladora', 45, 35.00, 'Cabelo', true, toTimestamp(now()), toTimestamp(now()));

INSERT INTO beauty_salon.services (id, name, description, duration, price, category, is_active, created_at, updated_at)
VALUES (uuid(), 'Manicure', 'Manicure completa', 45, 25.00, 'Unhas', true, toTimestamp(now()), toTimestamp(now()));

INSERT INTO beauty_salon.services (id, name, description, duration, price, category, is_active, created_at, updated_at)
VALUES (uuid(), 'Pedicure', 'Pedicure completa', 60, 30.00, 'Unhas', true, toTimestamp(now()), toTimestamp(now()));

INSERT INTO beauty_salon.services (id, name, description, duration, price, category, is_active, created_at, updated_at)
VALUES (uuid(), 'Limpeza de Pele', 'Limpeza facial profunda', 90, 80.00, 'Estética', true, toTimestamp(now()), toTimestamp(now()));

-- Sample staff
INSERT INTO beauty_salon.staff (id, name, email, phone, role, specialties, is_active, created_at, updated_at)
VALUES (uuid(), 'Maria Silva', 'maria@salao.com', '11999991111', 'Cabeleireira', {'Corte', 'Escova', 'Coloração'}, true, toTimestamp(now()), toTimestamp(now()));

INSERT INTO beauty_salon.staff (id, name, email, phone, role, specialties, is_active, created_at, updated_at)
VALUES (uuid(), 'Ana Santos', 'ana@salao.com', '11999992222', 'Manicure', {'Manicure', 'Pedicure', 'Nail Art'}, true, toTimestamp(now()), toTimestamp(now()));

INSERT INTO beauty_salon.staff (id, name, email, phone, role, specialties, is_active, created_at, updated_at)
VALUES (uuid(), 'Carla Oliveira', 'carla@salao.com', '11999993333', 'Esteticista', {'Limpeza de Pele', 'Massagem', 'Depilação'}, true, toTimestamp(now()), toTimestamp(now()));

-- Sample customers
INSERT INTO beauty_salon.customers (id, name, email, phone, address, created_at, updated_at)
VALUES (uuid(), 'Julia Costa', 'julia@email.com', '11888881111', 'Rua das Flores, 123', toTimestamp(now()), toTimestamp(now()));

INSERT INTO beauty_salon.customers (id, name, email, phone, address, created_at, updated_at)
VALUES (uuid(), 'Fernanda Lima', 'fernanda@email.com', '11888882222', 'Av. Paulista, 456', toTimestamp(now()), toTimestamp(now()));

INSERT INTO beauty_salon.customers (id, name, email, phone, address, created_at, updated_at)
VALUES (uuid(), 'Beatriz Rocha', 'beatriz@email.com', '11888883333', 'Rua Augusta, 789', toTimestamp(now()), toTimestamp(now()));
`,
			Down: `
TRUNCATE beauty_salon.customers;
TRUNCATE beauty_salon.staff;
TRUNCATE beauty_salon.services;
`,
		},
	}
}

// RunMigrations executes all pending migrations
func (m *Migrator) RunMigrations() error {
	log.Println("Starting database migrations...")

	// First, ensure the keyspace exists and create migration table
	if err := m.createMigrationTable(); err != nil {
		return fmt.Errorf("failed to create migration table: %w", err)
	}

	migrations := GetMigrations()
	
	for _, migration := range migrations {
		applied, err := m.isMigrationApplied(migration.Version)
		if err != nil {
			return fmt.Errorf("failed to check migration status for version %s: %w", migration.Version, err)
		}

		if applied {
			log.Printf("Migration %s already applied, skipping", migration.Version)
			continue
		}

		log.Printf("Applying migration %s: %s", migration.Version, migration.Description)
		
		if err := m.executeMigration(migration); err != nil {
			return fmt.Errorf("failed to apply migration %s: %w", migration.Version, err)
		}

		if err := m.recordMigration(migration); err != nil {
			return fmt.Errorf("failed to record migration %s: %w", migration.Version, err)
		}

		log.Printf("Migration %s applied successfully", migration.Version)
	}

	log.Println("All migrations completed successfully")
	return nil
}

// createMigrationTable creates the schema_migrations table if it doesn't exist
func (m *Migrator) createMigrationTable() error {
	// First create keyspace if it doesn't exist
	createKeyspace := `
		CREATE KEYSPACE IF NOT EXISTS beauty_salon
		WITH REPLICATION = {
			'class': 'SimpleStrategy',
			'replication_factor': 1
		}
	`
	
	if err := m.session.Query(createKeyspace).Exec(); err != nil {
		return fmt.Errorf("failed to create keyspace: %w", err)
	}

	// Note: gocql doesn't support USE statements, keyspace is set in session creation

	// Create migration table in the beauty_salon keyspace
	createTable := `
		CREATE TABLE IF NOT EXISTS beauty_salon.schema_migrations (
			version TEXT PRIMARY KEY,
			description TEXT,
			applied_at TIMESTAMP
		)
	`
	
	if err := m.session.Query(createTable).Exec(); err != nil {
		return fmt.Errorf("failed to create migration table: %w", err)
	}

	return nil
}

// isMigrationApplied checks if a migration has already been applied
func (m *Migrator) isMigrationApplied(version string) (bool, error) {
	var count int64
	query := "SELECT COUNT(*) FROM beauty_salon.schema_migrations WHERE version = ?"
	
	if err := m.session.Query(query, version).Scan(&count); err != nil {
		return false, err
	}
	
	return count > 0, nil
}

// executeMigration executes the migration SQL
func (m *Migrator) executeMigration(migration Migration) error {
	// Split the migration into individual statements
	statements := splitSQL(migration.Up)
	
	for _, stmt := range statements {
		stmt = trimStatement(stmt)
		if stmt == "" {
			continue
		}
		
		if err := m.session.Query(stmt).Exec(); err != nil {
			return fmt.Errorf("failed to execute statement: %s, error: %w", stmt, err)
		}
	}
	
	return nil
}

// recordMigration records that a migration has been applied
func (m *Migrator) recordMigration(migration Migration) error {
	query := `
		INSERT INTO beauty_salon.schema_migrations (version, description, applied_at)
		VALUES (?, ?, ?)
	`
	
	return m.session.Query(query, migration.Version, migration.Description, time.Now()).Exec()
}

// splitSQL splits a multi-statement SQL string into individual statements
func splitSQL(sql string) []string {
	statements := []string{}
	current := ""
	
	for _, line := range splitLines(sql) {
		line = trimLine(line)
		
		// Skip empty lines and comments
		if line == "" || isComment(line) {
			continue
		}
		
		current += line + "\n"
		
		// If line ends with semicolon, it's the end of a statement
		if endsWithSemicolon(line) {
			statements = append(statements, current)
			current = ""
		}
	}
	
	// Add any remaining statement
	if current != "" {
		statements = append(statements, current)
	}
	
	return statements
}

// Helper functions
func splitLines(s string) []string {
	lines := []string{}
	current := ""
	
	for _, char := range s {
		if char == '\n' {
			lines = append(lines, current)
			current = ""
		} else {
			current += string(char)
		}
	}
	
	if current != "" {
		lines = append(lines, current)
	}
	
	return lines
}

func trimLine(line string) string {
	// Simple trim implementation
	start := 0
	end := len(line)
	
	// Trim leading whitespace
	for start < len(line) && isWhitespace(line[start]) {
		start++
	}
	
	// Trim trailing whitespace
	for end > start && isWhitespace(line[end-1]) {
		end--
	}
	
	return line[start:end]
}

func trimStatement(stmt string) string {
	return trimLine(stmt)
}

func isWhitespace(char byte) bool {
	return char == ' ' || char == '\t' || char == '\r' || char == '\n'
}

func isComment(line string) bool {
	return len(line) >= 2 && line[:2] == "--"
}

func endsWithSemicolon(line string) bool {
	return len(line) > 0 && line[len(line)-1] == ';'
}
