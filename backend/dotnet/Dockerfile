# Dockerfile otimizado - usa artefatos pré-buildados
FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS runtime

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy pre-built artifacts
COPY ./publish .

# Memory configuration with build args
ARG DOTNET_MAX_MEMORY=1g
ARG DOTNET_GC_SERVER=1
ARG DOTNET_GC_CONCURRENT=1

# Set .NET runtime environment variables
ENV DOTNET_GCHeapHardLimit=${DOTNET_MAX_MEMORY}
ENV DOTNET_gcServer=${DOTNET_GC_SERVER}
ENV DOTNET_gcConcurrent=${DOTNET_GC_CONCURRENT}

# Expose port
EXPOSE 10002

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:10002/health || exit 1

# Run the application
ENTRYPOINT ["dotnet", "BeautySalonAPI.dll"]
