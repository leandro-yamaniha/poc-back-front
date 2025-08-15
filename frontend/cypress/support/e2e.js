// ***********************************************************
// This example support/e2e.js is processed and
// loaded automatically before your test files.
//
// This is a great place to put global configuration and
// behavior that modifies Cypress.
//
// You can change the location of this file or turn off
// automatically serving support files with the
// 'supportFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/configuration
// ***********************************************************

// Import commands.js using ES2015 syntax:
import './commands'

// Alternatively you can use CommonJS syntax:
// require('./commands')

// Hide fetch/XHR requests from command log for cleaner output
const app = window.top;
if (!app.document.head.querySelector('[data-hide-command-log-request]')) {
  const style = app.document.createElement('style');
  style.innerHTML = '.command-name-request, .command-name-xhr { display: none }';
  style.setAttribute('data-hide-command-log-request', '');
  app.document.head.appendChild(style);
}

// Global error handling
Cypress.on('uncaught:exception', (err, runnable) => {
  // Returning false here prevents Cypress from failing the test
  // on uncaught exceptions. We can customize this based on error types.
  
  // Don't fail on ResizeObserver errors (common in React apps)
  if (err.message.includes('ResizeObserver loop limit exceeded')) {
    return false;
  }
  
  // Don't fail on network errors during development
  if (err.message.includes('NetworkError') || err.message.includes('fetch')) {
    return false;
  }
  
  // Let other errors fail the test
  return true;
});

// Global configuration
Cypress.config('defaultCommandTimeout', 10000);
Cypress.config('requestTimeout', 10000);
Cypress.config('responseTimeout', 10000);

// Add custom commands for better test readability
Cypress.Commands.add('getByTestId', (testId) => {
  return cy.get(`[data-testid="${testId}"]`);
});

Cypress.Commands.add('findByTestId', (testId) => {
  return cy.find(`[data-testid="${testId}"]`);
});

// Command to wait for React to be ready
Cypress.Commands.add('waitForReact', () => {
  cy.window().should('have.property', 'React');
});

// Command to clear local storage and session storage
Cypress.Commands.add('clearStorage', () => {
  cy.clearLocalStorage();
  cy.clearCookies();
  cy.window().then((win) => {
    win.sessionStorage.clear();
  });
});

// Before each test
beforeEach(() => {
  // Clear storage before each test
  cy.clearStorage();
  
  // Set viewport to ensure consistent testing
  cy.viewport(1280, 720);
});

// After each test
afterEach(() => {
  // Clean up any remaining state
  cy.clearStorage();
});
