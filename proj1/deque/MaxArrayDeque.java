package deque;

import java.util.Comparator;
import java.util.Iterator;

public class MaxArrayDeque<T> extends ArrayDeque implements Iterable{
    Comparator<T> maxComparator;

    public MaxArrayDeque(Comparator<T> c) {
        maxComparator = c;
    }

    public T max() {
        return max(maxComparator);
    }

    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T maxItem = (T) this.get(0);
        Iterator<T> hey = iterator();
        while (hey.hasNext()) {
            int i = 0;
            T temp = hey.next();
            if (c.compare(maxItem, temp) < 0) {
                maxItem = temp;
            }
        }
        return maxItem;
    }



    public Iterator<T> iterator() {
        return new ArrayIterator();
    }

    private class ArrayIterator implements Iterator<T> {
        int curPos;

        ArrayIterator() {
            curPos = 0;
        }
        @Override
        public T next() {
            T returnItem = (T) get(curPos);
            curPos++;
            return returnItem;
        }

        @Override
        public boolean hasNext() {
            return curPos < size();
        }
    }
}
