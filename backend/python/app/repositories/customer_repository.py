"""
Customer repository for Beauty Salon Management System
"""

import logging
from typing import List, Optional
from uuid import UUID
from datetime import datetime
from cassandra.cluster import Session

from app.models.customer import Customer, CustomerCreate, CustomerUpdate


logger = logging.getLogger(__name__)


class CustomerRepository:
    """Repository for customer data operations"""
    
    def __init__(self, session: Session):
        self.session = session
        self.table_name = "customers"
    
    async def create(self, customer_data: CustomerCreate) -> Customer:
        """Create a new customer"""
        try:
            customer = Customer(**customer_data.dict())
            
            query = f"""
            INSERT INTO {self.table_name} 
            (id, name, email, phone, address, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """
            
            self.session.execute(query, [
                customer.id,
                customer.name,
                customer.email,
                customer.phone,
                customer.address,
                customer.created_at,
                customer.updated_at
            ])
            
            logger.info(f"Customer created with ID: {customer.id}")
            return customer
            
        except Exception as e:
            logger.error(f"Error creating customer: {e}")
            raise
    
    async def get_by_id(self, customer_id: UUID) -> Optional[Customer]:
        """Get customer by ID"""
        try:
            query = f"SELECT * FROM {self.table_name} WHERE id = ?"
            result = self.session.execute(query, [customer_id])
            row = result.one()
            
            if row:
                return Customer(
                    id=row.id,
                    name=row.name,
                    email=row.email,
                    phone=row.phone,
                    address=row.address,
                    created_at=row.created_at,
                    updated_at=row.updated_at
                )
            return None
            
        except Exception as e:
            logger.error(f"Error getting customer by ID {customer_id}: {e}")
            raise
    
    async def get_by_email(self, email: str) -> Optional[Customer]:
        """Get customer by email"""
        try:
            query = f"SELECT * FROM {self.table_name} WHERE email = ? ALLOW FILTERING"
            result = self.session.execute(query, [email])
            row = result.one()
            
            if row:
                return Customer(
                    id=row.id,
                    name=row.name,
                    email=row.email,
                    phone=row.phone,
                    address=row.address,
                    created_at=row.created_at,
                    updated_at=row.updated_at
                )
            return None
            
        except Exception as e:
            logger.error(f"Error getting customer by email {email}: {e}")
            raise
    
    async def get_all(self, limit: int = 100) -> List[Customer]:
        """Get all customers with optional limit"""
        try:
            query = f"SELECT * FROM {self.table_name} LIMIT {limit}"
            result = self.session.execute(query)
            
            customers = []
            for row in result:
                customer = Customer(
                    id=row.id,
                    name=row.name,
                    email=row.email,
                    phone=row.phone,
                    address=row.address,
                    created_at=row.created_at,
                    updated_at=row.updated_at
                )
                customers.append(customer)
            
            return customers
            
        except Exception as e:
            logger.error(f"Error getting all customers: {e}")
            raise
    
    async def update(self, customer_id: UUID, customer_data: CustomerUpdate) -> Optional[Customer]:
        """Update customer"""
        try:
            # Get existing customer
            existing_customer = await self.get_by_id(customer_id)
            if not existing_customer:
                return None
            
            # Update fields that are provided
            update_data = customer_data.dict(exclude_unset=True)
            for field, value in update_data.items():
                setattr(existing_customer, field, value)
            
            existing_customer.update_timestamp()
            
            query = f"""
            UPDATE {self.table_name} 
            SET name = ?, email = ?, phone = ?, address = ?, updated_at = ?
            WHERE id = ?
            """
            
            self.session.execute(query, [
                existing_customer.name,
                existing_customer.email,
                existing_customer.phone,
                existing_customer.address,
                existing_customer.updated_at,
                customer_id
            ])
            
            logger.info(f"Customer updated with ID: {customer_id}")
            return existing_customer
            
        except Exception as e:
            logger.error(f"Error updating customer {customer_id}: {e}")
            raise
    
    async def delete(self, customer_id: UUID) -> bool:
        """Delete customer"""
        try:
            # Check if customer exists
            existing_customer = await self.get_by_id(customer_id)
            if not existing_customer:
                return False
            
            query = f"DELETE FROM {self.table_name} WHERE id = ?"
            self.session.execute(query, [customer_id])
            
            logger.info(f"Customer deleted with ID: {customer_id}")
            return True
            
        except Exception as e:
            logger.error(f"Error deleting customer {customer_id}: {e}")
            raise
    
    async def exists_by_email(self, email: str, exclude_id: Optional[UUID] = None) -> bool:
        """Check if customer exists by email"""
        try:
            customer = await self.get_by_email(email)
            if not customer:
                return False
            
            # If excluding a specific ID, check if it's different
            if exclude_id and customer.id == exclude_id:
                return False
            
            return True
            
        except Exception as e:
            logger.error(f"Error checking customer existence by email {email}: {e}")
            raise
    
    async def search_by_name(self, name: str, limit: int = 50) -> List[Customer]:
        """Search customers by name (case-insensitive)"""
        try:
            # Get all customers and filter by name (Cassandra limitation)
            all_customers = await self.get_all(limit=1000)
            
            # Filter by name (case-insensitive)
            search_term = name.lower()
            matching_customers = [
                customer for customer in all_customers
                if search_term in customer.name.lower()
            ]
            
            return matching_customers[:limit]
            
        except Exception as e:
            logger.error(f"Error searching customers by name {name}: {e}")
            raise
    
    async def count(self) -> int:
        """Get total count of customers"""
        try:
            query = f"SELECT COUNT(*) FROM {self.table_name}"
            result = self.session.execute(query)
            row = result.one()
            return row.count if row else 0
            
        except Exception as e:
            logger.error(f"Error counting customers: {e}")
            raise
