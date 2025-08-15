package service

import (
	"fmt"
	"log"

	"github.com/google/uuid"

	"beauty-salon-backend-go/internal/models"
	"beauty-salon-backend-go/internal/repository"
)

// CustomerService handles business logic for customers
type CustomerService struct {
	repo *repository.CustomerRepository
}

// NewCustomerService creates a new customer service
func NewCustomerService(repo *repository.CustomerRepository) *CustomerService {
	return &CustomerService{repo: repo}
}

// CreateCustomer creates a new customer
func (s *CustomerService) CreateCustomer(name, email, phone string) (*models.Customer, error) {
	// Validate input
	if name == "" {
		return nil, models.ErrInvalidName
	}
	if email == "" {
		return nil, models.ErrInvalidEmail
	}

	// Check if customer with email already exists
	existing, err := s.repo.FindByEmail(email)
	if err != nil && err != models.ErrNotFound {
		return nil, fmt.Errorf("failed to check existing customer: %w", err)
	}
	if existing != nil {
		return nil, models.ErrAlreadyExists
	}

	// Create new customer
	customer := models.NewCustomer(name, email, phone)
	
	if err := s.repo.Create(customer); err != nil {
		return nil, fmt.Errorf("failed to create customer: %w", err)
	}

	log.Printf("Created customer: %s (ID: %s)", customer.Name, customer.ID)
	return customer, nil
}

// GetCustomerByID retrieves a customer by ID
func (s *CustomerService) GetCustomerByID(id uuid.UUID) (*models.Customer, error) {
	customer, err := s.repo.FindByID(id)
	if err != nil {
		if err == models.ErrNotFound {
			return nil, models.ErrCustomerNotFound
		}
		return nil, fmt.Errorf("failed to get customer: %w", err)
	}
	return customer, nil
}

// GetCustomerByEmail retrieves a customer by email
func (s *CustomerService) GetCustomerByEmail(email string) (*models.Customer, error) {
	if email == "" {
		return nil, models.ErrInvalidEmail
	}

	customer, err := s.repo.FindByEmail(email)
	if err != nil {
		if err == models.ErrNotFound {
			return nil, models.ErrCustomerNotFound
		}
		return nil, fmt.Errorf("failed to get customer by email: %w", err)
	}
	return customer, nil
}

// GetAllCustomers retrieves all customers
func (s *CustomerService) GetAllCustomers() ([]*models.Customer, error) {
	customers, err := s.repo.FindAll()
	if err != nil {
		return nil, fmt.Errorf("failed to get all customers: %w", err)
	}
	return customers, nil
}

// SearchCustomersByName searches customers by name
func (s *CustomerService) SearchCustomersByName(name string) ([]*models.Customer, error) {
	if name == "" {
		return []*models.Customer{}, nil
	}

	customers, err := s.repo.SearchByName(name)
	if err != nil {
		return nil, fmt.Errorf("failed to search customers: %w", err)
	}
	return customers, nil
}

// UpdateCustomer updates an existing customer
func (s *CustomerService) UpdateCustomer(id uuid.UUID, name, email, phone string) (*models.Customer, error) {
	// Validate input
	if name == "" {
		return nil, models.ErrInvalidName
	}
	if email == "" {
		return nil, models.ErrInvalidEmail
	}

	// Check if customer exists
	customer, err := s.repo.FindByID(id)
	if err != nil {
		if err == models.ErrNotFound {
			return nil, models.ErrCustomerNotFound
		}
		return nil, fmt.Errorf("failed to find customer: %w", err)
	}

	// Check if email is being changed and if new email already exists
	if customer.Email != email {
		existing, err := s.repo.FindByEmail(email)
		if err != nil && err != models.ErrNotFound {
			return nil, fmt.Errorf("failed to check existing customer: %w", err)
		}
		if existing != nil && existing.ID != id {
			return nil, models.ErrAlreadyExists
		}
	}

	// Update customer fields
	customer.Name = name
	customer.Email = email
	customer.Phone = phone
	customer.UpdateTimestamp()

	if err := s.repo.Update(customer); err != nil {
		return nil, fmt.Errorf("failed to update customer: %w", err)
	}

	log.Printf("Updated customer: %s (ID: %s)", customer.Name, customer.ID)
	return customer, nil
}

// DeleteCustomer deletes a customer by ID
func (s *CustomerService) DeleteCustomer(id uuid.UUID) error {
	// Check if customer exists
	exists, err := s.repo.Exists(id)
	if err != nil {
		return fmt.Errorf("failed to check customer existence: %w", err)
	}
	if !exists {
		return models.ErrCustomerNotFound
	}

	if err := s.repo.Delete(id); err != nil {
		return fmt.Errorf("failed to delete customer: %w", err)
	}

	log.Printf("Deleted customer with ID: %s", id)
	return nil
}

// CustomerExists checks if a customer exists by ID
func (s *CustomerService) CustomerExists(id uuid.UUID) (bool, error) {
	exists, err := s.repo.Exists(id)
	if err != nil {
		return false, fmt.Errorf("failed to check customer existence: %w", err)
	}
	return exists, nil
}

// GetCustomerCount gets the total count of customers
func (s *CustomerService) GetCustomerCount() (int, error) {
	count, err := s.repo.Count()
	if err != nil {
		return 0, fmt.Errorf("failed to get customer count: %w", err)
	}
	return count, nil
}

// SearchCustomers searches customers by name, email or phone
func (s *CustomerService) SearchCustomers(query string, limit int) ([]*models.Customer, error) {
	if query == "" {
		return []*models.Customer{}, nil
	}

	customers, err := s.repo.Search(query, limit)
	if err != nil {
		return nil, fmt.Errorf("failed to search customers: %w", err)
	}
	return customers, nil
}
