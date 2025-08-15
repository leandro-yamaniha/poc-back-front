package models

import (
	"time"

	"github.com/google/uuid"
)

// Customer represents a beauty salon customer
type Customer struct {
	ID        uuid.UUID `json:"id" cql:"id"`
	Name      string    `json:"name" cql:"name" binding:"required"`
	Email     string    `json:"email" cql:"email" binding:"required,email"`
	Phone     string    `json:"phone" cql:"phone"`
	CreatedAt time.Time `json:"created_at" cql:"created_at"`
	UpdatedAt time.Time `json:"updated_at" cql:"updated_at"`
}

// NewCustomer creates a new customer with generated ID and timestamps
func NewCustomer(name, email, phone string) *Customer {
	now := time.Now()
	return &Customer{
		ID:        uuid.New(),
		Name:      name,
		Email:     email,
		Phone:     phone,
		CreatedAt: now,
		UpdatedAt: now,
	}
}

// UpdateTimestamp updates the UpdatedAt field to current time
func (c *Customer) UpdateTimestamp() {
	c.UpdatedAt = time.Now()
}

// Validate performs basic validation on customer fields
func (c *Customer) Validate() error {
	if c.Name == "" {
		return ErrInvalidName
	}
	if c.Email == "" {
		return ErrInvalidEmail
	}
	return nil
}
