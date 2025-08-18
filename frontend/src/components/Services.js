import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Table, Button, Modal, Form, Badge } from 'react-bootstrap';
import { servicesAPI } from '../services/api';
import { toast } from 'react-toastify';
import LoadingSpinner, { TableLoading } from './LoadingSpinner';
import { useLoading } from '../contexts/LoadingContext';

function Services() {
  const [services, setServices] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [editingService, setEditingService] = useState(null);
  
  // Hook de loading global
  const { withLoading, isLoading } = useLoading();
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    duration: '',
    price: '',
    category: '',
    isActive: true
  });

  const categories = ['Cabelo', 'Unhas', 'Estética', 'Massagem', 'Depilação'];

  useEffect(() => {
    loadServices();
  }, []);

  const loadServices = async () => {
    await withLoading('services-list', async () => {
      try {
        const response = await servicesAPI.getAll();
        setServices(response.data);
      } catch (error) {
        console.error('Error loading services:', error);
        toast.error('Erro ao carregar serviços');
      }
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    await withLoading('services-submit', async () => {
      try {
        const serviceData = {
          ...formData,
          duration: parseInt(formData.duration),
          price: parseFloat(formData.price)
        };

        if (editingService) {
          await servicesAPI.update(editingService.id, serviceData);
          toast.success('Serviço atualizado com sucesso!');
        } else {
          await servicesAPI.create(serviceData);
          toast.success('Serviço criado com sucesso!');
        }
        setShowModal(false);
        setEditingService(null);
        setFormData({
          name: '',
          description: '',
          duration: '',
          price: '',
          category: '',
          isActive: true
        });
        loadServices();
      } catch (error) {
        console.error('Error saving service:', error);
        toast.error('Erro ao salvar serviço');
      }
    });
  };

  const handleEdit = (service) => {
    setEditingService(service);
    setFormData({
      name: service.name,
      description: service.description,
      duration: service.duration.toString(),
      price: service.price.toString(),
      category: service.category,
      isActive: service.isActive
    });
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este serviço?')) {
      await withLoading('services-delete', async () => {
        try {
          await servicesAPI.delete(id);
          toast.success('Serviço excluído com sucesso!');
          loadServices();
        } catch (error) {
          console.error('Error deleting service:', error);
          toast.error('Erro ao excluir serviço');
        }
      });
    }
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setEditingService(null);
    setFormData({
      name: '',
      description: '',
      duration: '',
      price: '',
      category: '',
      isActive: true
    });
  };

  const formatPrice = (price) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(price);
  };

  const isLoadingList = isLoading('services-list');
  const isSubmitting = isLoading('services-submit');
  const isDeleting = isLoading('services-delete');

  return (
    <Container>
      <div className="page-header">
        <h1>Serviços</h1>
        <p>Gerencie os serviços oferecidos pelo seu salão</p>
      </div>

      <Row className="mb-4">
        <Col className="text-end">
          <Button variant="primary" onClick={() => setShowModal(true)}>
            Novo Serviço
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
                    <th>Categoria</th>
                    <th>Duração</th>
                    <th>Preço</th>
                    <th>Status</th>
                    <th>Ações</th>
                  </tr>
                </thead>
                <TableLoading rows={5} columns={6} />
              </Table>
            </div>
          ) : services.length === 0 ? (
            <div className="empty-state">
              <div className="display-1">✨</div>
              <h3>Nenhum serviço encontrado</h3>
              <p>Comece adicionando seus primeiros serviços!</p>
            </div>
          ) : (
            <div className="table-responsive">
              <Table hover>
                <thead>
                  <tr>
                    <th>Nome</th>
                    <th>Categoria</th>
                    <th>Duração</th>
                    <th>Preço</th>
                    <th>Status</th>
                    <th>Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {services.map((service) => (
                    <tr key={service.id}>
                      <td>
                        <strong>{service.name}</strong>
                        <br />
                        <small className="text-muted">{service.description}</small>
                      </td>
                      <td>
                        <Badge bg="info">{service.category}</Badge>
                      </td>
                      <td>{service.duration} min</td>
                      <td>{formatPrice(service.price)}</td>
                      <td>
                        <Badge bg={service.isActive ? 'success' : 'secondary'}>
                          {service.isActive ? 'Ativo' : 'Inativo'}
                        </Badge>
                      </td>
                      <td>
                        <Button
                          variant="outline-primary"
                          size="sm"
                          className="me-2"
                          onClick={() => handleEdit(service)}
                          disabled={isDeleting}
                        >
                          Editar
                        </Button>
                        <Button
                          variant="outline-danger"
                          size="sm"
                          onClick={() => handleDelete(service.id)}
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

      <Modal show={showModal} onHide={handleCloseModal} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>
            {editingService ? 'Editar Serviço' : 'Novo Serviço'}
          </Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleSubmit}>
          <Modal.Body>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label htmlFor="service-name">Nome</Form.Label>
                  <Form.Control
                    type="text"
                    id="service-name"
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    required
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label htmlFor="service-category">Categoria</Form.Label>
                  <Form.Select
                    id="service-category"
                    value={formData.category}
                    onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                    required
                  >
                    <option value="">Selecione uma categoria</option>
                    {categories.map((category) => (
                      <option key={category} value={category}>
                        {category}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>
              </Col>
            </Row>
            <Form.Group className="mb-3">
              <Form.Label htmlFor="service-description">Descrição</Form.Label>
              <Form.Control
                as="textarea"
                id="service-description"
                rows={3}
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              />
            </Form.Group>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label htmlFor="service-duration">Duração (minutos)</Form.Label>
                  <Form.Control
                    type="number"
                    id="service-duration"
                    value={formData.duration}
                    onChange={(e) => setFormData({ ...formData, duration: e.target.value })}
                    required
                    min="1"
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label htmlFor="service-price">Preço (R$)</Form.Label>
                  <Form.Control
                    type="number"
                    id="service-price"
                    step="0.01"
                    value={formData.price}
                    onChange={(e) => setFormData({ ...formData, price: e.target.value })}
                    required
                    min="0"
                  />
                </Form.Group>
              </Col>
            </Row>
            <Form.Group className="mb-3">
              <Form.Check
                type="checkbox"
                id="service-active"
                label="Serviço ativo"
                checked={formData.isActive}
                onChange={(e) => setFormData({ ...formData, isActive: e.target.checked })}
              />
            </Form.Group>
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
                  {editingService ? 'Atualizando...' : 'Criando...'}
                </>
              ) : (
                editingService ? 'Atualizar' : 'Criar'
              )}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </Container>
  );
}

export default Services;
