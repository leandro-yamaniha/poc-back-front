using BeautySalonAPI.Models;
using BeautySalonAPI.Repositories;

namespace BeautySalonAPI.Services;

public interface IStaffService
{
    Task<IEnumerable<Staff>> GetAllStaffAsync();
    Task<Staff?> GetStaffByIdAsync(Guid id);
    Task<IEnumerable<Staff>> GetActiveStaffAsync();
    Task<IEnumerable<Staff>> GetStaffByRoleAsync(string role);
    Task<Staff> CreateStaffAsync(Staff staff);
    Task<Staff> UpdateStaffAsync(Guid id, Staff staff);
    Task DeleteStaffAsync(Guid id);
}

public class StaffService : IStaffService
{
    private readonly IStaffRepository _repository;

    public StaffService(IStaffRepository repository)
    {
        _repository = repository;
    }

    public async Task<IEnumerable<Staff>> GetAllStaffAsync()
    {
        return await _repository.GetAllAsync();
    }

    public async Task<Staff?> GetStaffByIdAsync(Guid id)
    {
        return await _repository.GetByIdAsync(id);
    }

    public async Task<IEnumerable<Staff>> GetActiveStaffAsync()
    {
        var allStaff = await _repository.GetAllAsync();
        return allStaff.Where(s => s.Active);
    }

    public async Task<IEnumerable<Staff>> GetStaffByRoleAsync(string role)
    {
        var allStaff = await _repository.GetAllAsync();
        return allStaff.Where(s => s.Role == role && s.Active);
    }

    public async Task<Staff> CreateStaffAsync(Staff staff)
    {
        return await _repository.CreateAsync(staff);
    }

    public async Task<Staff> UpdateStaffAsync(Guid id, Staff staff)
    {
        var updated = await _repository.UpdateAsync(id, staff);
        if (updated == null)
            throw new KeyNotFoundException($"Staff with ID {id} not found");

        return updated;
    }

    public async Task DeleteStaffAsync(Guid id)
    {
        var deleted = await _repository.DeleteAsync(id);
        if (!deleted)
            throw new KeyNotFoundException($"Staff with ID {id} not found");
    }
}
