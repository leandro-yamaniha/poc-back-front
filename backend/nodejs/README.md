# Beauty Salon Backend - Node.js

A comprehensive Node.js backend for the Beauty Salon Management System, providing REST APIs for managing customers, services, staff, and appointments.

## 🚀 Features

- **Customer Management**: CRUD operations for customer data
- **Service Management**: Manage salon services with categories and pricing
- **Staff Management**: Handle staff information, roles, and specialties
- **Appointment Scheduling**: Complete appointment booking system with conflict detection
- **Performance Monitoring**: Health checks, metrics, and cache management
- **Data Validation**: Comprehensive input validation with express-validator
- **Caching**: In-memory caching with node-cache for improved performance
- **Security**: Rate limiting, CORS, and security headers with helmet
- **Database**: Apache Cassandra integration with cassandra-driver

## 🛠 Technology Stack

- **Runtime**: Node.js 18+
- **Framework**: Express.js
- **Database**: Apache Cassandra
- **Caching**: node-cache
- **Validation**: express-validator
- **Security**: helmet, cors, express-rate-limit
- **Testing**: Jest, supertest
- **Development**: nodemon

## 📋 Prerequisites

- Node.js 18.0.0 or higher
- Apache Cassandra 4.0+
- npm or yarn package manager

## 🔧 Installation

1. **Clone the repository and navigate to the Node.js backend:**
   ```bash
   cd beauty-salon-app/backend-nodejs
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Configure environment variables:**
   ```bash
   cp .env.example .env
   # Edit .env with your configuration
   ```

4. **Start Cassandra database:**
   ```bash
   # Using Docker
   docker run --name cassandra -p 9042:9042 -d cassandra:4.1
   
   # Or start your local Cassandra instance
   cassandra -f
   ```

5. **Create the keyspace and tables:**
   ```bash
   # Connect to Cassandra and run the schema from ../database/init.cql
   cqlsh -f ../database/init.cql
   ```

## 🚀 Running the Application

### Development Mode
```bash
npm run dev
```

### Production Mode
```bash
npm start
```

The server will start on `http://localhost:3000` (or the port specified in your .env file).

## 📊 API Endpoints

### Health Check
- `GET /health` - Application health status

### Customers
- `GET /api/customers` - Get all customers
- `GET /api/customers/:id` - Get customer by ID
- `GET /api/customers/email/:email` - Get customer by email
- `GET /api/customers/search?name=...` - Search customers by name
- `POST /api/customers` - Create new customer
- `PUT /api/customers/:id` - Update customer
- `DELETE /api/customers/:id` - Delete customer
- `GET /api/customers/count` - Get customer count

### Services
- `GET /api/services` - Get all services
- `GET /api/services/active` - Get active services only
- `GET /api/services/:id` - Get service by ID
- `GET /api/services/category/:category` - Get services by category
- `GET /api/services/search?name=...` - Search services by name
- `POST /api/services` - Create new service
- `PUT /api/services/:id` - Update service
- `DELETE /api/services/:id` - Delete service
- `GET /api/services/categories` - Get all categories
- `GET /api/services/count` - Get service count

### Staff
- `GET /api/staff` - Get all staff
- `GET /api/staff/active` - Get active staff only
- `GET /api/staff/:id` - Get staff by ID
- `GET /api/staff/email/:email` - Get staff by email
- `GET /api/staff/role/:role` - Get staff by role
- `GET /api/staff/specialty/:specialty` - Get staff by specialty
- `GET /api/staff/search?name=...` - Search staff by name
- `POST /api/staff` - Create new staff
- `PUT /api/staff/:id` - Update staff
- `DELETE /api/staff/:id` - Delete staff
- `GET /api/staff/roles` - Get all roles
- `GET /api/staff/count` - Get staff count

### Appointments
- `GET /api/appointments` - Get all appointments
- `GET /api/appointments/:id` - Get appointment by ID
- `GET /api/appointments/customer/:customerId` - Get appointments by customer
- `GET /api/appointments/staff/:staffId` - Get appointments by staff
- `GET /api/appointments/service/:serviceId` - Get appointments by service
- `GET /api/appointments/status/:status` - Get appointments by status
- `GET /api/appointments/today` - Get today's appointments
- `GET /api/appointments/upcoming` - Get upcoming appointments
- `GET /api/appointments/date-range?startDate=...&endDate=...` - Get appointments by date range
- `POST /api/appointments` - Create new appointment
- `PUT /api/appointments/:id` - Update appointment
- `DELETE /api/appointments/:id` - Delete appointment
- `GET /api/appointments/count` - Get appointment count
- `GET /api/appointments/status/:status/count` - Get count by status

### Monitoring
- `GET /api/monitoring/health` - Detailed health check
- `GET /api/monitoring/performance` - Performance metrics
- `GET /api/monitoring/cache` - Cache metrics
- `POST /api/monitoring/cache/clear` - Clear all caches
- `GET /api/monitoring/dashboard` - Dashboard statistics

## 📜 OpenAPI Documentation

The API is documented using the OpenAPI 3.0 standard. You can access the interactive Swagger UI to explore the endpoints, view models, and test the API directly in your browser.

- **Swagger UI URL**: [http://localhost:3000/api-docs](http://localhost:3000/api-docs)

Make sure the application is running before accessing the URL.

## 🧪 Testing

```bash
# Run all tests
npm test

# Run tests in watch mode
npm run test:watch

# Run tests with coverage
npm run test:coverage
```

## 📝 Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `PORT` | Server port | 3000 |
| `NODE_ENV` | Environment | development |
| `CASSANDRA_CONTACT_POINTS` | Cassandra hosts | localhost |
| `CASSANDRA_PORT` | Cassandra port | 9042 |
| `CASSANDRA_KEYSPACE` | Keyspace name | beauty_salon |
| `CASSANDRA_LOCAL_DATACENTER` | Local datacenter | datacenter1 |
| `CACHE_TTL` | Cache TTL in ms | 600000 |
| `CACHE_MAX_KEYS` | Max cache keys | 1000 |
| `RATE_LIMIT_WINDOW_MS` | Rate limit window | 900000 |
| `RATE_LIMIT_MAX_REQUESTS` | Max requests per window | 100 |
| `CORS_ORIGIN` | CORS origin | * |

## 🏗 Architecture

```
src/
├── app.js                 # Application entry point
├── config/
│   └── cassandra.js       # Cassandra configuration
├── models/
│   ├── Customer.js        # Customer model with validation
│   ├── Service.js         # Service model with validation
│   ├── Staff.js           # Staff model with validation
│   └── Appointment.js     # Appointment model with validation
├── repositories/
│   ├── CustomerRepository.js    # Customer data access
│   ├── ServiceRepository.js     # Service data access
│   ├── StaffRepository.js       # Staff data access
│   └── AppointmentRepository.js # Appointment data access
├── services/
│   ├── CustomerService.js       # Customer business logic
│   ├── ServiceService.js        # Service business logic
│   ├── StaffService.js          # Staff business logic
│   └── AppointmentService.js    # Appointment business logic
├── controllers/
│   ├── CustomerController.js    # Customer HTTP handlers
│   ├── ServiceController.js     # Service HTTP handlers
│   ├── StaffController.js       # Staff HTTP handlers
│   ├── AppointmentController.js # Appointment HTTP handlers
│   └── MonitoringController.js  # Monitoring HTTP handlers
├── routes/
│   ├── customerRoutes.js        # Customer routes
│   ├── serviceRoutes.js         # Service routes
│   ├── staffRoutes.js           # Staff routes
│   ├── appointmentRoutes.js     # Appointment routes
│   └── monitoringRoutes.js      # Monitoring routes
└── middleware/
    └── errorHandler.js          # Global error handling
```

## 🔒 Security Features

- **Rate Limiting**: Prevents abuse with configurable limits
- **CORS**: Cross-origin resource sharing configuration
- **Helmet**: Security headers for protection
- **Input Validation**: Comprehensive validation with express-validator
- **Error Handling**: Secure error responses without information leakage

## 📊 Performance Features

- **Caching**: In-memory caching with configurable TTL
- **Connection Pooling**: Optimized Cassandra connections
- **Compression**: Response compression with gzip
- **Monitoring**: Performance metrics and health checks

## 🐛 Troubleshooting

### Common Issues

1. **Cassandra Connection Error**
   - Ensure Cassandra is running on the configured port
   - Check keyspace exists and is accessible
   - Verify network connectivity

2. **Port Already in Use**
   - Change the PORT in .env file
   - Kill existing processes using the port

3. **Validation Errors**
   - Check request body format matches model requirements
   - Ensure all required fields are provided

## 📄 License

This project is licensed under the MIT License.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📞 Support

For support and questions, please create an issue in the repository.
