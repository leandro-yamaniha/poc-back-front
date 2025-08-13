CREATE TABLE customers (
    id UUID PRIMARY KEY,
    name TEXT,
    email TEXT,
    phone TEXT,
    address TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX customers_email_idx ON customers (email);
CREATE INDEX customers_name_idx ON customers (name);
CREATE INDEX customers_phone_idx ON customers (phone);
CREATE INDEX customers_created_at_idx ON customers (created_at);

CREATE TABLE services (
    id UUID PRIMARY KEY,
    name TEXT,
    description TEXT,
    price DECIMAL,
    duration INT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE staff (
    id UUID PRIMARY KEY,
    name TEXT,
    email TEXT,
    phone TEXT,
    specialization TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE appointments (
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
