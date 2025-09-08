'''
Implement HashMap
'''
class HashMap:
    def __init__(self, capacity=1000):
        self.capacity = capacity   # number of buckets
        self.buckets = [[] for _ in range(capacity)]

    def _hash(self, key):
        """Hash function to map keys to bucket index"""
        return hash(key) % self.capacity

    def put(self, key, value):
        """Insert or update key-value pair"""
        idx = self._hash(key)
        bucket = self.buckets[idx]

        for i, (k, v) in enumerate(bucket):
            if k == key:
                bucket[i] = (key, value)  # update
                return
        bucket.append((key, value))  # insert

    def get(self, key):
        """Retrieve value for given key, else return -1"""
        idx = self._hash(key)
        bucket = self.buckets[idx]

        for k, v in bucket:
            if k == key:
                return v
        return -1

    def remove(self, key):
        """Remove key-value pair if present"""
        idx = self._hash(key)
        bucket = self.buckets[idx]

        for i, (k, v) in enumerate(bucket):
            if k == key:
                del bucket[i]
                return

# Example usage
hm = HashMap()
hm.put("name", "Apoorv")
hm.put("role", "Engineer")
print(hm.get("name"))   # Apoorv
print(hm.get("age"))    # -1 (not found)

hm.put("name", "Anand") # update
print(hm.get("name"))   # Anand

hm.remove("role")
print(hm.get("role"))   # -1
