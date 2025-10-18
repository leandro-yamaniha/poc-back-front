using Cassandra;
using BeautySalonAPI.Data;
using BeautySalonAPI.Models;
using CassandraSession = Cassandra.ISession;

namespace BeautySalonAPI.Repositories;

public class ServiceRepository : IServiceRepository
{
    private readonly CassandraSession _session;

    public ServiceRepository(CassandraContext context)
    {
        _session = context.Session;
    }

    public async Task<IEnumerable<Service>> GetAllAsync()
    {
        var statement = new SimpleStatement("SELECT * FROM services");
        var result = await _session.ExecuteAsync(statement);
        
        return result.Select(row => new Service
        {
            Id = row.GetValue<Guid>("id"),
            Name = row.GetValue<string>("name"),
            Description = row.GetValue<string>("description"),
            Price = row.GetValue<decimal>("price"),
            DurationMinutes = row.GetValue<int>("duration_minutes"),
            Category = row.GetValue<string>("category"),
            Active = row.GetValue<bool>("active"),
            CreatedAt = row.GetValue<DateTimeOffset>("created_at").DateTime
        });
    }

    public async Task<Service?> GetByIdAsync(Guid id)
    {
        var statement = new SimpleStatement("SELECT * FROM services WHERE id = ?", id);
        var result = await _session.ExecuteAsync(statement);
        var row = result.FirstOrDefault();

        if (row == null) return null;

        return new Service
        {
            Id = row.GetValue<Guid>("id"),
            Name = row.GetValue<string>("name"),
            Description = row.GetValue<string>("description"),
            Price = row.GetValue<decimal>("price"),
            DurationMinutes = row.GetValue<int>("duration_minutes"),
            Category = row.GetValue<string>("category"),
            Active = row.GetValue<bool>("active"),
            CreatedAt = row.GetValue<DateTimeOffset>("created_at").DateTime
        };
    }

    public async Task<Service> CreateAsync(Service service)
    {
        service.Id = Guid.NewGuid();
        service.CreatedAt = DateTime.UtcNow;

        var statement = new SimpleStatement(
            "INSERT INTO services (id, name, description, price, duration_minutes, category, active, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
            service.Id, service.Name, service.Description, service.Price, service.DurationMinutes, service.Category, service.Active, service.CreatedAt);

        await _session.ExecuteAsync(statement);
        return service;
    }

    public async Task<Service?> UpdateAsync(Guid id, Service service)
    {
        var existing = await GetByIdAsync(id);
        if (existing == null) return null;

        var statement = new SimpleStatement(
            "UPDATE services SET name = ?, description = ?, price = ?, duration_minutes = ?, category = ?, active = ? WHERE id = ?",
            service.Name, service.Description, service.Price, service.DurationMinutes, service.Category, service.Active, id);

        await _session.ExecuteAsync(statement);
        
        service.Id = id;
        service.CreatedAt = existing.CreatedAt;
        return service;
    }

    public async Task<bool> DeleteAsync(Guid id)
    {
        var existing = await GetByIdAsync(id);
        if (existing == null) return false;

        var statement = new SimpleStatement("DELETE FROM services WHERE id = ?", id);
        await _session.ExecuteAsync(statement);
        return true;
    }
}
