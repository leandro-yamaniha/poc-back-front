# Dockerfile otimizado - usa artefatos pré-buildados
FROM python:3.11-slim

# Install runtime dependencies
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy pre-installed virtual environment
COPY ./venv ./venv
COPY ./requirements.txt ./
COPY ./main.py ./
COPY ./app ./app

# Memory configuration with build args
ARG PYTHON_MAX_MEMORY=1g
ARG UVICORN_WORKERS=1

# Create non-root user
RUN useradd -m -u 1000 appuser && \
    chown -R appuser:appuser /app

USER appuser

# Use virtual environment
ENV PATH="/app/venv/bin:$PATH"
ENV UVICORN_WORKERS=${UVICORN_WORKERS}

# Expose port
EXPOSE 10003

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:10003/ || exit 1

# Run the application
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "10003"]
