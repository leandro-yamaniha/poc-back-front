package main

import (
	"flag"
	"log"
	"os"

	"beauty-salon-backend-go/internal/migrations"
	"beauty-salon-backend-go/pkg/database"
)

func main() {
	var (
		up   = flag.Bool("up", false, "Run migrations up")
		down = flag.Bool("down", false, "Run migrations down (not implemented)")
	)
	flag.Parse()

	if !*up && !*down {
		log.Println("Usage: migrate -up or migrate -down")
		os.Exit(1)
	}

	// Initialize database connection
	db, err := database.NewCassandraDBFromEnv()
	if err != nil {
		log.Fatalf("Failed to connect to database: %v", err)
	}
	defer db.Close()

	// Create migrator
	migrator := migrations.NewMigrator(db.Session)

	if *up {
		log.Println("Running migrations...")
		if err := migrator.RunMigrations(); err != nil {
			log.Fatalf("Failed to run migrations: %v", err)
		}
		log.Println("Migrations completed successfully!")
	}

	if *down {
		log.Println("Migration rollback not implemented yet")
		os.Exit(1)
	}
}
