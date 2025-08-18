// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************

// Custom command to setup API mocks for all endpoints
Cypress.Commands.add('setupApiMocks', () => {
  cy.intercept('GET', '/api/customers', { fixture: 'customers.json' }).as('getCustomers');
  cy.intercept('GET', '/api/services', { fixture: 'services.json' }).as('getServices');
  cy.intercept('GET', '/api/services/active', { fixture: 'services.json' }).as('getActiveServices');
  cy.intercept('GET', '/api/staff', { fixture: 'staff.json' }).as('getStaff');
  cy.intercept('GET', '/api/staff/active', { fixture: 'staff.json' }).as('getActiveStaff');
  cy.intercept('GET', '/api/appointments', { fixture: 'appointments.json' }).as('getAppointments');
  cy.intercept('GET', /\/api\/appointments\/date\/[0-9]{4}-[0-9]{2}-[0-9]{2}$/, { fixture: 'todayAppointments.json' }).as('getTodayAppointments');
});

// Custom command to create a customer through the UI
Cypress.Commands.add('createCustomer', (customer) => {
  cy.contains('Novo Cliente').click();
  cy.get('.modal').should('be.visible');
  
  cy.get('input[name="name"]').type(customer.name);
  cy.get('input[name="email"]').type(customer.email);
  cy.get('input[name="phone"]').type(customer.phone);
  if (customer.address) {
    cy.get('textarea[name="address"]').type(customer.address);
  }
  
  // Mock the API response
  cy.intercept('POST', '/api/customers', { 
    statusCode: 201, 
    body: { id: '123', ...customer } 
  }).as('createCustomer');
  
  cy.contains('button', 'Salvar').click();
  cy.wait('@createCustomer');
  cy.get('.modal').should('not.exist');
});

// Custom command to create a service through the UI
Cypress.Commands.add('createService', (service) => {
  cy.contains('Novo Serviço').click();
  cy.get('.modal').should('be.visible');
  
  cy.get('input[name="name"]').type(service.name);
  cy.get('select[name="category"]').select(service.category);
  cy.get('input[name="price"]').type(service.price.toString());
  cy.get('input[name="duration"]').type(service.duration.toString());
  if (service.description) {
    cy.get('textarea[name="description"]').type(service.description);
  }
  
  // Mock the API response
  cy.intercept('POST', '/api/services', { 
    statusCode: 201, 
    body: { id: '123', ...service } 
  }).as('createService');
  
  cy.contains('button', 'Salvar').click();
  cy.wait('@createService');
  cy.get('.modal').should('not.exist');
});

// Custom command to create an appointment through the UI
Cypress.Commands.add('createAppointment', (appointment) => {
  cy.contains('Novo Agendamento').click();
  cy.get('.modal').should('be.visible');
  
  cy.get('select[name="customer_id"]').select(1);
  cy.get('select[name="service_id"]').select(1);
  cy.get('select[name="staff_id"]').select(1);
  cy.get('input[name="appointment_date"]').type(appointment.date);
  
  if (appointment.notes) {
    cy.get('textarea[name="notes"]').type(appointment.notes);
  }
  
  // Mock the API response
  cy.intercept('POST', '/api/appointments', { 
    statusCode: 201, 
    body: { id: '123', ...appointment } 
  }).as('createAppointment');
  
  cy.contains('button', 'Salvar').click();
  cy.wait('@createAppointment');
  cy.get('.modal').should('not.exist');
});

// Custom command to wait for dashboard to load
Cypress.Commands.add('waitForDashboard', () => {
  cy.setupApiMocks();
  cy.visit('/');
  cy.wait(['@getCustomers', '@getActiveServices', '@getActiveStaff', '@getTodayAppointments']);
  cy.contains('h1', 'Dashboard').should('be.visible');
});

// Custom command to check if element is visible in viewport
Cypress.Commands.add('isInViewport', (element) => {
  cy.get(element).then($el => {
    const bottom = Cypress.$(cy.state('window')).height();
    const rect = $el[0].getBoundingClientRect();
    
    expect(rect.top).to.be.lessThan(bottom);
    expect(rect.bottom).to.be.greaterThan(0);
  });
});

// Custom command to test responsive design
Cypress.Commands.add('testResponsive', (callback) => {
  const viewports = [
    { width: 320, height: 568, name: 'mobile' },
    { width: 768, height: 1024, name: 'tablet' },
    { width: 1280, height: 720, name: 'desktop' }
  ];
  
  viewports.forEach(viewport => {
    cy.viewport(viewport.width, viewport.height);
    if (callback) {
      callback(viewport);
    }
  });
});

// Custom command to check accessibility
Cypress.Commands.add('checkA11y', () => {
  // Basic accessibility checks
  cy.get('img').each($img => {
    cy.wrap($img).should('have.attr', 'alt');
  });
  
  cy.get('button').each($btn => {
    cy.wrap($btn).should('not.be.empty');
  });
  
  cy.get('input').each($input => {
    cy.wrap($input).should('have.attr', 'type');
  });
  
  cy.get('a').each($link => {
    cy.wrap($link).should('not.be.empty');
  });
});

// Custom command to simulate network delays
Cypress.Commands.add('simulateSlowNetwork', () => {
  cy.intercept('GET', '/api/**', (req) => {
    req.reply((res) => {
      return new Promise(resolve => {
        setTimeout(() => resolve(res), 2000); // 2 second delay
      });
    });
  });
});

// Custom command to test error scenarios
Cypress.Commands.add('simulateApiError', (endpoint, statusCode = 500) => {
  cy.intercept('GET', endpoint, { 
    statusCode: statusCode, 
    body: { error: 'Server Error' } 
  }).as('apiError');
});

// Custom command to fill form fields by data-testid
Cypress.Commands.add('fillForm', (formData) => {
  Object.keys(formData).forEach(key => {
    const value = formData[key];
    if (value !== null && value !== undefined) {
      cy.get(`[data-testid="${key}"], [name="${key}"]`).then($el => {
        const tagName = $el.prop('tagName').toLowerCase();
        const type = $el.attr('type');
        
        if (tagName === 'select') {
          cy.wrap($el).select(value.toString());
        } else if (type === 'checkbox') {
          if (value) cy.wrap($el).check();
        } else if (type === 'radio') {
          cy.wrap($el).check();
        } else {
          cy.wrap($el).clear().type(value.toString());
        }
      });
    }
  });
});

// Custom command to wait for loading to complete
Cypress.Commands.add('waitForLoading', () => {
  cy.get('.spinner-border, .loading, [data-testid="loading"]').should('not.exist');
});

// Custom command to check toast notifications
Cypress.Commands.add('checkToast', (message, type = 'success') => {
  cy.get('.toast, .alert, .notification').should('be.visible');
  if (message) {
    cy.contains(message).should('be.visible');
  }
});

// Custom command to navigate to page and wait for load
Cypress.Commands.add('navigateAndWait', (path, apiCalls = []) => {
  cy.visit(path);
  if (apiCalls.length > 0) {
    cy.wait(apiCalls);
  }
  cy.waitForLoading();
});
