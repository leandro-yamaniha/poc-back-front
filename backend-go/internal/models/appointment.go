package models

import (
	"time"

	"github.com/google/uuid"
)

// AppointmentStatus represents the status of an appointment
type AppointmentStatus string

const (
	StatusScheduled AppointmentStatus = "scheduled"
	StatusConfirmed AppointmentStatus = "confirmed"
	StatusCompleted AppointmentStatus = "completed"
	StatusCancelled AppointmentStatus = "cancelled"
)

// Appointment represents a beauty salon appointment
type Appointment struct {
	ID              uuid.UUID         `json:"id" cql:"id"`
	CustomerID      uuid.UUID         `json:"customer_id" cql:"customer_id" binding:"required"`
	StaffID         uuid.UUID         `json:"staff_id" cql:"staff_id" binding:"required"`
	ServiceID       uuid.UUID         `json:"service_id" cql:"service_id" binding:"required"`
	AppointmentDate time.Time         `json:"appointment_date" cql:"appointment_date" binding:"required"`
	AppointmentTime time.Time         `json:"appointment_time" cql:"appointment_time" binding:"required"`
	Status          AppointmentStatus `json:"status" cql:"status"`
	Notes           string            `json:"notes" cql:"notes"`
	TotalPrice      float64           `json:"total_price" cql:"total_price"`
	CreatedAt       time.Time         `json:"created_at" cql:"created_at"`
	UpdatedAt       time.Time         `json:"updated_at" cql:"updated_at"`
}

// NewAppointment creates a new appointment with generated ID and timestamps
func NewAppointment(customerID, staffID, serviceID uuid.UUID, appointmentDate, appointmentTime time.Time, notes string, totalPrice float64) *Appointment {
	now := time.Now()
	return &Appointment{
		ID:              uuid.New(),
		CustomerID:      customerID,
		StaffID:         staffID,
		ServiceID:       serviceID,
		AppointmentDate: appointmentDate,
		AppointmentTime: appointmentTime,
		Status:          StatusScheduled,
		Notes:           notes,
		TotalPrice:      totalPrice,
		CreatedAt:       now,
		UpdatedAt:       now,
	}
}

// UpdateTimestamp updates the UpdatedAt field to current time
func (a *Appointment) UpdateTimestamp() {
	a.UpdatedAt = time.Now()
}

// Validate performs basic validation on appointment fields
func (a *Appointment) Validate() error {
	if a.CustomerID == uuid.Nil {
		return ErrCustomerNotFound
	}
	if a.StaffID == uuid.Nil {
		return ErrStaffNotFound
	}
	if a.ServiceID == uuid.Nil {
		return ErrServiceNotFound
	}
	if a.AppointmentDate.IsZero() {
		return ErrInvalidDate
	}
	if a.AppointmentTime.IsZero() {
		return ErrInvalidTime
	}
	return nil
}

// IsValidStatus checks if the status is valid
func (a *Appointment) IsValidStatus(status AppointmentStatus) bool {
	switch status {
	case StatusScheduled, StatusConfirmed, StatusCompleted, StatusCancelled:
		return true
	default:
		return false
	}
}
