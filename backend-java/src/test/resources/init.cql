CREATE KEYSPACE IF NOT EXISTS beauty_salon_test
  WITH REPLICATION = { 
   'class' : 'SimpleStrategy', 
   'replication_factor' : 1 
  };

USE beauty_salon_test;

CREATE TABLE IF NOT EXISTS customers (
    id UUID PRIMARY KEY,
    name TEXT,
    email TEXT,
    phone TEXT,
    address TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Performance optimized indices for Phase 2
CREATE INDEX IF NOT EXISTS customers_email_idx ON customers (email);
CREATE INDEX IF NOT EXISTS customers_name_idx ON customers (name);
CREATE INDEX IF NOT EXISTS customers_phone_idx ON customers (phone);
CREATE INDEX IF NOT EXISTS customers_created_at_idx ON customers (created_at);

CREATE TABLE IF NOT EXISTS services (
    id UUID PRIMARY KEY,
    name TEXT,
    description TEXT,
    price DECIMAL,
    duration INT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS staff (
    id UUID PRIMARY KEY,
    name TEXT,
    email TEXT,
    phone TEXT,
    specialization TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS appointments (
    id UUID PRIMARY KEY,
    customer_id UUID,
    service_id UUID,
    staff_id UUID,
    appointment_time TIMESTAMP,
    status TEXT,
    notes TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
