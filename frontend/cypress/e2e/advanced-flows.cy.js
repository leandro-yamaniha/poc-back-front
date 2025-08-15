describe('Advanced E2E Flows', () => {
  beforeEach(() => {
    cy.setupApiMocks();
  });

  describe('Complete Business Workflow', () => {
    it('should handle complete customer journey from registration to appointment', () => {
      // Step 1: Navigate to customers and create new customer
      cy.navigateAndWait('/customers', ['@getCustomers']);
      
      const newCustomer = {
        name: 'João da Silva',
        email: 'joao.silva@email.com',
        phone: '11999887766',
        address: 'Rua das Palmeiras, 456'
      };
      
      cy.createCustomer(newCustomer);
      cy.checkToast('Cliente criado com sucesso');
      
      // Step 2: Navigate to services and verify available services
      cy.navigateAndWait('/services', ['@getServices']);
      cy.contains('Corte de Cabelo Masculino').should('be.visible');
      
      // Step 3: Navigate to appointments and create new appointment
      cy.navigateAndWait('/appointments', ['@getAppointments']);
      
      const newAppointment = {
        date: '2025-12-31T10:00',
        notes: 'Primeiro agendamento do cliente'
      };
      
      cy.createAppointment(newAppointment);
      cy.checkToast('Agendamento criado com sucesso');
      
      // Step 4: Verify dashboard reflects new data
      cy.waitForDashboard();
      cy.get('.display-4').should('contain.text', '3'); // Assuming 3 customers now
    });

    it('should handle staff scheduling workflow', () => {
      // Navigate to staff management
      cy.navigateAndWait('/staff', ['@getStaff']);
      
      // Verify staff members are loaded
      cy.contains('Carlos Mendes').should('be.visible');
      cy.contains('CABELEIREIRO').should('be.visible');
      
      // Check staff availability (if implemented)
      cy.get('[data-testid="staff-schedule"]').should('exist');
      
      // Navigate to appointments to see staff schedule
      cy.navigateAndWait('/appointments', ['@getAppointments']);
      
      // Filter by staff member
      cy.get('select[name="staff_filter"]').select('Carlos Mendes');
      cy.contains('Carlos Mendes').should('be.visible');
    });
  });

  describe('Error Handling and Recovery', () => {
    it('should handle API failures gracefully', () => {
      // Simulate server error
      cy.simulateApiError('/api/customers', 500);
      
      cy.visit('/customers');
      cy.wait('@apiError');
      
      // Check error message is displayed
      cy.contains('Erro ao carregar').should('be.visible');
      cy.contains('Tente novamente').should('be.visible');
      
      // Test retry functionality
      cy.setupApiMocks(); // Reset to working state
      cy.contains('Tentar novamente').click();
      cy.wait('@getCustomers');
      
      // Verify data loads after retry
      cy.contains('Maria Silva').should('be.visible');
    });

    it('should handle network timeout scenarios', () => {
      cy.simulateSlowNetwork();
      
      cy.visit('/customers');
      
      // Should show loading state
      cy.get('.spinner-border, [data-testid="loading"]').should('be.visible');
      
      // Eventually should load (with longer timeout)
      cy.contains('Maria Silva', { timeout: 10000 }).should('be.visible');
    });

    it('should validate form inputs and show errors', () => {
      cy.visit('/customers');
      cy.wait('@getCustomers');
      
      // Try to create customer with invalid data
      cy.contains('Novo Cliente').click();
      cy.get('.modal').should('be.visible');
      
      // Submit empty form
      cy.contains('button', 'Salvar').click();
      
      // Should show validation errors
      cy.contains('Nome é obrigatório').should('be.visible');
      cy.contains('Email é obrigatório').should('be.visible');
      
      // Fill invalid email
      cy.get('input[name="email"]').type('invalid-email');
      cy.contains('button', 'Salvar').click();
      cy.contains('Email inválido').should('be.visible');
      
      // Fill valid data
      cy.fillForm({
        name: 'João Silva',
        email: 'joao@email.com',
        phone: '11999999999'
      });
      
      cy.intercept('POST', '/api/customers', { statusCode: 201, body: { id: '123' } }).as('createCustomer');
      cy.contains('button', 'Salvar').click();
      cy.wait('@createCustomer');
      
      cy.get('.modal').should('not.exist');
    });
  });

  describe('Performance and User Experience', () => {
    it('should load pages within acceptable time limits', () => {
      const pages = [
        { path: '/', name: 'Dashboard' },
        { path: '/customers', name: 'Customers' },
        { path: '/services', name: 'Services' },
        { path: '/staff', name: 'Staff' },
        { path: '/appointments', name: 'Appointments' }
      ];

      pages.forEach(page => {
        const startTime = Date.now();
        
        cy.visit(page.path);
        cy.contains(page.name).should('be.visible');
        
        cy.then(() => {
          const loadTime = Date.now() - startTime;
          expect(loadTime).to.be.lessThan(3000); // 3 seconds max
        });
      });
    });

    it('should handle concurrent user interactions', () => {
      cy.waitForDashboard();
      
      // Simulate rapid navigation
      cy.contains('Clientes').click();
      cy.contains('Serviços').click();
      cy.contains('Funcionários').click();
      cy.contains('Agendamentos').click();
      cy.contains('Dashboard').click();
      
      // Should end up on dashboard
      cy.url().should('eq', Cypress.config().baseUrl + '/');
      cy.contains('h1', 'Dashboard').should('be.visible');
    });

    it('should maintain state during navigation', () => {
      // Go to customers and open modal
      cy.navigateAndWait('/customers', ['@getCustomers']);
      cy.contains('Novo Cliente').click();
      cy.get('.modal').should('be.visible');
      
      // Fill some data
      cy.get('input[name="name"]').type('Test User');
      
      // Navigate away and back
      cy.contains('Dashboard').click();
      cy.contains('Clientes').click();
      
      // Modal should be closed and form reset
      cy.get('.modal').should('not.exist');
    });
  });

  describe('Accessibility and Usability', () => {
    it('should be keyboard navigable', () => {
      cy.visit('/');
      
      // Tab through main navigation
      cy.get('body').tab();
      cy.focused().should('contain', 'Dashboard');
      
      cy.focused().tab();
      cy.focused().should('contain', 'Clientes');
      
      cy.focused().tab();
      cy.focused().should('contain', 'Serviços');
    });

    it('should have proper ARIA labels and roles', () => {
      cy.visit('/');
      
      // Check main navigation has proper roles
      cy.get('nav').should('have.attr', 'role', 'navigation');
      
      // Check buttons have proper labels
      cy.get('button').each($btn => {
        cy.wrap($btn).should('satisfy', $el => {
          return $el.text().trim() !== '' || $el.attr('aria-label') !== undefined;
        });
      });
      
      // Check form inputs have labels
      cy.visit('/customers');
      cy.wait('@getCustomers');
      cy.contains('Novo Cliente').click();
      
      cy.get('input').each($input => {
        cy.wrap($input).should('satisfy', $el => {
          const id = $el.attr('id');
          const name = $el.attr('name');
          return cy.$$(`label[for="${id}"], label[for="${name}"]`).length > 0 ||
                 $el.attr('aria-label') !== undefined ||
                 $el.attr('placeholder') !== undefined;
        });
      });
    });

    it('should work with screen reader simulation', () => {
      cy.visit('/');
      
      // Check page has proper heading structure
      cy.get('h1').should('exist').and('be.visible');
      cy.get('h1').should('contain.text', 'Dashboard');
      
      // Check content is properly structured
      cy.get('main, [role="main"]').should('exist');
      
      // Check interactive elements are focusable
      cy.get('button, a, input, select, textarea').each($el => {
        cy.wrap($el).should('not.have.attr', 'tabindex', '-1');
      });
    });
  });

  describe('Data Integrity and Validation', () => {
    it('should prevent duplicate entries', () => {
      cy.visit('/customers');
      cy.wait('@getCustomers');
      
      // Try to create customer with existing email
      cy.contains('Novo Cliente').click();
      cy.fillForm({
        name: 'João Silva',
        email: 'maria.silva@email.com', // Existing email
        phone: '11999999999'
      });
      
      // Mock duplicate error response
      cy.intercept('POST', '/api/customers', { 
        statusCode: 409, 
        body: { error: 'Email já cadastrado' } 
      }).as('duplicateCustomer');
      
      cy.contains('button', 'Salvar').click();
      cy.wait('@duplicateCustomer');
      
      // Should show error message
      cy.contains('Email já cadastrado').should('be.visible');
      cy.get('.modal').should('be.visible'); // Modal should remain open
    });

    it('should handle data synchronization', () => {
      // Load customers page
      cy.navigateAndWait('/customers', ['@getCustomers']);
      cy.contains('Maria Silva').should('be.visible');
      
      // Simulate external data change
      const updatedCustomers = [
        {
          id: 'c4ae162b-1290-4b9d-a213-4b5e207e6176',
          name: 'Maria Silva Santos', // Updated name
          email: 'maria.silva@email.com',
          phone: '11987654321'
        }
      ];
      
      cy.intercept('GET', '/api/customers', { body: updatedCustomers }).as('getUpdatedCustomers');
      
      // Refresh or navigate back
      cy.reload();
      cy.wait('@getUpdatedCustomers');
      
      // Should show updated data
      cy.contains('Maria Silva Santos').should('be.visible');
    });
  });
});
