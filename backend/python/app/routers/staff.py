"""
Staff router for Beauty Salon Management System
"""

from typing import List
from uuid import UUID
from fastapi import APIRouter, Depends, Query, Request
from fastapi.responses import JSONResponse

from app.models.staff import StaffCreate, StaffUpdate, StaffResponse
from app.services.staff_service import StaffService
from app.repositories.staff_repository import StaffRepository


router = APIRouter()


def get_staff_service(request: Request) -> StaffService:
    """Dependency to get staff service"""
    session = request.app.state.db.get_session()
    staff_repository = StaffRepository(session)
    return StaffService(staff_repository)


@router.post("/", response_model=StaffResponse, status_code=201)
async def create_staff(
    staff_data: StaffCreate,
    staff_service: StaffService = Depends(get_staff_service)
):
    """Create a new staff member"""
    staff = await staff_service.create_staff(staff_data)
    return StaffResponse(**staff.dict())


@router.get("/", response_model=List[StaffResponse])
async def get_staff(
    limit: int = Query(100, ge=1, le=1000, description="Maximum number of staff members to return"),
    staff_service: StaffService = Depends(get_staff_service)
):
    """Get all staff members"""
    staff_members = await staff_service.get_all_staff(limit)
    return [StaffResponse(**staff.dict()) for staff in staff_members]


@router.get("/active", response_model=List[StaffResponse])
async def get_active_staff(
    limit: int = Query(100, ge=1, le=1000, description="Maximum number of staff members to return"),
    staff_service: StaffService = Depends(get_staff_service)
):
    """Get all active staff members"""
    staff_members = await staff_service.get_active_staff(limit)
    return [StaffResponse(**staff.dict()) for staff in staff_members]


@router.get("/{staff_id}", response_model=StaffResponse)
async def get_staff_member(
    staff_id: UUID,
    staff_service: StaffService = Depends(get_staff_service)
):
    """Get staff member by ID"""
    staff = await staff_service.get_staff_by_id(staff_id)
    return StaffResponse(**staff.dict())


@router.get("/email/{email}", response_model=StaffResponse)
async def get_staff_by_email(
    email: str,
    staff_service: StaffService = Depends(get_staff_service)
):
    """Get staff member by email"""
    staff = await staff_service.get_staff_by_email(email)
    return StaffResponse(**staff.dict())


@router.put("/{staff_id}", response_model=StaffResponse)
async def update_staff(
    staff_id: UUID,
    staff_data: StaffUpdate,
    staff_service: StaffService = Depends(get_staff_service)
):
    """Update staff member"""
    staff = await staff_service.update_staff(staff_id, staff_data)
    return StaffResponse(**staff.dict())


@router.delete("/{staff_id}")
async def delete_staff(
    staff_id: UUID,
    staff_service: StaffService = Depends(get_staff_service)
):
    """Delete staff member"""
    deleted = await staff_service.delete_staff(staff_id)
    if deleted:
        return JSONResponse(
            status_code=204,
            content={"message": "Staff member deleted successfully"}
        )
    return JSONResponse(
        status_code=404,
        content={"message": "Staff member not found"}
    )


@router.get("/role/{role}", response_model=List[StaffResponse])
async def get_staff_by_role(
    role: str,
    active_only: bool = Query(False, description="Return only active staff members"),
    limit: int = Query(100, ge=1, le=1000, description="Maximum number of staff members to return"),
    staff_service: StaffService = Depends(get_staff_service)
):
    """Get staff members by role"""
    staff_members = await staff_service.get_staff_by_role(role, active_only, limit)
    return [StaffResponse(**staff.dict()) for staff in staff_members]


@router.get("/role/{role}/active", response_model=List[StaffResponse])
async def get_active_staff_by_role(
    role: str,
    limit: int = Query(100, ge=1, le=1000, description="Maximum number of staff members to return"),
    staff_service: StaffService = Depends(get_staff_service)
):
    """Get active staff members by role"""
    staff_members = await staff_service.get_staff_by_role(role, active_only=True, limit=limit)
    return [StaffResponse(**staff.dict()) for staff in staff_members]


@router.get("/specialty/{specialty}", response_model=List[StaffResponse])
async def get_staff_by_specialty(
    specialty: str,
    limit: int = Query(100, ge=1, le=1000, description="Maximum number of staff members to return"),
    staff_service: StaffService = Depends(get_staff_service)
):
    """Get staff members by specialty"""
    staff_members = await staff_service.get_staff_by_specialty(specialty, limit)
    return [StaffResponse(**staff.dict()) for staff in staff_members]


@router.get("/search/name", response_model=List[StaffResponse])
async def search_staff(
    name: str = Query(..., min_length=1, description="Name to search for"),
    limit: int = Query(50, ge=1, le=500, description="Maximum number of results"),
    staff_service: StaffService = Depends(get_staff_service)
):
    """Search staff members by name"""
    staff_members = await staff_service.search_staff(name, limit)
    return [StaffResponse(**staff.dict()) for staff in staff_members]


@router.get("/roles/list")
async def get_staff_roles(
    staff_service: StaffService = Depends(get_staff_service)
):
    """Get all staff roles"""
    roles = await staff_service.get_staff_roles()
    return {"roles": roles}


@router.get("/count/total")
async def get_staff_count(
    staff_service: StaffService = Depends(get_staff_service)
):
    """Get total count of staff members"""
    count = await staff_service.get_staff_count()
    return {"count": count}


@router.get("/count/active")
async def get_active_staff_count(
    staff_service: StaffService = Depends(get_staff_service)
):
    """Get count of active staff members"""
    count = await staff_service.get_active_staff_count()
    return {"count": count}
