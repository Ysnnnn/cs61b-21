package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();
        int number;
        for(number = 1000; number < 128001; number = number * 2) {
            int ops = 10000;

            SLList SL = creatSLList(number);
            Stopwatch sw = new Stopwatch();
            while(ops > 0) {
                SL.getLast();
                ops -= 1;
            }
            double timeInSeconds = sw.elapsedTime();
            Ns.addLast(number);
            times.addLast(timeInSeconds);
            opCounts.addLast(10000);
        }
        printTimingTable(Ns, times, opCounts);
    }
    /** helper function to create a AList of size */
    public static SLList creatSLList(int size) {
        SLList slist = new SLList();
        while (size > 0) {
            slist.addLast(1);
            size -= 1;
        }
        return slist;
    }
}
