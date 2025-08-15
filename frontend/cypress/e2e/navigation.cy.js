describe('Navigation', () => {
  beforeEach(() => {
    cy.visit('/');
  });

  it('navigates to all main pages', () => {
    // Testa navegação para Dashboard
    cy.contains('Dashboard').click();
    cy.url().should('eq', Cypress.config().baseUrl + '/');
    cy.contains('h1', 'Dashboard').should('be.visible');

    // Testa navegação para Agendamentos
    cy.contains('Agendamentos').click();
    cy.url().should('include', '/appointments');
    cy.contains('h1', 'Agendamentos').should('be.visible');

    // Testa navegação para Clientes
    cy.contains('Clientes').click();
    cy.url().should('include', '/customers');
    cy.contains('h1', 'Clientes').should('be.visible');

    // Testa navegação para Serviços
    cy.contains('Serviços').click();
    cy.url().should('include', '/services');
    cy.contains('h1', 'Serviços').should('be.visible');

    // Testa navegação para Funcionários
    cy.contains('Funcionários').click();
    cy.url().should('include', '/staff');
    cy.contains('h1', 'Funcionários').should('be.visible');
  });

  it('navigates using action buttons from dashboard', () => {
    // Testa botão "Novo Agendamento"
    cy.contains('Novo Agendamento').click();
    cy.url().should('include', '/appointments');

    // Volta para o dashboard
    cy.contains('Dashboard').click();

    // Testa botão "Gerenciar Clientes"
    cy.contains('Gerenciar Clientes').click();
    cy.url().should('include', '/customers');

    // Volta para o dashboard
    cy.contains('Dashboard').click();

    // Testa botão "Gerenciar Serviços"
    cy.contains('Gerenciar Serviços').click();
    cy.url().should('include', '/services');
  });

  it('displays correct page titles', () => {
    const pages = [
      { link: 'Dashboard', title: 'Dashboard' },
      { link: 'Agendamentos', title: 'Agendamentos' },
      { link: 'Clientes', title: 'Clientes' },
      { link: 'Serviços', title: 'Serviços' },
      { link: 'Funcionários', title: 'Funcionários' }
    ];

    pages.forEach(page => {
      cy.contains(page.link).click();
      cy.contains('h1', page.title).should('be.visible');
    });
  });
});
