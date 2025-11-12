# Stage 1: Build the application
FROM maven:3-eclipse-temurin-21 AS build

WORKDIR /app

# Copy the entire project
COPY . .

# Package the application
RUN mvn package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 10001

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
