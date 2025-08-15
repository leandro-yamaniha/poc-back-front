const { defineConfig } = require('cypress');

module.exports = defineConfig({
  e2e: {
    baseUrl: 'http://localhost:3000',
    viewportWidth: 1280,
    viewportHeight: 720,
    video: true,
    screenshotOnRunFailure: true,
    defaultCommandTimeout: 10000,
    requestTimeout: 10000,
    responseTimeout: 10000,
    pageLoadTimeout: 30000,
    retries: {
      runMode: 2,
      openMode: 0
    },
    env: {
      apiUrl: 'http://localhost:8080/api'
    },
    setupNodeEvents(on, config) {
      // Task para logs customizados
      on('task', {
        log(message) {
          console.log(message);
          return null;
        }
      });

      // Configuração de screenshots
      on('after:screenshot', (details) => {
        console.log('Screenshot taken:', details.path);
      });

      // Configuração para diferentes ambientes
      if (config.env.environment === 'ci') {
        config.video = false;
        config.screenshotOnRunFailure = true;
      }

      return config;
    },
    specPattern: 'cypress/e2e/**/*.cy.{js,jsx,ts,tsx}',
    supportFile: 'cypress/support/e2e.js',
    fixturesFolder: 'cypress/fixtures',
    screenshotsFolder: 'cypress/screenshots',
    videosFolder: 'cypress/videos',
    downloadsFolder: 'cypress/downloads'
  },
  component: {
    devServer: {
      framework: 'create-react-app',
      bundler: 'webpack',
    },
    specPattern: 'src/**/*.cy.{js,jsx,ts,tsx}',
    supportFile: 'cypress/support/component.js'
  }
});
