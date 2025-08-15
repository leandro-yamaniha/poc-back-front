describe('Homepage', () => {
  beforeEach(() => {
    // Visita a página inicial antes de cada teste
    cy.visit('/');
  });

  it('successfully loads and displays the title', () => {
    // Verifica se o título da página contém o texto esperado
    cy.title().should('include', 'Beauty Salon');
  });

  it('displays the main navigation', () => {
    // Verifica se a navegação principal está presente
    cy.contains('Beauty Salon').should('be.visible');
    cy.contains('Dashboard').should('be.visible');
    cy.contains('Agendamentos').should('be.visible');
    cy.contains('Clientes').should('be.visible');
    cy.contains('Serviços').should('be.visible');
    cy.contains('Funcionários').should('be.visible');
  });

  it('displays the dashboard content', () => {
    // Verifica se o conteúdo do dashboard está presente
    cy.contains('h1', 'Dashboard').should('be.visible');
    cy.contains('Visão geral do seu salão de beleza').should('be.visible');
    
    // Verifica se as estatísticas estão sendo exibidas
    cy.contains('Clientes').should('be.visible');
    cy.contains('Serviços Ativos').should('be.visible');
    cy.contains('Funcionários').should('be.visible');
    cy.contains('Agendamentos Hoje').should('be.visible');
  });

  it('displays action buttons', () => {
    // Verifica se os botões de ação rápida estão presentes
    cy.contains('Novo Agendamento').should('be.visible');
    cy.contains('Gerenciar Clientes').should('be.visible');
    cy.contains('Gerenciar Serviços').should('be.visible');
  });
});
