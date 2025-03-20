package src.Player;

import java.util.Comparator;
import java.util.function.Function;

import src.Graph.Graph;
import src.Graph.Simulator;
import src.Graph.Vertex;
import src.Graph.State;
import src.Env.Result;

public class RealTimeAStarPlayer extends Player{
    public static final int DEFAULT_EXPANSIONS = 10;

    private int expansions;

    //to account for a a case of overflow, using ternary expression
    private Function<State, Integer> evaluationFunction = (s) -> (s.getEstimation() + s.getCost() < 0 ? Integer.MAX_VALUE : s.getEstimation() + s.getCost());
    private Comparator<? super State> comp = (s1, s2) -> Integer.compare(evaluationFunction.apply(s1), evaluationFunction.apply(s2));



    // Goal is a either a SUCESS / CUTOFF state -> take first action towards that state
    //  or Goal is a FAILURE -> do No-OP

    public RealTimeAStarPlayer(Graph g, Vertex v, int i, int exp){
        super(g, v, i, Player.IS_DELIVERING_PACKAGES, Player.IS_SEARCH_AGENT);  
        this.expansions = exp;

        
    }

    public RealTimeAStarPlayer(RealTimeAStarPlayer other){
        this(other, other.graph);
    }

    public RealTimeAStarPlayer(RealTimeAStarPlayer other, Graph g){
        super(other, g);
        this.expansions = other.expansions;

        evaluationFunction = (s) -> s.getEstimation() + s.getCost();
        comp = (s1, s2) -> Integer.compare(evaluationFunction.apply(s1), evaluationFunction.apply(s2));
    }

    @Override
    public Player clone() {
        RealTimeAStarPlayer p = new RealTimeAStarPlayer(this);
        return p;
    }

    @Override
    public Player clone(Graph g) {
        RealTimeAStarPlayer p = new RealTimeAStarPlayer(this, g);
        return p;
    }

    @Override
    int getNextStep() {
        Simulator sim = new Simulator(comp, State.DEFAULT_GOAL_FUNCTION, this.expansions, this);
        State s =  sim.treeSearch();

        //Note: I assumes that there's no state where all the packages are delivered (not counting CUTOFF)
        //we are doing a no-op.
        //If we would want to follow the local maximum (score-wise) we'd do:
        //return extractNextActionFromState(s);
        //look into localMaximumScoreTest variable in State::treeSearch()
        if(s.getStatus() == Result.FAILURE)
            return Player.NO_OP_NODE_CODE;

        return extractNextActionFromState(s);
    }
    
    @Override
    public String playerTypeString() {
        return "Real-Time A*";
    }

}
