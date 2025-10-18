package handlers

import (
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
	"github.com/google/uuid"

	"beauty-salon-backend-go/internal/models"
	"beauty-salon-backend-go/internal/service"
)

// StaffHandler handles HTTP requests for staff
type StaffHandler struct {
	service *service.StaffService
}

// NewStaffHandler creates a new staff handler
func NewStaffHandler(service *service.StaffService) *StaffHandler {
	return &StaffHandler{service: service}
}

// CreateStaffRequest represents the request body for creating a staff member
type CreateStaffRequest struct {
	Name      string `json:"name" binding:"required"`
	Email     string `json:"email" binding:"required,email"`
	Role      string `json:"role" binding:"required"`
	Phone     string `json:"phone"`
	Specialty string `json:"specialty"`
}

// UpdateStaffRequest represents the request body for updating a staff member
type UpdateStaffRequest struct {
	Name      string `json:"name" binding:"required"`
	Email     string `json:"email" binding:"required,email"`
	Role      string `json:"role" binding:"required"`
	Phone     string `json:"phone"`
	Specialty string `json:"specialty"`
}

// CreateStaff handles POST /staff
func (h *StaffHandler) CreateStaff(c *gin.Context) {
	var req CreateStaffRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	staff, err := h.service.CreateStaff(req.Name, req.Email, req.Role, req.Phone)
	if err != nil {
		if err == models.ErrAlreadyExists {
			c.JSON(http.StatusConflict, gin.H{"error": "Staff with this email already exists"})
			return
		}
		if err == models.ErrInvalidName || err == models.ErrInvalidEmail || err == models.ErrInvalidRole {
			c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to create staff", "details": err.Error()})
		return
	}

	c.JSON(http.StatusCreated, staff)
}

// GetStaff handles GET /staff/:id
func (h *StaffHandler) GetStaff(c *gin.Context) {
	idStr := c.Param("id")
	id, err := uuid.Parse(idStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid staff ID"})
		return
	}

	staff, err := h.service.GetStaffByID(id)
	if err != nil {
		if err == models.ErrStaffNotFound {
			c.JSON(http.StatusNotFound, gin.H{"error": "Staff not found"})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get staff"})
		return
	}

	c.JSON(http.StatusOK, staff)
}

// GetAllStaff handles GET /staff
func (h *StaffHandler) GetAllStaff(c *gin.Context) {
	// Check for role filter
	role := c.Query("role")
	if role != "" {
		staff, err := h.service.GetStaffByRole(role)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get staff by role"})
			return
		}
		c.JSON(http.StatusOK, staff)
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

	staff, err := h.service.GetAllStaff()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get staff"})
		return
	}

	// Simple pagination (in production, implement at database level)
	total := len(staff)
	start := (page - 1) * limit
	end := start + limit

	if start >= total {
		c.JSON(http.StatusOK, gin.H{
			"staff": []*models.Staff{},
			"total": total,
			"page":  page,
			"limit": limit,
		})
		return
	}

	if end > total {
		end = total
	}

	paginatedStaff := staff[start:end]
	c.JSON(http.StatusOK, gin.H{
		"staff": paginatedStaff,
		"total": total,
		"page":  page,
		"limit": limit,
	})
}

// UpdateStaff handles PUT /staff/:id
func (h *StaffHandler) UpdateStaff(c *gin.Context) {
	idStr := c.Param("id")
	id, err := uuid.Parse(idStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid staff ID"})
		return
	}

	var req UpdateStaffRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	staff, err := h.service.UpdateStaff(id, req.Name, req.Email, req.Role, req.Phone)
	if err != nil {
		if err == models.ErrStaffNotFound {
			c.JSON(http.StatusNotFound, gin.H{"error": "Staff not found"})
			return
		}
		if err == models.ErrAlreadyExists {
			c.JSON(http.StatusConflict, gin.H{"error": "Staff with this email already exists"})
			return
		}
		if err == models.ErrInvalidName || err == models.ErrInvalidEmail || err == models.ErrInvalidRole {
			c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to update staff"})
		return
	}

	c.JSON(http.StatusOK, staff)
}

// DeleteStaff handles DELETE /staff/:id
func (h *StaffHandler) DeleteStaff(c *gin.Context) {
	idStr := c.Param("id")
	id, err := uuid.Parse(idStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid staff ID"})
		return
	}

	err = h.service.DeleteStaff(id)
	if err != nil {
		if err == models.ErrStaffNotFound {
			c.JSON(http.StatusNotFound, gin.H{"error": "Staff not found"})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to delete staff"})
		return
	}

	c.JSON(http.StatusNoContent, nil)
}

// GetStaffByEmail handles GET /staff/email/:email
func (h *StaffHandler) GetStaffByEmail(c *gin.Context) {
	email := c.Param("email")
	if email == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Email is required"})
		return
	}

	staff, err := h.service.GetStaffByEmail(email)
	if err != nil {
		if err == models.ErrStaffNotFound {
			c.JSON(http.StatusNotFound, gin.H{"error": "Staff not found"})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get staff"})
		return
	}

	c.JSON(http.StatusOK, staff)
}

// GetActiveStaff gets all active staff members
func (h *StaffHandler) GetActiveStaff(c *gin.Context) {
	staff, err := h.service.GetActiveStaff()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get active staff"})
		return
	}

	c.JSON(http.StatusOK, staff)
}

// GetStaffCount gets the total count of staff members
func (h *StaffHandler) GetStaffCount(c *gin.Context) {
	count, err := h.service.GetStaffCount()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"count": count})
}

// GetRoles gets all staff roles
func (h *StaffHandler) GetRoles(c *gin.Context) {
	roles, err := h.service.GetRoles()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get roles"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"roles": roles})
}

// SearchStaff searches staff by name or email
func (h *StaffHandler) SearchStaff(c *gin.Context) {
	query := c.Query("q")
	if query == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Query parameter 'q' is required"})
		return
	}

	limit := 50 // Default limit
	if limitStr := c.Query("limit"); limitStr != "" {
		if parsedLimit, err := strconv.Atoi(limitStr); err == nil && parsedLimit > 0 {
			limit = parsedLimit
		}
	}

	staff, err := h.service.SearchStaff(query, limit)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, staff)
}

// GetStaffByRole gets staff by role
func (h *StaffHandler) GetStaffByRole(c *gin.Context) {
	role := c.Param("role")
	if role == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Role is required"})
		return
	}

	staff, err := h.service.GetStaffByRole(role)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get staff by role"})
		return
	}

	c.JSON(http.StatusOK, staff)
}

// GetActiveStaffByRole gets active staff by role
func (h *StaffHandler) GetActiveStaffByRole(c *gin.Context) {
	role := c.Param("role")
	if role == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Role is required"})
		return
	}

	staff, err := h.service.GetActiveStaffByRole(role)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get active staff by role"})
		return
	}

	c.JSON(http.StatusOK, staff)
}

// GetStaffBySpecialty gets staff by specialty
func (h *StaffHandler) GetStaffBySpecialty(c *gin.Context) {
	specialty := c.Param("specialty")
	if specialty == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Specialty is required"})
		return
	}

	staff, err := h.service.GetStaffBySpecialty(specialty)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get staff by specialty"})
		return
	}

	c.JSON(http.StatusOK, staff)
}
