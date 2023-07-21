package randomizedtest;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
      AListNoResizing Alist = new AListNoResizing();
      BuggyAList Blist = new BuggyAList();
      Alist.addLast(4);
      Alist.addLast(5);
      Alist.addLast(6);

      Blist.addLast(4);
      Blist.addLast(5);
      Blist.addLast(6);

      assertEquals(Alist.size(), Blist.size());
      assertEquals(Alist.removeLast(),Blist.removeLast() );
      assertEquals(Alist.removeLast(),Blist.removeLast() );
      assertEquals(Alist.removeLast(),Blist.removeLast() );
    }
    @Test
    public void randomizedTest() {
      AListNoResizing<Integer> L = new AListNoResizing<>();
      BuggyAList<Integer> B = new BuggyAList<>();
      int N = 5000;
      for (int i = 0; i < N; i += 1) {
        int operationNumber = StdRandom.uniform(0, 4);
        if (operationNumber == 0) {
          // addLast
          int randVal = StdRandom.uniform(0, 100);
          L.addLast(randVal);
          B.addLast(randVal);
          System.out.println("addLast(" + randVal + ")");
//          assertEquals(L.size(), B.size());
        } else if (operationNumber == 1) {
          // getLast
          if(L.size() == 0) {
            continue;
          }
          int last = L.getLast();
          int Blast = B.getLast();
          System.out.println("getLast(" + last + ")");
//          assertEquals(last, Blast);
        } else if (operationNumber == 2) {
          //removeLast
          if(L.size() == 0) {
            continue;
          }
          int last = L.removeLast();
          int Blast = B.removeLast();
          System.out.println("removeLast(" + last + ")");
//          assertEquals(last, Blast);
        } else if (operationNumber == 3) {
          // size
          int size = L.size();
          int bSize = B.size();
          System.out.println("size: " + size);
        }
      }
    }
}
