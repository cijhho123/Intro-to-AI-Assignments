package src.Graph;

import java.util.PriorityQueue;



public class AdjacencyMatrixGraph implements Graph  {

    private int size;

    private  Vertex [] vertecies;
    private boolean [][] isFragile; //symmetric. isFragile[x][y] = TRUE iff isFragile[y][x] = TRUE
    //TODO: add fragileEdge and BrokenEdge list for displayInfo

    //Constructor
    public AdjacencyMatrixGraph(int size){
        this.size = size;
        vertecies = new Vertex[size];
        isFragile = new boolean[size][size];

        for(int i = 0; i < size; i++){
            vertecies[i] = new Vertex(i, size);
            
            for(int j=0; j<size; j++)
                isFragile[i][j] = false;
        }
    }

    public Vertex[] getVertecies(){
        return vertecies;
    }

    public Vertex getVertex(int n){
        return vertecies[n];
    }

    // assumes that cost is a positive integer (cost > 0)
    public void addEdge(int e1, int e2, int cost) {
        vertecies[e1].setNeighborWeight(e2, cost);
        vertecies[e2].setNeighborWeight(e1, cost);
    }

    public void removeEdge(int e1, int e2) {
        vertecies[e1].removeEdge(e2);
        vertecies[e2].removeEdge(e1);
    }

    public void blockEdge(int e1, int e2){
        vertecies[e1].setNeighborWeight(e2, Vertex.BLOCKED);
        vertecies[e2].setNeighborWeight(e1, Vertex.BLOCKED);
    }

    //assumes the edge already exists in the graph
    public void setFragileEdge(int e1, int e2){
        isFragile[e1][e2] = true;
        isFragile[e2][e1] = true;
    }

    public boolean areNeighbors(int e1, int e2) {
        return vertecies[e1].isNeighbor(e2);
    }

    public int getEdgeCost(int e1, int e2) {
        return vertecies[e1].getNeighborWeight(e2);
    }

    public void updateFragileEdge(Vertex prevLocation, Vertex location) {
        if(prevLocation == null || location == null)
            return;

        int v1 = prevLocation.getIndex();
        int v2 = location.getIndex();

        if(isFragile[v1][v2]){
            isFragile[v1][v2] = false;
            isFragile[v2][v1] = false;
            this.getVertex(v1).setNeighborWeight(v2, Vertex.BROKEN);
            this.getVertex(v2).setNeighborWeight(v1, Vertex.BROKEN);
        }
    }

    //---------------------Paths---------------------
    
//get the cheapest path betweeen e1 and e2 using Dijkstra's algorithm, null if none.
// article: https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
public int[][] getCheapestPath(int e1, int e2){
    //required fields
    int[] dist = new int[this.size];
    int[] prev = new int[this.size];

    //  used lambda expression as comaprator in the Priority Queue, 
    //  see: https://stackoverflow.com/questions/44225896/java-8-lambda-comparator
    PriorityQueue<Integer> minHeap = new PriorityQueue<Integer>(this.size, (d1, d2) -> Integer.compare(dist[d1], dist[d2]));


    //init values
    for(int i = 1; i < this.size; i++){
        dist[i] = Integer.MAX_VALUE;
        prev[i] = -1;
    }

    dist[e1] = 0;
    dist[0] = Graph.NOT_DEFINED_YET;
    prev[0] = Graph.NOT_DEFINED_YET;

    for(int i = 1; i < this.size; i++)
        minHeap.add(i);

    //find path
    while(! minHeap.isEmpty()){
        int u = minHeap.poll();
        if(u == e2){
            prev[0] = u;
            dist[0] = dist[u];

            int[][] result =  {prev, dist};
            return result;
        }
            

        int[] weights = this.vertecies[u].getAllNeighborsWeights();

        for(int i = 1; i < this.size; i++){
            if(weights[i] < 0)
                continue;

            int alt = dist[u] + weights[i];

            if(alt < dist[i]){
                dist[i] = alt;
                prev[i] = u;

                //remove and insert the item so the PQ will update
                minHeap.remove(i);
                minHeap.add(i);
            }
        }
    }

    //if no path from e1 to e2 exists
    return null;
}

    

    // Get the cheapest path from vertex v to any package on the graph
    // Returns an array of backwards path, and the destination vertex in index 0 (unsued by Vertex, as they start with 1), null if none.
    public int[][] getCheapestPathToPackage(int v){
        //required fields
        int[] dist = new int[this.size];
        int[] prev = new int[this.size];

        //  used lambda expression as comaprator in the Priority Queue, 
        //  see: https://stackoverflow.com/questions/44225896/java-8-lambda-comparator
        PriorityQueue<Integer> minHeap = new PriorityQueue<Integer>(this.size, (d1, d2) -> Integer.compare(dist[d1], dist[d2]));


        //init values
        for(int i = 1; i < this.size; i++){
            dist[i] = Integer.MAX_VALUE;
            prev[i] = -1;
        }

        dist[v] = 0;
        dist[0] = Graph.NOT_DEFINED_YET;
        prev[0] = Graph.NOT_DEFINED_YET;

        for(int i = 1; i < this.size; i++)
            minHeap.add(i);

        //find path
        while(! minHeap.isEmpty()){
            int u = minHeap.poll();
            if(this.vertecies[u].containPackagesToDeliver()){
                prev[0] = u;
                dist[0] = dist[u];

                int[][] result =  {prev, dist};
                return result;
            }
               

            int[] weights = this.vertecies[u].getAllNeighborsWeights();

            for(int i = 1; i < this.size; i++){
                if(weights[i] < 0)
                    continue;

                int alt = dist[u] + weights[i];

                if(alt < dist[i]){
                    dist[i] = alt;
                    prev[i] = u;

                    //remove and insert the item so the PQ will update
                    minHeap.remove(i);
                    minHeap.add(i);
                }
            }
        }

        //if no path from e1 to e2 exists
        return null;
    }

    public int[][] getCheapestPathToFragileEdge(int v){
        //required fields
        int[] dist = new int[this.size];
        int[] prev = new int[this.size];

        //  used lambda expression as comaprator in the Priority Queue, 
        //  see: https://stackoverflow.com/questions/44225896/java-8-lambda-comparator
        PriorityQueue<Integer> minHeap = new PriorityQueue<Integer>(this.size, (d1, d2) -> Integer.compare(dist[d1], dist[d2]));


        //init values
        for(int i = 1; i < this.size; i++){
            dist[i] = Integer.MAX_VALUE;
            prev[i] = -1;
        }

        dist[v] = 0;
        dist[0] = Graph.NOT_DEFINED_YET;

        prev[v] = v;    //there are no edges from a vertex to 0, to the base condition will never be satisfied(which is good)
        prev[0] = Graph.NOT_DEFINED_YET;

        for(int i = 1; i < this.size; i++)
            minHeap.add(i);

        //find path
        while(! minHeap.isEmpty()){
            int u = minHeap.poll();

            //in case the player is on a vertex in another isolated subgraphs
            if(prev[u] == -1)
                continue;   //TODO: might be able to return here entirely with null value

            if(this.isFragile[u][prev[u]]){   //TODO: need to make it more generic: pass the cond here as well to have one function for all
                prev[0] = u;
                prev[v] = -1;
                dist[0] = dist[u];

                int[][] result =  {prev, dist};
                return result;
            }
               

            int[] weights = this.vertecies[u].getAllNeighborsWeights();

            for(int i = 1; i < this.size; i++){
                if(weights[i] < 0)
                    continue;

                int alt = dist[u] + weights[i];

                if(alt < dist[i]){
                    dist[i] = alt;
                    prev[i] = u;

                    //remove and insert the item so the PQ will update
                    minHeap.remove(i);
                    minHeap.add(i);
                }
            }
        }

        //if no path from e1 to e2 exists
        return null;
    }

    public int extractNextStepFromPath(int[] path, int dst){
        int prev = dst;
        while(path[prev] != -1 && path[path[prev]] != -1 && path[path[prev]] != 0)
            prev = path[prev];

        return prev;
    }

    public int getPathCost(int src, int dst){
        return getCheapestPath(src, dst)[Graph.COST_INDEX][0];
    }
    
}

