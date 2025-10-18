"""
Cassandra database connection for Beauty Salon Management System
"""

import os
import logging
from typing import Optional
from cassandra.cluster import Cluster, Session
from cassandra.auth import PlainTextAuthProvider
from cassandra.policies import DCAwareRoundRobinPolicy


logger = logging.getLogger(__name__)


class DatabaseConnection:
    """Cassandra database connection manager"""
    
    def __init__(self):
        self.cluster: Optional[Cluster] = None
        self.session: Optional[Session] = None
        self.keyspace = os.getenv("CASSANDRA_KEYSPACE", "beauty_salon")
        
    async def connect(self):
        """Connect to Cassandra database"""
        try:
            # Get connection parameters from environment
            hosts = os.getenv("CASSANDRA_HOSTS", "localhost").split(",")
            port = int(os.getenv("CASSANDRA_PORT", "9042"))
            username = os.getenv("CASSANDRA_USERNAME")
            password = os.getenv("CASSANDRA_PASSWORD")
            datacenter = os.getenv("CASSANDRA_DATACENTER", "datacenter1")
            
            # Setup authentication if credentials provided
            auth_provider = None
            if username and password:
                auth_provider = PlainTextAuthProvider(username=username, password=password)
            
            # Setup load balancing policy
            load_balancing_policy = DCAwareRoundRobinPolicy(local_dc=datacenter)
            
            # Create cluster connection
            self.cluster = Cluster(
                contact_points=hosts,
                port=port,
                auth_provider=auth_provider,
                load_balancing_policy=load_balancing_policy
            )
            
            # Connect to cluster
            self.session = self.cluster.connect()
            
            # Create keyspace if it doesn't exist
            await self._create_keyspace()
            
            # Use the keyspace
            self.session.set_keyspace(self.keyspace)
            
            # Create tables if they don't exist
            await self._create_tables()
            
            logger.info(f"Connected to Cassandra database: {self.keyspace}")
            
        except Exception as e:
            logger.error(f"Failed to connect to Cassandra: {e}")
            raise
    
    async def disconnect(self):
        """Disconnect from Cassandra database"""
        try:
            if self.session:
                self.session.shutdown()
            if self.cluster:
                self.cluster.shutdown()
            logger.info("Disconnected from Cassandra database")
        except Exception as e:
            logger.error(f"Error disconnecting from Cassandra: {e}")
    
    async def _create_keyspace(self):
        """Create keyspace if it doesn't exist"""
        try:
            create_keyspace_query = f"""
            CREATE KEYSPACE IF NOT EXISTS {self.keyspace}
            WITH replication = {{
                'class': 'SimpleStrategy',
                'replication_factor': 1
            }}
            """
            self.session.execute(create_keyspace_query)
            logger.info(f"Keyspace {self.keyspace} created or already exists")
        except Exception as e:
            logger.error(f"Failed to create keyspace: {e}")
            raise
    
    async def _create_tables(self):
        """Create tables if they don't exist"""
        try:
            # Customers table
            customers_table = """
            CREATE TABLE IF NOT EXISTS customers (
                id UUID PRIMARY KEY,
                name TEXT,
                email TEXT,
                phone TEXT,
                address TEXT,
                created_at TIMESTAMP,
                updated_at TIMESTAMP
            )
            """
            
            # Services table
            services_table = """
            CREATE TABLE IF NOT EXISTS services (
                id UUID PRIMARY KEY,
                name TEXT,
                description TEXT,
                duration INT,
                price DECIMAL,
                category TEXT,
                is_active BOOLEAN,
                created_at TIMESTAMP,
                updated_at TIMESTAMP
            )
            """
            
            # Staff table
            staff_table = """
            CREATE TABLE IF NOT EXISTS staff (
                id UUID PRIMARY KEY,
                name TEXT,
                email TEXT,
                phone TEXT,
                role TEXT,
                specialties LIST<TEXT>,
                is_active BOOLEAN,
                hire_date TIMESTAMP,
                created_at TIMESTAMP,
                updated_at TIMESTAMP
            )
            """
            
            # Appointments table
            appointments_table = """
            CREATE TABLE IF NOT EXISTS appointments (
                id UUID PRIMARY KEY,
                customer_id UUID,
                staff_id UUID,
                service_id UUID,
                appointment_date DATE,
                appointment_time TIME,
                status TEXT,
                notes TEXT,
                price DECIMAL,
                created_at TIMESTAMP,
                updated_at TIMESTAMP
            )
            """
            
            # Create indexes for better query performance
            indexes = [
                "CREATE INDEX IF NOT EXISTS ON customers (email)",
                "CREATE INDEX IF NOT EXISTS ON services (category)",
                "CREATE INDEX IF NOT EXISTS ON services (is_active)",
                "CREATE INDEX IF NOT EXISTS ON staff (email)",
                "CREATE INDEX IF NOT EXISTS ON staff (role)",
                "CREATE INDEX IF NOT EXISTS ON staff (is_active)",
                "CREATE INDEX IF NOT EXISTS ON appointments (customer_id)",
                "CREATE INDEX IF NOT EXISTS ON appointments (staff_id)",
                "CREATE INDEX IF NOT EXISTS ON appointments (service_id)",
                "CREATE INDEX IF NOT EXISTS ON appointments (appointment_date)",
                "CREATE INDEX IF NOT EXISTS ON appointments (status)"
            ]
            
            # Execute table creation queries
            tables = [customers_table, services_table, staff_table, appointments_table]
            for table_query in tables:
                self.session.execute(table_query)
            
            # Execute index creation queries
            for index_query in indexes:
                self.session.execute(index_query)
            
            logger.info("Database tables and indexes created successfully")
            
        except Exception as e:
            logger.error(f"Failed to create tables: {e}")
            raise
    
    def get_session(self) -> Session:
        """Get the current database session"""
        if not self.session:
            raise RuntimeError("Database not connected")
        return self.session
