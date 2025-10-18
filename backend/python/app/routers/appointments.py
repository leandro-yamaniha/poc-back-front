"""
Appointments router for Beauty Salon Management System
"""

from typing import List
from uuid import UUID
from datetime import date
from fastapi import APIRouter, Depends, Query, Request
from fastapi.responses import JSONResponse

from app.models.appointment import (
    AppointmentCreate, AppointmentUpdate, AppointmentResponse, 
    AppointmentStatus, AppointmentStatusUpdate
)
from app.services.appointment_service import AppointmentService
from app.services.customer_service import CustomerService
from app.services.staff_service import StaffService
from app.services.service_service import ServiceService
from app.repositories.appointment_repository import AppointmentRepository
from app.repositories.customer_repository import CustomerRepository
from app.repositories.staff_repository import StaffRepository
from app.repositories.service_repository import ServiceRepository


router = APIRouter()


def get_appointment_service(request: Request) -> AppointmentService:
    """Dependency to get appointment service"""
    session = request.app.state.db.get_session()
    
    # Create repositories
    appointment_repository = AppointmentRepository(session)
    customer_repository = CustomerRepository(session)
    staff_repository = StaffRepository(session)
    service_repository = ServiceRepository(session)
    
    # Create services
    customer_service = CustomerService(customer_repository)
    staff_service = StaffService(staff_repository)
    service_service = ServiceService(service_repository)
    
    return AppointmentService(
        appointment_repository,
        customer_service,
        staff_service,
        service_service
    )


@router.post("/", response_model=AppointmentResponse, status_code=201)
async def create_appointment(
    appointment_data: AppointmentCreate,
    appointment_service: AppointmentService = Depends(get_appointment_service)
):
    """Create a new appointment"""
    appointment = await appointment_service.create_appointment(appointment_data)
    return AppointmentResponse(**appointment.dict())


@router.get("/", response_model=List[AppointmentResponse])
async def get_appointments(
    limit: int = Query(100, ge=1, le=1000, description="Maximum number of appointments to return"),
    appointment_service: AppointmentService = Depends(get_appointment_service)
):
    """Get all appointments"""
    appointments = await appointment_service.get_all_appointments(limit)
    return [AppointmentResponse(**appointment.dict()) for appointment in appointments]


@router.get("/{appointment_id}", response_model=AppointmentResponse)
async def get_appointment(
    appointment_id: UUID,
    appointment_service: AppointmentService = Depends(get_appointment_service)
):
    """Get appointment by ID"""
    appointment = await appointment_service.get_appointment_by_id(appointment_id)
    return AppointmentResponse(**appointment.dict())


@router.put("/{appointment_id}", response_model=AppointmentResponse)
async def update_appointment(
    appointment_id: UUID,
    appointment_data: AppointmentUpdate,
    appointment_service: AppointmentService = Depends(get_appointment_service)
):
    """Update appointment"""
    appointment = await appointment_service.update_appointment(appointment_id, appointment_data)
    return AppointmentResponse(**appointment.dict())


@router.patch("/{appointment_id}/status", response_model=AppointmentResponse)
async def update_appointment_status(
    appointment_id: UUID,
    status_data: AppointmentStatusUpdate,
    appointment_service: AppointmentService = Depends(get_appointment_service)
):
    """Update appointment status"""
    appointment = await appointment_service.update_appointment_status(appointment_id, status_data)
    return AppointmentResponse(**appointment.dict())


@router.delete("/{appointment_id}")
async def delete_appointment(
    appointment_id: UUID,
    appointment_service: AppointmentService = Depends(get_appointment_service)
):
    """Delete appointment"""
    deleted = await appointment_service.delete_appointment(appointment_id)
    if deleted:
        return JSONResponse(
            status_code=204,
            content={"message": "Appointment deleted successfully"}
        )
    return JSONResponse(
        status_code=404,
        content={"message": "Appointment not found"}
    )


@router.get("/customer/{customer_id}", response_model=List[AppointmentResponse])
async def get_appointments_by_customer(
    customer_id: UUID,
    limit: int = Query(100, ge=1, le=1000, description="Maximum number of appointments to return"),
    appointment_service: AppointmentService = Depends(get_appointment_service)
):
    """Get appointments by customer ID"""
    appointments = await appointment_service.get_appointments_by_customer(customer_id, limit)
    return [AppointmentResponse(**appointment.dict()) for appointment in appointments]


@router.get("/staff/{staff_id}", response_model=List[AppointmentResponse])
async def get_appointments_by_staff(
    staff_id: UUID,
    limit: int = Query(100, ge=1, le=1000, description="Maximum number of appointments to return"),
    appointment_service: AppointmentService = Depends(get_appointment_service)
):
    """Get appointments by staff ID"""
    appointments = await appointment_service.get_appointments_by_staff(staff_id, limit)
    return [AppointmentResponse(**appointment.dict()) for appointment in appointments]


@router.get("/service/{service_id}", response_model=List[AppointmentResponse])
async def get_appointments_by_service(
    service_id: UUID,
    limit: int = Query(100, ge=1, le=1000, description="Maximum number of appointments to return"),
    appointment_service: AppointmentService = Depends(get_appointment_service)
):
    """Get appointments by service ID"""
    appointments = await appointment_service.get_appointments_by_service(service_id, limit)
    return [AppointmentResponse(**appointment.dict()) for appointment in appointments]


@router.get("/date/{appointment_date}", response_model=List[AppointmentResponse])
async def get_appointments_by_date(
    appointment_date: date,
    limit: int = Query(100, ge=1, le=1000, description="Maximum number of appointments to return"),
    appointment_service: AppointmentService = Depends(get_appointment_service)
):
    """Get appointments by date"""
    appointments = await appointment_service.get_appointments_by_date(appointment_date, limit)
    return [AppointmentResponse(**appointment.dict()) for appointment in appointments]


@router.get("/date/{appointment_date}/staff/{staff_id}", response_model=List[AppointmentResponse])
async def get_appointments_by_date_and_staff(
    appointment_date: date,
    staff_id: UUID,
    limit: int = Query(100, ge=1, le=1000, description="Maximum number of appointments to return"),
    appointment_service: AppointmentService = Depends(get_appointment_service)
):
    """Get appointments by date and staff"""
    appointments = await appointment_service.get_appointments_by_date_and_staff(
        appointment_date, staff_id, limit
    )
    return [AppointmentResponse(**appointment.dict()) for appointment in appointments]


@router.get("/status/{status}", response_model=List[AppointmentResponse])
async def get_appointments_by_status(
    status: AppointmentStatus,
    limit: int = Query(100, ge=1, le=1000, description="Maximum number of appointments to return"),
    appointment_service: AppointmentService = Depends(get_appointment_service)
):
    """Get appointments by status"""
    appointments = await appointment_service.get_appointments_by_status(status, limit)
    return [AppointmentResponse(**appointment.dict()) for appointment in appointments]


@router.get("/upcoming/list", response_model=List[AppointmentResponse])
async def get_upcoming_appointments(
    limit: int = Query(100, ge=1, le=1000, description="Maximum number of appointments to return"),
    appointment_service: AppointmentService = Depends(get_appointment_service)
):
    """Get upcoming appointments"""
    appointments = await appointment_service.get_upcoming_appointments(limit)
    return [AppointmentResponse(**appointment.dict()) for appointment in appointments]


@router.get("/today/list", response_model=List[AppointmentResponse])
async def get_today_appointments(
    limit: int = Query(100, ge=1, le=1000, description="Maximum number of appointments to return"),
    appointment_service: AppointmentService = Depends(get_appointment_service)
):
    """Get today's appointments"""
    appointments = await appointment_service.get_today_appointments(limit)
    return [AppointmentResponse(**appointment.dict()) for appointment in appointments]


@router.get("/count/total")
async def get_appointment_count(
    appointment_service: AppointmentService = Depends(get_appointment_service)
):
    """Get total count of appointments"""
    count = await appointment_service.get_appointment_count()
    return {"count": count}


@router.get("/count/status/{status}")
async def get_appointment_count_by_status(
    status: AppointmentStatus,
    appointment_service: AppointmentService = Depends(get_appointment_service)
):
    """Get count of appointments by status"""
    count = await appointment_service.get_appointment_count_by_status(status)
    return {"count": count}
