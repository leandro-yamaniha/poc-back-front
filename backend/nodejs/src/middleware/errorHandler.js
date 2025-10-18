const errorHandler = (err, req, res, next) => {
  console.error('Error occurred:', {
    message: err.message,
    stack: err.stack,
    url: req.url,
    method: req.method,
    timestamp: new Date().toISOString()
  });

  // Default error
  let error = {
    message: err.message || 'Internal Server Error',
    status: err.status || 500,
    timestamp: new Date().toISOString()
  };

  // Cassandra errors
  if (err.name === 'NoHostAvailableError') {
    error = {
      message: 'Database connection error',
      status: 503,
      timestamp: new Date().toISOString()
    };
  }

  // Validation errors
  if (err.name === 'ValidationError') {
    error = {
      message: 'Validation failed',
      details: err.details || err.message,
      status: 400,
      timestamp: new Date().toISOString()
    };
  }

  // Custom business logic errors
  if (err.message && (
    err.message.includes('não encontrado') ||
    err.message.includes('not found') ||
    err.message.includes('não existe')
  )) {
    error.status = 404;
  }

  if (err.message && (
    err.message.includes('já está em uso') ||
    err.message.includes('already exists') ||
    err.message.includes('conflito')
  )) {
    error.status = 409;
  }

  // Don't leak error details in production
  if (process.env.NODE_ENV === 'production' && error.status === 500) {
    error.message = 'Internal Server Error';
    delete error.stack;
  }

  res.status(error.status).json(error);
};

module.exports = errorHandler;
