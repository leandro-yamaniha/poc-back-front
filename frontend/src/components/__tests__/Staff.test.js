import React from 'react';
import { render, screen, fireEvent, waitFor, within } from '@testing-library/react';
import { BrowserRouter as Router } from 'react-router-dom';
import { toast } from 'react-toastify';
import '@testing-library/jest-dom';
import Staff from '../Staff';
import * as api from '../../services/api';
import { LoadingProvider } from '../../contexts/LoadingContext';

// Mock the API and toast
jest.mock('../../services/api', () => ({
  ...jest.requireActual('../../services/api'),
  staffAPI: {
    getAll: jest.fn(),
    create: jest.fn(),
    update: jest.fn(),
    delete: jest.fn(),
  }
}));

jest.mock('react-toastify', () => ({
  toast: {
    success: jest.fn(),
    error: jest.fn(),
    info: jest.fn(),
  },
}));


// Sample staff data for testing
const mockStaff = [
  {
    id: 1,
    name: 'João Silva',
    email: 'joao@example.com',
    phone: '(11) 99999-9999',
    role: 'Cabeleireiro',
    specialties: ['Corte', 'Coloração'],
    isActive: true,
  },
  {
    id: 2,
    name: 'Maria Souza',
    email: 'maria@example.com',
    phone: '(11) 88888-8888',
    role: 'Manicure',
    specialties: ['Manicure', 'Nail Art'],
    isActive: true,
  },
];

describe('Staff Component', () => {
  // Helper para renderizar com providers
  const renderWithProviders = (component) => {
    return render(
      <Router>
        <LoadingProvider>
          {component}
        </LoadingProvider>
      </Router>
    );
  };

  beforeEach(() => {
    jest.clearAllMocks();
    api.staffAPI.getAll.mockResolvedValue({ data: mockStaff });
  });

  const renderStaffComponent = () => {
    return renderWithProviders(<Staff />);
  };

  test('renders staff list with correct data', async () => {
    renderStaffComponent();

    // Check if loading spinner is shown initially
    expect(screen.getByRole('status')).toBeInTheDocument();

    // Wait for data to load and verify content
    const staffName = await screen.findByText('João Silva');
    expect(staffName).toBeInTheDocument();
    
    // Check staff member details
    expect(screen.getByText('joao@example.com')).toBeInTheDocument();
    expect(screen.getByText('(11) 99999-9999')).toBeInTheDocument();
    
    // Check role badge
    const roleBadge = screen.getByText('Cabeleireiro');
    expect(roleBadge).toBeInTheDocument();
    expect(roleBadge).toHaveClass('badge', 'bg-primary');
    
    // Check specialties
    expect(screen.getByText('Corte')).toBeInTheDocument();
    expect(screen.getByText('Coloração')).toBeInTheDocument();
    
    // Check status (there are multiple 'Ativo' elements, get the first one)
    const statusBadges = await screen.findAllByText('Ativo');
    expect(statusBadges.length).toBeGreaterThan(0);
    expect(statusBadges[0]).toBeInTheDocument();
    expect(statusBadges[0]).toHaveClass('badge', 'bg-success');
  });

  test('opens and closes the new staff modal', async () => {
    render(
      <Router>
        <Staff />
      </Router>
    );

    // Click the new staff button
    const newStaffButton = await screen.findByText('Novo Funcionário');
    fireEvent.click(newStaffButton);

    // Check if modal is open by finding the dialog and its title
    const dialog = screen.getByRole('dialog');
    expect(dialog).toBeInTheDocument();
    
    // Find the modal title within the dialog
    const modalTitle = within(dialog).getByText('Novo Funcionário');
    expect(modalTitle).toBeInTheDocument();

    // Close the modal using the close button in the header
    const closeButton = within(dialog).getByLabelText('Close');
    fireEvent.click(closeButton);

    // Check if modal is closed
    await waitFor(() => {
      expect(screen.queryByRole('dialog')).not.toBeInTheDocument();
    });
  });

  test('creates a new staff member', async () => {
    render(
      <Router>
        <Staff />
      </Router>
    );

    // Open the modal
    const newStaffButton = await screen.findByText('Novo Funcionário');
    fireEvent.click(newStaffButton);

    // Fill out the form using labels for better accessibility
    fireEvent.change(screen.getByLabelText('Nome'), { target: { value: 'Novo Funcionário' } });
    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'novo@example.com' } });
    fireEvent.change(screen.getByLabelText('Telefone'), { target: { value: '(11) 77777-7777' } });
    fireEvent.change(screen.getByLabelText('Função'), { target: { value: 'Esteticista' } });
    
    // Submit the form
    const submitButton = screen.getByText('Criar');
    fireEvent.click(submitButton);

    // Check if the API was called with the correct data
    await waitFor(() => {
      expect(api.staffAPI.create).toHaveBeenCalledWith({
        name: 'Novo Funcionário',
        email: 'novo@example.com',
        phone: '(11) 77777-7777',
        role: 'Esteticista',
        specialties: [],
        isActive: true
      });
      
      // Check if success toast was shown
      expect(toast.success).toHaveBeenCalledWith('Funcionário criado com sucesso!');
    });
    
    // Check if modal is closed after successful submission
    await waitFor(() => {
      expect(screen.queryByRole('dialog')).not.toBeInTheDocument();
    });
  });

  test('edits an existing staff member', async () => {
    render(
      <Router>
        <Staff />
      </Router>
    );

    // Wait for data to load and find the first edit button
    const editButtons = await screen.findAllByRole('button', { name: /editar/i });
    fireEvent.click(editButtons[0]);

    // Check if modal is in edit mode
    expect(screen.getByText('Editar Funcionário')).toBeInTheDocument();

    // Change the name using label
    const nameInput = screen.getByLabelText('Nome');
    fireEvent.change(nameInput, { target: { value: 'João Silva Atualizado' } });

    // Submit the form
    const updateButton = screen.getByRole('button', { name: /atualizar/i });
    fireEvent.click(updateButton);

    // Check if the API was called with the updated data
    await waitFor(() => {
      expect(api.staffAPI.update).toHaveBeenCalledWith(1, {
        name: 'João Silva Atualizado',
        email: 'joao@example.com',
        phone: '(11) 99999-9999',
        role: 'Cabeleireiro',
        specialties: ['Corte', 'Coloração'],
        isActive: true
      });
      
      // Check if success toast was shown
      expect(toast.success).toHaveBeenCalledWith('Funcionário atualizado com sucesso!');
    });
    
    // Check if modal is closed after successful update
    await waitFor(() => {
      expect(screen.queryByRole('dialog')).not.toBeInTheDocument();
    });
  });

  test('deletes a staff member after confirmation', async () => {
    // Mock window.confirm
    const originalConfirm = window.confirm;
    window.confirm = jest.fn(() => true);

    // Mock the toast.success function
    const toastSuccessSpy = jest.spyOn(toast, 'success');

    // Mock the API response
    api.staffAPI.delete.mockResolvedValueOnce({});

    render(
      <Router>
        <Staff />
      </Router>
    );

    // Wait for data to load and find the first delete button
    const deleteButtons = await screen.findAllByRole('button', { name: /excluir/i });
    fireEvent.click(deleteButtons[0]);

    // Check if confirmation was requested
    expect(window.confirm).toHaveBeenCalledWith('Tem certeza que deseja excluir este funcionário?');

    // Wait for the API call to complete
    await waitFor(() => {
      // Check if the API was called with the correct ID
      expect(api.staffAPI.delete).toHaveBeenCalledWith(1);
      
      // Check if success toast was shown
      expect(toastSuccessSpy).toHaveBeenCalledWith('Funcionário excluído com sucesso!');
    });

    // Restore original confirm
    window.confirm = originalConfirm;
    toastSuccessSpy.mockRestore();
  });

  test('handles API errors gracefully', async () => {
    // Mock a failed API call
    const errorMessage = 'Erro ao carregar funcionários';
    api.staffAPI.getAll.mockRejectedValueOnce(new Error(errorMessage));

    render(
      <Router>
        <Staff />
      </Router>
    );

    // Check if error toast was shown
    await waitFor(() => {
      expect(toast.error).toHaveBeenCalledWith('Erro ao carregar funcionários');
    });
  });
});
