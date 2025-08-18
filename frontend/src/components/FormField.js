import React from 'react';
import { Form } from 'react-bootstrap';

/**
 * Componente reutilizável para campos de formulário com validação visual
 * @param {Object} props - Propriedades do componente
 * @param {string} props.label - Label do campo
 * @param {string} props.name - Nome do campo
 * @param {string} props.type - Tipo do input
 * @param {string} props.value - Valor atual do campo
 * @param {string} props.error - Mensagem de erro
 * @param {boolean} props.touched - Se o campo foi tocado
 * @param {Function} props.onChange - Função de mudança
 * @param {Function} props.onBlur - Função de blur
 * @param {boolean} props.required - Se o campo é obrigatório
 * @param {string} props.placeholder - Placeholder do campo
 * @param {string} props.as - Tipo de elemento (input, textarea, select)
 * @param {number} props.rows - Número de linhas para textarea
 * @param {Array} props.options - Opções para select
 * @param {React.ReactNode} props.children - Filhos para select customizado
 */
const FormField = ({
  label,
  name,
  type = 'text',
  value,
  error,
  touched,
  onChange,
  onBlur,
  required = false,
  placeholder,
  as = 'input',
  rows,
  options,
  children,
  ...props
}) => {
  const hasError = touched && error;
  const fieldId = `field-${name}`;
  const errorId = `${fieldId}-error`;

  return (
    <Form.Group className="mb-3">
      <Form.Label htmlFor={fieldId}>
        {label}
        {required && <span className="text-danger ms-1" aria-label="obrigatório">*</span>}
      </Form.Label>
      
      {as === 'select' ? (
        <Form.Select
          id={fieldId}
          name={name}
          value={value}
          onChange={onChange}
          onBlur={onBlur}
          isInvalid={hasError}
          aria-describedby={hasError ? errorId : undefined}
          required={required}
          {...props}
        >
          {options ? (
            <>
              <option value="">Selecione...</option>
              {options.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </>
          ) : (
            children
          )}
        </Form.Select>
      ) : (
        <Form.Control
          id={fieldId}
          name={name}
          type={type}
          as={as}
          rows={rows}
          value={value}
          onChange={onChange}
          onBlur={onBlur}
          placeholder={placeholder}
          isInvalid={hasError}
          aria-describedby={hasError ? errorId : undefined}
          required={required}
          {...props}
        />
      )}
      
      {hasError && (
        <Form.Control.Feedback 
          type="invalid" 
          id={errorId}
          role="alert"
          className="d-block"
        >
          {error}
        </Form.Control.Feedback>
      )}
    </Form.Group>
  );
};

export default FormField;
