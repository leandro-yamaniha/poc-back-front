# Beauty Salon Go Backend Makefile

.PHONY: help build run test clean deps docker-build docker-run

# Variables
BINARY_NAME=beauty-salon-api
DOCKER_IMAGE=beauty-salon-go
DOCKER_TAG=latest

# Default target
help: ## Show this help message
	@echo "Available commands:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

deps: ## Download dependencies
	go mod download
	go mod tidy

build: deps ## Build the application
	go build -o bin/$(BINARY_NAME) cmd/server/main.go

run: ## Run the application
	go run cmd/server/main.go

test: ## Run tests
	go test -v ./...

test-coverage: ## Run tests with coverage
	go test -v -coverprofile=coverage.out ./...
	go tool cover -html=coverage.out -o coverage.html

clean: ## Clean build artifacts
	rm -rf bin/
	rm -f coverage.out coverage.html

lint: ## Run linter
	golangci-lint run

format: ## Format code
	go fmt ./...

vet: ## Run go vet
	go vet ./...

docker-build: ## Build Docker image
	docker build -t $(DOCKER_IMAGE):$(DOCKER_TAG) .

docker-run: ## Run Docker container
	docker run -p 8080:8080 --env-file .env $(DOCKER_IMAGE):$(DOCKER_TAG)

dev: ## Run in development mode with hot reload (requires air)
	air

install-tools: ## Install development tools
	go install github.com/cosmtrek/air@latest
	go install github.com/golangci/golangci-lint/cmd/golangci-lint@latest

# Database commands
db-setup: ## Setup Cassandra database (requires Docker)
	docker run -d --name cassandra -p 9042:9042 cassandra:4.1

db-stop: ## Stop Cassandra database
	docker stop cassandra
	docker rm cassandra

migrate-up: ## Run database migrations
	go run cmd/migrate/main.go -up

migrate-build: ## Build migration tool
	go build -o bin/migrate cmd/migrate/main.go

# Environment setup
env-copy: ## Copy environment example file
	cp .env.example .env

setup: env-copy deps install-tools ## Initial project setup
	@echo "Setup complete! Edit .env file and run 'make run'"
