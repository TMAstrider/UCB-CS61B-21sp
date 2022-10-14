package bstmap;

public class BSTMapMainTest {

    public static void main(String[] args) {
        BSTMap<Integer, Integer> simpleNode = new BSTMap<>();
        simpleNode.put(1, 11);
        simpleNode.printInOrder();
    }
}
