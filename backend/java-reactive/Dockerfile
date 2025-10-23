# Dockerfile otimizado - usa artefatos pré-buildados
FROM eclipse-temurin:21-jre-alpine

# Install curl for health checks
RUN apk add --no-cache curl

WORKDIR /app

# Copy pre-built JAR
COPY ./target/release/app.jar ./app.jar

# Expose port
EXPOSE 10006

# Run the application
ENTRYPOINT ["java"]
CMD ["-jar", "-Xmx1g", "-Xms512m", "app.jar"]
