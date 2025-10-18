using Microsoft.AspNetCore.Mvc;
using BeautySalonAPI.Models;
using BeautySalonAPI.Services;

namespace BeautySalonAPI.Controllers;

[ApiController]
[Route("api/[controller]")]
public class AppointmentController : ControllerBase
{
    private readonly IAppointmentService _appointmentService;

    public AppointmentController(IAppointmentService appointmentService)
    {
        _appointmentService = appointmentService;
    }

    [HttpGet]
    public async Task<IActionResult> GetAllAppointments()
    {
        var appointments = await _appointmentService.GetAllAppointmentsAsync();
        return Ok(appointments);
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> GetAppointmentById(Guid id)
    {
        var appointment = await _appointmentService.GetAppointmentByIdAsync(id);
        if (appointment == null)
            return NotFound($"Appointment with ID {id} not found");

        return Ok(appointment);
    }

    [HttpGet("customer/{customerId}")]
    public async Task<IActionResult> GetAppointmentsByCustomer(Guid customerId)
    {
        var appointments = await _appointmentService.GetAppointmentsByCustomerAsync(customerId);
        return Ok(appointments);
    }

    [HttpGet("staff/{staffId}")]
    public async Task<IActionResult> GetAppointmentsByStaff(Guid staffId)
    {
        var appointments = await _appointmentService.GetAppointmentsByStaffAsync(staffId);
        return Ok(appointments);
    }

    [HttpGet("date/{date}")]
    public async Task<IActionResult> GetAppointmentsByDate(DateTime date)
    {
        var appointments = await _appointmentService.GetAppointmentsByDateAsync(date);
        return Ok(appointments);
    }

    [HttpPost]
    public async Task<IActionResult> CreateAppointment([FromBody] Appointment appointment)
    {
        if (!ModelState.IsValid)
            return BadRequest(ModelState);

        try
        {
            var createdAppointment = await _appointmentService.CreateAppointmentAsync(appointment);
            return CreatedAtAction(nameof(GetAppointmentById), new { id = createdAppointment.Id }, createdAppointment);
        }
        catch (ArgumentException ex)
        {
            return BadRequest(ex.Message);
        }
        catch (Exception ex)
        {
            return BadRequest(ex.Message);
        }
    }

    [HttpPut("{id}")]
    public async Task<IActionResult> UpdateAppointment(Guid id, [FromBody] Appointment appointment)
    {
        if (!ModelState.IsValid)
            return BadRequest(ModelState);

        try
        {
            var updatedAppointment = await _appointmentService.UpdateAppointmentAsync(id, appointment);
            return Ok(updatedAppointment);
        }
        catch (KeyNotFoundException ex)
        {
            return NotFound(ex.Message);
        }
        catch (Exception ex)
        {
            return BadRequest(ex.Message);
        }
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteAppointment(Guid id)
    {
        try
        {
            await _appointmentService.DeleteAppointmentAsync(id);
            return NoContent();
        }
        catch (KeyNotFoundException ex)
        {
            return NotFound(ex.Message);
        }
        catch (Exception ex)
        {
            return BadRequest(ex.Message);
        }
    }
}
