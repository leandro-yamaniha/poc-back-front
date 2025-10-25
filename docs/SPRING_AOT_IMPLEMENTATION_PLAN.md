# 🚀 Spring Boot AOT Implementation Plan

## 📋 **Objetivo**

Implementar Spring Boot AOT (Ahead-of-Time) compilation para gerar builds nativos funcionais do backend Java Reactive, resolvendo os problemas atuais de JarLauncher e configuração de reflexão.

---

## 🎯 **Problemas Atuais a Resolver**

### ❌ **Build Minimal (37MB)**
- Usa JarLauncher do Spring Boot
- ZipException ao executar
- Não consegue abrir o próprio executável como ZIP

### ❌ **Build Direct (58MB)**
- Faltam configurações de reflexão do Spring
- ClassNotFoundException em runtime
- Spring factories não carregados corretamente

---

## ✅ **Solução: Spring Boot 3 AOT**

Spring Boot 3 tem suporte nativo integrado via **Spring AOT (Ahead-of-Time)** que:
- ✅ Processa aplicação em build time
- ✅ Gera hints de reflexão automaticamente
- ✅ Otimiza para GraalVM Native Image
- ✅ Resolve Spring factories em build time
- ✅ Suporta Reactive/WebFlux nativamente

---

## 📝 **Plano de Implementação**

### **Fase 1: Configuração Base AOT**

#### **1.1 Adicionar Dependências Spring AOT**

```xml
<!-- pom.xml -->
<dependencies>
    <!-- Spring Boot AOT (já incluído no Spring Boot 3) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aot</artifactId>
    </dependency>
    
    <!-- GraalVM SDK para hints customizados -->
    <dependency>
        <groupId>org.graalvm.sdk</groupId>
        <artifactId>graal-sdk</artifactId>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

#### **1.2 Configurar Spring Boot Maven Plugin com AOT**

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <version>3.5.4</version>
    <configuration>
        <image>
            <builder>paketobuildpacks/builder:tiny</builder>
            <env>
                <BP_NATIVE_IMAGE>true</BP_NATIVE_IMAGE>
            </env>
        </image>
    </configuration>
    <executions>
        <execution>
            <id>process-aot</id>
            <goals>
                <goal>process-aot</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

#### **1.3 Configurar Native Maven Plugin**

```xml
<plugin>
    <groupId>org.graalvm.buildtools</groupId>
    <artifactId>native-maven-plugin</artifactId>
    <version>0.10.3</version>
    <extensions>true</extensions>
    <configuration>
        <classesDirectory>${project.build.outputDirectory}</classesDirectory>
        <metadataRepository>
            <enabled>true</enabled>
        </metadataRepository>
    </configuration>
    <executions>
        <execution>
            <id>add-reachability-metadata</id>
            <goals>
                <goal>add-reachability-metadata</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

---

### **Fase 2: Runtime Hints Customizados**

#### **2.1 Criar RuntimeHintsRegistrar para Modelos**

```java
// src/main/java/com/beautysalon/reactive/config/NativeRuntimeHints.java
package com.beautysalon.reactive.config;

import com.beautysalon.reactive.model.*;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

@Configuration
@ImportRuntimeHints(NativeRuntimeHints.BeautySalonRuntimeHints.class)
public class NativeRuntimeHints {

    static class BeautySalonRuntimeHints implements RuntimeHintsRegistrar {
        
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            // Registrar modelos para reflexão
            hints.reflection()
                .registerType(Customer.class, hint -> hint
                    .withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                                MemberCategory.INVOKE_DECLARED_METHODS,
                                MemberCategory.DECLARED_FIELDS))
                .registerType(Service.class, hint -> hint
                    .withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                                MemberCategory.INVOKE_DECLARED_METHODS,
                                MemberCategory.DECLARED_FIELDS))
                .registerType(Staff.class, hint -> hint
                    .withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                                MemberCategory.INVOKE_DECLARED_METHODS,
                                MemberCategory.DECLARED_FIELDS))
                .registerType(Appointment.class, hint -> hint
                    .withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                                MemberCategory.INVOKE_DECLARED_METHODS,
                                MemberCategory.DECLARED_FIELDS));
            
            // Registrar recursos
            hints.resources()
                .registerPattern("application*.yml")
                .registerPattern("application*.yaml")
                .registerPattern("application*.properties")
                .registerPattern("META-INF/spring.factories")
                .registerPattern("META-INF/spring/*");
        }
    }
}
```

#### **2.2 Criar RuntimeHints para Cassandra**

```java
// src/main/java/com/beautysalon/reactive/config/CassandraRuntimeHints.java
package com.beautysalon.reactive.config;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class CassandraRuntimeHints implements RuntimeHintsRegistrar {
    
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        // Cassandra Driver
        hints.reflection()
            .registerType(CqlSession.class, hint -> hint
                .withMembers(MemberCategory.INVOKE_PUBLIC_METHODS))
            .registerType(Row.class, hint -> hint
                .withMembers(MemberCategory.INVOKE_PUBLIC_METHODS));
        
        // Cassandra resources
        hints.resources()
            .registerPattern("reference.conf")
            .registerPattern("com/datastax/**/*.properties");
        
        // Cassandra serialization
        hints.serialization()
            .registerType(com.datastax.oss.driver.api.core.type.DataType.class);
    }
}
```

#### **2.3 Criar RuntimeHints para Reactor/Netty**

```java
// src/main/java/com/beautysalon/reactive/config/ReactorRuntimeHints.java
package com.beautysalon.reactive.config;

import io.netty.channel.Channel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class ReactorRuntimeHints implements RuntimeHintsRegistrar {
    
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        // Reactor types
        hints.reflection()
            .registerType(Mono.class, hint -> hint
                .withMembers(MemberCategory.INVOKE_PUBLIC_METHODS))
            .registerType(Flux.class, hint -> hint
                .withMembers(MemberCategory.INVOKE_PUBLIC_METHODS));
        
        // Netty
        hints.reflection()
            .registerType(Channel.class, hint -> hint
                .withMembers(MemberCategory.INVOKE_PUBLIC_METHODS));
        
        // Netty resources
        hints.resources()
            .registerPattern("META-INF/native-image/io.netty/**/*");
    }
}
```

---

### **Fase 3: Profile Nativo Maven**

#### **3.1 Criar Profile Native no pom.xml**

```xml
<profiles>
    <profile>
        <id>native</id>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <executions>
                        <execution>
                            <id>process-aot</id>
                            <goals>
                                <goal>process-aot</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
                <plugin>
                    <groupId>org.graalvm.buildtools</groupId>
                    <artifactId>native-maven-plugin</artifactId>
                    <configuration>
                        <buildArgs>
                            <arg>--no-fallback</arg>
                            <arg>--install-exit-handlers</arg>
                            <arg>-H:+ReportExceptionStackTraces</arg>
                            <arg>--enable-url-protocols=http,https</arg>
                        </buildArgs>
                    </configuration>
                    <executions>
                        <execution>
                            <id>build-native</id>
                            <goals>
                                <goal>compile-no-fork</goal>
                            </goals>
                            <phase>package</phase>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

---

### **Fase 4: Scripts de Build AOT**

#### **4.1 Script de Build AOT Completo**

```bash
#!/bin/bash
# build-aot-native.sh

set -e

echo "🚀 Building Spring Boot AOT Native Image..."

# Step 1: Process AOT
echo "▶ Step 1: Processing AOT..."
./mvnw clean spring-boot:process-aot -DskipTests

# Step 2: Build Native Image
echo "▶ Step 2: Building Native Image..."
./mvnw -Pnative native:compile -DskipTests

# Step 3: Test
echo "▶ Step 3: Testing Native Image..."
if [ -f "target/beauty-salon-reactive" ]; then
    echo "✅ Native image created successfully!"
    ls -lh target/beauty-salon-reactive
    file target/beauty-salon-reactive
else
    echo "❌ Native image not found!"
    exit 1
fi

echo "🎉 AOT Native Build Complete!"
```

#### **4.2 Script de Test AOT**

```bash
#!/bin/bash
# test-aot-native.sh

set -e

echo "🧪 Testing AOT Native Image..."

# Start native image
./target/beauty-salon-reactive \
    --spring.profiles.active=test \
    --server.port=8085 \
    --logging.level.root=INFO &

PID=$!
echo "Started with PID: $PID"

# Wait for startup
sleep 10

# Test health endpoint
if curl -f http://localhost:8085/actuator/health; then
    echo "✅ Health check passed!"
else
    echo "❌ Health check failed!"
    kill $PID
    exit 1
fi

# Cleanup
kill $PID
echo "🎉 AOT Native Test Complete!"
```

---

### **Fase 5: Comandos de Build**

#### **5.1 Build AOT Completo**

```bash
# Build com AOT processing
./mvnw clean package -Pnative -DskipTests

# Executar
./target/beauty-salon-reactive --server.port=8085
```

#### **5.2 Build AOT com Buildpacks (Docker)**

```bash
# Build container nativo com Buildpacks
./mvnw spring-boot:build-image -Pnative

# Run container
docker run -p 8085:8085 \
  -e SPRING_PROFILES_ACTIVE=docker \
  beauty-salon-reactive:1.0.0
```

---

## 📊 **Resultados Esperados**

### **Performance**
- ✅ Startup: < 1 segundo (vs 3-5s JAR)
- ✅ Memory: 128-256MB (vs 256-384MB JAR)
- ✅ Size: ~40-50MB executável
- ✅ CPU: Menor uso em steady state

### **Funcionalidade**
- ✅ Todos endpoints funcionando
- ✅ Cassandra connectivity
- ✅ Reactive streams
- ✅ Actuator endpoints
- ✅ SpringDoc/Swagger

### **Deployment**
- ✅ Single binary standalone
- ✅ Container minimalista
- ✅ Kubernetes ready
- ✅ Cloud native

---

## 🔧 **Troubleshooting**

### **Problema: ClassNotFoundException**
**Solução**: Adicionar RuntimeHints para a classe

### **Problema: Resource not found**
**Solução**: Registrar pattern no hints.resources()

### **Problema: Reflection error**
**Solução**: Registrar tipo no hints.reflection()

### **Problema: Serialization error**
**Solução**: Registrar tipo no hints.serialization()

---

## 📚 **Referências**

- [Spring Boot Native Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/native-image.html)
- [Spring AOT](https://docs.spring.io/spring-framework/reference/core/aot.html)
- [GraalVM Native Image](https://www.graalvm.org/latest/reference-manual/native-image/)
- [GraalVM Reachability Metadata](https://github.com/oracle/graalvm-reachability-metadata)

---

## ✅ **Checklist de Implementação**

- [ ] Adicionar dependências Spring AOT
- [ ] Configurar spring-boot-maven-plugin com AOT
- [ ] Criar NativeRuntimeHints para modelos
- [ ] Criar CassandraRuntimeHints
- [ ] Criar ReactorRuntimeHints
- [ ] Configurar profile native no pom.xml
- [ ] Criar script build-aot-native.sh
- [ ] Criar script test-aot-native.sh
- [ ] Testar build AOT completo
- [ ] Validar todos endpoints
- [ ] Documentar processo e resultados
- [ ] Atualizar README com instruções AOT

---

## 🎯 **Próximos Passos**

1. **Implementar Fase 1**: Configuração base AOT
2. **Implementar Fase 2**: Runtime hints customizados
3. **Implementar Fase 3**: Profile nativo Maven
4. **Implementar Fase 4**: Scripts automatizados
5. **Testar e validar**: Build completo e funcionalidade
6. **Documentar**: Processo e resultados finais

**Tempo estimado**: 2-3 horas de implementação + testes
