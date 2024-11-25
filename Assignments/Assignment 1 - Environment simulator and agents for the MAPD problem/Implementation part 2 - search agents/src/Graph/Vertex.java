package src.Graph;

import java.util.ArrayList;
import java.util.List;

public class Vertex {
    public static int NO_EDGE = -1; //no edge at all
    public static int BLOCKED = -2; //explicity blocked edge
    public static int BROKEN = -3;  //fragiled edge that was walked on once

    private int index;
    private int[] neighborsWeights;
    List<Package> packages;
    
    //Constructor
    public Vertex(int i, int size){
        index = i;
        packages = new ArrayList<>();

        neighborsWeights = new int[size];

        for(int j=0; j<size; j++)
            neighborsWeights[j] = NO_EDGE;
    }

    public int getIndex(){
        return this.index;
    }

    public int[] getAllNeighborsWeights(){
        return this.neighborsWeights;
    }

    public int getNeighborWeight(int n){
        return neighborsWeights[n];
    }

    public void setNeighborWeight(int n, int cost){
        this.neighborsWeights[n] = cost;
    }

    public void removeEdge(int n){
        this.neighborsWeights[n] = NO_EDGE;
    }

    public List<Package> getPackageList(){
        return this.packages;
    }

    public void addPackage(Package p){
        packages.add(p);
    }

    public boolean isNeighbor(int n){
        return neighborsWeights[n] != NO_EDGE;
    }

    public boolean containPackagesToDeliver() {
        for (Package p : this.packages) {
            if(p.getStatus().equals(Status.PENDING.name()))
                return true;
        }
        
            return false;
    }

}
