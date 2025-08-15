describe('Dashboard', () => {
  beforeEach(() => {
    cy.visit('/');
  });

  it('loads dashboard statistics correctly', () => {
    // Verifica se as estatísticas são carregadas
    cy.contains('h1', 'Dashboard').should('be.visible');
    cy.contains('Visão geral do seu salão de beleza').should('be.visible');

    // Verifica se os cards de estatísticas estão presentes
    cy.get('[data-testid="stats-customers"], .card').contains('Clientes').should('be.visible');
    cy.get('[data-testid="stats-services"], .card').contains('Serviços Ativos').should('be.visible');
    cy.get('[data-testid="stats-staff"], .card').contains('Funcionários').should('be.visible');
    cy.get('[data-testid="stats-appointments"], .card').contains('Agendamentos Hoje').should('be.visible');
  });

  it('displays statistics numbers', () => {
    // Aguarda o carregamento das estatísticas
    cy.wait(2000);

    // Verifica se os números das estatísticas são exibidos
    cy.get('.display-4').should('have.length.at.least', 4);
    
    // Verifica se não há mensagens de erro
    cy.contains('Erro ao carregar estatísticas').should('not.exist');
  });

  it('shows action buttons section', () => {
    // Verifica se a seção de ações rápidas está presente
    cy.contains('h5', 'Ações Rápidas').should('be.visible');
    
    // Verifica se os botões de ação estão presentes e funcionais
    cy.contains('Novo Agendamento').should('be.visible').and('have.attr', 'href');
    cy.contains('Gerenciar Clientes').should('be.visible').and('have.attr', 'href');
    cy.contains('Gerenciar Serviços').should('be.visible').and('have.attr', 'href');
  });

  it('shows summary section', () => {
    // Verifica se a seção de resumo total está presente
    cy.contains('h5', 'Resumo Total').should('be.visible');
    
    // Verifica se as informações do resumo estão presentes
    cy.contains('Clientes Cadastrados:').should('be.visible');
    cy.contains('Serviços Disponíveis:').should('be.visible');
    cy.contains('Equipe Ativa:').should('be.visible');
  });

  it('handles loading state', () => {
    // Recarrega a página para testar o estado de carregamento
    cy.reload();
    
    // Verifica se o spinner de carregamento aparece (se implementado)
    cy.get('.loading-spinner, .spinner-border').should('exist');
    
    // Aguarda o carregamento completar
    cy.wait(3000);
    
    // Verifica se o conteúdo é exibido após o carregamento
    cy.contains('h1', 'Dashboard').should('be.visible');
  });

  it('responsive design works correctly', () => {
    // Testa em diferentes tamanhos de tela
    cy.viewport('iphone-6');
    cy.contains('h1', 'Dashboard').should('be.visible');
    
    cy.viewport('ipad-2');
    cy.contains('h1', 'Dashboard').should('be.visible');
    
    cy.viewport(1280, 720);
    cy.contains('h1', 'Dashboard').should('be.visible');
  });
});
