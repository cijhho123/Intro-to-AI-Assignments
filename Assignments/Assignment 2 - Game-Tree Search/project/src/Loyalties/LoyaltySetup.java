package src.Loyalties;

import src.Player.LoyaltyPlayer;
import src.Player.Player;
import src.Graph.Graph;
import src.Graph.Vertex;



public class LoyaltySetup {

    private static int game_mode;
    private LoyaltyPlayer p1, p2;
    private int limit;

    public static final boolean P1 = false, P2 = true;
    private boolean turn;
    

    public LoyaltySetup(Graph graph, int mode, int limit, Vertex v1, Vertex v2){


        if(mode >= 1 && mode <= GameMode.MODE_COUNT){
            LoyaltySetup.game_mode = mode;
        } else {
            throw new IllegalArgumentException("Game mode can only be a number between 1 and " + GameMode.MODE_COUNT +
            "\nSyntax: #M <P1 location> <P2 location> <Game Mode> <CUTOFF LIMIT (optional)>");
        }
        

        this.p1 = new LoyaltyPlayer(graph, v1, game_mode, 0, limit, this);
        this.p2 = new LoyaltyPlayer(graph, v2, game_mode, 1, limit, this);
        this.turn = LoyaltySetup.P1;
        this.limit = limit;
    }

    public LoyaltySetup(Graph graph, int mode, Vertex v1, Vertex v2){
        this(graph, mode, LoyaltyPlayer.DEFAULT_CUTOFF, v1, v2);
    }

    //copy constructor 
    public LoyaltySetup(Graph t, Graph o){
        this.p1 = (LoyaltyPlayer) t.getPlayers().get(o.getLoyaltySetup().getPlayer1().getId());
        this.p2 = (LoyaltyPlayer) t.getPlayers().get(o.getLoyaltySetup().getPlayer2().getId());
        this.limit = o.getLoyaltySetup().getLimit();
        this.turn = o.getLoyaltySetup().turn;
        
    }

    public LoyaltyPlayer getPlayer1() {
        return this.p1;
    }

    public LoyaltyPlayer getPlayer2() {
        return this.p2;
    }

    public int getGameMode(){
        return LoyaltySetup.game_mode;
    }

    public boolean getTurn(){
        return this.turn;
    }

    public void nextTurn(){
        this.turn = ! this.turn;
    }

    public Player getTurnPlayer(){
        if(turn == P1)
            return this.p1;
        else
            return this.p2;
    }

    public int getLimit(){
        return this.limit;
    }
    

}

