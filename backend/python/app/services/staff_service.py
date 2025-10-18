"""
Staff service for Beauty Salon Management System
"""

import logging
from typing import List, Optional
from uuid import UUID
from fastapi import HTTPException, status

from app.models.staff import Staff, StaffCreate, StaffUpdate
from app.repositories.staff_repository import StaffRepository


logger = logging.getLogger(__name__)


class StaffService:
    """Service for staff business logic"""
    
    def __init__(self, staff_repository: StaffRepository):
        self.staff_repository = staff_repository
    
    async def create_staff(self, staff_data: StaffCreate) -> Staff:
        """Create a new staff member with business validation"""
        try:
            # Check if email already exists
            if await self.staff_repository.exists_by_email(staff_data.email):
                raise HTTPException(
                    status_code=status.HTTP_409_CONFLICT,
                    detail="Staff member with this email already exists"
                )
            
            # Create staff member
            staff = await self.staff_repository.create(staff_data)
            logger.info(f"Staff member created successfully: {staff.id}")
            return staff
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error creating staff member: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to create staff member"
            )
    
    async def get_staff_by_id(self, staff_id: UUID) -> Staff:
        """Get staff member by ID"""
        try:
            staff = await self.staff_repository.get_by_id(staff_id)
            if not staff:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Staff member not found"
                )
            return staff
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error getting staff member by ID {staff_id}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get staff member"
            )
    
    async def get_staff_by_email(self, email: str) -> Staff:
        """Get staff member by email"""
        try:
            staff = await self.staff_repository.get_by_email(email)
            if not staff:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Staff member not found"
                )
            return staff
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error getting staff member by email {email}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get staff member"
            )
    
    async def get_all_staff(self, limit: int = 100) -> List[Staff]:
        """Get all staff members"""
        try:
            staff_members = await self.staff_repository.get_all(limit)
            return staff_members
            
        except Exception as e:
            logger.error(f"Error getting all staff members: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get staff members"
            )
    
    async def get_active_staff(self, limit: int = 100) -> List[Staff]:
        """Get all active staff members"""
        try:
            staff_members = await self.staff_repository.get_active(limit)
            return staff_members
            
        except Exception as e:
            logger.error(f"Error getting active staff members: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get active staff members"
            )
    
    async def get_staff_by_role(self, role: str, active_only: bool = False, limit: int = 100) -> List[Staff]:
        """Get staff members by role"""
        try:
            if not role or not role.strip():
                raise HTTPException(
                    status_code=status.HTTP_400_BAD_REQUEST,
                    detail="Role cannot be empty"
                )
            
            staff_members = await self.staff_repository.get_by_role(role.strip(), active_only, limit)
            return staff_members
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error getting staff members by role {role}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get staff members by role"
            )
    
    async def get_staff_by_specialty(self, specialty: str, limit: int = 100) -> List[Staff]:
        """Get staff members by specialty"""
        try:
            if not specialty or not specialty.strip():
                raise HTTPException(
                    status_code=status.HTTP_400_BAD_REQUEST,
                    detail="Specialty cannot be empty"
                )
            
            staff_members = await self.staff_repository.get_by_specialty(specialty.strip(), limit)
            return staff_members
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error getting staff members by specialty {specialty}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get staff members by specialty"
            )
    
    async def update_staff(self, staff_id: UUID, staff_data: StaffUpdate) -> Staff:
        """Update staff member with business validation"""
        try:
            # Check if staff member exists
            existing_staff = await self.staff_repository.get_by_id(staff_id)
            if not existing_staff:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Staff member not found"
                )
            
            # If email is being updated, check if it's already taken by another staff member
            if staff_data.email and staff_data.email != existing_staff.email:
                if await self.staff_repository.exists_by_email(staff_data.email, exclude_id=staff_id):
                    raise HTTPException(
                        status_code=status.HTTP_409_CONFLICT,
                        detail="Email already exists for another staff member"
                    )
            
            # Update staff member
            updated_staff = await self.staff_repository.update(staff_id, staff_data)
            if not updated_staff:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Staff member not found"
                )
            
            logger.info(f"Staff member updated successfully: {staff_id}")
            return updated_staff
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error updating staff member {staff_id}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to update staff member"
            )
    
    async def delete_staff(self, staff_id: UUID) -> bool:
        """Delete staff member"""
        try:
            # Check if staff member exists
            existing_staff = await self.staff_repository.get_by_id(staff_id)
            if not existing_staff:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Staff member not found"
                )
            
            # TODO: Check if staff member has active appointments before deletion
            # This would require appointment repository dependency
            
            # Delete staff member
            deleted = await self.staff_repository.delete(staff_id)
            if deleted:
                logger.info(f"Staff member deleted successfully: {staff_id}")
            
            return deleted
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error deleting staff member {staff_id}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to delete staff member"
            )
    
    async def search_staff(self, name: str, limit: int = 50) -> List[Staff]:
        """Search staff members by name"""
        try:
            if not name or not name.strip():
                raise HTTPException(
                    status_code=status.HTTP_400_BAD_REQUEST,
                    detail="Search term cannot be empty"
                )
            
            staff_members = await self.staff_repository.search_by_name(name.strip(), limit)
            return staff_members
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error searching staff members by name {name}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to search staff members"
            )
    
    async def get_staff_roles(self) -> List[str]:
        """Get all staff roles"""
        try:
            roles = await self.staff_repository.get_roles()
            return roles
            
        except Exception as e:
            logger.error(f"Error getting staff roles: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get staff roles"
            )
    
    async def get_staff_count(self) -> int:
        """Get total count of staff members"""
        try:
            count = await self.staff_repository.count()
            return count
            
        except Exception as e:
            logger.error(f"Error getting staff count: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get staff count"
            )
    
    async def get_active_staff_count(self) -> int:
        """Get count of active staff members"""
        try:
            count = await self.staff_repository.count_active()
            return count
            
        except Exception as e:
            logger.error(f"Error getting active staff count: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get active staff count"
            )
    
    async def staff_exists(self, staff_id: UUID) -> bool:
        """Check if staff member exists"""
        try:
            staff = await self.staff_repository.get_by_id(staff_id)
            return staff is not None
            
        except Exception as e:
            logger.error(f"Error checking staff existence {staff_id}: {e}")
            return False
    
    async def staff_is_active(self, staff_id: UUID) -> bool:
        """Check if staff member exists and is active"""
        try:
            staff = await self.staff_repository.get_by_id(staff_id)
            return staff is not None and staff.is_active
            
        except Exception as e:
            logger.error(f"Error checking staff active status {staff_id}: {e}")
            return False
