#!/usr/bin/env python3
"""
Comprehensive Stress Test for Beauty Salon Backends
Alternative Python implementation using locust
"""

import time
import json
import statistics
from datetime import datetime
from typing import Dict, List
import requests
from concurrent.futures import ThreadPoolExecutor, as_completed

# Backend configurations
BACKENDS = {
    "dotnet": {
        "name": ".NET Core",
        "url": "http://localhost:8081/api/customers",
        "port": 8081
    },
    "python": {
        "name": "Python FastAPI",
        "url": "http://localhost:8082/api/customers",
        "port": 8082
    },
    "nodejs": {
        "name": "Node.js Express",
        "url": "http://localhost:8083/api/customers",
        "port": 8083
    },
    "go": {
        "name": "Go Gin",
        "url": "http://localhost:8084/api/v1/customers",
        "port": 8084
    },
    "java-reactive": {
        "name": "Java Reactive",
        "url": "http://localhost:8085/api/customers",
        "port": 8085
    }
}

# Test scenarios
SCENARIOS = [
    {"name": "Light Load", "users": 10, "requests": 100},
    {"name": "Medium Load", "users": 50, "requests": 500},
    {"name": "High Load", "users": 100, "requests": 1000},
    {"name": "Very High Load", "users": 200, "requests": 2000},
    {"name": "Extreme Load", "users": 500, "requests": 5000},
]


class StressTestRunner:
    def __init__(self):
        self.results = {}
        self.timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        
    def check_backend(self, backend_key: str) -> bool:
        """Check if backend is running"""
        backend = BACKENDS[backend_key]
        try:
            response = requests.get(backend["url"], timeout=5)
            return response.status_code == 200
        except:
            return False
    
    def warmup_backend(self, backend_key: str, requests_count: int = 50):
        """Warmup backend with initial requests"""
        backend = BACKENDS[backend_key]
        print(f"  Warming up {backend['name']}...")
        
        for _ in range(requests_count):
            try:
                requests.get(backend["url"], timeout=5)
            except:
                pass
        
        time.sleep(2)
        print(f"  ✓ {backend['name']} warmed up")
    
    def make_request(self, url: str) -> Dict:
        """Make single request and measure time"""
        start_time = time.time()
        error = None
        status_code = 0
        
        try:
            response = requests.get(url, timeout=30)
            status_code = response.status_code
            if status_code != 200:
                error = f"HTTP {status_code}"
        except Exception as e:
            error = str(e)
        
        end_time = time.time()
        latency = (end_time - start_time) * 1000  # Convert to ms
        
        return {
            "latency": latency,
            "error": error,
            "status_code": status_code
        }
    
    def run_scenario(self, backend_key: str, scenario: Dict) -> Dict:
        """Run single test scenario"""
        backend = BACKENDS[backend_key]
        url = backend["url"]
        users = scenario["users"]
        total_requests = scenario["requests"]
        
        print(f"\n  Testing {scenario['name']} ({users} users, {total_requests} requests)...")
        
        results = []
        errors = 0
        start_time = time.time()
        
        # Use ThreadPoolExecutor to simulate concurrent users
        with ThreadPoolExecutor(max_workers=users) as executor:
            futures = [
                executor.submit(self.make_request, url)
                for _ in range(total_requests)
            ]
            
            for future in as_completed(futures):
                result = future.result()
                results.append(result)
                if result["error"]:
                    errors += 1
        
        end_time = time.time()
        duration = end_time - start_time
        
        # Calculate metrics
        latencies = [r["latency"] for r in results if not r["error"]]
        
        if not latencies:
            return {
                "scenario": scenario["name"],
                "users": users,
                "total_requests": total_requests,
                "errors": errors,
                "error": "All requests failed"
            }
        
        latencies.sort()
        
        metrics = {
            "scenario": scenario["name"],
            "users": users,
            "total_requests": total_requests,
            "duration": duration,
            "rps": total_requests / duration,
            "latency_avg": statistics.mean(latencies),
            "latency_median": statistics.median(latencies),
            "latency_p95": latencies[int(len(latencies) * 0.95)],
            "latency_p99": latencies[int(len(latencies) * 0.99)],
            "latency_min": min(latencies),
            "latency_max": max(latencies),
            "errors": errors,
            "error_rate": (errors / total_requests) * 100
        }
        
        print(f"    RPS: {metrics['rps']:.2f}")
        print(f"    Avg Latency: {metrics['latency_avg']:.2f}ms")
        print(f"    p99 Latency: {metrics['latency_p99']:.2f}ms")
        print(f"    Errors: {errors}")
        
        return metrics
    
    def test_backend(self, backend_key: str) -> List[Dict]:
        """Test single backend with all scenarios"""
        backend = BACKENDS[backend_key]
        print(f"\n{'='*60}")
        print(f"Testing: {backend['name']}")
        print(f"{'='*60}")
        
        if not self.check_backend(backend_key):
            print(f"  ✗ {backend['name']} is NOT running!")
            return []
        
        print(f"  ✓ {backend['name']} is running")
        
        # Warmup
        self.warmup_backend(backend_key)
        
        # Run all scenarios
        results = []
        for scenario in SCENARIOS:
            result = self.run_scenario(backend_key, scenario)
            results.append(result)
            time.sleep(3)  # Cool down between scenarios
        
        return results
    
    def generate_report(self, all_results: Dict):
        """Generate Markdown report"""
        report_file = f"stress-test-results/python_report_{self.timestamp}.md"
        
        with open(report_file, 'w') as f:
            f.write("# 🔥 Python Stress Test Report\n\n")
            f.write(f"**Test Date:** {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n")
            f.write(f"**Test Tool:** Python + ThreadPoolExecutor\n\n")
            f.write("---\n\n")
            
            # Results per backend
            for backend_key, results in all_results.items():
                if not results:
                    continue
                
                backend = BACKENDS[backend_key]
                f.write(f"## 🚀 {backend['name']}\n\n")
                f.write("| Scenario | Users | RPS | Avg Latency | p95 | p99 | Max | Errors |\n")
                f.write("|----------|-------|-----|-------------|-----|-----|-----|--------|\n")
                
                for result in results:
                    if "error" in result and result.get("error") != "All requests failed":
                        continue
                    
                    f.write(f"| {result['scenario']} | {result['users']} | "
                           f"{result['rps']:.2f} | {result['latency_avg']:.2f}ms | "
                           f"{result['latency_p95']:.2f}ms | {result['latency_p99']:.2f}ms | "
                           f"{result['latency_max']:.2f}ms | {result['errors']} |\n")
                
                f.write("\n")
            
            f.write("---\n\n")
            f.write("**🏆 Test completed successfully!**\n")
        
        print(f"\n{'='*60}")
        print(f"Report saved to: {report_file}")
        print(f"{'='*60}")
    
    def run_all(self):
        """Run tests for all backends"""
        print("\n🔥 COMPREHENSIVE BACKEND STRESS TEST (Python)\n")
        
        # Create results directory
        import os
        os.makedirs("stress-test-results", exist_ok=True)
        
        # Test all backends
        all_results = {}
        for backend_key in BACKENDS.keys():
            results = self.test_backend(backend_key)
            if results:
                all_results[backend_key] = results
        
        # Generate report
        if all_results:
            self.generate_report(all_results)
        else:
            print("\n✗ No backends were tested!")
        
        print("\n✓ All tests completed!\n")


if __name__ == "__main__":
    runner = StressTestRunner()
    runner.run_all()
