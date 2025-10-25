# 🔍 Problema: Native AOT + Cassandra (Netty/JCTools)

## ❌ **Erro Identificado**

```
java.lang.NoSuchFieldException: producerIndex
at io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess.fieldOffset
```

## 🎯 **Causa Técnica**

### **1. O que é JCTools?**
- **JCTools**: Java Concurrency Tools
- **Função**: Estruturas de dados lock-free (queues, stacks)
- **Uso**: Netty usa para comunicação assíncrona de alta performance

### **2. Problema no Native Image**
- **Unsafe Access**: JCTools usa `sun.misc.Unsafe` para acesso direto à memória
- **Reflection**: Acessa campos privados via reflection
- **GraalVM**: Não consegue rastrear esses acessos em build-time

### **3. Quando Ocorre**
- ✅ **Sem Cassandra**: Funciona (usando H2 in-memory)
- ❌ **Com Cassandra**: Falha (Netty usado para conexão)

---

## 🔧 **Soluções Técnicas**

### **Opção 1: Substituir JCTools (Recomendada)**
```xml
<!-- pom.xml - Excluir JCTools do Netty -->
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.jctools</groupId>
            <artifactId>jctools-core</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<!-- Adicionar versão compatível -->
<dependency>
    <groupId>org.jctools</groupId>
    <artifactId>jctools-core</artifactId>
    <version>4.0.1</version>
</dependency>
```

### **Opção 2: Configurar Reflection Hints**
```java
// Adicionar em NativeRuntimeHints.java
hints.reflection()
    .registerType(
        Class.forName("io.netty.util.internal.shaded.org.jctools.queues.MpscUnpaddedArrayQueueProducerIndexField"),
        hint -> hint.withMembers(INVOKE_DECLARED_METHODS, DECLARED_FIELDS)
    );
```

### **Opção 3: Usar Netty Native**
```xml
<!-- Usar Netty transport nativo -->
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-transport-native-epoll</artifactId>
    <classifier>linux-x86_64</classifier>
</dependency>
```

---

## 📊 **Status Atual**

| Cenário | Status | Solução |
|---------|--------|---------|
| **Sem Cassandra** | ✅ Funciona | H2 in-memory |
| **Com Cassandra** | ❌ Falha | JCTools issue |
| **Produção** | ⚠️ Parcial | Funciona sem DB |

---

## 🚀 **Soluções Imediatas**

### **1. Usar JAR + Virtual Threads (Recomendado)**
```bash
# Funciona perfeitamente com Cassandra
java -jar target/beauty-salon-reactive-1.0.0.jar \
  --spring.profiles.active=docker
```

### **2. Usar Test Mode**
```bash
# Funciona sem Cassandra
docker run -p 8085:8085 beauty-salon-reactive:native-final \
  --spring.profiles.active=test
```

### **3. Aguardar Correção**
- ✅ Build AOT funciona
- ✅ Docker Compose funciona
- ✅ Infraestrutura completa
- ⚠️ Aguardar resolução JCTools

---

## 🔬 **Análise Técnica Detalhada**

### **Stack Trace Analysis**
```
Caused by: java.lang.NoSuchFieldException: producerIndex
    at java.lang.Class.getDeclaredField()
    at io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess.fieldOffset()
```

**O que acontece:**
1. Netty inicializa queue para comunicação
2. JCTools tenta acessar campo `producerIndex` via Unsafe
3. GraalVM não incluiu o campo na imagem nativa
4. Runtime falha com NoSuchFieldException

### **Por que funciona sem Cassandra:**
- Sem conexão DB, Netty não inicializa
- Spring Boot usa H2 in-memory
- Nenhum componente de rede ativado

### **Por que falha com Cassandra:**
- Cassandra driver ativa Netty
- Comunicação TCP requer queues do Netty
- JCTools inicializado e falha

---

## 🎯 **Recomendação**

**Para Produção Imediata:**
```bash
# Use JAR + Virtual Threads (funciona perfeitamente)
java -jar target/beauty-salon-reactive-1.0.0.jar \
  --spring.profiles.active=docker
```

**Para Native AOT Completo:**
- Aguardar correção upstream
- Ou implementar solução customizada acima

**Status**: Infraestrutura 100% pronta, aguardando resolução técnica específica.

---

## 📚 **Referências**

- [GraalVM Native Image Limitations](https://www.graalvm.org/latest/reference-manual/native-image/Limitations/)
- [Netty + GraalVM Issues](https://github.com/netty/netty/issues/10327)
- [JCTools Unsafe Access](https://github.com/JCTools/JCTools/issues/284)
