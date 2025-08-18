describe('Integration Tests', () => {
  beforeEach(() => {
    // Intercepta chamadas da API para testes mais estáveis
    cy.intercept('GET', '/api/customers', { fixture: 'customers.json' }).as('getCustomers');
    cy.intercept('GET', '/api/services/active', { fixture: 'services.json' }).as('getServices');
    cy.intercept('GET', '/api/staff/active', { fixture: 'staff.json' }).as('getStaff');
    cy.intercept('GET', '/api/appointments', { fixture: 'appointments.json' }).as('getAppointments');
    cy.intercept('GET', /\/api\/appointments\/date\/[0-9]{4}-[0-9]{2}-[0-9]{2}$/, { fixture: 'todayAppointments.json' }).as('getTodayAppointments');
  });

  it('complete dashboard workflow', () => {
    // Visita o dashboard
    cy.visit('/');
    
    // Aguarda o carregamento das estatísticas
    cy.wait(['@getCustomers', '@getServices', '@getStaff', '@getAppointments', '@getTodayAppointments']);
    
    // Verifica se as estatísticas foram carregadas
    cy.contains('h1', 'Dashboard').should('be.visible');
    cy.get('.display-4').should('have.length.at.least', 4);
    
    // Testa navegação através dos botões de ação
    cy.contains('Novo Agendamento').click();
    cy.url().should('include', '/appointments');
    
    // Volta ao dashboard
    cy.contains('Dashboard').click();
    cy.url().should('eq', Cypress.config().baseUrl + '/');
  });

  it('complete customer management workflow', () => {
    cy.visit('/customers');
    cy.wait('@getCustomers');
    
    // Abre modal de novo cliente
    cy.contains('Novo Cliente').click();
    cy.get('.modal').should('be.visible');
    
    // Preenche formulário de cliente
    cy.get('input[name="name"]').type('João Silva');
    cy.get('input[name="email"]').type('joao@email.com');
    cy.get('input[name="phone"]').type('11999999999');
    cy.get('textarea[name="address"]').type('Rua das Flores, 123');
    
    // Intercepta a criação do cliente
    cy.intercept('POST', '/api/customers', { statusCode: 201, body: { id: '123', name: 'João Silva' } }).as('createCustomer');
    
    // Salva o cliente
    cy.contains('button', 'Salvar').click();
    cy.wait('@createCustomer');
    
    // Verifica se o modal foi fechado
    cy.get('.modal').should('not.exist');
  });

  it('complete service management workflow', () => {
    cy.visit('/services');
    cy.wait('@getServices');
    
    // Abre modal de novo serviço
    cy.contains('Novo Serviço').click();
    cy.get('.modal').should('be.visible');
    
    // Preenche formulário de serviço
    cy.get('input[name="name"]').type('Corte de Cabelo');
    cy.get('select[name="category"]').select('CABELO');
    cy.get('input[name="price"]').type('50.00');
    cy.get('input[name="duration"]').type('60');
    cy.get('textarea[name="description"]').type('Corte de cabelo masculino');
    
    // Intercepta a criação do serviço
    cy.intercept('POST', '/api/services', { statusCode: 201, body: { id: '123', name: 'Corte de Cabelo' } }).as('createService');
    
    // Salva o serviço
    cy.contains('button', 'Salvar').click();
    cy.wait('@createService');
    
    // Verifica se o modal foi fechado
    cy.get('.modal').should('not.exist');
  });

  it('complete appointment booking workflow', () => {
    cy.visit('/appointments');
    cy.wait('@getAppointments');
    
    // Abre modal de novo agendamento
    cy.contains('Novo Agendamento').click();
    cy.get('.modal').should('be.visible');
    
    // Seleciona cliente, serviço e funcionário
    cy.get('select[name="customer_id"]').select(1);
    cy.get('select[name="service_id"]').select(1);
    cy.get('select[name="staff_id"]').select(1);
    
    // Define data e hora do agendamento
    cy.get('input[name="appointment_date"]').type('2025-12-31T10:00');
    
    // Intercepta a criação do agendamento
    cy.intercept('POST', '/api/appointments', { statusCode: 201, body: { id: '123' } }).as('createAppointment');
    
    // Salva o agendamento
    cy.contains('button', 'Salvar').click();
    cy.wait('@createAppointment');
    
    // Verifica se o modal foi fechado
    cy.get('.modal').should('not.exist');
  });

  it('error handling and recovery', () => {
    // Simula erro na API
    cy.intercept('GET', '/api/customers', { statusCode: 500, body: { error: 'Server Error' } }).as('getCustomersError');
    
    cy.visit('/customers');
    cy.wait('@getCustomersError');
    
    // Verifica se mensagem de erro é exibida
    cy.contains('Erro ao carregar').should('be.visible');
  });

  it('responsive behavior across devices', () => {
    const viewports = ['iphone-6', 'ipad-2', [1280, 720]];
    
    viewports.forEach(viewport => {
      cy.viewport(viewport);
      cy.visit('/');
      
      // Verifica se elementos principais estão visíveis
      cy.contains('Beauty Salon').should('be.visible');
      cy.contains('Dashboard').should('be.visible');
      
      // Testa navegação mobile
      if (viewport === 'iphone-6') {
        // Verifica se menu mobile funciona (se implementado)
        cy.get('.navbar-toggler, .mobile-menu').should('exist');
      }
    });
  });

  it('accessibility compliance', () => {
    cy.visit('/');
    
    // Verifica se elementos têm atributos de acessibilidade
    cy.get('button').should('have.attr', 'type');
    cy.get('input').should('have.attr', 'type');
    cy.get('img').should('have.attr', 'alt');
    
    // Verifica se links têm texto descritivo
    cy.get('a').should('not.be.empty');
  });

  it('performance and loading times', () => {
    cy.visit('/', {
      onBeforeLoad: (win) => {
        win.performance.mark('start');
      },
      onLoad: (win) => {
        win.performance.mark('end');
        win.performance.measure('pageLoad', 'start', 'end');
        const measure = win.performance.getEntriesByName('pageLoad')[0];
        expect(measure.duration).to.be.lessThan(5000); // 5 segundos
      }
    });
    
    // Verifica se a página carrega em tempo hábil
    cy.contains('Dashboard').should('be.visible');
  });
});
