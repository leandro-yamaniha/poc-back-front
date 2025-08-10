import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Table, Button, Modal, Form, InputGroup } from 'react-bootstrap';
import { customersAPI } from '../services/api';
import { toast } from 'react-toastify';

function Customers() {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingCustomer, setEditingCustomer] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    address: ''
  });

  useEffect(() => {
    loadCustomers();
  }, []);

  const loadCustomers = async () => {
    try {
      setLoading(true);
      const response = await customersAPI.getAll();
      setCustomers(response.data);
    } catch (error) {
      console.error('Error loading customers:', error);
      toast.error('Erro ao carregar clientes');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    if (searchTerm.trim()) {
      try {
        const response = await customersAPI.search(searchTerm);
        setCustomers(response.data);
      } catch (error) {
        console.error('Error searching customers:', error);
        toast.error('Erro ao buscar clientes');
      }
    } else {
      loadCustomers();
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingCustomer) {
        await customersAPI.update(editingCustomer.id, formData);
        toast.success('Cliente atualizado com sucesso!');
      } else {
        await customersAPI.create(formData);
        toast.success('Cliente criado com sucesso!');
      }
      setShowModal(false);
      setEditingCustomer(null);
      setFormData({ name: '', email: '', phone: '', address: '' });
      loadCustomers();
    } catch (error) {
      console.error('Error saving customer:', error);
      toast.error('Erro ao salvar cliente');
    }
  };

  const handleEdit = (customer) => {
    setEditingCustomer(customer);
    setFormData({
      name: customer.name,
      email: customer.email,
      phone: customer.phone,
      address: customer.address
    });
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este cliente?')) {
      try {
        await customersAPI.delete(id);
        toast.success('Cliente excluído com sucesso!');
        loadCustomers();
      } catch (error) {
        console.error('Error deleting customer:', error);
        toast.error('Erro ao excluir cliente');
      }
    }
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setEditingCustomer(null);
    setFormData({ name: '', email: '', phone: '', address: '' });
  };

  if (loading) {
    return (
      <Container>
        <div className="loading-spinner">
          <div className="spinner-border text-primary" role="status">
            <span className="visually-hidden">Carregando...</span>
          </div>
        </div>
      </Container>
    );
  }

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
            <Button variant="outline-primary" onClick={handleSearch}>
              Buscar
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
          {customers.length === 0 ? (
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
                        >
                          Editar
                        </Button>
                        <Button
                          variant="outline-danger"
                          size="sm"
                          onClick={() => handleDelete(customer.id)}
                        >
                          Excluir
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
        <Form onSubmit={handleSubmit}>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label htmlFor="customer-name">Nome</Form.Label>
              <Form.Control
                id="customer-name"
                type="text"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                required
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label htmlFor="customer-email">Email</Form.Label>
              <Form.Control
                id="customer-email"
                type="email"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                required
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label htmlFor="customer-phone">Telefone</Form.Label>
              <Form.Control
                id="customer-phone"
                type="text"
                value={formData.phone}
                onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                required
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label htmlFor="customer-address">Endereço</Form.Label>
              <Form.Control
                id="customer-address"
                as="textarea"
                rows={3}
                value={formData.address}
                onChange={(e) => setFormData({ ...formData, address: e.target.value })}
              />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={handleCloseModal}>
              Cancelar
            </Button>
            <Button variant="primary" type="submit">
              {editingCustomer ? 'Atualizar' : 'Criar'}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </Container>
  );
}

export default Customers;
