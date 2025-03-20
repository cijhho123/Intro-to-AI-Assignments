package src.BayesNetwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Node {
    /*  A node of a Bayes Network graph
    */

    private ArrayList<Node> parents;
    private ArrayList<Node> children;
    private String description;

    private int[] indexes;  //hold any needed indexes (Vi for vertex, Vi Vj for an edge)

    //Constructor
    public Node(String s, int[] indexes){
        parents = new ArrayList<Node>();
        children = new ArrayList<Node>();
        this.description = s;

        this.indexes = indexes;
    }

    public Node(String s){
        this(s, null);
    }

    //Copy Constructor
    public Node(Node other){
        parents = new ArrayList<Node>();
        children = new ArrayList<Node>();
        
        this.description = other.description;
        this.indexes = Arrays.copyOf(other.indexes, other.indexes.length);

        //Does not link parents and children, need to be handled externally
    }

    public void addChild(Node c){
        this.children.add(c);
        c.parents.add(this);
    }

    public String getDescription(){
        return this.description;
    }

    public Node getChildAtIndex(int index){
        if (children.size() <= index)
            return null;
        
        return children.get(index);
    }

    public Node getParentAtIndex(int index){
        if (parents.size() >= index)
            return null;
        
        return parents.get(index);
    }

    public List<Node> getChildren(){
        return this.children;
    }

    public List<Node> getParents(){
        return this.parents;
    }

    public int getIthIndex(int i){
        return indexes[i];
    }

    public boolean isAllChildrenRemoved(List<Node> removedItems){
        for(Node child : this.children){
            if(! removedItems.contains(child)){
                return false;
            }
        }

        return true;
    }

}
