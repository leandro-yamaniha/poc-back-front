// Mock específico para React Bootstrap components
import React from 'react';

const MockNavbar = ({ children, ...props }) => <div {...props}>{children}</div>;
MockNavbar.Brand = ({ children, ...props }) => <div {...props}>{children}</div>;
MockNavbar.Toggle = ({ children, ...props }) => <button {...props} role="button" aria-label="Toggle navigation">{children}</button>;
MockNavbar.Collapse = ({ children, ...props }) => <div {...props} className="collapse navbar-collapse">{children}</div>;

const MockNav = ({ children, ...props }) => <div {...props}>{children}</div>;
MockNav.Link = ({ children, ...props }) => <a {...props}>{children}</a>;

const MockModal = ({ children, show, ...props }) => show ? <div {...props}>{children}</div> : null;
MockModal.Header = ({ children, ...props }) => <div {...props}>{children}</div>;
MockModal.Title = ({ children, ...props }) => <h4 {...props}>{children}</h4>;
MockModal.Body = ({ children, ...props }) => <div {...props}>{children}</div>;
MockModal.Footer = ({ children, ...props }) => <div {...props}>{children}</div>;

const MockForm = ({ children, onSubmit, ...props }) => <form onSubmit={onSubmit} {...props}>{children}</form>;
MockForm.Group = ({ children, ...props }) => <div {...props}>{children}</div>;
MockForm.Label = ({ children, ...props }) => <label {...props}>{children}</label>;
MockForm.Control = ({ value, onChange, ...props }) => <input value={value} onChange={onChange} {...props} />;
MockForm.Select = ({ value, onChange, children, ...props }) => <select value={value} onChange={onChange} {...props}>{children}</select>;

export default {
  Container: ({ children, ...props }) => <div data-testid="container" {...props}>{children}</div>,
  Row: ({ children, ...props }) => <div data-testid="row" {...props}>{children}</div>,
  Col: ({ children, ...props }) => <div data-testid="col" {...props}>{children}</div>,
  Card: ({ children, ...props }) => <div data-testid="card" {...props}>{children}</div>,
  Button: ({ children, onClick, ...props }) => <button onClick={onClick} data-testid="button" {...props}>{children}</button>,
  Form: MockForm,
  Alert: ({ children, ...props }) => <div data-testid="alert" {...props}>{children}</div>,
  Table: ({ children, ...props }) => <table data-testid="table" {...props}>{children}</table>,
  Modal: MockModal,
  Spinner: (props) => <div data-testid="spinner" {...props}>Loading...</div>,
  Navbar: MockNavbar,
  Nav: MockNav,
  Badge: ({ children, ...props }) => <span data-testid="badge" {...props}>{children}</span>
};
