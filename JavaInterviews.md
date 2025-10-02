
### **1. Object-Oriented Programming (OOP)**
- **Inheritance & Polymorphism**: 
  - Single vs Multi-inheritance. Example: Override methods to demonstrate runtime polymorphism.
  - Method Overloading (parameter overload), Overriding (`@Override` annotation).
- **Encapsulation**: Use of `final`, `static` in classes; no member variables outside getters/setters.
- **Abstract Classes/Interfaces**:
  - When to use interfaces vs abstract classes. Example: Swing uses `ActionListener` interface for event handling.
- **Design Patterns** (Must understand at least one):
  - **Singleton**: Ensure thread-safety (e.g., using `double-checked locking`). 
    ```java
    public class Singleton {
      private static Singleton instance = null;
      public synchronized static Singleton getInstance() { ... }
    }
    ```
  - **Factory Method**: Use abstract classes/interfaces for product injection.
  - **Observer**: Implement event handling (e.g., GUI listeners).

---

### **2. Data Structures & Algorithms**
- **Basic Structures**:
  - Arrays, Linked Lists, Stacks/QQueues, Trees (Binary Search Tree), Graphs (BFS/DFS).
- **Sorting/Searching**:
  - Quicksort, Mergesort, Kadane’s Algorithm.
- **Algorithm Analysis**: Time/space complexity (e.g., O(n log n) vs O(n²)).
- **Common Pitfalls**:
  - Misunderstanding linked list traversal/depth-first search.

---

### **3. Memory Management**
- **Heap/Stack**: Explain how memory allocation works and differences between `new` (heap), block variables (`this`, local 
references, frames).
- **Garbage Collection**: Discuss generational GC, reference counting (though Java abstracts this).

---

### **4. Exception Handling**
- Try-catch-finally blocks.
- Custom exceptions using `extends Exception` or `Throwable`.
- Propagation (`throws` keyword), catching specific exceptions.

---

### **5. Concurrency**
- **Threads**: Runnable vs `Thread` subclassing, thread pools (Executors).
- **Synchronization**:
  - Methods/matches with locks, `ReentrantLock`, and `CountDownLatch`.
- **Deadlocks & Race Conditions**: Strategies to avoid them.
- **Concurrency Utilities**: Use of `ConcurrentHashMap`, CyclicBarrier.

---

### **6. Design Patterns (Examples)**
- **Builder Pattern**: For complex object creation (e.g., constructing a date/time string).
- **Strategy Pattern**: Encapsulate algorithms in interchangeable classes/interfaces.

---

### **7. API/Web Development Concepts**
- **Servlets/JSP**: Understand MVC architecture, request/response handling.
- **JDBC**: Execute SQL queries and process ResultSets.
- **Struts/MVelocity**: Lightweight frameworks for form processing (historical context).

---

### **8. Syntax & Common Gotchas**
- **String Handling**:
  - Immutable strings (`s1 = "abc"; s2 = s1; s2 += "de";`).
  - `==` vs `.equals()` for object comparison.
- **Type Casting**: Widening (automatic) vs narrowing (explicit `(int)`).
- **Lambda Expressions**: Use with functional interfaces (e.g., `Runnable` or `Comparator`).

---

### **9. API Design & SOLID Principles**
- **SRP/LSP**: Single Responsibility Principle, Dependency Inversion.
- **Interface Segregation**: Prefer small interfaces over monolithic ones.

---

### **10. Algorithms Challenges**
- **Greedy Algorithm** (e.g., coin change problem).
- **Dynamic Programming** (e.g., longest palindromic subsequence).

---

**Preparation Tips**:
1. **Practice coding problems** on data structures, sorting, and DP.
2. **Mock interviews** to simulate timed challenges.
3. **Review System Design** for distributed systems if required by the role.

