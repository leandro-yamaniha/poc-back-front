"""
Health check router for Beauty Salon Management System
"""

from fastapi import APIRouter, Request
from datetime import datetime
import os
import sys

router = APIRouter()


@router.get("/health")
async def health_check(request: Request):
    """Health check endpoint"""
    try:
        # Get database connection status
        db_status = "connected" if hasattr(request.app.state, 'db') else "disconnected"
        
        return {
            "status": "healthy",
            "timestamp": datetime.now().isoformat(),
            "version": "1.0.0",
            "database": {
                "status": db_status,
                "type": "cassandra"
            },
            "environment": {
                "python_version": sys.version,
                "platform": os.name
            }
        }
    except Exception as e:
        return {
            "status": "unhealthy",
            "timestamp": datetime.now().isoformat(),
            "error": str(e)
        }


@router.get("/health/ready")
async def readiness_check(request: Request):
    """Readiness check endpoint"""
    try:
        # Check if database is connected
        if not hasattr(request.app.state, 'db'):
            return {
                "status": "not_ready",
                "timestamp": datetime.now().isoformat(),
                "reason": "Database not connected"
            }
        
        return {
            "status": "ready",
            "timestamp": datetime.now().isoformat()
        }
    except Exception as e:
        return {
            "status": "not_ready",
            "timestamp": datetime.now().isoformat(),
            "error": str(e)
        }


@router.get("/health/live")
async def liveness_check():
    """Liveness check endpoint"""
    return {
        "status": "alive",
        "timestamp": datetime.now().isoformat()
    }
