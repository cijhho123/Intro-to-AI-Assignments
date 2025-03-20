package src.Graph;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Function;

import src.Env.Enviroment;
import src.Env.Result;
import src.Loyalties.LoyaltySetup;




public class Simulator {
    public static final State NO_PARENT = null;
    public static final int NO_ACTION = -1;
    public static final double EXPANSION_TIME = 0; 

    private Enviroment env;
    private Graph graph;
    private State root;

    //Loyalty mode
    private LoyaltySetup loyaltySetup;
    private Function<State, Integer> stateEvaluationFunction;
    private int playerID;
    
    //make sure the comparator is MAX heap! see: https://stackoverflow.com/questions/1098277/java-implementation-for-min-max-heap
    private PriorityQueue<State> fringe;    
    private State initialState;

    private int limit;

    public Simulator(Comparator<? super State> comp, Function<State, Integer> evalFunction, int id, int limit, LoyaltySetup ls){

        this.graph = ls.getPlayer1().getGraph().getGraphCopy();
        this.loyaltySetup = this.graph.getLoyaltySetup();
        this.env = graph.getEnviroment();      

        this.stateEvaluationFunction = evalFunction;
        this.playerID = id;

        this.limit = limit;
        this.initialState = generateIniialState();  
        this.root = initialState;

        fringe = new PriorityQueue<State>(comp);
        fringe.add(initialState);
    }

    //Constructor for simulation with no limit
    public Simulator(Comparator<? super State> comp, Function<State, Integer> evalFunction, int id, LoyaltySetup ls){  
        this(comp, evalFunction, id, ls.getLimit(), ls);
    }

    
    private State miniMaxDecision(State state){
        List<State> children = state.expand();

        //a-b pruning
        int a = Integer.MIN_VALUE;
        int b = Integer.MAX_VALUE;
        
        State maxState = null;
        for(State s : children){
            State sBestChild = minValue(s, a ,b);

            if (maxState == null || sBestChild.getEvaluation() > maxState.getEvaluation()) {
                maxState = s; 
                a = Math.max(a, sBestChild.getEvaluation());
            }
        }

        return maxState;
    }

    public State miniMax(){
        return this.miniMaxDecision(this.root);
    }

    private State maxValue(State state, int a1, int b1){        
        if(state.getDepth() >= this.limit || state.getLocalTime() >= Enviroment.MAX_DEADLINE || state.isGameOver()){
            state.setStatus(Result.CUTOFF);
            return state;
        }

        int a = a1;
        int b = b1;
    
        List<State> children = state.expand();
        State maxState = null;
        
        for(State s : children){
            State sBestChild = minValue(s, a, b);

            if (maxState == null || sBestChild.getEvaluation() > maxState.getEvaluation()) {
                maxState = sBestChild;
            }

            //a-b pruning
            a = Math.max(a, maxState.getEvaluation());
            if (a >= b) {
                return maxState;
            }
        }
        return maxState;
    }

    private State minValue(State state, int a1, int b1){
        if(state.getDepth() >= this.limit || state.getLocalTime() >= Enviroment.MAX_DEADLINE || state.isGameOver()){
            state.setStatus(Result.CUTOFF);
            return state;
        }

        int a = a1;
        int b = b1;

        List<State> children = state.expand();
        State minState = null;
            
        for(State s : children){
            State sBestChild = maxValue(s, a, b);

            if (minState == null || sBestChild.getEvaluation() < minState.getEvaluation()) {
                minState = sBestChild;
            }

            //a-b pruning
            b = Math.min(b, minState.getEvaluation());
            if (b <= a) {
                return minState;
            }
        }
        return minState;
    }

    private State generateIniialState(){
        State root = new State(env, graph, loyaltySetup, stateEvaluationFunction, playerID);
        return root;
    }

    private State maxiMaxDecision(State state){
        if(state.getDepth() >= this.limit || state.getLocalTime() >= Enviroment.MAX_DEADLINE || state.isGameOver()){
            state.setStatus(Result.CUTOFF);
            return state;
        }

        List<State> children = state.expand();
        State maxState = children.get(0);
        
        for(State s : children){
            State sBestChild = maxiMaxDecision(s);

            if(sBestChild.getEvaluation() > maxState.getEvaluation())
                maxState = sBestChild;
            if(sBestChild.getEvaluation() == maxState.getEvaluation() && sBestChild.getDepth() < maxState.getDepth())
                maxState = sBestChild;
        }
        return maxState;
    }

    public State maxiMax(){
        return this.maxiMaxDecision(this.root);
    }

}
