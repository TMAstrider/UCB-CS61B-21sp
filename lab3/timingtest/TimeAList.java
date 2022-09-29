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
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();
//        int i = 1;
//        int count = (1 << i) * 1000;
//        System.out.println(count);
        int aSimpleSymbol = 1;
        int base = 1000;
        int addedNumber = 1;
        AList<Integer> simpleTest = new AList<>();
        for (int i = 1; i < 15; i += 1) {
            Stopwatch sw = new Stopwatch();
            int cnt = (aSimpleSymbol << i) * base;
//            int fib41 = fib(41);
            for (int j = 0; j < cnt; ++j) {
                simpleTest.addLast(addedNumber);
            }
//            double timeInSeconds = sw.elapsedTime();
            times.addLast(sw.elapsedTime());
            Ns.addLast(cnt);
            opCounts.addLast(cnt);

            printTimingTable(Ns, times, opCounts);


        }

    }
}
