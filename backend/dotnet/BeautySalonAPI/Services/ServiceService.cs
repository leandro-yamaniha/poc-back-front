using BeautySalonAPI.Data;
using BeautySalonAPI.Models;
using Microsoft.EntityFrameworkCore;

namespace BeautySalonAPI.Services;

public interface IServiceService
{
    Task<IEnumerable<Service>> GetAllServicesAsync();
    Task<Service?> GetServiceByIdAsync(Guid id);
    Task<IEnumerable<Service>> GetActiveServicesAsync();
    Task<IEnumerable<Service>> GetServicesByCategoryAsync(string category);
    Task<Service> CreateServiceAsync(Service service);
    Task<Service> UpdateServiceAsync(Guid id, Service service);
    Task DeleteServiceAsync(Guid id);
}

public class ServiceService : IServiceService
{
    private readonly BeautySalonDbContext _context;

    public ServiceService(BeautySalonDbContext context)
    {
        _context = context;
    }

    public async Task<IEnumerable<Service>> GetAllServicesAsync()
    {
        return await _context.Services.ToListAsync();
    }

    public async Task<Service?> GetServiceByIdAsync(Guid id)
    {
        return await _context.Services.FindAsync(id);
    }

    public async Task<IEnumerable<Service>> GetActiveServicesAsync()
    {
        return await _context.Services
            .Where(s => s.Active)
            .ToListAsync();
    }

    public async Task<IEnumerable<Service>> GetServicesByCategoryAsync(string category)
    {
        return await _context.Services
            .Where(s => s.Category == category && s.Active)
            .ToListAsync();
    }

    public async Task<Service> CreateServiceAsync(Service service)
    {
        _context.Services.Add(service);
        await _context.SaveChangesAsync();
        return service;
    }

    public async Task<Service> UpdateServiceAsync(Guid id, Service service)
    {
        var existingService = await _context.Services.FindAsync(id);
        if (existingService == null)
            throw new KeyNotFoundException($"Service with ID {id} not found");

        existingService.Name = service.Name;
        existingService.Description = service.Description;
        existingService.Price = service.Price;
        existingService.DurationMinutes = service.DurationMinutes;
        existingService.Category = service.Category;
        existingService.Active = service.Active;
        existingService.UpdatedAt = DateTime.UtcNow;

        await _context.SaveChangesAsync();
        return existingService;
    }

    public async Task DeleteServiceAsync(Guid id)
    {
        var service = await _context.Services.FindAsync(id);
        if (service == null)
            throw new KeyNotFoundException($"Service with ID {id} not found");

        _context.Services.Remove(service);
        await _context.SaveChangesAsync();
    }
}
