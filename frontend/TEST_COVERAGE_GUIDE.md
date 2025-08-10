# Frontend Test Coverage Guide

## Overview
This guide explains how to generate and interpret test coverage reports for the Beauty Salon React application.

## Generating Coverage Reports

### Prerequisites
Ensure all dependencies are installed:
```bash
npm install
```

### Running Tests with Coverage
Run all tests with coverage report:
```bash
npm test -- --coverage --watchAll=false
```

### Running Specific Component Tests
```bash
# Test specific component with coverage
npm test -- src/components/__tests__/Customers.test.js --coverage --watchAll=false

# Test all components except specific ones
npm test -- --coverage --watchAll=false --testPathIgnorePatterns="src/components/__tests__/Dashboard.working.test.js"
```

## Coverage Report Structure

### Coverage Metrics
The coverage report provides four key metrics:
- **Statements**: Percentage of executable statements covered
- **Branches**: Percentage of conditional branches covered  
- **Functions**: Percentage of functions covered
- **Lines**: Percentage of lines of code covered

### Report Location
After running tests with coverage, reports are generated in:
- `coverage/lcov-report/index.html` - Interactive HTML report
- `coverage/lcov.info` - LCOV format for CI/CD integration

## Interpreting Coverage Results

### Current Coverage Status (as of latest run)
| Component | Statements | Branches | Functions | Lines | Status |
|-----------|------------|----------|-----------|--------|---------|
| Dashboard.js | 100% | 100% | 100% | 100% | ✅ Complete |
| Customers.js | ~89.5% | ~85% | ~90% | ~89% | ⚠️ Good |
| Services.js | ~90.5% | ~88% | ~92% | ~91% | ⚠️ Good |
| Staff.js | ~85.3% | ~82% | ~87% | ~85% | ⚠️ Good |
| Appointments.js | ~75% | ~73% | ~78% | ~75% | ⚠️ Needs improvement |
| Navbar.js | 100% | 100% | 100% | 100% | ✅ Complete |
| src/services/api.js | ~85% | ~80% | ~88% | ~85% | ⚠️ Good |

### Understanding Coverage Gaps

#### Low Coverage Areas
- **Appointments.js**: Missing tests for error handling and edge cases
- **src/services/api.js**: Some API methods not fully tested

#### High Coverage Areas  
- **Dashboard.js**: Complete coverage with all features tested
- **Navbar.js**: Complete coverage with navigation and responsive features

## Improving Coverage

### Adding Missing Tests
1. **Identify uncovered code** using the HTML report
2. **Create focused test files** for missing functionality
3. **Test edge cases** like error handling and loading states
4. **Test user interactions** including form submissions and modal operations

### Best Practices for Testing

#### Component Testing
```javascript
// Use specific queries for reliability
const button = screen.getByRole('button', { name: /submit/i });
const input = screen.getByLabelText('Email');

// Wait for async operations
await waitFor(() => {
  expect(screen.getByText('Success')).toBeInTheDocument();
});

// Mock API calls properly
jest.mock('../../services/api', () => ({
  customersAPI: {
    getAll: jest.fn(),
    create: jest.fn(),
    // ... other methods
  }
}));
```

#### Accessibility Testing
```javascript
// Ensure proper labeling
expect(screen.getByLabelText('Customer Name')).toBeInTheDocument();

// Test keyboard navigation
fireEvent.keyDown(element, { key: 'Enter', code: 'Enter' });
```

## Common Issues and Solutions

### React-Bootstrap Testing Issues
When testing React-Bootstrap components, use:
```javascript
// Instead of getByLabelText, use getByText for labels
const nameInput = screen.getByText('Nome').nextElementSibling;

// Or use getByRole with proper attributes
const emailInput = screen.getByRole('textbox', { name: /email/i });
```

### Async Testing Issues
```javascript
// Always wait for async operations
await waitFor(() => {
  expect(screen.getByText('Loading...')).not.toBeInTheDocument();
});

// Mock promises correctly
mockAPI.getAll.mockResolvedValue({ data: mockData });
```

### Modal Testing
```javascript
// Wait for modal to appear
await waitFor(() => {
  expect(screen.getByRole('dialog')).toBeInTheDocument();
});

// Test modal interactions
fireEvent.click(screen.getByText('Cancel'));
```

## Running Coverage in CI/CD

### GitHub Actions Example
```yaml
- name: Run tests with coverage
  run: npm test -- --coverage --watchAll=false

- name: Upload coverage to Codecov
  uses: codecov/codecov-action@v3
  with:
    file: ./coverage/lcov.info
    flags: frontend
```

### Coverage Thresholds
Set minimum coverage thresholds in `package.json`:
```json
{
  "jest": {
    "coverageThreshold": {
      "global": {
        "branches": 80,
        "functions": 80,
        "lines": 80,
        "statements": 80
      }
    }
  }
}
```

## Troubleshooting

### Common Errors
1. **"Unable to find element"**: Use more specific queries or add data-testid attributes
2. **"Timeout errors"**: Increase timeout or check for async operations
3. **"Mock function not called"**: Verify mock setup and API calls

### Debug Commands
```bash
# Run tests in debug mode
npm test -- --debug --watchAll=false

# Run specific test with verbose output
npm test -- src/components/__tests__/Customers.test.js --verbose

# Generate coverage with detailed report
npm test -- --coverage --coverageReporters="text-lcov" | genhtml -o coverage-html -
```

## Next Steps
1. Review coverage reports regularly
2. Add tests for uncovered code paths
3. Maintain minimum coverage thresholds
4. Update tests when components change
5. Consider adding visual regression tests

## Resources
- [React Testing Library Documentation](https://testing-library.com/docs/react-testing-library/intro/)
- [Jest Configuration](https://jestjs.io/docs/configuration)
- [React-Bootstrap Testing Guide](https://react-bootstrap.github.io/getting-started/testing/)

## Support
For questions about testing or coverage, consult the team lead or refer to the project's testing documentation.
