package src.Player;

import java.util.Comparator;
import java.util.function.Function;

import src.Env.Result;
import src.Graph.Graph;
import src.Graph.Simulator;
import src.Graph.State;
import src.Graph.Vertex;

public class AStarPlayer extends Player{
    public static final int DEFAULT_LIMIT = 10000;

    private int limit;

    //to account for a a case of overflow, using ternary expression
    private Function<State, Integer> evaluationFunction = (s) -> (s.getEstimation() + s.getCost() < 0 ? Integer.MAX_VALUE : s.getEstimation() + s.getCost());
    private Comparator<? super State> comp = (s1, s2) -> Integer.compare(evaluationFunction.apply(s1), evaluationFunction.apply(s2)); 

    // Goal is either a SUCESS state where all packages are delivered on time -> perform first step to that action
    // or Goal is a CUTOFF / FAILURE state where we do No-OP

    public AStarPlayer(Graph g, Vertex v, int i, int lim){
        super(g, v, i, Player.IS_DELIVERING_PACKAGES, Player.IS_SEARCH_AGENT);
        this.limit = lim;
    }

    public AStarPlayer(AStarPlayer other){
        this(other, other.graph);
    }

    public AStarPlayer(AStarPlayer other, Graph g){
        super(other, g);  
        this.limit = other.limit;
    }

    
    @Override
    public Player clone() {
        AStarPlayer p = new AStarPlayer(this);
        return p;
    }

    @Override
    public Player clone(Graph g) {
        AStarPlayer p = new AStarPlayer(this, g);
        return p;
    }

    @Override
    int getNextStep() {
        Simulator sim = new Simulator(comp, State.DEFAULT_GOAL_FUNCTION, this.limit, this);
        State s =  sim.treeSearch();

        if(s.getStatus() == Result.FAILURE || s.getStatus() == Result.CUTOFF)
            return Player.NO_OP_NODE_CODE;
        
        return extractNextActionFromState(s);
    }

    @Override
    public String playerTypeString() {
        return "A*";
    }

}
