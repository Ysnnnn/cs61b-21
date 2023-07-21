package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
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
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        AList Ns = new AList<Integer>();
        AList times = new AList<Double>();
        int number;
        for(number = 1000; number < 10000000; number = number * 2) {
            Stopwatch sw = new Stopwatch();
            creatAlist(number);
            double timeInSeconds = sw.elapsedTime();
            Ns.addLast(number);
            times.addLast(timeInSeconds);
        }
        printTimingTable(Ns, times, Ns);
    }
    /** helper function to create a AList of size */
    public static void creatAlist(int size) {
        AList list = new AList();
        while(size > 0) {
            list.addLast(1);
            size -= 1;
        }
    }
}
