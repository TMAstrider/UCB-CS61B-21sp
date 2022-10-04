package deque;
import java.util.Iterator;

/**
 * @TMAstrider <T_T>
 */
public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class StuffNode {
        private T item;
        private StuffNode prev;
        private StuffNode next;
        StuffNode(T x) {
            item = x;
            prev = next = null;
        }
    }
    public StuffNode sentinel;
    public int size;

    public  LinkedListDeque() {
        sentinel = new StuffNode(null);
        size = 0;
        sentinel.next = sentinel.prev = sentinel;
    }


    /**
     * sorts out the relation for the first element
     */
    private void helperAddFirst(T x, StuffNode sentinel, int size) {
        StuffNode nodeSwap = new StuffNode(x);
        if (size == 0) {
            sentinel.prev = nodeSwap;
            nodeSwap.prev = sentinel;
            nodeSwap.next = sentinel;
        } else {
            nodeSwap.prev = sentinel;
            nodeSwap.next = sentinel.next;
            sentinel.next.prev = nodeSwap;
        }
        sentinel.next = nodeSwap;
    }

    /**
     * Adds an item of type T to the front of the deque. You can assume that item is never null.
     */
    @Override
    public void addFirst(T x) {
        helperAddFirst(x, sentinel, size);
        size += 1;
    }
//    public StuffNode getFirst() {
//        return sentinel.next;
//    }

    /**
     * sorts out the relation for the last element
     */
    private void addLastHelper(T x, StuffNode sentinel, int size) {
        StuffNode nodeSwap = new StuffNode(x);
        if (size == 0) {
            nodeSwap.prev = sentinel;
            sentinel.next = nodeSwap;
            nodeSwap.next = sentinel;
        } else {
            nodeSwap.next = sentinel;
            sentinel.prev.next = nodeSwap;
            nodeSwap.prev = sentinel.prev;
        }
        sentinel.prev = nodeSwap;
    }

    /**
     * Adds an item of type T to the back of the deque. You can assume that item is never null.
     */
    @Override
    public void addLast(T x) {
        addLastHelper(x, sentinel, size);
        size += 1;
    }
    /**
     * Returns true if deque is empty, false otherwise.
     */

    /**
     *Returns the number of items in the deque.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Prints the items in the deque from first to last,
     * separated by a space. Once all the items have been printed,
     * print out a new line.
     */
    @Override
    public void printDeque() {
        StuffNode pItem = sentinel.next;
        while (pItem != sentinel) {
            System.out.print(pItem.item);
            System.out.print(" ");
            pItem = pItem.next;
        }
        System.out.println();
    }

    /**
     * Removes and returns the item at the front of the deque. If no such item exists, returns null.
     */
    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T item = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size--;
        return item;
    }


    /**
     * Removes and returns the item at the back of the deque. If no such item exists, returns null.
     */
    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T item = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size--;
        return item;
    }

    /**
     * Gets the item at the given index, where 0 is the front,
     * 1 is the next item, and so forth. If no such item exists,
     * returns null. Must not alter the deque!
     */
    @Override
    public T get(int index) {
        StuffNode p = sentinel.next;
        while (index > 0) {
            p = p.next;
            if (p == sentinel) {
                return null;
            }
            index--;

        }
        return p.item;
    }
    /**
     * getRecursively helper
     */
    private T getRecursiveHelper(StuffNode p, int index) {
        if (p == sentinel) {
            return null;
        }
        if (index == 0) {
            return p.item;
        }
        index--;
        return getRecursiveHelper(p.next, index);
    }
    /**
     * Gets the item at the given index using recursion
     */
    public T getRecursive(int index) {
        return getRecursiveHelper(sentinel.next, index);

    }

    public Iterator<T> iterator() {
        return new LLIterator();
    }

    private class LLIterator implements Iterator<T> {
        private int curIPos = 0;

        @Override
        public boolean hasNext() {
            return curIPos < size();
        }

        @Override
        public T next() {
            T ans = get(curIPos);
            curIPos++;
            return ans;
        }
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof Deque) {
            Deque<T> pussy = (Deque<T>) o;
            if (pussy.size() != this.size()) {
                return false;
            }
            for (int i = 0; i < this.size(); i++) {
                if (this.get(i) != pussy.get(i)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

}
