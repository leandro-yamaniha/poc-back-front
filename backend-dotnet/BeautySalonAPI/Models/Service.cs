using System.ComponentModel.DataAnnotations;

namespace BeautySalonAPI.Models;

public class Service
{
    [Key]
    public Guid Id { get; set; }

    [Required(ErrorMessage = "Name is required")]
    [StringLength(100, ErrorMessage = "Name cannot exceed 100 characters")]
    public string Name { get; set; } = string.Empty;

    [StringLength(500, ErrorMessage = "Description cannot exceed 500 characters")]
    public string? Description { get; set; }

    [Required(ErrorMessage = "Price is required")]
    [Range(0.01, double.MaxValue, ErrorMessage = "Price must be greater than 0")]
    public decimal Price { get; set; }

    [Required(ErrorMessage = "Duration is required")]
    [Range(1, int.MaxValue, ErrorMessage = "Duration must be at least 1 minute")]
    public int DurationMinutes { get; set; }

    [Required(ErrorMessage = "Category is required")]
    [StringLength(50, ErrorMessage = "Category cannot exceed 50 characters")]
    public string Category { get; set; } = string.Empty;

    public bool Active { get; set; }

    public DateTime CreatedAt { get; set; }

    public DateTime UpdatedAt { get; set; }

    public Service()
    {
        Id = Guid.NewGuid();
        Active = true;
        CreatedAt = DateTime.UtcNow;
        UpdatedAt = DateTime.UtcNow;
    }

    public static Service Create(string name, string? description, decimal price, int durationMinutes, string category)
    {
        return new Service
        {
            Name = name,
            Description = description,
            Price = price,
            DurationMinutes = durationMinutes,
            Category = category
        };
    }

    public Service Update(string? name = null, string? description = null, decimal? price = null,
                         int? durationMinutes = null, string? category = null, bool? active = null)
    {
        return new Service
        {
            Id = this.Id,
            Name = name ?? this.Name,
            Description = description ?? this.Description,
            Price = price ?? this.Price,
            DurationMinutes = durationMinutes ?? this.DurationMinutes,
            Category = category ?? this.Category,
            Active = active ?? this.Active,
            CreatedAt = this.CreatedAt,
            UpdatedAt = DateTime.UtcNow
        };
    }
}
