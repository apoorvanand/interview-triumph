/*
Design a data structure that can efficiently manage data packets in a network router. Each data packet consists of the following attributes:

source: A unique identifier for the machine that generated the packet.
destination: A unique identifier for the target machine.
timestamp: The time at which the packet arrived at the router.
Implement the Router class:

Router(int memoryLimit): Initializes the Router object with a fixed memory limit.

memoryLimit is the maximum number of packets the router can store at any given time.
If adding a new packet would exceed this limit, the oldest packet must be removed to free up space.
bool addPacket(int source, int destination, int timestamp): Adds a packet with the given attributes to the router.

A packet is considered a duplicate if another packet with the same source, destination, and timestamp already exists in the router.
Return true if the packet is successfully added (i.e., it is not a duplicate); otherwise return false.
int[] forwardPacket(): Forwards the next packet in FIFO (First In First Out) order.

Remove the packet from storage.
Return the packet as an array [source, destination, timestamp].
If there are no packets to forward, return an empty array.
int getCount(int destination, int startTime, int endTime):

Returns the number of packets currently stored in the router (i.e., not yet forwarded) that have the specified destination and have timestamps in the inclusive range [startTime, endTime].
Note that queries for addPacket will be made in increasing order of timestamp.

 

Example 1:

Input:
["Router", "addPacket", "addPacket", "addPacket", "addPacket", "addPacket", "forwardPacket", "addPacket", "getCount"]
[[3], [1, 4, 90], [2, 5, 90], [1, 4, 90], [3, 5, 95], [4, 5, 105], [], [5, 2, 110], [5, 100, 110]]

Output:
[null, true, true, false, true, true, [2, 5, 90], true, 1]

Explanation

Router router = new Router(3); // Initialize Router with memoryLimit of 3.
router.addPacket(1, 4, 90); // Packet is added. Return True.
router.addPacket(2, 5, 90); // Packet is added. Return True.
router.addPacket(1, 4, 90); // This is a duplicate packet. Return False.
router.addPacket(3, 5, 95); // Packet is added. Return True
router.addPacket(4, 5, 105); // Packet is added, [1, 4, 90] is removed as number of packets exceeds memoryLimit. Return True.
router.forwardPacket(); // Return [2, 5, 90] and remove it from router.
router.addPacket(5, 2, 110); // Packet is added. Return True.
router.getCount(5, 100, 110); // The only packet with destination 5 and timestamp in the inclusive range [100, 110] is [4, 5, 105]. Return 1.
Example 2:

Input:
["Router", "addPacket", "forwardPacket", "forwardPacket"]
[[2], [7, 4, 90], [], []]

Output:
[null, true, [7, 4, 90], []]

Explanation

Router router = new Router(2); // Initialize Router with memoryLimit of 2.
router.addPacket(7, 4, 90); // Return True.
router.forwardPacket(); // Return [7, 4, 90].
router.forwardPacket(); // There are no packets left, return [].
 

Constraints:

2 <= memoryLimit <= 105
1 <= source, destination <= 2 * 105
1 <= timestamp <= 109
1 <= startTime <= endTime <= 109
At most 105 calls will be made to addPacket, forwardPacket, and getCount methods altogether.
queries for addPacket will be made in increasing order of timestamp.
*/
// @author apoorvanand
import java.util.*;

/**
 * A helper class to represent a data packet.
 * This makes the code cleaner and easier to manage.
 */
class Packet {
    int source;
    int destination;
    int timestamp;
    String key;

    Packet(int source, int destination, int timestamp) {
        this.source = source;
        this.destination = destination;
        this.timestamp = timestamp;
        // A unique string representation for the packet used for duplicate checking.
        this.key = source + ":" + destination + ":" + timestamp;
    }
}

class Router {
    private final int memoryLimit;
    // Queue for FIFO order of packets (for forwarding and eviction).
    private final Queue<Packet> packetQueue;
    // Set for O(1) duplicate checking.
    private final Set<String> packetSet;
    // Map for efficient getCount queries. Maps destination -> (timestamp -> count).
    private final Map<Integer, TreeMap<Integer, Integer>> destinationMap;

    public Router(int memoryLimit) {
        this.memoryLimit = memoryLimit;
        this.packetQueue = new LinkedList<>();
        this.packetSet = new HashSet<>();
        this.destinationMap = new HashMap<>();
    }

    public boolean addPacket(int source, int destination, int timestamp) {
        String key = source + ":" + destination + ":" + timestamp;
        // 1. Duplicate Check: O(1) average time.
        if (packetSet.contains(key)) {
            return false;
        }

        // 2. Eviction Logic: If memory is full, remove the oldest packet.
        if (packetQueue.size() == memoryLimit) {
            Packet oldestPacket = packetQueue.poll();
            if (oldestPacket != null) {
                // Remove from all data structures.
                packetSet.remove(oldestPacket.key);
                updateDestinationMap(oldestPacket.destination, oldestPacket.timestamp, -1);
            }
        }

        // 3. Add New Packet to all data structures.
        Packet newPacket = new Packet(source, destination, timestamp);
        packetQueue.add(newPacket);
        packetSet.add(newPacket.key);
        updateDestinationMap(destination, timestamp, 1);

        return true;
    }

    public int[] forwardPacket() {
        // 1. Check if Empty: O(1) time.
        if (packetQueue.isEmpty()) {
            return new int[0];
        }

        // 2. Forward Oldest Packet and remove it from storage.
        Packet packetToForward = packetQueue.poll();
        packetSet.remove(packetToForward.key);
        updateDestinationMap(packetToForward.destination, packetToForward.timestamp, -1);

        return new int[]{packetToForward.source, packetToForward.destination, packetToForward.timestamp};
    }

    public int getCount(int destination, int startTime, int endTime) {
        if (!destinationMap.containsKey(destination)) {
            return 0;
        }

        TreeMap<Integer, Integer> timestamps = destinationMap.get(destination);
        // Get a view of the map for the inclusive range [startTime, endTime].
        SortedMap<Integer, Integer> rangeMap = timestamps.subMap(startTime, true, endTime, true);

        // Sum counts in the range.
        return rangeMap.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Helper method to update the destinationMap.
     * It increments or decrements the count for a given destination and timestamp.
     * If a count drops to zero, the timestamp entry is removed to save space.
     */
    private void updateDestinationMap(int destination, int timestamp, int delta) {
        destinationMap.computeIfAbsent(destination, k -> new TreeMap<>());
        TreeMap<Integer, Integer> timestamps = destinationMap.get(destination);
        int newCount = timestamps.getOrDefault(timestamp, 0) + delta;

        if (newCount > 0) {
            timestamps.put(timestamp, newCount);
        } else {
            timestamps.remove(timestamp);
            if (timestamps.isEmpty()) {
                destinationMap.remove(destination);
            }
        }
    }
}