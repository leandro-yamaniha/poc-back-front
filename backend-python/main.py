"""
Beauty Salon Management System - Python FastAPI Backend
Main application entry point
"""

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from contextlib import asynccontextmanager
import uvicorn
import os
from dotenv import load_dotenv

from app.database.connection import DatabaseConnection
from app.routers import customers, services, staff, appointments, health

# Load environment variables
load_dotenv()

@asynccontextmanager
async def lifespan(app: FastAPI):
    """Application lifespan manager"""
    # Startup
    db_connection = DatabaseConnection()
    await db_connection.connect()
    app.state.db = db_connection
    
    yield
    
    # Shutdown
    await app.state.db.disconnect()

# Create FastAPI application
app = FastAPI(
    title="Beauty Salon Management API",
    description="Complete REST API for Beauty Salon Management System",
    version="1.0.0",
    docs_url="/api/docs",
    redoc_url="/api/redoc",
    lifespan=lifespan
)

# CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Configure appropriately for production
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Include routers
app.include_router(health.router, prefix="/api", tags=["Health"])
app.include_router(customers.router, prefix="/api/customers", tags=["Customers"])
app.include_router(services.router, prefix="/api/services", tags=["Services"])
app.include_router(staff.router, prefix="/api/staff", tags=["Staff"])
app.include_router(appointments.router, prefix="/api/appointments", tags=["Appointments"])

@app.get("/")
async def root():
    """Root endpoint"""
    return {
        "message": "Beauty Salon Management API",
        "version": "1.0.0",
        "docs": "/api/docs"
    }

if __name__ == "__main__":
    port = int(os.getenv("PORT", 8000))
    host = os.getenv("HOST", "0.0.0.0")
    
    uvicorn.run(
        "main:app",
        host=host,
        port=port,
        reload=True,
        log_level="info"
    )
