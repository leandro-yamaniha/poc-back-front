"""
Unit tests for service classes in the Beauty Salon application.
"""
import pytest
from unittest.mock import Mock, AsyncMock, patch
from uuid import uuid4
from datetime import datetime

from app.services.customer_service import CustomerService
from app.services.staff_service import StaffService
from app.services.service_service import ServiceService
from app.services.appointment_service import AppointmentService
from app.models.customer import Customer, CustomerCreate, CustomerUpdate
from app.models.staff import Staff, StaffCreate, StaffUpdate
from app.models.service import Service, ServiceCreate, ServiceUpdate
from app.models.appointment import Appointment, AppointmentCreate, AppointmentUpdate


class TestCustomerService:
    """Test cases for CustomerService."""
    
    @pytest.fixture
    def mock_customer_repo(self):
        """Create a mock CustomerRepository."""
        return Mock()
    
    @pytest.fixture
    def customer_service(self, mock_customer_repo):
        """Create CustomerService with mock repository."""
        return CustomerService(mock_customer_repo)
    
    def test_create_customer(self, customer_service, mock_customer_repo):
        """Test creating a customer."""
        customer_create = CustomerCreate(
            name="John Doe",
            email="john@example.com",
            phone="+1234567890",
            address="123 Main St"
        )
        
        created_customer = Customer(
            id=uuid4(),
            name="John Doe",
            email="john@example.com",
            phone="+1234567890",
            address="123 Main St",
            created_at=datetime.now(),
            updated_at=datetime.now()
        )
        
        mock_customer_repo.create.return_value = created_customer
        
        result = customer_service.create_customer(customer_create)
        
        assert result == created_customer
        mock_customer_repo.create.assert_called_once()
    
    def test_get_customer_by_id(self, customer_service, mock_customer_repo):
        """Test getting customer by ID."""
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
        
        mock_customer_repo.get_by_id.return_value = customer
        
        result = customer_service.get_customer(customer_id)
        
        assert result == customer
        mock_customer_repo.get_by_id.assert_called_once_with(customer_id)
    
    def test_get_customer_by_id_not_found(self, customer_service, mock_customer_repo):
        """Test getting customer by ID when not found."""
        customer_id = uuid4()
        mock_customer_repo.get_by_id.return_value = None
        
        result = customer_service.get_customer(customer_id)
        
        assert result is None
        mock_customer_repo.get_by_id.assert_called_once_with(customer_id)
    
    def test_get_all_customers(self, customer_service, mock_customer_repo):
        """Test getting all customers."""
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
        
        mock_customer_repo.get_all.return_value = customers
        
        result = customer_service.get_all_customers(limit=10)
        
        assert result == customers
        assert len(result) == 2
        mock_customer_repo.get_all.assert_called_once_with(limit=10)
    
    def test_update_customer(self, customer_service, mock_customer_repo):
        """Test updating a customer."""
        customer_id = uuid4()
        customer_update = CustomerUpdate(
            name="John Doe Updated",
            email="john.updated@example.com",
            phone="+1234567890",
            address="123 Main St Updated"
        )
        
        existing_customer = Customer(
            id=customer_id,
            name="John Doe",
            email="john@example.com",
            phone="+1234567890",
            address="123 Main St",
            created_at=datetime.now(),
            updated_at=datetime.now()
        )
        
        updated_customer = Customer(
            id=customer_id,
            name="John Doe Updated",
            email="john.updated@example.com",
            phone="+1234567890",
            address="123 Main St Updated",
            created_at=existing_customer.created_at,
            updated_at=datetime.now()
        )
        
        mock_customer_repo.get_by_id.return_value = existing_customer
        mock_customer_repo.update.return_value = updated_customer
        
        result = customer_service.update_customer(customer_id, customer_update)
        
        assert result == updated_customer
        mock_customer_repo.get_by_id.assert_called_once_with(customer_id)
        mock_customer_repo.update.assert_called_once()
    
    def test_update_customer_not_found(self, customer_service, mock_customer_repo):
        """Test updating a customer that doesn't exist."""
        customer_id = uuid4()
        customer_update = CustomerUpdate(name="Updated Name")
        
        mock_customer_repo.get_by_id.return_value = None
        
        result = customer_service.update_customer(customer_id, customer_update)
        
        assert result is None
        mock_customer_repo.get_by_id.assert_called_once_with(customer_id)
        mock_customer_repo.update.assert_not_called()
    
    def test_delete_customer(self, customer_service, mock_customer_repo):
        """Test deleting a customer."""
        customer_id = uuid4()
        mock_customer_repo.delete.return_value = True
        
        result = customer_service.delete_customer(customer_id)
        
        assert result is True
        mock_customer_repo.delete.assert_called_once_with(customer_id)


class TestStaffService:
    """Test cases for StaffService."""
    
    @pytest.fixture
    def mock_staff_repo(self):
        """Create a mock StaffRepository."""
        return Mock()
    
    @pytest.fixture
    def staff_service(self, mock_staff_repo):
        """Create StaffService with mock repository."""
        return StaffService(mock_staff_repo)
    
    def test_create_staff(self, staff_service, mock_staff_repo):
        """Test creating a staff member."""
        staff_create = StaffCreate(
            name="Dr. Smith",
            email="dr.smith@salon.com",
            phone="+1234567890",
            role="STYLIST",
            specialties=["Hair Cut", "Hair Color"],
            is_active=True
        )
        
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
        
        mock_staff_repo.create.return_value = created_staff
        
        result = staff_service.create_staff(staff_create)
        
        assert result == created_staff
        mock_staff_repo.create.assert_called_once()
    
    def test_get_staff_by_id(self, staff_service, mock_staff_repo):
        """Test getting staff by ID."""
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
        
        mock_staff_repo.get_by_id.return_value = staff
        
        result = staff_service.get_staff(staff_id)
        
        assert result == staff
        mock_staff_repo.get_by_id.assert_called_once_with(staff_id)


class TestServiceService:
    """Test cases for ServiceService."""
    
    @pytest.fixture
    def mock_service_repo(self):
        """Create a mock ServiceRepository."""
        return Mock()
    
    @pytest.fixture
    def service_service(self, mock_service_repo):
        """Create ServiceService with mock repository."""
        return ServiceService(mock_service_repo)
    
    def test_create_service(self, service_service, mock_service_repo):
        """Test creating a service."""
        service_create = ServiceCreate(
            name="Hair Cut",
            description="Professional hair cutting",
            price=50.00,
            duration=60,
            category="Hair",
            is_active=True
        )
        
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
        
        mock_service_repo.create.return_value = created_service
        
        result = service_service.create_service(service_create)
        
        assert result == created_service
        mock_service_repo.create.assert_called_once()
    
    def test_get_service_by_id(self, service_service, mock_service_repo):
        """Test getting service by ID."""
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
        
        mock_service_repo.get_by_id.return_value = service
        
        result = service_service.get_service(service_id)
        
        assert result == service
        mock_service_repo.get_by_id.assert_called_once_with(service_id)


class TestAppointmentService:
    """Test cases for AppointmentService."""
    
    @pytest.fixture
    def mock_appointment_repo(self):
        """Create a mock AppointmentRepository."""
        return Mock()
    
    @pytest.fixture
    def mock_customer_repo(self):
        """Create a mock CustomerRepository."""
        return Mock()
    
    @pytest.fixture
    def mock_service_repo(self):
        """Create a mock ServiceRepository."""
        return Mock()
    
    @pytest.fixture
    def mock_staff_repo(self):
        """Create a mock StaffRepository."""
        return Mock()
    
    @pytest.fixture
    def appointment_service(self, mock_appointment_repo, mock_customer_repo, mock_service_repo, mock_staff_repo):
        """Create AppointmentService with mock repositories."""
        return AppointmentService(
            mock_appointment_repo,
            mock_customer_repo,
            mock_service_repo,
            mock_staff_repo
        )
    
    def test_create_appointment(self, appointment_service, mock_appointment_repo, mock_customer_repo, mock_service_repo, mock_staff_repo):
        """Test creating an appointment."""
        customer_id = uuid4()
        service_id = uuid4()
        staff_id = uuid4()
        
        appointment_create = AppointmentCreate(
            customer_id=customer_id,
            service_id=service_id,
            staff_id=staff_id,
            appointment_time=datetime(2024, 12, 25, 14, 30),
            status="SCHEDULED",
            notes="First time customer"
        )
        
        # Mock the existence checks
        mock_customer_repo.get_by_id.return_value = Customer(
            id=customer_id,
            name="John Doe",
            email="john@example.com",
            phone="+1234567890",
            address="123 Main St",
            created_at=datetime.now(),
            updated_at=datetime.now()
        )
        
        mock_service_repo.get_by_id.return_value = Service(
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
        
        mock_staff_repo.get_by_id.return_value = Staff(
            id=staff_id,
            name="Dr. Smith",
            email="dr.smith@salon.com",
            phone="+1234567890",
            role="STYLIST",
            specialties=["Hair Cut"],
            is_active=True,
            created_at=datetime.now(),
            updated_at=datetime.now()
        )
        
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
        
        mock_appointment_repo.create.return_value = created_appointment
        
        result = appointment_service.create_appointment(appointment_create)
        
        assert result == created_appointment
        mock_customer_repo.get_by_id.assert_called_once_with(customer_id)
        mock_service_repo.get_by_id.assert_called_once_with(service_id)
        mock_staff_repo.get_by_id.assert_called_once_with(staff_id)
        mock_appointment_repo.create.assert_called_once()
    
    def test_get_appointment_by_id(self, appointment_service, mock_appointment_repo):
        """Test getting appointment by ID."""
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
        
        mock_appointment_repo.get_by_id.return_value = appointment
        
        result = appointment_service.get_appointment(appointment_id)
        
        assert result == appointment
        mock_appointment_repo.get_by_id.assert_called_once_with(appointment_id)
