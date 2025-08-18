import React from 'react';
import './SkipLinks.css';

/**
 * Componente de Skip Links para navegação rápida
 * Permite que usuários de teclado pulem para seções principais
 */
const SkipLinks = () => {
  const skipToContent = (targetId) => {
    const target = document.getElementById(targetId);
    if (target) {
      target.focus();
      target.scrollIntoView({ behavior: 'smooth' });
    }
  };

  return (
    <div className="skip-links">
      <a 
        href="#main-content" 
        className="skip-link"
        onClick={(e) => {
          e.preventDefault();
          skipToContent('main-content');
        }}
      >
        Pular para conteúdo principal
      </a>
      <a 
        href="#navigation" 
        className="skip-link"
        onClick={(e) => {
          e.preventDefault();
          skipToContent('navigation');
        }}
      >
        Pular para navegação
      </a>
      <a 
        href="#search" 
        className="skip-link"
        onClick={(e) => {
          e.preventDefault();
          skipToContent('search');
        }}
      >
        Pular para busca
      </a>
    </div>
  );
};

export default SkipLinks;
