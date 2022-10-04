package deque;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Comparator;
import java.util.Random;

public class MaxArrayDequeTest {

    public static class IntComparator implements Comparator<Integer>{

        @Override
        public int compare(Integer o1, Integer o2) {
            if (o1 > o2){
                return 1;
            } else if (o1 < o2) {
                return -1;
            }else {
                return 0;
            }
        }


    }

    @Test
    public void test1(){

        MaxArrayDeque<Integer> a = new MaxArrayDeque<>(new IntComparator());
        int max = 0;
        a.addFirst(max);
        for (int i = 0; i < 1000000; i++) {
            int k = new Random().nextInt(1000000);
            if (k > max){
                max = k;
            }
            a.addFirst(k);

        }
        assertEquals(max, (int) a.max());


    }
}
