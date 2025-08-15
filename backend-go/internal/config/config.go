package config

import (
	"log"
	"os"
	"strconv"
	"strings"
)

// Config holds all configuration for the application
type Config struct {
	Server    ServerConfig
	Database  DatabaseConfig
	Logging   LoggingConfig
	CORS      CORSConfig
	Health    HealthConfig
	Cache     CacheConfig
}

// ServerConfig holds server configuration
type ServerConfig struct {
	Port         string
	Host         string
	ReadTimeout  int
	WriteTimeout int
	IdleTimeout  int
}

// DatabaseConfig holds database configuration
type DatabaseConfig struct {
	Hosts      []string
	Keyspace   string
	Username   string
	Password   string
	Datacenter string
}

// LoggingConfig holds logging configuration
type LoggingConfig struct {
	Level  string
	Format string
}

// CORSConfig holds CORS configuration
type CORSConfig struct {
	AllowedOrigins []string
	AllowedMethods []string
	AllowedHeaders []string
}

// HealthConfig holds health check configuration
type HealthConfig struct {
	Enabled  bool
	Endpoint string
}

// CacheConfig holds cache configuration
type CacheConfig struct {
	Enabled bool
	TTL     int
}

// LoadConfig loads configuration from environment variables
func LoadConfig() *Config {
	return &Config{
		Server: ServerConfig{
			Port:         getEnvOrDefault("SERVER_PORT", "8080"),
			Host:         getEnvOrDefault("SERVER_HOST", "0.0.0.0"),
			ReadTimeout:  getEnvAsIntOrDefault("SERVER_READ_TIMEOUT", 30),
			WriteTimeout: getEnvAsIntOrDefault("SERVER_WRITE_TIMEOUT", 30),
			IdleTimeout:  getEnvAsIntOrDefault("SERVER_IDLE_TIMEOUT", 120),
		},
		Database: DatabaseConfig{
			Hosts:      getEnvAsSlice("CASSANDRA_HOSTS", []string{"localhost:9042"}),
			Keyspace:   getEnvOrDefault("CASSANDRA_KEYSPACE", "beauty_salon"),
			Username:   os.Getenv("CASSANDRA_USERNAME"),
			Password:   os.Getenv("CASSANDRA_PASSWORD"),
			Datacenter: getEnvOrDefault("CASSANDRA_DATACENTER", "datacenter1"),
		},
		Logging: LoggingConfig{
			Level:  getEnvOrDefault("LOG_LEVEL", "info"),
			Format: getEnvOrDefault("LOG_FORMAT", "json"),
		},
		CORS: CORSConfig{
			AllowedOrigins: getEnvAsSlice("CORS_ALLOWED_ORIGINS", []string{"http://localhost:3000"}),
			AllowedMethods: getEnvAsSlice("CORS_ALLOWED_METHODS", []string{"GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"}),
			AllowedHeaders: getEnvAsSlice("CORS_ALLOWED_HEADERS", []string{"Origin", "Content-Type", "Accept", "Authorization", "X-Requested-With"}),
		},
		Health: HealthConfig{
			Enabled:  getEnvAsBoolOrDefault("HEALTH_ENABLED", true),
			Endpoint: getEnvOrDefault("HEALTH_ENDPOINT", "/health"),
		},
		Cache: CacheConfig{
			Enabled: getEnvAsBoolOrDefault("CACHE_ENABLED", true),
			TTL:     getEnvAsIntOrDefault("CACHE_TTL", 300),
		},
	}
}

// Helper functions for environment variable parsing

func getEnvOrDefault(key, defaultValue string) string {
	if value := os.Getenv(key); value != "" {
		return value
	}
	return defaultValue
}

func getEnvAsIntOrDefault(key string, defaultValue int) int {
	if valueStr := os.Getenv(key); valueStr != "" {
		if value, err := strconv.Atoi(valueStr); err == nil {
			return value
		}
		log.Printf("Warning: Invalid integer value for %s: %s, using default: %d", key, valueStr, defaultValue)
	}
	return defaultValue
}

func getEnvAsBoolOrDefault(key string, defaultValue bool) bool {
	if valueStr := os.Getenv(key); valueStr != "" {
		if value, err := strconv.ParseBool(valueStr); err == nil {
			return value
		}
		log.Printf("Warning: Invalid boolean value for %s: %s, using default: %t", key, valueStr, defaultValue)
	}
	return defaultValue
}

func getEnvAsSlice(key string, defaultValue []string) []string {
	if valueStr := os.Getenv(key); valueStr != "" {
		return strings.Split(valueStr, ",")
	}
	return defaultValue
}
