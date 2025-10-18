# Virtual Threads + ZGC vs Standard Configuration Performance Comparison

**Test Date:** Sat Aug  9 23:49:09 -03 2025
**Java Version:** openjdk version "24.0.1" 2025-04-15

## Test Configuration

- **Concurrent Users:** 200
- **Requests per User:** 20
- **Total Requests per Test:** 4,000
- **Total Tests:** 3 (Creation, Retrieval, Mixed CRUD)

---

## Standard Configuration (G1GC) Results

### Configuration Details
   📋 Active Profile: test
   🧵 Virtual Threads: DISABLED
   🗑️  GC Type: OpenJDK 64-Bit Server VM
   ⚙️  Java Version: 24.0.1

### Performance Metrics

#### Overall Performance:
   🚀 Average Throughput: 950,61 req/s
   ⏱️  Average Response Time: 78670 ms
   📊 Total Requests: 12000
   ✅ Configuration: Standard


#### Test Results:

**Customer Creation:**
   📊 Creation Results:
      ✅ Success Rate: 100,00%
      🚀 Throughput: 2779,71 req/s
      ⏱️  Avg Response Time: 1262 ms
      ⚡ Min Response Time: 931 ms
      🔥 Max Response Time: 1437 ms
      ⏰ Total Time: 1439 ms

**Customer Retrieval:**
   📊 Retrieval Results:
      ✅ Success Rate: 97,25%
      🚀 Throughput: 23,38 req/s
      ⏱️  Avg Response Time: 160394 ms
      ⚡ Min Response Time: 66612 ms
      🔥 Max Response Time: 166350 ms
      ⏰ Total Time: 166357 ms

**Mixed CRUD:**
   📊 Mixed CRUD Results:
      ✅ Success Rate: 98,80%
      🚀 Throughput: 48,74 req/s
      ⏱️  Avg Response Time: 74354 ms
      ⚡ Min Response Time: 57199 ms
      🔥 Max Response Time: 81049 ms
      ⏰ Total Time: 81079 ms

---

## Virtual Threads + ZGC Results

### Configuration Details
Starting Virtual Threads + ZGC test at Sat Aug  9 23:53:40 -03 2025
   📋 Active Profile: virtual-threads,test
   🧵 Virtual Threads: ENABLED
   🗑️  GC Type: OpenJDK 64-Bit Server VM

### Performance Metrics

#### Overall Performance:
   🚀 Average Throughput: 818,69 req/s
   ⏱️  Average Response Time: 91395 ms
   📊 Total Requests: 12000
   ✅ Configuration: Virtual Threads + ZGC


#### Test Results:

**Customer Creation:**
   📊 Creation Results:
      ✅ Success Rate: 100,00%
      🚀 Throughput: 2399,52 req/s
      ⏱️  Avg Response Time: 1461 ms
      ⚡ Min Response Time: 1058 ms
      🔥 Max Response Time: 1655 ms
      ⏰ Total Time: 1667 ms

**Customer Retrieval:**
   📊 Retrieval Results:
      ✅ Success Rate: 96,38%
      🚀 Throughput: 23,31 req/s
      ⏱️  Avg Response Time: 159968 ms
      ⚡ Min Response Time: 33492 ms
      🔥 Max Response Time: 165389 ms
      ⏰ Total Time: 165394 ms

**Mixed CRUD:**
   📊 Mixed CRUD Results:
      ✅ Success Rate: 99,88%
      🚀 Throughput: 33,24 req/s
      ⏱️  Avg Response Time: 112757 ms
      ⚡ Min Response Time: 98818 ms
      🔥 Max Response Time: 119841 ms
      ⏰ Total Time: 120171 ms

---

## Performance Comparison Summary

### Throughput Comparison

| Configuration | Creation (req/s) | Retrieval (req/s) | Mixed CRUD (req/s) | Overall Avg |
|---------------|------------------|-------------------|-------------------|-------------|
| Standard (G1GC) | - | - | - | Throughput: |
| Virtual Threads + ZGC | - | - | - | Throughput: |

### Performance Improvement

**Virtual Threads + ZGC vs Standard:** N/A% improvement

## Recommendations

Based on the performance comparison results:

### When to use Virtual Threads + ZGC:
- High concurrency applications (1000+ concurrent users)
- I/O intensive workloads
- Applications with many blocking operations
- Low-latency requirements

### When to use Standard Configuration:
- CPU-intensive workloads
- Lower concurrency applications
- Memory-constrained environments
- Production stability requirements (G1GC is more mature)

