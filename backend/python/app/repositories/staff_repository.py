"""
Staff repository for Beauty Salon Management System
"""

import logging
from typing import List, Optional
from uuid import UUID
from datetime import datetime
from cassandra.cluster import Session

from app.models.staff import Staff, StaffCreate, StaffUpdate


logger = logging.getLogger(__name__)


class StaffRepository:
    """Repository for staff data operations"""
    
    def __init__(self, session: Session):
        self.session = session
        self.table_name = "staff"
    
    async def create(self, staff_data: StaffCreate) -> Staff:
        """Create a new staff member"""
        try:
            staff = Staff(**staff_data.dict())
            
            query = f"""
            INSERT INTO {self.table_name} 
            (id, name, email, phone, role, specialties, is_active, hire_date, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """
            
            self.session.execute(query, [
                staff.id,
                staff.name,
                staff.email,
                staff.phone,
                staff.role,
                staff.specialties,
                staff.is_active,
                staff.hire_date,
                staff.created_at,
                staff.updated_at
            ])
            
            logger.info(f"Staff member created with ID: {staff.id}")
            return staff
            
        except Exception as e:
            logger.error(f"Error creating staff member: {e}")
            raise
    
    async def get_by_id(self, staff_id: UUID) -> Optional[Staff]:
        """Get staff member by ID"""
        try:
            query = f"SELECT * FROM {self.table_name} WHERE id = ?"
            result = self.session.execute(query, [staff_id])
            row = result.one()
            
            if row:
                return Staff(
                    id=row.id,
                    name=row.name,
                    email=row.email,
                    phone=row.phone,
                    role=row.role,
                    specialties=row.specialties or [],
                    is_active=row.is_active,
                    hire_date=row.hire_date,
                    created_at=row.created_at,
                    updated_at=row.updated_at
                )
            return None
            
        except Exception as e:
            logger.error(f"Error getting staff member by ID {staff_id}: {e}")
            raise
    
    async def get_by_email(self, email: str) -> Optional[Staff]:
        """Get staff member by email"""
        try:
            query = f"SELECT * FROM {self.table_name} WHERE email = ? ALLOW FILTERING"
            result = self.session.execute(query, [email])
            row = result.one()
            
            if row:
                return Staff(
                    id=row.id,
                    name=row.name,
                    email=row.email,
                    phone=row.phone,
                    role=row.role,
                    specialties=row.specialties or [],
                    is_active=row.is_active,
                    hire_date=row.hire_date,
                    created_at=row.created_at,
                    updated_at=row.updated_at
                )
            return None
            
        except Exception as e:
            logger.error(f"Error getting staff member by email {email}: {e}")
            raise
    
    async def get_all(self, limit: int = 100) -> List[Staff]:
        """Get all staff members with optional limit"""
        try:
            query = f"SELECT * FROM {self.table_name} LIMIT ?"
            result = self.session.execute(query, [limit])
            
            staff_members = []
            for row in result:
                staff = Staff(
                    id=row.id,
                    name=row.name,
                    email=row.email,
                    phone=row.phone,
                    role=row.role,
                    specialties=row.specialties or [],
                    is_active=row.is_active,
                    hire_date=row.hire_date,
                    created_at=row.created_at,
                    updated_at=row.updated_at
                )
                staff_members.append(staff)
            
            return staff_members
            
        except Exception as e:
            logger.error(f"Error getting all staff members: {e}")
            raise
    
    async def get_active(self, limit: int = 100) -> List[Staff]:
        """Get all active staff members"""
        try:
            query = f"SELECT * FROM {self.table_name} WHERE is_active = ? LIMIT ? ALLOW FILTERING"
            result = self.session.execute(query, [True, limit])
            
            staff_members = []
            for row in result:
                staff = Staff(
                    id=row.id,
                    name=row.name,
                    email=row.email,
                    phone=row.phone,
                    role=row.role,
                    specialties=row.specialties or [],
                    is_active=row.is_active,
                    hire_date=row.hire_date,
                    created_at=row.created_at,
                    updated_at=row.updated_at
                )
                staff_members.append(staff)
            
            return staff_members
            
        except Exception as e:
            logger.error(f"Error getting active staff members: {e}")
            raise
    
    async def get_by_role(self, role: str, active_only: bool = False, limit: int = 100) -> List[Staff]:
        """Get staff members by role"""
        try:
            if active_only:
                query = f"""
                SELECT * FROM {self.table_name} 
                WHERE role = ? AND is_active = ? 
                LIMIT ? ALLOW FILTERING
                """
                result = self.session.execute(query, [role, True, limit])
            else:
                query = f"""
                SELECT * FROM {self.table_name} 
                WHERE role = ? 
                LIMIT ? ALLOW FILTERING
                """
                result = self.session.execute(query, [role, limit])
            
            staff_members = []
            for row in result:
                staff = Staff(
                    id=row.id,
                    name=row.name,
                    email=row.email,
                    phone=row.phone,
                    role=row.role,
                    specialties=row.specialties or [],
                    is_active=row.is_active,
                    hire_date=row.hire_date,
                    created_at=row.created_at,
                    updated_at=row.updated_at
                )
                staff_members.append(staff)
            
            return staff_members
            
        except Exception as e:
            logger.error(f"Error getting staff members by role {role}: {e}")
            raise
    
    async def get_by_specialty(self, specialty: str, limit: int = 100) -> List[Staff]:
        """Get staff members by specialty"""
        try:
            # Get all staff and filter by specialty (Cassandra limitation with lists)
            all_staff = await self.get_all(limit=1000)
            
            # Filter by specialty
            matching_staff = [
                staff for staff in all_staff
                if specialty.lower() in [s.lower() for s in staff.specialties]
            ]
            
            return matching_staff[:limit]
            
        except Exception as e:
            logger.error(f"Error getting staff members by specialty {specialty}: {e}")
            raise
    
    async def update(self, staff_id: UUID, staff_data: StaffUpdate) -> Optional[Staff]:
        """Update staff member"""
        try:
            # Get existing staff member
            existing_staff = await self.get_by_id(staff_id)
            if not existing_staff:
                return None
            
            # Update fields that are provided
            update_data = staff_data.dict(exclude_unset=True)
            for field, value in update_data.items():
                setattr(existing_staff, field, value)
            
            existing_staff.update_timestamp()
            
            query = f"""
            UPDATE {self.table_name} 
            SET name = ?, email = ?, phone = ?, role = ?, specialties = ?, 
                is_active = ?, hire_date = ?, updated_at = ?
            WHERE id = ?
            """
            
            self.session.execute(query, [
                existing_staff.name,
                existing_staff.email,
                existing_staff.phone,
                existing_staff.role,
                existing_staff.specialties,
                existing_staff.is_active,
                existing_staff.hire_date,
                existing_staff.updated_at,
                staff_id
            ])
            
            logger.info(f"Staff member updated with ID: {staff_id}")
            return existing_staff
            
        except Exception as e:
            logger.error(f"Error updating staff member {staff_id}: {e}")
            raise
    
    async def delete(self, staff_id: UUID) -> bool:
        """Delete staff member"""
        try:
            # Check if staff member exists
            existing_staff = await self.get_by_id(staff_id)
            if not existing_staff:
                return False
            
            query = f"DELETE FROM {self.table_name} WHERE id = ?"
            self.session.execute(query, [staff_id])
            
            logger.info(f"Staff member deleted with ID: {staff_id}")
            return True
            
        except Exception as e:
            logger.error(f"Error deleting staff member {staff_id}: {e}")
            raise
    
    async def exists_by_email(self, email: str, exclude_id: Optional[UUID] = None) -> bool:
        """Check if staff member exists by email"""
        try:
            staff = await self.get_by_email(email)
            if not staff:
                return False
            
            # If excluding a specific ID, check if it's different
            if exclude_id and staff.id == exclude_id:
                return False
            
            return True
            
        except Exception as e:
            logger.error(f"Error checking staff member existence by email {email}: {e}")
            raise
    
    async def search_by_name(self, name: str, limit: int = 50) -> List[Staff]:
        """Search staff members by name (case-insensitive)"""
        try:
            # Get all staff and filter by name (Cassandra limitation)
            all_staff = await self.get_all(limit=1000)
            
            # Filter by name (case-insensitive)
            search_term = name.lower()
            matching_staff = [
                staff for staff in all_staff
                if search_term in staff.name.lower()
            ]
            
            return matching_staff[:limit]
            
        except Exception as e:
            logger.error(f"Error searching staff members by name {name}: {e}")
            raise
    
    async def get_roles(self) -> List[str]:
        """Get all unique staff roles"""
        try:
            # Get all staff and extract unique roles
            all_staff = await self.get_all(limit=1000)
            roles = list(set(staff.role for staff in all_staff))
            return sorted(roles)
            
        except Exception as e:
            logger.error(f"Error getting staff roles: {e}")
            raise
    
    async def count(self) -> int:
        """Get total count of staff members"""
        try:
            query = f"SELECT COUNT(*) FROM {self.table_name}"
            result = self.session.execute(query)
            row = result.one()
            return row.count if row else 0
            
        except Exception as e:
            logger.error(f"Error counting staff members: {e}")
            raise
    
    async def count_active(self) -> int:
        """Get count of active staff members"""
        try:
            query = f"SELECT COUNT(*) FROM {self.table_name} WHERE is_active = ? ALLOW FILTERING"
            result = self.session.execute(query, [True])
            row = result.one()
            return row.count if row else 0
            
        except Exception as e:
            logger.error(f"Error counting active staff members: {e}")
            raise
