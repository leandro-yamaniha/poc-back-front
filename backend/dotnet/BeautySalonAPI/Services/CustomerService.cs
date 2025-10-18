using BeautySalonAPI.Models;
using BeautySalonAPI.Repositories;

namespace BeautySalonAPI.Services;

public interface ICustomerService
{
    Task<IEnumerable<Customer>> GetAllCustomersAsync();
    Task<Customer?> GetCustomerByIdAsync(Guid id);
    Task<IEnumerable<Customer>> GetCustomersByNameAsync(string name);
    Task<Customer> CreateCustomerAsync(Customer customer);
    Task<Customer> UpdateCustomerAsync(Guid id, Customer customer);
    Task DeleteCustomerAsync(Guid id);
}

public class CustomerService : ICustomerService
{
    private readonly ICustomerRepository _repository;

    public CustomerService(ICustomerRepository repository)
    {
        _repository = repository;
    }

    public async Task<IEnumerable<Customer>> GetAllCustomersAsync()
    {
        return await _repository.GetAllAsync();
    }

    public async Task<Customer?> GetCustomerByIdAsync(Guid id)
    {
        return await _repository.GetByIdAsync(id);
    }

    public async Task<IEnumerable<Customer>> GetCustomersByNameAsync(string name)
    {
        var allCustomers = await _repository.GetAllAsync();
        return allCustomers.Where(c => c.Name.Contains(name, StringComparison.OrdinalIgnoreCase));
    }

    public async Task<Customer> CreateCustomerAsync(Customer customer)
    {
        return await _repository.CreateAsync(customer);
    }

    public async Task<Customer> UpdateCustomerAsync(Guid id, Customer customer)
    {
        var updated = await _repository.UpdateAsync(id, customer);
        if (updated == null)
            throw new KeyNotFoundException($"Customer with ID {id} not found");

        return updated;
    }

    public async Task DeleteCustomerAsync(Guid id)
    {
        var deleted = await _repository.DeleteAsync(id);
        if (!deleted)
            throw new KeyNotFoundException($"Customer with ID {id} not found");
    }
}
