package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private static final int DEFAULT_initialSize = 16;
    private static final double DEFAULT_LoadFactor = 0.75;
    private final double LoadFactor;
    private int size;

    /** Constructors */
    public MyHashMap() {
        this(DEFAULT_initialSize, DEFAULT_LoadFactor);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, DEFAULT_LoadFactor);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        LoadFactor = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        Node node = new Node(key,value);
        return node;
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        for (int i = 0; i < tableSize; i += 1) {
            table[i] = createBucket();
        }
        return table;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!
    @Override
    public void clear() {
        size = 0;
        buckets = createTable(DEFAULT_initialSize);
    }

    @Override
    public boolean containsKey(K key) {
        int index = getIndex(key,buckets);
        Node node = getNode(key, index);
        if (node == null) {
            return false;
        } else {
            return true;
        }
    }
    private int getIndex(K key, Collection<Node>[] buckets) {
        return Math.floorMod(key.hashCode(), buckets.length);
    }
    private Node getNode(K key, int Index) {
        for(Node node : buckets[Index]) {
            if (key.equals(node.key)) {
                return node;
            }
        }
        return null;
    }
    private Node getNode(K key, int Index, Collection<Node>[] oldBuckets) {
        for(Node node : oldBuckets[Index]) {
            if (key.equals(node.key)) {
                return node;
            }
        }
        return null;
    }

    @Override
    public V get(K key) {
        int index = getIndex(key, buckets);
        Node node = getNode(key, index);
        if (node == null) {
            return null;
        }
        return node.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        int hashCode = key.hashCode();
        int index = getIndex(key, buckets);
        if(containsKey(key)){
            getNode(key, index).value = value;
        } else {
            buckets[index].add(createNode(key, value));
            size += 1;
        }
        int loadFactor = size() / buckets.length;
        if (loadFactor > LoadFactor) {
            resize(buckets.length * 2);
        }
    }

    @Override
    public Set<K> keySet() {
        HashSet<K> set = new HashSet<>();
        addKey(set);
        return set;
    }
    /** add all key to keySet */
    private void addKey(Set<K> set) {
        for(int i = 0; i < buckets.length; i += 1) {
            for(Node node : buckets[i]) {
                set.add(node.key);
            }
        }
    }

    @Override
    public V remove(K key) {
        int index = getIndex(key, buckets);
        Node node = getNode(key, index);
        if (node != null) {
            buckets[index].remove(node);
            size -= 1;
            return node.value;
        }

        return null;
    }
    private void resize(int tableSize) {
        size = 0;
        Set<K> set = keySet();
        Collection<Node>[] newBuckets = createTable(tableSize);
        Collection<Node>[] oldBuckets;
        oldBuckets = buckets;
        buckets = newBuckets;
        for(K key : set) {
            Node node = getNode(key, getIndex(key, oldBuckets), oldBuckets);
            put(node.key, node.value);
        }


    }

    @Override
    public V remove(K key, V value) {
        int index = getIndex(key, buckets);
        Node node = getNode(key, index);
        if (node != null && node.key.equals(key)) {
            buckets[index].remove(node);
            size -= 1;
            return node.value;
        }

        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }
}
