"""
Unit tests for Pydantic models in the Beauty Salon application.
"""
import pytest
from datetime import datetime
from uuid import uuid4
from pydantic import ValidationError

from app.models.customer import Customer, CustomerCreate, CustomerUpdate, CustomerResponse
from app.models.staff import Staff, StaffCreate, StaffUpdate, StaffResponse
from app.models.service import Service, ServiceCreate, ServiceUpdate, ServiceResponse
from app.models.appointment import Appointment, AppointmentCreate, AppointmentUpdate, AppointmentResponse


class TestCustomerModel:
    """Test cases for Customer model and related schemas."""
    
    def test_customer_create_valid(self):
        """Test creating a valid customer."""
        customer_data = {
            "name": "John Doe",
            "email": "john.doe@example.com",
            "phone": "11999998888",
            "address": "123 Main St"
        }
        customer = CustomerCreate(**customer_data)
        assert customer.name == "John Doe"
        assert customer.email == "john.doe@example.com"
        assert customer.phone == "11999998888"
        assert customer.address == "123 Main St"
    
    def test_customer_create_invalid_email(self):
        """Test customer creation with invalid email."""
        customer_data = {
            "name": "John Doe",
            "email": "invalid-email",
            "phone": "11999998888",
            "address": "123 Main St"
        }
        with pytest.raises(ValidationError) as exc_info:
            CustomerCreate(**customer_data)
        assert "value is not a valid email address" in str(exc_info.value)
    
    def test_customer_create_invalid_phone(self):
        """Test customer creation with invalid phone."""
        customer_data = {
            "name": "John Doe",
            "email": "john.doe@example.com",
            "phone": "invalid-phone",
            "address": "123 Main St"
        }
        with pytest.raises(ValidationError) as exc_info:
            CustomerCreate(**customer_data)
        assert "String should match pattern" in str(exc_info.value)
    
    def test_customer_full_model(self):
        """Test full Customer model with all fields."""
        customer_id = uuid4()
        now = datetime.now()
        
        customer = Customer(
            id=customer_id,
            name="Jane Smith",
            email="jane.smith@example.com",
            phone="11888887777",
            address="456 Oak Ave",
            created_at=now,
            updated_at=now
        )
        
        assert customer.id == customer_id
        assert customer.name == "Jane Smith"
        assert customer.email == "jane.smith@example.com"
        assert customer.phone == "11888887777"
        assert customer.address == "456 Oak Ave"
        assert customer.created_at == now
        assert customer.updated_at == now


class TestStaffModel:
    """Test cases for Staff model and related schemas."""
    
    def test_staff_create_valid(self):
        """Test creating a valid staff member."""
        staff_data = {
            "name": "Dr. Smith",
            "email": "dr.smith@salon.com",
            "phone": "11999998888",
            "role": "STYLIST",
            "specialties": ["Hair Cut", "Hair Color"],
            "is_active": True
        }
        staff = StaffCreate(**staff_data)
        assert staff.name == "Dr. Smith"
        assert staff.email == "dr.smith@salon.com"
        assert staff.phone == "11999998888"
        assert staff.role == "STYLIST"
        assert staff.specialties == ["Hair Cut", "Hair Color"]
        assert staff.is_active is True
    
    def test_staff_create_invalid_role(self):
        """Test staff creation with invalid role."""
        staff_data = {
            "name": "Dr. Smith",
            "email": "dr.smith@salon.com",
            "phone": "11999998888",
            "role": "INVALID_ROLE",
            "specialties": ["Hair Cut"],
            "is_active": True
        }
        with pytest.raises(ValidationError) as exc_info:
            StaffCreate(**staff_data)
        assert "Input should be" in str(exc_info.value)
    
    def test_staff_full_model(self):
        """Test full Staff model with all fields."""
        staff_id = uuid4()
        now = datetime.now()
        
        staff = Staff(
            id=staff_id,
            name="Alice Johnson",
            email="alice@salon.com",
            phone="11777776666",
            role="MANAGER",
            specialties=["Management", "Customer Service"],
            is_active=True,
            created_at=now,
            updated_at=now
        )
        
        assert staff.id == staff_id
        assert staff.name == "Alice Johnson"
        assert staff.role == "MANAGER"
        assert staff.specialties == ["Management", "Customer Service"]
        assert staff.is_active is True


class TestServiceModel:
    """Test cases for Service model and related schemas."""
    
    def test_service_create_valid(self):
        """Test creating a valid service."""
        service_data = {
            "name": "Hair Cut",
            "description": "Professional hair cutting service",
            "price": 50.00,
            "duration": 60,
            "category": "Hair",
            "is_active": True
        }
        service = ServiceCreate(**service_data)
        assert service.name == "Hair Cut"
        assert service.description == "Professional hair cutting service"
        assert service.price == 50.00
        assert service.duration == 60
        assert service.category == "Hair"
        assert service.is_active is True
    
    def test_service_create_negative_price(self):
        """Test service creation with negative price."""
        service_data = {
            "name": "Hair Cut",
            "description": "Professional hair cutting service",
            "price": -10.00,
            "duration": 60,
            "category": "Hair",
            "is_active": True
        }
        with pytest.raises(ValidationError) as exc_info:
            ServiceCreate(**service_data)
        assert "Input should be greater than 0" in str(exc_info.value)
    
    def test_service_create_zero_duration(self):
        """Test service creation with zero duration."""
        service_data = {
            "name": "Hair Cut",
            "description": "Professional hair cutting service",
            "price": 50.00,
            "duration": 0,
            "category": "Hair",
            "is_active": True
        }
        with pytest.raises(ValidationError) as exc_info:
            ServiceCreate(**service_data)
        assert "Input should be greater than 0" in str(exc_info.value)


class TestAppointmentModel:
    """Test cases for Appointment model and related schemas."""
    
    def test_appointment_create_valid(self):
        """Test creating a valid appointment."""
        customer_id = uuid4()
        service_id = uuid4()
        staff_id = uuid4()
        appointment_time = datetime(2024, 12, 25, 14, 30)
        
        appointment_data = {
            "customer_id": customer_id,
            "service_id": service_id,
            "staff_id": staff_id,
            "appointment_time": appointment_time,
            "status": "SCHEDULED",
            "notes": "First time customer"
        }
        appointment = AppointmentCreate(**appointment_data)
        assert appointment.customer_id == customer_id
        assert appointment.service_id == service_id
        assert appointment.staff_id == staff_id
        assert appointment.appointment_time == appointment_time
        assert appointment.status == "SCHEDULED"
        assert appointment.notes == "First time customer"
    
    def test_appointment_create_invalid_status(self):
        """Test appointment creation with invalid status."""
        appointment_data = {
            "customer_id": uuid4(),
            "service_id": uuid4(),
            "staff_id": uuid4(),
            "appointment_time": datetime(2024, 12, 25, 14, 30),
            "status": "INVALID_STATUS",
            "notes": "Test appointment"
        }
        with pytest.raises(ValidationError) as exc_info:
            AppointmentCreate(**appointment_data)
        assert "Input should be" in str(exc_info.value)
    
    def test_appointment_full_model(self):
        """Test full Appointment model with all fields."""
        appointment_id = uuid4()
        customer_id = uuid4()
        service_id = uuid4()
        staff_id = uuid4()
        appointment_time = datetime(2024, 12, 25, 14, 30)
        now = datetime.now()
        
        appointment = Appointment(
            id=appointment_id,
            customer_id=customer_id,
            service_id=service_id,
            staff_id=staff_id,
            appointment_time=appointment_time,
            status="CONFIRMED",
            notes="Regular customer",
            created_at=now,
            updated_at=now
        )
        
        assert appointment.id == appointment_id
        assert appointment.customer_id == customer_id
        assert appointment.service_id == service_id
        assert appointment.staff_id == staff_id
        assert appointment.appointment_time == appointment_time
        assert appointment.status == "CONFIRMED"
        assert appointment.notes == "Regular customer"
        assert appointment.created_at == now
        assert appointment.updated_at == now
