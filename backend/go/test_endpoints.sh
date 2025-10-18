#!/bin/bash

# Basic endpoint testing script for Beauty Salon Go Backend
# This script tests the main endpoints to ensure they are responding correctly

BASE_URL="http://localhost:8080/api/v1"

echo "🧪 Testing Beauty Salon Go Backend Endpoints..."
echo "Base URL: $BASE_URL"
echo

# Test health endpoint
echo "1. Testing Health Endpoint..."
curl -s -o /dev/null -w "Health: %{http_code}\n" "$BASE_URL/health"

# Test customers endpoints
echo "2. Testing Customer Endpoints..."
curl -s -o /dev/null -w "GET /customers: %{http_code}\n" "$BASE_URL/customers"
curl -s -o /dev/null -w "GET /customers/count: %{http_code}\n" "$BASE_URL/customers/count"

# Test services endpoints
echo "3. Testing Service Endpoints..."
curl -s -o /dev/null -w "GET /services: %{http_code}\n" "$BASE_URL/services"
curl -s -o /dev/null -w "GET /services/active: %{http_code}\n" "$BASE_URL/services/active"
curl -s -o /dev/null -w "GET /services/count: %{http_code}\n" "$BASE_URL/services/count"
curl -s -o /dev/null -w "GET /services/categories: %{http_code}\n" "$BASE_URL/services/categories"

# Test staff endpoints
echo "4. Testing Staff Endpoints..."
curl -s -o /dev/null -w "GET /staff: %{http_code}\n" "$BASE_URL/staff"
curl -s -o /dev/null -w "GET /staff/active: %{http_code}\n" "$BASE_URL/staff/active"
curl -s -o /dev/null -w "GET /staff/count: %{http_code}\n" "$BASE_URL/staff/count"
curl -s -o /dev/null -w "GET /staff/roles: %{http_code}\n" "$BASE_URL/staff/roles"

# Test appointments endpoints
echo "5. Testing Appointment Endpoints..."
curl -s -o /dev/null -w "GET /appointments: %{http_code}\n" "$BASE_URL/appointments"
curl -s -o /dev/null -w "GET /appointments/count: %{http_code}\n" "$BASE_URL/appointments/count"
curl -s -o /dev/null -w "GET /appointments/upcoming: %{http_code}\n" "$BASE_URL/appointments/upcoming"
curl -s -o /dev/null -w "GET /appointments/today: %{http_code}\n" "$BASE_URL/appointments/today"

echo
echo "✅ Endpoint testing completed!"
echo "Note: 200 = Success, 404 = Not Found (expected for empty DB), 500 = Server Error"
