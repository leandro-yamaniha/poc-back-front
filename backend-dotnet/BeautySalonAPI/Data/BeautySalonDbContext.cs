using Microsoft.EntityFrameworkCore;
using BeautySalonAPI.Models;

namespace BeautySalonAPI.Data;

public class BeautySalonDbContext : DbContext
{
    public BeautySalonDbContext(DbContextOptions<BeautySalonDbContext> options) : base(options) { }

    public DbSet<Customer> Customers { get; set; } = null!;
    public DbSet<Service> Services { get; set; } = null!;
    public DbSet<Staff> Staff { get; set; } = null!;
    public DbSet<Appointment> Appointments { get; set; } = null!;

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        // Configure JSON serialization for List<string> properties
        modelBuilder.Entity<Staff>()
            .Property(s => s.Specialties)
            .HasConversion(
                v => string.Join(",", v ?? new List<string>()),
                v => v.Split(",", StringSplitOptions.RemoveEmptyEntries).ToList()
            );

        // Configure enum conversion for AppointmentStatus
        modelBuilder.Entity<Appointment>()
            .Property(a => a.Status)
            .HasConversion<string>();
    }
}
