# Dockerfile otimizado - usa artefatos pré-buildados
FROM eclipse-temurin:21-jre-alpine

# Install curl for health checks
RUN apk add --no-cache curl

WORKDIR /app

# Copy pre-built JAR
COPY ./target/release/app.jar ./app.jar

# Expose port
EXPOSE 10001

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:10001/actuator/health || exit 1

# Memory configuration with build args
ARG JAVA_MAX_MEMORY=1g
ARG JAVA_MIN_MEMORY=512m
ARG JAVA_OPTS=""

# Run the application
ENTRYPOINT ["java"]
CMD ["-jar", "-Xmx${JAVA_MAX_MEMORY}", "-Xms${JAVA_MIN_MEMORY}", "${JAVA_OPTS}", "app.jar"]
