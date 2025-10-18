"""
Appointment service for Beauty Salon Management System
"""

import logging
from typing import List, Optional
from uuid import UUID
from datetime import date, time
from fastapi import HTTPException, status

from app.models.appointment import Appointment, AppointmentCreate, AppointmentUpdate, AppointmentStatus, AppointmentStatusUpdate
from app.repositories.appointment_repository import AppointmentRepository
from app.services.customer_service import CustomerService
from app.services.staff_service import StaffService
from app.services.service_service import ServiceService


logger = logging.getLogger(__name__)


class AppointmentService:
    """Service for appointment business logic"""
    
    def __init__(
        self, 
        appointment_repository: AppointmentRepository,
        customer_service: CustomerService,
        staff_service: StaffService,
        service_service: ServiceService
    ):
        self.appointment_repository = appointment_repository
        self.customer_service = customer_service
        self.staff_service = staff_service
        self.service_service = service_service
    
    async def create_appointment(self, appointment_data: AppointmentCreate) -> Appointment:
        """Create a new appointment with business validation"""
        try:
            # Validate that customer exists
            if not await self.customer_service.customer_exists(appointment_data.customer_id):
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Customer not found"
                )
            
            # Validate that staff member exists and is active
            if not await self.staff_service.staff_is_active(appointment_data.staff_id):
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Staff member not found or inactive"
                )
            
            # Validate that service exists and is active
            if not await self.service_service.service_is_active(appointment_data.service_id):
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Service not found or inactive"
                )
            
            # Check for scheduling conflicts
            if await self.appointment_repository.check_conflict(
                appointment_data.staff_id,
                appointment_data.appointment_date,
                appointment_data.appointment_time
            ):
                raise HTTPException(
                    status_code=status.HTTP_409_CONFLICT,
                    detail="Staff member is not available at the requested time"
                )
            
            # Create appointment
            appointment = await self.appointment_repository.create(appointment_data)
            logger.info(f"Appointment created successfully: {appointment.id}")
            return appointment
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error creating appointment: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to create appointment"
            )
    
    async def get_appointment_by_id(self, appointment_id: UUID) -> Appointment:
        """Get appointment by ID"""
        try:
            appointment = await self.appointment_repository.get_by_id(appointment_id)
            if not appointment:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Appointment not found"
                )
            return appointment
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error getting appointment by ID {appointment_id}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get appointment"
            )
    
    async def get_all_appointments(self, limit: int = 100) -> List[Appointment]:
        """Get all appointments"""
        try:
            appointments = await self.appointment_repository.get_all(limit)
            return appointments
            
        except Exception as e:
            logger.error(f"Error getting all appointments: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get appointments"
            )
    
    async def get_appointments_by_customer(self, customer_id: UUID, limit: int = 100) -> List[Appointment]:
        """Get appointments by customer ID"""
        try:
            # Validate that customer exists
            if not await self.customer_service.customer_exists(customer_id):
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Customer not found"
                )
            
            appointments = await self.appointment_repository.get_by_customer(customer_id, limit)
            return appointments
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error getting appointments by customer {customer_id}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get appointments by customer"
            )
    
    async def get_appointments_by_staff(self, staff_id: UUID, limit: int = 100) -> List[Appointment]:
        """Get appointments by staff ID"""
        try:
            # Validate that staff member exists
            if not await self.staff_service.staff_exists(staff_id):
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Staff member not found"
                )
            
            appointments = await self.appointment_repository.get_by_staff(staff_id, limit)
            return appointments
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error getting appointments by staff {staff_id}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get appointments by staff"
            )
    
    async def get_appointments_by_service(self, service_id: UUID, limit: int = 100) -> List[Appointment]:
        """Get appointments by service ID"""
        try:
            # Validate that service exists
            if not await self.service_service.service_exists(service_id):
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Service not found"
                )
            
            appointments = await self.appointment_repository.get_by_service(service_id, limit)
            return appointments
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error getting appointments by service {service_id}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get appointments by service"
            )
    
    async def get_appointments_by_date(self, appointment_date: date, limit: int = 100) -> List[Appointment]:
        """Get appointments by date"""
        try:
            appointments = await self.appointment_repository.get_by_date(appointment_date, limit)
            return appointments
            
        except Exception as e:
            logger.error(f"Error getting appointments by date {appointment_date}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get appointments by date"
            )
    
    async def get_appointments_by_date_and_staff(self, appointment_date: date, staff_id: UUID, limit: int = 100) -> List[Appointment]:
        """Get appointments by date and staff"""
        try:
            # Validate that staff member exists
            if not await self.staff_service.staff_exists(staff_id):
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Staff member not found"
                )
            
            appointments = await self.appointment_repository.get_by_date_and_staff(appointment_date, staff_id, limit)
            return appointments
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error getting appointments by date {appointment_date} and staff {staff_id}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get appointments by date and staff"
            )
    
    async def get_appointments_by_status(self, status: AppointmentStatus, limit: int = 100) -> List[Appointment]:
        """Get appointments by status"""
        try:
            appointments = await self.appointment_repository.get_by_status(status, limit)
            return appointments
            
        except Exception as e:
            logger.error(f"Error getting appointments by status {status}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get appointments by status"
            )
    
    async def get_upcoming_appointments(self, limit: int = 100) -> List[Appointment]:
        """Get upcoming appointments"""
        try:
            appointments = await self.appointment_repository.get_upcoming(limit)
            return appointments
            
        except Exception as e:
            logger.error(f"Error getting upcoming appointments: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get upcoming appointments"
            )
    
    async def get_today_appointments(self, limit: int = 100) -> List[Appointment]:
        """Get today's appointments"""
        try:
            appointments = await self.appointment_repository.get_today(limit)
            return appointments
            
        except Exception as e:
            logger.error(f"Error getting today's appointments: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get today's appointments"
            )
    
    async def update_appointment(self, appointment_id: UUID, appointment_data: AppointmentUpdate) -> Appointment:
        """Update appointment with business validation"""
        try:
            # Check if appointment exists
            existing_appointment = await self.appointment_repository.get_by_id(appointment_id)
            if not existing_appointment:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Appointment not found"
                )
            
            # Validate updated entities if provided
            if appointment_data.customer_id and not await self.customer_service.customer_exists(appointment_data.customer_id):
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Customer not found"
                )
            
            if appointment_data.staff_id and not await self.staff_service.staff_is_active(appointment_data.staff_id):
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Staff member not found or inactive"
                )
            
            if appointment_data.service_id and not await self.service_service.service_is_active(appointment_data.service_id):
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Service not found or inactive"
                )
            
            # Check for scheduling conflicts if date, time, or staff is being updated
            if (appointment_data.staff_id or appointment_data.appointment_date or appointment_data.appointment_time):
                staff_id = appointment_data.staff_id or existing_appointment.staff_id
                appointment_date = appointment_data.appointment_date or existing_appointment.appointment_date
                appointment_time = appointment_data.appointment_time or existing_appointment.appointment_time
                
                if await self.appointment_repository.check_conflict(
                    staff_id, appointment_date, appointment_time, exclude_id=appointment_id
                ):
                    raise HTTPException(
                        status_code=status.HTTP_409_CONFLICT,
                        detail="Staff member is not available at the requested time"
                    )
            
            # Update appointment
            updated_appointment = await self.appointment_repository.update(appointment_id, appointment_data)
            if not updated_appointment:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Appointment not found"
                )
            
            logger.info(f"Appointment updated successfully: {appointment_id}")
            return updated_appointment
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error updating appointment {appointment_id}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to update appointment"
            )
    
    async def update_appointment_status(self, appointment_id: UUID, status_data: AppointmentStatusUpdate) -> Appointment:
        """Update appointment status"""
        try:
            # Check if appointment exists
            existing_appointment = await self.appointment_repository.get_by_id(appointment_id)
            if not existing_appointment:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Appointment not found"
                )
            
            # Update status
            updated_appointment = await self.appointment_repository.update_status(appointment_id, status_data.status)
            if not updated_appointment:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Appointment not found"
                )
            
            logger.info(f"Appointment status updated to {status_data.status} for ID: {appointment_id}")
            return updated_appointment
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error updating appointment status {appointment_id}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to update appointment status"
            )
    
    async def delete_appointment(self, appointment_id: UUID) -> bool:
        """Delete appointment"""
        try:
            # Check if appointment exists
            existing_appointment = await self.appointment_repository.get_by_id(appointment_id)
            if not existing_appointment:
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail="Appointment not found"
                )
            
            # Delete appointment
            deleted = await self.appointment_repository.delete(appointment_id)
            if deleted:
                logger.info(f"Appointment deleted successfully: {appointment_id}")
            
            return deleted
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error(f"Error deleting appointment {appointment_id}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to delete appointment"
            )
    
    async def get_appointment_count(self) -> int:
        """Get total count of appointments"""
        try:
            count = await self.appointment_repository.count()
            return count
            
        except Exception as e:
            logger.error(f"Error getting appointment count: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get appointment count"
            )
    
    async def get_appointment_count_by_status(self, status: AppointmentStatus) -> int:
        """Get count of appointments by status"""
        try:
            count = await self.appointment_repository.count_by_status(status)
            return count
            
        except Exception as e:
            logger.error(f"Error getting appointment count by status {status}: {e}")
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to get appointment count by status"
            )
