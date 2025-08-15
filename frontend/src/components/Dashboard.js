import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import api, { customersAPI, servicesAPI, staffAPI, appointmentsAPI } from '../services/api';
import { toast } from 'react-toastify';

function Dashboard() {
  const [stats, setStats] = useState({
    customers: 0,
    services: 0,
    staff: 0,
    appointments: 0,
    todayAppointments: 0
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    try {
      setLoading(true);
      
      const [customersRes, servicesRes, staffRes, appointmentsRes, todayAppointmentsRes] = await Promise.all([
        customersAPI.getAll(),
        servicesAPI.getActive(),
        staffAPI.getActive(),
        appointmentsAPI.getAll(),
        api.get('/appointments/today')
      ]);

      setStats({
        customers: customersRes.data.length,
        services: servicesRes.data.length,
        staff: staffRes.data.length,
        appointments: appointmentsRes.data.length,
        todayAppointments: todayAppointmentsRes.data.length
      });
    } catch (error) {
      console.error('Error loading stats:', error);
      toast.error('Erro ao carregar estatísticas');
    } finally {
      setLoading(false);
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
        <h1>Dashboard</h1>
        <p>Visão geral do seu salão de beleza</p>
      </div>

      <Row className="mb-4">
        <Col md={6} lg={3} className="mb-3">
          <Card className="dashboard-card h-100">
            <Card.Body className="text-center">
              <div className="display-4">👥</div>
              <h2 className="display-4">{stats.customers}</h2>
              <p className="mb-0">Clientes</p>
            </Card.Body>
          </Card>
        </Col>
        
        <Col md={6} lg={3} className="mb-3">
          <Card className="dashboard-card h-100">
            <Card.Body className="text-center">
              <div className="display-4">✨</div>
              <h2 className="display-4">{stats.services}</h2>
              <p className="mb-0">Serviços Ativos</p>
            </Card.Body>
          </Card>
        </Col>
        
        <Col md={6} lg={3} className="mb-3">
          <Card className="dashboard-card h-100">
            <Card.Body className="text-center">
              <div className="display-4">👩‍💼</div>
              <h2 className="display-4">{stats.staff}</h2>
              <p className="mb-0">Funcionários</p>
            </Card.Body>
          </Card>
        </Col>
        
        <Col md={6} lg={3} className="mb-3">
          <Card className="dashboard-card h-100">
            <Card.Body className="text-center">
              <div className="display-4">📅</div>
              <h2 className="display-4">{stats.todayAppointments}</h2>
              <p className="mb-0">Agendamentos Hoje</p>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Row>
        <Col md={6} className="mb-4">
          <Card>
            <Card.Header>
              <h5 className="mb-0">Ações Rápidas</h5>
            </Card.Header>
            <Card.Body>
              <div className="d-grid gap-2">
                <Button as={Link} to="/appointments" variant="primary">
                  Novo Agendamento
                </Button>
                <Button as={Link} to="/customers" variant="outline-primary">
                  Gerenciar Clientes
                </Button>
                <Button as={Link} to="/services" variant="outline-primary">
                  Gerenciar Serviços
                </Button>
              </div>
            </Card.Body>
          </Card>
        </Col>
        
        <Col md={6} className="mb-4">
          <Card>
            <Card.Header>
              <h5 className="mb-0">Resumo Total</h5>
            </Card.Header>
            <Card.Body>
              <div className="mb-3">
                <div className="d-flex justify-content-between">
                  <span>Total de Agendamentos:</span>
                  <strong>{stats.appointments}</strong>
                </div>
              </div>
              <div className="mb-3">
                <div className="d-flex justify-content-between">
                  <span>Clientes Cadastrados:</span>
                  <strong>{stats.customers}</strong>
                </div>
              </div>
              <div className="mb-3">
                <div className="d-flex justify-content-between">
                  <span>Serviços Disponíveis:</span>
                  <strong>{stats.services}</strong>
                </div>
              </div>
              <div>
                <div className="d-flex justify-content-between">
                  <span>Equipe Ativa:</span>
                  <strong>{stats.staff}</strong>
                </div>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
}

export default Dashboard;
