'''
runway management system
airlines = [start, end]
[1.15, 1.20] [1.20,1.35]



'''

'''
1. Airlines_info
- airline identifer
- time 
- type
- runway_assigned
2.control_center
- runaway

3. runways
- runway_id 
- airline_identifer
- operational 
- available 

get_available()

Airline -> control_center ->(get_avaiable -controlcenter) - [runaways] -> transaction locking --> 
airline - assign_runway() (control_center) locking 
control 
- set_maintaince()



'''
Design a **runway management system** that manages multiple runways and airlines, handling runway assignments, availability, and maintenance. The system should support querying available runways, assigning runways to airlines, and managing runway operational status with concurrency control (locking) to avoid conflicts.

---

**Assumptions:**  
- Airlines request runway assignments for specific time intervals (e.g., [1.15, 1.20]).  
- Runways can be operational or under maintenance (unavailable).  
- Multiple runways exist, each identified uniquely.  
- The system must prevent conflicting assignments (no double booking).  
- Time intervals are continuous and can overlap; the system must detect conflicts.  
- Locking mechanisms are needed to handle concurrent requests safely.  
- The system should provide APIs for:  
  - Getting available runways for a given time interval.  
  - Assigning a runway to an airline.  
  - Setting runway maintenance status.  
- The system should maintain airline info including identifier, time, type (arrival/departure), and assigned runway.

---

**Brute force approach:**  
- Store all runway assignments as a list of intervals per runway.  
- To get available runways for a new request interval, check each runway’s assignments and see if the new interval overlaps with any existing assignment or maintenance period.  
- Assign the first available runway found.  
- Time complexity for availability check: O(R * N) where R = number of runways, N = number of assignments per runway.

---

**Optimization and tradeoffs:**  
- Use interval trees or balanced BSTs keyed by time intervals per runway to speed up overlap queries to O(log N).  
- Maintain a priority queue or heap of available runways for quick access.  
- Locking granularity: per runway locking to allow concurrent assignments on different runways.  
- Big-O:  
  - Availability query: O(R log N) with interval trees (better than brute force).  
  - Assignment: O(log N) + locking overhead.  
- Tradeoff: More complex data structures increase implementation complexity but improve performance.

---

**Edge cases:**  
- Overlapping intervals exactly at boundary points (e.g., end time of one assignment equals start time of another) — define if allowed or not.  
- Runway under maintenance during requested interval.  
- Concurrent requests for the same runway and overlapping intervals.  
- Airlines requesting multiple runways simultaneously.  
- Empty runway list or no available runways.  
- Invalid time intervals (start >= end).  
- Time intervals spanning midnight or multiple days (if applicable).

---

**APIs / Classes design:**  

```python
class Runway:
    def __init__(self, runway_id: str):
        self.runway_id = runway_id
        self.assignments = IntervalTree()  # stores (start, end) intervals with airline info
        self.operational = True
        self.lock = threading.Lock()

    def is_available(self, start: float, end: float) -> bool:
        # Check operational and no overlapping assignments
        if not self.operational:
            return False
        return not self.assignments.overlaps(start, end)

    def assign(self, airline_id: str, start: float, end: float) -> bool:
        with self.lock:
            if self.is_available(start, end):
                self.assignments.add(start, end, airline_id)
                return True
            return False

    def set_maintenance(self, status: bool):
        with self.lock:
            self.operational = not status
            if status:
                self.assignments.clear()  # or mark unavailable

class ControlCenter:
    def __init__(self, runways: List[Runway]):
        self.runways = runways

    def get_available_runways(self, start: float, end: float) -> List[Runway]:
        return [r for r in self.runways if r.is_available(start, end)]

    def assign_runway(self, airline_id: str, start: float, end: float) -> Optional[str]:
        for runway in self.runways:
            if runway.assign(airline_id, start, end):
                return runway.runway_id
        return None
```

---

**Architecture / Data model:**  
- **Runway** entity with ID, operational status, and assigned intervals.  
- **Airline assignment** stored as intervals with airline ID and time.  
- **ControlCenter** manages all runways and coordinates assignments.  
- Use interval trees (e.g., `intervaltree` Python package) for efficient interval queries.  
- Use locks per runway to handle concurrency.

---

**Dry run example:**  
- Runways: R1, R2  
- Assign [1.15, 1.20] to Airline A on R1 → success  
- Assign [1.20, 1.35] to Airline B → R1 overlaps at 1.20? If boundary allowed, assign R1 else assign R2  
- Query available runways for [1.18, 1.22] → R1 busy, R2 available → return R2

```python
from intervaltree import IntervalTree
import threading
from typing import List, Optional

class Runway:
    def __init__(self, runway_id: str):
        self.runway_id = runway_id
        self.assignments = IntervalTree()
        self.operational = True
        self.lock = threading.Lock()

    def is_available(self, start: float, end: float) -> bool:
        if not self.operational:
            return False
        overlapping = self.assignments.overlap(start, end)
        return len(overlapping) == 0

    def assign(self, airline_id: str, start: float, end: float) -> bool:
        with self.lock:
            if self.is_available(start, end):
                self.assignments[start:end] = airline_id
                return True
            return False

    def set_maintenance(self, status: bool):
        with self.lock:
            self.operational = not status
            if status:
                self.assignments.clear()

class ControlCenter:
    def __init__(self, runways: List[Runway]):
        self.runways = runways

    def get_available_runways(self, start: float, end: float) -> List[str]:
        return [r.runway_id for r in self.runways if r.is_available(start, end)]

    def assign_runway(self, airline_id: str, start: float, end: float) -> Optional[str]:
        for runway in self.runways:
            if runway.assign(airline_id, start, end):
                return runway.runway_id
        return None
