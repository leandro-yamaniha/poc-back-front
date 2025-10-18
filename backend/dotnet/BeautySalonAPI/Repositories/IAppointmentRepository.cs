using BeautySalonAPI.Models;

namespace BeautySalonAPI.Repositories;

public interface IAppointmentRepository
{
    Task<IEnumerable<Appointment>> GetAllAsync();
    Task<Appointment?> GetByIdAsync(Guid id);
    Task<Appointment> CreateAsync(Appointment appointment);
    Task<Appointment?> UpdateAsync(Guid id, Appointment appointment);
    Task<bool> DeleteAsync(Guid id);
}
