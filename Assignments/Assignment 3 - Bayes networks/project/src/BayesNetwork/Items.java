package src.BayesNetwork;

import java.util.ArrayList;
import java.util.Iterator;

public class Items {
    
    private ArrayList<String> itemsList;
    private Iterator<String> itr;
    private int vertexCount, edgeCount;

    //Constructor
    public Items(Enviroment env){
        this.itemsList = new ArrayList<String>();
        this.vertexCount = env.getSize();
        this.edgeCount = env.getEdgeCount();

        itemsList.add("S1");

        for(int i = 1; i < vertexCount; i++)
            itemsList.add("V" + Integer.toString(i));
        
        for(int i = 1; i < edgeCount; i++)
            itemsList.add("E" + Integer.toString(i));

        itr = itemsList.iterator();
    }

    public String getNextItem(){
        return itr.next();
    }

    public boolean hasNextItem(){
        return this.itr.hasNext();
    }
}
