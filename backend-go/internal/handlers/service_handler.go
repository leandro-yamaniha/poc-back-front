package handlers

import (
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
	"github.com/google/uuid"

	"beauty-salon-backend-go/internal/models"
	"beauty-salon-backend-go/internal/service"
)

// ServiceHandler handles HTTP requests for services
type ServiceHandler struct {
	service *service.ServiceService
}

// NewServiceHandler creates a new service handler
func NewServiceHandler(service *service.ServiceService) *ServiceHandler {
	return &ServiceHandler{service: service}
}

// CreateServiceRequest represents the request body for creating a service
type CreateServiceRequest struct {
	Name        string  `json:"name" binding:"required"`
	Description string  `json:"description"`
	Price       float64 `json:"price" binding:"required,gt=0"`
	Category    string  `json:"category"`
}

// UpdateServiceRequest represents the request body for updating a service
type UpdateServiceRequest struct {
	Name        string  `json:"name" binding:"required"`
	Description string  `json:"description"`
	Price       float64 `json:"price" binding:"required,gt=0"`
	Category    string  `json:"category"`
}

// CreateService handles POST /services
func (h *ServiceHandler) CreateService(c *gin.Context) {
	var req CreateServiceRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	service, err := h.service.CreateService(req.Name, req.Description, req.Category, req.Price)
	if err != nil {
		if err == models.ErrInvalidName || err == models.ErrInvalidPrice {
			c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to create service", "details": err.Error()})
		return
	}

	c.JSON(http.StatusCreated, service)
}

// GetService handles GET /services/:id
func (h *ServiceHandler) GetService(c *gin.Context) {
	idStr := c.Param("id")
	id, err := uuid.Parse(idStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid service ID"})
		return
	}

	service, err := h.service.GetServiceByID(id)
	if err != nil {
		if err == models.ErrServiceNotFound {
			c.JSON(http.StatusNotFound, gin.H{"error": "Service not found"})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get service"})
		return
	}

	c.JSON(http.StatusOK, service)
}

// GetAllServices handles GET /services
func (h *ServiceHandler) GetAllServices(c *gin.Context) {
	// Check for category filter
	category := c.Query("category")
	if category != "" {
		services, err := h.service.GetServicesByCategory(category)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get services by category"})
			return
		}
		c.JSON(http.StatusOK, services)
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

	services, err := h.service.GetAllServices()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get services"})
		return
	}

	// Simple pagination (in production, implement at database level)
	total := len(services)
	start := (page - 1) * limit
	end := start + limit

	if start >= total {
		c.JSON(http.StatusOK, gin.H{
			"services": []*models.Service{},
			"total":    total,
			"page":     page,
			"limit":    limit,
		})
		return
	}

	if end > total {
		end = total
	}

	paginatedServices := services[start:end]
	c.JSON(http.StatusOK, gin.H{
		"services": paginatedServices,
		"total":    total,
		"page":     page,
		"limit":    limit,
	})
}

// UpdateService handles PUT /services/:id
func (h *ServiceHandler) UpdateService(c *gin.Context) {
	idStr := c.Param("id")
	id, err := uuid.Parse(idStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid service ID"})
		return
	}

	var req UpdateServiceRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	service, err := h.service.UpdateService(id, req.Name, req.Description, req.Category, req.Price)
	if err != nil {
		if err == models.ErrServiceNotFound {
			c.JSON(http.StatusNotFound, gin.H{"error": "Service not found"})
			return
		}
		if err == models.ErrInvalidName || err == models.ErrInvalidPrice {
			c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to update service"})
		return
	}

	c.JSON(http.StatusOK, service)
}

// DeleteService handles DELETE /services/:id
func (h *ServiceHandler) DeleteService(c *gin.Context) {
	idStr := c.Param("id")
	id, err := uuid.Parse(idStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid service ID"})
		return
	}

	err = h.service.DeleteService(id)
	if err != nil {
		if err == models.ErrServiceNotFound {
			c.JSON(http.StatusNotFound, gin.H{"error": "Service not found"})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to delete service"})
		return
	}

	c.JSON(http.StatusNoContent, nil)
}
