using BeautySalonAPI.Data;
using BeautySalonAPI.Models;
using Microsoft.EntityFrameworkCore;

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
    private readonly BeautySalonDbContext _context;

    public StaffService(BeautySalonDbContext context)
    {
        _context = context;
    }

    public async Task<IEnumerable<Staff>> GetAllStaffAsync()
    {
        return await _context.Staff.ToListAsync();
    }

    public async Task<Staff?> GetStaffByIdAsync(Guid id)
    {
        return await _context.Staff.FindAsync(id);
    }

    public async Task<IEnumerable<Staff>> GetActiveStaffAsync()
    {
        return await _context.Staff
            .Where(s => s.Active)
            .ToListAsync();
    }

    public async Task<IEnumerable<Staff>> GetStaffByRoleAsync(string role)
    {
        return await _context.Staff
            .Where(s => s.Role == role && s.Active)
            .ToListAsync();
    }

    public async Task<Staff> CreateStaffAsync(Staff staff)
    {
        _context.Staff.Add(staff);
        await _context.SaveChangesAsync();
        return staff;
    }

    public async Task<Staff> UpdateStaffAsync(Guid id, Staff staff)
    {
        var existingStaff = await _context.Staff.FindAsync(id);
        if (existingStaff == null)
            throw new KeyNotFoundException($"Staff with ID {id} not found");

        existingStaff.Name = staff.Name;
        existingStaff.Email = staff.Email;
        existingStaff.Phone = staff.Phone;
        existingStaff.Role = staff.Role;
        existingStaff.Specialties = staff.Specialties;
        existingStaff.Active = staff.Active;
        existingStaff.UpdatedAt = DateTime.UtcNow;

        await _context.SaveChangesAsync();
        return existingStaff;
    }

    public async Task DeleteStaffAsync(Guid id)
    {
        var staff = await _context.Staff.FindAsync(id);
        if (staff == null)
            throw new KeyNotFoundException($"Staff with ID {id} not found");

        _context.Staff.Remove(staff);
        await _context.SaveChangesAsync();
    }
}
