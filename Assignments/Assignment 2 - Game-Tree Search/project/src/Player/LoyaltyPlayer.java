package src.Player;

import java.util.Comparator;
import java.util.function.Function;

import src.Graph.Graph;
import src.Graph.Simulator;
import src.Graph.State;
import src.Graph.Vertex;
import src.Loyalties.GameMode;
import src.Loyalties.LoyaltySetup;

public class LoyaltyPlayer extends Player{
    public static final int DEFAULT_CUTOFF = 6;
    private int limit;

    public static final int SELF_DELIVER_PACKAGE_POINTS = 2, SELF_PICKUP_PACKAGE_POINTS = 1, 
                        OTHER_DELIVER_PACKAGE_POINTS = 2, OTHER_PICKUP_PACKAGE_POINTS = 1,
                        DEPTH_COST = 1;

    private LoyaltySetup setup;
    private int mode;

    private State gameTree;
    private Function<State, Integer> evaluationFunction;
    private Comparator<? super State> comp; 

    public LoyaltyPlayer(Graph g, Vertex v, int mode, int i, int lim, LoyaltyPlayer other, LoyaltySetup setup){
        super(g, v, i, Player.IS_DELIVERING_PACKAGES, Player.IS_NOT_SEARCH_AGENT);
        this.limit = lim;

        this.setup = setup;
        this.mode = mode;
        this.gameTree = null;

        switch (mode) {
            case GameMode.ADVERSARIAL:
                evaluationFunction = (s) -> ((s.evaulateMainPlayer() - s.evaulateOtherPlayer()));
                comp = (s1, s2) -> (-1) * Integer.compare(s1.getEvaluation(), s2.getEvaluation());
                break;
            
                case GameMode.SEMI_COOPERATIVE:
                evaluationFunction = (s) -> (s.evaulateMainPlayer());
                comp = (s1, s2) -> {
                    int res = (-1) * Integer.compare(s1.getEvaluation(), s2.getEvaluation());
                    if(res != 0)
                        return res;
                    return (-1) * Integer.compare(s1.evaulateOtherPlayer(), s2.evaulateOtherPlayer());
                };
                break;

            case GameMode.FULLY_COOPERATIVE:
                evaluationFunction = (s) -> (s.evaulateMainPlayer() + s.evaulateOtherPlayer());
                comp = (s1, s2) ->  (-1) * Integer.compare(s1.getEvaluation(), s2.getEvaluation());
                break;
        }
    }

    public LoyaltyPlayer(Graph g, Vertex v, int mode, int i, int lim, LoyaltySetup setup){
        this(g, v, mode, i, lim, null, setup);
    }

    public LoyaltyPlayer(LoyaltyPlayer other){
        this(other, other.graph);
    }

    public LoyaltyPlayer(LoyaltyPlayer other, Graph g){
        super(other, g);  
        this.limit = other.limit;
        this.setup = other.setup;
        this.mode = other.mode;
        this.evaluationFunction = other.evaluationFunction;
        this.gameTree = null;
    }

    public int evaulatePlayer(){
        return SELF_DELIVER_PACKAGE_POINTS * this.getScore() + SELF_PICKUP_PACKAGE_POINTS * this.getPackages().size();
    }

    @Override
    int getNextStep() {
        Simulator sim = new Simulator(comp, evaluationFunction, this.getId(), this.limit, this.setup);
        
        if(this.mode == GameMode.ADVERSARIAL){
            this.gameTree = sim.miniMax();
        } else {
            this.gameTree = sim.maxiMax();
        }

        //remove the stub node (tree root)
        this.gameTree.eliminateOldestActionState();
        return this.gameTree.extractActionFromTree(this.id);
    }
    
    public void stepInGameTree(){
        this.gameTree.eliminateOldestActionState();
    }

    @Override
    public String playerTypeString() {
        return "Loyalty Player";
    }

    @Override
    public Player clone() {
        return new LoyaltyPlayer(this);
    }

    @Override
    public Player clone(Graph g) {
        return new LoyaltyPlayer(this, g);
    }
}