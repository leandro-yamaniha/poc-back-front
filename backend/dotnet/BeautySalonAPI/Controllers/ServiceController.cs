using Microsoft.AspNetCore.Mvc;
using BeautySalonAPI.Models;
using BeautySalonAPI.Services;

namespace BeautySalonAPI.Controllers;

[ApiController]
[Route("api/[controller]")]
public class ServiceController : ControllerBase
{
    private readonly IServiceService _serviceService;

    public ServiceController(IServiceService serviceService)
    {
        _serviceService = serviceService;
    }

    [HttpGet]
    public async Task<IActionResult> GetAllServices()
    {
        var services = await _serviceService.GetAllServicesAsync();
        return Ok(services);
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> GetServiceById(Guid id)
    {
        var service = await _serviceService.GetServiceByIdAsync(id);
        if (service == null)
            return NotFound($"Service with ID {id} not found");

        return Ok(service);
    }

    [HttpGet("active")]
    public async Task<IActionResult> GetActiveServices()
    {
        var services = await _serviceService.GetActiveServicesAsync();
        return Ok(services);
    }

    [HttpGet("category/{category}")]
    public async Task<IActionResult> GetServicesByCategory(string category)
    {
        var services = await _serviceService.GetServicesByCategoryAsync(category);
        return Ok(services);
    }

    [HttpPost]
    public async Task<IActionResult> CreateService([FromBody] Service service)
    {
        if (!ModelState.IsValid)
            return BadRequest(ModelState);

        try
        {
            var createdService = await _serviceService.CreateServiceAsync(service);
            return CreatedAtAction(nameof(GetServiceById), new { id = createdService.Id }, createdService);
        }
        catch (Exception ex)
        {
            return BadRequest(ex.Message);
        }
    }

    [HttpPut("{id}")]
    public async Task<IActionResult> UpdateService(Guid id, [FromBody] Service service)
    {
        if (!ModelState.IsValid)
            return BadRequest(ModelState);

        try
        {
            var updatedService = await _serviceService.UpdateServiceAsync(id, service);
            return Ok(updatedService);
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
    public async Task<IActionResult> DeleteService(Guid id)
    {
        try
        {
            await _serviceService.DeleteServiceAsync(id);
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
