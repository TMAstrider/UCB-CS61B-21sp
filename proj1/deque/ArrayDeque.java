package deque;


import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;
    private  double retentionRate = 0.25;
    private boolean isFull() {
        if (size == items.length) {
            return true;
        }
        return false;
    }

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 3;
        nextLast = 4;
    }
    /**
     * Resize the ArrayList
     *  For arrays of length 16 or more,
     *  your usage factor should always be at least 25%.
     *  resizeFactor == 1 for enlarging the ArrayDeque
     *  resizeFactor == 0 for shrinking the ArrayDeque
     */
    private void resize(int resizeFactor) {
        int brandNewSize;
        if (resizeFactor == 1) {
            brandNewSize = items.length * 2;
        } else {
            brandNewSize = items.length / 2;
        }

        T[] copyArray = (T[]) new Object[brandNewSize];
        for (int i = 0; i < size; i++) {
            copyArray[i] = get(i);
        }
        items = copyArray;
        nextFirst = brandNewSize - 1;
        nextLast = size;

    }

    /**
     * Trying to figure out the next nextFirst
     * I call it Clockwise Tracking
     */
    private int getNextFirstCW(int curPos) {
        return (curPos - 1 + items.length) % items.length;
    }

    /**
     * Trying to figure out the previous nextFirst
     * I call it CounterClockwise Tracking with error 1
     */
    private int getNextFirstCCW(int curPos) {
        return getNextLastCW(curPos);
    }

    private int getNextLastCW(int curPos) {
        return (curPos + 1) % items.length;
    }

    private int getNextLastCCW(int curPos) {
        return getNextFirstCW(curPos);
    }

    private int getExactIndexElement(int curPos, int index) {
        return (curPos + index + 1) % items.length;
    }
    /**
     * Adds an item of type T to the front of the deque.
     * You can assume that item is never null.
     */
    @Override
    public void addFirst(T item) {
        if (isFull()) {
            resize(1);
        }
        items[nextFirst] = item;
        nextFirst = getNextFirstCW(nextFirst);
        size++;
    }

    /**
     * Adds an item of type T to the back of the deque.
     * You can assume that item is never null.
     */
    @Override
    public void addLast(T item) {
        if (isFull()) {
            resize(1);
        }
        items[nextLast] = item;
        nextLast = getNextLastCW(nextLast);
        size++;
    }

    /**
     * Returns true if deque is empty, false otherwise.
     */


    /**
     * Returns the number of items in the deque.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Prints the items in the deque from first to last, separated by a space.
     * Once all the items have been printed, print out a new line.
     */
    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(get(i));
            System.out.print(" ");
        }
        System.out.println();
    }

    /**
     * Removes and returns the item at the front of the deque.
     * If no such item exists, returns null.
     */
    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T getItem = items[getNextFirstCCW(nextFirst)];
        items[getNextFirstCCW(nextFirst)] = null;
        nextFirst = getNextFirstCCW(nextFirst);
        size--;
        while (items.length >= 16 && size < items.length * retentionRate) {
            resize(0);
        }
        return getItem;

    }

    /**
     * Removes and returns the item at the back of the deque.
     * If no such item exists, returns null.
     */
    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T getItem = items[getNextLastCCW(nextLast)];
        items[getNextLastCCW(nextLast)] = null;
        nextLast = getNextLastCCW(nextLast);
        size--;
        while (items.length >= 16 && size < items.length * retentionRate) {
            resize(0);
        }
        return getItem;
    }

    /**
     * Gets the item at the given index, where 0 is the front,
     * 1 is the next item, and so forth.
     * If no such item exists, returns null. Must not alter the deque!
     */
    @Override
    public T get(int index) {
        return items[getExactIndexElement(nextFirst, index)];
    }

    @Override
    public Iterator<T> iterator() {
        return new ADIterator();
    }
    private class ADIterator implements Iterator<T> {
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
                if (!this.get(i).equals(pussy.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
