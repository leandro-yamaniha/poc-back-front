import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Table, Button, Modal, Form, Badge } from 'react-bootstrap';
import { staffAPI } from '../services/api';
import { toast } from 'react-toastify';

function Staff() {
  const [staff, setStaff] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingStaff, setEditingStaff] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    role: '',
    specialties: [],
    isActive: true
  });

  const roles = ['Cabeleireira', 'Manicure', 'Esteticista', 'Massagista', 'Recepcionista'];
  const availableSpecialties = [
    'Corte', 'Escova', 'Coloração', 'Manicure', 'Pedicure', 'Nail Art',
    'Limpeza de Pele', 'Massagem', 'Depilação', 'Sobrancelha'
  ];

  useEffect(() => {
    loadStaff();
  }, []);

  const loadStaff = async () => {
    try {
      setLoading(true);
      const response = await staffAPI.getAll();
      setStaff(response.data);
    } catch (error) {
      console.error('Error loading staff:', error);
      toast.error('Erro ao carregar funcionários');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingStaff) {
        await staffAPI.update(editingStaff.id, formData);
        toast.success('Funcionário atualizado com sucesso!');
      } else {
        await staffAPI.create(formData);
        toast.success('Funcionário criado com sucesso!');
      }
      setShowModal(false);
      setEditingStaff(null);
      setFormData({
        name: '',
        email: '',
        phone: '',
        role: '',
        specialties: [],
        isActive: true
      });
      loadStaff();
    } catch (error) {
      console.error('Error saving staff:', error);
      toast.error('Erro ao salvar funcionário');
    }
  };

  const handleEdit = (staffMember) => {
    setEditingStaff(staffMember);
    setFormData({
      name: staffMember.name,
      email: staffMember.email,
      phone: staffMember.phone,
      role: staffMember.role,
      specialties: staffMember.specialties || [],
      isActive: staffMember.isActive
    });
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este funcionário?')) {
      try {
        await staffAPI.delete(id);
        toast.success('Funcionário excluído com sucesso!');
        loadStaff();
      } catch (error) {
        console.error('Error deleting staff:', error);
        toast.error('Erro ao excluir funcionário');
      }
    }
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setEditingStaff(null);
    setFormData({
      name: '',
      email: '',
      phone: '',
      role: '',
      specialties: [],
      isActive: true
    });
  };

  const handleSpecialtyChange = (specialty, checked) => {
    if (checked) {
      setFormData({
        ...formData,
        specialties: [...formData.specialties, specialty]
      });
    } else {
      setFormData({
        ...formData,
        specialties: formData.specialties.filter(s => s !== specialty)
      });
    }
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
        <h1>Funcionários</h1>
        <p>Gerencie a equipe do seu salão</p>
      </div>

      <Row className="mb-4">
        <Col className="text-end">
          <Button variant="primary" onClick={() => setShowModal(true)}>
            Novo Funcionário
          </Button>
        </Col>
      </Row>

      <Card>
        <Card.Body>
          {staff.length === 0 ? (
            <div className="empty-state">
              <div className="display-1">👩‍💼</div>
              <h3>Nenhum funcionário encontrado</h3>
              <p>Comece adicionando sua equipe!</p>
            </div>
          ) : (
            <div className="table-responsive">
              <Table hover>
                <thead>
                  <tr>
                    <th>Nome</th>
                    <th>Email</th>
                    <th>Telefone</th>
                    <th>Função</th>
                    <th>Especialidades</th>
                    <th>Status</th>
                    <th>Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {staff.map((staffMember) => (
                    <tr key={staffMember.id}>
                      <td>{staffMember.name}</td>
                      <td>{staffMember.email}</td>
                      <td>{staffMember.phone}</td>
                      <td>
                        <Badge bg="primary">{staffMember.role}</Badge>
                      </td>
                      <td>
                        {staffMember.specialties && staffMember.specialties.length > 0 ? (
                          staffMember.specialties.map((specialty, index) => (
                            <Badge key={index} bg="secondary" className="me-1">
                              {specialty}
                            </Badge>
                          ))
                        ) : (
                          <span className="text-muted">Nenhuma</span>
                        )}
                      </td>
                      <td>
                        <Badge bg={staffMember.isActive ? 'success' : 'secondary'}>
                          {staffMember.isActive ? 'Ativo' : 'Inativo'}
                        </Badge>
                      </td>
                      <td>
                        <Button
                          variant="outline-primary"
                          size="sm"
                          className="me-2"
                          onClick={() => handleEdit(staffMember)}
                        >
                          Editar
                        </Button>
                        <Button
                          variant="outline-danger"
                          size="sm"
                          onClick={() => handleDelete(staffMember.id)}
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

      <Modal show={showModal} onHide={handleCloseModal} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>
            {editingStaff ? 'Editar Funcionário' : 'Novo Funcionário'}
          </Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleSubmit}>
          <Modal.Body>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3" controlId="name">
                  <Form.Label htmlFor="staff-name">Nome</Form.Label>
                  <Form.Control
                    type="text"
                    id="staff-name"
                    data-testid="staff-name"
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    required
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3" controlId="email">
                  <Form.Label htmlFor="staff-email">Email</Form.Label>
                  <Form.Control
                    type="email"
                    id="staff-email"
                    data-testid="staff-email"
                    value={formData.email}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    required
                  />
                </Form.Group>
              </Col>
            </Row>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3" controlId="phone">
                  <Form.Label htmlFor="staff-phone">Telefone</Form.Label>
                  <Form.Control
                    type="text"
                    id="staff-phone"
                    data-testid="staff-phone"
                    value={formData.phone}
                    onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                    required
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3" controlId="role">
                  <Form.Label htmlFor="staff-role">Função</Form.Label>
                  <Form.Select
                    id="staff-role"
                    data-testid="staff-role"
                    value={formData.role}
                    onChange={(e) => setFormData({ ...formData, role: e.target.value })}
                    required
                  >
                    <option value="">Selecione uma função</option>
                    {roles.map((role) => (
                      <option key={role} value={role}>
                        {role}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>
              </Col>
            </Row>
            <Form.Group className="mb-3">
              <Form.Label>Especialidades</Form.Label>
              <Row>
                {availableSpecialties.map((specialty) => (
                  <Col md={4} key={specialty}>
                    <Form.Check
                      type="checkbox"
                      id={`staff-specialty-${specialty}`}
                      label={specialty}
                      checked={formData.specialties.includes(specialty)}
                      onChange={(e) => handleSpecialtyChange(specialty, e.target.checked)}
                    />
                  </Col>
                ))}
              </Row>
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Check
                type="checkbox"
                id="staff-active"
                label="Funcionário ativo"
                checked={formData.isActive}
                onChange={(e) => setFormData({ ...formData, isActive: e.target.checked })}
              />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={handleCloseModal}>
              Cancelar
            </Button>
            <Button variant="primary" type="submit">
              {editingStaff ? 'Atualizar' : 'Criar'}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </Container>
  );
}

export default Staff;
