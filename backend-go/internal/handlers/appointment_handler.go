package handlers

import (
	"log"
	"net/http"
	"strconv"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/google/uuid"

	"beauty-salon-backend-go/internal/models"
	"beauty-salon-backend-go/internal/service"
)

// AppointmentHandler handles HTTP requests for appointments
type AppointmentHandler struct {
	service *service.AppointmentService
}

// NewAppointmentHandler creates a new appointment handler
func NewAppointmentHandler(service *service.AppointmentService) *AppointmentHandler {
	return &AppointmentHandler{service: service}
}

// CreateAppointmentRequest represents the request body for creating an appointment
type CreateAppointmentRequest struct {
	CustomerID      string `json:"customer_id" binding:"required"`
	StaffID         string `json:"staff_id" binding:"required"`
	ServiceID       string `json:"service_id" binding:"required"`
	AppointmentDate string `json:"appointment_date" binding:"required"`
	AppointmentTime string `json:"appointment_time" binding:"required"`
	Notes           string `json:"notes"`
}

// UpdateAppointmentRequest represents the request body for updating an appointment
type UpdateAppointmentRequest struct {
	CustomerID      string `json:"customer_id" binding:"required"`
	StaffID         string `json:"staff_id" binding:"required"`
	ServiceID       string `json:"service_id" binding:"required"`
	AppointmentDate string `json:"appointment_date" binding:"required"`
	AppointmentTime string `json:"appointment_time" binding:"required"`
	Status          string `json:"status" binding:"required"`
	Notes           string `json:"notes"`
}

// UpdateAppointmentStatusRequest represents the request body for updating appointment status
type UpdateAppointmentStatusRequest struct {
	Status string `json:"status" binding:"required"`
}

// CreateAppointment handles POST /appointments
func (h *AppointmentHandler) CreateAppointment(c *gin.Context) {
	var req CreateAppointmentRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	// Parse UUIDs
	customerID, err := uuid.Parse(req.CustomerID)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid customer ID"})
		return
	}

	staffID, err := uuid.Parse(req.StaffID)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid staff ID"})
		return
	}

	serviceID, err := uuid.Parse(req.ServiceID)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid service ID"})
		return
	}

	// Parse date and time
	appointmentDate, err := time.Parse("2006-01-02", req.AppointmentDate)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid appointment date format (YYYY-MM-DD)"})
		return
	}

	appointmentTime, err := time.Parse("15:04", req.AppointmentTime)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid appointment time format (HH:MM)"})
		return
	}

	log.Printf("Creating appointment with parsed data: customerID=%s, staffID=%s, serviceID=%s, date=%s, time=%s", 
		customerID, staffID, serviceID, appointmentDate.Format("2006-01-02"), appointmentTime.Format("15:04:05"))
	
	appointment, err := h.service.CreateAppointment(customerID, staffID, serviceID, appointmentDate, appointmentTime, req.Notes)
	if err != nil {
		log.Printf("Error creating appointment: %v", err)
		if err == models.ErrCustomerNotFound {
			c.JSON(http.StatusBadRequest, gin.H{"error": "Customer not found"})
			return
		}
		if err == models.ErrStaffNotFound {
			c.JSON(http.StatusBadRequest, gin.H{"error": "Staff not found"})
			return
		}
		if err == models.ErrServiceNotFound {
			c.JSON(http.StatusBadRequest, gin.H{"error": "Service not found"})
			return
		}
		if err == models.ErrConflictingBooking {
			c.JSON(http.StatusConflict, gin.H{"error": "Conflicting appointment booking"})
			return
		}
		if err == models.ErrInvalidDate || err == models.ErrInvalidTime {
			c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to create appointment", "details": err.Error()})
		return
	}

	c.JSON(http.StatusCreated, appointment)
}

// GetAppointment handles GET /appointments/:id
func (h *AppointmentHandler) GetAppointment(c *gin.Context) {
	idStr := c.Param("id")
	id, err := uuid.Parse(idStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid appointment ID"})
		return
	}

	appointment, err := h.service.GetAppointmentByID(id)
	if err != nil {
		if err == models.ErrAppointmentNotFound {
			c.JSON(http.StatusNotFound, gin.H{"error": "Appointment not found"})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get appointment"})
		return
	}

	c.JSON(http.StatusOK, appointment)
}

// GetAllAppointments handles GET /appointments
func (h *AppointmentHandler) GetAllAppointments(c *gin.Context) {
	// Check for customer filter
	if customerIDStr := c.Query("customer_id"); customerIDStr != "" {
		customerID, err := uuid.Parse(customerIDStr)
		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid customer ID"})
			return
		}
		appointments, err := h.service.GetAppointmentsByCustomerID(customerID)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get appointments by customer"})
			return
		}
		c.JSON(http.StatusOK, appointments)
		return
	}

	// Check for staff filter
	if staffIDStr := c.Query("staff_id"); staffIDStr != "" {
		staffID, err := uuid.Parse(staffIDStr)
		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid staff ID"})
			return
		}
		appointments, err := h.service.GetAppointmentsByStaffID(staffID)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get appointments by staff"})
			return
		}
		c.JSON(http.StatusOK, appointments)
		return
	}

	// Check for date filter
	if dateStr := c.Query("date"); dateStr != "" {
		date, err := time.Parse("2006-01-02", dateStr)
		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid date format (YYYY-MM-DD)"})
			return
		}
		appointments, err := h.service.GetAppointmentsByDate(date)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get appointments by date"})
			return
		}
		c.JSON(http.StatusOK, appointments)
		return
	}

	// Check for pagination parameters
	page := 1
	limit := 10
	if pageStr := c.Query("page"); pageStr != "" {
		if p, err := strconv.Atoi(pageStr); err == nil && p > 0 {
			page = p
		}
	}
	if limitStr := c.Query("limit"); limitStr != "" {
		if l, err := strconv.Atoi(limitStr); err == nil && l > 0 && l <= 100 {
			limit = l
		}
	}

	appointments, err := h.service.GetAllAppointments()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get appointments"})
		return
	}

	// Simple pagination (in production, implement at database level)
	total := len(appointments)
	start := (page - 1) * limit
	end := start + limit

	if start >= total {
		c.JSON(http.StatusOK, gin.H{
			"appointments": []*models.Appointment{},
			"total":        total,
			"page":         page,
			"limit":        limit,
		})
		return
	}

	if end > total {
		end = total
	}

	paginatedAppointments := appointments[start:end]
	c.JSON(http.StatusOK, gin.H{
		"appointments": paginatedAppointments,
		"total":        total,
		"page":         page,
		"limit":        limit,
	})
}

// UpdateAppointment handles PUT /appointments/:id
func (h *AppointmentHandler) UpdateAppointment(c *gin.Context) {
	idStr := c.Param("id")
	id, err := uuid.Parse(idStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid appointment ID"})
		return
	}

	var req UpdateAppointmentRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	// Parse UUIDs
	customerID, err := uuid.Parse(req.CustomerID)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid customer ID"})
		return
	}

	staffID, err := uuid.Parse(req.StaffID)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid staff ID"})
		return
	}

	serviceID, err := uuid.Parse(req.ServiceID)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid service ID"})
		return
	}

	// Parse date and time
	appointmentDate, err := time.Parse("2006-01-02", req.AppointmentDate)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid appointment date format (YYYY-MM-DD)"})
		return
	}

	appointmentTime, err := time.Parse("15:04", req.AppointmentTime)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid appointment time format (HH:MM)"})
		return
	}

	// Parse status
	status := models.AppointmentStatus(req.Status)

	appointment, err := h.service.UpdateAppointment(id, customerID, staffID, serviceID, appointmentDate, appointmentTime, status, req.Notes)
	if err != nil {
		if err == models.ErrAppointmentNotFound {
			c.JSON(http.StatusNotFound, gin.H{"error": "Appointment not found"})
			return
		}
		if err == models.ErrCustomerNotFound {
			c.JSON(http.StatusBadRequest, gin.H{"error": "Customer not found"})
			return
		}
		if err == models.ErrStaffNotFound {
			c.JSON(http.StatusBadRequest, gin.H{"error": "Staff not found"})
			return
		}
		if err == models.ErrServiceNotFound {
			c.JSON(http.StatusBadRequest, gin.H{"error": "Service not found"})
			return
		}
		if err == models.ErrConflictingBooking {
			c.JSON(http.StatusConflict, gin.H{"error": "Conflicting appointment booking"})
			return
		}
		if err == models.ErrInvalidDate || err == models.ErrInvalidTime || err == models.ErrInvalidStatus {
			c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to update appointment"})
		return
	}

	c.JSON(http.StatusOK, appointment)
}

// UpdateAppointmentStatus handles PATCH /appointments/:id/status
func (h *AppointmentHandler) UpdateAppointmentStatus(c *gin.Context) {
	idStr := c.Param("id")
	id, err := uuid.Parse(idStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid appointment ID"})
		return
	}

	var req UpdateAppointmentStatusRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	status := models.AppointmentStatus(req.Status)

	appointment, err := h.service.UpdateAppointmentStatus(id, status)
	if err != nil {
		if err == models.ErrAppointmentNotFound {
			c.JSON(http.StatusNotFound, gin.H{"error": "Appointment not found"})
			return
		}
		if err == models.ErrInvalidStatus {
			c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to update appointment status"})
		return
	}

	c.JSON(http.StatusOK, appointment)
}

// DeleteAppointment handles DELETE /appointments/:id
func (h *AppointmentHandler) DeleteAppointment(c *gin.Context) {
	idStr := c.Param("id")
	id, err := uuid.Parse(idStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid appointment ID"})
		return
	}

	err = h.service.DeleteAppointment(id)
	if err != nil {
		if err == models.ErrAppointmentNotFound {
			c.JSON(http.StatusNotFound, gin.H{"error": "Appointment not found"})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to delete appointment"})
		return
	}

	c.JSON(http.StatusNoContent, nil)
}
