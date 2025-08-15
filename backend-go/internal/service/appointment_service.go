package service

import (
	"fmt"
	"log"
	"time"

	"github.com/google/uuid"

	"beauty-salon-backend-go/internal/models"
	"beauty-salon-backend-go/internal/repository"
)

// AppointmentService handles business logic for appointments
type AppointmentService struct {
	repo            *repository.AppointmentRepository
	customerService *CustomerService
	staffService    *StaffService
	serviceService  *ServiceService
}

// NewAppointmentService creates a new appointment service
func NewAppointmentService(
	repo *repository.AppointmentRepository,
	customerService *CustomerService,
	staffService *StaffService,
	serviceService *ServiceService,
) *AppointmentService {
	return &AppointmentService{
		repo:            repo,
		customerService: customerService,
		staffService:    staffService,
		serviceService:  serviceService,
	}
}

// CreateAppointment creates a new appointment
func (s *AppointmentService) CreateAppointment(customerID, staffID, serviceID uuid.UUID, appointmentDate, appointmentTime time.Time, notes string) (*models.Appointment, error) {
	log.Printf("CreateAppointment: Starting validation checks")
	
	// Validate that customer exists
	customerExists, err := s.customerService.CustomerExists(customerID)
	if err != nil {
		return nil, fmt.Errorf("failed to check customer existence: %w", err)
	}
	if !customerExists {
		return nil, models.ErrCustomerNotFound
	}
	log.Printf("CreateAppointment: Customer validation passed")

	// Validate that staff exists
	staffExists, err := s.staffService.StaffExists(staffID)
	if err != nil {
		return nil, fmt.Errorf("failed to check staff existence: %w", err)
	}
	if !staffExists {
		return nil, models.ErrStaffNotFound
	}
	log.Printf("CreateAppointment: Staff validation passed")

	// Validate that service exists and get price
	service, err := s.serviceService.GetServiceByID(serviceID)
	if err != nil {
		return nil, fmt.Errorf("failed to get service: %w", err)
	}
	log.Printf("CreateAppointment: Service validation passed, price: %f", service.Price)

	// Validate appointment date and time
	if appointmentDate.IsZero() {
		return nil, models.ErrInvalidDate
	}
	if appointmentTime.IsZero() {
		return nil, models.ErrInvalidTime
	}
	log.Printf("CreateAppointment: Date/time validation passed")

	// Check for conflicting appointments (same staff, same date and time)
	log.Printf("CreateAppointment: Checking for conflicts")
	if err := s.checkForConflicts(staffID, appointmentDate, appointmentTime); err != nil {
		return nil, err
	}
	log.Printf("CreateAppointment: Conflict check passed")

	// Create new appointment
	appointment := models.NewAppointment(customerID, staffID, serviceID, appointmentDate, appointmentTime, notes, service.Price)
	log.Printf("CreateAppointment: Appointment object created, attempting to save to database")
	
	if err := s.repo.Create(appointment); err != nil {
		log.Printf("CreateAppointment: Database create failed: %v", err)
		return nil, fmt.Errorf("failed to create appointment: %w", err)
	}

	log.Printf("Created appointment: %s (ID: %s)", appointment.ID, appointment.ID)
	return appointment, nil
}

// GetAppointmentByID retrieves an appointment by ID
func (s *AppointmentService) GetAppointmentByID(id uuid.UUID) (*models.Appointment, error) {
	appointment, err := s.repo.FindByID(id)
	if err != nil {
		if err == models.ErrNotFound {
			return nil, models.ErrAppointmentNotFound
		}
		return nil, fmt.Errorf("failed to get appointment: %w", err)
	}
	return appointment, nil
}

// GetAllAppointments retrieves all appointments
func (s *AppointmentService) GetAllAppointments() ([]*models.Appointment, error) {
	appointments, err := s.repo.FindAll()
	if err != nil {
		return nil, fmt.Errorf("failed to get all appointments: %w", err)
	}
	return appointments, nil
}

// GetAppointmentsByCustomerID retrieves appointments by customer ID
func (s *AppointmentService) GetAppointmentsByCustomerID(customerID uuid.UUID) ([]*models.Appointment, error) {
	appointments, err := s.repo.FindByCustomerID(customerID)
	if err != nil {
		return nil, fmt.Errorf("failed to get appointments by customer: %w", err)
	}
	return appointments, nil
}

// GetAppointmentsByStaffID retrieves appointments by staff ID
func (s *AppointmentService) GetAppointmentsByStaffID(staffID uuid.UUID) ([]*models.Appointment, error) {
	appointments, err := s.repo.FindByStaffID(staffID)
	if err != nil {
		return nil, fmt.Errorf("failed to get appointments by staff: %w", err)
	}
	return appointments, nil
}

// GetAppointmentsByDate retrieves appointments by date
func (s *AppointmentService) GetAppointmentsByDate(date time.Time) ([]*models.Appointment, error) {
	appointments, err := s.repo.FindByDate(date)
	if err != nil {
		return nil, fmt.Errorf("failed to get appointments by date: %w", err)
	}
	return appointments, nil
}

// UpdateAppointment updates an existing appointment
func (s *AppointmentService) UpdateAppointment(id uuid.UUID, customerID, staffID, serviceID uuid.UUID, appointmentDate, appointmentTime time.Time, status models.AppointmentStatus, notes string) (*models.Appointment, error) {
	// Check if appointment exists
	appointment, err := s.repo.FindByID(id)
	if err != nil {
		if err == models.ErrNotFound {
			return nil, models.ErrAppointmentNotFound
		}
		return nil, fmt.Errorf("failed to find appointment: %w", err)
	}

	// Validate that customer exists
	customerExists, err := s.customerService.CustomerExists(customerID)
	if err != nil {
		return nil, fmt.Errorf("failed to check customer existence: %w", err)
	}
	if !customerExists {
		return nil, models.ErrCustomerNotFound
	}

	// Validate that staff exists
	staffExists, err := s.staffService.StaffExists(staffID)
	if err != nil {
		return nil, fmt.Errorf("failed to check staff existence: %w", err)
	}
	if !staffExists {
		return nil, models.ErrStaffNotFound
	}

	// Validate that service exists and get price
	service, err := s.serviceService.GetServiceByID(serviceID)
	if err != nil {
		return nil, fmt.Errorf("failed to get service: %w", err)
	}

	// Validate appointment date and time
	if appointmentDate.IsZero() {
		return nil, models.ErrInvalidDate
	}
	if appointmentTime.IsZero() {
		return nil, models.ErrInvalidTime
	}

	// Validate status
	if !appointment.IsValidStatus(status) {
		return nil, models.ErrInvalidStatus
	}

	// Check for conflicts if staff, date, or time changed
	if appointment.StaffID != staffID || !appointment.AppointmentDate.Equal(appointmentDate) || !appointment.AppointmentTime.Equal(appointmentTime) {
		if err := s.checkForConflicts(staffID, appointmentDate, appointmentTime); err != nil {
			return nil, err
		}
	}

	// Update appointment fields
	appointment.CustomerID = customerID
	appointment.StaffID = staffID
	appointment.ServiceID = serviceID
	appointment.AppointmentDate = appointmentDate
	appointment.AppointmentTime = appointmentTime
	appointment.Status = status
	appointment.Notes = notes
	appointment.TotalPrice = service.Price
	appointment.UpdateTimestamp()

	if err := s.repo.Update(appointment); err != nil {
		return nil, fmt.Errorf("failed to update appointment: %w", err)
	}

	log.Printf("Updated appointment: %s (ID: %s)", appointment.ID, appointment.ID)
	return appointment, nil
}

// UpdateAppointmentStatus updates only the status of an appointment
func (s *AppointmentService) UpdateAppointmentStatus(id uuid.UUID, status models.AppointmentStatus) (*models.Appointment, error) {
	// Check if appointment exists
	appointment, err := s.repo.FindByID(id)
	if err != nil {
		if err == models.ErrNotFound {
			return nil, models.ErrAppointmentNotFound
		}
		return nil, fmt.Errorf("failed to find appointment: %w", err)
	}

	// Validate status
	if !appointment.IsValidStatus(status) {
		return nil, models.ErrInvalidStatus
	}

	// Update status
	appointment.Status = status
	appointment.UpdateTimestamp()

	if err := s.repo.Update(appointment); err != nil {
		return nil, fmt.Errorf("failed to update appointment status: %w", err)
	}

	log.Printf("Updated appointment status: %s -> %s (ID: %s)", appointment.Status, status, appointment.ID)
	return appointment, nil
}

// DeleteAppointment deletes an appointment by ID
func (s *AppointmentService) DeleteAppointment(id uuid.UUID) error {
	// Check if appointment exists
	exists, err := s.repo.Exists(id)
	if err != nil {
		return fmt.Errorf("failed to check appointment existence: %w", err)
	}
	if !exists {
		return models.ErrAppointmentNotFound
	}

	if err := s.repo.Delete(id); err != nil {
		return fmt.Errorf("failed to delete appointment: %w", err)
	}

	log.Printf("Deleted appointment with ID: %s", id)
	return nil
}

// checkForConflicts checks if there are conflicting appointments
func (s *AppointmentService) checkForConflicts(staffID uuid.UUID, appointmentDate, appointmentTime time.Time) error {
	// Get appointments for the staff on the given date
	appointments, err := s.repo.FindByStaffID(staffID)
	if err != nil {
		return fmt.Errorf("failed to check for conflicts: %w", err)
	}

	// Check for time conflicts
	for _, existing := range appointments {
		if existing.AppointmentDate.Equal(appointmentDate) && existing.AppointmentTime.Equal(appointmentTime) {
			if existing.Status != models.StatusCancelled {
				return models.ErrConflictingBooking
			}
		}
	}

	return nil
}
