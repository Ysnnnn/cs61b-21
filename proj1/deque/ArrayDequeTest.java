package deque;
import org.junit.Test;

import static org.junit.Assert.*;
public class ArrayDequeTest {
    @Test
    public void addTest() {


        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 10; i++) {
            ad1.addLast(i);
        }
        for (int i = 0; i < 10; i++) {
            assertEquals("Should be equal", i, (int) ad1.get(i));
        }

        assertNull("Should be null when index out of bound", ad1.get(10));
    }
    @Test
    public void randomizedTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        ad1.addFirst(0);
        for (int i = 1; i < 20; i++) {
            ad1.addLast(i);
        }
        int a =  ad1.removeLast();
        a =  ad1.removeLast();
        a =  ad1.removeLast();
        a =  ad1.removeLast();
        a =  ad1.removeLast();
        a =  ad1.removeLast();
        a = ad1.removeLast();
        a =  ad1.removeLast();
        a =  ad1.removeLast();
        a =  ad1.removeLast();
        a =  ad1.removeLast();
        a =  ad1.removeLast();
        a =  ad1.removeLast();
        ad1.addFirst(4);
        ad1.addFirst(5);
        a =  ad1.removeLast();
    }
}
