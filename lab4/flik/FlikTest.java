package flik;
import org.junit.Test;
import java.util.Random;
import static org.junit.Assert.*;

public class FlikTest {
    @Test
    public void test1() {
        for (int i =0; i < 500; i++) {
            int random = new Random().nextInt(1000000);
            int samNum = random;
//            System.out.print(random);
//            System.out.print(" ");
//            System.out.print(samNum);
            assertTrue(Flik.isSameNumber(random, samNum));
        }
    }

    @Test
    public void test2() {
        int a = 128, b = 128, c = 100;
        assertTrue(Flik.isSameNumber(a, b));
        assertFalse(Flik.isSameNumber(a, c));
    }
}
