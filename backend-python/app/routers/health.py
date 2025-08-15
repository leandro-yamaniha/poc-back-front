"""
Health check router for Beauty Salon Management System
"""

from fastapi import APIRouter, Request
from datetime import datetime
import psutil
import os

router = APIRouter()


@router.get("/health")
async def health_check(request: Request):
    """Health check endpoint"""
    try:
        # Get database connection status
        db_status = "connected" if hasattr(request.app.state, 'db') else "disconnected"
        
        # Get system metrics
        cpu_percent = psutil.cpu_percent(interval=1)
        memory = psutil.virtual_memory()
        disk = psutil.disk_usage('/')
        
        return {
            "status": "healthy",
            "timestamp": datetime.now().isoformat(),
            "version": "1.0.0",
            "database": {
                "status": db_status,
                "type": "cassandra"
            },
            "system": {
                "cpu_percent": cpu_percent,
                "memory": {
                    "total": memory.total,
                    "available": memory.available,
                    "percent": memory.percent
                },
                "disk": {
                    "total": disk.total,
                    "free": disk.free,
                    "percent": (disk.used / disk.total) * 100
                }
            },
            "environment": {
                "python_version": os.sys.version,
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
