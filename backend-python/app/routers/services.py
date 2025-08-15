"""
Services router for Beauty Salon Management System
"""

from typing import List
from uuid import UUID
from fastapi import APIRouter, Depends, Query, Request
from fastapi.responses import JSONResponse

from app.models.service import ServiceCreate, ServiceUpdate, ServiceResponse
from app.services.service_service import ServiceService
from app.repositories.service_repository import ServiceRepository


router = APIRouter()


def get_service_service(request: Request) -> ServiceService:
    """Dependency to get service service"""
    session = request.app.state.db.get_session()
    service_repository = ServiceRepository(session)
    return ServiceService(service_repository)


@router.post("/", response_model=ServiceResponse, status_code=201)
async def create_service(
    service_data: ServiceCreate,
    service_service: ServiceService = Depends(get_service_service)
):
    """Create a new service"""
    service = await service_service.create_service(service_data)
    return ServiceResponse(**service.dict())


@router.get("/", response_model=List[ServiceResponse])
async def get_services(
    limit: int = Query(100, ge=1, le=1000, description="Maximum number of services to return"),
    service_service: ServiceService = Depends(get_service_service)
):
    """Get all services"""
    services = await service_service.get_all_services(limit)
    return [ServiceResponse(**service.dict()) for service in services]


@router.get("/active", response_model=List[ServiceResponse])
async def get_active_services(
    limit: int = Query(100, ge=1, le=1000, description="Maximum number of services to return"),
    service_service: ServiceService = Depends(get_service_service)
):
    """Get all active services"""
    services = await service_service.get_active_services(limit)
    return [ServiceResponse(**service.dict()) for service in services]


@router.get("/{service_id}", response_model=ServiceResponse)
async def get_service(
    service_id: UUID,
    service_service: ServiceService = Depends(get_service_service)
):
    """Get service by ID"""
    service = await service_service.get_service_by_id(service_id)
    return ServiceResponse(**service.dict())


@router.put("/{service_id}", response_model=ServiceResponse)
async def update_service(
    service_id: UUID,
    service_data: ServiceUpdate,
    service_service: ServiceService = Depends(get_service_service)
):
    """Update service"""
    service = await service_service.update_service(service_id, service_data)
    return ServiceResponse(**service.dict())


@router.delete("/{service_id}")
async def delete_service(
    service_id: UUID,
    service_service: ServiceService = Depends(get_service_service)
):
    """Delete service"""
    deleted = await service_service.delete_service(service_id)
    if deleted:
        return JSONResponse(
            status_code=204,
            content={"message": "Service deleted successfully"}
        )
    return JSONResponse(
        status_code=404,
        content={"message": "Service not found"}
    )


@router.get("/category/{category}", response_model=List[ServiceResponse])
async def get_services_by_category(
    category: str,
    active_only: bool = Query(False, description="Return only active services"),
    limit: int = Query(100, ge=1, le=1000, description="Maximum number of services to return"),
    service_service: ServiceService = Depends(get_service_service)
):
    """Get services by category"""
    services = await service_service.get_services_by_category(category, active_only, limit)
    return [ServiceResponse(**service.dict()) for service in services]


@router.get("/category/{category}/active", response_model=List[ServiceResponse])
async def get_active_services_by_category(
    category: str,
    limit: int = Query(100, ge=1, le=1000, description="Maximum number of services to return"),
    service_service: ServiceService = Depends(get_service_service)
):
    """Get active services by category"""
    services = await service_service.get_services_by_category(category, active_only=True, limit=limit)
    return [ServiceResponse(**service.dict()) for service in services]


@router.get("/search/name", response_model=List[ServiceResponse])
async def search_services(
    name: str = Query(..., min_length=1, description="Name to search for"),
    limit: int = Query(50, ge=1, le=500, description="Maximum number of results"),
    service_service: ServiceService = Depends(get_service_service)
):
    """Search services by name"""
    services = await service_service.search_services(name, limit)
    return [ServiceResponse(**service.dict()) for service in services]


@router.get("/categories/list")
async def get_service_categories(
    service_service: ServiceService = Depends(get_service_service)
):
    """Get all service categories"""
    categories = await service_service.get_service_categories()
    return {"categories": categories}


@router.get("/count/total")
async def get_service_count(
    service_service: ServiceService = Depends(get_service_service)
):
    """Get total count of services"""
    count = await service_service.get_service_count()
    return {"count": count}


@router.get("/count/active")
async def get_active_service_count(
    service_service: ServiceService = Depends(get_service_service)
):
    """Get count of active services"""
    count = await service_service.get_active_service_count()
    return {"count": count}
