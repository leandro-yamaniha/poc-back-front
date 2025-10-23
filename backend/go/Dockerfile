# Dockerfile otimizado - usa artefatos pré-buildados
FROM alpine:latest

# Install runtime dependencies
RUN apk --no-cache add ca-certificates curl tzdata netcat-openbsd bash

WORKDIR /root/

# Copy pre-built binaries
COPY ./bin/main .
COPY ./bin/migrate .

# Copy configuration and scripts
COPY .env.example .env
COPY docker-entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

# Expose port
EXPOSE 10005

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:10005/health || exit 1

# Run the application
ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["./main"]
