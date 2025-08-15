package service

import (
	"fmt"
	"log"

	"github.com/google/uuid"

	"beauty-salon-backend-go/internal/models"
	"beauty-salon-backend-go/internal/repository"
)

// StaffService handles business logic for staff
type StaffService struct {
	repo *repository.StaffRepository
}

// NewStaffService creates a new staff service
func NewStaffService(repo *repository.StaffRepository) *StaffService {
	return &StaffService{repo: repo}
}

// CreateStaff creates a new staff member
func (s *StaffService) CreateStaff(name, email, role, phone string) (*models.Staff, error) {
	// Validate input
	if name == "" {
		return nil, models.ErrInvalidName
	}
	if email == "" {
		return nil, models.ErrInvalidEmail
	}
	if role == "" {
		return nil, models.ErrInvalidRole
	}

	// Check if staff with email already exists
	existing, err := s.repo.FindByEmail(email)
	if err != nil && err != models.ErrNotFound {
		return nil, fmt.Errorf("failed to check existing staff: %w", err)
	}
	if existing != nil {
		return nil, models.ErrAlreadyExists
	}

	// Create new staff member
	staff := models.NewStaff(name, email, role, phone)
	
	if err := s.repo.Create(staff); err != nil {
		return nil, fmt.Errorf("failed to create staff: %w", err)
	}

	log.Printf("Created staff: %s (ID: %s)", staff.Name, staff.ID)
	return staff, nil
}

// GetStaffByID retrieves a staff member by ID
func (s *StaffService) GetStaffByID(id uuid.UUID) (*models.Staff, error) {
	staff, err := s.repo.FindByID(id)
	if err != nil {
		if err == models.ErrNotFound {
			return nil, models.ErrStaffNotFound
		}
		return nil, fmt.Errorf("failed to get staff: %w", err)
	}
	return staff, nil
}

// GetStaffByEmail retrieves a staff member by email
func (s *StaffService) GetStaffByEmail(email string) (*models.Staff, error) {
	if email == "" {
		return nil, models.ErrInvalidEmail
	}

	staff, err := s.repo.FindByEmail(email)
	if err != nil {
		if err == models.ErrNotFound {
			return nil, models.ErrStaffNotFound
		}
		return nil, fmt.Errorf("failed to get staff by email: %w", err)
	}
	return staff, nil
}

// GetAllStaff retrieves all staff members
func (s *StaffService) GetAllStaff() ([]*models.Staff, error) {
	staff, err := s.repo.FindAll()
	if err != nil {
		return nil, fmt.Errorf("failed to get all staff: %w", err)
	}
	return staff, nil
}

// GetStaffByRole retrieves staff members by role
func (s *StaffService) GetStaffByRole(role string) ([]*models.Staff, error) {
	if role == "" {
		return s.GetAllStaff()
	}

	staff, err := s.repo.FindByRole(role)
	if err != nil {
		return nil, fmt.Errorf("failed to get staff by role: %w", err)
	}
	return staff, nil
}

// UpdateStaff updates an existing staff member
func (s *StaffService) UpdateStaff(id uuid.UUID, name, email, role, phone string) (*models.Staff, error) {
	// Validate input
	if name == "" {
		return nil, models.ErrInvalidName
	}
	if email == "" {
		return nil, models.ErrInvalidEmail
	}
	if role == "" {
		return nil, models.ErrInvalidRole
	}

	// Check if staff exists
	staff, err := s.repo.FindByID(id)
	if err != nil {
		if err == models.ErrNotFound {
			return nil, models.ErrStaffNotFound
		}
		return nil, fmt.Errorf("failed to find staff: %w", err)
	}

	// Check if email is being changed and if new email already exists
	if staff.Email != email {
		existing, err := s.repo.FindByEmail(email)
		if err != nil && err != models.ErrNotFound {
			return nil, fmt.Errorf("failed to check existing staff: %w", err)
		}
		if existing != nil && existing.ID != id {
			return nil, models.ErrAlreadyExists
		}
	}

	// Update staff fields
	staff.Name = name
	staff.Email = email
	staff.Role = role
	staff.Phone = phone
	staff.UpdateTimestamp()

	if err := s.repo.Update(staff); err != nil {
		return nil, fmt.Errorf("failed to update staff: %w", err)
	}

	log.Printf("Updated staff: %s (ID: %s)", staff.Name, staff.ID)
	return staff, nil
}

// DeleteStaff deletes a staff member by ID
func (s *StaffService) DeleteStaff(id uuid.UUID) error {
	// Check if staff exists
	exists, err := s.repo.Exists(id)
	if err != nil {
		return fmt.Errorf("failed to check staff existence: %w", err)
	}
	if !exists {
		return models.ErrStaffNotFound
	}

	if err := s.repo.Delete(id); err != nil {
		return fmt.Errorf("failed to delete staff: %w", err)
	}

	log.Printf("Deleted staff with ID: %s", id)
	return nil
}

// StaffExists checks if a staff member exists by ID
func (s *StaffService) StaffExists(id uuid.UUID) (bool, error) {
	exists, err := s.repo.Exists(id)
	if err != nil {
		return false, fmt.Errorf("failed to check staff existence: %w", err)
	}
	return exists, nil
}

// GetActiveStaff retrieves all active staff members
func (s *StaffService) GetActiveStaff() ([]*models.Staff, error) {
	staff, err := s.repo.FindActive()
	if err != nil {
		return nil, fmt.Errorf("failed to get active staff: %w", err)
	}
	return staff, nil
}

// GetStaffCount gets the total count of staff members
func (s *StaffService) GetStaffCount() (int, error) {
	count, err := s.repo.Count()
	if err != nil {
		return 0, fmt.Errorf("failed to get staff count: %w", err)
	}
	return count, nil
}

// GetRoles gets all unique staff roles
func (s *StaffService) GetRoles() ([]string, error) {
	roles, err := s.repo.GetRoles()
	if err != nil {
		return nil, fmt.Errorf("failed to get roles: %w", err)
	}
	return roles, nil
}

// SearchStaff searches staff by name, email or role
func (s *StaffService) SearchStaff(query string, limit int) ([]*models.Staff, error) {
	if query == "" {
		return []*models.Staff{}, nil
	}

	staff, err := s.repo.Search(query, limit)
	if err != nil {
		return nil, fmt.Errorf("failed to search staff: %w", err)
	}
	return staff, nil
}

// GetActiveStaffByRole retrieves active staff members by role
func (s *StaffService) GetActiveStaffByRole(role string) ([]*models.Staff, error) {
	if role == "" {
		return s.GetActiveStaff()
	}

	staff, err := s.repo.FindActiveByRole(role)
	if err != nil {
		return nil, fmt.Errorf("failed to get active staff by role: %w", err)
	}
	return staff, nil
}

// GetStaffBySpecialty retrieves staff members by specialty
func (s *StaffService) GetStaffBySpecialty(specialty string) ([]*models.Staff, error) {
	if specialty == "" {
		return []*models.Staff{}, nil
	}

	staff, err := s.repo.FindBySpecialty(specialty)
	if err != nil {
		return nil, fmt.Errorf("failed to get staff by specialty: %w", err)
	}
	return staff, nil
}
