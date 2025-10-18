using BeautySalonAPI.Data;
using BeautySalonAPI.Models;
using Microsoft.EntityFrameworkCore;

namespace BeautySalonAPI.Services;

public interface IAppointmentService
{
    Task<IEnumerable<Appointment>> GetAllAppointmentsAsync();
    Task<Appointment?> GetAppointmentByIdAsync(Guid id);
    Task<IEnumerable<Appointment>> GetAppointmentsByCustomerAsync(Guid customerId);
    Task<IEnumerable<Appointment>> GetAppointmentsByStaffAsync(Guid staffId);
    Task<IEnumerable<Appointment>> GetAppointmentsByDateAsync(DateTime date);
    Task<Appointment> CreateAppointmentAsync(Appointment appointment);
    Task<Appointment> UpdateAppointmentAsync(Guid id, Appointment appointment);
    Task DeleteAppointmentAsync(Guid id);
}

public class AppointmentService : IAppointmentService
{
    private readonly BeautySalonDbContext _context;

    public AppointmentService(BeautySalonDbContext context)
    {
        _context = context;
    }

    public async Task<IEnumerable<Appointment>> GetAllAppointmentsAsync()
    {
        return await _context.Appointments.ToListAsync();
    }

    public async Task<Appointment?> GetAppointmentByIdAsync(Guid id)
    {
        return await _context.Appointments.FindAsync(id);
    }

    public async Task<IEnumerable<Appointment>> GetAppointmentsByCustomerAsync(Guid customerId)
    {
        return await _context.Appointments
            .Where(a => a.CustomerId == customerId)
            .ToListAsync();
    }

    public async Task<IEnumerable<Appointment>> GetAppointmentsByStaffAsync(Guid staffId)
    {
        return await _context.Appointments
            .Where(a => a.StaffId == staffId)
            .ToListAsync();
    }

    public async Task<IEnumerable<Appointment>> GetAppointmentsByDateAsync(DateTime date)
    {
        var startOfDay = date.Date;
        var endOfDay = startOfDay.AddDays(1);

        return await _context.Appointments
            .Where(a => a.AppointmentDate >= startOfDay && a.AppointmentDate < endOfDay)
            .ToListAsync();
    }

    public async Task<Appointment> CreateAppointmentAsync(Appointment appointment)
    {
        // Validate that customer, service, and staff exist
        if (!await _context.Customers.AnyAsync(c => c.Id == appointment.CustomerId))
            throw new ArgumentException($"Customer with ID {appointment.CustomerId} not found");

        if (!await _context.Services.AnyAsync(s => s.Id == appointment.ServiceId))
            throw new ArgumentException($"Service with ID {appointment.ServiceId} not found");

        if (!await _context.Staff.AnyAsync(s => s.Id == appointment.StaffId))
            throw new ArgumentException($"Staff with ID {appointment.StaffId} not found");

        _context.Appointments.Add(appointment);
        await _context.SaveChangesAsync();
        return appointment;
    }

    public async Task<Appointment> UpdateAppointmentAsync(Guid id, Appointment appointment)
    {
        var existingAppointment = await _context.Appointments.FindAsync(id);
        if (existingAppointment == null)
            throw new KeyNotFoundException($"Appointment with ID {id} not found");

        existingAppointment.CustomerId = appointment.CustomerId;
        existingAppointment.ServiceId = appointment.ServiceId;
        existingAppointment.StaffId = appointment.StaffId;
        existingAppointment.AppointmentDate = appointment.AppointmentDate;
        existingAppointment.Status = appointment.Status;
        existingAppointment.Notes = appointment.Notes;
        existingAppointment.UpdatedAt = DateTime.UtcNow;

        await _context.SaveChangesAsync();
        return existingAppointment;
    }

    public async Task DeleteAppointmentAsync(Guid id)
    {
        var appointment = await _context.Appointments.FindAsync(id);
        if (appointment == null)
            throw new KeyNotFoundException($"Appointment with ID {id} not found");

        _context.Appointments.Remove(appointment);
        await _context.SaveChangesAsync();
    }
}
