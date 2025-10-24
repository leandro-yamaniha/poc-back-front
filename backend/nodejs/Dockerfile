# Dockerfile otimizado - usa artefatos pré-buildados
FROM node:18-alpine

# Install curl for health checks
RUN apk add --no-cache curl

WORKDIR /app

# Copy pre-installed dependencies and application
COPY ./node_modules ./node_modules
COPY ./package*.json ./
COPY ./src ./src
COPY ./docker-entrypoint.sh ./

# Memory configuration with build args
ARG NODE_MAX_MEMORY=1024
ARG NODE_MAX_OLD_SPACE_SIZE=1024

# Set Node.js memory environment variables
ENV NODE_OPTIONS="--max-old-space-size=${NODE_MAX_OLD_SPACE_SIZE}"

# Create non-root user
RUN addgroup -g 1001 -S nodejs && \
    adduser -S nodejs -u 1001 && \
    chmod +x docker-entrypoint.sh && \
    chown -R nodejs:nodejs /app

USER nodejs

# Expose port
EXPOSE 10004

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:10004/health || exit 1

# Run the application
ENTRYPOINT ["./docker-entrypoint.sh"]
CMD ["node", "src/app.js"]
