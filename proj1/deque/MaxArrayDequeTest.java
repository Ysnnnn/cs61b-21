package deque;
import org.junit.Test;
import static org.junit.Assert.*;
import  java.util.Comparator;
public class MaxArrayDequeTest {
    public static class IntComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1 - o2;
        }
    }
    public static class StringLengthComparator implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            return o1.length() - o2.length();
        }
    }
    public static class StringFistComparator implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    }
    @Test
    public void IntComparatorTest() {
        MaxArrayDeque mAD = new MaxArrayDeque<Integer>(new IntComparator());
        mAD.addFirst(3);
        mAD.addFirst(4);
        mAD.addFirst(7);
        mAD.addFirst(2);
        assertEquals(mAD.max(), 7);
    }
    @Test
    public void StringComparatorTest() {
        MaxArrayDeque mAD = new MaxArrayDeque<String>(new StringFistComparator());
        mAD.addFirst("Today is Monday");
        mAD.addFirst("I hope Thursday coming faster");
        mAD.addFirst("Because my vacation will begin on Thursday");
        mAD.addFirst("A vacation!");
        assertEquals("Because my vacation will begin on Thursday",mAD.max(new StringLengthComparator()));
        assertEquals("Today is Monday", mAD.max());
    }
}
