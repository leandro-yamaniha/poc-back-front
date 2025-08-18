import React, { useState, useEffect, useRef, useMemo, useCallback } from 'react';
import { Container, Row, Col, Card, Table, Button, Modal, Form } from 'react-bootstrap';
import { customersAPI } from '../services/api';
import { toast } from 'react-toastify';
import { useFormValidation } from '../hooks/useFormValidation';
import FormField from './FormField';
import LoadingSpinner, { TableLoading } from './LoadingSpinner';
import { useLoading } from '../contexts/LoadingContext';
import { useAccessibility } from '../hooks/useAccessibility';
import { useErrorHandling } from '../hooks/useErrorHandling';
import { NetworkErrorFallback, EmptyStateWithError } from './ErrorFallbacks';
import { useDebouncedSearch } from '../hooks/useDebounce';
import { useOptimizedFilter, usePerformanceMonitor, useCleanup } from '../hooks/usePerformance';

const Customers = React.memo(() => {
  const [customers, setCustomers] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [editingCustomer, setEditingCustomer] = useState(null);
  const searchInputRef = useRef(null);

  // Hook de loading global
  const { withLoading, isLoading } = useLoading();
  
  // Hook de acessibilidade
  const { useFocusTrap, announceToScreenReader } = useAccessibility();
  const modalRef = useFocusTrap(showModal);
  
  // Hook de tratamento de erros
  const { handleApiCall, showErrorWithActions, isRetrying } = useErrorHandling();
  
  // Hooks de performance
  const { addCleanup } = useCleanup();
  usePerformanceMonitor('Customers', [customers.length, showModal]);

  // Regras de validação para o formulário
  const validationRules = {
    name: {
      required: true,
      minLength: 2,
      maxLength: 100,
      requiredMessage: 'Nome é obrigatório',
      minLengthMessage: 'Nome deve ter pelo menos 2 caracteres',
      maxLengthMessage: 'Nome deve ter no máximo 100 caracteres'
    },
    email: {
      required: true,
      email: true,
      maxLength: 150,
      requiredMessage: 'Email é obrigatório',
      emailMessage: 'Email inválido',
      maxLengthMessage: 'Email deve ter no máximo 150 caracteres'
    },
    phone: {
      required: true,
      phone: true,
      requiredMessage: 'Telefone é obrigatório',
      phoneMessage: 'Telefone inválido (formato: (11) 99999-9999)'
    },
    address: {
      maxLength: 255,
      maxLengthMessage: 'Endereço deve ter no máximo 255 caracteres'
    }
  };

  // Hook de validação de formulário
  const {
    values: formData,
    errors,
    touched,
    isSubmitting,
    handleChange,
    handleBlur,
    handleSubmit,
    reset: resetForm,
    setFormValues
  } = useFormValidation({
    name: '',
    email: '',
    phone: '',
    address: ''
  }, validationRules);

  useEffect(() => {
    loadCustomers();
  }, []);

  const loadCustomers = async () => {
    await withLoading('customers-list', async () => {
      await handleApiCall(
        () => customersAPI.getAll(),
        {
          onRetry: (error, attempt) => {
            announceToScreenReader(`Tentativa ${attempt} de carregar clientes`);
          },
          fallback: async () => {
            // Fallback: tentar carregar dados do cache local se disponível
            const cachedData = localStorage.getItem('customers-cache');
            if (cachedData) {
              const parsed = JSON.parse(cachedData);
              toast.info('Carregando dados do cache local');
              return { data: parsed };
            }
            throw new Error('Nenhum dado em cache disponível');
          }
        }
      ).then(response => {
        setCustomers(response.data);
        announceToScreenReader(`${response.data.length} clientes carregados`);
        
        // Cache dos dados para fallback futuro
        localStorage.setItem('customers-cache', JSON.stringify(response.data));
      }).catch(error => {
        announceToScreenReader('Erro ao carregar clientes');
        
        // Mostrar erro com ações de recuperação
        showErrorWithActions(error, [
          {
            label: 'Tentar Novamente',
            handler: loadCustomers
          },
          {
            label: 'Trabalhar Offline',
            handler: () => {
              const cachedData = localStorage.getItem('customers-cache');
              if (cachedData) {
                setCustomers(JSON.parse(cachedData));
                toast.info('Modo offline ativado');
              }
            }
          }
        ]);
      });
    });
  };

  // Busca otimizada com debounce
  const performSearch = useCallback(async (term) => {
    if (term.trim()) {
      await withLoading('customers-search', async () => {
        await handleApiCall(
          () => customersAPI.search(term),
          {
            maxRetries: 2,
            fallback: async () => {
              // Fallback: filtrar dados locais
              const filtered = customers.filter(customer => 
                customer.name.toLowerCase().includes(term.toLowerCase()) ||
                customer.email.toLowerCase().includes(term.toLowerCase())
              );
              return { data: filtered };
            }
          }
        ).then(response => {
          setCustomers(response.data);
        });
      });
    } else {
      loadCustomers();
    }
  }, [customers, withLoading, handleApiCall]);
  
  const { searchTerm, isSearching, handleSearchChange } = useDebouncedSearch(performSearch, 500);

  const submitCustomer = async (customerData) => {
    await withLoading('customers-submit', async () => {
      await handleApiCall(
        () => {
          if (editingCustomer) {
            return customersAPI.update(editingCustomer.id, customerData);
          } else {
            return customersAPI.create(customerData);
          }
        },
        {
          maxRetries: 2,
          onRetry: (error, attempt) => {
            const action = editingCustomer ? 'atualizar' : 'criar';
            announceToScreenReader(`Tentativa ${attempt} de ${action} cliente`);
          },
          fallback: async (error) => {
            // Fallback: salvar localmente para sincronizar depois
            const pendingData = {
              ...customerData,
              id: editingCustomer?.id || Date.now().toString(),
              _pending: true,
              _action: editingCustomer ? 'update' : 'create'
            };
            
            const pending = JSON.parse(localStorage.getItem('pending-customers') || '[]');
            pending.push(pendingData);
            localStorage.setItem('pending-customers', JSON.stringify(pending));
            
            toast.info('Dados salvos localmente. Serão sincronizados quando a conexão for restaurada.');
            return { data: pendingData };
          }
        }
      ).then(() => {
        const action = editingCustomer ? 'atualizado' : 'criado';
        toast.success(`Cliente ${action} com sucesso!`);
        announceToScreenReader(`Cliente ${action} com sucesso`);
        
        setShowModal(false);
        setEditingCustomer(null);
        resetForm();
        loadCustomers();
      }).catch(error => {
        announceToScreenReader('Erro ao salvar cliente');
        
        if (error.response?.status === 409) {
          showErrorWithActions(error, [
            {
              label: 'Usar Email Diferente',
              handler: () => {
                // Focar no campo email para correção
                const emailField = document.querySelector('input[name="email"]');
                if (emailField) emailField.focus();
              }
            }
          ]);
        }
      });
    });
  };

  const handleEdit = useCallback((customer) => {
    setEditingCustomer(customer);
    setFormValues({
      name: customer.name,
      email: customer.email,
      phone: customer.phone,
      address: customer.address || ''
    });
    setShowModal(true);
  }, [setFormValues]);

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este cliente?')) {
      await withLoading('customers-delete', async () => {
        await handleApiCall(
          () => customersAPI.delete(id),
          {
            maxRetries: 1,
            onRetry: () => {
              announceToScreenReader('Tentando excluir cliente novamente');
            }
          }
        ).then(() => {
          toast.success('Cliente excluído com sucesso!');
          announceToScreenReader('Cliente excluído com sucesso');
          loadCustomers();
        }).catch(error => {
          announceToScreenReader('Erro ao excluir cliente');
          
          showErrorWithActions(error, [
            {
              label: 'Tentar Novamente',
              handler: () => handleDelete(id)
            },
            {
              label: 'Marcar para Exclusão',
              handler: () => {
                // Marcar localmente para exclusão posterior
                const toDelete = JSON.parse(localStorage.getItem('pending-deletions') || '[]');
                toDelete.push({ type: 'customer', id, timestamp: Date.now() });
                localStorage.setItem('pending-deletions', JSON.stringify(toDelete));
                toast.info('Cliente marcado para exclusão quando a conexão for restaurada.');
              }
            }
          ]);
        });
      });
    }
  };

  const handleCloseModal = useCallback(() => {
    setShowModal(false);
    setEditingCustomer(null);
    resetForm();
  }, [resetForm]);

  const handleNewCustomer = useCallback(() => {
    setEditingCustomer(null);
    resetForm();
    setShowModal(true);
  }, [resetForm]);

  // Estados memoizados para evitar re-renders
  const loadingStates = useMemo(() => ({
    isLoadingList: isLoading('customers-list'),
    isSearchingAPI: isLoading('customers-search'),
    isDeleting: isLoading('customers-delete'),
    isSubmittingForm: isSubmitting,
    isFormValid: Object.keys(errors).length === 0,
    hasNetworkError: customers.length === 0 && !isLoading('customers-list')
  }), [isLoading, isSubmitting, errors, customers.length]);
  
  const { isLoadingList, isSearchingAPI, isDeleting, isSubmittingForm, isFormValid, hasNetworkError } = loadingStates;

  // Filtros otimizados com memoização
  const filteredCustomers = useOptimizedFilter(
    customers,
    { search: searchTerm },
    {
      searchFields: ['name', 'email', 'phone'],
      caseSensitive: false
    }
  );

  return (
    <Container>
      <div className="page-header">
        <h1 id="page-title">Clientes</h1>
        <p>Gerencie os clientes do seu salão</p>
      </div>

      <Row className="mb-4">
        <Col md={6}>
          <Form.Group>
            <Form.Label htmlFor="search-customers">Buscar clientes:</Form.Label>
            <Form.Control
              id="search-customers"
              type="text"
              placeholder="Buscar por nome ou email..."
              value={searchTerm}
              onChange={(e) => handleSearchChange(e.target.value)}
              ref={searchInputRef}
              aria-describedby="search-help"
            />
            {isSearching && (
              <div className="position-absolute end-0 top-50 translate-middle-y me-3">
                <div className="spinner-border spinner-border-sm text-primary" role="status">
                  <span className="visually-hidden">Buscando...</span>
                </div>
              </div>
            )}
            <Form.Text id="search-help" className="sr-only">
              Digite para filtrar clientes por nome ou email
            </Form.Text>
          </Form.Group>
        </Col>
        <Col md={6} className="text-end d-flex align-items-end">
          <Button 
            variant="primary" 
            onClick={handleNewCustomer}
            aria-describedby="new-customer-help"
            disabled={isRetrying}
          >
            {isRetrying ? (
              <>
                <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                Reconectando...
              </>
            ) : (
              'Novo Cliente'
            )}
          </Button>
          <span id="new-customer-help" className="sr-only">
            Abre formulário para cadastrar novo cliente
          </span>
        </Col>
      </Row>

      <Card>
        <Card.Body>
          {isLoadingList ? (
            <div className="table-responsive">
              <Table hover>
                <thead>
                  <tr>
                    <th>Nome</th>
                    <th>Email</th>
                    <th>Telefone</th>
                    <th>Endereço</th>
                    <th>Ações</th>
                  </tr>
                </thead>
                <TableLoading rows={5} columns={5} />
              </Table>
            </div>
          ) : filteredCustomers.length === 0 ? (
            hasNetworkError ? (
              <NetworkErrorFallback 
                onRetry={loadCustomers}
                onOfflineMode={() => {
                  const cachedData = localStorage.getItem('customers-cache');
                  if (cachedData) {
                    setCustomers(JSON.parse(cachedData));
                    toast.info('Modo offline ativado');
                  }
                }}
              />
            ) : (
              <EmptyStateWithError
                title="Nenhum cliente encontrado"
                message="Comece adicionando seu primeiro cliente!"
                onRetry={loadCustomers}
                onCreate={handleNewCustomer}
              />
            )
          ) : (
            <div className="table-responsive">
              <Table hover>
                <thead>
                  <tr>
                    <th>Nome</th>
                    <th>Email</th>
                    <th>Telefone</th>
                    <th>Endereço</th>
                    <th>Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredCustomers.map((customer) => (
                    <tr key={customer.id}>
                      <th scope="row">{customer.name}</th>
                      <td>{customer.email}</td>
                      <td>{customer.phone}</td>
                      <td>{customer.address}</td>
                      <td>
                        <Button
                          variant="outline-primary"
                          size="sm"
                          className="me-2"
                          onClick={() => handleEdit(customer)}
                          disabled={isDeleting}
                          aria-label={`Editar cliente ${customer.name}`}
                        >
                          Editar
                        </Button>
                        <Button
                          variant="outline-danger"
                          size="sm"
                          onClick={() => handleDelete(customer.id)}
                          disabled={isDeleting}
                          aria-label={`Excluir cliente ${customer.name}`}
                        >
                          {isDeleting ? (
                            <>
                              <span className="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
                              <span className="sr-only">Excluindo cliente...</span>
                              Excluindo...
                            </>
                          ) : (
                            'Excluir'
                          )}
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </div>
          )}
        </Card.Body>
      </Card>

      <Modal 
        show={showModal} 
        onHide={handleCloseModal} 
        size="lg" 
        animation={false}
        aria-labelledby="customer-modal-title"
        aria-describedby="customer-modal-description"
      >
        <div ref={modalRef}>
          <Modal.Header closeButton>
            <Modal.Title id="customer-modal-title">
              {editingCustomer ? 'Editar Cliente' : 'Novo Cliente'}
            </Modal.Title>
          </Modal.Header>
          <Form onSubmit={handleSubmit} noValidate role="form">
            <Modal.Body>
              <p id="customer-modal-description" className="sr-only">
                {editingCustomer ? 'Formulário para editar dados do cliente' : 'Formulário para cadastrar novo cliente'}
              </p>
              <Row>
                <FormField
                  label="Nome"
                  name="name"
                  type="text"
                  value={formData.name}
                  error={errors.name}
                  touched={touched.name}
                  onChange={handleChange}
                  onBlur={handleBlur}
                  required
                  placeholder="Digite o nome completo"
                />
                
                <FormField
                  label="Email"
                  name="email"
                  type="email"
                  value={formData.email}
                  error={errors.email}
                  touched={touched.email}
                  onChange={handleChange}
                  onBlur={handleBlur}
                  required
                  placeholder="Digite o email"
                />
                
                <FormField
                  label="Telefone"
                  name="phone"
                  type="tel"
                  value={formData.phone}
                  error={errors.phone}
                  touched={touched.phone}
                  onChange={handleChange}
                  onBlur={handleBlur}
                  required
                  placeholder="(11) 99999-9999"
                />
                
                <FormField
                  label="Endereço"
                  name="address"
                  as="textarea"
                  rows={3}
                  value={formData.address}
                  error={errors.address}
                  touched={touched.address}
                  onChange={handleChange}
                  onBlur={handleBlur}
                  placeholder="Digite o endereço completo (opcional)"
                />
              </Row>
            </Modal.Body>
            <Modal.Footer>
              <Button variant="secondary" onClick={handleCloseModal}>
                Cancelar
              </Button>
              <Button 
                variant="primary" 
                type="submit"
                disabled={isSubmittingForm || !isFormValid}
                aria-describedby="submit-help"
              >
                {isSubmittingForm ? (
                  <>
                    <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                    <span className="sr-only">Processando...</span>
                    {editingCustomer ? 'Atualizando...' : 'Criando...'}
                  </>
                ) : (
                  editingCustomer ? 'Atualizar' : 'Criar'
                )}
              </Button>
              <span id="submit-help" className="sr-only">
                {editingCustomer ? 'Salva as alterações do cliente' : 'Cadastra o novo cliente'}
              </span>
            </Modal.Footer>
          </Form>
        </div>
      </Modal>
    </Container>
  );
});

Customers.displayName = 'Customers';

export default Customers;
