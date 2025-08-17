FROM node:18-alpine

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy source code
COPY . .

# Inject API URL at build time (used by CRA during build)
ARG REACT_APP_API_URL
ENV REACT_APP_API_URL=${REACT_APP_API_URL}

# Build the application
RUN npm run build

# Install serve to run the application
RUN npm install -g serve

# Default port (can be overridden at runtime)
ENV PORT=3000

# Expose port
EXPOSE 3000

# Start the application
CMD ["sh", "-c", "serve -s build -l ${PORT:-3000}"]
