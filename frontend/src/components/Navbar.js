import React from 'react';
import { Navbar as BootstrapNavbar, Nav, Container } from 'react-bootstrap';
import { LinkContainer } from 'react-router-bootstrap';

function Navbar() {
  return (
    <BootstrapNavbar bg="light" expand="lg" className="shadow-sm mb-4">
      <Container>
        <LinkContainer to="/">
          <BootstrapNavbar.Brand>
            💄 Beauty Salon
          </BootstrapNavbar.Brand>
        </LinkContainer>
        
        <BootstrapNavbar.Toggle aria-controls="basic-navbar-nav" />
        <BootstrapNavbar.Collapse id="basic-navbar-nav" data-testid="navbar">
          <Nav className="ms-auto">
            <LinkContainer to="/">
              <Nav.Link>Dashboard</Nav.Link>
            </LinkContainer>
            <LinkContainer to="/appointments">
              <Nav.Link>Agendamentos</Nav.Link>
            </LinkContainer>
            <LinkContainer to="/customers">
              <Nav.Link>Clientes</Nav.Link>
            </LinkContainer>
            <LinkContainer to="/services">
              <Nav.Link>Serviços</Nav.Link>
            </LinkContainer>
            <LinkContainer to="/staff">
              <Nav.Link>Funcionários</Nav.Link>
            </LinkContainer>
          </Nav>
        </BootstrapNavbar.Collapse>
      </Container>
    </BootstrapNavbar>
  );
}

export default Navbar;
