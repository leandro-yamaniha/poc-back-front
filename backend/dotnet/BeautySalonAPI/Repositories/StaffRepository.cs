using Cassandra;
using BeautySalonAPI.Data;
using BeautySalonAPI.Models;
using System.Text.Json;
using CassandraSession = Cassandra.ISession;

namespace BeautySalonAPI.Repositories;

public class StaffRepository : IStaffRepository
{
    private readonly CassandraSession _session;

    public StaffRepository(CassandraContext context)
    {
        _session = context.Session;
    }

    public async Task<IEnumerable<Staff>> GetAllAsync()
    {
        var statement = new SimpleStatement("SELECT * FROM staff");
        var result = await _session.ExecuteAsync(statement);
        
        return result.Select(row => new Staff
        {
            Id = row.GetValue<Guid>("id"),
            Name = row.GetValue<string>("name"),
            Email = row.GetValue<string>("email"),
            Phone = row.GetValue<string>("phone"),
            Role = row.GetValue<string>("role"),
            Specialties = DeserializeSpecialties(row.GetValue<string>("specialties")),
            Active = row.GetValue<bool>("active"),
            CreatedAt = row.GetValue<DateTimeOffset>("created_at").DateTime
        });
    }

    public async Task<Staff?> GetByIdAsync(Guid id)
    {
        var statement = new SimpleStatement("SELECT * FROM staff WHERE id = ?", id);
        var result = await _session.ExecuteAsync(statement);
        var row = result.FirstOrDefault();

        if (row == null) return null;

        return new Staff
        {
            Id = row.GetValue<Guid>("id"),
            Name = row.GetValue<string>("name"),
            Email = row.GetValue<string>("email"),
            Phone = row.GetValue<string>("phone"),
            Role = row.GetValue<string>("role"),
            Specialties = DeserializeSpecialties(row.GetValue<string>("specialties")),
            Active = row.GetValue<bool>("active"),
            CreatedAt = row.GetValue<DateTimeOffset>("created_at").DateTime
        };
    }

    public async Task<Staff> CreateAsync(Staff staff)
    {
        staff.Id = Guid.NewGuid();
        staff.CreatedAt = DateTime.UtcNow;

        var specialtiesJson = SerializeSpecialties(staff.Specialties);
        var statement = new SimpleStatement(
            "INSERT INTO staff (id, name, email, phone, role, specialties, active, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
            staff.Id, staff.Name, staff.Email, staff.Phone, staff.Role, specialtiesJson, staff.Active, staff.CreatedAt);

        await _session.ExecuteAsync(statement);
        return staff;
    }

    public async Task<Staff?> UpdateAsync(Guid id, Staff staff)
    {
        var existing = await GetByIdAsync(id);
        if (existing == null) return null;

        var specialtiesJson = SerializeSpecialties(staff.Specialties);
        var statement = new SimpleStatement(
            "UPDATE staff SET name = ?, email = ?, phone = ?, role = ?, specialties = ?, active = ? WHERE id = ?",
            staff.Name, staff.Email, staff.Phone, staff.Role, specialtiesJson, staff.Active, id);

        await _session.ExecuteAsync(statement);
        
        staff.Id = id;
        staff.CreatedAt = existing.CreatedAt;
        return staff;
    }

    public async Task<bool> DeleteAsync(Guid id)
    {
        var existing = await GetByIdAsync(id);
        if (existing == null) return false;

        var statement = new SimpleStatement("DELETE FROM staff WHERE id = ?", id);
        await _session.ExecuteAsync(statement);
        return true;
    }

    private static string SerializeSpecialties(List<string>? specialties)
    {
        return specialties == null || !specialties.Any() 
            ? "[]" 
            : JsonSerializer.Serialize(specialties);
    }

    private static List<string>? DeserializeSpecialties(string? specialtiesJson)
    {
        if (string.IsNullOrEmpty(specialtiesJson) || specialtiesJson == "[]")
            return new List<string>();
        
        return JsonSerializer.Deserialize<List<string>>(specialtiesJson);
    }
}
