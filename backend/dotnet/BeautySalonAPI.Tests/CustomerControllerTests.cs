using System.Collections.Generic;
using System.Net;
using System.Net.Http;
using System.Net.Http.Json;
using System.Threading.Tasks;
using BeautySalonAPI.Models;
using Microsoft.AspNetCore.Mvc.Testing;
using Xunit;

namespace BeautySalonAPI.Tests;

public class CustomerControllerTests : IClassFixture<WebApplicationFactory<Program>>
{
    private readonly WebApplicationFactory<Program> _factory;
    private readonly HttpClient _client;

    public CustomerControllerTests(WebApplicationFactory<Program> factory)
    {
        _factory = factory;
        _client = factory.CreateClient();
    }

    [Fact]
    public async Task GetAllCustomers_ReturnsEmptyList_WhenNoCustomersExist()
    {
        // Act
        var response = await _client.GetAsync("/api/customer");

        // Assert
        response.EnsureSuccessStatusCode();
        var customers = await response.Content.ReadFromJsonAsync<IEnumerable<Customer>>();
        Assert.NotNull(customers);
        Assert.Empty(customers);
    }

    [Fact]
    public async Task CreateCustomer_ReturnsCreatedCustomer()
    {
        // Arrange
        var customer = new Customer
        {
            Name = "John Doe",
            Email = "john@example.com",
            Phone = "+1234567890",
            Address = "123 Main St"
        };

        // Act
        var response = await _client.PostAsJsonAsync("/api/customer", customer);

        // Assert
        response.EnsureSuccessStatusCode();
        Assert.Equal(HttpStatusCode.Created, response.StatusCode);

        var createdCustomer = await response.Content.ReadFromJsonAsync<Customer>();
        Assert.NotNull(createdCustomer);
        Assert.Equal("John Doe", createdCustomer.Name);
        Assert.Equal("john@example.com", createdCustomer.Email);
    }
}
