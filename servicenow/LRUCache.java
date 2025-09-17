public class LRUCache {
    static class Node {
        int k, v;
        Node prev, next;
        Node(int k, int v) {this.k = k; this.v = v;}
    }
    private final int cap;
    private final Map<Integer, Node> map = new HashMap<>();
    private final Node head = new Node(-1, -1), tail = new Node(-1, -1);

    LRUCache(int capacity) {
        this.cap = capacity;
        head.next = tail;
        tail.prev = head;
    }
    private int get(int key) {
        Node n = map.get(key);
        if(n == null) return -1;
        moveToFront(n);
        reurn n.v;
    }
    public void put(int key, int value) {
        Node n = map.get(key);
        if (n!=null) { n.v = value; moveToFront(n); return;}
        if (map.size() == cap) {
            Node lru = tail.prev; remove(lru); map.remove(lru.k);
        }
        Node nn= new Node(key, value);
        insertFront(nn);
        map.put(key, nn);
    }
    private void moveToFront(Node n){ remove(n); insertFront(n); }
    private void insertFront(Node n) {
        n.next = head.next;
        n.prev = head;
        head.next.prev = n;
        head.next = n;
    }
    private void remove(Node n) {
        n.prev.next = n.next;
        n.next.prev = n.prev;
    }
}