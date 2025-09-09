To continue our mock Google SWE interview, let's explore three more advanced DSA problems related to JSON and HashMap libraries. These problems build on the foundational concepts from the previous set and introduce new challenges like performance optimization, distributed systems thinking, and more complex data transformations.

## Problem 4: JSON Merging with Conflict Resolution 🤝

### **Description**

Given two JSON objects, `json1` and `json2`, write a function that merges `json2` into `json1`. If a key exists in both objects, the value from `json2` should overwrite the value in `json1`. The function should handle nested objects and arrays correctly.

### **Example**

**`json1`:**

```json
{
  "user": {
    "id": 123,
    "name": "Alice"
  },
  "preferences": {
    "theme": "dark"
  }
}
```

**`json2`:**

```json
{
  "user": {
    "name": "Bob",
    "email": "bob@example.com"
  },
  "settings": {
    "notifications": true
  }
}
```

**Expected Output:**

```json
{
  "user": {
    "id": 123,
    "name": "Bob",
    "email": "bob@example.com"
  },
  "preferences": {
    "theme": "dark"
  },
  "settings": {
    "notifications": true
  }
}
```

### **Why this problem?**

This is a common task in software development, especially when dealing with configuration files or state management. It's an excellent test of **recursion** and careful handling of different data types. Unlike a simple deep copy, you need to write logic to traverse both objects simultaneously and perform an intelligent merge. It also implicitly tests your understanding of **deep vs. shallow copies**.

-----

## Problem 5: JSON Path Parser 🛣️

### **Description**

Implement a function that takes a JSON object and a JSONPath string and returns the value at that path. A simple JSONPath string can be a dot-separated string of keys, e.g., `"user.name"`. Your function should handle nested objects and arrays (using bracket notation like `"user.friends[0].name"`).

### **Example**

**JSON Object:**

```json
{
  "user": {
    "name": "Alice",
    "friends": [
      { "name": "Bob" },
      { "name": "Charlie" }
    ]
  }
}
```

**JSONPath:** `"user.friends[1].name"`

**Expected Output:** `"Charlie"`

### **Why this problem?**

This problem is all about **parsing** and **state management**. It requires you to break down the JSONPath string into its components and sequentially navigate the JSON object. The use of a **HashMap** is crucial for looking up keys, while arrays require special handling. It tests your ability to handle different types of input and build a robust, general-purpose function. It's a great lead-in to discussions about **lexical analysis** and **parsing algorithms**.

-----

## Problem 6: Distributed Cache with a HashMap and LRU Policy 🏛️

### **Description**

Imagine you are building a distributed cache for a large-scale system. You decide to use a **HashMap** to store key-value pairs. To prevent the cache from growing indefinitely, you need to implement a **Least Recently Used (LRU)** eviction policy. Design a data structure that implements this cache with `O(1)` average time complexity for both `get` and `put` operations.

### **Example**

**Operations:**

1.  `put("A", 1)`
2.  `put("B", 2)`
3.  `put("C", 3)` (Cache is full, so "A" is evicted)
4.  `get("B")` (Accessing "B" makes it the most recently used)
5.  `put("D", 4)` (Cache is full, so "C" is evicted)

**Final State:** The cache contains `{"B": 2, "D": 4}`.

### **Why this problem?**

While not directly a JSON problem, this is a quintessential interview question that combines a **HashMap** with a **doubly linked list**. The hash map provides `O(1)` access to the nodes, and the linked list maintains the usage order for `O(1)` updates to the LRU policy. This problem is a gold standard for assessing your understanding of data structures and how they can be combined to meet performance requirements. It shows you can think about system design and efficiency, which is a core skill for any senior SWE role.

``` Python

def json_merge(target: dict, source:dict) -> dict:
  for key, value in source.items():
    if key in target and isinstance(target[key], dict) and isinstance(value, dict):
      # Recurse for the nested dictionaries
      json_merge(target[key], value)
    else:
      # Overwrite or add the value
      target[key] = value
    return target


def get_json_path(data, path_str: str):
  current = data
  path_segments = re.findall(r'[^.\[\]] + |\[\d+\]', path_str)
  for segment in path_segments:
    if segment.startswith('['):
      # Handle array index
      index = int(segment[1:-1])
      if isinstance(current, list) and len(current) > index:
        current = current[index]
      else:
        return None
  return current

# LRU cache
class LRUCache:
  def __init__(self, capacity:int):
    self.capacity = capacity
    self.cache = {}
    self.order = deque()
  
  def get(self, key:int) -> int:
    if key not in self.cache:
      return -1
    # Move the accessed key to the front
    self.order.remove(key)
    self.order.append(left(key))
    return self.cache[key]
  
  def put(self, key:int, value:int):
    if key in self.cache:
      self.order.remove(key)
    # add the new key to the front
    self.order.appendleft(key)
    self.cache[key]= value
    # Evicr the least recently used item if capacity is exceed
    if len(self.cache) > self.capacity:
      oldest_key = self.order.pop()
      del self.cache[oldest_key]

# Serialize and deserialize 
def serialize(data):
    if isinstance(data, dict):
        items = [f'"{key}":{serialize(value)}' for key, value in data.items()]
        return f'{{{",".join(items)}}}'
    elif isinstance(data, list):
        items = [serialize(item) for item in data]
        return f'[{",".join(items)}]'
    elif isinstance(data, str):
        return f'"{data}"'
    elif data is None:
        return 'null'
    else:
        return str(data)

# Deserialization is significantly more complex and would require a full parser implementation.
# The core idea is a recursive descent parser that consumes the string based on character type.


```


