package src.Graph;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

import src.Env.Enviroment;
import src.Env.Result;
import src.Player.Player;



public class Simulator {
    public static final State NO_PARENT = null;
    public static final int NO_ACTION = -1;
    public static final int NO_LIMIT = -1;
    public static final double EXPANSION_TIME = 0;

    private Enviroment env;
    private Graph graph;
    private Player player;

    @SuppressWarnings("unused")
    private int searchTime;

    //make sure the comparator is MAX heap! see: https://stackoverflow.com/questions/1098277/java-implementation-for-min-max-heap
    private PriorityQueue<State> fringe;    
    private State initialState;

    private int limit;
    private int expansionCounter = 0;

    private State localMaximumScoreTest;

    public Simulator(Comparator<? super State> comp, Function<State,Boolean> test, int limit, Player p){  
        this.searchTime = 0;
        this.player = p;
        this.graph = p.getGraph().getGraphCopy();
        this.env = graph.getEnviroment();

        this.limit = limit;
        this.initialState = generateIniialState();        

        fringe = new PriorityQueue<State>(comp);
        fringe.add(initialState);

        this.localMaximumScoreTest = this.initialState;
    }

    //Constructor for simulation with no limit
    public Simulator(Comparator<? super State> comp, Function<State,Boolean> test, Player p){  
        this(comp, test, Simulator.NO_LIMIT, p);
    }

    public State treeSearch(){
        while(! fringe.isEmpty()){
            State s = fringe.poll();

            if(limit != Simulator.NO_LIMIT && expansionCounter >= limit){
                s.setStatus(Result.CUTOFF);
                return s;
            }

            //check for goal state
            if(s.getEstimation() == 0){
                s.setStatus(Result.SUCESS);
                return s;
            }

            s.checkGameOver();

            //if this state is a dead-end 
            if(s.isInGameOver()){
                s.setStatus(Result.FAILURE);
            } else {    //if the state is not a dead-end (have some pick-up and delivering left to do)
                this.searchTime += Simulator.EXPANSION_TIME;
                this.expansionCounter++;
                
                List<State> expandedStates = s.expand();
                for (State childState : expandedStates) {
                    fringe.add(childState);
                }
            }
            
            //look for local maximum (score-wise)
            if(localMaximumScoreTest.getScore() < s.getScore() && s.getEstimation() != Integer.MAX_VALUE){
                localMaximumScoreTest = s;
            }
           
        }

        //in case no global maximum was found 
        //related to the bonus' behaviour, we are utilizing local maximum in case of failure, here the local maximun is a flat line
        this.localMaximumScoreTest.setStatus(Result.FAILURE);
        return localMaximumScoreTest;
    }

    public void setLocalMaximumScoreState(State s){
        this.localMaximumScoreTest = s;
    }

    private State generateIniialState(){
        State root = new State(env, graph, player);
        return root;
    }

    public boolean checkRootGameOver(){
        this.initialState.checkGameOver();
        return this.initialState.isInGameOver();
    }

    // utils
    public static List<Package> packageListDeepCopy(List<Package> p){
        return p.stream().map(pkg -> new Package(pkg)).collect(Collectors.toList());
    }
}
