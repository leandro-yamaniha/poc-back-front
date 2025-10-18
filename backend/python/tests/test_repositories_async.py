"""
Async unit tests for repository classes in the Beauty Salon application.
"""
import pytest
import pytest_asyncio
from unittest.mock import Mock, AsyncMock
from uuid import uuid4
from datetime import datetime

from app.repositories.customer_repository import CustomerRepository
from app.repositories.staff_repository import StaffRepository
from app.repositories.service_repository import ServiceRepository
from app.repositories.appointment_repository import AppointmentRepository
from app.models.customer import Customer
from app.models.staff import Staff
from app.models.service import Service
from app.models.appointment import Appointment


class TestCustomerRepositoryAsync:
    """Test cases for CustomerRepository async methods."""
    
    @pytest.fixture
    def mock_session(self):
        """Create a mock Cassandra session."""
        session = Mock()
        session.execute = Mock()
        return session
    
    @pytest.fixture
    def customer_repo(self, mock_session):
        """Create CustomerRepository with mock session."""
        return CustomerRepository(mock_session)
    
    @pytest.mark.asyncio
    async def test_create_customer(self, customer_repo, mock_session):
        """Test creating a customer."""
        customer = Customer(
            id=uuid4(),
            name="John Doe",
            email="john@example.com",
            phone="+55 11 99999-9999",
            address="123 Main St",
            created_at=datetime.now(),
            updated_at=datetime.now()
        )
        
        result = await customer_repo.create(customer)
        
        assert result == customer
        mock_session.execute.assert_called_once()
    
    @pytest.mark.asyncio
    async def test_get_customer_by_id(self, customer_repo, mock_session):
        """Test getting customer by ID."""
        customer_id = uuid4()
        mock_row = Mock()
        mock_row.id = customer_id
        mock_row.name = "John Doe"
        mock_row.email = "john@example.com"
        mock_row.phone = "+55 11 99999-9999"
        mock_row.address = "123 Main St"
        mock_row.created_at = datetime.now()
        mock_row.updated_at = datetime.now()
        
        mock_session.execute.return_value = [mock_row]
        
        result = await customer_repo.get_by_id(customer_id)
        
        assert result is not None
        assert result.id == customer_id
        assert result.name == "John Doe"
        mock_session.execute.assert_called_once()
    
    @pytest.mark.asyncio
    async def test_get_customer_by_id_not_found(self, customer_repo, mock_session):
        """Test getting customer by ID when not found."""
        customer_id = uuid4()
        mock_session.execute.return_value = []
        
        result = await customer_repo.get_by_id(customer_id)
        
        assert result is None
        mock_session.execute.assert_called_once()
    
    @pytest.mark.asyncio
    async def test_get_all_customers(self, customer_repo, mock_session):
        """Test getting all customers."""
        mock_row1 = Mock()
        mock_row1.id = uuid4()
        mock_row1.name = "John Doe"
        mock_row1.email = "john@example.com"
        mock_row1.phone = "+55 11 99999-9999"
        mock_row1.address = "123 Main St"
        mock_row1.created_at = datetime.now()
        mock_row1.updated_at = datetime.now()
        
        mock_row2 = Mock()
        mock_row2.id = uuid4()
        mock_row2.name = "Jane Smith"
        mock_row2.email = "jane@example.com"
        mock_row2.phone = "+55 11 88888-8888"
        mock_row2.address = "456 Oak Ave"
        mock_row2.created_at = datetime.now()
        mock_row2.updated_at = datetime.now()
        
        mock_session.execute.return_value = [mock_row1, mock_row2]
        
        result = await customer_repo.get_all(limit=10)
        
        assert len(result) == 2
        assert result[0].name == "John Doe"
        assert result[1].name == "Jane Smith"
        mock_session.execute.assert_called_once()
    
    @pytest.mark.asyncio
    async def test_update_customer(self, customer_repo, mock_session):
        """Test updating a customer."""
        customer = Customer(
            id=uuid4(),
            name="John Doe Updated",
            email="john.updated@example.com",
            phone="+55 11 99999-9999",
            address="123 Main St Updated",
            created_at=datetime.now(),
            updated_at=datetime.now()
        )
        
        result = await customer_repo.update(customer)
        
        assert result == customer
        mock_session.execute.assert_called_once()
    
    @pytest.mark.asyncio
    async def test_delete_customer(self, customer_repo, mock_session):
        """Test deleting a customer."""
        customer_id = uuid4()
        
        result = await customer_repo.delete(customer_id)
        
        assert result is True
        mock_session.execute.assert_called_once()


class TestStaffRepositoryAsync:
    """Test cases for StaffRepository async methods."""
    
    @pytest.fixture
    def mock_session(self):
        """Create a mock Cassandra session."""
        session = Mock()
        session.execute = Mock()
        return session
    
    @pytest.fixture
    def staff_repo(self, mock_session):
        """Create StaffRepository with mock session."""
        return StaffRepository(mock_session)
    
    @pytest.mark.asyncio
    async def test_create_staff(self, staff_repo, mock_session):
        """Test creating a staff member."""
        staff = Staff(
            id=uuid4(),
            name="Dr. Smith",
            email="dr.smith@salon.com",
            phone="+55 11 99999-9999",
            role="STYLIST",
            specialties=["Hair Cut", "Hair Color"],
            is_active=True,
            created_at=datetime.now(),
            updated_at=datetime.now()
        )
        
        result = await staff_repo.create(staff)
        
        assert result == staff
        mock_session.execute.assert_called_once()
    
    @pytest.mark.asyncio
    async def test_get_staff_by_id(self, staff_repo, mock_session):
        """Test getting staff by ID."""
        staff_id = uuid4()
        mock_row = Mock()
        mock_row.id = staff_id
        mock_row.name = "Dr. Smith"
        mock_row.email = "dr.smith@salon.com"
        mock_row.phone = "+55 11 99999-9999"
        mock_row.role = "STYLIST"
        mock_row.specialties = ["Hair Cut", "Hair Color"]
        mock_row.is_active = True
        mock_row.created_at = datetime.now()
        mock_row.updated_at = datetime.now()
        
        mock_session.execute.return_value = [mock_row]
        
        result = await staff_repo.get_by_id(staff_id)
        
        assert result is not None
        assert result.id == staff_id
        assert result.name == "Dr. Smith"
        assert result.role == "STYLIST"
        mock_session.execute.assert_called_once()


class TestServiceRepositoryAsync:
    """Test cases for ServiceRepository async methods."""
    
    @pytest.fixture
    def mock_session(self):
        """Create a mock Cassandra session."""
        session = Mock()
        session.execute = Mock()
        return session
    
    @pytest.fixture
    def service_repo(self, mock_session):
        """Create ServiceRepository with mock session."""
        return ServiceRepository(mock_session)
    
    @pytest.mark.asyncio
    async def test_create_service(self, service_repo, mock_session):
        """Test creating a service."""
        service = Service(
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
        
        result = await service_repo.create(service)
        
        assert result == service
        mock_session.execute.assert_called_once()
    
    @pytest.mark.asyncio
    async def test_get_service_by_id(self, service_repo, mock_session):
        """Test getting service by ID."""
        service_id = uuid4()
        mock_row = Mock()
        mock_row.id = service_id
        mock_row.name = "Hair Cut"
        mock_row.description = "Professional hair cutting"
        mock_row.price = 50.00
        mock_row.duration = 60
        mock_row.category = "Hair"
        mock_row.is_active = True
        mock_row.created_at = datetime.now()
        mock_row.updated_at = datetime.now()
        
        mock_session.execute.return_value = [mock_row]
        
        result = await service_repo.get_by_id(service_id)
        
        assert result is not None
        assert result.id == service_id
        assert result.name == "Hair Cut"
        assert result.price == 50.00
        mock_session.execute.assert_called_once()


class TestAppointmentRepositoryAsync:
    """Test cases for AppointmentRepository async methods."""
    
    @pytest.fixture
    def mock_session(self):
        """Create a mock Cassandra session."""
        session = Mock()
        session.execute = Mock()
        return session
    
    @pytest.fixture
    def appointment_repo(self, mock_session):
        """Create AppointmentRepository with mock session."""
        return AppointmentRepository(mock_session)
    
    @pytest.mark.asyncio
    async def test_create_appointment(self, appointment_repo, mock_session):
        """Test creating an appointment."""
        appointment = Appointment(
            id=uuid4(),
            customer_id=uuid4(),
            service_id=uuid4(),
            staff_id=uuid4(),
            appointment_time=datetime(2024, 12, 25, 14, 30),
            status="SCHEDULED",
            notes="First time customer",
            created_at=datetime.now(),
            updated_at=datetime.now()
        )
        
        result = await appointment_repo.create(appointment)
        
        assert result == appointment
        mock_session.execute.assert_called_once()
    
    @pytest.mark.asyncio
    async def test_get_appointment_by_id(self, appointment_repo, mock_session):
        """Test getting appointment by ID."""
        appointment_id = uuid4()
        customer_id = uuid4()
        service_id = uuid4()
        staff_id = uuid4()
        
        mock_row = Mock()
        mock_row.id = appointment_id
        mock_row.customer_id = customer_id
        mock_row.service_id = service_id
        mock_row.staff_id = staff_id
        mock_row.appointment_time = datetime(2024, 12, 25, 14, 30)
        mock_row.status = "SCHEDULED"
        mock_row.notes = "First time customer"
        mock_row.created_at = datetime.now()
        mock_row.updated_at = datetime.now()
        
        mock_session.execute.return_value = [mock_row]
        
        result = await appointment_repo.get_by_id(appointment_id)
        
        assert result is not None
        assert result.id == appointment_id
        assert result.customer_id == customer_id
        assert result.service_id == service_id
        assert result.staff_id == staff_id
        assert result.status == "SCHEDULED"
        mock_session.execute.assert_called_once()
