package deque;

public class LauncherLinkedList {
    public static void main(String[] args) {
        LinkedListDeque<Integer> launcher = new LinkedListDeque<>();
        launcher.addLast(4);
        launcher.addLast(5);
        launcher.addFirst(3);
        launcher.addFirst(2);
        launcher.addFirst(1);
        launcher.printDeque();

        System.out.println(launcher.getRecursive(3));
        String[] a = {"char", "Strider"};
        System.out.println(a.length);



    }
}
