using BeautySalonAPI.Data;
using BeautySalonAPI.Models;
using Microsoft.EntityFrameworkCore;

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
    private readonly BeautySalonDbContext _context;

    public CustomerService(BeautySalonDbContext context)
    {
        _context = context;
    }

    public async Task<IEnumerable<Customer>> GetAllCustomersAsync()
    {
        return await _context.Customers.ToListAsync();
    }

    public async Task<Customer?> GetCustomerByIdAsync(Guid id)
    {
        return await _context.Customers.FindAsync(id);
    }

    public async Task<IEnumerable<Customer>> GetCustomersByNameAsync(string name)
    {
        return await _context.Customers
            .Where(c => c.Name.Contains(name))
            .ToListAsync();
    }

    public async Task<Customer> CreateCustomerAsync(Customer customer)
    {
        _context.Customers.Add(customer);
        await _context.SaveChangesAsync();
        return customer;
    }

    public async Task<Customer> UpdateCustomerAsync(Guid id, Customer customer)
    {
        var existingCustomer = await _context.Customers.FindAsync(id);
        if (existingCustomer == null)
            throw new KeyNotFoundException($"Customer with ID {id} not found");

        existingCustomer.Name = customer.Name;
        existingCustomer.Email = customer.Email;
        existingCustomer.Phone = customer.Phone;
        existingCustomer.Address = customer.Address;
        existingCustomer.UpdatedAt = DateTime.UtcNow;

        await _context.SaveChangesAsync();
        return existingCustomer;
    }

    public async Task DeleteCustomerAsync(Guid id)
    {
        var customer = await _context.Customers.FindAsync(id);
        if (customer == null)
            throw new KeyNotFoundException($"Customer with ID {id} not found");

        _context.Customers.Remove(customer);
        await _context.SaveChangesAsync();
    }
}
