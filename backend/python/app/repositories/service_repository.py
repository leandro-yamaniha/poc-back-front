"""
Service repository for Beauty Salon Management System
"""

import logging
from typing import List, Optional
from uuid import UUID
from datetime import datetime
from cassandra.cluster import Session

from app.models.service import Service, ServiceCreate, ServiceUpdate


logger = logging.getLogger(__name__)


class ServiceRepository:
    """Repository for service data operations"""
    
    def __init__(self, session: Session):
        self.session = session
        self.table_name = "services"
    
    async def create(self, service_data: ServiceCreate) -> Service:
        """Create a new service"""
        try:
            service = Service(**service_data.dict())
            
            query = f"""
            INSERT INTO {self.table_name} 
            (id, name, description, duration, price, category, is_active, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """
            
            self.session.execute(query, [
                service.id,
                service.name,
                service.description,
                service.duration,
                service.price,
                service.category,
                service.is_active,
                service.created_at,
                service.updated_at
            ])
            
            logger.info(f"Service created with ID: {service.id}")
            return service
            
        except Exception as e:
            logger.error(f"Error creating service: {e}")
            raise
    
    async def get_by_id(self, service_id: UUID) -> Optional[Service]:
        """Get service by ID"""
        try:
            query = f"SELECT * FROM {self.table_name} WHERE id = ?"
            result = self.session.execute(query, [service_id])
            row = result.one()
            
            if row:
                return Service(
                    id=row.id,
                    name=row.name,
                    description=row.description,
                    duration=row.duration,
                    price=row.price,
                    category=row.category,
                    is_active=row.is_active,
                    created_at=row.created_at,
                    updated_at=row.updated_at
                )
            return None
            
        except Exception as e:
            logger.error(f"Error getting service by ID {service_id}: {e}")
            raise
    
    async def get_all(self, limit: int = 100) -> List[Service]:
        """Get all services with optional limit"""
        try:
            query = f"SELECT * FROM {self.table_name} LIMIT ?"
            result = self.session.execute(query, [limit])
            
            services = []
            for row in result:
                service = Service(
                    id=row.id,
                    name=row.name,
                    description=row.description,
                    duration=row.duration,
                    price=row.price,
                    category=row.category,
                    is_active=row.is_active,
                    created_at=row.created_at,
                    updated_at=row.updated_at
                )
                services.append(service)
            
            return services
            
        except Exception as e:
            logger.error(f"Error getting all services: {e}")
            raise
    
    async def get_active(self, limit: int = 100) -> List[Service]:
        """Get all active services"""
        try:
            query = f"SELECT * FROM {self.table_name} WHERE is_active = ? LIMIT ? ALLOW FILTERING"
            result = self.session.execute(query, [True, limit])
            
            services = []
            for row in result:
                service = Service(
                    id=row.id,
                    name=row.name,
                    description=row.description,
                    duration=row.duration,
                    price=row.price,
                    category=row.category,
                    is_active=row.is_active,
                    created_at=row.created_at,
                    updated_at=row.updated_at
                )
                services.append(service)
            
            return services
            
        except Exception as e:
            logger.error(f"Error getting active services: {e}")
            raise
    
    async def get_by_category(self, category: str, active_only: bool = False, limit: int = 100) -> List[Service]:
        """Get services by category"""
        try:
            if active_only:
                query = f"""
                SELECT * FROM {self.table_name} 
                WHERE category = ? AND is_active = ? 
                LIMIT ? ALLOW FILTERING
                """
                result = self.session.execute(query, [category, True, limit])
            else:
                query = f"""
                SELECT * FROM {self.table_name} 
                WHERE category = ? 
                LIMIT ? ALLOW FILTERING
                """
                result = self.session.execute(query, [category, limit])
            
            services = []
            for row in result:
                service = Service(
                    id=row.id,
                    name=row.name,
                    description=row.description,
                    duration=row.duration,
                    price=row.price,
                    category=row.category,
                    is_active=row.is_active,
                    created_at=row.created_at,
                    updated_at=row.updated_at
                )
                services.append(service)
            
            return services
            
        except Exception as e:
            logger.error(f"Error getting services by category {category}: {e}")
            raise
    
    async def update(self, service_id: UUID, service_data: ServiceUpdate) -> Optional[Service]:
        """Update service"""
        try:
            # Get existing service
            existing_service = await self.get_by_id(service_id)
            if not existing_service:
                return None
            
            # Update fields that are provided
            update_data = service_data.dict(exclude_unset=True)
            for field, value in update_data.items():
                setattr(existing_service, field, value)
            
            existing_service.update_timestamp()
            
            query = f"""
            UPDATE {self.table_name} 
            SET name = ?, description = ?, duration = ?, price = ?, 
                category = ?, is_active = ?, updated_at = ?
            WHERE id = ?
            """
            
            self.session.execute(query, [
                existing_service.name,
                existing_service.description,
                existing_service.duration,
                existing_service.price,
                existing_service.category,
                existing_service.is_active,
                existing_service.updated_at,
                service_id
            ])
            
            logger.info(f"Service updated with ID: {service_id}")
            return existing_service
            
        except Exception as e:
            logger.error(f"Error updating service {service_id}: {e}")
            raise
    
    async def delete(self, service_id: UUID) -> bool:
        """Delete service"""
        try:
            # Check if service exists
            existing_service = await self.get_by_id(service_id)
            if not existing_service:
                return False
            
            query = f"DELETE FROM {self.table_name} WHERE id = ?"
            self.session.execute(query, [service_id])
            
            logger.info(f"Service deleted with ID: {service_id}")
            return True
            
        except Exception as e:
            logger.error(f"Error deleting service {service_id}: {e}")
            raise
    
    async def search_by_name(self, name: str, limit: int = 50) -> List[Service]:
        """Search services by name (case-insensitive)"""
        try:
            # Get all services and filter by name (Cassandra limitation)
            all_services = await self.get_all(limit=1000)
            
            # Filter by name (case-insensitive)
            search_term = name.lower()
            matching_services = [
                service for service in all_services
                if search_term in service.name.lower()
            ]
            
            return matching_services[:limit]
            
        except Exception as e:
            logger.error(f"Error searching services by name {name}: {e}")
            raise
    
    async def get_categories(self) -> List[str]:
        """Get all unique service categories"""
        try:
            # Get all services and extract unique categories
            all_services = await self.get_all(limit=1000)
            categories = list(set(service.category for service in all_services))
            return sorted(categories)
            
        except Exception as e:
            logger.error(f"Error getting service categories: {e}")
            raise
    
    async def count(self) -> int:
        """Get total count of services"""
        try:
            query = f"SELECT COUNT(*) FROM {self.table_name}"
            result = self.session.execute(query)
            row = result.one()
            return row.count if row else 0
            
        except Exception as e:
            logger.error(f"Error counting services: {e}")
            raise
    
    async def count_active(self) -> int:
        """Get count of active services"""
        try:
            query = f"SELECT COUNT(*) FROM {self.table_name} WHERE is_active = ? ALLOW FILTERING"
            result = self.session.execute(query, [True])
            row = result.one()
            return row.count if row else 0
            
        except Exception as e:
            logger.error(f"Error counting active services: {e}")
            raise
