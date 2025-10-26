# Maven Profiles: JVM vs Native

Este projeto possui dois profiles Maven para facilitar a comparação entre execução JVM tradicional e GraalVM Native Image.

## 📋 Profiles Disponíveis

### 1. Profile `jvm` (Padrão)

Compila a aplicação como JAR executável tradicional para JVM.

**Características:**
- ✅ Build rápido (~6 segundos)
- ✅ Compatibilidade total com JVM
- ✅ Hot reload e debugging completo
- ⚠️  Startup mais lento (3-5 segundos)
- ⚠️  Maior uso de memória (256-384MB)

**Como usar:**

```bash
# Build (profile ativo por padrão)
./mvnw clean package

# Ou explicitamente
./mvnw clean package -Pjvm

# Executar
java -jar target/beauty-salon-reactive-jvm.jar \
  --spring.profiles.active=docker \
  --server.port=8085
```

**Arquivo gerado:**
- `target/beauty-salon-reactive-jvm.jar` (~46MB)

---

### 2. Profile `native`

Compila a aplicação como executável nativo usando GraalVM.

**Características:**
- ⚡ Startup instantâneo (< 2 segundos)
- 💾 Menor uso de memória (128-256MB)
- 📦 Executável standalone (não precisa de JVM)
- ⚠️  Build mais lento (3-4 minutos)
- ⚠️  Requer GraalVM instalado

**Como usar:**

```bash
# Build
./mvnw clean package -Pnative -DskipTests

# Executar
./target/beauty-salon-reactive \
  --spring.profiles.active=docker \
  --server.port=8085
```

**Arquivo gerado:**
- `target/beauty-salon-reactive` (~135MB executável nativo)

---

## 🔧 Pré-requisitos

### Para Profile JVM
- Java 21 ou superior
- Maven 3.9+

### Para Profile Native
- GraalVM 21+ (com Native Image)
- Maven 3.9+
- Variável `JAVA_HOME` apontando para GraalVM

**Instalar GraalVM Native Image:**
```bash
# Se usando SDKMAN
sdk install java 21.0.1-graal
sdk use java 21.0.1-graal

# Instalar Native Image
gu install native-image

# Verificar
native-image --version
```

---

## 📊 Comparação de Performance

| Métrica | JVM | Native | Melhoria |
|---------|-----|--------|----------|
| **Build Time** | ~6s | ~3-4min | -97% |
| **Startup Time** | 3-5s | 1-2s | +60-70% |
| **Memory Usage** | 256-384MB | 128-256MB | +40% |
| **JAR/Binary Size** | 46MB | 135MB | -66% |
| **Throughput** | ~100 req/s | ~100 req/s | Similar |
| **Latency** | 7-10ms | 6-8ms | +15% |

---

## 🎯 Quando Usar Cada Profile

### Use `jvm` quando:
- ✅ Desenvolvimento local
- ✅ Debugging e profiling
- ✅ Testes rápidos
- ✅ CI/CD com builds frequentes
- ✅ Ambiente com JVM já instalada

### Use `native` quando:
- ✅ Produção (menor custo de cloud)
- ✅ Containers/Kubernetes
- ✅ Serverless/FaaS
- ✅ Edge computing
- ✅ Ambientes com recursos limitados
- ✅ Startup time é crítico

---

## 🚀 Exemplos de Uso

### Build e Test Rápido (JVM)
```bash
# Desenvolvimento rápido
./mvnw clean test
./mvnw spring-boot:run
```

### Build para Produção (Native)
```bash
# Build otimizado
./mvnw clean package -Pnative -DskipTests

# Executar com configurações de produção
./target/beauty-salon-reactive \
  --spring.profiles.active=docker \
  --spring.data.cassandra.contact-points=cassandra \
  --server.port=8085 \
  --logging.level.root=WARN
```

### Benchmark Comparativo
```bash
# Usar script de benchmark
cd ../scripts
./benchmark-native-simple.sh
```

---

## 📝 Configurações dos Profiles

### Profile JVM
```xml
<profile>
    <id>jvm</id>
    <activation>
        <activeByDefault>true</activeByDefault>
    </activation>
    <build>
        <finalName>beauty-salon-reactive-jvm</finalName>
    </build>
</profile>
```

### Profile Native
```xml
<profile>
    <id>native</id>
    <build>
        <finalName>beauty-salon-reactive-native</finalName>
        <plugins>
            <plugin>
                <groupId>org.graalvm.buildtools</groupId>
                <artifactId>native-maven-plugin</artifactId>
                <!-- Configurações GraalVM -->
            </plugin>
        </plugins>
    </build>
</profile>
```

---

## 🐛 Troubleshooting

### Erro: "native-image not found"
```bash
# Verificar se GraalVM está instalado
echo $JAVA_HOME
java -version

# Instalar native-image
gu install native-image
```

### Erro: Build nativo falha
```bash
# Limpar e rebuild
./mvnw clean
./mvnw package -Pnative -DskipTests -X
```

### JVM não inicia
```bash
# Verificar Java version
java -version  # Deve ser 21+

# Rebuild
./mvnw clean package -Pjvm
```

---

## 📚 Referências

- [GraalVM Native Image](https://www.graalvm.org/latest/reference-manual/native-image/)
- [Spring Boot Native](https://docs.spring.io/spring-boot/docs/current/reference/html/native-image.html)
- [Maven Profiles](https://maven.apache.org/guides/introduction/introduction-to-profiles.html)

---

## 🎉 Conclusão

Os profiles Maven facilitam a alternância entre JVM e Native, permitindo:
- **Desenvolvimento rápido** com JVM
- **Deploy otimizado** com Native
- **Comparações de performance** objetivas
- **Flexibilidade** para diferentes ambientes

Escolha o profile adequado para seu caso de uso! 🚀
