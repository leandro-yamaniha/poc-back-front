using Cassandra;
using BeautySalonAPI.Data;
using BeautySalonAPI.Models;
using CassandraSession = Cassandra.ISession;

namespace BeautySalonAPI.Repositories;

public class AppointmentRepository : IAppointmentRepository
{
    private readonly CassandraSession _session;

    public AppointmentRepository(CassandraContext context)
    {
        _session = context.Session;
    }

    public async Task<IEnumerable<Appointment>> GetAllAsync()
    {
        var statement = new SimpleStatement("SELECT * FROM appointments");
        var result = await _session.ExecuteAsync(statement);
        
        return result.Select(row => new Appointment
        {
            Id = row.GetValue<Guid>("id"),
            CustomerId = row.GetValue<Guid>("customer_id"),
            ServiceId = row.GetValue<Guid>("service_id"),
            StaffId = row.GetValue<Guid>("staff_id"),
            AppointmentDate = row.GetValue<DateTimeOffset>("appointment_date").DateTime,
            Status = Enum.Parse<AppointmentStatus>(row.GetValue<string>("status")),
            Notes = row.GetValue<string>("notes"),
            CreatedAt = row.GetValue<DateTimeOffset>("created_at").DateTime
        });
    }

    public async Task<Appointment?> GetByIdAsync(Guid id)
    {
        var statement = new SimpleStatement("SELECT * FROM appointments WHERE id = ?", id);
        var result = await _session.ExecuteAsync(statement);
        var row = result.FirstOrDefault();

        if (row == null) return null;

        return new Appointment
        {
            Id = row.GetValue<Guid>("id"),
            CustomerId = row.GetValue<Guid>("customer_id"),
            ServiceId = row.GetValue<Guid>("service_id"),
            StaffId = row.GetValue<Guid>("staff_id"),
            AppointmentDate = row.GetValue<DateTimeOffset>("appointment_date").DateTime,
            Status = Enum.Parse<AppointmentStatus>(row.GetValue<string>("status")),
            Notes = row.GetValue<string>("notes"),
            CreatedAt = row.GetValue<DateTimeOffset>("created_at").DateTime
        };
    }

    public async Task<Appointment> CreateAsync(Appointment appointment)
    {
        appointment.Id = Guid.NewGuid();
        appointment.CreatedAt = DateTime.UtcNow;

        var statement = new SimpleStatement(
            "INSERT INTO appointments (id, customer_id, service_id, staff_id, appointment_date, status, notes, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
            appointment.Id, appointment.CustomerId, appointment.ServiceId, appointment.StaffId, 
            appointment.AppointmentDate, appointment.Status.ToString(), appointment.Notes, appointment.CreatedAt);

        await _session.ExecuteAsync(statement);
        return appointment;
    }

    public async Task<Appointment?> UpdateAsync(Guid id, Appointment appointment)
    {
        var existing = await GetByIdAsync(id);
        if (existing == null) return null;

        var statement = new SimpleStatement(
            "UPDATE appointments SET appointment_date = ?, status = ?, notes = ? WHERE id = ?",
            appointment.AppointmentDate, appointment.Status.ToString(), appointment.Notes, id);

        await _session.ExecuteAsync(statement);
        
        appointment.Id = id;
        appointment.CustomerId = existing.CustomerId;
        appointment.ServiceId = existing.ServiceId;
        appointment.StaffId = existing.StaffId;
        appointment.CreatedAt = existing.CreatedAt;
        return appointment;
    }

    public async Task<bool> DeleteAsync(Guid id)
    {
        var existing = await GetByIdAsync(id);
        if (existing == null) return false;

        var statement = new SimpleStatement("DELETE FROM appointments WHERE id = ?", id);
        await _session.ExecuteAsync(statement);
        return true;
    }
}
