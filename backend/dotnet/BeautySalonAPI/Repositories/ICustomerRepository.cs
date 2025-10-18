using BeautySalonAPI.Models;

namespace BeautySalonAPI.Repositories;

public interface ICustomerRepository
{
    Task<IEnumerable<Customer>> GetAllAsync();
    Task<Customer?> GetByIdAsync(Guid id);
    Task<Customer> CreateAsync(Customer customer);
    Task<Customer?> UpdateAsync(Guid id, Customer customer);
    Task<bool> DeleteAsync(Guid id);
}
