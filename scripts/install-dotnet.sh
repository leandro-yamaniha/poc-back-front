#!/bin/bash

# Script de Instalação do .NET Core
# Compatível com macOS, Linux e Windows (via WSL/Git Bash)
# Versão: 8.0 LTS (recomendada para produção)

set -e  # Parar em caso de erro

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para logging
log() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

# Detectar sistema operacional
detect_os() {
    case "$(uname -s)" in
        "Darwin")
            echo "macos"
            ;;
        "Linux")
            echo "linux"
            ;;
        "MINGW64"*|"MSYS"*)
            echo "windows"
            ;;
        *)
            error "Sistema operacional não suportado: $(uname -s)"
            exit 1
            ;;
    esac
}

# Instalar no macOS
install_macos() {
    log "Detectado macOS"

    # Método 1: Tentar Homebrew primeiro
    if command -v brew &> /dev/null; then
        log "Tentando instalar .NET Core via Homebrew..."

        if brew list --cask | grep -q dotnet-sdk; then
            warn ".NET SDK já instalado via Homebrew. Verificando se está funcionando..."
            if brew reinstall --cask dotnet-sdk; then
                success ".NET SDK reinstalado via Homebrew"
            else
                warn "Problemas com Homebrew. Tentando método alternativo..."
                install_macos_alternative
            fi
        else
            if brew install --cask dotnet-sdk; then
                success ".NET Core instalado via Homebrew"
            else
                warn "Problemas com Homebrew. Tentando método alternativo..."
                install_macos_alternative
            fi
        fi
    else
        warn "Homebrew não encontrado. Usando método alternativo..."
        install_macos_alternative
    fi

    # Verificar instalação
    verify_installation
}

# Método alternativo de instalação no macOS
install_macos_alternative() {
    log "Usando método alternativo de instalação no macOS..."

    # Tentar instalar via script oficial da Microsoft
    if curl -sSL https://dot.net/v1/dotnet-install.sh | bash /dev/stdin --channel 8.0; then
        success ".NET Core instalado via script oficial"
    else
        # Tentar download direto
        warn "Script oficial falhou. Tentando download direto..."

        local temp_dir=$(mktemp -d)
        cd "$temp_dir"

        # Baixar o instalador PKG
        if curl -L -o dotnet-sdk.pkg "https://download.visualstudio.microsoft.com/download/pr/dotnet-sdk-8.0.400-osx-x64.pkg"; then
            # Instalar o pacote
            if sudo installer -pkg dotnet-sdk.pkg -target /; then
                success ".NET Core instalado via pacote PKG"
                # Limpar
                rm -rf "$temp_dir"
            else
                error "Falha na instalação do pacote PKG"
                rm -rf "$temp_dir"
                exit 1
            fi
        else
            error "Falha no download do pacote PKG"
            rm -rf "$temp_dir"
            exit 1
        fi
    fi
}

# Instalar no Linux
install_linux() {
    log "Detectado Linux"

    # Detectar distribuição
    if [ -f /etc/debian_version ]; then
        install_debian
    elif [ -f /etc/redhat-release ]; then
        install_redhat
    elif [ -f /etc/arch-release ]; then
        install_arch
    else
        error "Distribuição Linux não suportada"
        echo "Por favor, instale manualmente: https://learn.microsoft.com/dotnet/core/install/linux"
        exit 1
    fi

    # Verificar instalação
    verify_installation
}

# Instalar no Debian/Ubuntu
install_debian() {
    log "Instalando .NET Core no Debian/Ubuntu..."

    # Atualizar pacotes
    sudo apt update

    # Instalar dependências
    sudo apt install -y wget

    # Baixar e instalar Microsoft package repository
    wget https://packages.microsoft.com/config/ubuntu/22.04/packages-microsoft-prod.deb -O packages-microsoft-prod.deb
    sudo dpkg -i packages-microsoft-prod.deb
    rm packages-microsoft-prod.deb

    # Instalar .NET Core
    sudo apt update
    sudo apt install -y dotnet-sdk-8.0

    success ".NET Core instalado com sucesso no Ubuntu/Debian"
}

# Instalar no Red Hat/CentOS/Fedora
install_redhat() {
    log "Instalando .NET Core no Red Hat/CentOS/Fedora..."

    # Instalar dependências
    sudo dnf install -y wget

    # Baixar e instalar Microsoft package repository
    sudo dnf install -y https://packages.microsoft.com/config/rhel/8/packages-microsoft-prod.rpm

    # Instalar .NET Core
    sudo dnf install -y dotnet-sdk-8.0

    success ".NET Core instalado com sucesso no Red Hat/CentOS/Fedora"
}

# Instalar no Arch Linux
install_arch() {
    log "Instalando .NET Core no Arch Linux..."

    # Instalar .NET Core via AUR (se yay estiver disponível)
    if command -v yay &> /dev/null; then
        yay -S dotnet-core-sdk
    else
        warn "yay não encontrado. Instalando via package manager oficial..."
        # Método alternativo para Arch
        sudo pacman -S wget
        wget https://download.visualstudio.microsoft.com/download/pr/dotnet-core-sdk-linux.tar.gz
        sudo mkdir -p /opt/dotnet
        sudo tar zxf dotnet-core-sdk-linux.tar.gz -C /opt/dotnet
        sudo ln -sf /opt/dotnet/dotnet /usr/local/bin/dotnet
    fi

    success ".NET Core instalado com sucesso no Arch Linux"
}

# Instalar no Windows
install_windows() {
    log "Detectado Windows"

    # Verificar se está rodando via WSL ou Git Bash
    if [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "cygwin" ]]; then
        warn "Ambiente Windows detectado. Usando winget..."

        # Instalar via winget (Windows Package Manager)
        if command -v winget &> /dev/null; then
            winget install Microsoft.DotNet.SDK.8
        else
            error "winget não encontrado. Instalando manualmente..."
            echo "Por favor, baixe e instale manualmente:"
            echo "https://dotnet.microsoft.com/download/dotnet/8.0"
            exit 1
        fi
    else
        error "Para Windows nativo, use o instalador oficial:"
        echo "https://dotnet.microsoft.com/download/dotnet/8.0"
        exit 1
    fi

    # Verificar instalação
    verify_installation
}

# Verificar instalação
verify_installation() {
    log "Verificando instalação do .NET Core..."

    # Tentar encontrar o dotnet em diferentes locais
    local dotnet_cmd=""

    # Método 1: Verificar se está no PATH
    if command -v dotnet &> /dev/null; then
        dotnet_cmd="dotnet"
    # Método 2: Tentar caminhos comuns no macOS
    elif [[ -x "/usr/local/share/dotnet/dotnet" ]]; then
        dotnet_cmd="/usr/local/share/dotnet/dotnet"
        export PATH="/usr/local/share/dotnet:$PATH"
    elif [[ -x "/opt/homebrew/bin/dotnet" ]]; then
        dotnet_cmd="/opt/homebrew/bin/dotnet"
        export PATH="/opt/homebrew/bin:$PATH"
    elif [[ -x "$HOME/.dotnet/dotnet" ]]; then
        dotnet_cmd="$HOME/.dotnet/dotnet"
        export PATH="$HOME/.dotnet:$PATH"
    # Método 3: Tentar encontrar em outros locais
    else
        # Procurar em locais comuns
        for path in "/usr/local/bin" "/usr/bin" "/opt" "$HOME"; do
            if [[ -x "$path/dotnet" ]]; then
                dotnet_cmd="$path/dotnet"
                export PATH="$path:$PATH"
                break
            fi
        done
    fi

    if [[ -n "$dotnet_cmd" ]]; then
        local version=$($dotnet_cmd --version 2>/dev/null || echo "unknown")
        success ".NET Core encontrado! Versão: $version"

        # Testar se funciona
        if $dotnet_cmd --version &> /dev/null; then
            success ".NET Core funcionando perfeitamente!"

            # Mostrar informações adicionais se disponível
            echo ""
            echo -e "${BLUE}=== Informações da Instalação ==="${NC}
            if command -v dotnet &> /dev/null; then
                dotnet --info 2>/dev/null | head -10 || echo "Informações limitadas disponíveis"
            fi

            # Configurar PATH se necessário
            setup_path
        else
            warn ".NET Core encontrado mas com problemas. Tentando reparar..."
            setup_path
        fi

    else
        error ".NET Core não foi encontrado em nenhum local comum"
        warn "Tentando instalar novamente..."
        case "$(detect_os)" in
            "macos")
                warn "Tentando forçar reinstalação via Homebrew..."
                brew reinstall --cask dotnet-sdk
                ;;
            "linux")
                warn "Verifique se o .NET foi instalado corretamente"
                ;;
            "windows")
                warn "Verifique se o winget instalou corretamente"
                ;;
        esac
        exit 1
    fi
}

# Configurar PATH
setup_path() {
    local dotnet_path=""

    case "$(detect_os)" in
        "macos")
            dotnet_path="/usr/local/share/dotnet"
            ;;
        "linux")
            dotnet_path="/usr/share/dotnet"
            ;;
        "windows")
            dotnet_path="/c/Program Files/dotnet"
            ;;
    esac

    # Adicionar ao PATH se necessário
    if [[ ":$PATH:" != *":$dotnet_path:"* ]]; then
        warn "Adicionando .NET ao PATH..."

        case "$(detect_os)" in
            "macos")
                echo 'export PATH="$HOME/.dotnet:$PATH"' >> ~/.zshrc
                echo 'export PATH="$HOME/.dotnet:$PATH"' >> ~/.bashrc
                ;;
            "linux")
                echo 'export PATH="$HOME/.dotnet:$PATH"' >> ~/.bashrc
                echo 'export PATH="$HOME/.dotnet:$PATH"' >> ~/.profile
                ;;
            "windows")
                echo "No Windows, o PATH é configurado automaticamente pelo instalador"
                ;;
        esac

        success "PATH configurado. Reinicie o terminal ou execute: source ~/.bashrc"
    fi
}

# Função principal
main() {
    echo -e "${GREEN}"
    echo "=================================="
    echo "  Instalador do .NET Core 8.0"
    echo "=================================="
    echo -e "${NC}"

    local os=$(detect_os)

    case "$os" in
        "macos")
            install_macos
            ;;
        "linux")
            install_linux
            ;;
        "windows")
            install_windows
            ;;
    esac

    echo ""
    echo -e "${GREEN}=================================="
    echo "  Instalação Concluída!"
    echo "=================================="
    echo -e "${NC}"
    echo "Para testar a instalação:"
    echo "  dotnet --version"
    echo ""
    echo "Para criar seu primeiro projeto:"
    echo "  dotnet new console -o MyApp"
    echo "  cd MyApp"
    echo "  dotnet run"
    echo ""
    echo "Happy coding! 🚀"
}

# Executar apenas se chamado diretamente
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
