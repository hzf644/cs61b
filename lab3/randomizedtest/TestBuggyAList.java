package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
        AListNoResizing<Integer> a = new AListNoResizing<>();
        BuggyAList<Integer> b = new BuggyAList<>();
        a.addLast(1);
        b.addLast(1);
        a.addLast(2);
        b.addLast(2);
        a.addLast(3);
        b.addLast(3);
        assert a.removeLast() == b.removeLast();
        assert a.removeLast() == b.removeLast();
        assert a.removeLast() == b.removeLast();
    }

    @Test
    public void randomizedTest(){
      AListNoResizing<Integer> L = new AListNoResizing<>();
      BuggyAList<Integer> M = new BuggyAList<>();
      int N = 50000;
      for (int i = 0; i < N; i += 1) {
        int operationNumber = StdRandom.uniform(0, 3);
        if (operationNumber == 0) {
          // addLast
          int randVal = StdRandom.uniform(0, 100);
          L.addLast(randVal);
          M.addLast(randVal);
          System.out.println("addLast(" + randVal + ")");
        } else if (operationNumber == 1) {
          // size
          System.out.println("L.size: " + L.size());
          System.out.println("M.size: " + M.size());
        }
        else{
            if(L.size()>0&&M.size()>0&&L.size()==M.size()) {
                int a = L.removeLast();
                int b = M.removeLast();
                System.out.println("L removes "+a);
                System.out.println("M removes "+b);
            }
        }
      }
    }
}
