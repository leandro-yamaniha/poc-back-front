package repository

import (
	"fmt"
	"time"

	"github.com/gocql/gocql"
	"github.com/google/uuid"

	"beauty-salon-backend-go/internal/models"
	"beauty-salon-backend-go/pkg/database"
)

// StaffRepository handles staff data operations
type StaffRepository struct {
	db *database.CassandraDB
}

// NewStaffRepository creates a new staff repository
func NewStaffRepository(db *database.CassandraDB) *StaffRepository {
	return &StaffRepository{db: db}
}

// Create inserts a new staff member
func (r *StaffRepository) Create(staff *models.Staff) error {
	query := `INSERT INTO staff (id, name, email, role, phone, created_at, updated_at) 
			  VALUES (?, ?, ?, ?, ?, ?, ?)`
	
	return r.db.Session.Query(query,
		staff.ID.String(),
		staff.Name,
		staff.Email,
		staff.Role,
		staff.Phone,
		staff.CreatedAt,
		staff.UpdatedAt,
	).Exec()
}

// FindByID retrieves a staff member by ID
func (r *StaffRepository) FindByID(id uuid.UUID) (*models.Staff, error) {
	staff := &models.Staff{}
	var idStr string
	query := `SELECT id, name, email, role, phone, created_at, updated_at 
			  FROM staff WHERE id = ?`
	
	err := r.db.Session.Query(query, id.String()).Scan(
		&idStr,
		&staff.Name,
		&staff.Email,
		&staff.Role,
		&staff.Phone,
		&staff.CreatedAt,
		&staff.UpdatedAt,
	)
	
	if err != nil {
		if err == gocql.ErrNotFound {
			return nil, models.ErrNotFound
		}
		return nil, fmt.Errorf("failed to find staff: %w", err)
	}
	
	// Parse UUID from string
	parsedID, err := uuid.Parse(idStr)
	if err != nil {
		return nil, fmt.Errorf("failed to parse staff ID: %w", err)
	}
	staff.ID = parsedID
	
	return staff, nil
}

// FindByEmail retrieves a staff member by email
func (r *StaffRepository) FindByEmail(email string) (*models.Staff, error) {
	staff := &models.Staff{}
	var idStr string
	query := `SELECT id, name, email, role, phone, created_at, updated_at 
			  FROM staff WHERE email = ? ALLOW FILTERING`
	
	err := r.db.Session.Query(query, email).Scan(
		&idStr,
		&staff.Name,
		&staff.Email,
		&staff.Role,
		&staff.Phone,
		&staff.CreatedAt,
		&staff.UpdatedAt,
	)
	
	if err != nil {
		if err == gocql.ErrNotFound {
			return nil, models.ErrNotFound
		}
		return nil, fmt.Errorf("failed to find staff by email: %w", err)
	}
	
	// Parse UUID from string
	parsedID, err := uuid.Parse(idStr)
	if err != nil {
		return nil, fmt.Errorf("failed to parse staff ID: %w", err)
	}
	staff.ID = parsedID
	
	return staff, nil
}

// FindAll retrieves all staff members
func (r *StaffRepository) FindAll() ([]*models.Staff, error) {
	query := `SELECT id, name, email, role, phone, created_at, updated_at FROM staff`
	iter := r.db.Session.Query(query).Iter()
	
	var staffMembers []*models.Staff
	staff := &models.Staff{}
	var idStr string
	
	for iter.Scan(
		&idStr,
		&staff.Name,
		&staff.Email,
		&staff.Role,
		&staff.Phone,
		&staff.CreatedAt,
		&staff.UpdatedAt,
	) {
		// Parse UUID from string
		parsedID, err := uuid.Parse(idStr)
		if err != nil {
			continue // Skip invalid UUIDs
		}
		
		staffMembers = append(staffMembers, &models.Staff{
			ID:        parsedID,
			Name:      staff.Name,
			Email:     staff.Email,
			Role:      staff.Role,
			Phone:     staff.Phone,
			CreatedAt: staff.CreatedAt,
			UpdatedAt: staff.UpdatedAt,
		})
		staff = &models.Staff{}
	}
	
	if err := iter.Close(); err != nil {
		return nil, fmt.Errorf("failed to retrieve staff: %w", err)
	}
	
	return staffMembers, nil
}

// FindByRole retrieves staff members by role
func (r *StaffRepository) FindByRole(role string) ([]*models.Staff, error) {
	query := `SELECT id, name, email, role, phone, created_at, updated_at 
			  FROM staff WHERE role = ? ALLOW FILTERING`
	iter := r.db.Session.Query(query, role).Iter()
	
	var staffMembers []*models.Staff
	staff := &models.Staff{}
	var idStr string
	
	for iter.Scan(
		&idStr,
		&staff.Name,
		&staff.Email,
		&staff.Role,
		&staff.Phone,
		&staff.CreatedAt,
		&staff.UpdatedAt,
	) {
		// Parse UUID from string
		parsedID, err := uuid.Parse(idStr)
		if err != nil {
			continue // Skip invalid UUIDs
		}
		
		staffMembers = append(staffMembers, &models.Staff{
			ID:        parsedID,
			Name:      staff.Name,
			Email:     staff.Email,
			Role:      staff.Role,
			Phone:     staff.Phone,
			CreatedAt: staff.CreatedAt,
			UpdatedAt: staff.UpdatedAt,
		})
		staff = &models.Staff{}
	}
	
	if err := iter.Close(); err != nil {
		return nil, fmt.Errorf("failed to retrieve staff by role: %w", err)
	}
	
	return staffMembers, nil
}

// Update updates an existing staff member
func (r *StaffRepository) Update(staff *models.Staff) error {
	staff.UpdatedAt = time.Now()
	
	query := `UPDATE staff SET name = ?, email = ?, role = ?, phone = ?, updated_at = ? 
			  WHERE id = ?`
	
	return r.db.Session.Query(query,
		staff.Name,
		staff.Email,
		staff.Role,
		staff.Phone,
		staff.UpdatedAt,
		staff.ID.String(),
	).Exec()
}

// Delete removes a staff member by ID
func (r *StaffRepository) Delete(id uuid.UUID) error {
	query := `DELETE FROM staff WHERE id = ?`
	return r.db.Session.Query(query, id.String()).Exec()
}

// Exists checks if a staff member exists by ID
func (r *StaffRepository) Exists(id uuid.UUID) (bool, error) {
	var count int
	query := `SELECT COUNT(*) FROM staff WHERE id = ?`
	
	err := r.db.Session.Query(query, id.String()).Scan(&count)
	if err != nil {
		return false, fmt.Errorf("failed to check staff existence: %w", err)
	}
	
	return count > 0, nil
}

// FindActive retrieves all active staff members (assuming all staff are active for now)
func (r *StaffRepository) FindActive() ([]*models.Staff, error) {
	// For now, we'll return all staff as active
	// In a real implementation, you might have an 'active' field in the database
	return r.FindAll()
}

// Count returns the total number of staff members
func (r *StaffRepository) Count() (int, error) {
	var count int
	query := `SELECT COUNT(*) FROM staff`
	
	err := r.db.Session.Query(query).Scan(&count)
	if err != nil {
		return 0, fmt.Errorf("failed to count staff: %w", err)
	}
	
	return count, nil
}

// GetRoles returns all unique staff roles
func (r *StaffRepository) GetRoles() ([]string, error) {
	query := `SELECT DISTINCT role FROM staff ALLOW FILTERING`
	iter := r.db.Session.Query(query).Iter()
	
	var roles []string
	var role string
	
	for iter.Scan(&role) {
		roles = append(roles, role)
	}
	
	if err := iter.Close(); err != nil {
		return nil, fmt.Errorf("failed to retrieve roles: %w", err)
	}
	
	return roles, nil
}

// Search searches staff by name, email or role with limit
func (r *StaffRepository) Search(searchQuery string, limit int) ([]*models.Staff, error) {
	query := `SELECT id, name, email, role, phone, created_at, updated_at FROM staff ALLOW FILTERING`
	iter := r.db.Session.Query(query).Iter()
	
	var staffMembers []*models.Staff
	count := 0
	
	for count < limit {
		var idStr string
		var name, email, role, phone string
		var createdAt, updatedAt time.Time
		
		if !iter.Scan(&idStr, &name, &email, &role, &phone, &createdAt, &updatedAt) {
			break
		}
		
		// Simple case-insensitive substring match for name, email, or role
		if containsIgnoreCase(name, searchQuery) || 
		   containsIgnoreCase(email, searchQuery) || 
		   containsIgnoreCase(role, searchQuery) {
			id, err := uuid.Parse(idStr)
			if err != nil {
				return nil, fmt.Errorf("failed to parse UUID: %w", err)
			}
			
			staffMembers = append(staffMembers, &models.Staff{
				ID:        id,
				Name:      name,
				Email:     email,
				Role:      role,
				Phone:     phone,
				CreatedAt: createdAt,
				UpdatedAt: updatedAt,
			})
			count++
		}
	}
	
	if err := iter.Close(); err != nil {
		return nil, fmt.Errorf("failed to search staff: %w", err)
	}
	
	return staffMembers, nil
}

// FindActiveByRole retrieves active staff members by role
func (r *StaffRepository) FindActiveByRole(role string) ([]*models.Staff, error) {
	// For now, we'll return staff by role (assuming all are active)
	// In a real implementation, you might filter by both role and active status
	return r.FindByRole(role)
}

// FindBySpecialty retrieves staff members by specialty (using role as specialty for now)
func (r *StaffRepository) FindBySpecialty(specialty string) ([]*models.Staff, error) {
	// For now, we'll use role as specialty
	// In a real implementation, you might have a separate specialty field
	return r.FindByRole(specialty)
}
