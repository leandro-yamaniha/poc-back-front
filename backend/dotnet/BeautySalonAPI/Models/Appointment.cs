using System.ComponentModel.DataAnnotations;

namespace BeautySalonAPI.Models;

public class Appointment
{
    [Key]
    public Guid Id { get; set; }

    [Required(ErrorMessage = "Customer ID is required")]
    public Guid CustomerId { get; set; }

    [Required(ErrorMessage = "Service ID is required")]
    public Guid ServiceId { get; set; }

    [Required(ErrorMessage = "Staff ID is required")]
    public Guid StaffId { get; set; }

    [Required(ErrorMessage = "Appointment date is required")]
    public DateTime AppointmentDate { get; set; }

    public AppointmentStatus Status { get; set; }

    [StringLength(500, ErrorMessage = "Notes cannot exceed 500 characters")]
    public string? Notes { get; set; }

    public DateTime CreatedAt { get; set; }

    public DateTime UpdatedAt { get; set; }

    public Appointment()
    {
        Id = Guid.NewGuid();
        Status = AppointmentStatus.Scheduled;
        CreatedAt = DateTime.UtcNow;
        UpdatedAt = DateTime.UtcNow;
    }

    public static Appointment Create(Guid customerId, Guid serviceId, Guid staffId,
                                   DateTime appointmentDate, string? notes = null)
    {
        return new Appointment
        {
            CustomerId = customerId,
            ServiceId = serviceId,
            StaffId = staffId,
            AppointmentDate = appointmentDate,
            Notes = notes
        };
    }

    public Appointment Update(DateTime? appointmentDate = null, AppointmentStatus? status = null, string? notes = null)
    {
        return new Appointment
        {
            Id = this.Id,
            CustomerId = this.CustomerId,
            ServiceId = this.ServiceId,
            StaffId = this.StaffId,
            AppointmentDate = appointmentDate ?? this.AppointmentDate,
            Status = status ?? this.Status,
            Notes = notes ?? this.Notes,
            CreatedAt = this.CreatedAt,
            UpdatedAt = DateTime.UtcNow
        };
    }
}

public enum AppointmentStatus
{
    Scheduled,
    Confirmed,
    InProgress,
    Completed,
    Cancelled,
    NoShow
}
