-- Create tables for beauty salon application

-- Customers table
CREATE TABLE IF NOT EXISTS customers (
    id UUID PRIMARY KEY,
    name TEXT,
    email TEXT,
    phone TEXT,
    address TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create index for customer email lookup
CREATE INDEX IF NOT EXISTS ON customers (email);

-- Staff table
CREATE TABLE IF NOT EXISTS staff (
    id UUID PRIMARY KEY,
    name TEXT,
    email TEXT,
    phone TEXT,
    role TEXT,
    specialties SET<TEXT>,
    is_active BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Services table
CREATE TABLE IF NOT EXISTS services (
    id UUID PRIMARY KEY,
    name TEXT,
    description TEXT,
    duration INT, -- in minutes
    price DECIMAL,
    category TEXT,
    is_active BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Appointments table
CREATE TABLE IF NOT EXISTS appointments (
    id UUID PRIMARY KEY,
    customer_id UUID,
    staff_id UUID,
    service_id UUID,
    appointment_date DATE,
    appointment_time TIME,
    status TEXT, -- scheduled, confirmed, completed, cancelled
    notes TEXT,
    total_price DECIMAL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create index for appointments by date
CREATE INDEX IF NOT EXISTS ON appointments (appointment_date);
CREATE INDEX IF NOT EXISTS ON appointments (customer_id);
CREATE INDEX IF NOT EXISTS ON appointments (staff_id);

-- Payments table
CREATE TABLE IF NOT EXISTS payments (
    id UUID PRIMARY KEY,
    appointment_id UUID,
    amount DECIMAL,
    payment_method TEXT, -- cash, card, pix
    payment_status TEXT, -- pending, completed, refunded
    payment_date TIMESTAMP,
    created_at TIMESTAMP
);

-- Create index for payments by appointment
CREATE INDEX IF NOT EXISTS ON payments (appointment_id);
