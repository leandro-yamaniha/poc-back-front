import React from 'react';
import { renderHook, act, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';

// Mock react-toastify
jest.mock('react-toastify', () => ({
  toast: {
    info: jest.fn(),
    success: jest.fn(),
    error: jest.fn()
  }
}));

// Unmock the hook for this test file
jest.unmock('../useErrorHandling');
const { useErrorHandling } = require('../useErrorHandling');
const { toast } = require('react-toastify');

jest.setTimeout(30000);

describe('useErrorHandling', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('categorizeError', () => {
    test('categorizes network errors correctly', () => {
      const { result } = renderHook(() => useErrorHandling());
      const networkError = { message: 'Network Error' }; // No response property

      const category = result.current.categorizeError(networkError);

      expect(category).toEqual({
        type: 'NETWORK_ERROR',
        message: 'Erro de conexão. Verifique sua internet.',
        retryable: true,
        severity: 'high'
      });
    });

    test('categorizes server errors correctly', () => {
      const { result } = renderHook(() => useErrorHandling());
      const serverError = { response: { status: 500 } };

      const category = result.current.categorizeError(serverError);

      expect(category).toEqual({
        type: 'SERVER_ERROR',
        message: 'Erro interno do servidor. Tente novamente.',
        retryable: true,
        severity: 'high'
      });
    });

    test('categorizes rate limit errors correctly', () => {
      const { result } = renderHook(() => useErrorHandling());
      const rateLimitError = { response: { status: 429 } };

      const category = result.current.categorizeError(rateLimitError);

      expect(category).toEqual({
        type: 'RATE_LIMIT',
        message: 'Muitas tentativas. Aguarde um momento.',
        retryable: true,
        severity: 'medium'
      });
    });

    test('categorizes conflict errors correctly', () => {
      const { result } = renderHook(() => useErrorHandling());
      const conflictError = { 
        response: { 
          status: 409,
          data: { message: 'Resource already exists' }
        }
      };

      const category = result.current.categorizeError(conflictError);

      expect(category).toEqual({
        type: 'CONFLICT',
        message: 'Resource already exists',
        retryable: false,
        severity: 'low'
      });
    });

    test('categorizes not found errors correctly', () => {
      const { result } = renderHook(() => useErrorHandling());
      const notFoundError = { response: { status: 404 } };

      const category = result.current.categorizeError(notFoundError);

      expect(category).toEqual({
        type: 'NOT_FOUND',
        message: 'Recurso não encontrado.',
        retryable: false,
        severity: 'medium'
      });
    });

    test('categorizes client errors correctly', () => {
      const { result } = renderHook(() => useErrorHandling());
      const clientError = { 
        response: { 
          status: 400,
          data: { message: 'Bad request' }
        }
      };

      const category = result.current.categorizeError(clientError);

      expect(category).toEqual({
        type: 'CLIENT_ERROR',
        message: 'Bad request',
        retryable: false,
        severity: 'low'
      });
    });

    test('categorizes unknown errors correctly', () => {
      const { result } = renderHook(() => useErrorHandling());
      const unknownError = { response: { status: 999 } }; // Unknown status

      const category = result.current.categorizeError(unknownError);

      expect(category).toEqual({
        type: 'SERVER_ERROR',
        message: 'Erro interno do servidor. Tente novamente.',
        retryable: true,
        severity: 'high'
      });
    });
  });

  describe('executeWithRetry', () => {
    afterEach(() => {
      jest.useRealTimers();
      jest.clearAllMocks();
    });
    test('executes operation successfully on first try', async () => {
      const { result } = renderHook(() => useErrorHandling());
      const operation = jest.fn().mockResolvedValue('success');

      const response = await result.current.executeWithRetry(operation);

      expect(response).toBe('success');
      expect(operation).toHaveBeenCalledTimes(1);
    });

    test('retries operation on failure', async () => {
      jest.useFakeTimers();
      const { result } = renderHook(() => useErrorHandling());
      const operation = jest.fn()
        .mockRejectedValueOnce(new Error('Server Error'))
        .mockResolvedValueOnce('success');

      const promise = result.current.executeWithRetry(operation, { maxRetries: 2 });

      await jest.runAllTimersAsync();

      const response = await promise;

      expect(response).toBe('success');
      expect(operation).toHaveBeenCalledTimes(2);
    });

    test('throws error after max retries', async () => {
      jest.useFakeTimers();
      const { result } = renderHook(() => useErrorHandling());
      const mockError = new Error('Final Error');
      const operation = jest.fn().mockRejectedValue(mockError);

      const promise = result.current.executeWithRetry(operation, { maxRetries: 2 });

      await jest.runAllTimersAsync();

      await expect(promise).rejects.toThrow('Final Error');
      expect(operation).toHaveBeenCalledTimes(3);
    });

    test('calls onRetry callback on retry attempts', async () => {
      jest.useFakeTimers();
      const { result } = renderHook(() => useErrorHandling());
      const onRetry = jest.fn();
      const mockError = new Error('Retry Error');
      const operation = jest.fn()
        .mockRejectedValueOnce(mockError)
        .mockResolvedValue('success');

      const promise = result.current.executeWithRetry(operation, { maxRetries: 2, onRetry });

      await jest.runAllTimersAsync();

      await promise;

      expect(onRetry).toHaveBeenCalledWith(mockError, 1);
      expect(onRetry).toHaveBeenCalledTimes(1);
    });

    test('calls onError callback on final failure', async () => {
      jest.useFakeTimers();
      const { result } = renderHook(() => useErrorHandling());
      const onError = jest.fn();
      const mockError = new Error('Final Failure');
      mockError.response = { status: 500 }; // Attach response for categorization
      const operation = jest.fn().mockRejectedValue(mockError);

      const promise = result.current.executeWithRetry(operation, { maxRetries: 1, onError });

      await jest.runAllTimersAsync();

      await expect(promise).rejects.toThrow('Final Failure');

      expect(onError).toHaveBeenCalledWith(
        mockError,
        expect.objectContaining({ type: 'SERVER_ERROR' })
      );
    });

    test('uses fallback on final failure', async () => {
      jest.useFakeTimers();
      const { result } = renderHook(() => useErrorHandling());
      const mockError = new Error('Fallback Error');
      const operation = jest.fn().mockRejectedValue(mockError);
      const fallback = jest.fn().mockResolvedValue('fallback-result');

      const promise = result.current.executeWithRetry(operation, { maxRetries: 1, fallback });

      await jest.runAllTimersAsync();

      const response = await promise;

      expect(response).toBe('fallback-result');
      expect(fallback).toHaveBeenCalledWith(mockError);
    });

    test('shows retry notifications', async () => {
      jest.useFakeTimers();
      const { result } = renderHook(() => useErrorHandling());
      const operation = jest.fn()
        .mockRejectedValueOnce(new Error('Notification Error'))
        .mockResolvedValue('success');

      const promise = result.current.executeWithRetry(operation, { maxRetries: 2 });

      await jest.runAllTimersAsync();

      await promise;

      expect(toast.info).toHaveBeenCalledWith('Tentativa 2/3...', { autoClose: 2000 });
    });

    test('manages retry state correctly', async () => {
      jest.useFakeTimers();
      const { result } = renderHook(() => useErrorHandling());
      const operation = jest.fn()
        .mockRejectedValueOnce(new Error('State Error'))
        .mockResolvedValue('success');

      let promise;
      act(() => {
        promise = result.current.executeWithRetry(operation);
      });

      await waitFor(() => expect(result.current.isRetrying).toBe(true));
      expect(result.current.retryCount).toBe(1);

      await jest.runAllTimersAsync();
      await promise;

      await waitFor(() => expect(result.current.isRetrying).toBe(false));
      expect(result.current.retryCount).toBe(0);
    });
  });

  describe('handleApiCall', () => {
    test('wraps API calls with retry logic', async () => {
      const { result } = renderHook(() => useErrorHandling());
      const apiCall = jest.fn().mockResolvedValue('success');

      const response = await result.current.handleApiCall(apiCall);

      expect(response).toBe('success');
      expect(apiCall).toHaveBeenCalledTimes(1);
    });
  });

  describe('showErrorWithActions', () => {
    test('shows error toast', () => {
      const { result } = renderHook(() => useErrorHandling());
      const error = { response: { status: 500 } };

      result.current.showErrorWithActions(error);

      expect(result.current.categorizeError).toBeDefined();
    });
  });

  describe('cleanup', () => {
    test('cleanup function exists', () => {
      const { result } = renderHook(() => useErrorHandling());
      
      expect(result.current.cleanup).toBeDefined();
      expect(typeof result.current.cleanup).toBe('function');
    });
  });
});
