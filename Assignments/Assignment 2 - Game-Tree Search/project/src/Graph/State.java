package src.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Function;

import src.Env.Enviroment;
import src.Env.Main;
import src.Env.Result;
import src.Loyalties.LoyaltySetup;
import src.Player.Player;


public class State {
    public static Function<State,Boolean> DEFAULT_GOAL_FUNCTION = (s) -> s.isGameOver();

    //main variables
    private static Enviroment env;
    private Graph graph; // a recursive deep copy of a graph
    
    // state info
    private int status;

    private int currentTime;
    private boolean isDeadEnd;

    // Loyalty game mode
    private Function<State, Integer> evaluationFunction;
    private int mainPlayerID;
    private int OtherPlayerID;
    private int p1Action, p2Action;
    
    // node info
    private State parent;   //the state which lead us here
    private List<State> children;
    private int depth;      //depth of the simulation
    private int evaluation; //score of the state, based on the game mode


    //constructors for child
    public State (int p1Act, int p2Act, Graph g, LoyaltySetup ls, Function<State, Integer> evalFunc, State prnt, int d, int id){
        this.graph = g;
        this.p1Action = p1Act;
        this.p2Action = p2Act;

        this.currentTime = graph.getLocalTime();

        this.parent = prnt;
        this.children = new ArrayList<State>();
        this.depth = d;
        this.isDeadEnd = false;
        
        this.mainPlayerID = id;
        this.OtherPlayerID = 1 - id;
        this.evaluationFunction = evalFunc;
        this.evaluation = EvaluateState();

        this.status = Result.SEARCHING;
    }

    //constructor for root
    public State(Enviroment e, Graph g, LoyaltySetup ls, Function<State, Integer> evalFunc, int id){
        State.env = e;
        this.graph = g;

        this.currentTime = env.getCurrentTime();

        this.parent = Simulator.NO_PARENT;
        this.children = new ArrayList<State>();
        p1Action = Simulator.NO_ACTION;
        p2Action = Simulator.NO_ACTION;
        this.depth = 0;

        this.status = Result.SEARCHING;

        this.mainPlayerID = id;
        this.OtherPlayerID = 1 - id;
        this.evaluationFunction = evalFunc;
        this.evaluation = EvaluateState();
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

    public int getLocalTime(){
        return this.currentTime;
    }

    public int getAction(int pid){
        if(pid == 0)
            return getP1Action();
        else
            return getP2Action();
    }

    public int getP1Action(){
        return this.p1Action;
    }

    public int getP2Action(){
        return this.p2Action;
    }

    public List<State> getChildrenStates(){
        return this.children;
    }
    

    public List<State> expand(){
        List<State> successors = new ArrayList<State>();

        //assumes no new edges are created mid-game, and that edges can't be un-blocked
        //so its safe to use the root of all states - the actual graph
       
        List<Integer> p1Actions = getActionsForPlayer(0);
        List<Integer> p2Actions = getActionsForPlayer(1);

        for(int p1 : p1Actions){
            for(int p2 : p2Actions){
                State childState = createChildStateFromAction(p1, p2);
                successors.add(childState);
                this.children.add(childState);
            }
        }

        return successors;
    }

    private List<Integer> getActionsForPlayer(int p){
        List<Integer> actions = new ArrayList<Integer>();

        if(! this.graph.getPlayers().get(p).isBusy()){

            int[] weights = this.graph.getVertecies()[this.graph.getPlayers().get(p).getLocation().getIndex()].getAllNeighborsWeights();
            
            //traverse
            for(int i=1; i < this.graph.getSize(); i++){
                if(weights[i] > 0)
                    actions.add(i);
            }
        }
        //no op
        actions.add(Player.NO_OP_NODE_CODE);

        return actions;
    }

    private State createChildStateFromAction(int p1Action, int p2Action){
        Graph childGraph = this.graph.getGraphCopy();
                    
        //simulating the players' movement
        LoyaltySetup childLoyaltySetup = childGraph.getLoyaltySetup();
        if(this.mainPlayerID == 0){
            childLoyaltySetup.getPlayer1().emulateStep(p1Action);
            childLoyaltySetup.getPlayer2().emulateStep(p2Action);
        } else {
            childLoyaltySetup.getPlayer2().emulateStep(p2Action);
            childLoyaltySetup.getPlayer1().emulateStep(p1Action);

        }

        childGraph.advanceLocalTime(1);
        
        State childParent = this;
        int childP1Action = p1Action;
        int childP2Action = p2Action;
        int childDepth = this.depth + 1;

        State childState = new State(childP1Action, childP2Action, childGraph, childLoyaltySetup, this.evaluationFunction,
            childParent, childDepth, this.mainPlayerID);

        return childState;
    }

    private int EvaluateState(){
        return this.evaluationFunction.apply(this);
    }

    public int getEvaluation(){
        return this.evaluation;
    }

    public int evaulateMainPlayer(){
        return this.graph.getPlayers().get(mainPlayerID).evaulatePlayer();
    }

    public int evaulateOtherPlayer(){
        return this.graph.getPlayers().get(OtherPlayerID).evaulatePlayer();
    }


    public int calculateMSTHeuristicMainPlayer(){
        Graph auxilaryGraph = createAuxilaryGraph(this.graph, mainPlayerID);
        int MSTWeight =  calculateMSTWeight(auxilaryGraph, this.graph.getSize() - 1) ; //-1 to account for counting from 1

        return MSTWeight + this.graph.getPlayers().get(mainPlayerID).getBusyTime();
    }

    public int calculateMSTHeuristicOtherPlayer(){
        Graph auxilaryGraph = createAuxilaryGraph(this.graph, OtherPlayerID);
        int MSTWeight =  calculateMSTWeight(auxilaryGraph, this.graph.getSize() - 1); //-1 to account for counting from 1

        return MSTWeight + this.graph.getPlayers().get(OtherPlayerID).getBusyTime();
    }

    public int getMSTDiff(){
        return calculateMSTHeuristicMainPlayer() - calculateMSTHeuristicOtherPlayer();
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
    private Graph createAuxilaryGraph(Graph g, int playerLocation){
        boolean[] importantVertices = findImportantVerticies(g, playerLocation);
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

    private boolean[] findImportantVerticies(Graph g, int playerIndex){
        boolean[] importantVertices = new boolean[this.graph.getSize()];

        //Controlled Player's location
        importantVertices[this.graph.getPlayers().get(playerIndex).getLocation().getIndex()] = true;

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
        List<Package> packagesToDeliver = this.graph.getPlayers().get(playerIndex).getPackages();
        for(Package p : packagesToDeliver){
            importantVertices[p.getDestinationLocationIndex()] = true;
        }
        return importantVertices;
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

    public boolean isGameOver(){

        //check for packages map
        for(Vertex v : this.graph.getVertecies()){
            for(Package pkg : v.getPackageList()){
                if(pkg.getDeadline() > this.currentTime)
                    return false;
            }
        }

        //check for packages on players
        for(Player p : this.graph.getPlayers()){
            for(Package pkg : p.getPackages()){
                if(pkg.getDeadline() > this.currentTime)
                    return false;
            }
        }

        return true;
    }

    public int extractActionFromTree(int pid){
        State tmp = this;

        while(tmp.parent != null)
            tmp = tmp.parent;
        
        return tmp.getAction(pid);
    }

    //delete the highest state parent (first operation to perform), 
    //since we are holding the stats from the leaf, we're dealing with LIFO
    public void eliminateOldestActionState(){
        State tmp = this;

        if(tmp.parent == null){
            Main.RUNNING = false;
            return;
        }

        while(tmp.parent.parent != null)
            tmp = tmp.parent;
        
        tmp.parent = null;
    }
}
