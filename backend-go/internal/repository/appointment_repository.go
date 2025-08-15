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

// AppointmentRepository handles appointment data operations
type AppointmentRepository struct {
	db *database.CassandraDB
}

// NewAppointmentRepository creates a new appointment repository
func NewAppointmentRepository(db *database.CassandraDB) *AppointmentRepository {
	return &AppointmentRepository{db: db}
}

// Create inserts a new appointment
func (r *AppointmentRepository) Create(appointment *models.Appointment) error {
	query := `INSERT INTO appointments (id, customer_id, staff_id, service_id, appointment_date, 
			  appointment_time, status, notes, total_price, created_at, updated_at) 
			  VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`
	
	return r.db.Session.Query(query,
		appointment.ID.String(),
		appointment.CustomerID.String(),
		appointment.StaffID.String(),
		appointment.ServiceID.String(),
		appointment.AppointmentDate,
		appointment.AppointmentTime,
		string(appointment.Status),
		appointment.Notes,
		fmt.Sprintf("%.2f", appointment.TotalPrice),
		appointment.CreatedAt,
		appointment.UpdatedAt,
	).Exec()
}

// FindByID retrieves an appointment by ID
func (r *AppointmentRepository) FindByID(id uuid.UUID) (*models.Appointment, error) {
	appointment := &models.Appointment{}
	var status string
	var idStr, customerIDStr, staffIDStr, serviceIDStr string
	var appointmentDateStr, appointmentTimeStr string
	var totalPriceStr string
	
	query := `SELECT id, customer_id, staff_id, service_id, appointment_date, appointment_time, 
			  status, notes, total_price, created_at, updated_at 
			  FROM appointments WHERE id = ?`
	
	err := r.db.Session.Query(query, id.String()).Scan(
		&idStr,
		&customerIDStr,
		&staffIDStr,
		&serviceIDStr,
		&appointmentDateStr,
		&appointmentTimeStr,
		&status,
		&appointment.Notes,
		&totalPriceStr,
		&appointment.CreatedAt,
		&appointment.UpdatedAt,
	)
	
	if err != nil {
		if err == gocql.ErrNotFound {
			return nil, models.ErrNotFound
		}
		return nil, fmt.Errorf("failed to find appointment: %w", err)
	}
	
	// Parse UUIDs from strings
	parsedID, err := uuid.Parse(idStr)
	if err != nil {
		return nil, fmt.Errorf("failed to parse appointment ID: %w", err)
	}
	appointment.ID = parsedID
	
	parsedCustomerID, err := uuid.Parse(customerIDStr)
	if err != nil {
		return nil, fmt.Errorf("failed to parse customer ID: %w", err)
	}
	appointment.CustomerID = parsedCustomerID
	
	parsedStaffID, err := uuid.Parse(staffIDStr)
	if err != nil {
		return nil, fmt.Errorf("failed to parse staff ID: %w", err)
	}
	appointment.StaffID = parsedStaffID
	
	parsedServiceID, err := uuid.Parse(serviceIDStr)
	if err != nil {
		return nil, fmt.Errorf("failed to parse service ID: %w", err)
	}
	appointment.ServiceID = parsedServiceID
	
	// Parse date and time from strings
	appointmentDate, err := time.Parse("2006-01-02", appointmentDateStr)
	if err != nil {
		return nil, fmt.Errorf("failed to parse appointment date: %w", err)
	}
	appointment.AppointmentDate = appointmentDate
	
	appointmentTime, err := time.Parse("15:04:05", appointmentTimeStr)
	if err != nil {
		return nil, fmt.Errorf("failed to parse appointment time: %w", err)
	}
	appointment.AppointmentTime = appointmentTime
	
	appointment.Status = models.AppointmentStatus(status)
	
	totalPrice, err := strconv.ParseFloat(totalPriceStr, 64)
	if err != nil {
		return nil, fmt.Errorf("failed to parse total price: %w", err)
	}
	appointment.TotalPrice = totalPrice
	
	return appointment, nil
}

// FindAll retrieves all appointments
func (r *AppointmentRepository) FindAll() ([]*models.Appointment, error) {
	query := `SELECT id, customer_id, staff_id, service_id, appointment_date, appointment_time, 
			  status, notes, total_price, created_at, updated_at FROM appointments`
	iter := r.db.Session.Query(query).Iter()
	
	var appointments []*models.Appointment
	var idStr, customerIDStr, staffIDStr, serviceIDStr string
	var appointmentDateStr, appointmentTimeStr string
	var status string
	var notes string
	var totalPrice float64
	var createdAt, updatedAt time.Time
	
	for iter.Scan(
		&idStr,
		&customerIDStr,
		&staffIDStr,
		&serviceIDStr,
		&appointmentDateStr,
		&appointmentTimeStr,
		&status,
		&notes,
		&totalPrice,
		&createdAt,
		&updatedAt,
	) {
		// Parse UUIDs from strings
		parsedID, err := uuid.Parse(idStr)
		if err != nil {
			continue // Skip invalid UUIDs
		}
		parsedCustomerID, err := uuid.Parse(customerIDStr)
		if err != nil {
			continue // Skip invalid UUIDs
		}
		parsedStaffID, err := uuid.Parse(staffIDStr)
		if err != nil {
			continue // Skip invalid UUIDs
		}
		parsedServiceID, err := uuid.Parse(serviceIDStr)
		if err != nil {
			continue // Skip invalid UUIDs
		}
		
		// Parse date and time from strings
		appointmentDate, err := time.Parse("2006-01-02", appointmentDateStr)
		if err != nil {
			continue // Skip invalid dates
		}
		
		appointmentTime, err := time.Parse("15:04:05", appointmentTimeStr)
		if err != nil {
			continue // Skip invalid times
		}
		
		appointments = append(appointments, &models.Appointment{
			ID:              parsedID,
			CustomerID:      parsedCustomerID,
			StaffID:         parsedStaffID,
			ServiceID:       parsedServiceID,
			AppointmentDate: appointmentDate,
			AppointmentTime: appointmentTime,
			Status:          models.AppointmentStatus(status),
			Notes:           notes,
			TotalPrice:      totalPrice,
			CreatedAt:       createdAt,
			UpdatedAt:       updatedAt,
		})
	}
	
	if err := iter.Close(); err != nil {
		return nil, fmt.Errorf("failed to retrieve appointments: %w", err)
	}
	
	return appointments, nil
}

// FindByCustomerID retrieves appointments by customer ID
func (r *AppointmentRepository) FindByCustomerID(customerID uuid.UUID) ([]*models.Appointment, error) {
	query := `SELECT id, customer_id, staff_id, service_id, appointment_date, appointment_time, 
			  status, notes, total_price, created_at, updated_at 
			  FROM appointments WHERE customer_id = ? ALLOW FILTERING`
	iter := r.db.Session.Query(query, customerID.String()).Iter()
	
	var appointments []*models.Appointment
	var idStr, customerIDStr, staffIDStr, serviceIDStr string
	var appointmentDateStr, appointmentTimeStr string
	var status string
	var notes string
	var totalPrice float64
	var createdAt, updatedAt time.Time
	
	for iter.Scan(
		&idStr,
		&customerIDStr,
		&staffIDStr,
		&serviceIDStr,
		&appointmentDateStr,
		&appointmentTimeStr,
		&status,
		&notes,
		&totalPrice,
		&createdAt,
		&updatedAt,
	) {
		// Parse UUIDs from strings
		parsedID, err := uuid.Parse(idStr)
		if err != nil {
			continue // Skip invalid UUIDs
		}
		parsedCustomerID, err := uuid.Parse(customerIDStr)
		if err != nil {
			continue // Skip invalid UUIDs
		}
		parsedStaffID, err := uuid.Parse(staffIDStr)
		if err != nil {
			continue // Skip invalid UUIDs
		}
		parsedServiceID, err := uuid.Parse(serviceIDStr)
		if err != nil {
			continue // Skip invalid UUIDs
		}
		
		// Parse date and time from strings
		appointmentDate, err := time.Parse("2006-01-02", appointmentDateStr)
		if err != nil {
			continue // Skip invalid dates
		}
		
		appointmentTime, err := time.Parse("15:04:05", appointmentTimeStr)
		if err != nil {
			continue // Skip invalid times
		}
		
		appointments = append(appointments, &models.Appointment{
			ID:              parsedID,
			CustomerID:      parsedCustomerID,
			StaffID:         parsedStaffID,
			ServiceID:       parsedServiceID,
			AppointmentDate: appointmentDate,
			AppointmentTime: appointmentTime,
			Status:          models.AppointmentStatus(status),
			Notes:           notes,
			TotalPrice:      totalPrice,
			CreatedAt:       createdAt,
			UpdatedAt:       updatedAt,
		})
	}
	
	if err := iter.Close(); err != nil {
		return nil, fmt.Errorf("failed to retrieve appointments by customer: %w", err)
	}
	
	return appointments, nil
}

// FindByStaffID retrieves appointments by staff ID
func (r *AppointmentRepository) FindByStaffID(staffID uuid.UUID) ([]*models.Appointment, error) {
	query := `SELECT id, customer_id, staff_id, service_id, appointment_date, appointment_time, 
			  status, notes, total_price, created_at, updated_at 
			  FROM appointments WHERE staff_id = ? ALLOW FILTERING`
	iter := r.db.Session.Query(query, staffID.String()).Iter()
	
	var appointments []*models.Appointment
	var idStr, customerIDStr, staffIDStr, serviceIDStr string
	var appointmentDateStr, appointmentTimeStr string
	var status string
	var notes string
	var totalPrice float64
	var createdAt, updatedAt time.Time
	
	for iter.Scan(
		&idStr,
		&customerIDStr,
		&staffIDStr,
		&serviceIDStr,
		&appointmentDateStr,
		&appointmentTimeStr,
		&status,
		&notes,
		&totalPrice,
		&createdAt,
		&updatedAt,
	) {
		// Parse UUIDs from strings
		parsedID, err := uuid.Parse(idStr)
		if err != nil {
			continue // Skip invalid UUIDs
		}
		
		parsedCustomerID, err := uuid.Parse(customerIDStr)
		if err != nil {
			continue // Skip invalid UUIDs
		}
		
		parsedStaffID, err := uuid.Parse(staffIDStr)
		if err != nil {
			continue // Skip invalid UUIDs
		}
		
		parsedServiceID, err := uuid.Parse(serviceIDStr)
		if err != nil {
			continue // Skip invalid UUIDs
		}
		
		// Parse date and time from strings
		appointmentDate, err := time.Parse("2006-01-02", appointmentDateStr)
		if err != nil {
			continue // Skip invalid dates
		}
		
		appointmentTime, err := time.Parse("15:04:05", appointmentTimeStr)
		if err != nil {
			continue // Skip invalid times
		}
		
		appointments = append(appointments, &models.Appointment{
			ID:              parsedID,
			CustomerID:      parsedCustomerID,
			StaffID:         parsedStaffID,
			ServiceID:       parsedServiceID,
			AppointmentDate: appointmentDate,
			AppointmentTime: appointmentTime,
			Status:          models.AppointmentStatus(status),
			Notes:           notes,
			TotalPrice:      totalPrice,
			CreatedAt:       createdAt,
			UpdatedAt:       updatedAt,
		})
	}
	
	if err := iter.Close(); err != nil {
		return nil, fmt.Errorf("failed to retrieve appointments by staff: %w", err)
	}
	
	return appointments, nil
}

// FindByDate retrieves appointments by date
func (r *AppointmentRepository) FindByDate(date time.Time) ([]*models.Appointment, error) {
	query := `SELECT id, customer_id, staff_id, service_id, appointment_date, appointment_time, 
			  status, notes, total_price, created_at, updated_at 
			  FROM appointments WHERE appointment_date = ? ALLOW FILTERING`
	iter := r.db.Session.Query(query, date.Format("2006-01-02")).Iter()
	
	var appointments []*models.Appointment
	var idStr, customerIDStr, staffIDStr, serviceIDStr string
	var appointmentDateStr, appointmentTimeStr string
	var status string
	var notes string
	var totalPrice float64
	var createdAt, updatedAt time.Time
	
	for iter.Scan(
		&idStr,
		&customerIDStr,
		&staffIDStr,
		&serviceIDStr,
		&appointmentDateStr,
		&appointmentTimeStr,
		&status,
		&notes,
		&totalPrice,
		&createdAt,
		&updatedAt,
	) {
		// Parse UUIDs from strings
		parsedID, err := uuid.Parse(idStr)
		if err != nil {
			continue // Skip invalid UUIDs
		}
		parsedCustomerID, err := uuid.Parse(customerIDStr)
		if err != nil {
			continue // Skip invalid UUIDs
		}
		parsedStaffID, err := uuid.Parse(staffIDStr)
		if err != nil {
			continue // Skip invalid UUIDs
		}
		parsedServiceID, err := uuid.Parse(serviceIDStr)
		if err != nil {
			continue // Skip invalid UUIDs
		}
		
		// Parse date and time from strings
		appointmentDate, err := time.Parse("2006-01-02", appointmentDateStr)
		if err != nil {
			continue // Skip invalid dates
		}
		
		appointmentTime, err := time.Parse("15:04:05", appointmentTimeStr)
		if err != nil {
			continue // Skip invalid times
		}
		
		appointments = append(appointments, &models.Appointment{
			ID:              parsedID,
			CustomerID:      parsedCustomerID,
			StaffID:         parsedStaffID,
			ServiceID:       parsedServiceID,
			AppointmentDate: appointmentDate,
			AppointmentTime: appointmentTime,
			Status:          models.AppointmentStatus(status),
			Notes:           notes,
			TotalPrice:      totalPrice,
			CreatedAt:       createdAt,
			UpdatedAt:       updatedAt,
		})
	}
	
	if err := iter.Close(); err != nil {
		return nil, fmt.Errorf("failed to retrieve appointments by date: %w", err)
	}
	
	return appointments, nil
}

// Update updates an existing appointment
func (r *AppointmentRepository) Update(appointment *models.Appointment) error {
	appointment.UpdatedAt = time.Now()
	
	query := `UPDATE appointments SET customer_id = ?, staff_id = ?, service_id = ?, 
			  appointment_date = ?, appointment_time = ?, status = ?, notes = ?, 
			  total_price = ?, updated_at = ? WHERE id = ?`
	
	return r.db.Session.Query(query,
		appointment.CustomerID.String(),
		appointment.StaffID.String(),
		appointment.ServiceID.String(),
		appointment.AppointmentDate.Format("2006-01-02"),
		appointment.AppointmentTime.Format("15:04:05"),
		string(appointment.Status),
		appointment.Notes,
		fmt.Sprintf("%.2f", appointment.TotalPrice),
		appointment.UpdatedAt,
		appointment.ID.String(),
	).Exec()
}

// Delete removes an appointment by ID
func (r *AppointmentRepository) Delete(id uuid.UUID) error {
	query := `DELETE FROM appointments WHERE id = ?`
	return r.db.Session.Query(query, id.String()).Exec()
}

// Exists checks if an appointment exists by ID
func (r *AppointmentRepository) Exists(id uuid.UUID) (bool, error) {
	var count int
	query := `SELECT COUNT(*) FROM appointments WHERE id = ?`
	
	err := r.db.Session.Query(query, id.String()).Scan(&count)
	if err != nil {
		return false, fmt.Errorf("failed to check appointment existence: %w", err)
	}
	
	return count > 0, nil
}
