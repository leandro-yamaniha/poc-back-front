describe('Services Management', () => {
  beforeEach(() => {
    cy.visit('/services');
  });

  it('loads services page correctly', () => {
    // Verifica se a página de serviços carrega corretamente
    cy.contains('h1', 'Serviços').should('be.visible');
    cy.contains('Gerenciar serviços do salão').should('be.visible');
  });

  it('displays services list', () => {
    // Verifica se a lista de serviços é exibida
    cy.get('table, .table').should('be.visible');
    
    // Verifica se as colunas da tabela estão presentes
    cy.contains('th, .table-header', 'Nome').should('be.visible');
    cy.contains('th, .table-header', 'Categoria').should('be.visible');
    cy.contains('th, .table-header', 'Preço').should('be.visible');
    cy.contains('th, .table-header', 'Duração').should('be.visible');
    cy.contains('th, .table-header', 'Status').should('be.visible');
    cy.contains('th, .table-header', 'Ações').should('be.visible');
  });

  it('shows add service button', () => {
    // Verifica se o botão de adicionar serviço está presente
    cy.contains('button, .btn', 'Novo Serviço').should('be.visible');
  });

  it('opens add service modal', () => {
    // Clica no botão de novo serviço
    cy.contains('button, .btn', 'Novo Serviço').click();
    
    // Verifica se o modal é aberto
    cy.get('.modal, [role="dialog"]').should('be.visible');
    cy.contains('Novo Serviço').should('be.visible');
    
    // Verifica se os campos do formulário estão presentes
    cy.get('input[name="name"], input[placeholder*="Nome"]').should('be.visible');
    cy.get('select[name="category"], select').should('be.visible');
    cy.get('input[name="price"], input[placeholder*="Preço"]').should('be.visible');
    cy.get('input[name="duration"], input[placeholder*="Duração"]').should('be.visible');
    cy.get('textarea[name="description"], textarea[placeholder*="Descrição"]').should('be.visible');
  });

  it('can filter services by category', () => {
    // Verifica se existe filtro por categoria
    cy.get('select[name="category"], .filter-select').should('be.visible');
    
    // Testa filtro por categoria
    cy.get('select[name="category"], .filter-select').select('CABELO');
    cy.wait(1000);
  });

  it('can filter services by status', () => {
    // Verifica se existe filtro por status
    cy.get('select[name="status"], .status-filter').should('be.visible');
    
    // Testa filtro por status ativo
    cy.get('select[name="status"], .status-filter').select('ATIVO');
    cy.wait(1000);
  });

  it('validates service form fields', () => {
    // Abre o modal
    cy.contains('button, .btn', 'Novo Serviço').click();
    
    // Tenta salvar sem preencher campos obrigatórios
    cy.contains('button', 'Salvar').click();
    
    // Verifica se mensagens de validação aparecem
    cy.get('.is-invalid, .error, .text-danger').should('exist');
  });

  it('displays service status badges', () => {
    // Aguarda o carregamento da lista
    cy.wait(2000);
    
    // Verifica se badges de status são exibidos
    cy.get('.badge, .status-badge').should('exist');
  });

  it('shows service actions', () => {
    // Aguarda o carregamento da lista
    cy.wait(2000);
    
    // Verifica se existem botões de ação para os serviços
    cy.get('button[title*="Editar"], .btn-warning, .fa-edit').should('exist');
    cy.get('button[title*="Excluir"], .btn-danger, .fa-trash').should('exist');
  });

  it('handles empty state', () => {
    // Se não houver serviços, verifica se uma mensagem apropriada é exibida
    cy.get('tbody tr').then($rows => {
      if ($rows.length === 0) {
        cy.contains('Nenhum serviço encontrado').should('be.visible');
      }
    });
  });
});
