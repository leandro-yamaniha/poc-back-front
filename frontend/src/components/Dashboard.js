import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Button } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import api, { customersAPI, servicesAPI, staffAPI, appointmentsAPI } from '../services/api';
import { toast } from 'react-toastify';
import LoadingSpinner, { CardLoading } from './LoadingSpinner';
import { useLoading } from '../contexts/LoadingContext';

function Dashboard() {
  const [stats, setStats] = useState({
    customers: 0,
    services: 0,
    staff: 0,
    appointments: 0,
    todayAppointments: 0
  });
  
  // Hook de loading global
  const { withLoading, isLoading } = useLoading();

  // Format date as YYYY-MM-DD for backend endpoint /appointments/date/{date}
  const formatDate = (date) => {
    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');
    return `${yyyy}-${mm}-${dd}`;
  };

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    await withLoading('dashboard-stats', async () => {
      try {
        const [customersRes, servicesRes, staffRes, appointmentsRes, todayAppointmentsRes] = await Promise.all([
          customersAPI.getAll(),
          servicesAPI.getActive(),
          staffAPI.getActive(),
          appointmentsAPI.getAll(),
          appointmentsAPI.getByDate(formatDate(new Date()))
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
      }
    });
  };

  const isLoadingStats = isLoading('dashboard-stats');

  if (isLoadingStats) {
    return (
      <Container>
        <div className="page-header">
          <h1>Dashboard</h1>
          <p>Visão geral do seu salão de beleza</p>
        </div>
        
        <Row className="mb-4">
          {[1, 2, 3, 4].map((i) => (
            <Col key={i} md={6} lg={3} className="mb-3">
              <CardLoading height="120px" />
            </Col>
          ))}
        </Row>
        
        <Row>
          <Col md={6} className="mb-4">
            <CardLoading height="200px" />
          </Col>
          <Col md={6} className="mb-4">
            <CardLoading height="200px" />
          </Col>
        </Row>
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
          <Card className="dashboard-card h-100 border-primary">
            <Card.Body className="text-center">
              <div className="display-4 text-primary">👥</div>
              <h2 className="display-4 text-primary">{stats.customers}</h2>
              <p className="mb-0 text-muted">Clientes</p>
            </Card.Body>
          </Card>
        </Col>
        
        <Col md={6} lg={3} className="mb-3">
          <Card className="dashboard-card h-100 border-success">
            <Card.Body className="text-center">
              <div className="display-4 text-success">✨</div>
              <h2 className="display-4 text-success">{stats.services}</h2>
              <p className="mb-0 text-muted">Serviços Ativos</p>
            </Card.Body>
          </Card>
        </Col>
        
        <Col md={6} lg={3} className="mb-3">
          <Card className="dashboard-card h-100 border-info">
            <Card.Body className="text-center">
              <div className="display-4 text-info">👩‍💼</div>
              <h2 className="display-4 text-info">{stats.staff}</h2>
              <p className="mb-0 text-muted">Funcionários</p>
            </Card.Body>
          </Card>
        </Col>
        
        <Col md={6} lg={3} className="mb-3">
          <Card className="dashboard-card h-100 border-warning">
            <Card.Body className="text-center">
              <div className="display-4 text-warning">📅</div>
              <h2 className="display-4 text-warning">{stats.todayAppointments}</h2>
              <p className="mb-0 text-muted">Agendamentos Hoje</p>
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
