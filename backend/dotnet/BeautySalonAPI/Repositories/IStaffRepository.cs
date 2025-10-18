using BeautySalonAPI.Models;

namespace BeautySalonAPI.Repositories;

public interface IStaffRepository
{
    Task<IEnumerable<Staff>> GetAllAsync();
    Task<Staff?> GetByIdAsync(Guid id);
    Task<Staff> CreateAsync(Staff staff);
    Task<Staff?> UpdateAsync(Guid id, Staff staff);
    Task<bool> DeleteAsync(Guid id);
}
