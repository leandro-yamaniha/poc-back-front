"""
Customer service for Beauty Salon Management System
"""

import logging
from typing import List, Optional
from uuid import UUID
from fastapi import HTTPException, status

from app.models.customer import Customer, CustomerCreate, CustomerUpdate
from app.repositories.customer_repository import CustomerRepository


logger = logging.getLogger(__name__)


class CustomerService:
    """Service for customer business logic"""
    
    def __init__(self, customer_repository: CustomerRepository):
        self.customer_repository = customer_repository
    
    async def create_customer(self, customer_data: CustomerCreate) -> Customer:
        """Create a new customer with business validation"""
        try:
            # Check if email already exists
            if await self.customer_repository.exists_by_email(customer_data.email):
                raise HTTPException(
                    status_code=status.HTTP_409_CONFLICT,
                    detail="Customer with this email already exists"
                )
            
            # Create customer
            customer = await self.customer_repository.create(customer_data)
            logger.info(f"Customer created successfully: {customer.id}")
            return customer
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error creating customer: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to create customer"
            )
    
    async def get_customer_by_id(self, customer_id: UUID) -> Customer:
        """Get customer by ID"""
        try:
            customer = await self.customer_repository.get_by_id(customer_id)
            if not customer:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Customer not found"
                )
            return customer
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error getting customer by ID {customer_id}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get customer"
            )
    
    async def get_customer_by_email(self, email: str) -> Customer:
        """Get customer by email"""
        try:
            customer = await self.customer_repository.get_by_email(email)
            if not customer:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Customer not found"
                )
            return customer
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error getting customer by email {email}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get customer"
            )
    
    async def get_all_customers(self, limit: int = 100) -> List[Customer]:
        """Get all customers"""
        try:
            customers = await self.customer_repository.get_all(limit)
            return customers
            
        except Exception as e:
            logger.error(f"Error getting all customers: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get customers"
            )
    
    async def update_customer(self, customer_id: UUID, customer_data: CustomerUpdate) -> Customer:
        """Update customer with business validation"""
        try:
            # Check if customer exists
            existing_customer = await self.customer_repository.get_by_id(customer_id)
            if not existing_customer:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Customer not found"
                )
            
            # If email is being updated, check if it's already taken by another customer
            if customer_data.email and customer_data.email != existing_customer.email:
                if await self.customer_repository.exists_by_email(customer_data.email, exclude_id=customer_id):
                    raise HTTPException(
                        status_code=status.HTTP_409_CONFLICT,
                        detail="Email already exists for another customer"
                    )
            
            # Update customer
            updated_customer = await self.customer_repository.update(customer_id, customer_data)
            if not updated_customer:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Customer not found"
                )
            
            logger.info(f"Customer updated successfully: {customer_id}")
            return updated_customer
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error updating customer {customer_id}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to update customer"
            )
    
    async def delete_customer(self, customer_id: UUID) -> bool:
        """Delete customer"""
        try:
            # Check if customer exists
            existing_customer = await self.customer_repository.get_by_id(customer_id)
            if not existing_customer:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Customer not found"
                )
            
            # TODO: Check if customer has active appointments before deletion
            # This would require appointment repository dependency
            
            # Delete customer
            deleted = await self.customer_repository.delete(customer_id)
            if deleted:
                logger.info(f"Customer deleted successfully: {customer_id}")
            
            return deleted
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error deleting customer {customer_id}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to delete customer"
            )
    
    async def search_customers(self, name: str, limit: int = 50) -> List[Customer]:
        """Search customers by name"""
        try:
            if not name or not name.strip():
                raise HTTPException(
                    status_code=status.HTTP_400_BAD_REQUEST,
                    detail="Search term cannot be empty"
                )
            
            customers = await self.customer_repository.search_by_name(name.strip(), limit)
            return customers
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error searching customers by name {name}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to search customers"
            )
    
    async def get_customer_count(self) -> int:
        """Get total count of customers"""
        try:
            count = await self.customer_repository.count()
            return count
            
        except Exception as e:
            logger.error(f"Error getting customer count: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get customer count"
            )
    
    async def customer_exists(self, customer_id: UUID) -> bool:
        """Check if customer exists"""
        try:
            customer = await self.customer_repository.get_by_id(customer_id)
            return customer is not None
            
        except Exception as e:
            logger.error(f"Error checking customer existence {customer_id}: {e}")
            return False
