package src.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Function;

import src.Env.Enviroment;
import src.Env.Result;
import src.Env.Status;
import src.Player.Player;


public class State {
    public static Function<State,Boolean> DEFAULT_GOAL_FUNCTION = (s) -> s.isGameOver();

    //main variables
    private static Enviroment env;
    private Graph graph; // a recursive deep copy of a graph
    private Player controlloedPlayer;
    
    // state info
    private int status;

    private int currentTime;
    private int currentLocation;
    private int score;      //simulation score, might translate to @evaluation 
    private boolean isDeadEnd;

    
    // node info
    private State parent;   //the state which lead us here
    private List<State> children;
    private int action;     //edge that got us here
    private int depth;      //depth of the simulation

    private int cost;       //g(x) - cost so far to reach this state
    private int estimation; //h(x) - estimated cost to goal from current state


    //constructors for child
    public State (int l, Player p, Graph g, State prnt, int act, int d, int c, int s){
        this.graph = g;
        this.controlloedPlayer = p;

        this.currentTime = graph.getLocalTime();
        this.currentLocation = l;

        this.parent = prnt;
        this.children = new ArrayList<State>();
        this.action = act;
        this.depth = d;
        this.score = s;
        this.isDeadEnd = false;
        
        this.cost = c;
        this.estimation = calculateEstimation();

        this.status = Result.SEARCHING;
    }

    //constructor for root
    public State(Enviroment e, Graph g, Player p){
        State.env = e;
        this.graph = g;
        this.controlloedPlayer = this.graph.getPlayers().get(p.getId());

        this.currentTime = env.getCurrentTime();
        this.currentLocation = p.getLocation().getIndex();


        this.parent = Simulator.NO_PARENT;
        this.children = new ArrayList<State>();
        this.action = Simulator.NO_ACTION;
        this.depth = 0;

        this.status = Result.SEARCHING;

        this.score = 0;

        this.cost = 0;
        this.estimation = calculateEstimation();
    }

    public int getStatus(){
        return this.status;
    }

    public void setStatus(int r){
        this.status = r; 
    }
    
    public boolean isInGameOver(){
        return this.isDeadEnd;
    }

    public int getDepth(){
        return this.depth;
    }

    public State getParent() {
        return this.parent;
    }

    public int getAction(){
        return this.action;
    }
    

    public List<State> expand(){
        List<State> successors = new ArrayList<State>();

        //assumes no new edges are created mid-game, and that edges can't be un-blocked
        //so its safe to use the root of all states - the actual graph
        int[] weights = this.graph.getVertecies()[this.currentLocation].getAllNeighborsWeights();

        //go over all neighbors
        for(int i=1; i < this.graph.getSize(); i++){
            //check for non-neighbors verticies (either no edge at all or broken edge)
            if(weights[i] < 0)
                continue;


            State childState = createChildStateFromAction(i);

            children.add(childState);
            successors.add(childState); //decoupled on purpose, keep it flexiable for possible future changes.
        }

        return successors;
    }

    private State createChildStateFromAction(int i){
        Graph childGraph = this.graph.getGraphCopy();
            
        Edge neighbor = childGraph.getEdges()[this.currentLocation][i];
        childGraph.advanceLocalTime(neighbor.getWeight());

        int childlocation = i;

        //simulating the player's movement
        Player childPlayer = childGraph.getPlayers().get(this.controlloedPlayer.getId());

        //simulate non-search agents movements (bonus 1 and bonus 2)
        int childScore = this.score + simulateAgentsStep(childGraph, i);

        State childParent = this;
        int childAction = i;
        int childDepth = this.depth + 1;
        int childCost = this.cost + this.graph.getVertecies()[this.currentLocation].getAllNeighborsWeights()[i];

        State childState = new State(childlocation, childPlayer, childGraph,
            childParent, childAction, childDepth, childCost, childScore);

        return childState;
    }

    private int simulateAgentsStep(Graph childGraph, int edgeIndex){
        List<Player> statePlayers = childGraph.getPlayers();
        Player childPlayer = statePlayers.get(this.controlloedPlayer.getId());

        int deltaTime = this.graph.getVertecies()[this.currentLocation].getNeighborWeight(edgeIndex);
        int deltaScore = 0;

        while(deltaTime > 0){

            //update search agent player
            deltaScore += childPlayer.emulateStep(edgeIndex);

            for(int playerIndex=0; playerIndex < statePlayers.size(); playerIndex++){
                //non-search agents step simulation
                if(statePlayers.get(playerIndex) != childPlayer && !statePlayers.get(playerIndex).isSearchAgent())
                    statePlayers.get(playerIndex).travers();
            }
            
            deltaTime--;
        }

        return deltaScore;  
    }

    @SuppressWarnings("unused")
     //Note: Eyal said we are allowed to drop the noOp option in Expand(), so its unused out (via mail in 2/12/2024)
    private State createChildStateFromNoOp(){
        Graph childGraph = this.graph.getGraphCopy();
        childGraph.advanceLocalTime(Player.NO_OP_COST);
        int childlocation = this.currentLocation;

        //simulating the player's movement (No-Op)
        Player childPlayer = childGraph.getPlayers().get(this.graph.getPlayers().indexOf(this.controlloedPlayer));
        int childScore = this.score + childPlayer.emulateStep(Player.NO_OP_NODE_CODE);
        
        State childParent = this;
        int childAction = Player.NO_OP_NODE_CODE;
        int childDepth = this.depth + 1;
        int childCost = this.cost + Player.NO_OP_COST;

        State childStateNoOp = new State(childlocation, childPlayer, childGraph,
                childParent, childAction, childDepth, childCost, childScore);

        return childStateNoOp;
    }

    public int updatePackages(List<Package> packages, int time, int location){
        int score = 0;

        for(Package p : packages){
            if(p.canBePickedUp(time, location)){
                p.simulatePickUp();
                score ++;
            }
        }

        return score;
    }

    public int getEstimation() {
        return this.estimation;
    }

    public int getCost(){
        return this.cost;
    }

    public int getScore(){
        return this.score;
    }


    public int calculateEstimation(){
        Graph auxilaryGraph = createAuxilaryGraph(this.graph);
        return calculateMSTWeight(auxilaryGraph, this.graph.getSize() - 1); //-1 to account for counting from 1
    }

    /*  Create an Auxilary Graph for the graph g, containing only the significat points:
     *      - Controlled Player's location
     *      - Pending packages' location
     *      - Destination for packages the controlled player currently hold
     *      - Fragile edges
     * 
     *      NOTE: will add only vertecies accesiable from the controlloed player's vertex. 
     *            So the Auxilary graph will be a connected graph. (which is a pre-cond for Kruskal)
     */
    private Graph createAuxilaryGraph(Graph g){
        boolean[] importantVertices = findImportantVerticies(g);
        Graph aux = g.getGraphCopy();

        int size = 0;
        
        //remove all the edges from the auxilary graph
        for(int i = 1; i < aux.getSize(); i++){
            for(int j = 1; j < aux.getSize(); j++){
                aux.removeEdge(i, j);
            }
        }

        //add only important edges (edges which are between two important verticies)
        for(int i = 1; i < aux.getSize(); i++){
            if(!importantVertices[i])
                continue;

            for(int j = 1; j < i; j++){
                if(importantVertices[j]){
                    int distance = g.getPathCost(i, j);
                    if(distance == AdjacencyMatrixGraph.NO_PATH){
                        distance = Integer.MAX_VALUE;
                    }

                    aux.addEdge(i, j, distance);
                    size ++;
                }
            }
        }
        aux.setSizeAuxilaryGraph(size);
        return aux;
    }

    //cauclate the sum of edges of the Minimum spanning tree of the provided graph using Kruskal’s algorithm
    // MST: https://en.wikipedia.org/wiki/Minimum_spanning_tree
    // Kruskal's algorithm: https://en.wikipedia.org/wiki/Kruskal%27s_algorithm
    private int calculateMSTWeight(Graph g, int verticesCount){
        PriorityQueue<Edge> edgesSortedByWeight = new PriorityQueue<Edge>(g.getSize(), 
            (e1, e2) -> Integer.compare(e1.getWeight(), e2.getWeight()));

        for(int i = 1; i < g.getSize(); i++){
            for(int j = 1; j < i; j++){
                Edge edge = g.getEdge(i, j);
                if(edge != null && edge.getWeight() > 0){
                    edgesSortedByWeight.add(edge);
                }
            }
        }  

        int[] parent = new int[g.getSize()];
        for(int i=0; i<parent.length; i++)
            parent[i] = i;

        List<Edge> tree = new ArrayList<Edge>();

        while(! edgesSortedByWeight.isEmpty()){
            Edge edge = edgesSortedByWeight.poll();

            //check for case of unconnected graph (infinity really is a nightmarish concept)
            if(tree.size() < verticesCount && edge.getWeight() == Integer.MAX_VALUE)
                return Integer.MAX_VALUE;

            int x = g.extractNextStepFromPath(parent, edge.getV1());
            int y = g.extractNextStepFromPath(parent, edge.getV2());
            
            //check for a potential cycle
            if( x != y){
                tree.add(edge);
                
                //union of the two sets
                parent[y] = x;
            }
        }


        //calculate the MST's weight
        int MSTWeight = 0;
        for(Edge edge : tree){
            MSTWeight += edge.getWeight();
        }

        return MSTWeight;
    }

    private boolean[] findImportantVerticies(Graph g){
        boolean[] importantVertices = new boolean[this.graph.getSize()];

        //Controlled Player's location
        importantVertices[this.controlloedPlayer.getLocation().getIndex()] = true;

        //Pending packages' location
        Vertex[] vertecies = this.graph.getVertecies();

        for(int i = 1; i < vertecies.length; i++){
            List <Package> packages = vertecies[i].getPackageList();

            if(packages.size() > 0)
                importantVertices[i] = true;

            for(int p = 0; p < packages.size(); p++){
                importantVertices[packages.get(p).getDestinationLocationIndex()] = true;
            }
        }
        
        //Destination for packages the controlled player currently hold
        List<Package> packagesToDeliver = this.controlloedPlayer.getPackages();
        for(Package p : packagesToDeliver){
            importantVertices[p.getDestinationLocationIndex()] = true;
        }

        return importantVertices;
    }

    
    /*  Generic goal test function, check the following:
     *      1. for every package under State -> Graph -> Verticies[] -> Packages
     *      2. for every package under State -> ControlledPlayer -> Packages
     *      
     *      there exists a vertex (in either of them) that satisfy all 3 conditions:
     *          a) the package is not already delivered
     *          b) the deadline for deliver hasn't passed
     *          c) the package is deliverable:
     *              I)  the player is currently holding the package AND the destnation is accesiable (have a path)
     *                      OR
     *              II) the packae is pending or not-spawned AND its source and destination are accesiable (have a path)
     */
    private boolean isGameOver(){
        // cond 1.
        for(int v = 1; v < this.graph.getSize(); v++){
            List<Package> packages = this.graph.getVertex(v).getPackageList();

            if(! checkPackageListForRelevance(packages))
                return false;
        }       
    
        // cond 2.
        List<Package> packages = this.controlloedPlayer.getPackages();
        return checkPackageListForRelevance(packages);
    }

    public void checkGameOver(){
        this.isDeadEnd = isGameOver();
    }

    /*  A goal state is a state where every packae was delivered.
        By the design of the Graph Object, it means that no Package is on the verticies, but on players
        And evrery package on player is on status DELIVERED
    */
    public boolean isGoalState(){
        //check graph verticies
        Vertex[] v = this.graph.getVertecies();
        for(int i = 1; i < v.length; i++)
            if(v[i].containPackagesToDeliver())
                return false;
        
        //check controlled player packages
        return this.controlloedPlayer.isHoldAnyUndeliveredPackage();
    }

    private boolean checkPackageListForRelevance(List<Package> list){
        for(Package p : list){
            if(checkPackageRelevance(p)){
                return false;   //there are still packages to deliver
            }
        }
        return true;
    }

    private boolean checkPackageRelevance(Package p){
        //     cond a)                         cond b)                       cond c)
        return isPackageNotDelivered(p) && !isPackageDeadlinePassed(p) && isPackageDeliverable(p);
    }

    private boolean isPackageNotDelivered(Package p){
        return p.getStatus() != Status.DELIVERED;
    }

    private boolean isPackageDeadlinePassed(Package p){
        return p.getDeadline() < this.currentTime;
    }

    private boolean isPackageDeliverable(Package p){
        if(p.getStatus() == Status.NOT_SPAWNED || p.getStatus() == Status.PENDING){ 
            boolean isAccessible =  
                this.graph.isAccessible(controlloedPlayer.getLocation().getIndex(), p.getStartingLocationIndex())  &&
                this.graph.isAccessible(controlloedPlayer.getLocation().getIndex(), p.getDestinationLocationIndex());
            return isAccessible;
        } else if(p.getStatus() == Status.DELIVERING && p.getPlayer().getId() == controlloedPlayer.getId()){
            return this.graph.isAccessible(controlloedPlayer.getLocation().getIndex(), p.getDestinationLocationIndex());
        } else {
            return false;
        }
    }

}
