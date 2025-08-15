describe('Customers Management', () => {
  beforeEach(() => {
    cy.visit('/customers');
  });

  it('loads customers page correctly', () => {
    // Verifica se a página de clientes carrega corretamente
    cy.contains('h1', 'Clientes').should('be.visible');
    cy.contains('Gerenciar clientes do salão').should('be.visible');
  });

  it('displays customers list', () => {
    // Verifica se a lista de clientes é exibida
    cy.get('table, .table').should('be.visible');
    
    // Verifica se as colunas da tabela estão presentes
    cy.contains('th, .table-header', 'Nome').should('be.visible');
    cy.contains('th, .table-header', 'Email').should('be.visible');
    cy.contains('th, .table-header', 'Telefone').should('be.visible');
    cy.contains('th, .table-header', 'Ações').should('be.visible');
  });

  it('shows add customer button', () => {
    // Verifica se o botão de adicionar cliente está presente
    cy.contains('button, .btn', 'Novo Cliente').should('be.visible');
  });

  it('opens add customer modal', () => {
    // Clica no botão de novo cliente
    cy.contains('button, .btn', 'Novo Cliente').click();
    
    // Verifica se o modal é aberto
    cy.get('.modal, [role="dialog"]').should('be.visible');
    cy.contains('Novo Cliente').should('be.visible');
    
    // Verifica se os campos do formulário estão presentes
    cy.get('input[name="name"], input[placeholder*="Nome"]').should('be.visible');
    cy.get('input[name="email"], input[placeholder*="Email"]').should('be.visible');
    cy.get('input[name="phone"], input[placeholder*="Telefone"]').should('be.visible');
    cy.get('textarea[name="address"], textarea[placeholder*="Endereço"]').should('be.visible');
  });

  it('can close add customer modal', () => {
    // Abre o modal
    cy.contains('button, .btn', 'Novo Cliente').click();
    cy.get('.modal, [role="dialog"]').should('be.visible');
    
    // Fecha o modal usando o botão Cancelar
    cy.contains('button', 'Cancelar').click();
    cy.get('.modal, [role="dialog"]').should('not.exist');
  });

  it('validates required fields in add customer form', () => {
    // Abre o modal
    cy.contains('button, .btn', 'Novo Cliente').click();
    
    // Tenta salvar sem preencher campos obrigatórios
    cy.contains('button', 'Salvar').click();
    
    // Verifica se mensagens de validação aparecem
    cy.get('.is-invalid, .error, .text-danger').should('exist');
  });

  it('can search customers', () => {
    // Verifica se o campo de busca está presente
    cy.get('input[placeholder*="Buscar"], input[type="search"]').should('be.visible');
    
    // Testa a funcionalidade de busca
    cy.get('input[placeholder*="Buscar"], input[type="search"]').type('Maria');
    
    // Aguarda os resultados da busca
    cy.wait(1000);
  });

  it('displays customer actions', () => {
    // Aguarda o carregamento da lista
    cy.wait(2000);
    
    // Verifica se existem botões de ação para os clientes
    cy.get('button[title*="Editar"], .btn-warning, .fa-edit').should('exist');
    cy.get('button[title*="Excluir"], .btn-danger, .fa-trash').should('exist');
  });

  it('handles empty state', () => {
    // Se não houver clientes, verifica se uma mensagem apropriada é exibida
    cy.get('tbody tr').then($rows => {
      if ($rows.length === 0) {
        cy.contains('Nenhum cliente encontrado').should('be.visible');
      }
    });
  });

  it('responsive design works correctly', () => {
    // Testa em diferentes tamanhos de tela
    cy.viewport('iphone-6');
    cy.contains('h1', 'Clientes').should('be.visible');
    
    cy.viewport('ipad-2');
    cy.contains('h1', 'Clientes').should('be.visible');
    
    cy.viewport(1280, 720);
    cy.contains('h1', 'Clientes').should('be.visible');
  });
});
