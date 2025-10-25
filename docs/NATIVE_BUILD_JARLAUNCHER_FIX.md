# 🔧 Fix: JarLauncher Issue no Build Nativo

## ❌ **Problema Identificado**

```
Exception in thread "main" java.util.zip.ZipException: zip END header not found
at org.springframework.boot.loader.launch.JarLauncher.main(JarLauncher.java:40)
```

**Causa**: O executável nativo `beauty-salon-reactive-native` foi compilado a partir do JAR fat do Spring Boot, que usa o `JarLauncher`. O GraalVM não consegue processar o JarLauncher em tempo de execução porque ele tenta abrir o próprio executável como um arquivo ZIP.

---

## ✅ **Soluções Disponíveis**

### **Solução 1: Usar Spring Boot 3 Native Support (RECOMENDADO)**

Spring Boot 3 tem suporte nativo integrado via Spring AOT (Ahead-of-Time compilation).

#### **Passo 1: Adicionar dependência Spring Native**

Já está configurado no `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aot</artifactId>
</dependency>
```

#### **Passo 2: Build com Maven Native Profile**

```bash
cd backend/java-reactive

# Limpar builds anteriores
./mvnw clean

# Build nativo usando Spring Boot Native
./mvnw -Pnative native:compile -DskipTests

# O executável será gerado em:
# target/beauty-salon-reactive
```

#### **Passo 3: Executar**

```bash
./target/beauty-salon-reactive \
  --spring.profiles.active=docker \
  --spring.data.cassandra.contact-points=localhost \
  --server.port=8085
```

---

### **Solução 2: Build Direto com Classe Principal**

Usar o script `build-native-direct.sh` que extrai o JAR e compila usando a classe principal diretamente.

```bash
cd backend
./scripts/build-native-direct.sh
```

Este script:
1. Extrai o conteúdo do JAR
2. Monta o classpath manualmente
3. Compila usando `com.beautysalon.reactive.BeautySalonReactiveApplication` diretamente
4. Evita o JarLauncher completamente

---

### **Solução 3: Usar Buildpacks (Cloud Native)**

Para deployment em produção, usar Cloud Native Buildpacks que geram imagens nativas automaticamente:

```bash
cd backend/java-reactive

# Build com Buildpacks
./mvnw spring-boot:build-image -Pnative

# Executar container
docker run -p 8085:8085 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e SPRING_DATA_CASSANDRA_CONTACT_POINTS=cassandra \
  beauty-salon-reactive:1.0.0
```

---

## 🚀 **Solução Rápida (Agora)**

### **Opção A: Rebuild com Spring Native**

```bash
cd backend/java-reactive

# Remover executável problemático
rm -f target/beauty-salon-reactive-native

# Build correto com Spring Native
./mvnw clean -Pnative native:compile -DskipTests

# Executar novo executável
./target/beauty-salon-reactive --server.port=8085
```

### **Opção B: Usar JAR com Virtual Threads**

Enquanto isso, use o JAR otimizado com Virtual Threads que já funciona:

```bash
cd backend/java-reactive

# Executar JAR com Virtual Threads
java -jar target/beauty-salon-reactive-1.0.0.jar \
  --spring.profiles.active=docker \
  --server.port=8085
```

Performance será excelente com Virtual Threads (quase tão boa quanto nativo).

---

## 📊 **Comparação de Performance**

| Método | Startup | Memory | Complexidade |
|--------|---------|--------|--------------|
| **JAR + Virtual Threads** | ~3-5s | 256-384MB | ✅ Simples |
| **Native (correto)** | <1s | 128-256MB | ⚠️ Complexo |
| **Native (JarLauncher)** | ❌ Não funciona | - | - |

---

## 🎯 **Recomendação**

**Para Desenvolvimento**: Use JAR + Virtual Threads
- Startup rápido (3-5s)
- Sem complexidade de build nativo
- Hot reload funciona
- Debugging completo

**Para Produção**: Use Native Image correto
- Startup instantâneo (<1s)
- Menor uso de memória
- Menor custo cloud
- Build via CI/CD com Spring Native

---

## 📝 **Próximos Passos**

1. ✅ **Testar JAR + Virtual Threads** (já funciona)
2. 🔄 **Rebuild com Spring Native** (solução correta)
3. 🔄 **Configurar CI/CD** para builds nativos
4. 🔄 **Benchmarks** de performance real

---

## 🔗 **Referências**

- [Spring Boot Native Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/native-image.html)
- [GraalVM Native Image](https://www.graalvm.org/latest/reference-manual/native-image/)
- [Spring AOT](https://docs.spring.io/spring-framework/reference/core/aot.html)
