package routes

import (
	"net/http"
	"time"

	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"

	"beauty-salon-backend-go/internal/config"
	"beauty-salon-backend-go/internal/handlers"
)

// SetupRoutes configures all routes for the application
func SetupRoutes(
	cfg *config.Config,
	customerHandler *handlers.CustomerHandler,
	serviceHandler *handlers.ServiceHandler,
	staffHandler *handlers.StaffHandler,
	appointmentHandler *handlers.AppointmentHandler,
) *gin.Engine {
	// Set Gin mode based on environment
	if cfg.Logging.Level == "debug" {
		gin.SetMode(gin.DebugMode)
	} else {
		gin.SetMode(gin.ReleaseMode)
	}

	router := gin.New()

	// Middleware
	router.Use(gin.Logger())
	router.Use(gin.Recovery())

	// CORS middleware
	corsConfig := cors.Config{
		AllowOrigins:     cfg.CORS.AllowedOrigins,
		AllowMethods:     cfg.CORS.AllowedMethods,
		AllowHeaders:     cfg.CORS.AllowedHeaders,
		ExposeHeaders:    []string{"Content-Length"},
		AllowCredentials: true,
		MaxAge:           12 * time.Hour,
	}
	router.Use(cors.New(corsConfig))

	// Health check endpoint
	if cfg.Health.Enabled {
		router.GET(cfg.Health.Endpoint, func(c *gin.Context) {
			c.JSON(http.StatusOK, gin.H{
				"status":    "healthy",
				"timestamp": time.Now().UTC(),
				"service":   "beauty-salon-api",
				"version":   "1.0.0",
			})
		})
	}

	// API v1 routes
	v1 := router.Group("/api/v1")
	{
		// Customer routes
		customers := v1.Group("/customers")
		{
			customers.POST("", customerHandler.CreateCustomer)
			customers.GET("", customerHandler.GetAllCustomers)
			customers.GET("/:id", customerHandler.GetCustomer)
			customers.PUT("/:id", customerHandler.UpdateCustomer)
			customers.DELETE("/:id", customerHandler.DeleteCustomer)
			customers.GET("/email/:email", customerHandler.GetCustomerByEmail)
		}

		// Service routes
		services := v1.Group("/services")
		{
			services.POST("", serviceHandler.CreateService)
			services.GET("", serviceHandler.GetAllServices)
			services.GET("/:id", serviceHandler.GetService)
			services.PUT("/:id", serviceHandler.UpdateService)
			services.DELETE("/:id", serviceHandler.DeleteService)
		}

		// Staff routes
		staff := v1.Group("/staff")
		{
			staff.POST("", staffHandler.CreateStaff)
			staff.GET("", staffHandler.GetAllStaff)
			staff.GET("/:id", staffHandler.GetStaff)
			staff.PUT("/:id", staffHandler.UpdateStaff)
			staff.DELETE("/:id", staffHandler.DeleteStaff)
			staff.GET("/email/:email", staffHandler.GetStaffByEmail)
		}

		// Appointment routes
		appointments := v1.Group("/appointments")
		{
			appointments.POST("", appointmentHandler.CreateAppointment)
			appointments.GET("", appointmentHandler.GetAllAppointments)
			appointments.GET("/:id", appointmentHandler.GetAppointment)
			appointments.PUT("/:id", appointmentHandler.UpdateAppointment)
			appointments.DELETE("/:id", appointmentHandler.DeleteAppointment)
			appointments.PATCH("/:id/status", appointmentHandler.UpdateAppointmentStatus)
		}
	}

	// Root endpoint
	router.GET("/", func(c *gin.Context) {
		c.JSON(http.StatusOK, gin.H{
			"message": "Beauty Salon API",
			"version": "1.0.0",
			"status":  "running",
			"endpoints": gin.H{
				"health":       cfg.Health.Endpoint,
				"customers":    "/api/v1/customers",
				"services":     "/api/v1/services",
				"staff":        "/api/v1/staff",
				"appointments": "/api/v1/appointments",
			},
		})
	})

	return router
}
