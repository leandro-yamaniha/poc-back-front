"""
Unit tests for FastAPI routers in the Beauty Salon application.
"""
import pytest
from unittest.mock import Mock, patch
from fastapi.testclient import TestClient
from uuid import uuid4
from datetime import datetime

from main import app
from app.models.customer import Customer, CustomerCreate, CustomerUpdate
from app.models.staff import Staff, StaffCreate, StaffUpdate
from app.models.service import Service, ServiceCreate, ServiceUpdate
from app.models.appointment import Appointment, AppointmentCreate, AppointmentUpdate


@pytest.fixture
def client():
    """Create a test client for the FastAPI app."""
    return TestClient(app)


class TestCustomerRoutes:
    """Test cases for customer routes."""
    
    @patch('app.routers.customers.customer_service')
    def test_create_customer(self, mock_customer_service, client):
        """Test creating a customer via API."""
        customer_data = {
            "name": "John Doe",
            "email": "john@example.com",
            "phone": "+1234567890",
            "address": "123 Main St"
        }
        
        created_customer = Customer(
            id=uuid4(),
            name="John Doe",
            email="john@example.com",
            phone="+1234567890",
            address="123 Main St",
            created_at=datetime.now(),
            updated_at=datetime.now()
        )
        
        mock_customer_service.create_customer.return_value = created_customer
        
        response = client.post("/api/customers/", json=customer_data)
        
        assert response.status_code == 201
        response_data = response.json()
        assert response_data["name"] == "John Doe"
        assert response_data["email"] == "john@example.com"
        mock_customer_service.create_customer.assert_called_once()
    
    @patch('app.routers.customers.customer_service')
    def test_get_customer(self, mock_customer_service, client):
        """Test getting a customer by ID via API."""
        customer_id = uuid4()
        customer = Customer(
            id=customer_id,
            name="John Doe",
            email="john@example.com",
            phone="+1234567890",
            address="123 Main St",
            created_at=datetime.now(),
            updated_at=datetime.now()
        )
        
        mock_customer_service.get_customer.return_value = customer
        
        response = client.get(f"/api/customers/{customer_id}")
        
        assert response.status_code == 200
        response_data = response.json()
        assert response_data["name"] == "John Doe"
        assert response_data["email"] == "john@example.com"
        mock_customer_service.get_customer.assert_called_once_with(customer_id)
    
    @patch('app.routers.customers.customer_service')
    def test_get_customer_not_found(self, mock_customer_service, client):
        """Test getting a customer that doesn't exist."""
        customer_id = uuid4()
        mock_customer_service.get_customer.return_value = None
        
        response = client.get(f"/api/customers/{customer_id}")
        
        assert response.status_code == 404
        mock_customer_service.get_customer.assert_called_once_with(customer_id)
    
    @patch('app.routers.customers.customer_service')
    def test_get_all_customers(self, mock_customer_service, client):
        """Test getting all customers via API."""
        customers = [
            Customer(
                id=uuid4(),
                name="John Doe",
                email="john@example.com",
                phone="+1234567890",
                address="123 Main St",
                created_at=datetime.now(),
                updated_at=datetime.now()
            ),
            Customer(
                id=uuid4(),
                name="Jane Smith",
                email="jane@example.com",
                phone="+1987654321",
                address="456 Oak Ave",
                created_at=datetime.now(),
                updated_at=datetime.now()
            )
        ]
        
        mock_customer_service.get_all_customers.return_value = customers
        
        response = client.get("/api/customers/")
        
        assert response.status_code == 200
        response_data = response.json()
        assert len(response_data) == 2
        assert response_data[0]["name"] == "John Doe"
        assert response_data[1]["name"] == "Jane Smith"
        mock_customer_service.get_all_customers.assert_called_once()
    
    @patch('app.routers.customers.customer_service')
    def test_update_customer(self, mock_customer_service, client):
        """Test updating a customer via API."""
        customer_id = uuid4()
        update_data = {
            "name": "John Doe Updated",
            "email": "john.updated@example.com"
        }
        
        updated_customer = Customer(
            id=customer_id,
            name="John Doe Updated",
            email="john.updated@example.com",
            phone="+1234567890",
            address="123 Main St",
            created_at=datetime.now(),
            updated_at=datetime.now()
        )
        
        mock_customer_service.update_customer.return_value = updated_customer
        
        response = client.put(f"/api/customers/{customer_id}", json=update_data)
        
        assert response.status_code == 200
        response_data = response.json()
        assert response_data["name"] == "John Doe Updated"
        assert response_data["email"] == "john.updated@example.com"
        mock_customer_service.update_customer.assert_called_once()
    
    @patch('app.routers.customers.customer_service')
    def test_delete_customer(self, mock_customer_service, client):
        """Test deleting a customer via API."""
        customer_id = uuid4()
        mock_customer_service.delete_customer.return_value = True
        
        response = client.delete(f"/api/customers/{customer_id}")
        
        assert response.status_code == 204
        mock_customer_service.delete_customer.assert_called_once_with(customer_id)


class TestStaffRoutes:
    """Test cases for staff routes."""
    
    @patch('app.routers.staff.staff_service')
    def test_create_staff(self, mock_staff_service, client):
        """Test creating a staff member via API."""
        staff_data = {
            "name": "Dr. Smith",
            "email": "dr.smith@salon.com",
            "phone": "+1234567890",
            "role": "STYLIST",
            "specialties": ["Hair Cut", "Hair Color"],
            "is_active": True
        }
        
        created_staff = Staff(
            id=uuid4(),
            name="Dr. Smith",
            email="dr.smith@salon.com",
            phone="+1234567890",
            role="STYLIST",
            specialties=["Hair Cut", "Hair Color"],
            is_active=True,
            created_at=datetime.now(),
            updated_at=datetime.now()
        )
        
        mock_staff_service.create_staff.return_value = created_staff
        
        response = client.post("/api/staff/", json=staff_data)
        
        assert response.status_code == 201
        response_data = response.json()
        assert response_data["name"] == "Dr. Smith"
        assert response_data["role"] == "STYLIST"
        mock_staff_service.create_staff.assert_called_once()
    
    @patch('app.routers.staff.staff_service')
    def test_get_staff(self, mock_staff_service, client):
        """Test getting a staff member by ID via API."""
        staff_id = uuid4()
        staff = Staff(
            id=staff_id,
            name="Dr. Smith",
            email="dr.smith@salon.com",
            phone="+1234567890",
            role="STYLIST",
            specialties=["Hair Cut", "Hair Color"],
            is_active=True,
            created_at=datetime.now(),
            updated_at=datetime.now()
        )
        
        mock_staff_service.get_staff.return_value = staff
        
        response = client.get(f"/api/staff/{staff_id}")
        
        assert response.status_code == 200
        response_data = response.json()
        assert response_data["name"] == "Dr. Smith"
        assert response_data["role"] == "STYLIST"
        mock_staff_service.get_staff.assert_called_once_with(staff_id)


class TestServiceRoutes:
    """Test cases for service routes."""
    
    @patch('app.routers.services.service_service')
    def test_create_service(self, mock_service_service, client):
        """Test creating a service via API."""
        service_data = {
            "name": "Hair Cut",
            "description": "Professional hair cutting",
            "price": 50.00,
            "duration": 60,
            "category": "Hair",
            "is_active": True
        }
        
        created_service = Service(
            id=uuid4(),
            name="Hair Cut",
            description="Professional hair cutting",
            price=50.00,
            duration=60,
            category="Hair",
            is_active=True,
            created_at=datetime.now(),
            updated_at=datetime.now()
        )
        
        mock_service_service.create_service.return_value = created_service
        
        response = client.post("/api/services/", json=service_data)
        
        assert response.status_code == 201
        response_data = response.json()
        assert response_data["name"] == "Hair Cut"
        assert response_data["price"] == 50.00
        mock_service_service.create_service.assert_called_once()
    
    @patch('app.routers.services.service_service')
    def test_get_service(self, mock_service_service, client):
        """Test getting a service by ID via API."""
        service_id = uuid4()
        service = Service(
            id=service_id,
            name="Hair Cut",
            description="Professional hair cutting",
            price=50.00,
            duration=60,
            category="Hair",
            is_active=True,
            created_at=datetime.now(),
            updated_at=datetime.now()
        )
        
        mock_service_service.get_service.return_value = service
        
        response = client.get(f"/api/services/{service_id}")
        
        assert response.status_code == 200
        response_data = response.json()
        assert response_data["name"] == "Hair Cut"
        assert response_data["price"] == 50.00
        mock_service_service.get_service.assert_called_once_with(service_id)


class TestAppointmentRoutes:
    """Test cases for appointment routes."""
    
    @patch('app.routers.appointments.appointment_service')
    def test_create_appointment(self, mock_appointment_service, client):
        """Test creating an appointment via API."""
        customer_id = uuid4()
        service_id = uuid4()
        staff_id = uuid4()
        
        appointment_data = {
            "customer_id": str(customer_id),
            "service_id": str(service_id),
            "staff_id": str(staff_id),
            "appointment_time": "2024-12-25T14:30:00",
            "status": "SCHEDULED",
            "notes": "First time customer"
        }
        
        created_appointment = Appointment(
            id=uuid4(),
            customer_id=customer_id,
            service_id=service_id,
            staff_id=staff_id,
            appointment_time=datetime(2024, 12, 25, 14, 30),
            status="SCHEDULED",
            notes="First time customer",
            created_at=datetime.now(),
            updated_at=datetime.now()
        )
        
        mock_appointment_service.create_appointment.return_value = created_appointment
        
        response = client.post("/api/appointments/", json=appointment_data)
        
        assert response.status_code == 201
        response_data = response.json()
        assert response_data["status"] == "SCHEDULED"
        assert response_data["notes"] == "First time customer"
        mock_appointment_service.create_appointment.assert_called_once()
    
    @patch('app.routers.appointments.appointment_service')
    def test_get_appointment(self, mock_appointment_service, client):
        """Test getting an appointment by ID via API."""
        appointment_id = uuid4()
        appointment = Appointment(
            id=appointment_id,
            customer_id=uuid4(),
            service_id=uuid4(),
            staff_id=uuid4(),
            appointment_time=datetime(2024, 12, 25, 14, 30),
            status="SCHEDULED",
            notes="First time customer",
            created_at=datetime.now(),
            updated_at=datetime.now()
        )
        
        mock_appointment_service.get_appointment.return_value = appointment
        
        response = client.get(f"/api/appointments/{appointment_id}")
        
        assert response.status_code == 200
        response_data = response.json()
        assert response_data["status"] == "SCHEDULED"
        assert response_data["notes"] == "First time customer"
        mock_appointment_service.get_appointment.assert_called_once_with(appointment_id)


class TestHealthRoutes:
    """Test cases for health check routes."""
    
    def test_health_check(self, client):
        """Test health check endpoint."""
        response = client.get("/health")
        
        assert response.status_code == 200
        response_data = response.json()
        assert response_data["status"] == "healthy"
        assert "timestamp" in response_data
    
    def test_ready_check(self, client):
        """Test readiness check endpoint."""
        response = client.get("/ready")
        
        assert response.status_code == 200
        response_data = response.json()
        assert response_data["status"] == "ready"
        assert "timestamp" in response_data
    
    def test_live_check(self, client):
        """Test liveness check endpoint."""
        response = client.get("/live")
        
        assert response.status_code == 200
        response_data = response.json()
        assert response_data["status"] == "alive"
        assert "timestamp" in response_data
