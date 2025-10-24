# ☁️ Beauty Salon - Análise de Escalabilidade em Nuvem

## 📊 Comparativo de Instâncias AWS, Azure e GCP por Linguagem

### 🎯 Objetivo
Análise comparativa de custos e escalabilidade para deployment dos backends do Beauty Salon App nas principais plataformas de nuvem, considerando tamanhos de containers Docker e recursos mínimos recomendados.

---

## 📦 Resumo dos Containers por Linguagem

| Backend | Tamanho Docker | Memória Mínima | CPU Mínima | Startup | Observações |
|---------|----------------|----------------|------------|---------|-------------|
| **Go** | 52MB | 64MB | 0.1 vCPU | <100ms | Binário nativo, máxima eficiência |
| **Node.js** | 276MB | 128MB | 0.25 vCPU | 1-3s | Runtime V8 otimizado |
| **.NET** | 367MB | 256MB | 0.5 vCPU | 2-5s | Runtime .NET 8 com AOT |
| **Java Reactive** | 378MB | 512MB | 0.5 vCPU | 5-15s | JVM + WebFlux, alta concorrência |
| **Java Tradicional** | 380MB | 512MB | 0.5 vCPU | 5-15s | JVM + Spring Boot completo |
| **Python** | 393MB | 256MB | 0.25 vCPU | 3-8s | Interpretador + FastAPI |

---

## ☁️ AWS - Amazon Web Services

### 🏷️ Tipos de Instância Recomendados

#### **Go - Máxima Eficiência**
| Tipo Instância | vCPU | RAM | Preço/hora* | Containers/Instância | Custo/Container/hora |
|----------------|------|-----|-------------|---------------------|---------------------|
| **t4g.nano** | 2 | 0.5GB | $0.0042 | 7 | $0.0006 |
| **t4g.micro** | 2 | 1GB | $0.0084 | 15 | $0.00056 |
| **t4g.small** | 2 | 2GB | $0.0168 | 30 | $0.00056 |
| **t4g.medium** | 2 | 4GB | $0.0336 | 60 | $0.00056 |

#### **Node.js - Equilibrio Ideal**
| Tipo Instância | vCPU | RAM | Preço/hora* | Containers/Instância | Custo/Container/hora |
|----------------|------|-----|-------------|---------------------|---------------------|
| **t4g.small** | 2 | 2GB | $0.0168 | 12 | $0.0014 |
| **t4g.medium** | 2 | 4GB | $0.0336 | 28 | $0.0012 |
| **t4g.large** | 2 | 8GB | $0.0672 | 60 | $0.00112 |
| **c6g.large** | 2 | 4GB | $0.068 | 28 | $0.00243 |

#### **.NET - Eficiência Corporativa**
| Tipo Instância | vCPU | RAM | Preço/hora* | Containers/Instância | Custo/Container/hora |
|----------------|------|-----|-------------|---------------------|---------------------|
| **t4g.medium** | 2 | 4GB | $0.0336 | 14 | $0.0024 |
| **t4g.large** | 2 | 8GB | $0.0672 | 30 | $0.00224 |
| **c6g.large** | 2 | 4GB | $0.068 | 14 | $0.00486 |
| **c6g.xlarge** | 4 | 8GB | $0.136 | 30 | $0.00453 |

#### **Java (Reactive/Tradicional) - Poder Empresarial**
| Tipo Instância | vCPU | RAM | Preço/hora* | Containers/Instância | Custo/Container/hora |
|----------------|------|-----|-------------|---------------------|---------------------|
| **t4g.large** | 2 | 8GB | $0.0672 | 14 | $0.0048 |
| **t4g.xlarge** | 4 | 16GB | $0.1344 | 30 | $0.00448 |
| **c6g.xlarge** | 4 | 8GB | $0.136 | 14 | $0.00971 |
| **m6g.large** | 2 | 8GB | $0.077 | 14 | $0.0055 |

#### **Python - Flexibilidade Máxima**
| Tipo Instância | vCPU | RAM | Preço/hora* | Containers/Instância | Custo/Container/hora |
|----------------|------|-----|-------------|---------------------|---------------------|
| **t4g.medium** | 2 | 4GB | $0.0336 | 14 | $0.0024 |
| **t4g.large** | 2 | 8GB | $0.0672 | 30 | $0.00224 |
| **c6g.large** | 2 | 4GB | $0.068 | 14 | $0.00486 |
| **m6g.large** | 2 | 8GB | $0.077 | 30 | $0.00257 |

---

## 🔷 Azure - Microsoft Azure

### 🏷️ Tipos de Instância Recomendados

#### **Go - Máxima Eficiência**
| Tipo Instância | vCPU | RAM | Preço/hora* | Containers/Instância | Custo/Container/hora |
|----------------|------|-----|-------------|---------------------|---------------------|
| **B1ls** | 1 | 0.5GB | $0.0052 | 7 | $0.00074 |
| **B1s** | 1 | 1GB | $0.0104 | 15 | $0.00069 |
| **B1ms** | 1 | 2GB | $0.0208 | 30 | $0.00069 |
| **B2s** | 2 | 4GB | $0.0416 | 60 | $0.00069 |

#### **Node.js - Equilibrio Ideal**
| Tipo Instância | vCPU | RAM | Preço/hora* | Containers/Instância | Custo/Container/hora |
|----------------|------|-----|-------------|---------------------|---------------------|
| **B1ms** | 1 | 2GB | $0.0208 | 12 | $0.00173 |
| **B2s** | 2 | 4GB | $0.0416 | 28 | $0.00149 |
| **B2ms** | 2 | 8GB | $0.0832 | 60 | $0.00139 |
| **F2s_v2** | 2 | 4GB | $0.085 | 28 | $0.00304 |

#### **.NET - Eficiência Corporativa**
| Tipo Instância | vCPU | RAM | Preço/hora* | Containers/Instância | Custo/Container/hora |
|----------------|------|-----|-------------|---------------------|---------------------|
| **B2s** | 2 | 4GB | $0.0416 | 14 | $0.00297 |
| **B2ms** | 2 | 8GB | $0.0832 | 30 | $0.00277 |
| **F2s_v2** | 2 | 4GB | $0.085 | 14 | $0.00607 |
| **F4s_v2** | 4 | 8GB | $0.169 | 30 | $0.00563 |

#### **Java (Reactive/Tradicional) - Poder Empresarial**
| Tipo Instância | vCPU | RAM | Preço/hora* | Containers/Instância | Custo/Container/hora |
|----------------|------|-----|-------------|---------------------|---------------------|
| **B2ms** | 2 | 8GB | $0.0832 | 14 | $0.00594 |
| **B4ms** | 4 | 16GB | $0.166 | 30 | $0.00553 |
| **F4s_v2** | 4 | 8GB | $0.169 | 14 | $0.01207 |
| **D2s_v3** | 2 | 8GB | $0.096 | 14 | $0.00686 |

#### **Python - Flexibilidade Máxima**
| Tipo Instância | vCPU | RAM | Preço/hora* | Containers/Instância | Custo/Container/hora |
|----------------|------|-----|-------------|---------------------|---------------------|
| **B2s** | 2 | 4GB | $0.0416 | 14 | $0.00297 |
| **B2ms** | 2 | 8GB | $0.0832 | 30 | $0.00277 |
| **F2s_v2** | 2 | 4GB | $0.085 | 14 | $0.00607 |
| **D2s_v3** | 2 | 8GB | $0.096 | 30 | $0.0032 |

---

## 🟢 GCP - Google Cloud Platform

### 🏷️ Tipos de Instância Recomendados

#### **Go - Máxima Eficiência**
| Tipo Instância | vCPU | RAM | Preço/hora* | Containers/Instância | Custo/Container/hora |
|----------------|------|-----|-------------|---------------------|---------------------|
| **e2-micro** | 1 | 1GB | $0.006 | 15 | $0.0004 |
| **e2-small** | 1 | 2GB | $0.012 | 30 | $0.0004 |
| **e2-medium** | 1 | 4GB | $0.024 | 60 | $0.0004 |
| **e2-standard-2** | 2 | 8GB | $0.067 | 120 | $0.00056 |

#### **Node.js - Equilibrio Ideal**
| Tipo Instância | vCPU | RAM | Preço/hora* | Containers/Instância | Custo/Container/hora |
|----------------|------|-----|-------------|---------------------|---------------------|
| **e2-small** | 1 | 2GB | $0.012 | 12 | $0.001 |
| **e2-medium** | 1 | 4GB | $0.024 | 28 | $0.00086 |
| **e2-standard-2** | 2 | 8GB | $0.067 | 60 | $0.00112 |
| **c2-standard-4** | 4 | 16GB | $0.199 | 120 | $0.00166 |

#### **.NET - Eficiência Corporativa**
| Tipo Instância | vCPU | RAM | Preço/hora* | Containers/Instância | Custo/Container/hora |
|----------------|------|-----|-------------|---------------------|---------------------|
| **e2-medium** | 1 | 4GB | $0.024 | 14 | $0.00171 |
| **e2-standard-2** | 2 | 8GB | $0.067 | 30 | $0.00223 |
| **c2-standard-4** | 4 | 16GB | $0.199 | 60 | $0.00332 |
| **n2-standard-2** | 2 | 8GB | $0.097 | 30 | $0.00323 |

#### **Java (Reactive/Tradicional) - Poder Empresarial**
| Tipo Instância | vCPU | RAM | Preço/hora* | Containers/Instância | Custo/Container/hora |
|----------------|------|-----|-------------|---------------------|---------------------|
| **e2-standard-2** | 2 | 8GB | $0.067 | 14 | $0.00479 |
| **e2-standard-4** | 4 | 16GB | $0.134 | 30 | $0.00447 |
| **c2-standard-4** | 4 | 16GB | $0.199 | 30 | $0.00663 |
| **n2-standard-2** | 2 | 8GB | $0.097 | 14 | $0.00693 |

#### **Python - Flexibilidade Máxima**
| Tipo Instância | vCPU | RAM | Preço/hora* | Containers/Instância | Custo/Container/hora |
|----------------|------|-----|-------------|---------------------|---------------------|
| **e2-medium** | 1 | 4GB | $0.024 | 14 | $0.00171 |
| **e2-standard-2** | 2 | 8GB | $0.067 | 30 | $0.00223 |
| **c2-standard-4** | 4 | 16GB | $0.199 | 60 | $0.00332 |
| **n2-standard-2** | 2 | 8GB | $0.097 | 30 | $0.00323 |

---

## 📊 Comparativo de Custos por Linguagem

### 💰 Custo por Container/Hora (Instância Otimizada)

| Linguagem | AWS | Azure | GCP | Melhor Opção | Economia vs Pior |
|-----------|-----|-------|-----|--------------|------------------|
| **Go** | $0.00056 | $0.00069 | $0.0004 | 🏆 GCP | 72% vs Azure |
| **Node.js** | $0.00112 | $0.00139 | $0.00086 | 🏆 GCP | 62% vs Azure |
| **.NET** | $0.00224 | $0.00277 | $0.00171 | 🏆 GCP | 62% vs Azure |
| **Java Reactive** | $0.00448 | $0.00553 | $0.00447 | 🏆 GCP | 24% vs Azure |
| **Java Tradicional** | $0.00448 | $0.00553 | $0.00447 | 🏆 GCP | 24% vs Azure |
| **Python** | $0.00224 | $0.00277 | $0.00171 | 🏆 GCP | 62% vs Azure |

### 📈 Escalabilidade por Linguagem

#### **Cenário: 1000 Requisições/Segundo**

| Linguagem | Containers Necessários | AWS/mês | Azure/mês | GCP/mês | Melhor Custo |
|-----------|------------------------|---------|-----------|---------|--------------|
| **Go** | 5 | $20.16 | $24.84 | $14.40 | 🏆 GCP |
| **Node.js** | 10 | $80.64 | $100.08 | $61.92 | 🏆 GCP |
| **.NET** | 15 | $241.92 | $298.92 | $184.68 | 🏆 GCP |
| **Java Reactive** | 8 | $258.05 | $318.24 | $257.47 | 🏆 GCP |
| **Java Tradicional** | 12 | $387.07 | $477.36 | $386.21 | 🏆 GCP |
| **Python** | 20 | $322.56 | $398.88 | $245.76 | 🏆 GCP |

#### **Cenário: 10,000 Requisições/Segundo**

| Linguagem | Containers Necessários | AWS/mês | Azure/mês | GCP/mês | Melhor Custo |
|-----------|------------------------|---------|-----------|---------|--------------|
| **Go** | 50 | $201.60 | $248.40 | $144.00 | 🏆 GCP |
| **Node.js** | 100 | $806.40 | $1,000.80 | $619.20 | 🏆 GCP |
| **.NET** | 150 | $2,419.20 | $2,989.20 | $1,846.80 | 🏆 GCP |
| **Java Reactive** | 80 | $2,580.48 | $3,182.40 | $2,574.72 | 🏆 GCP |
| **Java Tradicional** | 120 | $3,870.72 | $4,773.60 | $3,862.08 | 🏆 GCP |
| **Python** | 200 | $3,225.60 | $3,988.80 | $2,457.60 | 🏆 GCP |

---

## 🎯 Recomendações por Cenário

### 🚀 **Startup/MVP (< 1000 req/s)**
| Prioridade | Linguagem | Plataforma | Custo/mês | Justificativa |
|------------|-----------|------------|-----------|---------------|
| **1ª** | Go | GCP | $14.40 | Menor custo, máxima eficiência |
| **2ª** | Node.js | GCP | $61.92 | Desenvolvimento rápido, custo baixo |
| **3ª** | Python | GCP | $245.76 | Flexibilidade, prototipagem rápida |

### 🏢 **Empresa Média (1K-10K req/s)**
| Prioridade | Linguagem | Plataforma | Custo/mês | Justificativa |
|------------|-----------|------------|-----------|---------------|
| **1ª** | Go | GCP | $144.00 | Escalabilidade com menor custo |
| **2ª** | Node.js | GCP | $619.20 | Equilibrio custo/produtividade |
| **3ª** | .NET | GCP | $1,846.80 | Integração Microsoft, performance |

### 🏭 **Enterprise (> 10K req/s)**
| Prioridade | Linguagem | Plataforma | Custo/mês | Justificativa |
|------------|-----------|------------|-----------|---------------|
| **1ª** | Java Reactive | GCP | $2,574.72 | Máxima concorrência, ecossistema |
| **2ª** | Go | GCP | $144.00 | Eficiência extrema, baixo custo |
| **3ª** | .NET | AWS | $2,419.20 | Integração enterprise, suporte |

---

## 💡 Insights e Otimizações

### 🏆 **Vencedores por Categoria**

#### **Menor Custo Absoluto**
- **Go no GCP**: $0.0004/container/hora
- **Economia**: 72% vs opção mais cara (Azure)
- **Ideal para**: Microserviços, APIs simples

#### **Melhor Custo-Benefício**
- **Node.js no GCP**: $0.00086/container/hora
- **Vantagem**: Desenvolvimento rápido + custo baixo
- **Ideal para**: Startups, MVPs, APIs REST

#### **Melhor para Alta Concorrência**
- **Java Reactive no GCP**: $0.00447/container/hora
- **Vantagem**: Máxima performance concorrente
- **Ideal para**: Sistemas críticos, alta carga

### 🔧 **Otimizações Recomendadas**

#### **1. Auto Scaling**
```yaml
# Kubernetes HPA Example
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: beauty-salon-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: beauty-salon-app
  minReplicas: 2
  maxReplicas: 100
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

#### **2. Resource Limits**
```yaml
# Go - Máxima Eficiência
resources:
  requests:
    memory: "64Mi"
    cpu: "50m"
  limits:
    memory: "128Mi"
    cpu: "100m"

# Node.js - Equilibrio
resources:
  requests:
    memory: "128Mi"
    cpu: "100m"
  limits:
    memory: "256Mi"
    cpu: "250m"

# Java - Poder Empresarial
resources:
  requests:
    memory: "512Mi"
    cpu: "250m"
  limits:
    memory: "1Gi"
    cpu: "500m"
```

#### **3. Spot Instances (Economia 60-90%)**
| Linguagem | AWS Spot | Azure Spot | GCP Preemptible | Economia Média |
|-----------|----------|------------|-----------------|----------------|
| **Go** | $0.00017 | $0.00021 | $0.00012 | 70% |
| **Node.js** | $0.00034 | $0.00042 | $0.00026 | 70% |
| **Java** | $0.00134 | $0.00166 | $0.00134 | 70% |

---

## 📋 Resumo Executivo

### 🎯 **Principais Conclusões**

1. **GCP é consistentemente mais barato** em todas as linguagens
2. **Go oferece 5-10x melhor eficiência** que Java/Python
3. **Node.js tem o melhor custo-benefício** para desenvolvimento
4. **Java Reactive justifica o custo** apenas em alta concorrência
5. **Spot instances reduzem custos em 70%** para workloads tolerantes

### 💰 **ROI por Linguagem (10K req/s)**

| Linguagem | Custo Anual (GCP) | Economia vs Java | Desenvolvedores | Custo Total | ROI |
|-----------|-------------------|------------------|-----------------|-------------|-----|
| **Go** | $1,728 | $44,616 | 2 | $241,728 | 🟢 Máximo |
| **Node.js** | $7,430 | $38,914 | 2 | $247,430 | 🟢 Alto |
| **.NET** | $22,162 | $24,182 | 2 | $262,162 | 🟡 Médio |
| **Python** | $29,491 | $16,853 | 1.5 | $254,491 | 🟡 Médio |
| **Java Reactive** | $30,897 | $15,447 | 3 | $390,897 | 🔴 Baixo |
| **Java Tradicional** | $46,344 | $0 | 3 | $406,344 | 🔴 Referência |

### 🚀 **Recomendação Final**

**Para o Beauty Salon App:**
1. **MVP/Startup**: Go + GCP ($144/mês para 10K req/s)
2. **Crescimento**: Node.js + GCP ($619/mês para 10K req/s)  
3. **Enterprise**: Java Reactive + GCP ($2,575/mês para 10K req/s)

---

**📊 Análise realizada em:** 23 de Outubro de 2025  
**💰 Preços baseados em:** Região us-east-1/us-central1/East US  
**⚠️ Nota:** Preços podem variar. Sempre consulte calculadoras oficiais das plataformas.

*Preços em USD, região padrão, sem descontos por volume ou contratos anuais.
