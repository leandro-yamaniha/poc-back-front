import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import '@testing-library/jest-dom';
import Navbar from '../Navbar';
import { LoadingProvider } from '../../contexts/LoadingContext';

// Mock dos hooks personalizados
jest.mock('../../hooks/useAccessibility', () => ({
  useAccessibility: jest.fn(() => ({
    useFocusTrap: jest.fn(() => ({ current: null })),
    announceToScreenReader: jest.fn()
  }))
}));

describe('Navbar Component', () => {
  const renderNavbar = () => {
    return render(
      <MemoryRouter>
        <LoadingProvider>
          <Navbar />
        </LoadingProvider>
      </MemoryRouter>
    );
  };

  test('renders the brand/logo', () => {
    renderNavbar();
    const brandElement = screen.getByText('💄 Beauty Salon');
    expect(brandElement).toBeInTheDocument();
  });

  test('renders all navigation links', () => {
    renderNavbar();
    
    const navLinks = [
      'Dashboard',
      'Agendamentos',
      'Clientes',
      'Serviços',
      'Funcionários'
    ];

    navLinks.forEach(linkText => {
      const linkElement = screen.getByText(linkText);
      expect(linkElement).toBeInTheDocument();
    });
  });

  test('navigation links have correct paths', () => {
    renderNavbar();
    
    const linkPaths = {
      'Dashboard': '/',
      'Agendamentos': '/appointments',
      'Clientes': '/customers',
      'Serviços': '/services',
      'Funcionários': '/staff'
    };

    Object.entries(linkPaths).forEach(([text, path]) => {
      const linkElement = screen.getByText(text).closest('a');
      expect(linkElement).toHaveAttribute('href', path);
    });
  });

  test('toggles mobile menu when button is clicked', () => {
    // Mock window.matchMedia for testing
    Object.defineProperty(window, 'matchMedia', {
      writable: true,
      value: jest.fn().mockImplementation(query => ({
        matches: false,
        media: query,
        onchange: null,
        addListener: jest.fn(),
        removeListener: jest.fn(),
        addEventListener: jest.fn(),
        removeEventListener: jest.fn(),
        dispatchEvent: jest.fn(),
      })),
    });

    renderNavbar();
    
    // Initially, the mobile menu should be collapsed
    const navElement = screen.getByTestId('navbar');
    expect(navElement).toHaveClass('collapse');
    
    // Find and click the toggle button
    const toggleButton = screen.getByRole('button', { name: /toggle navigation/i });
    fireEvent.click(toggleButton);
    
    // After clicking, the menu should be in the process of expanding
    // It will have either 'collapsing' (during transition) or 'show' (after transition)
    expect(
      navElement.classList.contains('show') || 
      navElement.classList.contains('collapsing')
    ).toBeTruthy();
  });
});
