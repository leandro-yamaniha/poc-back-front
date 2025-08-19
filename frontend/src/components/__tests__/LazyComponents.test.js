import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';

// Use the mocked version from setupTests.js
import { withSuspense, LazyComponentWrapper } from '../LazyComponents';

// Simple test components
const TestComponent = ({ message = 'Test Component' }) => <div>{message}</div>;
const SimpleComponent = () => <div>Simple Component</div>;

describe('withSuspense HOC', () => {
  test('renders component successfully', () => {
    const WrappedComponent = withSuspense(TestComponent);
    
    render(<WrappedComponent message="Hello World" />);
    
    expect(screen.getByText('Hello World')).toBeInTheDocument();
  });

  test('wraps component with suspense functionality', () => {
    const WrappedComponent = withSuspense(SimpleComponent);
    
    render(<WrappedComponent />);
    
    expect(screen.getByText('Simple Component')).toBeInTheDocument();
  });
});

describe('LazyComponentWrapper', () => {
  test('renders children components', () => {
    render(
      <LazyComponentWrapper>
        <TestComponent message="Wrapped Component" />
      </LazyComponentWrapper>
    );
    
    expect(screen.getByText('Wrapped Component')).toBeInTheDocument();
  });

  test('provides wrapper functionality', () => {
    render(
      <LazyComponentWrapper>
        <SimpleComponent />
      </LazyComponentWrapper>
    );
    
    expect(screen.getByText('Simple Component')).toBeInTheDocument();
  });
});
