package deque;

public class LauncherArrayDeque {
    public static void main(String[] args) {
        ArrayDeque<Integer> launcher = new ArrayDeque<>();
        launcher.addFirst(11);
        launcher.addFirst(10);
//        launcher.addLast(6);
//        launcher.addLast(7);
        launcher.addFirst(9);
        launcher.addFirst(8);
        launcher.addFirst(7);
//        launcher.addLast(6);
//        launcher.addLast(7);
//        launcher.addLast(6);
        launcher.addLast(12);
        launcher.addLast(990);
        launcher.addFirst(6);
        launcher.addFirst(5);
        launcher.addFirst(4);
        launcher.addFirst(3);
        launcher.addFirst(2);
        launcher.addFirst(1);
        System.out.println(launcher.removeFirst());
        System.out.println(launcher.removeLast());
        launcher.removeFirst();
        launcher.removeFirst();
        launcher.removeFirst();
        launcher.removeFirst();
        launcher.removeFirst();
        launcher.removeFirst();
        launcher.removeFirst();
        launcher.removeFirst();
        launcher.removeFirst();
        launcher.printDeque();
        System.out.println(launcher.isEmpty());
        launcher.removeFirst();
        launcher.removeFirst();
        launcher.printDeque();
        System.out.println(launcher.isEmpty());
    }
}
