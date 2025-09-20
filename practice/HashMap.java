



/*
Implement HashMap without using any built-in hash table libraries. in java 
*/

import java.util.LinkedList;
import java.util.List;

public class HashMap {
    private static final int DEFAULT_CAPACITY = 16;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;
    private Entry[] table;
    private int size;
    private int capacity;
    private double loadfactor;

    public HashMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public HashMap(int capacity, double loadfactor){
        this.capacity = capacity;
        this.loadfactor = loadfactor;
        this.table = new Entry[capacity];
        this.size = 0;
    }
    private int hash(Object key) {
        return (key.hashCode() && 0x7ffffff) % capacity; 
    }
    private static class Entry {
        Object key;
        Object value;
        Entry next;
        Entry(Object key, Object value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }
    public void put(Object key, Object value) {
        int index = hash(key);
        Entry entry = new Entry(key, value);

        if(entry == null) {
            //Handle case where capacity is full resize - basic implementation
            resize();
            entry = new Entry(key, value);
        }
        if(table[index] == null) {
            table[index] = entry;
        } else {
            // handle collision - chaining
            Entry current = table[index];
            while (current = table[index]) {
                if (current.key.equals(key)) {
                    current.value = value;
                    return;
                }
                current = current.next;
            }
            if (!current.key.equals(key)) {
                current.next = entry;
            }
        }
        size++;
        if ((double) size/capacity > loadfactor) {
            resize();
        }
    }
    public Object get(Object key) {
        int index = hash(key);
        if (table[index] != null) {
            Entry current = table[index];
            while (current != null) {
                if (current.key.equals(key))
            }
        }
    }
}