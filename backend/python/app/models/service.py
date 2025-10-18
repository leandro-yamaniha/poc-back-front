"""
Service model for Beauty Salon Management System
"""

from datetime import datetime
from decimal import Decimal
from typing import Optional
from uuid import UUID, uuid4
from pydantic import BaseModel, Field, validator


class Service(BaseModel):
    """Service model with validation"""
    
    id: UUID = Field(default_factory=uuid4)
    name: str = Field(..., min_length=1, max_length=100, description="Service name")
    description: Optional[str] = Field(None, max_length=500, description="Service description")
    duration: int = Field(..., gt=0, description="Duration in minutes")
    price: Decimal = Field(..., gt=0, description="Service price")
    category: str = Field(..., min_length=1, max_length=50, description="Service category")
    is_active: bool = Field(default=True, description="Whether service is active")
    created_at: datetime = Field(default_factory=datetime.now)
    updated_at: datetime = Field(default_factory=datetime.now)
    
    class Config:
        """Pydantic configuration"""
        json_encoders = {
            datetime: lambda v: v.isoformat(),
            UUID: lambda v: str(v),
            Decimal: lambda v: float(v)
        }
        schema_extra = {
            "example": {
                "name": "Corte de Cabelo",
                "description": "Corte de cabelo masculino ou feminino",
                "duration": 60,
                "price": 50.00,
                "category": "Cabelo",
                "is_active": True
            }
        }
    
    @validator('name')
    def validate_name(cls, v):
        """Validate service name"""
        if not v or not v.strip():
            raise ValueError('Name cannot be empty')
        return v.strip()
    
    @validator('category')
    def validate_category(cls, v):
        """Validate service category"""
        if not v or not v.strip():
            raise ValueError('Category cannot be empty')
        return v.strip()
    
    @validator('price')
    def validate_price(cls, v):
        """Validate service price"""
        if v <= 0:
            raise ValueError('Price must be greater than zero')
        return v
    
    def update_timestamp(self):
        """Update the updated_at timestamp"""
        self.updated_at = datetime.now()


class ServiceCreate(BaseModel):
    """Schema for creating a service"""
    name: str = Field(..., min_length=1, max_length=100)
    description: Optional[str] = Field(None, max_length=500)
    duration: int = Field(..., gt=0)
    price: Decimal = Field(..., gt=0)
    category: str = Field(..., min_length=1, max_length=50)
    is_active: bool = Field(default=True)


class ServiceUpdate(BaseModel):
    """Schema for updating a service"""
    name: Optional[str] = Field(None, min_length=1, max_length=100)
    description: Optional[str] = Field(None, max_length=500)
    duration: Optional[int] = Field(None, gt=0)
    price: Optional[Decimal] = Field(None, gt=0)
    category: Optional[str] = Field(None, min_length=1, max_length=50)
    is_active: Optional[bool] = None


class ServiceResponse(BaseModel):
    """Schema for service response"""
    id: UUID
    name: str
    description: Optional[str]
    duration: int
    price: Decimal
    category: str
    is_active: bool
    created_at: datetime
    updated_at: datetime
    
    class Config:
        json_encoders = {
            datetime: lambda v: v.isoformat(),
            UUID: lambda v: str(v),
            Decimal: lambda v: float(v)
        }
