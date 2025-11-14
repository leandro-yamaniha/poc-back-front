# Beauty Salon - Stress Test Results

**Data:** 2025-11-13 23:40:59  
**Duração do teste completo:** N/A  
**Diretório:** ./stress-test-results/20251113_233303

## ⚙️ Configuração dos Testes

- **Duração por backend:** 30s
- **Conexões simultâneas:** 100
- **Threads:** 4
- **Endpoint testado:** /api/customers (GET)
- **Intervalo entre testes:** 5s

## 📊 Resumo Executivo

| Backend | Status | Req/s | Latency (avg) | Memória | CPU |
|---------|--------|-------|---------------|---------|-----|
| java-reactive-native | ✅ OK | 4060.07 | 28.97ms | 98.79MiB | 0.69% |
| java-reactive-jvm | ✅ OK | 1688.11 | 89.94ms | 122.7MiB | 85.50% |
| nodejs | ✅ OK | 5391.71 | 19.20ms | 82.05MiB | 0.10% |
| python | ✅ OK | 16629.05 | 6.08ms | 75.43MiB | 0.23% |
| go | ✅ OK | 22456.70 | 4.96ms | 27.21MiB | 0.00% |

## 📈 Resultados Detalhados por Backend


---

### 🔹 java-reactive-native

**Status:** ✅ Teste concluído com sucesso

#### Métricas de Performance
```
Running 30s test @ http://localhost:8085/api/customers
  4 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    28.97ms   25.41ms 279.13ms   77.59%
    Req/Sec     1.02k   238.98     1.93k    73.50%
  Latency Distribution
     50%   15.03ms
     75%   48.66ms
     90%   70.00ms
     99%   84.39ms
  122169 requests in 30.09s, 21.55MB read
Requests/sec:   4060.07
Transfer/sec:    733.51KB
```

#### Recursos Docker
```
NAME                                     CPU %     MEM USAGE / LIMIT     MEM %
beauty-salon-backend-reactive-native     0.69%     98.79MiB / 135MiB     73.18%
beauty-salon-cassandra-reactive-native   0.70%     1.414GiB / 7.752GiB   18.24%
```


---

### 🔹 java-reactive-jvm

**Status:** ✅ Teste concluído com sucesso

#### Métricas de Performance
```
Running 30s test @ http://localhost:8085/api/customers
  4 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    89.94ms  139.22ms   1.10s    93.30%
    Req/Sec   457.21    204.88     0.91k    58.36%
  Latency Distribution
     50%   79.92ms
     75%   95.01ms
     90%  181.99ms
     99%  798.03ms
  50686 requests in 30.03s, 8.94MB read
Requests/sec:   1688.11
Transfer/sec:    305.03KB
```

#### Recursos Docker
```
NAME                                  CPU %     MEM USAGE / LIMIT     MEM %
beauty-salon-backend-reactive-jvm     85.50%    122.7MiB / 135MiB     90.90%
beauty-salon-cassandra-reactive-jvm   1.98%     1.208GiB / 7.752GiB   15.58%
```


---

### 🔹 nodejs

**Status:** ✅ Teste concluído com sucesso

#### Métricas de Performance
```
Running 30s test @ http://localhost:3000/api/customers
  4 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    19.20ms   10.47ms  96.21ms   83.17%
    Req/Sec     1.35k   169.33     1.72k    75.50%
  Latency Distribution
     50%   14.70ms
     75%   21.75ms
     90%   37.80ms
     99%   50.85ms
  161869 requests in 30.02s, 246.53MB read
Requests/sec:   5391.71
Transfer/sec:      8.21MB
```

#### Recursos Docker
```
NAME                            CPU %     MEM USAGE / LIMIT     MEM %
beauty-salon-backend-nodejs     0.10%     82.05MiB / 135MiB     60.78%
beauty-salon-cassandra-nodejs   1.07%     1.125GiB / 7.752GiB   14.52%
```


---

### 🔹 python

**Status:** ✅ Teste concluído com sucesso

#### Métricas de Performance
```
Running 30s test @ http://localhost:8000/api/customers
  4 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     6.08ms    2.63ms 156.35ms   98.64%
    Req/Sec     4.18k   259.31     4.54k    80.50%
  Latency Distribution
     50%    5.84ms
     75%    6.13ms
     90%    6.67ms
     99%    8.98ms
  499672 requests in 30.05s, 74.34MB read
Requests/sec:  16629.05
Transfer/sec:      2.47MB
```

#### Recursos Docker
```
NAME                            CPU %     MEM USAGE / LIMIT     MEM %
beauty-salon-backend-python     0.23%     75.43MiB / 135MiB     55.87%
beauty-salon-cassandra-python   1.09%     1.112GiB / 7.752GiB   14.35%
```


---

### 🔹 go

**Status:** ✅ Teste concluído com sucesso

#### Métricas de Performance
```
Running 30s test @ http://localhost:8080/api/customers
  4 threads and 100 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     4.96ms    3.82ms  66.93ms   90.93%
    Req/Sec     5.64k     0.87k    7.22k    77.33%
  Latency Distribution
     50%    3.93ms
     75%    5.54ms
     90%    8.31ms
     99%   21.79ms
  674222 requests in 30.02s, 490.60MB read
Requests/sec:  22456.70
Transfer/sec:     16.34MB
```

#### Recursos Docker
```
NAME                        CPU %     MEM USAGE / LIMIT     MEM %
beauty-salon-backend-go     0.00%     27.21MiB / 135MiB     20.16%
beauty-salon-cassandra-go   1.08%     1.239GiB / 7.752GiB   15.98%
```


## 💡 Recomendações

### Análise Comparativa

Baseado nos resultados acima:

1. **Melhor Performance (Throughput):** Verifique qual backend teve maior Requests/sec
2. **Menor Latência:** Identifique o backend com menor latência média
3. **Menor Uso de Recursos:** Compare memória e CPU utilizados
4. **Estabilidade:** Verifique backends sem erros nos logs

### Próximos Passos

- [ ] Analisar logs detalhados dos backends com erros
- [ ] Comparar resultados com benchmarks anteriores
- [ ] Identificar gargalos de performance
- [ ] Otimizar configurações dos backends com pior performance
- [ ] Executar testes com maior carga (mais conexões/duração)

---

**Gerado automaticamente por:** `stress-test-all-backends.sh`  
**Documentação:** `backend/scripts/README.md`

