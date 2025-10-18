using BeautySalonAPI.Models;
using BeautySalonAPI.Repositories;

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
    private readonly IServiceRepository _repository;

    public ServiceService(IServiceRepository repository)
    {
        _repository = repository;
    }

    public async Task<IEnumerable<Service>> GetAllServicesAsync()
    {
        return await _repository.GetAllAsync();
    }

    public async Task<Service?> GetServiceByIdAsync(Guid id)
    {
        return await _repository.GetByIdAsync(id);
    }

    public async Task<IEnumerable<Service>> GetActiveServicesAsync()
    {
        var allServices = await _repository.GetAllAsync();
        return allServices.Where(s => s.Active);
    }

    public async Task<IEnumerable<Service>> GetServicesByCategoryAsync(string category)
    {
        var allServices = await _repository.GetAllAsync();
        return allServices.Where(s => s.Category == category && s.Active);
    }

    public async Task<Service> CreateServiceAsync(Service service)
    {
        return await _repository.CreateAsync(service);
    }

    public async Task<Service> UpdateServiceAsync(Guid id, Service service)
    {
        var updated = await _repository.UpdateAsync(id, service);
        if (updated == null)
            throw new KeyNotFoundException($"Service with ID {id} not found");

        return updated;
    }

    public async Task DeleteServiceAsync(Guid id)
    {
        var deleted = await _repository.DeleteAsync(id);
        if (!deleted)
            throw new KeyNotFoundException($"Service with ID {id} not found");
    }
}
