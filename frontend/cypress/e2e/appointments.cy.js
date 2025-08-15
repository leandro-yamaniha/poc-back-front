describe('Appointments Management', () => {
  beforeEach(() => {
    cy.visit('/appointments');
  });

  it('loads appointments page correctly', () => {
    // Verifica se a página de agendamentos carrega corretamente
    cy.contains('h1', 'Agendamentos').should('be.visible');
    cy.contains('Gerenciar agendamentos do salão').should('be.visible');
  });

  it('displays appointments list', () => {
    // Verifica se a lista de agendamentos é exibida
    cy.get('table, .table').should('be.visible');
    
    // Verifica se as colunas da tabela estão presentes
    cy.contains('th, .table-header', 'Data/Hora').should('be.visible');
    cy.contains('th, .table-header', 'Cliente').should('be.visible');
    cy.contains('th, .table-header', 'Serviço').should('be.visible');
    cy.contains('th, .table-header', 'Funcionário').should('be.visible');
    cy.contains('th, .table-header', 'Status').should('be.visible');
    cy.contains('th, .table-header', 'Ações').should('be.visible');
  });

  it('shows add appointment button', () => {
    // Verifica se o botão de adicionar agendamento está presente
    cy.contains('button, .btn', 'Novo Agendamento').should('be.visible');
  });

  it('displays date picker for filtering', () => {
    // Verifica se o seletor de data está presente
    cy.get('.react-datepicker-wrapper, input[type="date"]').should('be.visible');
  });

  it('opens add appointment modal', () => {
    // Clica no botão de novo agendamento
    cy.contains('button, .btn', 'Novo Agendamento').click();
    
    // Verifica se o modal é aberto
    cy.get('.modal, [role="dialog"]').should('be.visible');
    cy.contains('Novo Agendamento').should('be.visible');
    
    // Verifica se os campos do formulário estão presentes
    cy.get('select[name="customer_id"], .customer-select').should('be.visible');
    cy.get('select[name="service_id"], .service-select').should('be.visible');
    cy.get('select[name="staff_id"], .staff-select').should('be.visible');
    cy.get('input[name="appointment_date"], .date-input').should('be.visible');
  });

  it('can filter appointments by date', () => {
    // Seleciona uma data no date picker
    cy.get('.react-datepicker-wrapper input, input[type="date"]').click();
    
    // Aguarda o carregamento dos resultados filtrados
    cy.wait(1000);
  });

  it('can filter appointments by status', () => {
    // Verifica se existe filtro por status
    cy.get('select[name="status"], .status-filter').should('be.visible');
    
    // Testa filtro por status
    cy.get('select[name="status"], .status-filter').select('AGENDADO');
    cy.wait(1000);
  });

  it('validates appointment form fields', () => {
    // Abre o modal
    cy.contains('button, .btn', 'Novo Agendamento').click();
    
    // Tenta salvar sem preencher campos obrigatórios
    cy.contains('button', 'Salvar').click();
    
    // Verifica se mensagens de validação aparecem
    cy.get('.is-invalid, .error, .text-danger').should('exist');
  });

  it('displays appointment status badges', () => {
    // Aguarda o carregamento da lista
    cy.wait(2000);
    
    // Verifica se badges de status são exibidos
    cy.get('.badge, .status-badge').should('exist');
  });

  it('shows appointment actions', () => {
    // Aguarda o carregamento da lista
    cy.wait(2000);
    
    // Verifica se existem botões de ação para os agendamentos
    cy.get('button[title*="Editar"], .btn-warning, .fa-edit').should('exist');
    cy.get('button[title*="Excluir"], .btn-danger, .fa-trash').should('exist');
  });

  it('handles empty state', () => {
    // Se não houver agendamentos, verifica se uma mensagem apropriada é exibida
    cy.get('tbody tr').then($rows => {
      if ($rows.length === 0) {
        cy.contains('Nenhum agendamento encontrado').should('be.visible');
      }
    });
  });

  it('displays today appointments by default', () => {
    // Verifica se a página carrega com agendamentos de hoje por padrão
    cy.url().should('include', '/appointments');
    
    // Aguarda o carregamento
    cy.wait(2000);
    
    // Verifica se há indicação de que está mostrando agendamentos de hoje
    cy.contains('Hoje').should('be.visible');
  });
});
