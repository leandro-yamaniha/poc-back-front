"""
Staff model for Beauty Salon Management System
"""

from datetime import datetime
from typing import Optional, List
from uuid import UUID, uuid4
from pydantic import BaseModel, Field, EmailStr, validator


class Staff(BaseModel):
    """Staff model with validation"""
    
    id: UUID = Field(default_factory=uuid4)
    name: str = Field(..., min_length=1, max_length=100, description="Staff member name")
    email: EmailStr = Field(..., description="Staff member email address")
    phone: Optional[str] = Field(None, pattern=r"^\d{10,11}$", description="Phone number (10-11 digits)")
    role: str = Field(..., min_length=1, max_length=50, description="Staff role")
    specialties: Optional[List[str]] = Field(default=[], description="Staff specialties")
    is_active: bool = Field(default=True, description="Whether staff member is active")
    hire_date: Optional[datetime] = Field(default_factory=datetime.now, description="Hire date")
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
                "name": "Maria Santos",
                "email": "maria.santos@salao.com",
                "phone": "11987654321",
                "role": "Cabeleireira",
                "specialties": ["Corte", "Coloração", "Escova"],
                "is_active": True
            }
        }
    
    @validator('name')
    def validate_name(cls, v):
        """Validate staff name"""
        if not v or not v.strip():
            raise ValueError('Name cannot be empty')
        return v.strip()
    
    @validator('role')
    def validate_role(cls, v):
        """Validate staff role"""
        if not v or not v.strip():
            raise ValueError('Role cannot be empty')
        return v.strip()
    
    @validator('phone')
    def validate_phone(cls, v):
        """Validate phone number"""
        if v and not v.isdigit():
            raise ValueError('Phone must contain only digits')
        return v
    
    @validator('specialties')
    def validate_specialties(cls, v):
        """Validate specialties list"""
        if v is None:
            return []
        return [specialty.strip() for specialty in v if specialty.strip()]
    
    def update_timestamp(self):
        """Update the updated_at timestamp"""
        self.updated_at = datetime.now()


class StaffCreate(BaseModel):
    """Schema for creating a staff member"""
    name: str = Field(..., min_length=1, max_length=100)
    email: EmailStr
    phone: Optional[str] = Field(None, pattern=r"^\d{10,11}$")
    role: str = Field(..., min_length=1, max_length=50)
    specialties: Optional[List[str]] = Field(default=[])
    is_active: bool = Field(default=True)
    hire_date: Optional[datetime] = Field(default_factory=datetime.now)


class StaffUpdate(BaseModel):
    """Schema for updating a staff member"""
    name: Optional[str] = Field(None, min_length=1, max_length=100)
    email: Optional[EmailStr] = None
    phone: Optional[str] = Field(None, pattern=r"^\d{10,11}$")
    role: Optional[str] = Field(None, min_length=1, max_length=50)
    specialties: Optional[List[str]] = None
    is_active: Optional[bool] = None
    hire_date: Optional[datetime] = None


class StaffResponse(BaseModel):
    """Schema for staff response"""
    id: UUID
    name: str
    email: str
    phone: Optional[str]
    role: str
    specialties: List[str]
    is_active: bool
    hire_date: Optional[datetime]
    created_at: datetime
    updated_at: datetime
    
    class Config:
        json_encoders = {
            datetime: lambda v: v.isoformat(),
            UUID: lambda v: str(v)
        }
