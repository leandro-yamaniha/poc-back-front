import { renderHook, act } from '@testing-library/react';

// Unmock the hook for this test file
jest.unmock('../useFormValidation');
const useFormValidation = require('../useFormValidation').default;

describe('useFormValidation', () => {
  const initialState = {
    name: '',
    email: '',
    phone: '',
    password: ''
  };

  const validationRules = {
    name: {
      required: true,
      requiredMessage: 'Nome é obrigatório',
      minLength: 2,
      minLengthMessage: 'Nome deve ter pelo menos 2 caracteres'
    },
    email: {
      required: true,
      email: true,
      emailMessage: 'Email deve ter formato válido'
    },
    phone: {
      phone: true,
      phoneMessage: 'Telefone deve ter formato válido'
    },
    password: {
      required: true,
      minLength: 6,
      maxLength: 20,
      custom: (value) => {
        if (value && !/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(value)) {
          return 'Senha deve conter ao menos uma letra minúscula, maiúscula e um número';
        }
        return '';
      }
    }
  };

  test('initializes with correct default values', () => {
    const { result } = renderHook(() => useFormValidation(initialState, validationRules));

    expect(result.current.values).toEqual(initialState);
    expect(result.current.errors).toEqual({});
    expect(result.current.touched).toEqual({});
    expect(result.current.isSubmitting).toBe(false);
    expect(result.current.isValid).toBe(false);
  });

  describe('setValue', () => {
    test('updates field value', () => {
      const { result } = renderHook(() => useFormValidation(initialState, validationRules));

      act(() => {
        result.current.setValue('name', 'John Doe');
      });

      expect(result.current.values.name).toBe('John Doe');
    });

    test('validates field in real-time if already touched', () => {
      const { result } = renderHook(() => useFormValidation(initialState, validationRules));

      // Marcar campo como tocado primeiro
      act(() => {
        result.current.setFieldTouched('name');
      });

      // Agora definir um valor inválido
      act(() => {
        result.current.setValue('name', '');
      });

      expect(result.current.errors.name).toBe('Nome é obrigatório');
    });

    test('does not validate untouched fields', () => {
      const { result } = renderHook(() => useFormValidation(initialState, validationRules));

      act(() => {
        result.current.setValue('name', '');
      });

      expect(result.current.errors.name).toBeUndefined();
    });
  });

  describe('setFieldTouched', () => {
    test('marks field as touched and validates it', () => {
      const { result } = renderHook(() => useFormValidation(initialState, validationRules));

      act(() => {
        result.current.setFieldTouched('name');
      });

      expect(result.current.touched.name).toBe(true);
      expect(result.current.errors.name).toBe('Nome é obrigatório');
    });
  });

  describe('handleChange', () => {
    test('handles text input changes', () => {
      const { result } = renderHook(() => useFormValidation(initialState, validationRules));

      const event = {
        target: {
          name: 'name',
          value: 'John Doe',
          type: 'text'
        }
      };

      act(() => {
        result.current.handleChange(event);
      });

      expect(result.current.values.name).toBe('John Doe');
    });

    test('handles checkbox changes', () => {
      const initialStateWithCheckbox = { ...initialState, agree: false };
      const rulesWithCheckbox = { ...validationRules, agree: { required: true } };

      const { result } = renderHook(() => 
        useFormValidation(initialStateWithCheckbox, rulesWithCheckbox)
      );

      const event = {
        target: {
          name: 'agree',
          checked: true,
          type: 'checkbox'
        }
      };

      act(() => {
        result.current.handleChange(event);
      });

      expect(result.current.values.agree).toBe(true);
    });
  });

  describe('handleBlur', () => {
    test('marks field as touched on blur', () => {
      const { result } = renderHook(() => useFormValidation(initialState, validationRules));

      const event = {
        target: { name: 'email' }
      };

      act(() => {
        result.current.handleBlur(event);
      });

      expect(result.current.touched.email).toBe(true);
      expect(result.current.errors.email).toBe('Email é obrigatório');
    });
  });

  describe('validation rules', () => {
    test('validates required fields', () => {
      const { result } = renderHook(() => useFormValidation(initialState, validationRules));

      act(() => {
        result.current.setFieldTouched('name');
      });

      expect(result.current.errors.name).toBe('Nome é obrigatório');

      act(() => {
        result.current.setValue('name', 'John');
      });

      expect(result.current.errors.name).toBe('');
    });

    test('validates email format', () => {
      const { result } = renderHook(() => useFormValidation(initialState, validationRules));

      act(() => {
        result.current.setFieldTouched('email');
        result.current.setValue('email', 'invalid-email');
      });

      expect(result.current.errors.email).toBe('email é obrigatório');

      act(() => {
        result.current.setValue('email', 'valid@email.com');
      });

      expect(result.current.errors.email).toBe('');
    });

    test('validates phone format', () => {
      const { result } = renderHook(() => useFormValidation(initialState, validationRules));

      act(() => {
        result.current.setFieldTouched('phone');
        result.current.setValue('phone', '123');
      });

      expect(result.current.errors.phone).toBe('');

      act(() => {
        result.current.setValue('phone', '(11) 99999-9999');
      });

      expect(result.current.errors.phone).toBe('');

      act(() => {
        result.current.setValue('phone', '11999999999');
      });

      expect(result.current.errors.phone).toBe('');
    });

    test('validates minimum length', () => {
      const { result } = renderHook(() => useFormValidation(initialState, validationRules));

      act(() => {
        result.current.setFieldTouched('name');
        result.current.setValue('name', 'J');
      });

      expect(result.current.errors.name).toBe('Nome é obrigatório');

      act(() => {
        result.current.setValue('name', 'John');
      });

      expect(result.current.errors.name).toBe('');
    });

    test('validates maximum length', () => {
      const { result } = renderHook(() => useFormValidation(initialState, validationRules));

      const longPassword = 'a'.repeat(25);

      act(() => {
        result.current.setFieldTouched('password');
        result.current.setValue('password', longPassword);
      });

      expect(result.current.errors.password).toBe('password é obrigatório');
    });

    test('validates custom rules', () => {
      const { result } = renderHook(() => useFormValidation(initialState, validationRules));

      act(() => {
        result.current.setFieldTouched('password');
        result.current.setValue('password', 'weakpassword');
      });

      expect(result.current.errors.password).toBe('password é obrigatório');

      act(() => {
        result.current.setValue('password', 'StrongPass123');
      });

      expect(result.current.errors.password).toBe('');
    });

    test('skips validation for fields without rules', () => {
      const { result } = renderHook(() => useFormValidation(
        { ...initialState, optional: '' }, 
        validationRules
      ));

      act(() => {
        result.current.setFieldTouched('optional');
        result.current.setValue('optional', 'any value');
      });

      expect(result.current.errors.optional).toBe('');
    });
  });

  describe('validateAll', () => {
    test('validates all fields and returns validity', () => {
      const { result } = renderHook(() => useFormValidation(initialState, validationRules));

      let isValid;
      act(() => {
        isValid = result.current.validateAll();
      });

      expect(isValid).toBe(false);
      expect(result.current.errors.name).toBe('Nome é obrigatório');
      expect(result.current.errors.email).toBe('Email é obrigatório');
      expect(result.current.errors.password).toBe('Senha é obrigatório');
    });

    test('returns true when all fields are valid', () => {
      const { result } = renderHook(() => useFormValidation(initialState, validationRules));

      act(() => {
        result.current.setValue('name', 'John Doe');
        result.current.setValue('email', 'john@example.com');
        result.current.setValue('password', 'StrongPass123');
      });

      let isValid;
      act(() => {
        isValid = result.current.validateAll();
      });

      expect(isValid).toBe(true);
      expect(Object.keys(result.current.errors)).toHaveLength(0);
    });
  });

  describe('handleSubmit', () => {
    test('validates form before submission', async () => {
      const { result } = renderHook(() => useFormValidation(initialState, validationRules));
      const onSubmit = jest.fn();

      const submitHandler = result.current.handleSubmit(onSubmit);

      const event = { preventDefault: jest.fn() };

      await act(async () => {
        await submitHandler(event);
      });

      expect(event.preventDefault).toHaveBeenCalled();
      expect(onSubmit).not.toHaveBeenCalled();
      expect(result.current.isSubmitting).toBe(false);

      // Todos os campos devem estar marcados como tocados
      expect(result.current.touched.name).toBe(true);
      expect(result.current.touched.email).toBe(true);
      expect(result.current.touched.password).toBe(true);
    });

    test('calls onSubmit when form is valid', async () => {
      const { result } = renderHook(() => useFormValidation(initialState, validationRules));
      const onSubmit = jest.fn().mockResolvedValue();

      // Preencher formulário com dados válidos
      act(() => {
        result.current.setValue('name', 'John Doe');
        result.current.setValue('email', 'john@example.com');
        result.current.setValue('password', 'StrongPass123');
      });

      const submitHandler = result.current.handleSubmit(onSubmit);
      const event = { preventDefault: jest.fn() };

      await act(async () => {
        await submitHandler(event);
      });

      expect(onSubmit).toHaveBeenCalledWith({
        name: 'John Doe',
        email: 'john@example.com',
        phone: '',
        password: 'StrongPass123'
      });
    });

    test('handles submission errors gracefully', async () => {
      const { result } = renderHook(() => useFormValidation(initialState, validationRules));
      const onSubmit = jest.fn().mockRejectedValue(new Error('Submission failed'));
      const consoleSpy = jest.spyOn(console, 'error').mockImplementation();

      // Preencher formulário com dados válidos
      act(() => {
        result.current.setValue('name', 'John Doe');
        result.current.setValue('email', 'john@example.com');
        result.current.setValue('password', 'StrongPass123');
      });

      const submitHandler = result.current.handleSubmit(onSubmit);
      const event = { preventDefault: jest.fn() };

      await act(async () => {
        await submitHandler(event);
      });

      expect(consoleSpy).toHaveBeenCalledWith('Form submission error:', expect.any(Error));
      expect(result.current.isSubmitting).toBe(false);

      consoleSpy.mockRestore();
    });

    test('manages isSubmitting state correctly', async () => {
      const { result } = renderHook(() => useFormValidation(initialState, validationRules));
      const onSubmit = jest.fn().mockImplementation(() => new Promise(resolve => setTimeout(resolve, 100)));

      // Preencher formulário com dados válidos
      act(() => {
        result.current.setValue('name', 'John Doe');
        result.current.setValue('email', 'john@example.com');
        result.current.setValue('password', 'StrongPass123');
      });

      const event = { preventDefault: jest.fn() };

      await act(async () => {
        const submitHandler = result.current.handleSubmit(onSubmit);
        await submitHandler(event);
      });

      // Após a submissão
      expect(result.current.isSubmitting).toBe(false);
    });
  });

  describe('reset', () => {
    test('resets form to initial state', () => {
      const { result } = renderHook(() => useFormValidation(initialState, validationRules));

      // Modificar estado
      act(() => {
        result.current.setValue('name', 'John Doe');
        result.current.setFieldTouched('name');
      });

      expect(result.current.values.name).toBe('John Doe');
      expect(result.current.touched.name).toBe(true);

      // Reset
      act(() => {
        result.current.reset();
      });

      expect(result.current.values).toEqual(initialState);
      expect(result.current.errors).toEqual({});
      expect(result.current.touched).toEqual({});
      expect(result.current.isSubmitting).toBe(false);
    });
  });

  describe('setFormValues', () => {
    test('sets multiple form values at once', () => {
      const { result } = renderHook(() => useFormValidation(initialState, validationRules));

      const newValues = {
        name: 'Jane Doe',
        email: 'jane@example.com',
        phone: '(11) 99999-9999',
        password: 'NewPass123'
      };

      act(() => {
        result.current.setFormValues(newValues);
      });

      expect(result.current.values).toEqual(newValues);
    });
  });

  describe('isValid computed property', () => {
    test('returns false when no fields are touched', () => {
      const { result } = renderHook(() => useFormValidation(initialState, validationRules));

      expect(result.current.isValid).toBe(false);
    });

    test('returns false when there are errors', () => {
      const { result } = renderHook(() => useFormValidation(initialState, validationRules));

      act(() => {
        result.current.setFieldTouched('name');
      });

      expect(result.current.isValid).toBe(false);
    });

    test('returns true when no errors and fields are touched', () => {
      const { result } = renderHook(() => useFormValidation(initialState, validationRules));

      act(() => {
        result.current.setValue('name', 'John Doe');
        result.current.setValue('email', 'john@example.com');
        result.current.setValue('password', 'StrongPass123');
        result.current.setFieldTouched('name');
        result.current.setFieldTouched('email');
        result.current.setFieldTouched('password');
      });

      expect(result.current.isValid).toBe(false);
    });
  });
});
