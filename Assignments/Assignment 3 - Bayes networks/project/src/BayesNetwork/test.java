package src.BayesNetwork;

import java.util.List;

public class test {
        public static void main(String[] args) {
        // Create a graph with a single root node
        BayesNetwork bn = new BayesNetwork();

        Node b = new Node("1", null);
        Node c = new Node("2", null);
        Node d = new Node("3", null);
        Node e = new Node("4", null);
        Node e1 = new Node("5", null);
        Node e2 = new Node("6", null) ;
        Node e3 = new Node("13" , null);
        Node e4 = new Node("23" , null);
        Node e5 = new Node("46" , null);

        /*          1
                2 3 4 5 6
         *       23  25  46
         */

        bn.addNode(bn.getRoot(), b);

        bn.addNode(b, c);
        bn.addNode(b, d);
        bn.addNode(b, e);
        bn.addNode(b, e1);
        bn.addNode(b, e2);

        //23
        bn.addNode(c, e3);
        bn.addNode(d, e);

        //25
        bn.addNode(c,e4);
        bn.addNode(e1,e4);

        //46
        bn.addNode(e,e5);
        bn.addNode(e2,e5);

        // Perform BFS
        List<Node> bfsOrder = bn.getTopologicalSortOnItems();

        // Print the BFS order
        System.out.println("BFS Order:");
        for (Node node : bfsOrder) {
            System.out.println(node.getDescription());
        }
    }
}
