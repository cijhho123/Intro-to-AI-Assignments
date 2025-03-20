package src.Player;

import java.util.Comparator;
import java.util.function.Function;

import src.Env.Result;
import src.Graph.Graph;
import src.Graph.Simulator;
import src.Graph.State;
import src.Graph.Vertex;

public class GreedyPlayer extends Player{

    //to account for a a case of overflow

    private Function<State, Integer> evaluationFunction = (s) -> (s.getEstimation());
    private Comparator<? super State> comp = (s1, s2) -> Integer.compare(evaluationFunction.apply(s1), evaluationFunction.apply(s2)); 

    //Goal is a SUCESS state where all the packages are delivered on time -> take first action towards that state
    // or Goal is FAILURE -> do No-OP      or     move towards the found local maximum

    public GreedyPlayer(Graph g, Vertex v, int i){
        super(g, v, i, Player.IS_DELIVERING_PACKAGES, Player.IS_SEARCH_AGENT); 
    }

    public GreedyPlayer(GreedyPlayer other){
        this(other, other.graph);
    }

    public GreedyPlayer(GreedyPlayer other, Graph g){
        super(other, g); 
    }

    @Override
    public Player clone() {
        GreedyPlayer p = new GreedyPlayer(this);
        return p;
    }

    @Override
    public Player clone(Graph g) {
        GreedyPlayer p = new GreedyPlayer(this, g);
        return p;
    }

    @Override
    int getNextStep() {
        Simulator sim = new Simulator(comp, State.DEFAULT_GOAL_FUNCTION, Simulator.NO_LIMIT, this);
        State s =  sim.treeSearch();

        if(s.getStatus() == Result.FAILURE)
            return Player.NO_OP_NODE_CODE;

        //can't return CUTOFF in greedy (unlimited search), so we are either on a SUCESS state or on local maximum
        return extractNextActionFromState(s);
    }

    @Override
    public String playerTypeString() {
        return "Greedy";
    }

    
}
