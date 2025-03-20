package src.Graph;

import src.Env.Enviroment;
import src.Loyalties.LoyaltySetup;
import src.Player.Player;

import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;




public class AdjacencyMatrixGraph implements Graph  {
    public static final int NO_PATH = -1;
    public static final int UNDEFINED = -1;

    private Enviroment env;
    private int localTime;
    private List<Player> players;  

    private LoyaltySetup loyaltySetup;

    private int size;
    private int auxilaryGraphSize;

    private  Vertex [] vertecies;
    private boolean [][] isFragile; //symmetric. isFragile[x][y] = TRUE iff isFragile[y][x] = TRUE
    private Edge[][]  edges;

    //Constructor for main graph
    public AdjacencyMatrixGraph(int size, Enviroment e){
        this.env = e;
        this.localTime = e.getCurrentTime();
        players = e.getPlayers();   //shallow copy

        this.size = size;
        this.auxilaryGraphSize = AdjacencyMatrixGraph.UNDEFINED;
        vertecies = new Vertex[size];
        isFragile = new boolean[size][size];

        edges = new Edge[this.size][this.size];
        for(int i=0; i<this.size; i++)
            for(int j=0; j<this.size; j++)
                edges[i][j] = null;

        for(int i = 0; i < size; i++){
            vertecies[i] = new Vertex(i, size);
            
            for(int j=0; j<size; j++)
                isFragile[i][j] = false;
        }
    }

    //Constructor to create a recursive deep copy for simulation, everything non-premitive is deep-copied recursievly 
    private AdjacencyMatrixGraph(Graph other){
        this.env = other.getEnviroment();
        this.localTime = other.getLocalTime();

        this.size = other.getSize();
        this.auxilaryGraphSize = other.getAuxilaryGraphSize();  

        //NOTE: have to create all the verticies before creating the packages
        vertecies = java.util.Arrays.stream(other.getVertecies()).map(v -> new Vertex(v, this)).toArray(Vertex[]::new);
        java.util.Arrays.stream(vertecies).forEach(v -> {
            Vertex otherVertex = other.getVertex(v.getIndex());
            if (otherVertex != null) {
                v.deepCopyPackageFromVertex(otherVertex, this);
            } else {
                v = null;
            }
        });

        //NOTE: have to create all the players before creating their packages
        players = other.getPlayers().stream().map(p -> p.clone(this)).collect(Collectors.toList());
        players.stream().forEach(p -> p.deepCopyPackagesFromPlayer(other.getPlayers().get(p.getId()), this));
        
        this.isFragile = java.util.Arrays.stream(other.getFragileEdges()).map(boolean[]::clone).toArray(boolean[][]::new);
        
        this.edges = java.util.Arrays.stream(other.getEdges()).map(row -> java.util.Arrays.stream(row)
            .map(edge -> edge == null ? null : new Edge(edge)).toArray(Edge[]::new)).toArray(Edge[][]::new);

        this.loyaltySetup = new LoyaltySetup(this, other);
    }

    public void setLoyaltySetup(LoyaltySetup loyaltySetup){
        this.loyaltySetup = loyaltySetup;
    }

    public LoyaltySetup getLoyaltySetup(){
        return this.loyaltySetup;
    }
    

    public Graph getGraphCopy(){
        Graph g = new AdjacencyMatrixGraph(this);
        return g;
    }

    public List<Player> getPlayers(){
        return this.players;
    }

    public void advanceLocalTime(int t){
        this.localTime += t;
    }

    public int getLocalTime(){
        return this.localTime;
    }

    public int getSize(){
        return this.size;
    }

    public void setSizeAuxilaryGraph(int size){
        this.auxilaryGraphSize = size;
    }

    public int getAuxilaryGraphSize(){
        return this.auxilaryGraphSize;
    }


    public Vertex[] getVertecies(){
        return vertecies;
    }

    public Vertex getVertex(int n){
        return vertecies[n];
    }

    public Edge[][] getEdges(){
        return this.edges;
    }

    public boolean[][] getFragileEdges(){
        return this.isFragile;
    }

    // assumes that cost is a positive integer (cost > 0)
    public void addEdge(int e1, int e2, int cost) {
        vertecies[e1].setNeighborWeight(e2, cost);
        vertecies[e2].setNeighborWeight(e1, cost);

        Edge edgeToAdd = new Edge(e1, e2, cost);
        edges[e1][e2] = edgeToAdd;
        edges[e2][e1] = edgeToAdd;
    }

    public void addEdge(Edge e){
        addEdge(e.getV1(), e.getV2(), e.getWeight());

        if(e.isBrokenEdge())
            this.blockEdge(e.getV1(), e.getV2());
        if(e.isFragileEdge())
            this.setFragileEdge(e.getV1(), e.getV2());
    }

    //assumes the edge exists in the graph
    public void removeEdge(int e1, int e2) {
        vertecies[e1].removeEdge(e2);
        vertecies[e2].removeEdge(e1);

        edges[e1][e2] = null;
        edges[e2][e1] = null;
    }

    public void blockEdge(int e1, int e2){

        //check if such node even exists in the graph - of not, we can ignore its blocking 
        // (it is blocked by default in the weight matrix)

        if(edges[e1][e2] != null){
            edges[e1][e2].setBrokenEdge(true);
            edges[e2][e1].setBrokenEdge(true); 
        }

        vertecies[e1].setNeighborWeight(e2, Vertex.BLOCKED);
        vertecies[e2].setNeighborWeight(e1, Vertex.BLOCKED);
    }
    
    public boolean isBlocked(int e1, int e2){
        return this.edges[e1][e2].isBrokenEdge();
    }

    public void setFragileEdge(int e1, int e2){

        //check if such node even exists in the graph - of not, we can ignore its fragileness 
        // (it is blocked by default in the weight matrix)

        if(edges[e1][e2] != null){
            edges[e1][e2].setFragile(true); //points to the same object as edges[e2][e1]
        }

        isFragile[e1][e2] = true;
        isFragile[e2][e1] = true;
    }

    public boolean areNeighbors(int e1, int e2) {
        return vertecies[e1].isNeighbor(e2);
    }

    public int getEdgeCost(int e1, int e2) {
        return vertecies[e1].getNeighborWeight(e2);
    }

    public Edge getEdge(int e1, int e2){
        return this.edges[e1][e2];
    }

    public void updateFragileEdge(Vertex prevLocation, Vertex location) {
        if(prevLocation == null || location == null)
            return;

        int v1 = prevLocation.getIndex();
        int v2 = location.getIndex();

        if(isFragile[v1][v2]){
            isFragile[v1][v2] = false;
            isFragile[v2][v1] = false;
            blockEdge(v1, v2);
        }
    }

    public boolean isFragileEdge(int e1, int e2){
        return this.isFragile[e1][e2];
    }

    public Enviroment getEnviroment(){
        return this.env;
    }

    //---------------------Paths---------------------
        
    //get the cheapest path betweeen e1 and e2 using Dijkstra's algorithm, null if none.
    // article: https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
    // getWeights is a function that get an Object array as input and when applied return the weights. to make the function generic.
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
            prev[i] = AdjacencyMatrixGraph.UNDEFINED;
        }

        dist[e1] = 0;
        prev[e1] = e1;
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
                
                //take care of a case of Overflow (Integer.MAX_VALUE + a = negative)
                if(dist[u] == Integer.MAX_VALUE)
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

    public int[] getWeights(Vertex[] v, int index){
        return v[index].getAllNeighborsWeights();
    }

    public int[] getWeights(int[][] m, int index){
        return m[index];
    }

    public boolean isAccessible(int e1, int e2){
        return getPathCost(e1, e2) != AdjacencyMatrixGraph.NO_PATH;
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
            prev[i] = AdjacencyMatrixGraph.UNDEFINED;
        }

        dist[v] = 0;
        prev[v] = v;
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

                //take care of a case of Overflow (Integer.MAX_VALUE + a = negative)
                if(dist[u] == Integer.MAX_VALUE)
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
            prev[i] = AdjacencyMatrixGraph.UNDEFINED;
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
            if(prev[u] == AdjacencyMatrixGraph.UNDEFINED)
                continue;

            if(this.isFragile[u][prev[u]]){
                prev[0] = u;
                dist[0] = dist[u];

                int[][] result =  {prev, dist};
                return result;
            }
               

            int[] weights = this.vertecies[u].getAllNeighborsWeights();

            for(int i = 1; i < this.size; i++){
                if(weights[i] < 0)
                    continue;
                
                //take care of a case of Overflow (Integer.MAX_VALUE + a = negative)
                if(dist[u] == Integer.MAX_VALUE)
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
        //edge case where the path is of lengh 0
        if(path[dst] == dst || path[dst] == AdjacencyMatrixGraph.NO_PATH)
            return dst;

        int prev = dst;
        while(path[prev] != prev)
            prev = path[prev];

        return prev;
    }

    public int getPathCost(int src, int dst){
        int[][] data =  getCheapestPath(src, dst);

        if(data == null || data[Graph.COST_INDEX][0] == Integer.MAX_VALUE)
            return NO_PATH;

        return data[Graph.COST_INDEX][0];
    }
    
}

