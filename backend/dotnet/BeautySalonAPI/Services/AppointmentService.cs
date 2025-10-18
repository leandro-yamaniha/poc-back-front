using BeautySalonAPI.Models;
using BeautySalonAPI.Repositories;

namespace BeautySalonAPI.Services;

public interface IAppointmentService
{
    Task<IEnumerable<Appointment>> GetAllAppointmentsAsync();
    Task<Appointment?> GetAppointmentByIdAsync(Guid id);
    Task<IEnumerable<Appointment>> GetAppointmentsByDateAsync(DateTime date);
    Task<IEnumerable<Appointment>> GetAppointmentsByStaffAsync(Guid staffId);
    Task<IEnumerable<Appointment>> GetAppointmentsByCustomerAsync(Guid customerId);
    Task<Appointment> CreateAppointmentAsync(Appointment appointment);
    Task<Appointment> UpdateAppointmentAsync(Guid id, Appointment appointment);
    Task DeleteAppointmentAsync(Guid id);
}

public class AppointmentService : IAppointmentService
{
    private readonly IAppointmentRepository _repository;

    public AppointmentService(IAppointmentRepository repository)
    {
        _repository = repository;
    }

    public async Task<IEnumerable<Appointment>> GetAllAppointmentsAsync()
    {
        return await _repository.GetAllAsync();
    }

    public async Task<Appointment?> GetAppointmentByIdAsync(Guid id)
    {
        return await _repository.GetByIdAsync(id);
    }

    public async Task<IEnumerable<Appointment>> GetAppointmentsByDateAsync(DateTime date)
    {
        var allAppointments = await _repository.GetAllAsync();
        return allAppointments.Where(a => a.AppointmentDate.Date == date.Date);
    }

    public async Task<IEnumerable<Appointment>> GetAppointmentsByStaffAsync(Guid staffId)
    {
        var allAppointments = await _repository.GetAllAsync();
        return allAppointments.Where(a => a.StaffId == staffId);
    }

    public async Task<IEnumerable<Appointment>> GetAppointmentsByCustomerAsync(Guid customerId)
    {
        var allAppointments = await _repository.GetAllAsync();
        return allAppointments.Where(a => a.CustomerId == customerId);
    }

    public async Task<Appointment> CreateAppointmentAsync(Appointment appointment)
    {
        return await _repository.CreateAsync(appointment);
    }

    public async Task<Appointment> UpdateAppointmentAsync(Guid id, Appointment appointment)
    {
        var updated = await _repository.UpdateAsync(id, appointment);
        if (updated == null)
            throw new KeyNotFoundException($"Appointment with ID {id} not found");

        return updated;
    }

    public async Task DeleteAppointmentAsync(Guid id)
    {
        var deleted = await _repository.DeleteAsync(id);
        if (!deleted)
            throw new KeyNotFoundException($"Appointment with ID {id} not found");
    }
}
