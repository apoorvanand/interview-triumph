class UnsortedPriorityQueue:
    def __init__(self):
        self.data = []
    def is_empty(self):
        return len(self.data) == 0
    def insert(self, key, value):
        self.data.append((key, value))
    def min(self):
        if self.is_empty():
            raise Exception("Priority Queue is empty")
        min_index = 0
        for i in range(1, len(self.data)):
            if self.data[i][0] < self.data[min_index][0]:
                min_index = i
        return self.data[min_index]
    def remove_min(self):
        if self.is_empty():
            raise Exception("Priority Queue is empty")
        min_index = 0
        for i in range(1, len(self.data)):
            if self.data[i][0] < self.data[min_index][0]:
                min_index = i
        return self.data.pop(min_index) # Remove and return the minimum element
class SortedPriorityQueue:
    def __init__(self):
        self.data = []
    def is_empty(self):
        return len(self.data) == 0
    def insert(self, key, value):
        self.data.append((key, value))
        self.data.sort(key=lambda x: x[0]) # Keep the list sorted by key
    def min(self):
        if self.is_empty():
            raise Exception("Priority Queue is empty")
        return self.data[0]
    def remove_min(self):
        if self.is_empty():
            raise Exception("Priority Queue is empty")
        return self.data.pop(0) # Remove and return the minimum element
class HeapPriorityQueue:
    def __init__(self):
        self.data = []
    def is_empty(self):
        return len(self.data) == 0
    def insert(self, key, value):
        self.data.append((key, value))
        self._upheap(len(self.data) - 1)
    def min(self):
        if self.is_empty():
            raise Exception("Priority Queue is empty")
        return self.data[0]
    def remove_min(self):
        if self.is_empty():
            raise Exception("Priority Queue is empty")
        if len(self.data) == 1:
            return self.data.pop()
        min_item = self.data[0]
        self.data[0] = self.data.pop() # Move the last item to the root
        self._downheap(0)
        return min_item
    def _parent(self, index):
        return (index - 1) // 2
    def _left(self, index):
        return 2 * index + 1
    def _right(self, index):
        return 2 * index + 2
    def _has_left(self, index):
        return self._left(index) < len(self.data)
    def _has_right(self, index):
        return self._right(index) < len(self.data)
    def _swap(self, i, j):
        self.data[i], self.data[j] = self.data[j], self.data[i]
    def _upheap(self, index):
        parent = self._parent(index)
        if index > 0 and self.data[index][0] < self.data[parent][0]:
            self._swap(index, parent)
            self._upheap(parent)
    def _downheap(self, index):
        if self._has_left(index):
            left = self._left(index)
            small_child = left
            if self._has_right(index):
                right = self._right(index)
                if self.data[right][0] < self.data[left][0]:
                    small_child = right
            if self.data[small_child][0] < self.data[index][0]:
                self._swap(index, small_child)
                self._downheap(small_child)
if __name__ == "__main__":
    pq = HeapPriorityQueue()
    pq.insert(5, "Task 5")
    pq.insert(3, "Task 3")
    pq.insert(8, "Task 8")
    pq.insert(1, "Task 1")
    print("Minimum:", pq.min()) # Should be (1, "Task 1")
    while not pq.is_empty():
        print("Removed:", pq.remove_min()) # Should remove in order of priority
    # Output:
    # Minimum: (1, 'Task 1')
    # Removed: (1, 'Task 1')
    # Removed: (3, 'Task 3')
    # Removed: (5, 'Task 5')
    # Removed: (8, 'Task 8')    
    