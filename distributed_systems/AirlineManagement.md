design an airline scheduler if multiple airlines need to utilise the runway
low level design and class

AI Response (Ollama qwen2.5-coder:latest):
### Problem Restatement
Design a system to manage the scheduling of runways for multiple airlines. The system should ensure efficient use of the limited runway resources by allocating them based on arrival times, departure times, and priority.

### Assumptions
1. Each airline has a specific set of flights.
2. Flights are represented by their departure and arrival times.
3. There is only one runway available for all airlines.
4. Priority can be determined by various factors such as weather conditions, passenger count, or historical delay patterns.
5. Arrival and departure times are accurate and known in advance.

### Brute Force Approach
A brute force approach could involve checking each possible allocation of runways to flights and selecting the one that minimizes waiting time for all flights. This would involve iterating through all permutations of flight schedules and evaluating their efficiency.

### Optimized Approach (Tradeoff)
To optimize this, we can use a priority queue (min-heap) to always allocate the runway to the flight with the earliest arrival time or highest priority first. This approach balances simplicity with performance.

### Edge Cases
1. Flights that have overlapping arrival times.
2. Flights with zero wait time between arrival and departure.
3. Flights with different priorities.
4. Flights with varying duration.

### APIs/Classes Needed
1. `Flight` class: Represents a single flight with attributes like `flight_id`, `arrival_time`, `departure_time`, and `priority`.
2. `RunwayScheduler` class: Manages the scheduling of flights using a priority queue.
3. `PriorityQueue` class: Custom implementation of a min-heap for managing runway allocations.

### Architecture/Data Model
1. **Flight Class**
   ```python
   class Flight:
       def __init__(self, flight_id, arrival_time, departure_time, priority):
           self.flight_id = flight_id
           self.arrival_time = arrival_time
           self.departure_time = departure_time
           self.priority = priority

       def __lt__(self, other):
           # Compare based on arrival time first, then priority if same arrival time
           return (self.arrival_time, -self.priority) < (other.arrival_time, -other.priority)
   ```

2. **RunwayScheduler Class**
   ```python
   import heapq

   class RunwayScheduler:
       def __init__(self):
           self.heap = []
           self.current_time = 0

       def add_flight(self, flight):
           heapq.heappush(self.heap, (flight.arrival_time, flight))

       def schedule_flights(self):
           while self.heap:
               arrival_time, flight = heapq.heappop(self.heap)
               if arrival_time > self.current_time:
                   # Wait until the runway is free
                   self.current_time = arrival_time
               # Allocate runway and update current time
               print(f"Allocating runway to Flight {flight.flight_id} at {self.current_time}")
               self.current_time = max(self.current_time, flight.departure_time)
   ```

### Dry Run
1. Add flights with different priorities and times.
2. Call `schedule_flights()` to see the allocation process.

### Final Python Code/Solution
```python
class Flight:
    def __init__(self, flight_id, arrival_time, departure_time, priority):
        self.flight_id = flight_id
        self.arrival_time = arrival_time
        self.departure_time = departure_time
        self.priority = priority

    def __lt__(self, other):
        return (self.arrival_time, -self.priority) < (other.arrival_time, -other.priority)

class RunwayScheduler:
    import heapq

    def __init__(self):
        self.heap = []
        self.current_time = 0

    def add_flight(self, flight):
        heapq.heappush(self.heap, (flight.arrival_time, flight))

    def schedule_flights(self):
        while self.heap:
            arrival_time, flight = heapq.heappop(self.heap)
            if arrival_time > self.current_time:
                # Wait until the runway is free
                self.current_time = arrival_time
            # Allocate runway and update current time
            print(f"Allocating runway to Flight {flight.flight_id} at {self.current_time}")
            self.current_time = max(self.current_time, flight.departure_time)

# Example usage:
scheduler = RunwayScheduler()
scheduler.add_flight(Flight("F1", 0, 30, 2))
scheduler.add_flight(Flight("F2", 5, 25, 1))
scheduler.add_flight(Flight("F3", 10, 40, 3))

scheduler.schedule_flights()
```

### Potential Followup Questions and Python Code Modifications
1. **Priority Queues**: How can we handle multiple runways?
   - Use a list of heaps, one for each runway.
2. **Dynamic Flights**: What if flights are added during the scheduling process?
   - Implement a method to dynamically add flights to the scheduler.

### Example Modification (Multiple Runways)
```python
class MultiRunwayScheduler:
    def __init__(self, num_runways):
        self.heaps = [[] for _ in range(num_runways)]
        self.current_times = [0] * num_runways

    def add_flight(self, flight):
        heapq.heappush(self.heaps[flight.arrival_time % len(self.heaps)], (flight.arrival_time, flight))

    def schedule_flights(self):
        while any(self.heaps):
            for i in range(len(self.heaps)):
                if self.heaps[i]:
                    arrival_time, flight = heapq.heappop(self.heaps[i])
                    if arrival_time > self.current_times[i]:
                        # Wait until the runway is free
                        self.current_times[i] = arrival_time
                    # Allocate runway and update current time
                    print(f"Allocating runway {i + 1} to Flight {flight.flight_id} at {self.current_times[i]}")
                    self.current_times[i] = max(self.current_times[i], flight.departure_time)
```

### Example Usage (Multiple Runways)
```python
scheduler = MultiRunwayScheduler(3)
scheduler.add_flight(Flight("F1", 0, 30, 2))
scheduler.add_flight(Flight("F2", 5, 25, 1))
scheduler.add_flight(Flight("F3", 10, 40, 3))

scheduler.schedule_flights()
```