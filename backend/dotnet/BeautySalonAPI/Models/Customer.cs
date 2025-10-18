using System.ComponentModel.DataAnnotations;

namespace BeautySalonAPI.Models;

public class Customer
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

    [StringLength(200, ErrorMessage = "Address cannot exceed 200 characters")]
    public string? Address { get; set; }

    public DateTime CreatedAt { get; set; }

    public DateTime UpdatedAt { get; set; }

    public Customer()
    {
        Id = Guid.NewGuid();
        CreatedAt = DateTime.UtcNow;
        UpdatedAt = DateTime.UtcNow;
    }

    public static Customer Create(string name, string email, string phone, string? address = null)
    {
        return new Customer
        {
            Name = name,
            Email = email,
            Phone = phone,
            Address = address
        };
    }

    public Customer Update(string? name = null, string? email = null, string? phone = null, string? address = null)
    {
        return new Customer
        {
            Id = this.Id,
            Name = name ?? this.Name,
            Email = email ?? this.Email,
            Phone = phone ?? this.Phone,
            Address = address ?? this.Address,
            CreatedAt = this.CreatedAt,
            UpdatedAt = DateTime.UtcNow
        };
    }
}
