import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Table, Button, Modal, Form, InputGroup } from 'react-bootstrap';
import { customersAPI } from '../services/api';
import { toast } from 'react-toastify';
import useFormValidation from '../hooks/useFormValidation';
import FormField from './FormField';
import LoadingSpinner, { TableLoading } from './LoadingSpinner';
import { useLoading } from '../contexts/LoadingContext';

function Customers() {
  const [customers, setCustomers] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [editingCustomer, setEditingCustomer] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  
  // Hook de loading global
  const { withLoading, isLoading } = useLoading();

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
    handleSubmit: handleFormSubmit,
    reset,
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
      } catch (error) {
        console.error('Error loading customers:', error);
        toast.error('Erro ao carregar clientes');
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

  const submitCustomer = async (values) => {
    try {
      if (editingCustomer) {
        await customersAPI.update(editingCustomer.id, values);
        toast.success('Cliente atualizado com sucesso!');
      } else {
        await customersAPI.create(values);
        toast.success('Cliente criado com sucesso!');
      }
      setShowModal(false);
      setEditingCustomer(null);
      reset();
      loadCustomers();
    } catch (error) {
      console.error('Error saving customer:', error);
      if (error.response?.status === 409) {
        toast.error('Email já cadastrado');
      } else {
        toast.error('Erro ao salvar cliente');
      }
    }
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
          loadCustomers();
        } catch (error) {
          console.error('Error deleting customer:', error);
          toast.error('Erro ao excluir cliente');
        }
      });
    }
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setEditingCustomer(null);
    reset();
  };

  const isLoadingList = isLoading('customers-list');
  const isSearching = isLoading('customers-search');
  const isDeleting = isLoading('customers-delete');

  return (
    <Container>
      <div className="page-header">
        <h1>Clientes</h1>
        <p>Gerencie os clientes do seu salão</p>
      </div>

      <Row className="mb-4">
        <Col md={8}>
          <InputGroup>
            <Form.Control
              type="text"
              placeholder="Buscar clientes por nome..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
            />
            <Button 
              variant="outline-primary" 
              onClick={handleSearch}
              disabled={isSearching}
            >
              {isSearching ? (
                <>
                  <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                  Buscando...
                </>
              ) : (
                'Buscar'
              )}
            </Button>
          </InputGroup>
        </Col>
        <Col md={4} className="text-end">
          <Button variant="primary" onClick={() => setShowModal(true)}>
            Novo Cliente
          </Button>
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
          ) : customers.length === 0 ? (
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
                  {customers.map((customer) => (
                    <tr key={customer.id}>
                      <td>{customer.name}</td>
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
                        >
                          Editar
                        </Button>
                        <Button
                          variant="outline-danger"
                          size="sm"
                          onClick={() => handleDelete(customer.id)}
                          disabled={isDeleting}
                        >
                          {isDeleting ? (
                            <>
                              <span className="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
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

      <Modal show={showModal} onHide={handleCloseModal}>
        <Modal.Header closeButton>
          <Modal.Title>
            {editingCustomer ? 'Editar Cliente' : 'Novo Cliente'}
          </Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleFormSubmit(submitCustomer)}>
          <Modal.Body>
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
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={handleCloseModal}>
              Cancelar
            </Button>
            <Button 
              variant="primary" 
              type="submit"
              disabled={isSubmitting}
            >
              {isSubmitting ? (
                <>
                  <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                  {editingCustomer ? 'Atualizando...' : 'Criando...'}
                </>
              ) : (
                editingCustomer ? 'Atualizar' : 'Criar'
              )}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </Container>
  );
}

export default Customers;
