# 🏆 Beauty Salon - Relatório Comparativo V2

**Comparação: Limites de Recursos Restritos vs Sem Restrições**

**Data:** 2025-11-13  
**Testes Realizados:** 2 rodadas completas com 5 backends cada

---

## 📊 RESUMO EXECUTIVO - COMPARAÇÃO DE CONFIGURAÇÕES

### Teste V1: Sem Restrições (768 MiB / 1.0 vCPU)
**Data:** 2025-11-13 22:31:13

### Teste V2: Com Restrições (135 MiB / 0.5 vCPU)
**Data:** 2025-11-13 23:17:27

---

## 🎯 TABELA COMPARATIVA COMPLETA

| Backend | V1 Req/s | V2 Req/s | Δ % | V1 Lat | V2 Lat | Δ % | V1 Mem | V2 Mem | Δ % | Status |
|---------|----------|----------|-----|--------|--------|-----|--------|--------|-----|--------|
| **Go** | 14,138 | 9,001 | **-36%** | 10.66ms | 19.01ms | +79% | 16 MiB | 23 MiB | +44% | ✅✅ |
| **Python** | 15,535 | 6,068 | **-61%** | 6.57ms | 21.06ms | +221% | 76 MiB | 75 MiB | -1% | ✅✅ |
| **Node.js** | 3,518 | 2,722 | **-23%** | 28.89ms | 38.45ms | +33% | 136 MiB | 115 MiB | -15% | ✅✅ |
| **Java Native** | 1,954 | 1,430 | **-27%** | 52.27ms | 71.22ms | +36% | 112 MiB | 92 MiB | -18% | ✅✅ |
| **Java JVM** | 2,814 | 178 | **-94%** | 45.07ms | 568.71ms | +1162% | 275 MiB | 124 MiB | -55% | ✅⚠️ |

---

## 🏆 RANKINGS DETALHADOS

### ⚡ THROUGHPUT (Requests/segundo)

#### Teste V1 (768 MiB):
```
1. 🥇 Python      15,535 req/s  ████████████████████ 100%
2. 🥈 Go          14,138 req/s  ██████████████████   91%
3. 🥉 Node.js      3,518 req/s  ████                 23%
4. 4️⃣  Java JVM     2,814 req/s  ███                  18%
5. 5️⃣  Java Native  1,954 req/s  ██                   13%
```

#### Teste V2 (135 MiB):
```
1. 🥇 Go           9,001 req/s  ████████████████████ 100%
2. 🥈 Python       6,068 req/s  █████████████        67%
3. 🥉 Node.js      2,722 req/s  ██████               30%
4. 4️⃣  Java Native  1,430 req/s  ███                  16%
5. 5️⃣  Java JVM       178 req/s  ▌                     2%
```

**Análise:**
- 🔵 **Go** assumiu a liderança no V2 (mais resiliente a restrições)
- 🐍 **Python** caiu de 1º para 2º (perdeu 61% de performance)
- ☕ **Java JVM** teve queda dramática de 94% (throttling severo)

---

### 🚀 LATÊNCIA MÉDIA (menor é melhor)

#### Teste V1 (768 MiB):
```
1. 🥇 Python       6.57ms   ██
2. 🥈 Go          10.66ms   ███
3. 🥉 Node.js     28.89ms   ████████
4. 4️⃣  Java JVM    45.07ms   ████████████
5. 5️⃣  Java Native 52.27ms   ██████████████
```

#### Teste V2 (135 MiB):
```
1. 🥇 Go          19.01ms   ████
2. 🥈 Python      21.06ms   ████
3. 🥉 Node.js     38.45ms   ████████
4. 4️⃣  Java Native 71.22ms   ██████████████
5. 5️⃣  Java JVM   568.71ms  ████████████████████████████████████████
```

**Análise:**
- 🔵 **Go** manteve melhor latência sob restrição (+79% vs +221% do Python)
- ☕ **Java JVM** teve degradação catastrófica (45ms → 569ms, +1162%)
- 🐍 **Python** triplicou a latência mas ainda competitivo

---

### 💾 USO DE MEMÓRIA

#### Teste V1 (768 MiB limite):
```
Go           16 MiB    █
Python       76 MiB    █████
Java Native 112 MiB    ███████
Node.js     136 MiB    ████████
Java JVM    275 MiB    █████████████████
```

#### Teste V2 (135 MiB limite):
```
Go           23 MiB    █
Python       75 MiB    █████
Java Native  92 MiB    ██████
Node.js     115 MiB    ████████
Java JVM    124 MiB    █████████
```

**Análise:**
- ✅ Todos conseguiram rodar dentro do limite de 135 MiB
- 🔵 **Go** aumentou apenas 7 MiB (44% relativo, mas ainda muito baixo)
- ☕ **Java JVM** reduziu de 275 → 124 MiB (mas com performance terrível)
- 🐍 **Python** manteve uso quase idêntico (76 → 75 MiB)

---

## 📈 ANÁLISE DE IMPACTO DAS RESTRIÇÕES

### 🔵 GO - CAMPEÃO DA RESILIÊNCIA ⭐⭐⭐⭐⭐

**Performance:**
- Throughput: 14,138 → 9,001 req/s (-36%)
- Latência: 10.66ms → 19.01ms (+79%)
- Memória: 16 → 23 MiB (+44%)

**Veredito:** 🏆 **MELHOR ADAPTAÇÃO ÀS RESTRIÇÕES**
- Menor perda de throughput entre os top performers
- Ainda mantém 9k req/s (excelente)
- Uso de memória continua extremamente baixo
- **Ideal para ambientes com recursos limitados**

---

### 🐍 PYTHON - QUEDA SIGNIFICATIVA ⭐⭐⭐⭐

**Performance:**
- Throughput: 15,535 → 6,068 req/s (-61%)
- Latência: 6.57ms → 21.06ms (+221%)
- Memória: 76 → 75 MiB (-1%)

**Veredito:** ⚠️ **SENSÍVEL A RESTRIÇÕES DE CPU**
- Perdeu 61% de throughput (maior queda entre os bons)
- Latência triplicou (mas ainda competitiva)
- Memória estável (excelente)
- Limitação de CPU (0.5 vCPU) impactou muito
- **Precisa de mais CPU para performance máxima**

---

### 🟢 NODE.JS - ESTÁVEL ⭐⭐⭐⭐

**Performance:**
- Throughput: 3,518 → 2,722 req/s (-23%)
- Latência: 28.89ms → 38.45ms (+33%)
- Memória: 136 → 115 MiB (-15%)

**Veredito:** ✅ **BOA ADAPTAÇÃO**
- Menor perda de throughput (apenas 23%)
- Latência aumentou moderadamente
- Reduziu uso de memória (otimização automática)
- **Bom equilíbrio sob restrições**

---

### 🚀 JAVA NATIVE - RESILIENTE ⭐⭐⭐

**Performance:**
- Throughput: 1,954 → 1,430 req/s (-27%)
- Latência: 52.27ms → 71.22ms (+36%)
- Memória: 112 → 92 MiB (-18%)

**Veredito:** ✅ **ADAPTAÇÃO RAZOÁVEL**
- Perda moderada de throughput (27%)
- Latência aumentou 36%
- Reduziu uso de memória
- **Funciona bem em ambientes restritos**

---

### ☕ JAVA JVM - COLAPSO TOTAL ⭐

**Performance:**
- Throughput: 2,814 → 178 req/s (-94%) ❌
- Latência: 45.07ms → 568.71ms (+1162%) ❌
- Memória: 275 → 124 MiB (-55%)
- **Timeouts:** 10 requests timeout

**Veredito:** ❌ **FALHA CRÍTICA SOB RESTRIÇÕES**
- Perdeu 94% de throughput (catastrófico)
- Latência aumentou 12x (inaceitável)
- Teve timeouts (sinal de throttling severo)
- Memória no limite (124/135 MiB = 92%)
- **NÃO RECOMENDADO para 135 MiB / 0.5 vCPU**

---

## 🎯 COMPARAÇÃO: ANTES vs DEPOIS

### Eficiência de Recursos (Req/s por MiB de memória):

| Backend | V1 (768 MiB) | V2 (135 MiB) | Melhoria |
|---------|--------------|--------------|----------|
| **Go** | 883 req/s/MiB | 391 req/s/MiB | Mantém eficiência |
| **Python** | 204 req/s/MiB | 81 req/s/MiB | Queda significativa |
| **Node.js** | 26 req/s/MiB | 24 req/s/MiB | Estável |
| **Java Native** | 17 req/s/MiB | 16 req/s/MiB | Estável |
| **Java JVM** | 10 req/s/MiB | 1.4 req/s/MiB | Colapso |

**Conclusão:** Go mantém a melhor eficiência de recursos mesmo sob restrições.

---

## 💡 RECOMENDAÇÕES FINAIS

### Para Ambientes com Recursos Limitados (135 MiB / 0.5 vCPU):

#### 🥇 1º LUGAR: GO
```
Throughput:  9,001 req/s  ⭐⭐⭐⭐⭐
Latência:    19.01ms     ⭐⭐⭐⭐
Memória:     23 MiB      ⭐⭐⭐⭐⭐
Resiliência: -36%        ⭐⭐⭐⭐⭐
```
**Veredito:** **MELHOR ESCOLHA PARA AMBIENTES RESTRITOS**
- Excelente performance mesmo com restrições
- Uso de memória extremamente baixo
- Menor impacto das limitações
- **Recomendado para Kubernetes, Serverless, Edge Computing**

#### 🥈 2º LUGAR: PYTHON
```
Throughput:  6,068 req/s  ⭐⭐⭐⭐
Latência:    21.06ms     ⭐⭐⭐⭐
Memória:     75 MiB      ⭐⭐⭐⭐
Resiliência: -61%        ⭐⭐⭐
```
**Veredito:** **BOA OPÇÃO, MAS PRECISA DE MAIS CPU**
- Ainda oferece boa performance (6k req/s)
- Uso de memória estável
- Sensível a limitação de CPU
- **Recomendado se puder aumentar CPU para 1.0 vCPU**

#### 🥉 3º LUGAR: NODE.JS
```
Throughput:  2,722 req/s  ⭐⭐⭐
Latência:    38.45ms     ⭐⭐⭐
Memória:     115 MiB     ⭐⭐⭐
Resiliência: -23%        ⭐⭐⭐⭐
```
**Veredito:** **OPÇÃO EQUILIBRADA**
- Performance moderada mas estável
- Boa adaptação às restrições
- **Recomendado para aplicações web tradicionais**

#### 4️⃣ 4º LUGAR: JAVA NATIVE
```
Throughput:  1,430 req/s  ⭐⭐
Latência:    71.22ms     ⭐⭐
Memória:     92 MiB      ⭐⭐⭐
Resiliência: -27%        ⭐⭐⭐⭐
```
**Veredito:** **FUNCIONA, MAS PERFORMANCE BAIXA**
- Throughput limitado
- Latência alta
- **Use apenas se já estiver investido em Java**

#### 5️⃣ 5º LUGAR: JAVA JVM
```
Throughput:  178 req/s    ⭐
Latência:    568.71ms    ⭐
Memória:     124 MiB     ⭐⭐
Resiliência: -94%        ⭐
```
**Veredito:** ❌ **NÃO RECOMENDADO PARA 135 MiB**
- Performance inaceitável (178 req/s)
- Latência catastrófica (569ms)
- Timeouts frequentes
- **Precisa de mínimo 300 MiB / 1.0 vCPU**

---

## 🚀 ESTRATÉGIA RECOMENDADA POR CENÁRIO

### Cenário 1: Kubernetes com Pods Pequenos (135 MiB / 0.5 vCPU)
```
Serviço                    Backend      Motivo
────────────────────────────────────────────────────────
API de Alta Performance    Go           9k req/s, 23 MiB
API de Dados              Python       6k req/s, 75 MiB
API Web Tradicional       Node.js      2.7k req/s, 115 MiB
Workers Background        Go           Eficiência máxima
```

### Cenário 2: Serverless / AWS Lambda (Recursos Limitados)
```
Função                     Backend      Motivo
────────────────────────────────────────────────────────
API Gateway               Go           Startup rápido, baixa memória
Processamento Batch       Python       Boa performance, memória estável
Webhooks                  Node.js      Equilíbrio, fácil manutenção
```

### Cenário 3: Edge Computing (Recursos Muito Limitados)
```
Serviço                    Backend      Motivo
────────────────────────────────────────────────────────
API Edge                  Go           23 MiB, 9k req/s
Cache/Proxy               Go           Eficiência máxima
```

### Cenário 4: Sem Restrições de Recursos (768 MiB / 1.0 vCPU)
```
Serviço                    Backend      Motivo
────────────────────────────────────────────────────────
API Crítica               Python       15.5k req/s, 6.57ms latência
API Secundária            Go           14k req/s, eficiência
Serviços Legacy           Node.js      Ecossistema, produtividade
```

---

## 📊 CONCLUSÕES FINAIS

### 🏆 CAMPEÃO GERAL: GO

**Por quê?**
1. ✅ Melhor performance sob restrições (9k req/s)
2. ✅ Menor uso de memória (23 MiB)
3. ✅ Melhor resiliência (-36% vs -61% do Python)
4. ✅ Latência competitiva (19ms)
5. ✅ Ideal para ambientes modernos (K8s, Serverless, Edge)

### 🥈 VICE-CAMPEÃO: PYTHON

**Por quê?**
1. ✅ Ainda oferece boa performance (6k req/s)
2. ✅ Uso de memória estável (75 MiB)
3. ⚠️ Sensível a limitação de CPU
4. ✅ Campeão absoluto sem restrições (15.5k req/s)
5. 💡 **Recomendação:** Aumentar CPU para 1.0 vCPU

### 🥉 TERCEIRO: NODE.JS

**Por quê?**
1. ✅ Melhor adaptação proporcional (-23%)
2. ✅ Performance estável (2.7k req/s)
3. ✅ Ecossistema maduro
4. ✅ Bom para aplicações web tradicionais

### ⚠️ ATENÇÃO: JAVA JVM

**Não recomendado para 135 MiB / 0.5 vCPU:**
- ❌ Perda de 94% de performance
- ❌ Latência inaceitável (569ms)
- ❌ Timeouts frequentes
- 💡 **Solução:** Aumentar para 300 MiB / 1.0 vCPU ou usar Java Native

---

## 📈 IMPACTO DAS RESTRIÇÕES - RESUMO

| Métrica | Impacto Médio | Mais Resiliente | Menos Resiliente |
|---------|---------------|-----------------|------------------|
| **Throughput** | -48% | Node.js (-23%) | Java JVM (-94%) |
| **Latência** | +270% | Node.js (+33%) | Java JVM (+1162%) |
| **Memória** | -9% | Python (-1%) | Java JVM (-55%) |

**Conclusão:** Restrições de recursos impactam significativamente a performance, especialmente em backends que dependem de JVM tradicional.

---

## 🎯 DECISÃO FINAL

### Para o Beauty Salon App com 135 MiB / 0.5 vCPU:

**Arquitetura Recomendada:**

```
┌─────────────────────────────────────────────────────────────┐
│ Serviço                │ Backend  │ Req/s │ Memória │ CPU  │
├─────────────────────────────────────────────────────────────┤
│ API Agendamentos       │ Go       │ 9,000 │  23 MiB │ 0.5  │
│ API Clientes           │ Go       │ 9,000 │  23 MiB │ 0.5  │
│ API Serviços           │ Python   │ 6,068 │  75 MiB │ 0.5  │
│ Workers Background     │ Go       │   -   │  23 MiB │ 0.5  │
│ Dashboard Admin        │ Node.js  │ 2,722 │ 115 MiB │ 0.5  │
└─────────────────────────────────────────────────────────────┘

Total por Pod: ~260 MiB / 2.5 vCPU
Pods necessários: 5
Custo: Otimizado para cloud
```

**Benefícios:**
- ✅ Máxima performance onde necessário (Go)
- ✅ Boa performance em serviços secundários (Python)
- ✅ Flexibilidade em dashboards (Node.js)
- ✅ Otimização de custos (pods pequenos)
- ✅ Escalabilidade horizontal facilitada

---

**Gerado em:** 2025-11-13  
**Dados dos testes:**
- Teste V1 (768 MiB): `./stress-test-results/20251113_222307/`
- Teste V2 (135 MiB): `./stress-test-results/20251113_230853/`
