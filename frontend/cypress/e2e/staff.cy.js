describe('Staff Management', () => {
  beforeEach(() => {
    cy.visit('/staff');
  });

  it('loads staff page correctly', () => {
    // Verifica se a página de funcionários carrega corretamente
    cy.contains('h1', 'Funcionários').should('be.visible');
    cy.contains('Gerenciar funcionários do salão').should('be.visible');
  });

  it('displays staff list', () => {
    // Verifica se a lista de funcionários é exibida
    cy.get('table, .table').should('be.visible');
    
    // Verifica se as colunas da tabela estão presentes
    cy.contains('th, .table-header', 'Nome').should('be.visible');
    cy.contains('th, .table-header', 'Email').should('be.visible');
    cy.contains('th, .table-header', 'Telefone').should('be.visible');
    cy.contains('th, .table-header', 'Função').should('be.visible');
    cy.contains('th, .table-header', 'Status').should('be.visible');
    cy.contains('th, .table-header', 'Ações').should('be.visible');
  });

  it('shows add staff button', () => {
    // Verifica se o botão de adicionar funcionário está presente
    cy.contains('button, .btn', 'Novo Funcionário').should('be.visible');
  });

  it('opens add staff modal', () => {
    // Clica no botão de novo funcionário
    cy.contains('button, .btn', 'Novo Funcionário').click();
    
    // Verifica se o modal é aberto
    cy.get('.modal, [role="dialog"]').should('be.visible');
    cy.contains('Novo Funcionário').should('be.visible');
    
    // Verifica se os campos do formulário estão presentes
    cy.get('input[name="name"], input[placeholder*="Nome"]').should('be.visible');
    cy.get('input[name="email"], input[placeholder*="Email"]').should('be.visible');
    cy.get('input[name="phone"], input[placeholder*="Telefone"]').should('be.visible');
    cy.get('select[name="role"], select').should('be.visible');
  });

  it('can filter staff by role', () => {
    // Verifica se existe filtro por função
    cy.get('select[name="role"], .role-filter').should('be.visible');
    
    // Testa filtro por função
    cy.get('select[name="role"], .role-filter').select('CABELEIREIRO');
    cy.wait(1000);
  });

  it('can filter staff by status', () => {
    // Verifica se existe filtro por status
    cy.get('select[name="status"], .status-filter').should('be.visible');
    
    // Testa filtro por status ativo
    cy.get('select[name="status"], .status-filter').select('ATIVO');
    cy.wait(1000);
  });

  it('validates staff form fields', () => {
    // Abre o modal
    cy.contains('button, .btn', 'Novo Funcionário').click();
    
    // Tenta salvar sem preencher campos obrigatórios
    cy.contains('button', 'Salvar').click();
    
    // Verifica se mensagens de validação aparecem
    cy.get('.is-invalid, .error, .text-danger').should('exist');
  });

  it('displays staff status badges', () => {
    // Aguarda o carregamento da lista
    cy.wait(2000);
    
    // Verifica se badges de status são exibidos
    cy.get('.badge, .status-badge').should('exist');
  });

  it('shows staff actions', () => {
    // Aguarda o carregamento da lista
    cy.wait(2000);
    
    // Verifica se existem botões de ação para os funcionários
    cy.get('button[title*="Editar"], .btn-warning, .fa-edit').should('exist');
    cy.get('button[title*="Excluir"], .btn-danger, .fa-trash').should('exist');
  });

  it('handles empty state', () => {
    // Se não houver funcionários, verifica se uma mensagem apropriada é exibida
    cy.get('tbody tr').then($rows => {
      if ($rows.length === 0) {
        cy.contains('Nenhum funcionário encontrado').should('be.visible');
      }
    });
  });
});
