import React, { useState, useEffect, useRef } from 'react';
import { Container, Row, Col, Card, Table, Button, Modal, Form } from 'react-bootstrap';
import { customersAPI } from '../services/api';
import { toast } from 'react-toastify';
import { useFormValidation } from '../hooks/useFormValidation';
import FormField from './FormField';
import LoadingSpinner, { TableLoading } from './LoadingSpinner';
import { useLoading } from '../contexts/LoadingContext';
import { useAccessibility } from '../hooks/useAccessibility';

function Customers() {
  const [customers, setCustomers] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [editingCustomer, setEditingCustomer] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const searchInputRef = useRef(null);
  const modalRef = useRef(null);

  // Hook de loading global
  const { withLoading, isLoading } = useLoading();
  
  // Hook de acessibilidade
  const { useFocusTrap, announceToScreenReader } = useAccessibility();

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
      try {
        const response = await customersAPI.getAll();
        setCustomers(response.data);
        announceToScreenReader(`${response.data.length} clientes carregados`);
      } catch (error) {
        console.error('Error loading customers:', error);
        toast.error('Erro ao carregar clientes');
        announceToScreenReader('Erro ao carregar clientes');
      }
    });
  };

  const handleSearch = async () => {
    if (searchTerm.trim()) {
      await withLoading('customers-search', async () => {
        try {
          const response = await customersAPI.search(searchTerm);
          setCustomers(response.data);
        } catch (error) {
          console.error('Error searching customers:', error);
          toast.error('Erro ao buscar clientes');
        }
      });
    } else {
      loadCustomers();
    }
  };

  const submitCustomer = async (customerData) => {
    await withLoading('customers-submit', async () => {
      try {
        if (editingCustomer) {
          await customersAPI.update(editingCustomer.id, customerData);
          toast.success('Cliente atualizado com sucesso!');
          announceToScreenReader('Cliente atualizado com sucesso');
        } else {
          await customersAPI.create(customerData);
          toast.success('Cliente criado com sucesso!');
          announceToScreenReader('Cliente criado com sucesso');
        }
        
        setShowModal(false);
        setEditingCustomer(null);
        resetForm();
        loadCustomers();
      } catch (error) {
        console.error('Error saving customer:', error);
        if (error.response?.status === 409) {
          toast.error('Já existe um cliente com este email');
          announceToScreenReader('Erro: Já existe um cliente com este email');
        } else {
          toast.error('Erro ao salvar cliente');
          announceToScreenReader('Erro ao salvar cliente');
        }
      }
    });
  };

  const handleEdit = (customer) => {
    setEditingCustomer(customer);
    setFormValues({
      name: customer.name,
      email: customer.email,
      phone: customer.phone,
      address: customer.address || ''
    });
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este cliente?')) {
      await withLoading('customers-delete', async () => {
        try {
          await customersAPI.delete(id);
          toast.success('Cliente excluído com sucesso!');
          announceToScreenReader('Cliente excluído com sucesso');
          loadCustomers();
        } catch (error) {
          console.error('Error deleting customer:', error);
          toast.error('Erro ao excluir cliente');
          announceToScreenReader('Erro ao excluir cliente');
        }
      });
    }
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setEditingCustomer(null);
    resetForm();
  };

  const handleNewCustomer = () => {
    setShowModal(true);
  };

  const isLoadingList = isLoading('customers-list');
  const isSearching = isLoading('customers-search');
  const isDeleting = isLoading('customers-delete');
  const isSubmittingForm = isSubmitting;
  const isFormValid = Object.keys(errors).length === 0;

  const filteredCustomers = customers.filter(customer => 
    customer.name.toLowerCase().includes(searchTerm.toLowerCase()) || 
    customer.email.toLowerCase().includes(searchTerm.toLowerCase())
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
              ref={searchInputRef}
              type="text"
              placeholder="Digite o nome ou email..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              aria-describedby="search-help"
            />
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
          >
            Novo Cliente
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
            <div className="empty-state">
              <div className="display-1">👥</div>
              <h3>Nenhum cliente encontrado</h3>
              <p>Comece adicionando seu primeiro cliente!</p>
            </div>
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
}

export default Customers;
