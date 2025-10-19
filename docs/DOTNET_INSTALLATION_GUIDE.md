# Guia de Instalação do .NET Core 8.0

Este guia cobre a instalação do .NET Core 8.0 LTS em diferentes sistemas operacionais.

## 🚀 Instalação Automática (Recomendada)

### Script Universal
```bash
# Baixe e execute o script automático
curl -fsSL https://raw.githubusercontent.com/dotnet/install-scripts/main/src/dotnet-install.sh | bash

# Ou use nosso script personalizado
./backend/dotnet/install-dotnet.sh
```

## 📋 Instalação Manual por Plataforma

---

## 🍎 macOS

### Método 1: Homebrew (Recomendado)
```bash
# Instalar Homebrew (se não tiver)
curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh | bash

# Instalar .NET Core
brew install --cask dotnet-sdk

# Verificar instalação
dotnet --version
```

### Método 2: Download Oficial
```bash
# Baixar o instalador
curl -L -o dotnet-sdk.pkg https://download.visualstudio.microsoft.com/download/pr/dotnet-sdk-8.0.400-osx-x64.pkg

# Instalar
sudo installer -pkg dotnet-sdk.pkg -target /

# Limpar
rm dotnet-sdk.pkg

# Verificar
dotnet --version
```

### Método 3: Usando o Script Oficial
```bash
curl -sSL https://dot.net/v1/dotnet-install.sh | bash /dev/stdin --channel 8.0
```

---

## 🐧 Linux

### Ubuntu/Debian
```bash
# Atualizar pacotes
sudo apt update

# Instalar dependências
sudo apt install -y wget

# Baixar e instalar o repositório Microsoft
wget https://packages.microsoft.com/config/ubuntu/22.04/packages-microsoft-prod.deb -O packages-microsoft-prod.deb
sudo dpkg -i packages-microsoft-prod.deb
rm packages-microsoft-prod.deb

# Instalar .NET Core
sudo apt update
sudo apt install -y dotnet-sdk-8.0

# Verificar
dotnet --version
```

### CentOS/RHEL/Fedora
```bash
# Instalar dependências
sudo dnf install -y wget

# Baixar e instalar o repositório Microsoft
sudo dnf install -y https://packages.microsoft.com/config/rhel/8/packages-microsoft-prod.rpm

# Instalar .NET Core
sudo dnf install -y dotnet-sdk-8.0

# Verificar
dotnet --version
```

### Arch Linux
```bash
# Usando AUR (se tiver yay)
yay -S dotnet-core-sdk

# Ou método manual
sudo pacman -S wget
wget https://download.visualstudio.microsoft.com/download/pr/dotnet-core-sdk-linux.tar.gz
sudo mkdir -p /opt/dotnet
sudo tar zxf dotnet-core-sdk-linux.tar.gz -C /opt/dotnet
sudo ln -sf /opt/dotnet/dotnet /usr/local/bin/dotnet

# Verificar
dotnet --version
```

---

## 🪟 Windows

### Método 1: Windows Package Manager (winget)
```powershell
# Instalar .NET Core
winget install Microsoft.DotNet.SDK.8

# Verificar
dotnet --version
```

### Método 2: Download Oficial
1. Acesse: https://dotnet.microsoft.com/download/dotnet/8.0
2. Baixe o instalador para Windows
3. Execute o instalador `.exe`
4. Siga as instruções na tela
5. Verifique: `dotnet --version`

### Método 3: Chocolatey (Package Manager)
```powershell
# Instalar Chocolatey (se não tiver)
Set-ExecutionPolicy Bypass -Scope Process -Force
iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Instalar .NET Core
choco install dotnet-8.0-sdk

# Verificar
dotnet --version
```

---

## 🐳 Docker

Se preferir usar Docker:

```bash
# Imagem oficial do .NET Core
docker run -it mcr.microsoft.com/dotnet/sdk:8.0

# Para desenvolvimento
docker run -v $(pwd):/app -w /app mcr.microsoft.com/dotnet/sdk:8.0 bash
```

---

## ✅ Verificação da Instalação

Após a instalação, verifique se tudo está funcionando:

```bash
# Verificar versão
dotnet --version

# Verificar informações detalhadas
dotnet --info

# Criar e executar um projeto de teste
dotnet new console -o TestApp
cd TestApp
dotnet run
```

Resultado esperado:
```
8.0.xxx
Hello, World!
```

---

## 🔧 Configuração do Ambiente

### macOS/Linux
Adicione ao seu `~/.bashrc` ou `~/.zshrc`:
```bash
export PATH="$HOME/.dotnet:$PATH"
export DOTNET_CLI_TELEMETRY_OPTOUT=1  # Opcional: desabilitar telemetria
```

### Windows
O instalador configura automaticamente as variáveis de ambiente.

---

## 🛠 Desenvolvimento

### Visual Studio Code
1. Instalar extensão: "C# for Visual Studio Code"
2. Recarregar VS Code
3. Abrir pasta do projeto .NET

### Visual Studio (Windows)
1. Baixar: https://visualstudio.microsoft.com/downloads/
2. Selecionar workload ".NET desktop development"
3. Incluir .NET Core runtime

### Rider (JetBrains)
1. Baixar: https://www.jetbrains.com/rider/
2. Suporte nativo para .NET Core

---

## 🚨 Solução de Problemas

### .NET não encontrado no PATH
```bash
# macOS/Linux
source ~/.bashrc
# ou
source ~/.zshrc

# Windows
refreshenv
```

### Permissões no macOS
```bash
# Permitir execução de apps baixados
sudo spctl --master-disable
```

### Problemas de certificado no Linux
```bash
# Atualizar certificados
sudo apt install --reinstall ca-certificates
sudo update-ca-certificates
```

---

## 📚 Recursos Úteis

- [Documentação Oficial .NET](https://learn.microsoft.com/dotnet/core/)
- [Guia de Instalação Detalhado](https://learn.microsoft.com/dotnet/core/install/)
- [Downloads .NET](https://dotnet.microsoft.com/download/dotnet)
- [Comunidade .NET](https://dotnet.microsoft.com/platform/community)

---

## 🎯 Próximos Passos

Após instalar o .NET Core, você pode:

1. **Criar seu primeiro projeto:**
   ```bash
   dotnet new webapi -n MyAPI
   cd MyAPI
   dotnet run
   ```

2. **Explorar o ecossistema:**
   - ASP.NET Core (Web APIs, MVC)
   - Entity Framework Core (Banco de dados)
   - xUnit (Testes)
   - Blazor (WebAssembly/SPA)

3. **Contribuir com projetos open source:**
   - GitHub: https://github.com/dotnet
   - Comunidade brasileira: https://dotnet.org.br/

---

**🎉 Parabéns! .NET Core 8.0 instalado com sucesso!**
