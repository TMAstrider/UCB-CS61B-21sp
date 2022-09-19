public class Java_crash_course {
    /** Returns the maximum value from m. */
    public static int max(int[] m) {
        int max = 0;
        // System.out.println(m.length);
        for (int i = 0; i < m.length; i += 1) {
            if (m[i] > max) {
                max = m[i];
            }
        }
        return max;
    }
    public static void windowPosSum(int[] a, int n) {


    }
    public static void main(String[] args) {
        int[] numbers = new int[]{9, 2, 15, 2, 22, 10, 6};
        System.out.println(max(numbers));

    }
}


