package service

import (
	"fmt"
	"log"

	"github.com/google/uuid"

	"beauty-salon-backend-go/internal/models"
	"beauty-salon-backend-go/internal/repository"
)

// ServiceService handles business logic for services
type ServiceService struct {
	repo *repository.ServiceRepository
}

// NewServiceService creates a new service service
func NewServiceService(repo *repository.ServiceRepository) *ServiceService {
	return &ServiceService{repo: repo}
}

// CreateService creates a new service
func (s *ServiceService) CreateService(name, description, category string, price float64) (*models.Service, error) {
	// Validate input
	if name == "" {
		return nil, models.ErrInvalidName
	}
	if price <= 0 {
		return nil, models.ErrInvalidPrice
	}

	// Create new service
	service := models.NewService(name, description, category, price)
	
	if err := s.repo.Create(service); err != nil {
		return nil, fmt.Errorf("failed to create service: %w", err)
	}

	log.Printf("Created service: %s (ID: %s)", service.Name, service.ID)
	return service, nil
}

// GetServiceByID retrieves a service by ID
func (s *ServiceService) GetServiceByID(id uuid.UUID) (*models.Service, error) {
	service, err := s.repo.FindByID(id)
	if err != nil {
		if err == models.ErrNotFound {
			return nil, models.ErrServiceNotFound
		}
		return nil, fmt.Errorf("failed to get service: %w", err)
	}
	return service, nil
}

// GetAllServices retrieves all services
func (s *ServiceService) GetAllServices() ([]*models.Service, error) {
	services, err := s.repo.FindAll()
	if err != nil {
		return nil, fmt.Errorf("failed to get all services: %w", err)
	}
	return services, nil
}

// GetServicesByCategory retrieves services by category
func (s *ServiceService) GetServicesByCategory(category string) ([]*models.Service, error) {
	if category == "" {
		return s.GetAllServices()
	}

	services, err := s.repo.FindByCategory(category)
	if err != nil {
		return nil, fmt.Errorf("failed to get services by category: %w", err)
	}
	return services, nil
}

// UpdateService updates an existing service
func (s *ServiceService) UpdateService(id uuid.UUID, name, description, category string, price float64) (*models.Service, error) {
	// Validate input
	if name == "" {
		return nil, models.ErrInvalidName
	}
	if price <= 0 {
		return nil, models.ErrInvalidPrice
	}

	// Check if service exists
	service, err := s.repo.FindByID(id)
	if err != nil {
		if err == models.ErrNotFound {
			return nil, models.ErrServiceNotFound
		}
		return nil, fmt.Errorf("failed to find service: %w", err)
	}

	// Update service fields
	service.Name = name
	service.Description = description
	service.Category = category
	service.Price = price
	service.UpdateTimestamp()

	if err := s.repo.Update(service); err != nil {
		return nil, fmt.Errorf("failed to update service: %w", err)
	}

	log.Printf("Updated service: %s (ID: %s)", service.Name, service.ID)
	return service, nil
}

// DeleteService deletes a service by ID
func (s *ServiceService) DeleteService(id uuid.UUID) error {
	// Check if service exists
	exists, err := s.repo.Exists(id)
	if err != nil {
		return fmt.Errorf("failed to check service existence: %w", err)
	}
	if !exists {
		return models.ErrServiceNotFound
	}

	if err := s.repo.Delete(id); err != nil {
		return fmt.Errorf("failed to delete service: %w", err)
	}

	log.Printf("Deleted service with ID: %s", id)
	return nil
}

// ServiceExists checks if a service exists by ID
func (s *ServiceService) ServiceExists(id uuid.UUID) (bool, error) {
	exists, err := s.repo.Exists(id)
	if err != nil {
		return false, fmt.Errorf("failed to check service existence: %w", err)
	}
	return exists, nil
}
