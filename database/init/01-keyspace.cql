-- Create keyspace for beauty salon application
CREATE KEYSPACE IF NOT EXISTS beauty_salon
WITH REPLICATION = {
    'class': 'SimpleStrategy',
    'replication_factor': 1
};

USE beauty_salon;
