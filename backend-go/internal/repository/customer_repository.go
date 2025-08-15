package repository

import (
	"fmt"
	"time"

	"github.com/gocql/gocql"
	"github.com/google/uuid"

	"beauty-salon-backend-go/internal/models"
	"beauty-salon-backend-go/pkg/database"
)

// CustomerRepository handles customer data operations
type CustomerRepository struct {
	db *database.CassandraDB
}

// NewCustomerRepository creates a new customer repository
func NewCustomerRepository(db *database.CassandraDB) *CustomerRepository {
	return &CustomerRepository{db: db}
}

// Create inserts a new customer
func (r *CustomerRepository) Create(customer *models.Customer) error {
	query := `INSERT INTO customers (id, name, email, phone, created_at, updated_at) 
			  VALUES (?, ?, ?, ?, ?, ?)`
	
	return r.db.Session.Query(query,
		customer.ID.String(),
		customer.Name,
		customer.Email,
		customer.Phone,
		customer.CreatedAt,
		customer.UpdatedAt,
	).Exec()
}

// FindByID retrieves a customer by ID
func (r *CustomerRepository) FindByID(id uuid.UUID) (*models.Customer, error) {
	var idStr string
	var name, email, phone string
	var createdAt, updatedAt time.Time
	
	query := `SELECT id, name, email, phone, created_at, updated_at FROM customers WHERE id = ?`
	
	err := r.db.Session.Query(query, id.String()).Scan(
		&idStr,
		&name,
		&email,
		&phone,
		&createdAt,
		&updatedAt,
	)
	
	if err != nil {
		if err == gocql.ErrNotFound {
			return nil, models.ErrNotFound
		}
		return nil, fmt.Errorf("failed to find customer: %w", err)
	}
	
	parsedID, err := uuid.Parse(idStr)
	if err != nil {
		return nil, fmt.Errorf("failed to parse UUID: %w", err)
	}
	
	customer := &models.Customer{
		ID:        parsedID,
		Name:      name,
		Email:     email,
		Phone:     phone,
		CreatedAt: createdAt,
		UpdatedAt: updatedAt,
	}
	
	return customer, nil
}

// FindByEmail retrieves a customer by email
func (r *CustomerRepository) FindByEmail(email string) (*models.Customer, error) {
	var idStr string
	var name, customerEmail, phone string
	var createdAt, updatedAt time.Time
	
	query := `SELECT id, name, email, phone, created_at, updated_at 
			  FROM customers WHERE email = ? ALLOW FILTERING`
	
	err := r.db.Session.Query(query, email).Scan(
		&idStr,
		&name,
		&customerEmail,
		&phone,
		&createdAt,
		&updatedAt,
	)
	
	if err != nil {
		if err == gocql.ErrNotFound {
			return nil, models.ErrNotFound
		}
		return nil, fmt.Errorf("failed to find customer by email: %w", err)
	}
	
	id, err := uuid.Parse(idStr)
	if err != nil {
		return nil, fmt.Errorf("failed to parse UUID: %w", err)
	}
	
	customer := &models.Customer{
		ID:        id,
		Name:      name,
		Email:     customerEmail,
		Phone:     phone,
		CreatedAt: createdAt,
		UpdatedAt: updatedAt,
	}
	
	return customer, nil
}

// FindAll retrieves all customers
func (r *CustomerRepository) FindAll() ([]*models.Customer, error) {
	query := `SELECT id, name, email, phone, created_at, updated_at FROM customers`
	iter := r.db.Session.Query(query).Iter()
	
	var customers []*models.Customer
	
	for {
		var idStr string
		var name, email, phone string
		var createdAt, updatedAt time.Time
		
		if !iter.Scan(&idStr, &name, &email, &phone, &createdAt, &updatedAt) {
			break
		}
		
		id, err := uuid.Parse(idStr)
		if err != nil {
			return nil, fmt.Errorf("failed to parse UUID: %w", err)
		}
		
		customers = append(customers, &models.Customer{
			ID:        id,
			Name:      name,
			Email:     email,
			Phone:     phone,
			CreatedAt: createdAt,
			UpdatedAt: updatedAt,
		})
	}
	
	if err := iter.Close(); err != nil {
		return nil, fmt.Errorf("failed to retrieve customers: %w", err)
	}
	
	return customers, nil
}

// SearchByName searches customers by name (partial match)
func (r *CustomerRepository) SearchByName(name string) ([]*models.Customer, error) {
	// Note: This is a simple implementation. In production, consider using
	// a search engine like Elasticsearch for better text search capabilities
	query := `SELECT id, name, email, phone, created_at, updated_at 
			  FROM customers ALLOW FILTERING`
	iter := r.db.Session.Query(query).Iter()
	
	var customers []*models.Customer
	
	for {
		var idStr string
		var customerName, email, phone string
		var createdAt, updatedAt time.Time
		
		if !iter.Scan(&idStr, &customerName, &email, &phone, &createdAt, &updatedAt) {
			break
		}
		
		// Simple case-insensitive substring match
		if containsIgnoreCase(customerName, name) {
			id, err := uuid.Parse(idStr)
			if err != nil {
				return nil, fmt.Errorf("failed to parse UUID: %w", err)
			}
			
			customers = append(customers, &models.Customer{
				ID:        id,
				Name:      customerName,
				Email:     email,
				Phone:     phone,
				CreatedAt: createdAt,
				UpdatedAt: updatedAt,
			})
		}
	}
	
	if err := iter.Close(); err != nil {
		return nil, fmt.Errorf("failed to search customers: %w", err)
	}
	
	return customers, nil
}

// Update updates an existing customer
func (r *CustomerRepository) Update(customer *models.Customer) error {
	customer.UpdatedAt = time.Now()
	
	query := `UPDATE customers SET name = ?, email = ?, phone = ?, updated_at = ? 
			  WHERE id = ?`
	
	return r.db.Session.Query(query,
		customer.Name,
		customer.Email,
		customer.Phone,
		customer.UpdatedAt,
		customer.ID.String(),
	).Exec()
}

// Delete removes a customer by ID
func (r *CustomerRepository) Delete(id uuid.UUID) error {
	query := `DELETE FROM customers WHERE id = ?`
	return r.db.Session.Query(query, id.String()).Exec()
}

// Exists checks if a customer exists by ID
func (r *CustomerRepository) Exists(id uuid.UUID) (bool, error) {
	var count int
	query := `SELECT COUNT(*) FROM customers WHERE id = ?`
	
	err := r.db.Session.Query(query, id.String()).Scan(&count)
	if err != nil {
		return false, fmt.Errorf("failed to check customer existence: %w", err)
	}
	
	return count > 0, nil
}
