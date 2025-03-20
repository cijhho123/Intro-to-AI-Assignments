package src.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    //Constructor to create a recursive deep copy for simulation
    public Vertex(Vertex other, Graph g) {
        this.index = other.getIndex();

        //deep copy of objects, packages need to be created after all the verticies 
        //(the destination for a package might not exist yet)
        this.neighborsWeights = other.neighborsWeights.clone();
    }

    public void deepCopyPackageFromVertex(Vertex other, Graph g){
        this.packages =  other.packages.stream().map(p -> new Package(p, g)).collect(Collectors.toList());
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
        return neighborsWeights[n] > 0;
    }

    public boolean containPackagesToDeliver() {
        return this.packages.size() > 0;
    }
}
