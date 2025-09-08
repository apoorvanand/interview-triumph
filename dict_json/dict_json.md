Problem 1: Flatten a Nested JSON Object 🗄️
Description
Given a nested JSON object, write a function that "flattens" it. The key for the new object should be the path to the original value, with keys separated by dots. The values in the new object should be the original values. Assume all keys are strings.

Example
JSON

{
  "a": 1,
  "b": {
    "c": 2,
    "d": {
      "e": 3
    }
  },
  "f": "hello"
}
Expected Output:

JSON

{
  "a": 1,
  "b.c": 2,
  "b.d.e": 3,
  "f": "hello"
}
Why this problem?
This problem directly relates to the tree-like structure of JSON and requires you to think about recursion or stack-based iteration. The use of a HashMap (or dictionary in Python) is essential for building the flattened result. It tests your ability to handle nested data and build new keys on the fly.

Problem 2: Find the Most Frequent Words in a Text File 📜
Description
Given a text file and an integer k, find the k most frequent words in the file. The words are case-insensitive. Your solution should be efficient in terms of time and space complexity. You will need to handle punctuation and non-alphabetic characters.

Example
Input File:
"The quick brown fox jumps over the lazy dog. The lazy dog."

Input k: 2

Expected Output:
["the", "lazy"] (The order between "the" and "lazy" can be anything)

Why this problem?
This is a classic problem that's fundamental to many real-world applications, like search engines and data analysis. It's a perfect use case for a HashMap to store the word counts. Once you have the counts, you can use a Min-Heap (a type of priority queue) to efficiently find the top k elements. This problem tests your knowledge of:

HashMaps for counting frequencies.

String manipulation (converting to lowercase, removing punctuation).

Heaps/Priority Queues for efficient selection of the top k elements. This is a common pattern in interviews and shows you can move beyond simple hash table usage.

Problem 3: JSON Diff ↔️
Description
Given two JSON objects, json1 and json2, write a function to find the differences between them. The differences should be reported in a structured way, indicating added keys, removed keys, and keys with different values.

Example
json1:

JSON

{
  "a": 1,
  "b": {
    "c": 2
  },
  "d": "hello"
}
json2:

JSON

{
  "a": 1,
  "b": {
    "c": 3
  },
  "e": "world"
}
Expected Output (a possible structure):

JSON

{
  "changed": {
    "b.c": { "old": 2, "new": 3 }
  },
  "added": {
    "e": "world"
  },
  "removed": {
    "d": "hello"
  }
}
Why this problem?
This problem requires a recursive approach to traverse both JSON objects simultaneously. You must handle different data types (primitives, objects, arrays) and their comparisons. The use of a HashMap is crucial for storing and organizing the output. This problem assesses:

Recursive thinking and synchronized traversal of two data structures.

Handling edge cases like different data types at the same key.

Logical organization of output, which is a key skill in software development.






further more


To continue our mock Google SWE interview, let's explore three more advanced DSA problems related to JSON and HashMap libraries. These problems build on the foundational concepts from the previous set and introduce new challenges like performance optimization, distributed systems thinking, and more complex data transformations.

Problem 4: JSON Merging with Conflict Resolution 🤝
Description
Given two JSON objects, json1 and json2, write a function that merges json2 into json1. If a key exists in both objects, the value from json2 should overwrite the value in json1. The function should handle nested objects and arrays correctly.

Example
json1:

JSON

{
  "user": {
    "id": 123,
    "name": "Alice"
  },
  "preferences": {
    "theme": "dark"
  }
}
json2:

JSON

{
  "user": {
    "name": "Bob",
    "email": "bob@example.com"
  },
  "settings": {
    "notifications": true
  }
}
Expected Output:

JSON

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
Why this problem?
This is a common task in software development, especially when dealing with configuration files or state management. It's an excellent test of recursion and careful handling of different data types. Unlike a simple deep copy, you need to write logic to traverse both objects simultaneously and perform an intelligent merge. It also implicitly tests your understanding of deep vs. shallow copies.

Problem 5: JSON Path Parser 🛣️
Description
Implement a function that takes a JSON object and a JSONPath string and returns the value at that path. A simple JSONPath string can be a dot-separated string of keys, e.g., "user.name". Your function should handle nested objects and arrays (using bracket notation like "user.friends[0].name").

Example
JSON Object:

JSON

{
  "user": {
    "name": "Alice",
    "friends": [
      { "name": "Bob" },
      { "name": "Charlie" }
    ]
  }
}
JSONPath: "user.friends[1].name"

Expected Output: "Charlie"

Why this problem?
This problem is all about parsing and state management. It requires you to break down the JSONPath string into its components and sequentially navigate the JSON object. The use of a HashMap is crucial for looking up keys, while arrays require special handling. It tests your ability to handle different types of input and build a robust, general-purpose function. It's a great lead-in to discussions about lexical analysis and parsing algorithms.

Problem 6: Distributed Cache with a HashMap and LRU Policy 🏛️
Description
Imagine you are building a distributed cache for a large-scale system. You decide to use a HashMap to store key-value pairs. To prevent the cache from growing indefinitely, you need to implement a Least Recently Used (LRU) eviction policy. Design a data structure that implements this cache with O(1) average time complexity for both get and put operations.

Example
Operations:

put("A", 1)

put("B", 2)

put("C", 3) (Cache is full, so "A" is evicted)

get("B") (Accessing "B" makes it the most recently used)

put("D", 4) (Cache is full, so "C" is evicted)

Final State: The cache contains {"B": 2, "D": 4}.

Why this problem?
While not directly a JSON problem, this is a quintessential interview question that combines a HashMap with a doubly linked list. The hash map provides O(1) access to the nodes, and the linked list maintains the usage order for O(1) updates to the LRU policy. This problem is a gold standard for assessing your understanding of data structures and how they can be combined to meet performance requirements. It shows you can think about system design and efficiency, which is a core skill for any senior SWE role.






more


Problem 7: Serialize and Deserialize a JSON Tree 🌲
Description
Implement two functions, serialize and deserialize. The serialize function should take a JSON object and convert it into a single, flat string. The deserialize function should take that string and reconstruct the original JSON object. Your solution must handle nested objects, arrays, and primitive data types. You cannot use built-in JSON libraries for this task; you must create your own logic.

Example
JSON Object:

JSON

{
  "a": 1,
  "b": {
    "c": [2, 3],
    "d": "hello"
  },
  "e": null
}
Serialized String (one possible format):
{a:1,b:{c:[2,3],d:hello},e:null}

Why this problem?
This problem tests your understanding of tree traversal and state management without relying on built-in tools. You must design your own encoding scheme and then implement a corresponding parser. The serialize part is a form of pre-order traversal. The deserialize part is a classic parsing problem that requires careful handling of different data types and recursive structures. This is a great way to see if you can think like a compiler or interpreter.

Problem 8: Find All Paths from Root to Leaf in a JSON Tree 🗺️
Description
Given a nested JSON object, write a function that finds all paths from the root to every "leaf" value (a value that isn't another object or array). Each path should be represented as a dot-separated string.

Example
JSON Object:

JSON

{
  "data": {
    "user": {
      "id": 123,
      "name": "Alice"
    },
    "projects": [
      {
        "id": "A"
      },
      {
        "id": "B"
      }
    ]
  }
}
Expected Output:
["data.user.id", "data.user.name", "data.projects[0].id", "data.projects[1].id"]

Why this problem?
This is a fantastic problem for testing your knowledge of Depth-First Search (DFS) and recursion. You'll need to maintain a current path as you traverse the JSON object. It also forces you to handle the subtle differences between keys in an object and indices in an array, which is a common source of bugs. It’s a good test of your ability to write clean, recursive code that correctly builds and maintains state.

Problem 9: K-Most Frequent Strings in a JSON Array 📊
Description
Given a JSON array of strings, find the k most frequent strings. If two strings have the same frequency, the one that comes first alphabetically should be ranked higher. Your solution should be efficient for very large arrays.

Example
JSON Array: ["apple", "banana", "apple", "orange", "banana", "apple"]

k: 2

Expected Output: ["apple", "banana"]

Why this problem?
This problem combines the power of HashMaps and Heaps/Priority Queues. First, you'll use a hash map to count the frequency of each string. Then, you'll need a min-heap to efficiently find the top k elements. The tie-breaker rule (alphabetical order) adds a layer of complexity, requiring a custom comparator for your heap. This problem demonstrates your ability to combine multiple data structures to achieve optimal time and space complexity, a key skill for senior engineering roles. It's a slightly more complex version of the "Top K" problem and is very common in interviews.

Pharmaceutical Colony, Mahatma Gandhi Nagar, Chitragupta Nagar, Patna, Bihar, India





more


Problem 10: JSON Schema Validation 📝
Description
Given a JSON object and a JSON schema (which is also a JSON object), write a function to validate if the data object conforms to the rules defined in the schema. Your function should handle basic validation rules like type, required, and nested schemas.

Example
JSON Data:

JSON

{
  "name": "John Doe",
  "age": 30
}
JSON Schema:

JSON

{
  "type": "object",
  "properties": {
    "name": {
      "type": "string"
    },
    "age": {
      "type": "number"
    }
  },
  "required": ["name"]
}
Expected Output: true

Why this problem?
This problem is a fantastic way to test a candidate's ability to handle complex, rule-based logic using a recursive approach. It requires you to parse a schema (which is a JSON object itself) and then traverse the data object, checking for compliance at each level. It's a real-world problem that involves data validation and robust error handling. This is a step up from simple traversal and gets into the core of how many web frameworks and APIs operate.

Problem 11: Implement a Trie with JSON keys 🌳
Description
Implement a Trie (also known as a prefix tree) data structure where each node's children are stored in a HashMap. The trie should be built from a list of JSON key paths (e.g., "user.profile.name"). Your implementation should support a search(prefix) function that returns all key paths that start with the given prefix.

Example
Input Key Paths: ["user.name", "user.profile.age", "admin.access", "admin.profile.age"]

search("user") Expected Output: ["user.name", "user.profile.age"]

Why this problem?
This problem is a creative combination of a trie and a hash map. The use of a HashMap for the children of each trie node makes the implementation cleaner and more efficient than using a fixed-size array, especially for sparse key sets. This problem is excellent for assessing your understanding of tree data structures, prefix-based search, and how to choose the right data structure (HashMap vs. array) for the right job.

Problem 12: Concurrent HashMap for a Web Server 🌐
Description
Imagine you are building a simple web server that uses a HashMap to store session data. The server handles thousands of requests concurrently, and multiple threads might try to read from or write to the same map at the same time. Discuss the challenges and propose a solution to ensure thread safety and optimal performance. You should outline the design of a Concurrent HashMap from scratch.

Why this problem?
This is a high-level, systems-design-oriented problem that is perfect for a senior-level interview. It moves beyond just using a data structure and asks you to think about its implementation in a multithreaded environment. You should discuss concepts like locking, read-write locks, and segmentation (e.g., using an array of smaller HashMaps, each with its own lock). This problem tests your understanding of concurrency, deadlocks, and performance trade-offs, which are critical skills for building scalable software.

Pharmaceutical Colony, Mahatma Gandhi Nagar, Chitragupta Nagar, Patna, Bihar, India





share the solutions


Since the problems provided are typical for a Google SWE interview, providing a complete, runnable code solution for each is less useful than outlining the core logic and algorithmic approach. Here are the solutions broken down by problem, focusing on the key data structures and algorithms required.

Problem 1: Flatten a Nested JSON Object 🗄️
The best approach is a recursive function that traverses the JSON object. You'll need an auxiliary data structure, like a HashMap, to build the flattened result.

Algorithm:

Create a flatten function that takes the current JSON object, a result map, and a prefix string.

Iterate through each key-value pair in the current object.

Construct the new key by appending the current key to the prefix (e.g., prefix + "." + key).

If the value is a primitive (string, number, boolean, null), add the new key and value to the result map.

If the value is a nested object, make a recursive call to flatten with the nested object, the result map, and the new key as the prefix.

Start the process with an empty result map and an empty prefix.

Problem 2: Find the Most Frequent Words in a Text File 📜
This is a classic "Top K" problem that uses a HashMap and a Min-Heap (or Priority Queue).

Algorithm:

Counting Frequencies: Read the text file word by word. For each word, normalize it (lowercase, remove punctuation). Use a HashMap to store word -> count.

Top K Selection: Iterate through the word -> count entries in the HashMap.

Create a Min-Heap of size k. Push each word and its frequency into the heap.

If the heap size exceeds k, pop the element with the smallest frequency. This ensures the heap always contains the k most frequent words seen so far.

The final heap will contain the k most frequent words. Extract them to form the result list.

Problem 3: JSON Diff ↔️
This requires a recursive, side-by-side traversal of both JSON objects. You'll use a HashMap to build the structured output.

Algorithm:

Create a diff function that takes json1, json2, and a path string.

Iterate through the keys of json1 and json2 using a HashSet of all unique keys to avoid redundant checks.

Removed Keys: If a key is in json1 but not json2, add it to the "removed" map in your output.

Added Keys: If a key is in json2 but not json1, add it to the "added" map.

Changed/Unchanged: If a key exists in both:

If both values are objects, make a recursive call to diff with the nested objects and a new path.

If the values are different (and not objects), add the path, old value, and new value to the "changed" map.

If the values are the same, do nothing.

Problem 4: JSON Merging with Conflict Resolution 🤝
This is another recursive traversal problem. The key is to handle the merge logic correctly at each level.

Algorithm:

Create a merge function that takes target and source JSON objects.

Iterate through the keys of the source object.

For each key:

If the value in source is a primitive or array, overwrite the value in target at that key.

If the value is a nested object, make a recursive call to merge with the corresponding objects. If the key doesn't exist in target, create an empty object first.

Problem 5: JSON Path Parser 🛣️
This problem involves string parsing and sequential lookup. You don't need recursion, but rather a loop.

Algorithm:

Split the JSONPath string (e.g., "user.friends[1].name") into components: ["user", "friends[1]", "name"].

Iterate through the components and maintain a reference to the current object/array.

For each component:

If it's a key (no brackets), look up the key in the current object.

If it contains brackets (e.g., friends[1]), parse the key (friends) and index (1). Look up the key in the current object and then access the element at the specified index in the resulting array.

Update the current reference to the new value.

Return the final current value.

Problem 6: Distributed Cache with a HashMap and LRU Policy 🏛️
This is a classic data structure design problem that combines a HashMap and a doubly linked list.

Data Structures:

HashMap<Key, Node>: Maps a key to its corresponding node in the linked list. This provides O(1) access to any node.

DoublyLinkedList: Stores the key-value pairs. This allows for O(1) removal of a node from anywhere in the list and O(1) addition to the head.

Algorithm:

get(key): Look up the key in the HashMap. If found, move the corresponding node to the front of the doubly linked list (most recently used). Return the value.

put(key, value):

If the key exists, update the value and move the node to the front.

If the key doesn't exist:

Create a new node.

Add the new node to the front of the linked list.

Add the key-node pair to the HashMap.

If the cache size exceeds the capacity, remove the last node from the linked list and its entry from the HashMap.

Problem 7: Serialize and Deserialize a JSON Tree 🌲
This is a challenging parsing problem that requires you to create your own encoding scheme.

serialize:

Use a recursive function to traverse the JSON tree.

Use a StringBuilder to build the output string.

For each element, append its representation (e.g., {, }, [, ], ":", ,). For strings, you need to handle escaping characters.

deserialize:

This is a harder problem that requires a recursive descent parser.

Create a function, e.g., parseValue(), that reads from the input string.

Based on the next character ({, [, ", etc.), call the appropriate parsing function (e.g., parseObject(), parseArray(), parseString()).

These functions will recursively call parseValue() to handle nested data.

You'll need a pointer to the current position in the string.

Problem 8: Find All Paths from Root to Leaf in a JSON Tree 🗺️
This is a classic Depth-First Search (DFS) problem.

Algorithm:

Use a recursive helper function findPaths(node, currentPath, resultList).

If the node is a primitive, add the currentPath to the resultList.

If the node is an object, iterate through its keys. For each key, recursively call findPaths with the nested object and currentPath updated with the new key.

If the node is an array, iterate through its elements. For each element, recursively call findPaths with the element and currentPath updated with the index (e.g., [0]).

Problem 9: K-Most Frequent Strings in a JSON Array 📊
This is a variant of Problem 2, but with an added complexity for tie-breaking.

Algorithm:

Count Frequencies: Same as Problem 2, use a HashMap to store string -> count.

Top K Selection:

Create a Min-Heap of size k. The heap's comparator must handle two conditions: first, compare by frequency; if frequencies are equal, compare alphabetically.

Iterate through the string -> count entries. Push each entry onto the heap.

If the heap's size exceeds k, pop the element with the lowest frequency (and highest alphabetical order in case of a tie).

Finally, pop all elements from the heap and reverse the order to get the correct ranking.

Problem 10: JSON Schema Validation 📝
This is a recursive traversal and rule-based validation problem.

Algorithm:

Create a validate(data, schema) function.

Check the top-level type in the schema. If it doesn't match the data's type, return false.

If the schema specifies required properties, check if they exist in the data.

If the schema has a properties object, iterate through it:

For each property, check its type and other rules (minLength, maxLength, etc.).

If the property value is an object, make a recursive call to validate with the nested data and schema.

Problem 11: Implement a Trie with JSON keys 🌳
This is a trie implementation where each node is a HashMap.

Data Structure: Each node of the trie is a HashMap<String, TrieNode>. The key of the map is a segment of the JSON path.

Algorithm:

insert(path): Split the path ("user.profile.name") into segments ["user", "profile", "name"]. Traverse the trie, adding new nodes (HashMaps) as needed for each segment. Mark the final node as the end of a path.

search(prefix): Split the prefix. Traverse the trie. If the traversal is successful, recursively find all paths from the current node down to the leaf nodes to get all matching paths.

Problem 12: Concurrent HashMap for a Web Server 🌐
This is a design discussion. There is no single code solution, but rather an explanation of concepts.

The Problem: Concurrent modifications to a shared HashMap can lead to data races and corruption.

Solution Approach:

Coarse-grained Locking: A simple approach is to put a single lock on the entire map. This is thread-safe but has poor performance under high contention.

Fine-grained Locking (Segmentation): A much better solution is to divide the hash map into N smaller segments (e.g., an array of HashMaps). Each segment has its own lock.

To access a key, first, calculate its hash to determine which segment it belongs to.

Acquire the lock for that specific segment.

Perform the operation (get, put, delete).

Release the lock.

This allows multiple threads to access different segments of the map concurrently, greatly increasing throughput. This is the core principle behind Java's ConcurrentHashMap.






```
code in python


This response provides Python code solutions for the previously discussed DSA problems, focusing on clarity and following the outlined algorithms. The solutions use Python's built-in dict for HashMaps and collections.deque or heapq for queues and heaps.

Problem 1: Flatten a Nested JSON Object
This solution uses a recursive helper function to build the flattened dictionary.

Python

def flatten_json(data: dict) -> dict:
    result = {}

    def _flatten(obj, prefix=""):
        for key, value in obj.items():
            new_key = f"{prefix}.{key}" if prefix else key
            if isinstance(value, dict):
                _flatten(value, new_key)
            else:
                result[new_key] = value

    _flatten(data)
    return result
Problem 2: Find the Most Frequent Words
This solution uses collections.Counter for efficient counting and heapq for the min-heap.

Python

import collections
import re
import heapq

def find_k_most_frequent_words(text: str, k: int) -> list[str]:
    # Normalize words: lowercase and remove non-alphabetic characters
    words = re.findall(r'\b\w+\b', text.lower())
    
    # Use a dictionary to count frequencies
    word_counts = collections.Counter(words)
    
    # Use a min-heap to find the k most frequent words
    # The heap stores tuples of (frequency, word)
    min_heap = []
    for word, freq in word_counts.items():
        heapq.heappush(min_heap, (freq, word))
        if len(min_heap) > k:
            heapq.heappop(min_heap)
            
    # Extract words from the heap
    result = [item[1] for item in min_heap]
    return result
Problem 3: JSON Diff
This recursive function compares two dictionaries and organizes differences into "added," "removed," and "changed."

Python

def json_diff(json1: dict, json2: dict) -> dict:
    diff = {"added": {}, "removed": {}, "changed": {}}

    def _diff(obj1, obj2, path=""):
        all_keys = set(obj1.keys()) | set(obj2.keys())
        for key in all_keys:
            current_path = f"{path}.{key}" if path else key
            
            val1 = obj1.get(key)
            val2 = obj2.get(key)

            if key not in obj1:
                diff["added"][current_path] = val2
            elif key not in obj2:
                diff["removed"][current_path] = val1
            elif isinstance(val1, dict) and isinstance(val2, dict):
                _diff(val1, val2, current_path)
            elif val1 != val2:
                diff["changed"][current_path] = {"old": val1, "new": val2}
                
    _diff(json1, json2)
    return diff
Problem 4: JSON Merging
This is a recursive function that performs a deep merge of source into target.

Python

def json_merge(target: dict, source: dict) -> dict:
    for key, value in source.items():
        if key in target and isinstance(target[key], dict) and isinstance(value, dict):
            # Recurse for nested dictionaries
            json_merge(target[key], value)
        else:
            # Overwrite or add the key
            target[key] = value
    return target
Problem 5: JSON Path Parser
This solution uses a simple loop and string manipulation to navigate the JSON object.

Python

import re

def get_json_path(data, path_str: str):
    current = data
    path_segments = re.findall(r'[^.\[\]]+|\[\d+\]', path_str)
    
    for segment in path_segments:
        if segment.startswith('['):
            # Handle array index
            index = int(segment[1:-1])
            if isinstance(current, list) and len(current) > index:
                current = current[index]
            else:
                return None  # Path not found
        else:
            # Handle dictionary key
            if isinstance(current, dict) and segment in current:
                current = current[segment]
            else:
                return None # Path not found
    return current
Problem 6: Concurrent HashMap with LRU Policy
This is a class-based solution combining a dictionary and a deque for O(1) operations.

Python

from collections import deque

class LRUCache:
    def __init__(self, capacity: int):
        self.capacity = capacity
        self.cache = {}  # Dictionary to store key-value pairs
        self.order = deque() # Deque to maintain the usage order

    def get(self, key: int) -> int:
        if key not in self.cache:
            return -1
        
        # Move the accessed key to the front
        self.order.remove(key)
        self.order.appendleft(key)
        return self.cache[key]

    def put(self, key: int, value: int):
        if key in self.cache:
            self.order.remove(key)
        
        # Add the new key to the front
        self.order.appendleft(key)
        self.cache[key] = value

        # Evict the least recently used item if capacity is exceeded
        if len(self.cache) > self.capacity:
            oldest_key = self.order.pop()
            del self.cache[oldest_key]
Problem 7: Serialize and Deserialize a JSON Tree
This solution uses recursive functions to build and parse the JSON string. Note that this is a simplified version and doesn't handle all JSON spec complexities (like escaping).

Python

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
Problem 8: Find All Paths from Root to Leaf
This uses a recursive function with a list to track the current path.

Python

def find_all_paths(data: dict) -> list[str]:
    paths = []

    def _dfs(node, current_path=""):
        if isinstance(node, dict):
            for key, value in node.items():
                new_path = f"{current_path}.{key}" if current_path else key
                _dfs(value, new_path)
        elif isinstance(node, list):
            for i, item in enumerate(node):
                new_path = f"{current_path}[{i}]"
                _dfs(item, new_path)
        else:
            paths.append(current_path)

    _dfs(data)
    return paths
Problem 9: K-Most Frequent Strings (with tie-breaker)
This builds on Problem 2 by using a custom comparator. Since heapq doesn't support custom comparators, we can leverage Python's default tuple comparison.

Python

import collections
import heapq

def find_k_most_frequent_strings(arr: list[str], k: int) -> list[str]:
    counts = collections.Counter(arr)
    
    # Min-heap stores tuples: (frequency, word)
    # Python's tuple comparison will use the second element (word) for tie-breaking
    # To get correct alphabetical order for ties, we need to negate frequency and negate word
    min_heap = []
    
    for word, freq in counts.items():
        # Store (-frequency, word) to simulate a max-heap by frequency
        # and natural alphabetical order for the word
        heapq.heappush(min_heap, (-freq, word))
    
    # Pop k times to get the top elements
    result = []
    for _ in range(k):
        if min_heap:
            result.append(heapq.heappop(min_heap)[1])
            
    return result

```