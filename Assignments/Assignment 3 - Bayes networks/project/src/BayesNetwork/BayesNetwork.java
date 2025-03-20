package src.BayesNetwork;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class BayesNetwork {
    /*  A Bayes Network class.
    *      Contain a set of nodes Xi, one per variable (season, vertex, edges)
    *      A directed acyclic graph (DAG) sorted by a topologic order of influence.
    * 
    *      Conditional distribution represented as a conditional probability table (CPT) giving the distribution over Xi,
    *      for each combination of parent values.
    * 
    * 
    *      Influence diagram:
    *      
    *                     SEASON                    (Which sason is it)
    *                /  /   |   \   \
    *              V1  V2   V3  V4... Vn            (is the vertex contain a package)
    *              |  / |  / \  / \   /
    *              E1   E2    E3 ... Ek             (is the edge is blocked)
    */

    private Node root;  //dummy Node
	
	//Constructor
    public BayesNetwork(){
        this.root = new Node("root");     //Season node
    }

    public Node getRoot(){
        return this.root;
    }

    public void addNode(Node p, Node c){
        p.addChild(c);
    }

    /*  Getting a topological sort over the DGA using a BFS
     *  https://en.wikipedia.org/wiki/Breadth-first_search
     */
    public List<Node> getTopologicalSortOnItems(){
        List<Node> result = new ArrayList<>(); 
        Queue<Node> queue = new LinkedList<>();
        Set<Node> visited = new HashSet<>();

        queue.add(this.root);
        visited.add(this.root);

        while (!queue.isEmpty()) {
            Node current = queue.poll();        
            result.add(current);

            for (Node child : current.getChildren()) {
                if (!visited.contains(child)) {
                    visited.add(child);  
                    queue.add(child); 
                }
            }
        }

        //remove the dummy node
        result.remove(0);

        return result;
    }

    public List<Node> getEdgeNodesList(){
        List<Node> nodes = this.getTopologicalSortOnItems();
        while(!nodes.isEmpty() && nodes.get(0).getDescription().charAt(0) != 'E')
            nodes.remove(0);
            
        return nodes;
    }

    public void removeBarrenNodes(List<Node> list, Evidence e){
        List<Node> removedNodes = new ArrayList<Node>();

        boolean isRemoved = true;

        while(isRemoved){
            isRemoved = false;

            for(int i = list.size()-1; i >= 0; i--){
                Node curr = list.get(i);

                try {  
                    if(e.isKnown(curr))
                        continue;

                    if(curr.getChildren().size() == 0 || curr.isAllChildrenRemoved(removedNodes)){
                        removedNodes.add(curr);
                        list.remove(curr);
                        isRemoved = true;
                    }
                } catch (Exception e1) {
                    System.out.println("Illegal state detected when removing barren nodes.");
                    e1.printStackTrace();
                    System.exit(1);
                }
            }
        }
    }
}
	