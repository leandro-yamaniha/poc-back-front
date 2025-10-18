package models

import "errors"

// Common validation errors
var (
	ErrInvalidName     = errors.New("name cannot be empty")
	ErrInvalidEmail    = errors.New("email cannot be empty")
	ErrInvalidPrice    = errors.New("price must be greater than zero")
	ErrInvalidDuration = errors.New("duration must be greater than zero")
	ErrInvalidRole     = errors.New("role cannot be empty")
	ErrInvalidStatus   = errors.New("invalid status")
	ErrInvalidDate     = errors.New("invalid date")
	ErrInvalidTime     = errors.New("invalid time")
)

// Repository errors
var (
	ErrNotFound      = errors.New("record not found")
	ErrAlreadyExists = errors.New("record already exists")
	ErrDatabase      = errors.New("database error")
)

// Service errors
var (
	ErrCustomerNotFound    = errors.New("customer not found")
	ErrServiceNotFound     = errors.New("service not found")
	ErrStaffNotFound       = errors.New("staff not found")
	ErrAppointmentNotFound = errors.New("appointment not found")
	ErrConflictingBooking  = errors.New("conflicting appointment booking")
)
