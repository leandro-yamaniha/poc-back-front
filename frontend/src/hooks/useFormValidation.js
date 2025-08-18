import { useState, useCallback } from 'react';

/**
 * Hook customizado para validação de formulários com feedback visual
 * @param {Object} initialState - Estado inicial do formulário
 * @param {Object} validationRules - Regras de validação para cada campo
 * @returns {Object} - Objeto com valores, erros, funções de validação e manipulação
 */
const useFormValidation = (initialState, validationRules) => {
  const [values, setValues] = useState(initialState);
  const [errors, setErrors] = useState({});
  const [touched, setTouched] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Função para validar um campo específico
  const validateField = useCallback((fieldName, value) => {
    const rules = validationRules[fieldName];
    if (!rules) return '';

    // Validação de campo obrigatório
    if (rules.required && (!value || value.toString().trim() === '')) {
      return rules.requiredMessage || `${fieldName} é obrigatório`;
    }

    // Validação de email
    if (rules.email && value && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
      return rules.emailMessage || 'Email inválido';
    }

    // Validação de telefone brasileiro
    if (rules.phone && value) {
      const phoneRegex = /^(\(?\d{2}\)?\s?)?(\d{4,5}-?\d{4})$/;
      if (!phoneRegex.test(value.replace(/\s/g, ''))) {
        return rules.phoneMessage || 'Telefone inválido';
      }
    }

    // Validação de tamanho mínimo
    if (rules.minLength && value && value.length < rules.minLength) {
      return rules.minLengthMessage || `Mínimo de ${rules.minLength} caracteres`;
    }

    // Validação de tamanho máximo
    if (rules.maxLength && value && value.length > rules.maxLength) {
      return rules.maxLengthMessage || `Máximo de ${rules.maxLength} caracteres`;
    }

    // Validação customizada
    if (rules.custom && typeof rules.custom === 'function') {
      const customError = rules.custom(value, values);
      if (customError) return customError;
    }

    return '';
  }, [validationRules, values]);

  // Função para validar todos os campos
  const validateAll = useCallback(() => {
    const newErrors = {};
    let isValid = true;

    Object.keys(validationRules).forEach(fieldName => {
      const error = validateField(fieldName, values[fieldName]);
      if (error) {
        newErrors[fieldName] = error;
        isValid = false;
      }
    });

    setErrors(newErrors);
    return isValid;
  }, [values, validateField, validationRules]);

  // Função para atualizar valor de um campo
  const setValue = useCallback((fieldName, value) => {
    setValues(prev => ({ ...prev, [fieldName]: value }));
    
    // Validar em tempo real se o campo já foi tocado
    if (touched[fieldName]) {
      const error = validateField(fieldName, value);
      setErrors(prev => ({ ...prev, [fieldName]: error }));
    }
  }, [touched, validateField]);

  // Função para marcar campo como tocado
  const setFieldTouched = useCallback((fieldName) => {
    setTouched(prev => ({ ...prev, [fieldName]: true }));
    
    // Validar o campo quando for tocado
    const error = validateField(fieldName, values[fieldName]);
    setErrors(prev => ({ ...prev, [fieldName]: error }));
  }, [values, validateField]);

  // Função para manipular mudança de input
  const handleChange = useCallback((e) => {
    const { name, value, type, checked } = e.target;
    const fieldValue = type === 'checkbox' ? checked : value;
    setValue(name, fieldValue);
  }, [setValue]);

  // Função para manipular blur de input
  const handleBlur = useCallback((e) => {
    const { name } = e.target;
    setFieldTouched(name);
  }, [setFieldTouched]);

  // Função para resetar formulário
  const reset = useCallback(() => {
    setValues(initialState);
    setErrors({});
    setTouched({});
    setIsSubmitting(false);
  }, [initialState]);

  // Função para definir valores do formulário
  const setFormValues = useCallback((newValues) => {
    setValues(newValues);
  }, []);

  // Função para manipular submit
  const handleSubmit = useCallback((onSubmit) => {
    return async (e) => {
      e.preventDefault();
      setIsSubmitting(true);

      // Marcar todos os campos como tocados
      const allFields = Object.keys(validationRules);
      const newTouched = {};
      allFields.forEach(field => {
        newTouched[field] = true;
      });
      setTouched(newTouched);

      // Validar todos os campos
      const isValid = validateAll();

      if (isValid && onSubmit) {
        try {
          await onSubmit(values);
        } catch (error) {
          console.error('Form submission error:', error);
        }
      }

      setIsSubmitting(false);
    };
  }, [values, validateAll, validationRules]);

  // Verificar se o formulário é válido
  const isValid = Object.keys(errors).length === 0 && 
                  Object.keys(touched).length > 0;

  return {
    values,
    errors,
    touched,
    isSubmitting,
    isValid,
    setValue,
    setFieldTouched,
    handleChange,
    handleBlur,
    handleSubmit,
    validateAll,
    reset,
    setFormValues
  };
};

export default useFormValidation;
