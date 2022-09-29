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
        AList<Integer> N = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();
        int aSimpleSymbol = 1;
        int base = 1000;
        int addedNumber = 1;
        int aSymbolTimes = 10000;
        SLList<Integer> simpleTest = new SLList<>();
        for (int i = 0; i < 8; ++i) {

            int cnt = (aSimpleSymbol << i) * base;
//            int fib41 = fib(41);
            for (int j = 0; j < aSymbolTimes; ++j) {
                simpleTest.addLast(addedNumber);
            }

            Stopwatch sw = new Stopwatch();
            for (int j = 0; j < aSymbolTimes; ++j) {
                simpleTest.getLast();
            }
//            double timeInSeconds = sw.elapsedTime();
            times.addLast(sw.elapsedTime());
            N.addLast(cnt);
            opCounts.addLast(aSymbolTimes);

            printTimingTable(N, times, opCounts);
        }
    }

}
