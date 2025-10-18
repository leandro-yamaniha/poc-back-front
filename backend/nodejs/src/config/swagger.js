const swaggerJsdoc = require('swagger-jsdoc');
const swaggerUi = require('swagger-ui-express');
const YAML = require('yamljs');
const path = require('path');

/**
 * Configuração do Swagger/OpenAPI
 */

// Carrega a especificação OpenAPI do arquivo YAML
const openApiSpec = YAML.load(path.join(__dirname, '../docs/openapi.yaml'));

// Configuração adicional do Swagger JSDoc (para anotações inline se necessário)
const swaggerOptions = {
  definition: {
    openapi: '3.0.3',
    info: {
      title: 'Beauty Salon Management API',
      version: '1.0.0',
      description: 'API completa para gerenciamento de salão de beleza'
    },
    servers: [
      {
        url: process.env.API_BASE_URL || 'http://localhost:3000',
        description: 'Servidor da API'
      }
    ]
  },
  apis: [
    path.join(__dirname, '../routes/*.js'),
    path.join(__dirname, '../controllers/*.js'),
    path.join(__dirname, '../models/*.js')
  ]
};

// Gera a especificação usando swagger-jsdoc (para anotações inline)
const swaggerSpec = swaggerJsdoc(swaggerOptions);

// Mescla a especificação do arquivo YAML com as anotações inline
const finalSpec = {
  ...openApiSpec,
  // Adiciona informações dinâmicas se necessário
  info: {
    ...openApiSpec.info,
    version: process.env.API_VERSION || openApiSpec.info.version
  },
  servers: [
    {
      url: process.env.API_BASE_URL || 'http://localhost:8080',
      description: 'Servidor da API'
    }
  ]
};

// Configurações customizadas do Swagger UI
const swaggerUiOptions = {
  customCss: `
    .swagger-ui .topbar { display: none; }
    .swagger-ui .info .title { color: #2c3e50; }
    .swagger-ui .scheme-container { background: #f8f9fa; padding: 15px; border-radius: 5px; }
  `,
  customSiteTitle: 'Beauty Salon API Documentation',
  customfavIcon: '/favicon.ico',
  swaggerOptions: {
    persistAuthorization: true,
    displayRequestDuration: true,
    docExpansion: 'list',
    filter: true,
    showExtensions: true,
    showCommonExtensions: true,
    tryItOutEnabled: true,
    supportedSubmitMethods: ['get', 'post', 'put', 'delete', 'patch'],
    requestInterceptor: (request) => {
      // Adiciona headers CORS necessários
      request.headers['Access-Control-Allow-Origin'] = '*';
      return request;
    }
  }
};

/**
 * Configura o Swagger UI no Express app
 * @param {Express} app - Instância do Express
 */
function setupSwagger(app) {
  // Endpoint para servir a especificação OpenAPI em JSON
  app.get('/api-docs.json', (req, res) => {
    res.setHeader('Content-Type', 'application/json');
    res.send(finalSpec);
  });

  // Endpoint para servir a especificação OpenAPI em YAML
  app.get('/api-docs.yaml', (req, res) => {
    res.setHeader('Content-Type', 'text/yaml');
    res.send(YAML.stringify(finalSpec, 4));
  });

  // Swagger UI
  app.use('/api-docs', swaggerUi.serve);
  app.get('/api-docs', swaggerUi.setup(finalSpec, swaggerUiOptions));

  // Redirecionamento da raiz para a documentação (opcional)
  app.get('/docs', (req, res) => {
    res.redirect('/api-docs');
  });

  console.log('📚 Swagger UI configurado em:');
  const port = process.env.PORT || 8080;
  console.log(`   - Interface: http://localhost:${port}/api-docs`);
  console.log(`   - JSON: http://localhost:${port}/api-docs.json`);
  console.log(`   - YAML: http://localhost:${port}/api-docs.yaml`);
}

/**
 * Middleware para adicionar headers de CORS específicos para a documentação
 */
function swaggerCorsMiddleware(req, res, next) {
  if (req.path.startsWith('/api-docs')) {
    res.header('Access-Control-Allow-Origin', '*');
    res.header('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
    res.header('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept, Authorization');
  }
  next();
}

/**
 * Valida a especificação OpenAPI
 */
function validateOpenApiSpec() {
  try {
    // Validações básicas
    if (!finalSpec.info || !finalSpec.info.title) {
      throw new Error('OpenAPI spec must have info.title');
    }
    
    if (!finalSpec.paths || Object.keys(finalSpec.paths).length === 0) {
      throw new Error('OpenAPI spec must have at least one path');
    }

    console.log('✅ Especificação OpenAPI validada com sucesso');
    console.log(`📋 Título: ${finalSpec.info.title}`);
    console.log(`📋 Versão: ${finalSpec.info.version}`);
    console.log(`📋 Endpoints: ${Object.keys(finalSpec.paths).length}`);
    
    return true;
  } catch (error) {
    console.error('❌ Erro na validação da especificação OpenAPI:', error.message);
    return false;
  }
}

module.exports = {
  setupSwagger,
  swaggerCorsMiddleware,
  validateOpenApiSpec,
  swaggerSpec: finalSpec
};
