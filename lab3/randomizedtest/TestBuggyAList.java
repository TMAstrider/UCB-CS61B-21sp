package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
//   YOUR TESTS HERE
    @Test
    public void testone() {
        AListNoResizing<Integer> cor = new AListNoResizing<>();
        BuggyAList<Integer> bug_ls = new BuggyAList<>();
        cor.addLast(1);
        bug_ls.addLast(1);

        assertEquals(cor.size(), bug_ls.size());

    }
    @Test
    public void testThreeAddThreeMove() {
        AListNoResizing<Integer> correct = new AListNoResizing<>();
        BuggyAList<Integer> buggy = new BuggyAList<>();

        correct.addLast(3);
        buggy.addLast(3);
        correct.addLast(4);
        buggy.addLast(4);
        correct.addLast(5);
        buggy.addLast(5);

        assertEquals(correct.size(), buggy.size());

        assertEquals(correct.removeLast(), buggy.removeLast());
        assertEquals(correct.removeLast(), buggy.removeLast());
        assertEquals(correct.removeLast(), buggy.removeLast());
    }
    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> L_buggy = new BuggyAList<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                L_buggy.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
                assertEquals(L.size(), L_buggy.size());
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                System.out.println("size: " + size);
            } else if (operationNumber == 2 && L.size() > 0) {

                assertEquals(L.getLast(), L_buggy.getLast());
            } else if (operationNumber == 3 && L.size() > 0) {
                assertEquals(L.removeLast(), L_buggy.removeLast());
            }

        }
    }
}
