-- Insert sample data for testing

-- Sample services
INSERT INTO services (id, name, description, duration, price, category, is_active, created_at, updated_at)
VALUES (uuid(), 'Corte de Cabelo', 'Corte de cabelo feminino', 60, 50.00, 'Cabelo', true, toTimestamp(now()), toTimestamp(now()));

INSERT INTO services (id, name, description, duration, price, category, is_active, created_at, updated_at)
VALUES (uuid(), 'Escova', 'Escova modeladora', 45, 35.00, 'Cabelo', true, toTimestamp(now()), toTimestamp(now()));

INSERT INTO services (id, name, description, duration, price, category, is_active, created_at, updated_at)
VALUES (uuid(), 'Manicure', 'Manicure completa', 45, 25.00, 'Unhas', true, toTimestamp(now()), toTimestamp(now()));

INSERT INTO services (id, name, description, duration, price, category, is_active, created_at, updated_at)
VALUES (uuid(), 'Pedicure', 'Pedicure completa', 60, 30.00, 'Unhas', true, toTimestamp(now()), toTimestamp(now()));

INSERT INTO services (id, name, description, duration, price, category, is_active, created_at, updated_at)
VALUES (uuid(), 'Limpeza de Pele', 'Limpeza facial profunda', 90, 80.00, 'Estética', true, toTimestamp(now()), toTimestamp(now()));

-- Sample staff
INSERT INTO staff (id, name, email, phone, role, specialties, is_active, created_at, updated_at)
VALUES (uuid(), 'Maria Silva', 'maria@salao.com', '(11) 99999-1111', 'Cabeleireira', {'Corte', 'Escova', 'Coloração'}, true, toTimestamp(now()), toTimestamp(now()));

INSERT INTO staff (id, name, email, phone, role, specialties, is_active, created_at, updated_at)
VALUES (uuid(), 'Ana Santos', 'ana@salao.com', '(11) 99999-2222', 'Manicure', {'Manicure', 'Pedicure', 'Nail Art'}, true, toTimestamp(now()), toTimestamp(now()));

INSERT INTO staff (id, name, email, phone, role, specialties, is_active, created_at, updated_at)
VALUES (uuid(), 'Carla Oliveira', 'carla@salao.com', '(11) 99999-3333', 'Esteticista', {'Limpeza de Pele', 'Massagem', 'Depilação'}, true, toTimestamp(now()), toTimestamp(now()));

-- Sample customers
INSERT INTO customers (id, name, email, phone, address, created_at, updated_at)
VALUES (uuid(), 'Julia Costa', 'julia@email.com', '(11) 88888-1111', 'Rua das Flores, 123', toTimestamp(now()), toTimestamp(now()));

INSERT INTO customers (id, name, email, phone, address, created_at, updated_at)
VALUES (uuid(), 'Fernanda Lima', 'fernanda@email.com', '(11) 88888-2222', 'Av. Paulista, 456', toTimestamp(now()), toTimestamp(now()));

INSERT INTO customers (id, name, email, phone, address, created_at, updated_at)
VALUES (uuid(), 'Beatriz Rocha', 'beatriz@email.com', '(11) 88888-3333', 'Rua Augusta, 789', toTimestamp(now()), toTimestamp(now()));
