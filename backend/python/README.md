# Beauty Salon Management System - Python Backend

A modern, high-performance REST API built with FastAPI for managing beauty salon operations.

## 🚀 Features

- **Complete CRUD Operations** for Customers, Services, Staff, and Appointments
- **Advanced Filtering** by date, status, category, role, and specialty
- **Business Logic Validation** with conflict detection and data integrity
- **Cassandra Database Integration** with optimized queries
- **Automatic API Documentation** with OpenAPI/Swagger
- **Health Check Endpoints** for monitoring and deployment
- **Async/Await Support** for high performance
- **Type Safety** with Pydantic models and validation
- **CORS Support** for frontend integration

## 🏗️ Architecture

### MVC Pattern
```
FastAPI Routes → Services (Business Logic) → Repositories (Data Layer) → Cassandra
```

### Project Structure
```
backend-python/
├── app/
│   ├── models/           # Pydantic data models
│   ├── repositories/     # Data access layer
│   ├── services/         # Business logic layer
│   ├── routers/          # API endpoints
│   └── database/         # Database connection
├── main.py              # Application entry point
├── requirements.txt     # Python dependencies
├── Dockerfile          # Container configuration
└── README.md           # This file
```

## 📋 API Endpoints

### Health Check
- `GET /api/health` - Application health status
- `GET /api/health/ready` - Readiness check
- `GET /api/health/live` - Liveness check

### Customers (8 endpoints)
- `POST /api/customers` - Create customer
- `GET /api/customers` - List all customers
- `GET /api/customers/{id}` - Get customer by ID
- `GET /api/customers/email/{email}` - Get customer by email
- `PUT /api/customers/{id}` - Update customer
- `DELETE /api/customers/{id}` - Delete customer
- `GET /api/customers/search/name?name={name}` - Search customers by name
- `GET /api/customers/count/total` - Get customer count

### Services (11 endpoints)
- `POST /api/services` - Create service
- `GET /api/services` - List all services
- `GET /api/services/active` - List active services
- `GET /api/services/{id}` - Get service by ID
- `PUT /api/services/{id}` - Update service
- `DELETE /api/services/{id}` - Delete service
- `GET /api/services/category/{category}` - Get services by category
- `GET /api/services/category/{category}/active` - Get active services by category
- `GET /api/services/search/name?name={name}` - Search services by name
- `GET /api/services/categories/list` - Get all categories
- `GET /api/services/count/total` - Get service count
- `GET /api/services/count/active` - Get active service count

### Staff (12 endpoints)
- `POST /api/staff` - Create staff member
- `GET /api/staff` - List all staff
- `GET /api/staff/active` - List active staff
- `GET /api/staff/{id}` - Get staff by ID
- `GET /api/staff/email/{email}` - Get staff by email
- `PUT /api/staff/{id}` - Update staff
- `DELETE /api/staff/{id}` - Delete staff
- `GET /api/staff/role/{role}` - Get staff by role
- `GET /api/staff/role/{role}/active` - Get active staff by role
- `GET /api/staff/specialty/{specialty}` - Get staff by specialty
- `GET /api/staff/search/name?name={name}` - Search staff by name
- `GET /api/staff/roles/list` - Get all roles
- `GET /api/staff/count/total` - Get staff count
- `GET /api/staff/count/active` - Get active staff count

### Appointments (15 endpoints)
- `POST /api/appointments` - Create appointment
- `GET /api/appointments` - List all appointments
- `GET /api/appointments/{id}` - Get appointment by ID
- `PUT /api/appointments/{id}` - Update appointment
- `PATCH /api/appointments/{id}/status` - Update appointment status
- `DELETE /api/appointments/{id}` - Delete appointment
- `GET /api/appointments/customer/{customer_id}` - Get appointments by customer
- `GET /api/appointments/staff/{staff_id}` - Get appointments by staff
- `GET /api/appointments/service/{service_id}` - Get appointments by service
- `GET /api/appointments/date/{date}` - Get appointments by date
- `GET /api/appointments/date/{date}/staff/{staff_id}` - Get appointments by date and staff
- `GET /api/appointments/status/{status}` - Get appointments by status
- `GET /api/appointments/upcoming/list` - Get upcoming appointments
- `GET /api/appointments/today/list` - Get today's appointments
- `GET /api/appointments/count/total` - Get appointment count
- `GET /api/appointments/count/status/{status}` - Get count by status

**Total: 46+ REST API endpoints**

## 🛠️ Installation & Setup

### Prerequisites
- Python 3.11+
- Apache Cassandra 4.0+
- pip (Python package manager)

### 1. Clone and Setup
```bash
cd beauty-salon-app/backend-python
cp .env.example .env
# Edit .env with your configuration
```

### 2. Install Dependencies
```bash
pip install -r requirements.txt
```

### 3. Configure Environment
Edit `.env` file with your settings:
```env
HOST=0.0.0.0
PORT=8000
CASSANDRA_HOSTS=localhost
CASSANDRA_KEYSPACE=beauty_salon
```

### 4. Run the Application
```bash
# Development mode
python main.py

# Or with uvicorn directly
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```

## 🐳 Docker Deployment

### Build and Run
```bash
# Build image
docker build -t beauty-salon-python .

# Run container
docker run -p 8000:8000 --env-file .env beauty-salon-python
```

### Docker Compose Integration
```yaml
services:
  backend-python:
    build: ./backend-python
    ports:
      - "8000:8000"
    environment:
      - CASSANDRA_HOSTS=cassandra
    depends_on:
      - cassandra
```

## 📊 Data Models

### Customer
```python
{
  "id": "uuid",
  "name": "string",
  "email": "email",
  "phone": "string (10-11 digits)",
  "address": "string",
  "created_at": "datetime",
  "updated_at": "datetime"
}
```

### Service
```python
{
  "id": "uuid",
  "name": "string",
  "description": "string",
  "duration": "integer (minutes)",
  "price": "decimal",
  "category": "string",
  "is_active": "boolean",
  "created_at": "datetime",
  "updated_at": "datetime"
}
```

### Staff
```python
{
  "id": "uuid",
  "name": "string",
  "email": "email",
  "phone": "string (10-11 digits)",
  "role": "string",
  "specialties": ["string"],
  "is_active": "boolean",
  "hire_date": "datetime",
  "created_at": "datetime",
  "updated_at": "datetime"
}
```

### Appointment
```python
{
  "id": "uuid",
  "customer_id": "uuid",
  "staff_id": "uuid",
  "service_id": "uuid",
  "appointment_date": "date",
  "appointment_time": "time",
  "status": "scheduled|confirmed|in_progress|completed|cancelled|no_show",
  "notes": "string",
  "price": "decimal",
  "created_at": "datetime",
  "updated_at": "datetime"
}
```

## 🔧 Configuration

### Environment Variables
| Variable | Description | Default |
|----------|-------------|---------|
| `HOST` | Server host | `0.0.0.0` |
| `PORT` | Server port | `8000` |
| `CASSANDRA_HOSTS` | Cassandra hosts | `localhost` |
| `CASSANDRA_PORT` | Cassandra port | `9042` |
| `CASSANDRA_KEYSPACE` | Database keyspace | `beauty_salon` |
| `CASSANDRA_DATACENTER` | Datacenter name | `datacenter1` |
| `LOG_LEVEL` | Logging level | `INFO` |

## 📖 API Documentation

### Interactive Documentation
- **Swagger UI**: http://localhost:8000/api/docs
- **ReDoc**: http://localhost:8000/api/redoc

### Example Requests

#### Create Customer
```bash
curl -X POST "http://localhost:8000/api/customers" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@email.com",
    "phone": "11987654321",
    "address": "Rua das Flores, 123"
  }'
```

#### Create Appointment
```bash
curl -X POST "http://localhost:8000/api/appointments" \
  -H "Content-Type: application/json" \
  -d '{
    "customer_id": "uuid-here",
    "staff_id": "uuid-here",
    "service_id": "uuid-here",
    "appointment_date": "2024-01-15",
    "appointment_time": "14:30:00",
    "status": "scheduled"
  }'
```

## 🧪 Testing

### Manual Testing
```bash
# Health check
curl http://localhost:8000/api/health

# Get all customers
curl http://localhost:8000/api/customers

# Search services
curl "http://localhost:8000/api/services/search/name?name=corte"
```

### Automated Testing
```bash
# Install test dependencies
pip install pytest pytest-asyncio httpx

# Run tests (when implemented)
pytest tests/
```

## 🚀 Performance Features

- **Async/Await**: Non-blocking I/O operations
- **Connection Pooling**: Efficient database connections
- **Pydantic Validation**: Fast data validation and serialization
- **FastAPI**: High-performance web framework
- **Type Hints**: Better IDE support and runtime optimization

## 🔒 Security Features

- **Input Validation**: Comprehensive data validation with Pydantic
- **SQL Injection Prevention**: Parameterized queries
- **CORS Configuration**: Configurable cross-origin requests
- **Error Handling**: Secure error responses without sensitive data

## 🐛 Troubleshooting

### Common Issues

1. **Connection Refused**
   ```
   Error: Connection refused to Cassandra
   Solution: Ensure Cassandra is running and accessible
   ```

2. **Import Errors**
   ```
   Error: ModuleNotFoundError
   Solution: Install dependencies with pip install -r requirements.txt
   ```

3. **Port Already in Use**
   ```
   Error: Port 8000 is already in use
   Solution: Change PORT in .env or stop conflicting process
   ```

## 📈 Monitoring

### Health Endpoints
- `/api/health` - Overall health with system metrics
- `/api/health/ready` - Database connectivity check
- `/api/health/live` - Application liveness

### Metrics Available
- CPU usage percentage
- Memory usage statistics
- Disk usage information
- Database connection status

## 🤝 Contributing

1. Follow PEP 8 style guidelines
2. Add type hints to all functions
3. Write docstrings for public methods
4. Update tests for new features
5. Update this README for API changes

## 📄 License

This project is part of the Beauty Salon Management System.

---

**Backend Python** - High-performance FastAPI implementation with complete feature parity to Java and Node.js backends.
