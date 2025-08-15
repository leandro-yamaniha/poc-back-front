import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Table, Button, Modal, Form, Badge } from 'react-bootstrap';
import DatePicker from 'react-datepicker';
import api, { appointmentsAPI, customersAPI, servicesAPI, staffAPI } from '../services/api';
import { toast } from 'react-toastify';
import 'react-datepicker/dist/react-datepicker.css';

function Appointments() {
  const [appointments, setAppointments] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [services, setServices] = useState([]);
  const [staff, setStaff] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingAppointment, setEditingAppointment] = useState(null);
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [formData, setFormData] = useState({
    customerId: '',
    staffId: '',
    serviceId: '',
    appointmentDate: new Date(),
    appointmentTime: '',
    status: 'scheduled',
    notes: '',
    totalPrice: ''
  });

  const statusOptions = [
    { value: 'scheduled', label: 'Agendado', color: 'info' },
    { value: 'confirmed', label: 'Confirmado', color: 'primary' },
    { value: 'completed', label: 'Concluído', color: 'success' },
    { value: 'cancelled', label: 'Cancelado', color: 'danger' }
  ];

  useEffect(() => {
    loadData();
  }, []);

  const isInitialMount = React.useRef(true);
  useEffect(() => {
    if (isInitialMount.current) {
      isInitialMount.current = false;
    } else {
      loadAppointmentsByDate();
    }
  }, [selectedDate]);

  const loadData = async () => {
    try {
      setLoading(true);
      const [customersRes, servicesRes, staffRes] = await Promise.all([
        customersAPI.getAll(),
        servicesAPI.getActive(),
        staffAPI.getActive()
      ]);
      
      setCustomers(customersRes.data);
      setServices(servicesRes.data);
      setStaff(staffRes.data);
      
      await loadAppointmentsByDate();
    } catch (error) {
      console.error('Error loading data:', error);
      toast.error('Erro ao carregar dados');
    } finally {
      setLoading(false);
    }
  };

  const loadAppointmentsByDate = async () => {
    try {
      const dateStr = selectedDate.toISOString().split('T')[0];
      const today = new Date().toISOString().split('T')[0];
      
      // Use today endpoint if selected date is today, otherwise get all and filter
      if (dateStr === today) {
        const response = await api.get('/appointments/today');
        setAppointments(response.data);
      } else {
        // For other dates, get all appointments and filter client-side
        const response = await appointmentsAPI.getAll();
        const filteredAppointments = response.data.filter(appointment => {
          const appointmentDate = new Date(appointment.appointment_date).toISOString().split('T')[0];
          return appointmentDate === dateStr;
        });
        setAppointments(filteredAppointments);
      }
    } catch (error) {
      console.error('Error loading appointments:', error);
      toast.error('Erro ao carregar agendamentos');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const appointmentData = {
        ...formData,
        appointmentDate: formData.appointmentDate.toISOString().split('T')[0],
        totalPrice: parseFloat(formData.totalPrice) || 0
      };

      if (editingAppointment) {
        await appointmentsAPI.update(editingAppointment.id, appointmentData);
        toast.success('Agendamento atualizado com sucesso!');
      } else {
        await appointmentsAPI.create(appointmentData);
        toast.success('Agendamento criado com sucesso!');
      }
      
      setShowModal(false);
      setEditingAppointment(null);
      resetForm();
      loadAppointmentsByDate();
    } catch (error) {
      console.error('Error saving appointment:', error);
      toast.error('Erro ao salvar agendamento');
    }
  };

  const handleEdit = (appointment) => {
    setEditingAppointment(appointment);
    setFormData({
      customerId: appointment.customerId,
      staffId: appointment.staffId,
      serviceId: appointment.serviceId,
      appointmentDate: new Date(appointment.appointmentDate),
      appointmentTime: appointment.appointmentTime,
      status: appointment.status,
      notes: appointment.notes || '',
      totalPrice: appointment.totalPrice?.toString() || ''
    });
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este agendamento?')) {
      try {
        await appointmentsAPI.delete(id);
        toast.success('Agendamento excluído com sucesso!');
        loadAppointmentsByDate();
      } catch (error) {
        console.error('Error deleting appointment:', error);
        toast.error('Erro ao excluir agendamento');
      }
    }
  };

  const handleNewAppointment = () => {
    resetForm();
    setEditingAppointment(null);
    setShowModal(true);
  };

  

  const handleCloseModal = () => {
    setShowModal(false);
    setEditingAppointment(null);
    resetForm();
  };

  const resetForm = () => {
    setFormData({
      customerId: '',
      staffId: '',
      serviceId: '',
      appointmentDate: new Date(),
      appointmentTime: '',
      status: 'scheduled',
      notes: '',
      totalPrice: ''
    });
  };

  const handleServiceChange = (serviceId) => {
    const service = services.find(s => s.id === serviceId);
    setFormData({
      ...formData,
      serviceId,
      totalPrice: service ? service.price.toString() : ''
    });
  };

  const getCustomerName = (customerId) => {
    const customer = customers.find(c => c.id === customerId);
    return customer ? customer.name : 'Cliente não encontrado';
  };

  const getStaffName = (staffId) => {
    const staffMember = staff.find(s => s.id === staffId);
    return staffMember ? staffMember.name : 'Funcionário não encontrado';
  };

  const getServiceName = (serviceId) => {
    const service = services.find(s => s.id === serviceId);
    return service ? service.name : 'Serviço não encontrado';
  };

  const getStatusBadge = (status) => {
    const statusOption = statusOptions.find(s => s.value === status);
    return statusOption ? (
      <Badge bg={statusOption.color}>{statusOption.label}</Badge>
    ) : (
      <Badge bg="secondary">{status}</Badge>
    );
  };

  const formatPrice = (price) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(price);
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
        <h1>Agendamentos</h1>
        <p>Gerencie os agendamentos do seu salão</p>
      </div>

      <Row className="mb-4">
        <Col md={4}>
          <Form.Group>
            <Form.Label>Filtrar por data:</Form.Label>
            <DatePicker
              selected={selectedDate}
              onChange={setSelectedDate}
              dateFormat="dd/MM/yyyy"
              className="form-control"
            />
          </Form.Group>
        </Col>
        <Col md={8} className="text-end d-flex align-items-end">
          <Button variant="primary" onClick={handleNewAppointment}>
            Novo Agendamento
          </Button>
        </Col>
      </Row>

      <Card>
        <Card.Body>
          {appointments.length === 0 ? (
            <div className="empty-state">
              <div className="display-1">📅</div>
              <h3>Nenhum agendamento encontrado</h3>
              <p>Não há agendamentos para {selectedDate.toLocaleDateString('pt-BR')}</p>
            </div>
          ) : (
            <div className="table-responsive">
              <Table hover>
                <thead>
                  <tr>
                    <th>Horário</th>
                    <th>Cliente</th>
                    <th>Serviço</th>
                    <th>Funcionário</th>
                    <th>Valor</th>
                    <th>Status</th>
                    <th>Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {appointments.map((appointment) => (
                    <tr key={appointment.id}>
                      <td>
                        <strong>{appointment.appointmentTime}</strong>
                      </td>
                      <td>{getCustomerName(appointment.customerId)}</td>
                      <td>{getServiceName(appointment.serviceId)}</td>
                      <td>{getStaffName(appointment.staffId)}</td>
                      <td>{appointment.totalPrice ? formatPrice(appointment.totalPrice) : '-'}</td>
                      <td>{getStatusBadge(appointment.status)}</td>
                      <td>
                        <Button
                          variant="outline-primary"
                          size="sm"
                          className="me-2"
                          onClick={() => handleEdit(appointment)}
                        >
                          Editar
                        </Button>
                        <Button
                          variant="outline-danger"
                          size="sm"
                          onClick={() => handleDelete(appointment.id)}
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

      <Modal show={showModal} onHide={handleCloseModal} size="lg" animation={false}>
        <Modal.Header closeButton>
          <Modal.Title>
            {editingAppointment ? 'Editar Agendamento' : 'Novo Agendamento'}
          </Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleSubmit}>
          <Modal.Body>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3" controlId="customerId">
                  <Form.Label>Cliente</Form.Label>
                  <Form.Select
                    value={formData.customerId}
                    onChange={(e) => setFormData({ ...formData, customerId: e.target.value })}
                    required
                  >
                    <option value="">Selecione um cliente</option>
                    {customers.map((customer) => (
                      <option key={customer.id} value={customer.id}>
                        {customer.name}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3" controlId="staffId">
                  <Form.Label>Funcionário</Form.Label>
                  <Form.Select
                    value={formData.staffId}
                    onChange={(e) => setFormData({ ...formData, staffId: e.target.value })}
                    required
                  >
                    <option value="">Selecione um funcionário</option>
                    {staff.map((staffMember) => (
                      <option key={staffMember.id} value={staffMember.id}>
                        {staffMember.name} - {staffMember.role}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>
              </Col>
            </Row>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3" controlId="serviceId">
                  <Form.Label>Serviço</Form.Label>
                  <Form.Select
                    value={formData.serviceId}
                    onChange={(e) => handleServiceChange(e.target.value)}
                    required
                  >
                    <option value="">Selecione um serviço</option>
                    {services.map((service) => (
                      <option key={service.id} value={service.id}>
                        {service.name} - {formatPrice(service.price)}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Status</Form.Label>
                  <Form.Select
                    value={formData.status}
                    onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                    required
                  >
                    {statusOptions.map((status) => (
                      <option key={status.value} value={status.value}>
                        {status.label}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>
              </Col>
            </Row>
            <Row>
              <Col md={4}>
                <Form.Group className="mb-3">
                  <Form.Label>Data</Form.Label>
                  <DatePicker
                    selected={formData.appointmentDate}
                    onChange={(date) => setFormData({ ...formData, appointmentDate: date })}
                    dateFormat="dd/MM/yyyy"
                    className="form-control"
                    required
                  />
                </Form.Group>
              </Col>
              <Col md={4}>
                <Form.Group className="mb-3" controlId="appointmentTime">
                  <Form.Label>Horário</Form.Label>
                  <Form.Control
                    type="time"
                    value={formData.appointmentTime}
                    onChange={(e) => setFormData({ ...formData, appointmentTime: e.target.value })}
                    required
                  />
                </Form.Group>
              </Col>
              <Col md={4}>
                <Form.Group className="mb-3">
                  <Form.Label>Valor (R$)</Form.Label>
                  <Form.Control
                    type="number"
                    step="0.01"
                    value={formData.totalPrice}
                    onChange={(e) => setFormData({ ...formData, totalPrice: e.target.value })}
                    min="0"
                  />
                </Form.Group>
              </Col>
            </Row>
            <Form.Group className="mb-3">
              <Form.Label>Observações</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                value={formData.notes}
                onChange={(e) => setFormData({ ...formData, notes: e.target.value })}
                placeholder="Observações sobre o agendamento..."
              />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={handleCloseModal}>
              Cancelar
            </Button>
            <Button variant="primary" type="submit">
              {editingAppointment ? 'Atualizar' : 'Criar'}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </Container>
  );
}

export default Appointments;
