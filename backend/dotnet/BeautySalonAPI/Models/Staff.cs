using System.ComponentModel.DataAnnotations;

namespace BeautySalonAPI.Models;

public class Staff
{
    [Key]
    public Guid Id { get; set; }

    [Required(ErrorMessage = "Name is required")]
    [StringLength(100, ErrorMessage = "Name cannot exceed 100 characters")]
    public string Name { get; set; } = string.Empty;

    [Required(ErrorMessage = "Email is required")]
    [EmailAddress(ErrorMessage = "Invalid email format")]
    public string Email { get; set; } = string.Empty;

    [Phone(ErrorMessage = "Invalid phone number")]
    [StringLength(20, ErrorMessage = "Phone number cannot exceed 20 characters")]
    public string Phone { get; set; } = string.Empty;

    [Required(ErrorMessage = "Role is required")]
    [StringLength(50, ErrorMessage = "Role cannot exceed 50 characters")]
    public string Role { get; set; } = string.Empty;

    public List<string>? Specialties { get; set; }

    public bool Active { get; set; }

    public DateTime CreatedAt { get; set; }

    public DateTime UpdatedAt { get; set; }

    public Staff()
    {
        Id = Guid.NewGuid();
        Active = true;
        CreatedAt = DateTime.UtcNow;
        UpdatedAt = DateTime.UtcNow;
    }

    public static Staff Create(string name, string email, string phone, string role, List<string>? specialties = null)
    {
        return new Staff
        {
            Name = name,
            Email = email,
            Phone = phone,
            Role = role,
            Specialties = specialties
        };
    }

    public Staff Update(string? name = null, string? email = null, string? phone = null,
                       string? role = null, List<string>? specialties = null, bool? active = null)
    {
        return new Staff
        {
            Id = this.Id,
            Name = name ?? this.Name,
            Email = email ?? this.Email,
            Phone = phone ?? this.Phone,
            Role = role ?? this.Role,
            Specialties = specialties ?? this.Specialties,
            Active = active ?? this.Active,
            CreatedAt = this.CreatedAt,
            UpdatedAt = DateTime.UtcNow
        };
    }
}
