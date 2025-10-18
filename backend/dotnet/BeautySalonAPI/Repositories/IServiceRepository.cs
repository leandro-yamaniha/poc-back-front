using BeautySalonAPI.Models;

namespace BeautySalonAPI.Repositories;

public interface IServiceRepository
{
    Task<IEnumerable<Service>> GetAllAsync();
    Task<Service?> GetByIdAsync(Guid id);
    Task<Service> CreateAsync(Service service);
    Task<Service?> UpdateAsync(Guid id, Service service);
    Task<bool> DeleteAsync(Guid id);
}
