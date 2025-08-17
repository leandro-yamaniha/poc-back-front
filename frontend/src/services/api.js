import axios from 'axios';

// REACT_APP_API_URL should point to the full API prefix (e.g., http://localhost:8082/api/v1)
// Default to Go backend local port with v1 prefix
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api/v1';

const api = axios.create({
  // Do NOT append extra segments; REACT_APP_API_URL already contains the prefix
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Customers API
export const customersAPI = {
  getAll: () => api.get('/customers'),
  getById: (id) => api.get(`/customers/${id}`),
  getByEmail: (email) => api.get(`/customers/email/${email}`),
  search: (name) => api.get(`/customers/search?name=${name}`),
  create: (customer) => api.post('/customers', customer),
  update: (id, customer) => api.put(`/customers/${id}`, customer),
  delete: (id) => api.delete(`/customers/${id}`),
};

// Services API
export const servicesAPI = {
  getAll: () => api.get('/services'),
  getActive: () => api.get('/services/active'),
  getById: (id) => api.get(`/services/${id}`),
  getByCategory: (category) => api.get(`/services/category/${category}`),
  getActiveByCat: (category) => api.get(`/services/category/${category}/active`),
  create: (service) => api.post('/services', service),
  update: (id, service) => api.put(`/services/${id}`, service),
  delete: (id) => api.delete(`/services/${id}`),
};

// Staff API
export const staffAPI = {
  getAll: () => api.get('/staff'),
  getActive: () => api.get('/staff/active'),
  getById: (id) => api.get(`/staff/${id}`),
  getByRole: (role) => api.get(`/staff/role/${role}`),
  getActiveByRole: (role) => api.get(`/staff/role/${role}/active`),
  create: (staff) => api.post('/staff', staff),
  update: (id, staff) => api.put(`/staff/${id}`, staff),
  delete: (id) => api.delete(`/staff/${id}`),
};

// Appointments API
export const appointmentsAPI = {
  getAll: () => api.get('/appointments'),
  getById: (id) => api.get(`/appointments/${id}`),
  getByDate: (date) => api.get(`/appointments/date/${date}`),
  getByCustomer: (customerId) => api.get(`/appointments/customer/${customerId}`),
  getByStaff: (staffId) => api.get(`/appointments/staff/${staffId}`),
  getByStatus: (status) => api.get(`/appointments/status/${status}`),
  getByDateAndStaff: (date, staffId) => api.get(`/appointments/date/${date}/staff/${staffId}`),
  create: (appointment) => api.post('/appointments', appointment),
  update: (id, appointment) => api.put(`/appointments/${id}`, appointment),
  delete: (id) => api.delete(`/appointments/${id}`),
};

// Individual function exports for test compatibility
export const getCustomers = () => customersAPI.getAll().then(res => res.data);
export const getCustomerById = (id) => customersAPI.getById(id).then(res => res.data);
export const createCustomer = (customer) => customersAPI.create(customer).then(res => res.data);
export const updateCustomer = (id, customer) => customersAPI.update(id, customer).then(res => res.data);
export const deleteCustomer = (id) => customersAPI.delete(id).then(res => res.data);
export const searchCustomers = (name) => customersAPI.search(name).then(res => res.data);

export const getServices = () => servicesAPI.getAll().then(res => res.data);
export const getActiveServices = () => servicesAPI.getActive().then(res => res.data);
export const getServicesByCategory = (category) => servicesAPI.getByCategory(category).then(res => res.data);
export const createService = (service) => servicesAPI.create(service).then(res => res.data);
export const updateService = (id, service) => servicesAPI.update(id, service).then(res => res.data);
export const deleteService = (id) => servicesAPI.delete(id).then(res => res.data);

export const getStaff = () => staffAPI.getAll().then(res => res.data);
export const getActiveStaff = () => staffAPI.getActive().then(res => res.data);
export const createStaff = (staff) => staffAPI.create(staff).then(res => res.data);
export const updateStaff = (id, staff) => staffAPI.update(id, staff).then(res => res.data);
export const deleteStaff = (id) => staffAPI.delete(id).then(res => res.data);

export const getAppointments = () => appointmentsAPI.getAll().then(res => res.data);
export const getAppointmentsByDate = (date) => appointmentsAPI.getByDate(date).then(res => res.data);
export const createAppointment = (appointment) => appointmentsAPI.create(appointment).then(res => res.data);
export const updateAppointment = (id, appointment) => appointmentsAPI.update(id, appointment).then(res => res.data);
export const deleteAppointment = (id) => appointmentsAPI.delete(id).then(res => res.data);

export default api;
