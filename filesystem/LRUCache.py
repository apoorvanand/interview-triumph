from collections import deque

class LRUCache:
    def __init__(self, capacity:int):
        self.capacity= capacity
        self.cache={}
        self.order= deque() # Key order tracking

    def get(self, key:int) -> int:
        if key not in self.cache:
            return -1
        # Move the accessed key to the end (most recently used)
        self.order.remove(key)
        self.order.append(key)
        return self.cache[key]
    def put(self, key: int, value: int):
        if key in self.cache:
            self.order.remove(key)
        
        self.order.appendleft(key)
        self.cache[key] = value

        if len(self.cache) > self.capacity:
            oldest_key = self.order.pop()
            del self.cache[oldest_key]