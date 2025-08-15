# Beauty Salon Go Backend

A RESTful API backend for a beauty salon management system built with Go, Gin framework, and Apache Cassandra.

## Features

- **Customer Management**: CRUD operations for customer data
- **Service Management**: Manage salon services with categories and pricing
- **Staff Management**: Handle staff members with roles and contact information
- **Appointment Scheduling**: Book, update, and manage appointments
- **RESTful API**: Clean REST endpoints with proper HTTP status codes
- **Database**: Apache Cassandra for scalable data storage
- **CORS Support**: Configurable cross-origin resource sharing
- **Health Checks**: Built-in health monitoring endpoints
- **Graceful Shutdown**: Proper server shutdown handling

## Technology Stack

- **Language**: Go 1.21+
- **Web Framework**: Gin
- **Database**: Apache Cassandra 4.1+
- **Database Driver**: gocql
- **UUID**: Google UUID library
- **CORS**: gin-contrib/cors

## Project Structure

```
backend-go/
├── cmd/server/          # Application entry point
├── internal/
│   ├── config/          # Configuration management
│   ├── handlers/        # HTTP request handlers
│   ├── models/          # Data models and validation
│   ├── repository/      # Database access layer
│   ├── routes/          # Route definitions
│   └── service/         # Business logic layer
├── pkg/database/        # Database connection utilities
├── .env.example         # Environment variables template
├── go.mod              # Go module definition
├── Makefile            # Build and development commands
└── README.md           # This file
```

## Getting Started

### Prerequisites

- Go 1.21 or higher
- Apache Cassandra 4.1+ (or Docker)
- Make (optional, for using Makefile commands)

### Installation

1. **Clone the repository**:
   ```bash
   cd backend-go
   ```

2. **Install dependencies**:
   ```bash
   make deps
   # or
   go mod download
   ```

3. **Setup environment**:
   ```bash
   make env-copy
   # Edit .env file with your configuration
   ```

4. **Start Cassandra** (if using Docker):
   ```bash
   make db-setup
   # or
   docker run -d --name cassandra -p 9042:9042 cassandra:4.1
   ```

5. **Run the application**:
   ```bash
   make run
   # or
   go run cmd/server/main.go
   ```

The API will be available at `http://localhost:8080`

### Environment Variables

Copy `.env.example` to `.env` and configure:

```bash
# Server Configuration
SERVER_PORT=8080
SERVER_HOST=0.0.0.0

# Database Configuration
CASSANDRA_HOSTS=localhost:9042
CASSANDRA_KEYSPACE=beauty_salon
CASSANDRA_DATACENTER=datacenter1

# Logging
LOG_LEVEL=info
LOG_FORMAT=json

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:3000

# Health Check
HEALTH_ENABLED=true
HEALTH_ENDPOINT=/health
```

## API Endpoints

### Health Check
- `GET /health` - Health check endpoint

### Customers
- `POST /api/v1/customers` - Create a new customer
- `GET /api/v1/customers` - Get all customers
- `GET /api/v1/customers/:id` - Get customer by ID
- `PUT /api/v1/customers/:id` - Update customer
- `DELETE /api/v1/customers/:id` - Delete customer
- `GET /api/v1/customers/email/:email` - Get customer by email
- `GET /api/v1/customers/count` - Get total customer count
- `GET /api/v1/customers/search?q=query&limit=10` - Search customers

### Services
- `POST /api/v1/services` - Create a new service
- `GET /api/v1/services` - Get all services
- `GET /api/v1/services/:id` - Get service by ID
- `PUT /api/v1/services/:id` - Update service
- `DELETE /api/v1/services/:id` - Delete service
- `GET /api/v1/services/active` - Get active services only
- `GET /api/v1/services/count` - Get total service count
- `GET /api/v1/services/categories` - Get all service categories
- `GET /api/v1/services/search?q=query&limit=10` - Search services
- `GET /api/v1/services/category/:category/active` - Get active services by category

### Staff
- `POST /api/v1/staff` - Create a new staff member
- `GET /api/v1/staff` - Get all staff
- `GET /api/v1/staff/:id` - Get staff by ID
- `PUT /api/v1/staff/:id` - Update staff
- `DELETE /api/v1/staff/:id` - Delete staff
- `GET /api/v1/staff/email/:email` - Get staff by email
- `GET /api/v1/staff/active` - Get active staff only
- `GET /api/v1/staff/count` - Get total staff count
- `GET /api/v1/staff/roles` - Get all staff roles
- `GET /api/v1/staff/search?q=query&limit=10` - Search staff
- `GET /api/v1/staff/role/:role` - Get staff by role
- `GET /api/v1/staff/role/:role/active` - Get active staff by role
- `GET /api/v1/staff/specialty/:specialty` - Get staff by specialty

### Appointments
- `POST /api/v1/appointments` - Create a new appointment
- `GET /api/v1/appointments` - Get all appointments
- `GET /api/v1/appointments/:id` - Get appointment by ID
- `PUT /api/v1/appointments/:id` - Update appointment
- `DELETE /api/v1/appointments/:id` - Delete appointment
- `PATCH /api/v1/appointments/:id/status` - Update appointment status
- `GET /api/v1/appointments/customer/:customerId` - Get appointments by customer
- `GET /api/v1/appointments/staff/:staffId` - Get appointments by staff
- `GET /api/v1/appointments/service/:serviceId` - Get appointments by service
- `GET /api/v1/appointments/date/:date` - Get appointments by date (YYYY-MM-DD)
- `GET /api/v1/appointments/date/:date/staff/:staffId` - Get appointments by date and staff
- `GET /api/v1/appointments/status/:status` - Get appointments by status
- `GET /api/v1/appointments/count` - Get total appointment count
- `GET /api/v1/appointments/count/status/:status` - Get appointment count by status
- `GET /api/v1/appointments/upcoming` - Get upcoming appointments
- `GET /api/v1/appointments/today` - Get today's appointments
- `GET /api/v1/appointments/range?start=YYYY-MM-DD&end=YYYY-MM-DD` - Get appointments by date range

## Development

### Available Make Commands

```bash
make help          # Show available commands
make deps          # Download dependencies
make build         # Build the application
make run           # Run the application
make test          # Run tests
make test-coverage # Run tests with coverage
make clean         # Clean build artifacts
make lint          # Run linter
make format        # Format code
make vet           # Run go vet
make dev           # Run with hot reload (requires air)
```

### Testing

```bash
# Run all tests
make test

# Run tests with coverage
make test-coverage

# Run specific package tests
go test -v ./internal/service/...
```

### Code Quality

```bash
# Format code
make format

# Run linter
make lint

# Run go vet
make vet
```

## Database Schema

The application automatically creates the following Cassandra tables:

- **customers**: Customer information
- **services**: Salon services and pricing
- **staff**: Staff member details
- **appointments**: Appointment bookings

## Error Handling

The API returns appropriate HTTP status codes:

- `200 OK` - Successful GET/PUT requests
- `201 Created` - Successful POST requests
- `204 No Content` - Successful DELETE requests
- `400 Bad Request` - Invalid request data
- `404 Not Found` - Resource not found
- `409 Conflict` - Resource conflict (e.g., duplicate email)
- `500 Internal Server Error` - Server errors

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Run tests and linting
6. Submit a pull request

## License

This project is licensed under the MIT License.
