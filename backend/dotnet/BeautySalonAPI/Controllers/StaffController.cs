using Microsoft.AspNetCore.Mvc;
using BeautySalonAPI.Models;
using BeautySalonAPI.Services;

namespace BeautySalonAPI.Controllers;

[ApiController]
[Route("api/[controller]")]
public class StaffController : ControllerBase
{
    private readonly IStaffService _staffService;

    public StaffController(IStaffService staffService)
    {
        _staffService = staffService;
    }

    [HttpGet]
    public async Task<IActionResult> GetAllStaff()
    {
        var staff = await _staffService.GetAllStaffAsync();
        return Ok(staff);
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> GetStaffById(Guid id)
    {
        var staff = await _staffService.GetStaffByIdAsync(id);
        if (staff == null)
            return NotFound($"Staff with ID {id} not found");

        return Ok(staff);
    }

    [HttpGet("active")]
    public async Task<IActionResult> GetActiveStaff()
    {
        var staff = await _staffService.GetActiveStaffAsync();
        return Ok(staff);
    }

    [HttpGet("role/{role}")]
    public async Task<IActionResult> GetStaffByRole(string role)
    {
        var staff = await _staffService.GetStaffByRoleAsync(role);
        return Ok(staff);
    }

    [HttpPost]
    public async Task<IActionResult> CreateStaff([FromBody] Staff staff)
    {
        if (!ModelState.IsValid)
            return BadRequest(ModelState);

        try
        {
            var createdStaff = await _staffService.CreateStaffAsync(staff);
            return CreatedAtAction(nameof(GetStaffById), new { id = createdStaff.Id }, createdStaff);
        }
        catch (Exception ex)
        {
            return BadRequest(ex.Message);
        }
    }

    [HttpPut("{id}")]
    public async Task<IActionResult> UpdateStaff(Guid id, [FromBody] Staff staff)
    {
        if (!ModelState.IsValid)
            return BadRequest(ModelState);

        try
        {
            var updatedStaff = await _staffService.UpdateStaffAsync(id, staff);
            return Ok(updatedStaff);
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
    public async Task<IActionResult> DeleteStaff(Guid id)
    {
        try
        {
            await _staffService.DeleteStaffAsync(id);
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
