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
    private int size = 0;
    private static int initSize = 16;
    private static double defaultLoadFactor = 0.75;


    /** Constructors */
    public MyHashMap() {
        this(initSize, defaultLoadFactor);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, defaultLoadFactor);
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
        defaultLoadFactor = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
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
        for(int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    @Override
    public void clear() {
        buckets = createTable(initSize);
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        int index = getIndex(key);
        return getNodeBool(key, index);
    }

    private boolean getNodeBool(K key, int bucketIndex) {
        for(Node node : buckets[bucketIndex]) {
            if(key.equals(node.key)) {
                return true;
            }
        }
        return false;
    }
    private int getIndex(K key) {
        return getIndex(key, buckets);
    }

    private int getIndex(K key, Collection<Node>[] table) {
        int keyHash = key.hashCode();
        return Math.floorMod(keyHash, table.length);
    }
    @Override
    public V get(K key) {
        int index = getIndex(key);
        return getBucketNode(key, index);
    }

    private V getBucketNode(K key, int bucketIndex) {
        for(Node node : buckets[bucketIndex]) {
            if(key.equals(node.key)) {
                return node.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    private void resize() {
        int newCapacity = buckets.length * 2;
        Collection<Node>[] newBuckets = createTable(newCapacity);
        Iterator<Node> it = new MyHashMapNodeIterator();

    }
    @Override
    public void put(K key, V value) {
        int index = getIndex(key);
        Node newNode = createNode(key, value);
        put(newNode, index);
        size += 1;
        if(isMaxLoad()) {
            resize();
        }
    }

    private boolean isMaxLoad() {
        return (double)(size / buckets.length) > defaultLoadFactor;
    }
    /** helper method */
    private void put(Node inNode, int index) {
        put(inNode, index, buckets);
    }
    private void put(Node inNode, int index, Collection<Node>[] buckets) {
        buckets[index].add(inNode);
    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException("this method is not supported");
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException("this method is not supported");
    }

    @Override
    public Iterator<K> iterator() {
        return new MyHashMapIterator();
    }

    private class MyHashMapIterator implements Iterator<K> {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public K next() {
            return null;
        }
    }

    private class MyHashMapNodeIterator implements Iterator<Node> {
        private Iterator<Collection<Node>> BIterator = Arrays.stream(buckets).iterator();
        private Iterator<Node> currentBucketIterator;
        private int leftovers = size;
        @Override
        public boolean hasNext() {
            if(leftovers > 0) {
                return true;
            }
            return false;
        }

        @Override
        public Node next() {
//            if(this.hasNext()) {
//                Iterator<Node> currentBucket = BIterator.next();

//            }
            return null;
        }
    }

}
