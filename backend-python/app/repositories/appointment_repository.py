"""
Appointment repository for Beauty Salon Management System
"""

import logging
from typing import List, Optional
from uuid import UUID
from datetime import datetime, date, time
from cassandra.cluster import Session

from app.models.appointment import Appointment, AppointmentCreate, AppointmentUpdate, AppointmentStatus


logger = logging.getLogger(__name__)


class AppointmentRepository:
    """Repository for appointment data operations"""
    
    def __init__(self, session: Session):
        self.session = session
        self.table_name = "appointments"
    
    async def create(self, appointment_data: AppointmentCreate) -> Appointment:
        """Create a new appointment"""
        try:
            appointment = Appointment(**appointment_data.dict())
            
            query = f"""
            INSERT INTO {self.table_name} 
            (id, customer_id, staff_id, service_id, appointment_date, appointment_time, 
             status, notes, price, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """
            
            self.session.execute(query, [
                appointment.id,
                appointment.customer_id,
                appointment.staff_id,
                appointment.service_id,
                appointment.appointment_date,
                appointment.appointment_time,
                appointment.status.value,
                appointment.notes,
                appointment.price,
                appointment.created_at,
                appointment.updated_at
            ])
            
            logger.info(f"Appointment created with ID: {appointment.id}")
            return appointment
            
        except Exception as e:
            logger.error(f"Error creating appointment: {e}")
            raise
    
    async def get_by_id(self, appointment_id: UUID) -> Optional[Appointment]:
        """Get appointment by ID"""
        try:
            query = f"SELECT * FROM {self.table_name} WHERE id = ?"
            result = self.session.execute(query, [appointment_id])
            row = result.one()
            
            if row:
                return Appointment(
                    id=row.id,
                    customer_id=row.customer_id,
                    staff_id=row.staff_id,
                    service_id=row.service_id,
                    appointment_date=row.appointment_date,
                    appointment_time=row.appointment_time,
                    status=AppointmentStatus(row.status),
                    notes=row.notes,
                    price=row.price,
                    created_at=row.created_at,
                    updated_at=row.updated_at
                )
            return None
            
        except Exception as e:
            logger.error(f"Error getting appointment by ID {appointment_id}: {e}")
            raise
    
    async def get_all(self, limit: int = 100) -> List[Appointment]:
        """Get all appointments with optional limit"""
        try:
            query = f"SELECT * FROM {self.table_name} LIMIT ?"
            result = self.session.execute(query, [limit])
            
            appointments = []
            for row in result:
                appointment = Appointment(
                    id=row.id,
                    customer_id=row.customer_id,
                    staff_id=row.staff_id,
                    service_id=row.service_id,
                    appointment_date=row.appointment_date,
                    appointment_time=row.appointment_time,
                    status=AppointmentStatus(row.status),
                    notes=row.notes,
                    price=row.price,
                    created_at=row.created_at,
                    updated_at=row.updated_at
                )
                appointments.append(appointment)
            
            return appointments
            
        except Exception as e:
            logger.error(f"Error getting all appointments: {e}")
            raise
    
    async def get_by_customer(self, customer_id: UUID, limit: int = 100) -> List[Appointment]:
        """Get appointments by customer ID"""
        try:
            query = f"SELECT * FROM {self.table_name} WHERE customer_id = ? LIMIT ? ALLOW FILTERING"
            result = self.session.execute(query, [customer_id, limit])
            
            appointments = []
            for row in result:
                appointment = Appointment(
                    id=row.id,
                    customer_id=row.customer_id,
                    staff_id=row.staff_id,
                    service_id=row.service_id,
                    appointment_date=row.appointment_date,
                    appointment_time=row.appointment_time,
                    status=AppointmentStatus(row.status),
                    notes=row.notes,
                    price=row.price,
                    created_at=row.created_at,
                    updated_at=row.updated_at
                )
                appointments.append(appointment)
            
            return appointments
            
        except Exception as e:
            logger.error(f"Error getting appointments by customer {customer_id}: {e}")
            raise
    
    async def get_by_staff(self, staff_id: UUID, limit: int = 100) -> List[Appointment]:
        """Get appointments by staff ID"""
        try:
            query = f"SELECT * FROM {self.table_name} WHERE staff_id = ? LIMIT ? ALLOW FILTERING"
            result = self.session.execute(query, [staff_id, limit])
            
            appointments = []
            for row in result:
                appointment = Appointment(
                    id=row.id,
                    customer_id=row.customer_id,
                    staff_id=row.staff_id,
                    service_id=row.service_id,
                    appointment_date=row.appointment_date,
                    appointment_time=row.appointment_time,
                    status=AppointmentStatus(row.status),
                    notes=row.notes,
                    price=row.price,
                    created_at=row.created_at,
                    updated_at=row.updated_at
                )
                appointments.append(appointment)
            
            return appointments
            
        except Exception as e:
            logger.error(f"Error getting appointments by staff {staff_id}: {e}")
            raise
    
    async def get_by_service(self, service_id: UUID, limit: int = 100) -> List[Appointment]:
        """Get appointments by service ID"""
        try:
            query = f"SELECT * FROM {self.table_name} WHERE service_id = ? LIMIT ? ALLOW FILTERING"
            result = self.session.execute(query, [service_id, limit])
            
            appointments = []
            for row in result:
                appointment = Appointment(
                    id=row.id,
                    customer_id=row.customer_id,
                    staff_id=row.staff_id,
                    service_id=row.service_id,
                    appointment_date=row.appointment_date,
                    appointment_time=row.appointment_time,
                    status=AppointmentStatus(row.status),
                    notes=row.notes,
                    price=row.price,
                    created_at=row.created_at,
                    updated_at=row.updated_at
                )
                appointments.append(appointment)
            
            return appointments
            
        except Exception as e:
            logger.error(f"Error getting appointments by service {service_id}: {e}")
            raise
    
    async def get_by_date(self, appointment_date: date, limit: int = 100) -> List[Appointment]:
        """Get appointments by date"""
        try:
            query = f"SELECT * FROM {self.table_name} WHERE appointment_date = ? LIMIT ? ALLOW FILTERING"
            result = self.session.execute(query, [appointment_date, limit])
            
            appointments = []
            for row in result:
                appointment = Appointment(
                    id=row.id,
                    customer_id=row.customer_id,
                    staff_id=row.staff_id,
                    service_id=row.service_id,
                    appointment_date=row.appointment_date,
                    appointment_time=row.appointment_time,
                    status=AppointmentStatus(row.status),
                    notes=row.notes,
                    price=row.price,
                    created_at=row.created_at,
                    updated_at=row.updated_at
                )
                appointments.append(appointment)
            
            return appointments
            
        except Exception as e:
            logger.error(f"Error getting appointments by date {appointment_date}: {e}")
            raise
    
    async def get_by_date_and_staff(self, appointment_date: date, staff_id: UUID, limit: int = 100) -> List[Appointment]:
        """Get appointments by date and staff"""
        try:
            query = f"""
            SELECT * FROM {self.table_name} 
            WHERE appointment_date = ? AND staff_id = ? 
            LIMIT ? ALLOW FILTERING
            """
            result = self.session.execute(query, [appointment_date, staff_id, limit])
            
            appointments = []
            for row in result:
                appointment = Appointment(
                    id=row.id,
                    customer_id=row.customer_id,
                    staff_id=row.staff_id,
                    service_id=row.service_id,
                    appointment_date=row.appointment_date,
                    appointment_time=row.appointment_time,
                    status=AppointmentStatus(row.status),
                    notes=row.notes,
                    price=row.price,
                    created_at=row.created_at,
                    updated_at=row.updated_at
                )
                appointments.append(appointment)
            
            return appointments
            
        except Exception as e:
            logger.error(f"Error getting appointments by date {appointment_date} and staff {staff_id}: {e}")
            raise
    
    async def get_by_status(self, status: AppointmentStatus, limit: int = 100) -> List[Appointment]:
        """Get appointments by status"""
        try:
            query = f"SELECT * FROM {self.table_name} WHERE status = ? LIMIT ? ALLOW FILTERING"
            result = self.session.execute(query, [status.value, limit])
            
            appointments = []
            for row in result:
                appointment = Appointment(
                    id=row.id,
                    customer_id=row.customer_id,
                    staff_id=row.staff_id,
                    service_id=row.service_id,
                    appointment_date=row.appointment_date,
                    appointment_time=row.appointment_time,
                    status=AppointmentStatus(row.status),
                    notes=row.notes,
                    price=row.price,
                    created_at=row.created_at,
                    updated_at=row.updated_at
                )
                appointments.append(appointment)
            
            return appointments
            
        except Exception as e:
            logger.error(f"Error getting appointments by status {status}: {e}")
            raise
    
    async def get_upcoming(self, limit: int = 100) -> List[Appointment]:
        """Get upcoming appointments (today and future)"""
        try:
            today = date.today()
            
            # Get all appointments and filter (Cassandra limitation)
            all_appointments = await self.get_all(limit=1000)
            
            # Filter upcoming appointments
            upcoming = [
                appointment for appointment in all_appointments
                if appointment.appointment_date >= today and 
                appointment.status in [AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED]
            ]
            
            # Sort by date and time
            upcoming.sort(key=lambda x: (x.appointment_date, x.appointment_time))
            
            return upcoming[:limit]
            
        except Exception as e:
            logger.error(f"Error getting upcoming appointments: {e}")
            raise
    
    async def get_today(self, limit: int = 100) -> List[Appointment]:
        """Get today's appointments"""
        try:
            today = date.today()
            return await self.get_by_date(today, limit)
            
        except Exception as e:
            logger.error(f"Error getting today's appointments: {e}")
            raise
    
    async def update(self, appointment_id: UUID, appointment_data: AppointmentUpdate) -> Optional[Appointment]:
        """Update appointment"""
        try:
            # Get existing appointment
            existing_appointment = await self.get_by_id(appointment_id)
            if not existing_appointment:
                return None
            
            # Update fields that are provided
            update_data = appointment_data.dict(exclude_unset=True)
            for field, value in update_data.items():
                setattr(existing_appointment, field, value)
            
            existing_appointment.update_timestamp()
            
            query = f"""
            UPDATE {self.table_name} 
            SET customer_id = ?, staff_id = ?, service_id = ?, appointment_date = ?, 
                appointment_time = ?, status = ?, notes = ?, price = ?, updated_at = ?
            WHERE id = ?
            """
            
            self.session.execute(query, [
                existing_appointment.customer_id,
                existing_appointment.staff_id,
                existing_appointment.service_id,
                existing_appointment.appointment_date,
                existing_appointment.appointment_time,
                existing_appointment.status.value,
                existing_appointment.notes,
                existing_appointment.price,
                existing_appointment.updated_at,
                appointment_id
            ])
            
            logger.info(f"Appointment updated with ID: {appointment_id}")
            return existing_appointment
            
        except Exception as e:
            logger.error(f"Error updating appointment {appointment_id}: {e}")
            raise
    
    async def update_status(self, appointment_id: UUID, status: AppointmentStatus) -> Optional[Appointment]:
        """Update appointment status"""
        try:
            # Get existing appointment
            existing_appointment = await self.get_by_id(appointment_id)
            if not existing_appointment:
                return None
            
            existing_appointment.status = status
            existing_appointment.update_timestamp()
            
            query = f"UPDATE {self.table_name} SET status = ?, updated_at = ? WHERE id = ?"
            self.session.execute(query, [status.value, existing_appointment.updated_at, appointment_id])
            
            logger.info(f"Appointment status updated to {status} for ID: {appointment_id}")
            return existing_appointment
            
        except Exception as e:
            logger.error(f"Error updating appointment status {appointment_id}: {e}")
            raise
    
    async def delete(self, appointment_id: UUID) -> bool:
        """Delete appointment"""
        try:
            # Check if appointment exists
            existing_appointment = await self.get_by_id(appointment_id)
            if not existing_appointment:
                return False
            
            query = f"DELETE FROM {self.table_name} WHERE id = ?"
            self.session.execute(query, [appointment_id])
            
            logger.info(f"Appointment deleted with ID: {appointment_id}")
            return True
            
        except Exception as e:
            logger.error(f"Error deleting appointment {appointment_id}: {e}")
            raise
    
    async def count(self) -> int:
        """Get total count of appointments"""
        try:
            query = f"SELECT COUNT(*) FROM {self.table_name}"
            result = self.session.execute(query)
            row = result.one()
            return row.count if row else 0
            
        except Exception as e:
            logger.error(f"Error counting appointments: {e}")
            raise
    
    async def count_by_status(self, status: AppointmentStatus) -> int:
        """Get count of appointments by status"""
        try:
            query = f"SELECT COUNT(*) FROM {self.table_name} WHERE status = ? ALLOW FILTERING"
            result = self.session.execute(query, [status.value])
            row = result.one()
            return row.count if row else 0
            
        except Exception as e:
            logger.error(f"Error counting appointments by status {status}: {e}")
            raise
    
    async def check_conflict(self, staff_id: UUID, appointment_date: date, appointment_time: time, exclude_id: Optional[UUID] = None) -> bool:
        """Check if there's a scheduling conflict"""
        try:
            # Get appointments for the staff on the specific date
            appointments = await self.get_by_date_and_staff(appointment_date, staff_id)
            
            # Check for time conflicts
            for appointment in appointments:
                if exclude_id and appointment.id == exclude_id:
                    continue
                
                # Check if appointment is not cancelled or completed
                if appointment.status not in [AppointmentStatus.CANCELLED, AppointmentStatus.COMPLETED]:
                    if appointment.appointment_time == appointment_time:
                        return True
            
            return False
            
        except Exception as e:
            logger.error(f"Error checking appointment conflict: {e}")
            raise
