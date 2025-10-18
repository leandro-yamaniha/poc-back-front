# 🔄 Guia de Migração: .NET Core InMemory → Cassandra

## 📋 Situação Atual

### ❌ Problema Identificado
O backend .NET Core está usando **InMemoryDatabase**, enquanto todos os outros backends usam **Cassandra**.

**Arquivo:** `BeautySalonAPI.csproj`
```xml
<PackageReference Include="Microsoft.EntityFrameworkCore.InMemory" Version="8.0.8" />
<PackageReference Include="Microsoft.EntityFrameworkCore.SqlServer" Version="8.0.8" />
```

**Arquivo:** `Program.cs`
```csharp
builder.Services.AddDbContext<BeautySalonDbContext>(options =>
    options.UseInMemoryDatabase("BeautySalonDb"));
```

---

## ⚠️ Impacto no Benchmark

### Vantagem Injusta do .NET
- ⚡ **Sem latência de rede** (tudo em RAM)
- 🚀 **Sem I/O de disco** (zero latência)
- 💾 **Sem serialização** de rede
- 📊 **Performance artificialmente alta**

### Comparação Injusta
```
.NET InMemory:     10,000+ RPS, <1ms latency  ⚠️ Injusto
Java Cassandra:     6,000 RPS, 5-10ms latency  ✅ Real
Python Cassandra:   3,000 RPS, 10-20ms latency ✅ Real
Node.js Cassandra:  5,000 RPS, 8-15ms latency  ✅ Real
```

---

## ✅ Solução: Migrar para Cassandra

### 1. Adicionar Driver Cassandra

**Arquivo:** `BeautySalonAPI.csproj`

```xml
<ItemGroup>
  <!-- Remover ou comentar -->
  <!-- <PackageReference Include="Microsoft.EntityFrameworkCore.InMemory" Version="8.0.8" /> -->
  <!-- <PackageReference Include="Microsoft.EntityFrameworkCore.SqlServer" Version="8.0.8" /> -->
  
  <!-- Adicionar Cassandra -->
  <PackageReference Include="CassandraCSharpDriver" Version="3.20.1" />
  <PackageReference Include="Microsoft.AspNetCore.Mvc.NewtonsoftJson" Version="8.0.8" />
  <PackageReference Include="Swashbuckle.AspNetCore" Version="6.7.3" />
</ItemGroup>
```

---

### 2. Criar Configuração Cassandra

**Novo arquivo:** `Data/CassandraContext.cs`

```csharp
using Cassandra;

namespace BeautySalonAPI.Data;

public class CassandraContext
{
    private readonly ISession _session;
    private readonly ICluster _cluster;

    public CassandraContext(IConfiguration configuration)
    {
        var contactPoints = configuration["Cassandra:ContactPoints"] ?? "localhost";
        var port = int.Parse(configuration["Cassandra:Port"] ?? "9042");
        var keyspace = configuration["Cassandra:Keyspace"] ?? "beauty_salon";

        _cluster = Cluster.Builder()
            .AddContactPoints(contactPoints.Split(','))
            .WithPort(port)
            .WithDefaultKeyspace(keyspace)
            .Build();

        _session = _cluster.Connect();
    }

    public ISession Session => _session;

    public void Dispose()
    {
        _session?.Dispose();
        _cluster?.Dispose();
    }
}
```

---

### 3. Criar Repositórios Cassandra

**Exemplo:** `Repositories/CustomerRepository.cs`

```csharp
using Cassandra;
using BeautySalonAPI.Models;

namespace BeautySalonAPI.Repositories;

public interface ICustomerRepository
{
    Task<IEnumerable<Customer>> GetAllAsync();
    Task<Customer?> GetByIdAsync(Guid id);
    Task<Customer> CreateAsync(Customer customer);
    Task<Customer?> UpdateAsync(Guid id, Customer customer);
    Task<bool> DeleteAsync(Guid id);
}

public class CustomerRepository : ICustomerRepository
{
    private readonly ISession _session;

    public CustomerRepository(CassandraContext context)
    {
        _session = context.Session;
    }

    public async Task<IEnumerable<Customer>> GetAllAsync()
    {
        var statement = new SimpleStatement("SELECT * FROM customers");
        var result = await _session.ExecuteAsync(statement);
        
        return result.Select(row => new Customer
        {
            Id = row.GetValue<Guid>("id"),
            Name = row.GetValue<string>("name"),
            Email = row.GetValue<string>("email"),
            Phone = row.GetValue<string>("phone"),
            CreatedAt = row.GetValue<DateTimeOffset>("created_at")
        });
    }

    public async Task<Customer?> GetByIdAsync(Guid id)
    {
        var statement = new SimpleStatement(
            "SELECT * FROM customers WHERE id = ?", id);
        var result = await _session.ExecuteAsync(statement);
        var row = result.FirstOrDefault();

        if (row == null) return null;

        return new Customer
        {
            Id = row.GetValue<Guid>("id"),
            Name = row.GetValue<string>("name"),
            Email = row.GetValue<string>("email"),
            Phone = row.GetValue<string>("phone"),
            CreatedAt = row.GetValue<DateTimeOffset>("created_at")
        };
    }

    public async Task<Customer> CreateAsync(Customer customer)
    {
        customer.Id = Guid.NewGuid();
        customer.CreatedAt = DateTimeOffset.UtcNow;

        var statement = new SimpleStatement(
            "INSERT INTO customers (id, name, email, phone, created_at) VALUES (?, ?, ?, ?, ?)",
            customer.Id, customer.Name, customer.Email, customer.Phone, customer.CreatedAt);

        await _session.ExecuteAsync(statement);
        return customer;
    }

    public async Task<Customer?> UpdateAsync(Guid id, Customer customer)
    {
        var existing = await GetByIdAsync(id);
        if (existing == null) return null;

        var statement = new SimpleStatement(
            "UPDATE customers SET name = ?, email = ?, phone = ? WHERE id = ?",
            customer.Name, customer.Email, customer.Phone, id);

        await _session.ExecuteAsync(statement);
        
        customer.Id = id;
        customer.CreatedAt = existing.CreatedAt;
        return customer;
    }

    public async Task<bool> DeleteAsync(Guid id)
    {
        var existing = await GetByIdAsync(id);
        if (existing == null) return false;

        var statement = new SimpleStatement("DELETE FROM customers WHERE id = ?", id);
        await _session.ExecuteAsync(statement);
        return true;
    }
}
```

---

### 4. Atualizar Program.cs

**Arquivo:** `Program.cs`

```csharp
var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddControllers()
    .AddNewtonsoftJson();

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// ❌ REMOVER DbContext
// builder.Services.AddDbContext<BeautySalonDbContext>(options =>
//     options.UseInMemoryDatabase("BeautySalonDb"));

// ✅ ADICIONAR Cassandra
builder.Services.AddSingleton<CassandraContext>();
builder.Services.AddScoped<ICustomerRepository, CustomerRepository>();
builder.Services.AddScoped<IServiceRepository, ServiceRepository>();
builder.Services.AddScoped<IStaffRepository, StaffRepository>();
builder.Services.AddScoped<IAppointmentRepository, AppointmentRepository>();

// Register services
builder.Services.AddScoped<ICustomerService, CustomerService>();
builder.Services.AddScoped<IServiceService, ServiceService>();
builder.Services.AddScoped<IStaffService, StaffService>();
builder.Services.AddScoped<IAppointmentService, AppointmentService>();

// Add CORS
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAll",
        builder => builder
            .AllowAnyOrigin()
            .AllowAnyMethod()
            .AllowAnyHeader());
});

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseCors("AllowAll");
app.UseAuthorization();
app.MapControllers();

app.Run();
```

---

### 5. Atualizar appsettings.json

**Arquivo:** `appsettings.json`

```json
{
  "Logging": {
    "LogLevel": {
      "Default": "Information",
      "Microsoft.AspNetCore": "Warning"
    }
  },
  "AllowedHosts": "*",
  "Cassandra": {
    "ContactPoints": "localhost",
    "Port": 9042,
    "Keyspace": "beauty_salon"
  }
}
```

**Arquivo:** `appsettings.Docker.json` (novo)

```json
{
  "Cassandra": {
    "ContactPoints": "cassandra",
    "Port": 9042,
    "Keyspace": "beauty_salon"
  }
}
```

---

### 6. Atualizar Services

**Exemplo:** `Services/CustomerService.cs`

```csharp
public class CustomerService : ICustomerService
{
    private readonly ICustomerRepository _repository;

    public CustomerService(ICustomerRepository repository)
    {
        _repository = repository;
    }

    public async Task<IEnumerable<Customer>> GetAllCustomersAsync()
    {
        return await _repository.GetAllAsync();
    }

    public async Task<Customer?> GetCustomerByIdAsync(Guid id)
    {
        return await _repository.GetByIdAsync(id);
    }

    public async Task<Customer> CreateCustomerAsync(Customer customer)
    {
        return await _repository.CreateAsync(customer);
    }

    public async Task<Customer?> UpdateCustomerAsync(Guid id, Customer customer)
    {
        return await _repository.UpdateAsync(id, customer);
    }

    public async Task<bool> DeleteCustomerAsync(Guid id)
    {
        return await _repository.DeleteAsync(id);
    }
}
```

---

### 7. Atualizar docker-compose.yml

```yaml
backend-dotnet:
  build:
    context: ./backend/dotnet
    dockerfile: Dockerfile
  container_name: beauty-salon-backend-dotnet
  ports:
    - "8081:8080"
  environment:
    - ASPNETCORE_ENVIRONMENT=Docker
    - ASPNETCORE_URLS=http://+:8080
    - Cassandra__ContactPoints=cassandra
    - Cassandra__Port=9042
    - Cassandra__Keyspace=beauty_salon
  depends_on:
    cassandra:
      condition: service_healthy
  networks:
    - beauty-salon-network
  healthcheck:
    test: ["CMD-SHELL", "curl -f http://localhost:8080/health || exit 1"]
    interval: 30s
    timeout: 10s
    retries: 5
```

---

## 📊 Benefícios da Migração

### Comparação Justa
- ✅ **Mesma infraestrutura** que outros backends
- ✅ **Mesma latência** de rede
- ✅ **Mesma estrutura** de dados
- ✅ **Benchmark realista**

### Performance Real
```
ANTES (InMemory):
- Startup: 5s
- RPS: 10,000+
- Latency: <1ms
- ⚠️ Não representa produção

DEPOIS (Cassandra):
- Startup: 8-12s
- RPS: 6,000-8,000
- Latency: 5-15ms
- ✅ Representa produção real
```

---

## 🔧 Passos de Implementação

### 1. Instalar Pacote
```bash
cd backend/dotnet/BeautySalonAPI
dotnet add package CassandraCSharpDriver
dotnet remove package Microsoft.EntityFrameworkCore.InMemory
```

### 2. Criar Arquivos
- `Data/CassandraContext.cs`
- `Repositories/ICustomerRepository.cs`
- `Repositories/CustomerRepository.cs`
- `Repositories/IServiceRepository.cs`
- `Repositories/ServiceRepository.cs`
- `Repositories/IStaffRepository.cs`
- `Repositories/StaffRepository.cs`
- `Repositories/IAppointmentRepository.cs`
- `Repositories/AppointmentRepository.cs`

### 3. Atualizar Arquivos
- `Program.cs`
- `appsettings.json`
- `Services/*.cs`

### 4. Remover Arquivos
- `Data/BeautySalonDbContext.cs` (não mais necessário)

### 5. Testar
```bash
# Local
dotnet run

# Docker
docker-compose up -d backend-dotnet
curl http://localhost:8081/api/customers
```

---

## 📚 Recursos

### Documentação Oficial
- **CassandraCSharpDriver**: https://docs.datastax.com/en/developer/csharp-driver/latest/
- **Cassandra CQL**: https://cassandra.apache.org/doc/latest/cql/

### Exemplos
- **Repository Pattern**: https://github.com/datastax/csharp-driver/tree/master/examples
- **Async Operations**: https://docs.datastax.com/en/developer/csharp-driver/latest/features/async/

---

## ⚠️ Considerações

### Opção 1: Migrar para Cassandra (Recomendado)
- ✅ Comparação justa no benchmark
- ✅ Representa ambiente de produção
- ✅ Consistente com outros backends
- ⏱️ Tempo: ~2-4 horas de implementação

### Opção 2: Manter InMemory
- ⚠️ Benchmark separado "InMemory vs Database"
- ⚠️ Documentar claramente a diferença
- ⚠️ Não representa produção real
- ⏱️ Tempo: 0 (sem mudanças)

---

## 🎯 Recomendação

**MIGRAR PARA CASSANDRA** para:
1. Benchmark justo e realista
2. Consistência arquitetural
3. Representar ambiente de produção
4. Comparação válida entre tecnologias

---

**Decisão:** Implementar migração para Cassandra antes do benchmark final?
