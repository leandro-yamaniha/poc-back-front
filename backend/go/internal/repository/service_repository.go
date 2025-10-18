package repository

import (
	"fmt"
	"strconv"
	"time"

	"github.com/gocql/gocql"
	"github.com/google/uuid"

	"beauty-salon-backend-go/internal/models"
	"beauty-salon-backend-go/pkg/database"
)

// ServiceRepository handles service data operations
type ServiceRepository struct {
	db *database.CassandraDB
}

// NewServiceRepository creates a new service repository
func NewServiceRepository(db *database.CassandraDB) *ServiceRepository {
	return &ServiceRepository{db: db}
}

// Create inserts a new service
func (r *ServiceRepository) Create(service *models.Service) error {
	query := `INSERT INTO services (id, name, description, price, category, created_at, updated_at) 
			  VALUES (?, ?, ?, ?, ?, ?, ?)`
	
	return r.db.Session.Query(query,
		service.ID.String(),
		service.Name,
		service.Description,
		fmt.Sprintf("%.2f", service.Price),
		service.Category,
		service.CreatedAt,
		service.UpdatedAt,
	).Exec()
}

// FindByID retrieves a service by ID
func (r *ServiceRepository) FindByID(id uuid.UUID) (*models.Service, error) {
	query := `SELECT id, name, description, price, category, created_at, updated_at FROM services WHERE id = ?`
	
	var idStr string
	var name, description, category string
	var priceStr string
	var createdAt, updatedAt time.Time
	
	err := r.db.Session.Query(query, id.String()).Scan(
		&idStr, &name, &description, &priceStr, &category, &createdAt, &updatedAt,
	)
	if err != nil {
		if err == gocql.ErrNotFound {
			return nil, models.ErrServiceNotFound
		}
		return nil, fmt.Errorf("failed to find service: %w", err)
	}
	
	parsedID, err := uuid.Parse(idStr)
	if err != nil {
		return nil, fmt.Errorf("failed to parse UUID: %w", err)
	}
	
	price, err := strconv.ParseFloat(priceStr, 64)
	if err != nil {
		return nil, fmt.Errorf("failed to parse price: %w", err)
	}
	
	return &models.Service{
		ID:          parsedID,
		Name:        name,
		Description: description,
		Price:       price,
		Category:    category,
		CreatedAt:   createdAt,
		UpdatedAt:   updatedAt,
	}, nil
}

// FindAll retrieves all services
func (r *ServiceRepository) FindAll() ([]*models.Service, error) {
	query := `SELECT id, name, description, price, category, created_at, updated_at FROM services`
	iter := r.db.Session.Query(query).Iter()
	
	var services []*models.Service
	
	for {
		var idStr string
		var name, description, category string
		var priceStr string
		var createdAt, updatedAt time.Time
		
		if !iter.Scan(&idStr, &name, &description, &priceStr, &category, &createdAt, &updatedAt) {
			break
		}
		
		id, err := uuid.Parse(idStr)
		if err != nil {
			return nil, fmt.Errorf("failed to parse UUID: %w", err)
		}
		
		price, err := strconv.ParseFloat(priceStr, 64)
		if err != nil {
			return nil, fmt.Errorf("failed to parse price: %w", err)
		}
		
		services = append(services, &models.Service{
			ID:          id,
			Name:        name,
			Description: description,
			Price:       price,
			Category:    category,
			CreatedAt:   createdAt,
			UpdatedAt:   updatedAt,
		})
	}
	
	if err := iter.Close(); err != nil {
		return nil, fmt.Errorf("failed to retrieve services: %w", err)
	}
	
	return services, nil
}

// FindByCategory retrieves services by category
func (r *ServiceRepository) FindByCategory(category string) ([]*models.Service, error) {
	query := `SELECT id, name, description, price, category, created_at, updated_at FROM services WHERE category = ? ALLOW FILTERING`
	iter := r.db.Session.Query(query, category).Iter()
	
	var services []*models.Service
	
	for {
		var idStr string
		var name, description, serviceCategory string
		var priceStr string
		var createdAt, updatedAt time.Time
		
		if !iter.Scan(&idStr, &name, &description, &priceStr, &serviceCategory, &createdAt, &updatedAt) {
			break
		}
		
		id, err := uuid.Parse(idStr)
		if err != nil {
			return nil, fmt.Errorf("failed to parse UUID: %w", err)
		}
		
		price, err := strconv.ParseFloat(priceStr, 64)
		if err != nil {
			return nil, fmt.Errorf("failed to parse price: %w", err)
		}
		
		services = append(services, &models.Service{
			ID:          id,
			Name:        name,
			Description: description,
			Price:       price,
			Category:    serviceCategory,
			CreatedAt:   createdAt,
			UpdatedAt:   updatedAt,
		})
	}
	
	if err := iter.Close(); err != nil {
		return nil, fmt.Errorf("failed to retrieve services by category: %w", err)
	}
	
	return services, nil
}

// Update updates an existing service
func (r *ServiceRepository) Update(service *models.Service) error {
	service.UpdatedAt = time.Now()
	
	query := `UPDATE services SET name = ?, description = ?, price = ?, category = ?, updated_at = ? 
			  WHERE id = ?`
	
	return r.db.Session.Query(query,
		service.Name,
		service.Description,
		fmt.Sprintf("%.2f", service.Price),
		service.Category,
		service.UpdatedAt,
		service.ID.String(),
	).Exec()
}

// Delete removes a service by ID
func (r *ServiceRepository) Delete(id uuid.UUID) error {
	query := `DELETE FROM services WHERE id = ?`
	return r.db.Session.Query(query, id.String()).Exec()
}

// Exists checks if a service exists by ID
func (r *ServiceRepository) Exists(id uuid.UUID) (bool, error) {
	var count int
	query := `SELECT COUNT(*) FROM services WHERE id = ?`
	
	err := r.db.Session.Query(query, id.String()).Scan(&count)
	if err != nil {
		return false, fmt.Errorf("failed to check service existence: %w", err)
	}
	
	return count > 0, nil
}

// FindActive retrieves all active services (assuming all services are active for now)
func (r *ServiceRepository) FindActive() ([]*models.Service, error) {
	// For now, we'll return all services as active
	// In a real implementation, you might have an 'active' field in the database
	return r.FindAll()
}

// Count returns the total number of services
func (r *ServiceRepository) Count() (int, error) {
	var count int
	query := `SELECT COUNT(*) FROM services`
	
	err := r.db.Session.Query(query).Scan(&count)
	if err != nil {
		return 0, fmt.Errorf("failed to count services: %w", err)
	}
	
	return count, nil
}

// GetCategories returns all unique service categories
func (r *ServiceRepository) GetCategories() ([]string, error) {
	query := `SELECT DISTINCT category FROM services ALLOW FILTERING`
	iter := r.db.Session.Query(query).Iter()
	
	var categories []string
	var category string
	
	for iter.Scan(&category) {
		categories = append(categories, category)
	}
	
	if err := iter.Close(); err != nil {
		return nil, fmt.Errorf("failed to retrieve categories: %w", err)
	}
	
	return categories, nil
}

// Search searches services by name or description with limit
func (r *ServiceRepository) Search(searchQuery string, limit int) ([]*models.Service, error) {
	query := `SELECT id, name, description, price, category, created_at, updated_at FROM services ALLOW FILTERING`
	iter := r.db.Session.Query(query).Iter()
	
	var services []*models.Service
	count := 0
	
	for count < limit {
		var idStr string
		var name, description, category string
		var priceStr string
		var createdAt, updatedAt time.Time
		
		if !iter.Scan(&idStr, &name, &description, &priceStr, &category, &createdAt, &updatedAt) {
			break
		}
		
		// Simple case-insensitive substring match for name or description
		if containsIgnoreCase(name, searchQuery) || containsIgnoreCase(description, searchQuery) {
			id, err := uuid.Parse(idStr)
			if err != nil {
				return nil, fmt.Errorf("failed to parse UUID: %w", err)
			}
			
			price, err := strconv.ParseFloat(priceStr, 64)
			if err != nil {
				return nil, fmt.Errorf("failed to parse price: %w", err)
			}
			
			services = append(services, &models.Service{
				ID:          id,
				Name:        name,
				Description: description,
				Price:       price,
				Category:    category,
				CreatedAt:   createdAt,
				UpdatedAt:   updatedAt,
			})
			count++
		}
	}
	
	if err := iter.Close(); err != nil {
		return nil, fmt.Errorf("failed to search services: %w", err)
	}
	
	return services, nil
}

// FindActiveByCategoryName retrieves active services by category name
func (r *ServiceRepository) FindActiveByCategoryName(category string) ([]*models.Service, error) {
	// For now, we'll return services by category (assuming all are active)
	// In a real implementation, you might filter by both category and active status
	return r.FindByCategory(category)
}
