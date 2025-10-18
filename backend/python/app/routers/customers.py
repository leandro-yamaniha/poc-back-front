"""
Customers router for Beauty Salon Management System
"""

from typing import List
from uuid import UUID
from fastapi import APIRouter, Depends, Query, Request
from fastapi.responses import JSONResponse

from app.models.customer import CustomerCreate, CustomerUpdate, CustomerResponse
from app.services.customer_service import CustomerService
from app.repositories.customer_repository import CustomerRepository


router = APIRouter()


def get_customer_service(request: Request) -> CustomerService:
    """Dependency to get customer service"""
    session = request.app.state.db.get_session()
    customer_repository = CustomerRepository(session)
    return CustomerService(customer_repository)


@router.post("/", response_model=CustomerResponse, status_code=201)
async def create_customer(
    customer_data: CustomerCreate,
    customer_service: CustomerService = Depends(get_customer_service)
):
    """Create a new customer"""
    customer = await customer_service.create_customer(customer_data)
    return CustomerResponse(**customer.dict())


@router.get("/", response_model=List[CustomerResponse])
async def get_customers(
    limit: int = Query(100, ge=1, le=1000, description="Maximum number of customers to return"),
    customer_service: CustomerService = Depends(get_customer_service)
):
    """Get all customers"""
    customers = await customer_service.get_all_customers(limit)
    return [CustomerResponse(**customer.dict()) for customer in customers]


@router.get("/{customer_id}", response_model=CustomerResponse)
async def get_customer(
    customer_id: UUID,
    customer_service: CustomerService = Depends(get_customer_service)
):
    """Get customer by ID"""
    customer = await customer_service.get_customer_by_id(customer_id)
    return CustomerResponse(**customer.dict())


@router.get("/email/{email}", response_model=CustomerResponse)
async def get_customer_by_email(
    email: str,
    customer_service: CustomerService = Depends(get_customer_service)
):
    """Get customer by email"""
    customer = await customer_service.get_customer_by_email(email)
    return CustomerResponse(**customer.dict())


@router.put("/{customer_id}", response_model=CustomerResponse)
async def update_customer(
    customer_id: UUID,
    customer_data: CustomerUpdate,
    customer_service: CustomerService = Depends(get_customer_service)
):
    """Update customer"""
    customer = await customer_service.update_customer(customer_id, customer_data)
    return CustomerResponse(**customer.dict())


@router.delete("/{customer_id}")
async def delete_customer(
    customer_id: UUID,
    customer_service: CustomerService = Depends(get_customer_service)
):
    """Delete customer"""
    deleted = await customer_service.delete_customer(customer_id)
    if deleted:
        return JSONResponse(
            status_code=204,
            content={"message": "Customer deleted successfully"}
        )
    return JSONResponse(
        status_code=404,
        content={"message": "Customer not found"}
    )


@router.get("/search/name", response_model=List[CustomerResponse])
async def search_customers(
    name: str = Query(..., min_length=1, description="Name to search for"),
    limit: int = Query(50, ge=1, le=500, description="Maximum number of results"),
    customer_service: CustomerService = Depends(get_customer_service)
):
    """Search customers by name"""
    customers = await customer_service.search_customers(name, limit)
    return [CustomerResponse(**customer.dict()) for customer in customers]


@router.get("/count/total")
async def get_customer_count(
    customer_service: CustomerService = Depends(get_customer_service)
):
    """Get total count of customers"""
    count = await customer_service.get_customer_count()
    return {"count": count}
