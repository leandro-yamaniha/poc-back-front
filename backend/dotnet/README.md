# Beauty Salon API - .NET Core

Backend em .NET Core 8.0 para o sistema de gerenciamento de salão de beleza, utilizando Entity Framework Core com banco de dados em memória.

## 🚀 Características

- **.NET Core 8.0 LTS** - Framework moderno e performático
- **Entity Framework Core** - ORM para persistência de dados
- **Banco de dados em memória** - Para desenvolvimento e testes
- **REST API** - Endpoints RESTful para todas as operações CRUD
- **Swagger/OpenAPI** - Documentação automática da API
- **Injeção de Dependência** - Arquitetura limpa com IoC
- **Testes Unitários** - Cobertura de testes com xUnit
- **Docker** - Containerização pronta para produção

## 📋 Funcionalidades

### Entidades
- **Clientes (Customers)** - Gerenciamento de clientes do salão
- **Serviços (Services)** - Catálogo de serviços oferecidos
- **Funcionários (Staff)** - Equipe do salão
- **Agendamentos (Appointments)** - Controle de agendamentos

### Operações CRUD
- ✅ Criar, ler, atualizar e deletar para todas as entidades
- ✅ Busca por filtros (nome, categoria, data, etc.)
- ✅ Validação de dados com Data Annotations
- ✅ Tratamento de erros consistente

## 🛠 Tecnologias

- **ASP.NET Core Web API** - Framework web
- **Entity Framework Core** - Mapeamento objeto-relacional
- **xUnit** - Framework de testes
- **Swagger** - Documentação da API
- **Docker** - Containerização

## 🚀 Como executar

### Pré-requisitos
- .NET 8.0 SDK
- Docker (opcional)

### Desenvolvimento local

```bash
# Navegar para o diretório do projeto
cd backend-dotnet/BeautySalonAPI

# Restaurar dependências
dotnet restore

# Executar a aplicação
dotnet run

# A aplicação estará disponível em:
# - API: https://localhost:5001
# - Swagger UI: https://localhost:5001/swagger
```

### Com Docker

```bash
# Construir e executar com Docker Compose
docker-compose -f docker-compose-dotnet.yml up --build

# A aplicação estará disponível em:
# - API: http://localhost:8080
# - Health Check: http://localhost:8080/health
```

## 📚 Endpoints da API

### Health Check
- `GET /health` - Verificar se a aplicação está rodando

### Customers
- `GET /api/customer` - Listar todos os clientes
- `GET /api/customer/{id}` - Buscar cliente por ID
- `GET /api/customer/search?name={name}` - Buscar clientes por nome
- `POST /api/customer` - Criar novo cliente
- `PUT /api/customer/{id}` - Atualizar cliente
- `DELETE /api/customer/{id}` - Deletar cliente

### Services
- `GET /api/service` - Listar todos os serviços
- `GET /api/service/{id}` - Buscar serviço por ID
- `GET /api/service/active` - Listar serviços ativos
- `GET /api/service/category/{category}` - Buscar serviços por categoria
- `POST /api/service` - Criar novo serviço
- `PUT /api/service/{id}` - Atualizar serviço
- `DELETE /api/service/{id}` - Deletar serviço

### Staff
- `GET /api/staff` - Listar todos os funcionários
- `GET /api/staff/{id}` - Buscar funcionário por ID
- `GET /api/staff/active` - Listar funcionários ativos
- `GET /api/staff/role/{role}` - Buscar funcionários por cargo
- `POST /api/staff` - Criar novo funcionário
- `PUT /api/staff/{id}` - Atualizar funcionário
- `DELETE /api/staff/{id}` - Deletar funcionário

### Appointments
- `GET /api/appointment` - Listar todos os agendamentos
- `GET /api/appointment/{id}` - Buscar agendamento por ID
- `GET /api/appointment/customer/{customerId}` - Buscar agendamentos por cliente
- `GET /api/appointment/staff/{staffId}` - Buscar agendamentos por funcionário
- `GET /api/appointment/date/{date}` - Buscar agendamentos por data
- `POST /api/appointment` - Criar novo agendamento
- `PUT /api/appointment/{id}` - Atualizar agendamento
- `DELETE /api/appointment/{id}` - Deletar agendamento

## 🧪 Testes

```bash
# Executar todos os testes
dotnet test

# Executar testes com cobertura
dotnet test --collect:"XPlat Code Coverage"
```

## 📁 Estrutura do Projeto

```
backend-dotnet/
├── BeautySalonAPI/
│   ├── Controllers/          # Controllers da API
│   ├── Models/              # Modelos de dados
│   ├── Services/            # Lógica de negócio
│   ├── Data/                # Contexto do Entity Framework
│   └── Program.cs           # Ponto de entrada da aplicação
├── BeautySalonAPI.Tests/    # Testes unitários
└── Dockerfile              # Configuração Docker
```

## 🔧 Configuração

A aplicação utiliza banco de dados em memória por padrão. Para usar SQL Server em produção:

```csharp
// Em Program.cs, alterar a configuração do DbContext:
builder.Services.AddDbContext<BeautySalonDbContext>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("DefaultConnection")));
```

## 📝 Exemplos de Uso

### Criar um cliente
```bash
curl -X POST "https://localhost:5001/api/customer" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Maria Silva",
    "email": "maria@example.com",
    "phone": "+5511999999999",
    "address": "Rua das Flores, 123"
  }'
```

### Buscar serviços ativos
```bash
curl "https://localhost:5001/api/service/active"
```

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para detalhes.
