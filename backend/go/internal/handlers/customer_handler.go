package handlers

import (
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
	"github.com/google/uuid"

	"beauty-salon-backend-go/internal/models"
	"beauty-salon-backend-go/internal/service"
)

// CustomerHandler handles HTTP requests for customers
type CustomerHandler struct {
	service *service.CustomerService
}

// NewCustomerHandler creates a new customer handler
func NewCustomerHandler(service *service.CustomerService) *CustomerHandler {
	return &CustomerHandler{service: service}
}

// CreateCustomerRequest represents the request body for creating a customer
type CreateCustomerRequest struct {
	Name    string `json:"name" binding:"required"`
	Email   string `json:"email" binding:"required,email"`
	Phone   string `json:"phone"`
}

// UpdateCustomerRequest represents the request body for updating a customer
type UpdateCustomerRequest struct {
	Name    string `json:"name" binding:"required"`
	Email   string `json:"email" binding:"required,email"`
	Phone   string `json:"phone"`
}

// CreateCustomer handles POST /customers
func (h *CustomerHandler) CreateCustomer(c *gin.Context) {
	var req CreateCustomerRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	customer, err := h.service.CreateCustomer(req.Name, req.Email, req.Phone)
	if err != nil {
		if err == models.ErrAlreadyExists {
			c.JSON(http.StatusConflict, gin.H{"error": "Customer with this email already exists"})
			return
		}
		if err == models.ErrInvalidName || err == models.ErrInvalidEmail {
			c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to create customer", "details": err.Error()})
		return
	}

	c.JSON(http.StatusCreated, customer)
}

// GetCustomer handles GET /customers/:id
func (h *CustomerHandler) GetCustomer(c *gin.Context) {
	idStr := c.Param("id")
	id, err := uuid.Parse(idStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid customer ID"})
		return
	}

	customer, err := h.service.GetCustomerByID(id)
	if err != nil {
		if err == models.ErrCustomerNotFound {
			c.JSON(http.StatusNotFound, gin.H{"error": "Customer not found"})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get customer"})
		return
	}

	c.JSON(http.StatusOK, customer)
}

// GetAllCustomers handles GET /customers
func (h *CustomerHandler) GetAllCustomers(c *gin.Context) {
	// Check for search query parameter
	searchName := c.Query("name")
	if searchName != "" {
		customers, err := h.service.SearchCustomersByName(searchName)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to search customers"})
			return
		}
		c.JSON(http.StatusOK, customers)
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

	customers, err := h.service.GetAllCustomers()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get customers"})
		return
	}

	// Simple pagination (in production, implement at database level)
	total := len(customers)
	start := (page - 1) * limit
	end := start + limit

	if start >= total {
		c.JSON(http.StatusOK, gin.H{
			"customers": []*models.Customer{},
			"total":     total,
			"page":      page,
			"limit":     limit,
		})
		return
	}

	if end > total {
		end = total
	}

	paginatedCustomers := customers[start:end]
	c.JSON(http.StatusOK, gin.H{
		"customers": paginatedCustomers,
		"total":     total,
		"page":      page,
		"limit":     limit,
	})
}

// UpdateCustomer handles PUT /customers/:id
func (h *CustomerHandler) UpdateCustomer(c *gin.Context) {
	idStr := c.Param("id")
	id, err := uuid.Parse(idStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid customer ID"})
		return
	}

	var req UpdateCustomerRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	customer, err := h.service.UpdateCustomer(id, req.Name, req.Email, req.Phone)
	if err != nil {
		if err == models.ErrCustomerNotFound {
			c.JSON(http.StatusNotFound, gin.H{"error": "Customer not found"})
			return
		}
		if err == models.ErrAlreadyExists {
			c.JSON(http.StatusConflict, gin.H{"error": "Customer with this email already exists"})
			return
		}
		if err == models.ErrInvalidName || err == models.ErrInvalidEmail {
			c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to update customer"})
		return
	}

	c.JSON(http.StatusOK, customer)
}

// DeleteCustomer handles DELETE /customers/:id
func (h *CustomerHandler) DeleteCustomer(c *gin.Context) {
	idStr := c.Param("id")
	id, err := uuid.Parse(idStr)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Invalid customer ID"})
		return
	}

	err = h.service.DeleteCustomer(id)
	if err != nil {
		if err == models.ErrCustomerNotFound {
			c.JSON(http.StatusNotFound, gin.H{"error": "Customer not found"})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to delete customer"})
		return
	}

	c.JSON(http.StatusNoContent, nil)
}

// GetCustomerByEmail handles GET /customers/email/:email
func (h *CustomerHandler) GetCustomerByEmail(c *gin.Context) {
	email := c.Param("email")
	if email == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Email is required"})
		return
	}

	customer, err := h.service.GetCustomerByEmail(email)
	if err != nil {
		if err == models.ErrCustomerNotFound {
			c.JSON(http.StatusNotFound, gin.H{"error": "Customer not found"})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to get customer"})
		return
	}

	c.JSON(http.StatusOK, customer)
}

// GetCustomerCount gets the total count of customers
func (h *CustomerHandler) GetCustomerCount(c *gin.Context) {
	count, err := h.service.GetCustomerCount()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"count": count})
}

// SearchCustomers searches customers by name, email or phone
func (h *CustomerHandler) SearchCustomers(c *gin.Context) {
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

	customers, err := h.service.SearchCustomers(query, limit)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, customers)
}
