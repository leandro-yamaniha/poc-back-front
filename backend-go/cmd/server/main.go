package main

import (
	"context"
	"fmt"
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"beauty-salon-backend-go/internal/config"
	"beauty-salon-backend-go/internal/handlers"
	"beauty-salon-backend-go/internal/repository"
	"beauty-salon-backend-go/internal/routes"
	"beauty-salon-backend-go/internal/service"
	"beauty-salon-backend-go/pkg/database"
)

func main() {
	// Load configuration
	cfg := config.LoadConfig()

	// Initialize database
	db, err := database.NewCassandraDBFromEnv()
	if err != nil {
		log.Fatalf("Failed to connect to database: %v", err)
	}
	defer db.Close()

	// Create keyspace and tables
	if err := db.CreateKeyspaceIfNotExists(); err != nil {
		log.Fatalf("Failed to create keyspace: %v", err)
	}

	if err := db.CreateTables(); err != nil {
		log.Fatalf("Failed to create tables: %v", err)
	}

	// Initialize repositories
	customerRepo := repository.NewCustomerRepository(db)
	serviceRepo := repository.NewServiceRepository(db)
	staffRepo := repository.NewStaffRepository(db)
	appointmentRepo := repository.NewAppointmentRepository(db)

	// Initialize services
	customerService := service.NewCustomerService(customerRepo)
	serviceService := service.NewServiceService(serviceRepo)
	staffService := service.NewStaffService(staffRepo)
	appointmentService := service.NewAppointmentService(appointmentRepo, customerService, staffService, serviceService)

	// Initialize handlers
	customerHandler := handlers.NewCustomerHandler(customerService)
	serviceHandler := handlers.NewServiceHandler(serviceService)
	staffHandler := handlers.NewStaffHandler(staffService)
	appointmentHandler := handlers.NewAppointmentHandler(appointmentService)

	// Setup routes
	router := routes.SetupRoutes(cfg, customerHandler, serviceHandler, staffHandler, appointmentHandler)

	// Create HTTP server
	server := &http.Server{
		Addr:         fmt.Sprintf("%s:%s", cfg.Server.Host, cfg.Server.Port),
		Handler:      router,
		ReadTimeout:  time.Duration(cfg.Server.ReadTimeout) * time.Second,
		WriteTimeout: time.Duration(cfg.Server.WriteTimeout) * time.Second,
		IdleTimeout:  time.Duration(cfg.Server.IdleTimeout) * time.Second,
	}

	// Start server in a goroutine
	go func() {
		log.Printf("Starting Beauty Salon API server on %s:%s", cfg.Server.Host, cfg.Server.Port)
		log.Printf("Health check endpoint: %s", cfg.Health.Endpoint)
		log.Printf("API endpoints available at: /api/v1/")
		
		if err := server.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			log.Fatalf("Failed to start server: %v", err)
		}
	}()

	// Wait for interrupt signal to gracefully shutdown the server
	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	<-quit
	log.Println("Shutting down server...")

	// Give outstanding requests 30 seconds to complete
	ctx, cancel := context.WithTimeout(context.Background(), 30*time.Second)
	defer cancel()

	if err := server.Shutdown(ctx); err != nil {
		log.Fatalf("Server forced to shutdown: %v", err)
	}

	log.Println("Server exited gracefully")
}
