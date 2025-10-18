-- Flyway Migration: Create keyspace for Beauty Salon
CREATE KEYSPACE IF NOT EXISTS beauty_salon
WITH REPLICATION = {
    'class': 'SimpleStrategy',
    'replication_factor': 1
};
