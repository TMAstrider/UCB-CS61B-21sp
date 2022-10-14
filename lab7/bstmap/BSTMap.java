package bstmap;

import edu.princeton.cs.algs4.BST;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private BSTNode root;
    private int size = 0;

    private class BSTNode{
        private K key;
        private V value;
        private BSTNode left;
        private BSTNode right;

        BSTNode(K ok, V ov) {
            key = ok;
            value = ov;
        }

    }
    /** Removes all the mappings from this map. */
    public void clear() {
        root = null;
        size = 0;
    }

    /** Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        return findKey(root, key);
    }

    private boolean findKey(BSTNode initNode, K key) {
        if (initNode == null) {
            return false;
        }
        int temp = key.compareTo(initNode.key);
        if(temp < 0) {
            return findKey(initNode.left, key);
        } else if(temp > 0) {
            return findKey(initNode.right, key);
        }
        return true;
    }

    /** Returns the value to which the specified key is mapped,
     * or null if this map contains no mapping for the key. */
    public V get(K key) {
        return findGetVal(root, key);
    }

    private V findGetVal(BSTNode initNode, K key) {
        if (initNode == null) {
            return null;
        }
        int temp = key.compareTo(initNode.key);
        if(temp < 0) {
            return findGetVal(initNode.left, key);
        } else if(temp > 0) {
            return findGetVal(initNode.right, key);
        }
        return initNode.value;
    }

    /** Print the elements in order */
    public void printInOrder() {
        printInOrder(root);
    }

    private void printInOrder(BSTNode initNode) {
        if(initNode == null) {
            return;
        }
        printInOrder(initNode.left);
        System.out.println(initNode.key.toString() + "-->>" + initNode.value.toString());
        printInOrder(initNode.right);
    }

    /** Returns the number of key-value mappings in this map. */
    public int size() {
        return size;
    }

    /** Associates the specified value with the specified key in this map. */
    public void put(K key, V value) {
        root = Kput(root, key, value);
        size += 1;
    }

    private BSTNode Kput(BSTNode initNode, K key, V value) {
        BSTNode putNode = new BSTNode(key, value);
        if(initNode == null) {
            return putNode;
        }
        int temp = key.compareTo(initNode.key);
        if(temp > 0){
            initNode.right = Kput(initNode.right, key, value);
        }
        if(temp < 0) {
            initNode.left = Kput(initNode.left, key, value);
        }
        return initNode;

    }
    /** Returns a Set view of the keys contained in this map.
     * Not required for Lab 7.
     * If you don't implement this,
     * throw an UnsupportedOperationException. */
    public Set<K> keySet() {
        throw new UnsupportedOperationException("Invalid operation for sorted list.");
    }

    /**  Removes the mapping for the specified key from this map if present.
     *  Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    public V remove(K key) {
        throw new UnsupportedOperationException("Invalid operation for sorted list.");
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    public V remove(K key, V value) {
        throw new UnsupportedOperationException("Invalid operation for sorted list.");
    }

    @Override
    public Iterator<K> iterator() {
        return null;
    }
}
