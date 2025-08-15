"""
Service service for Beauty Salon Management System
"""

import logging
from typing import List, Optional
from uuid import UUID
from fastapi import HTTPException, status

from app.models.service import Service, ServiceCreate, ServiceUpdate
from app.repositories.service_repository import ServiceRepository


logger = logging.getLogger(__name__)


class ServiceService:
    """Service for service business logic"""
    
    def __init__(self, service_repository: ServiceRepository):
        self.service_repository = service_repository
    
    async def create_service(self, service_data: ServiceCreate) -> Service:
        """Create a new service with business validation"""
        try:
            # Validate business rules
            if service_data.price <= 0:
                raise HTTPException(
                    status_code=status.HTTP_400_BAD_REQUEST,
                    detail="Service price must be greater than zero"
                )
            
            if service_data.duration <= 0:
                raise HTTPException(
                    status_code=status.HTTP_400_BAD_REQUEST,
                    detail="Service duration must be greater than zero"
                )
            
            # Create service
            service = await self.service_repository.create(service_data)
            logger.info(f"Service created successfully: {service.id}")
            return service
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error creating service: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to create service"
            )
    
    async def get_service_by_id(self, service_id: UUID) -> Service:
        """Get service by ID"""
        try:
            service = await self.service_repository.get_by_id(service_id)
            if not service:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Service not found"
                )
            return service
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error getting service by ID {service_id}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get service"
            )
    
    async def get_all_services(self, limit: int = 100) -> List[Service]:
        """Get all services"""
        try:
            services = await self.service_repository.get_all(limit)
            return services
            
        except Exception as e:
            logger.error(f"Error getting all services: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get services"
            )
    
    async def get_active_services(self, limit: int = 100) -> List[Service]:
        """Get all active services"""
        try:
            services = await self.service_repository.get_active(limit)
            return services
            
        except Exception as e:
            logger.error(f"Error getting active services: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get active services"
            )
    
    async def get_services_by_category(self, category: str, active_only: bool = False, limit: int = 100) -> List[Service]:
        """Get services by category"""
        try:
            if not category or not category.strip():
                raise HTTPException(
                    status_code=status.HTTP_400_BAD_REQUEST,
                    detail="Category cannot be empty"
                )
            
            services = await self.service_repository.get_by_category(category.strip(), active_only, limit)
            return services
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error getting services by category {category}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get services by category"
            )
    
    async def update_service(self, service_id: UUID, service_data: ServiceUpdate) -> Service:
        """Update service with business validation"""
        try:
            # Check if service exists
            existing_service = await self.service_repository.get_by_id(service_id)
            if not existing_service:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Service not found"
                )
            
            # Validate business rules for updated fields
            if service_data.price is not None and service_data.price <= 0:
                raise HTTPException(
                    status_code=status.HTTP_400_BAD_REQUEST,
                    detail="Service price must be greater than zero"
                )
            
            if service_data.duration is not None and service_data.duration <= 0:
                raise HTTPException(
                    status_code=status.HTTP_400_BAD_REQUEST,
                    detail="Service duration must be greater than zero"
                )
            
            # Update service
            updated_service = await self.service_repository.update(service_id, service_data)
            if not updated_service:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Service not found"
                )
            
            logger.info(f"Service updated successfully: {service_id}")
            return updated_service
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error updating service {service_id}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to update service"
            )
    
    async def delete_service(self, service_id: UUID) -> bool:
        """Delete service"""
        try:
            # Check if service exists
            existing_service = await self.service_repository.get_by_id(service_id)
            if not existing_service:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Service not found"
                )
            
            # TODO: Check if service has active appointments before deletion
            # This would require appointment repository dependency
            
            # Delete service
            deleted = await self.service_repository.delete(service_id)
            if deleted:
                logger.info(f"Service deleted successfully: {service_id}")
            
            return deleted
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error deleting service {service_id}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to delete service"
            )
    
    async def search_services(self, name: str, limit: int = 50) -> List[Service]:
        """Search services by name"""
        try:
            if not name or not name.strip():
                raise HTTPException(
                    status_code=status.HTTP_400_BAD_REQUEST,
                    detail="Search term cannot be empty"
                )
            
            services = await self.service_repository.search_by_name(name.strip(), limit)
            return services
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error searching services by name {name}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to search services"
            )
    
    async def get_service_categories(self) -> List[str]:
        """Get all service categories"""
        try:
            categories = await self.service_repository.get_categories()
            return categories
            
        except Exception as e:
            logger.error(f"Error getting service categories: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get service categories"
            )
    
    async def get_service_count(self) -> int:
        """Get total count of services"""
        try:
            count = await self.service_repository.count()
            return count
            
        except Exception as e:
            logger.error(f"Error getting service count: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get service count"
            )
    
    async def get_active_service_count(self) -> int:
        """Get count of active services"""
        try:
            count = await self.service_repository.count_active()
            return count
            
        except Exception as e:
            logger.error(f"Error getting active service count: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get active service count"
            )
    
    async def service_exists(self, service_id: UUID) -> bool:
        """Check if service exists"""
        try:
            service = await self.service_repository.get_by_id(service_id)
            return service is not None
            
        except Exception as e:
            logger.error(f"Error checking service existence {service_id}: {e}")
            return False
    
    async def service_is_active(self, service_id: UUID) -> bool:
        """Check if service exists and is active"""
        try:
            service = await self.service_repository.get_by_id(service_id)
            return service is not None and service.is_active
            
        except Exception as e:
            logger.error(f"Error checking service active status {service_id}: {e}")
            return False
