package models

import (
	"time"

	"github.com/google/uuid"
)

// Staff represents a beauty salon staff member
type Staff struct {
	ID        uuid.UUID `json:"id" cql:"id"`
	Name      string    `json:"name" cql:"name" binding:"required"`
	Email     string    `json:"email" cql:"email" binding:"required,email"`
	Role      string    `json:"role" cql:"role" binding:"required"`
	Phone     string    `json:"phone" cql:"phone"`
	CreatedAt time.Time `json:"created_at" cql:"created_at"`
	UpdatedAt time.Time `json:"updated_at" cql:"updated_at"`
}

// NewStaff creates a new staff member with generated ID and timestamps
func NewStaff(name, email, role, phone string) *Staff {
	now := time.Now()
	return &Staff{
		ID:        uuid.New(),
		Name:      name,
		Email:     email,
		Role:      role,
		Phone:     phone,
		CreatedAt: now,
		UpdatedAt: now,
	}
}

// UpdateTimestamp updates the UpdatedAt field to current time
func (s *Staff) UpdateTimestamp() {
	s.UpdatedAt = time.Now()
}

// Validate performs basic validation on staff fields
func (s *Staff) Validate() error {
	if s.Name == "" {
		return ErrInvalidName
	}
	if s.Email == "" {
		return ErrInvalidEmail
	}
	if s.Role == "" {
		return ErrInvalidRole
	}
	return nil
}
