"""
Appointment model for Beauty Salon Management System
"""

from datetime import datetime, date, time
from decimal import Decimal
from enum import Enum
from typing import Optional
from uuid import UUID, uuid4
from pydantic import BaseModel, Field, validator


class AppointmentStatus(str, Enum):
    """Appointment status enumeration"""
    SCHEDULED = "scheduled"
    CONFIRMED = "confirmed"
    IN_PROGRESS = "in_progress"
    COMPLETED = "completed"
    CANCELLED = "cancelled"
    NO_SHOW = "no_show"


class Appointment(BaseModel):
    """Appointment model with validation"""
    
    id: UUID = Field(default_factory=uuid4)
    customer_id: UUID = Field(..., description="Customer ID")
    staff_id: UUID = Field(..., description="Staff member ID")
    service_id: UUID = Field(..., description="Service ID")
    appointment_date: date = Field(..., description="Appointment date")
    appointment_time: time = Field(..., description="Appointment time")
    status: AppointmentStatus = Field(default=AppointmentStatus.SCHEDULED, description="Appointment status")
    notes: Optional[str] = Field(None, max_length=500, description="Appointment notes")
    price: Optional[Decimal] = Field(None, gt=0, description="Appointment price")
    created_at: datetime = Field(default_factory=datetime.now)
    updated_at: datetime = Field(default_factory=datetime.now)
    
    class Config:
        """Pydantic configuration"""
        json_encoders = {
            datetime: lambda v: v.isoformat(),
            date: lambda v: v.isoformat(),
            time: lambda v: v.isoformat(),
            UUID: lambda v: str(v),
            Decimal: lambda v: float(v)
        }
        schema_extra = {
            "example": {
                "customer_id": "123e4567-e89b-12d3-a456-426614174000",
                "staff_id": "123e4567-e89b-12d3-a456-426614174001",
                "service_id": "123e4567-e89b-12d3-a456-426614174002",
                "appointment_date": "2024-01-15",
                "appointment_time": "14:30:00",
                "status": "scheduled",
                "notes": "Cliente prefere corte mais curto",
                "price": 50.00
            }
        }
    
    @validator('appointment_date')
    def validate_appointment_date(cls, v):
        """Validate appointment date is not in the past"""
        if v < date.today():
            raise ValueError('Appointment date cannot be in the past')
        return v
    
    @validator('price')
    def validate_price(cls, v):
        """Validate appointment price"""
        if v is not None and v <= 0:
            raise ValueError('Price must be greater than zero')
        return v
    
    @validator('notes')
    def validate_notes(cls, v):
        """Validate notes"""
        if v:
            return v.strip()
        return v
    
    def update_timestamp(self):
        """Update the updated_at timestamp"""
        self.updated_at = datetime.now()
    
    @property
    def appointment_datetime(self) -> datetime:
        """Get appointment as datetime object"""
        return datetime.combine(self.appointment_date, self.appointment_time)


class AppointmentCreate(BaseModel):
    """Schema for creating an appointment"""
    customer_id: UUID
    staff_id: UUID
    service_id: UUID
    appointment_date: date
    appointment_time: time
    status: AppointmentStatus = Field(default=AppointmentStatus.SCHEDULED)
    notes: Optional[str] = Field(None, max_length=500)
    price: Optional[Decimal] = Field(None, gt=0)


class AppointmentUpdate(BaseModel):
    """Schema for updating an appointment"""
    customer_id: Optional[UUID] = None
    staff_id: Optional[UUID] = None
    service_id: Optional[UUID] = None
    appointment_date: Optional[date] = None
    appointment_time: Optional[time] = None
    status: Optional[AppointmentStatus] = None
    notes: Optional[str] = Field(None, max_length=500)
    price: Optional[Decimal] = Field(None, gt=0)


class AppointmentStatusUpdate(BaseModel):
    """Schema for updating appointment status"""
    status: AppointmentStatus


class AppointmentResponse(BaseModel):
    """Schema for appointment response"""
    id: UUID
    customer_id: UUID
    staff_id: UUID
    service_id: UUID
    appointment_date: date
    appointment_time: time
    status: AppointmentStatus
    notes: Optional[str]
    price: Optional[Decimal]
    created_at: datetime
    updated_at: datetime
    
    class Config:
        json_encoders = {
            datetime: lambda v: v.isoformat(),
            date: lambda v: v.isoformat(),
            time: lambda v: v.isoformat(),
            UUID: lambda v: str(v),
            Decimal: lambda v: float(v)
        }


class AppointmentWithDetails(BaseModel):
    """Schema for appointment with related entity details"""
    id: UUID
    customer_id: UUID
    customer_name: str
    customer_email: str
    staff_id: UUID
    staff_name: str
    staff_role: str
    service_id: UUID
    service_name: str
    service_category: str
    appointment_date: date
    appointment_time: time
    status: AppointmentStatus
    notes: Optional[str]
    price: Optional[Decimal]
    created_at: datetime
    updated_at: datetime
    
    class Config:
        json_encoders = {
            datetime: lambda v: v.isoformat(),
            date: lambda v: v.isoformat(),
            time: lambda v: v.isoformat(),
            UUID: lambda v: str(v),
            Decimal: lambda v: float(v)
        }
