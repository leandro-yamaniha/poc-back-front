"""
Basic unit tests for Pydantic models - simplified version.
"""
import pytest
from datetime import datetime
from uuid import uuid4
from pydantic import ValidationError

from app.models.customer import CustomerCreate, CustomerUpdate
from app.models.staff import StaffCreate, StaffUpdate  
from app.models.service import ServiceCreate, ServiceUpdate
from app.models.appointment import AppointmentCreate, AppointmentUpdate


class TestBasicModels:
    """Basic model validation tests."""
    
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
    
    def test_customer_invalid_email(self):
        """Test customer with invalid email."""
        with pytest.raises(ValidationError):
            CustomerCreate(
                name="John Doe",
                email="invalid-email",
                phone="11999998888"
            )
    
    def test_customer_invalid_phone(self):
        """Test customer with invalid phone."""
        with pytest.raises(ValidationError):
            CustomerCreate(
                name="John Doe", 
                email="john@example.com",
                phone="invalid"
            )
    
    def test_staff_create_valid(self):
        """Test creating valid staff."""
        staff_data = {
            "name": "Dr. Smith",
            "email": "dr.smith@salon.com", 
            "phone": "11999998888",
            "role": "STYLIST",
            "specialties": ["Hair Cut"],
            "is_active": True
        }
        staff = StaffCreate(**staff_data)
        assert staff.name == "Dr. Smith"
        assert staff.role == "STYLIST"
    
    def test_staff_invalid_role(self):
        """Test staff with invalid role."""
        with pytest.raises(ValidationError):
            StaffCreate(
                name="Dr. Smith",
                email="dr.smith@salon.com",
                phone="11999998888", 
                role="INVALID_ROLE",
                specialties=["Hair Cut"],
                is_active=True
            )
    
    def test_service_create_valid(self):
        """Test creating valid service."""
        service_data = {
            "name": "Hair Cut",
            "description": "Professional hair cutting",
            "price": 50.00,
            "duration": 60,
            "category": "Hair",
            "is_active": True
        }
        service = ServiceCreate(**service_data)
        assert service.name == "Hair Cut"
        assert service.price == 50.00
    
    def test_service_negative_price(self):
        """Test service with negative price."""
        with pytest.raises(ValidationError):
            ServiceCreate(
                name="Hair Cut",
                description="Professional hair cutting",
                price=-10.00,
                duration=60,
                category="Hair",
                is_active=True
            )
    
    def test_appointment_create_valid(self):
        """Test creating valid appointment."""
        appointment_data = {
            "customer_id": uuid4(),
            "service_id": uuid4(),
            "staff_id": uuid4(),
            "appointment_time": datetime(2024, 12, 25, 14, 30),
            "status": "SCHEDULED",
            "notes": "First appointment"
        }
        appointment = AppointmentCreate(**appointment_data)
        assert appointment.status == "SCHEDULED"
        assert appointment.notes == "First appointment"
    
    def test_appointment_invalid_status(self):
        """Test appointment with invalid status."""
        with pytest.raises(ValidationError):
            AppointmentCreate(
                customer_id=uuid4(),
                service_id=uuid4(), 
                staff_id=uuid4(),
                appointment_time=datetime(2024, 12, 25, 14, 30),
                status="INVALID_STATUS",
                notes="Test"
            )
