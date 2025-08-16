"""
Customer model for Beauty Salon Management System
"""

from datetime import datetime
from typing import Optional
from uuid import UUID, uuid4
from pydantic import BaseModel, Field, EmailStr, validator


class Customer(BaseModel):
    """Customer model with validation"""
    
    id: UUID = Field(default_factory=uuid4)
    name: str = Field(..., min_length=1, max_length=100, description="Customer name")
    email: EmailStr = Field(..., description="Customer email address")
    phone: Optional[str] = Field(None, pattern=r"^\d{10,11}$", description="Phone number (10-11 digits)")
    address: Optional[str] = Field(None, max_length=255, description="Customer address")
    created_at: datetime = Field(default_factory=datetime.now)
    updated_at: datetime = Field(default_factory=datetime.now)
    
    class Config:
        """Pydantic configuration"""
        json_encoders = {
            datetime: lambda v: v.isoformat(),
            UUID: lambda v: str(v)
        }
        schema_extra = {
            "example": {
                "name": "João Silva",
                "email": "joao.silva@email.com",
                "phone": "11987654321",
                "address": "Rua das Flores, 123, São Paulo, SP"
            }
        }
    
    @validator('name')
    def validate_name(cls, v):
        """Validate customer name"""
        if not v or not v.strip():
            raise ValueError('Name cannot be empty')
        return v.strip()
    
    @validator('phone')
    def validate_phone(cls, v):
        """Validate phone number"""
        if v and not v.isdigit():
            raise ValueError('Phone must contain only digits')
        return v
    
    def update_timestamp(self):
        """Update the updated_at timestamp"""
        self.updated_at = datetime.now()


class CustomerCreate(BaseModel):
    """Schema for creating a customer"""
    name: str = Field(..., min_length=1, max_length=100)
    email: EmailStr
    phone: Optional[str] = Field(None, pattern=r"^\d{10,11}$")
    address: Optional[str] = Field(None, max_length=255)


class CustomerUpdate(BaseModel):
    """Schema for updating a customer"""
    name: Optional[str] = Field(None, min_length=1, max_length=100)
    email: Optional[EmailStr] = None
    phone: Optional[str] = Field(None, pattern=r"^\d{10,11}$")
    address: Optional[str] = Field(None, max_length=255)


class CustomerResponse(BaseModel):
    """Schema for customer response"""
    id: UUID
    name: str
    email: str
    phone: Optional[str]
    address: Optional[str]
    created_at: datetime
    updated_at: datetime
    
    class Config:
        json_encoders = {
            datetime: lambda v: v.isoformat(),
            UUID: lambda v: str(v)
        }
