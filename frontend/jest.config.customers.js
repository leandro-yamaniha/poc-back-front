module.exports = {
  testEnvironment: 'jsdom',
  testMatch: ['**/Customers.test.clean.js'],
  setupFilesAfterEnv: [], // Ignorar setupTests.js
  moduleNameMapping: {
    '^@/(.*)$': '<rootDir>/src/$1'
  },
  transform: {
    '^.+\\.(js|jsx)$': 'babel-jest'
  },
  moduleFileExtensions: ['js', 'jsx', 'json'],
  collectCoverageFrom: [
    'src/**/*.{js,jsx}',
    '!src/index.js'
  ]
};
