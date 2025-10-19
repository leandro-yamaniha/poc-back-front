/**
 * Retry utility for Cassandra operations
 * Handles schema propagation delays with exponential backoff
 */

/**
 * Execute a function with retry logic and exponential backoff
 * @param {Function} fn - Async function to execute
 * @param {Object} options - Retry options
 * @param {number} options.maxRetries - Maximum number of retries (default: 5)
 * @param {number} options.initialDelay - Initial delay in ms (default: 1000)
 * @param {number} options.maxDelay - Maximum delay in ms (default: 10000)
 * @param {string} options.operationName - Name for logging (default: 'operation')
 * @returns {Promise} Result of the function
 */
async function executeWithRetry(fn, options = {}) {
  const {
    maxRetries = 5,
    initialDelay = 1000,
    maxDelay = 10000,
    operationName = 'operation'
  } = options;

  let lastError;

  for (let attempt = 0; attempt < maxRetries; attempt++) {
    try {
      return await fn();
    } catch (error) {
      lastError = error;
      
      // If this was the last attempt, throw the error
      if (attempt === maxRetries - 1) {
        console.error(`❌ ${operationName} failed after ${maxRetries} attempts:`, error.message);
        throw error;
      }

      // Calculate delay with exponential backoff
      const delay = Math.min(initialDelay * Math.pow(2, attempt), maxDelay);
      
      console.warn(`⚠️  ${operationName} failed (attempt ${attempt + 1}/${maxRetries}): ${error.message}`);
      console.log(`   Retrying in ${delay}ms...`);
      
      // Wait before retrying
      await new Promise(resolve => setTimeout(resolve, delay));
    }
  }

  throw lastError;
}

/**
 * Execute Cassandra query with retry logic
 * Specifically handles schema propagation issues
 * @param {Object} client - Cassandra client
 * @param {string} query - CQL query
 * @param {Array} params - Query parameters
 * @param {Object} options - Retry options
 * @returns {Promise} Query result
 */
async function executeQueryWithRetry(client, query, params = [], options = {}) {
  return executeWithRetry(
    () => client.execute(query, params),
    {
      ...options,
      operationName: options.operationName || 'Query execution'
    }
  );
}

module.exports = {
  executeWithRetry,
  executeQueryWithRetry
};
