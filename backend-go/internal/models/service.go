package models

import (
	"time"

	"github.com/google/uuid"
)

// Service represents a beauty salon service
type Service struct {
	ID          uuid.UUID `json:"id" cql:"id"`
	Name        string    `json:"name" cql:"name" binding:"required"`
	Description string    `json:"description" cql:"description"`
	Price       float64   `json:"price" cql:"price" binding:"required,gt=0"`
	Category    string    `json:"category" cql:"category"`
	CreatedAt   time.Time `json:"created_at" cql:"created_at"`
	UpdatedAt   time.Time `json:"updated_at" cql:"updated_at"`
}

// NewService creates a new service with generated ID and timestamps
func NewService(name, description, category string, price float64) *Service {
	now := time.Now()
	return &Service{
		ID:          uuid.New(),
		Name:        name,
		Description: description,
		Price:       price,
		Category:    category,
		CreatedAt:   now,
		UpdatedAt:   now,
	}
}

// UpdateTimestamp updates the UpdatedAt field to current time
func (s *Service) UpdateTimestamp() {
	s.UpdatedAt = time.Now()
}

// Validate performs basic validation on service fields
func (s *Service) Validate() error {
	if s.Name == "" {
		return ErrInvalidName
	}
	if s.Price <= 0 {
		return ErrInvalidPrice
	}
	return nil
}
