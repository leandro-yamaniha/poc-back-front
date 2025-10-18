using Cassandra;
using BeautySalonAPI.Data;
using BeautySalonAPI.Models;

namespace BeautySalonAPI.Repositories;

public class CustomerRepository : ICustomerRepository
{
    private readonly ISession _session;

    public CustomerRepository(CassandraContext context)
    {
        _session = context.Session;
    }

    public async Task<IEnumerable<Customer>> GetAllAsync()
    {
        var statement = new SimpleStatement("SELECT * FROM customers");
        var result = await _session.ExecuteAsync(statement);
        
        return result.Select(row => new Customer
        {
            Id = row.GetValue<Guid>("id"),
            Name = row.GetValue<string>("name"),
            Email = row.GetValue<string>("email"),
            Phone = row.GetValue<string>("phone"),
            CreatedAt = row.GetValue<DateTimeOffset>("created_at")
        });
    }

    public async Task<Customer?> GetByIdAsync(Guid id)
    {
        var statement = new SimpleStatement("SELECT * FROM customers WHERE id = ?", id);
        var result = await _session.ExecuteAsync(statement);
        var row = result.FirstOrDefault();

        if (row == null) return null;

        return new Customer
        {
            Id = row.GetValue<Guid>("id"),
            Name = row.GetValue<string>("name"),
            Email = row.GetValue<string>("email"),
            Phone = row.GetValue<string>("phone"),
            CreatedAt = row.GetValue<DateTimeOffset>("created_at")
        };
    }

    public async Task<Customer> CreateAsync(Customer customer)
    {
        customer.Id = Guid.NewGuid();
        customer.CreatedAt = DateTimeOffset.UtcNow;

        var statement = new SimpleStatement(
            "INSERT INTO customers (id, name, email, phone, created_at) VALUES (?, ?, ?, ?, ?)",
            customer.Id, customer.Name, customer.Email, customer.Phone, customer.CreatedAt);

        await _session.ExecuteAsync(statement);
        return customer;
    }

    public async Task<Customer?> UpdateAsync(Guid id, Customer customer)
    {
        var existing = await GetByIdAsync(id);
        if (existing == null) return null;

        var statement = new SimpleStatement(
            "UPDATE customers SET name = ?, email = ?, phone = ? WHERE id = ?",
            customer.Name, customer.Email, customer.Phone, id);

        await _session.ExecuteAsync(statement);
        
        customer.Id = id;
        customer.CreatedAt = existing.CreatedAt;
        return customer;
    }

    public async Task<bool> DeleteAsync(Guid id)
    {
        var existing = await GetByIdAsync(id);
        if (existing == null) return false;

        var statement = new SimpleStatement("DELETE FROM customers WHERE id = ?", id);
        await _session.ExecuteAsync(statement);
        return true;
    }
}
