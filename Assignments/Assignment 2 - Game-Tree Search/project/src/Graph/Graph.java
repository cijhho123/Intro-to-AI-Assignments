package src.Graph;

import java.util.List;

import src.Env.Enviroment;
import src.Loyalties.LoyaltySetup;
import src.Player.Player;

/*  Notes:
    Since Adjacency List and Adjacency Matrix both have their strengths and weaknesses, I will abstract away the graph's methods 
    implementation to allow a change later on if needed.
    
 *  The graph is an undirected  weighted (integer value of weights) graph. 
 */
public interface Graph {
    public static final int PATH_INDEX = 0, COST_INDEX = 1;
    public static final int NOT_DEFINED_YET = -1;

    ////---------------------Basic operations---------------------
    
    //get a referece to the Enviroment object
    public Enviroment getEnviroment();

    //get the size of the graph (number of verticies)
    public int getSize();

    //advance local time by t units
    public void advanceLocalTime(int t);

    //get the local time of the graph
    public int getLocalTime();

    //the size of the auxilary graph
    public void setSizeAuxilaryGraph(int size);

    //get the size of the auxilary graph (-1) if undefined yet
    public int getAuxilaryGraphSize();

    //get all the verticies of the graph
    public Vertex[] getVertecies();

    public Vertex getVertex(int n);

    //get a hashmap of all the edges in the graph
    public Edge[][] getEdges(); 

    //get a specific edge from the graph (null if none)
    public Edge getEdge(int e1, int e2);

    //check if two verticies are immidiate neighbors (have edge connects them)
    public boolean areNeighbors(int e1, int e2);

    //check if two vertices are on the same connected sub-graph
    public boolean isAccessible(int e1, int e2);

    //return the cost of an edge between two nodes, assumes that e1 and e2 are neighbors
    public int getEdgeCost(int e1, int e2);

    // add edgr from e1 to e2 (and the reverse edge - undirected graph)
    public void addEdge(int e1, int e2, int cost);

    // remove an edge from the graph
    public void removeEdge(int e1, int e2);

    // set an edge as blocked
    public void blockEdge(int v1, int v2);

    //as an existing edge as fragile
    public void setFragileEdge(int v1, int v2);

    //check if a given edge is a fragile edge
    public boolean isFragileEdge(int v1, int v2);

    //set the edge between two verticies as broken
    public void updateFragileEdge(Vertex prevLocation, Vertex location);

    //get fragile edges
    public boolean[][] getFragileEdges();

    //get a deep copy of the graph
    public Graph getGraphCopy();

    //get a deep copy of the players in the current graph
    public List<Player> getPlayers();

    //link a LoyaltySetup object to the graph
    public void setLoyaltySetup(LoyaltySetup ls);


    //---------------------Paths---------------------

    //get the next step a play should do from a provided path, to get into "dst" vertex (backtracking)
    public int extractNextStepFromPath(int[] path, int dst);

    //get the cost of a path from src to dst (backtracking)
    public int getPathCost(int src, int dst);
    
    //get the cheapest path and cost betweeen e1 and e2, 
    //@return[0] = path, @return[1] = cost, @return[0][0] = destination vertex, @return[1][0] = total cost of path
    // getWeights is a function that get an Object array as input and when applied return the weights. to make the function generic.
    public int[][] getCheapestPath(int e1, int e2);

    //get the cheapest path and costs from vertex v to any package on the graph
    //@return[0] = path, @return[1] = cost, @return[0][0] = destination vertex, @return[1][0] = total cost of path
    public int[][] getCheapestPathToPackage(int v);

    //get the cheapest path and costs from vertex v to a fragile edge (null if none)
    public int[][] getCheapestPathToFragileEdge(int v);

    public LoyaltySetup getLoyaltySetup();

}
