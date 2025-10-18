-- Create keyspace for beauty salon Go backend
CREATE KEYSPACE IF NOT EXISTS beauty_salon_go
WITH REPLICATION = {
    'class': 'SimpleStrategy',
    'replication_factor': 1
};

-- Use the keyspace
USE beauty_salon_go;

-- Create customers table
CREATE TABLE IF NOT EXISTS customers (
    id UUID PRIMARY KEY,
    name TEXT,
    email TEXT,
    phone TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create services table
CREATE TABLE IF NOT EXISTS services (
    id UUID PRIMARY KEY,
    name TEXT,
    description TEXT,
    price FLOAT,
    category TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create staff table
CREATE TABLE IF NOT EXISTS staff (
    id UUID PRIMARY KEY,
    name TEXT,
    email TEXT,
    phone TEXT,
    role TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create appointments table
CREATE TABLE IF NOT EXISTS appointments (
    id UUID PRIMARY KEY,
    customer_id UUID,
    service_id UUID,
    staff_id UUID,
    appointment_date DATE,
    appointment_time TIME,
    status TEXT,
    notes TEXT,
    total_price DOUBLE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_customers_email ON customers (email);
CREATE INDEX IF NOT EXISTS idx_staff_email ON staff (email);
CREATE INDEX IF NOT EXISTS idx_staff_role ON staff (role);
CREATE INDEX IF NOT EXISTS idx_services_category ON services (category);
CREATE INDEX IF NOT EXISTS idx_appointments_customer_id ON appointments (customer_id);
CREATE INDEX IF NOT EXISTS idx_appointments_staff_id ON appointments (staff_id);
CREATE INDEX IF NOT EXISTS idx_appointments_service_id ON appointments (service_id);
CREATE INDEX IF NOT EXISTS idx_appointments_date ON appointments (appointment_date);
CREATE INDEX IF NOT EXISTS idx_appointments_status ON appointments (status);
