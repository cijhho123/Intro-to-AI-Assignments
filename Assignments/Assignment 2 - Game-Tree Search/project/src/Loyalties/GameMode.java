package src.Loyalties;

public class GameMode {
    public static final int MODE_COUNT = 3;
    
    public static final int 

    /*  Undefined game mode
    */
    UNDEFINED = 0,      
    
    /*  Adversarial (zero sum game): each agent aims to maximize its own individual score (number of packages deliverd on time)
        minus the opposing agent's score. That is, TS1=IS1-IS2 and TS2=IS2-IS1. Here you should implement an "optimal" agent, 
        using mini-max, with alpha-beta pruning.
    */
    ADVERSARIAL     = 1,    

    /*  A semi-cooperative game: 
        each agent tries to maximize its own individual score. The agent disregards the other agent score, 
        except that ties are broken cooperatively. THat is, TS1=IS1, breaking ties in favor of greater IS2.
    */
    SEMI_COOPERATIVE   = 2,   

    /*  A fully cooperative game: 
        both agents aim to maximize the sum of individual scores, so TS1=TS2=IS1+IS2.
    */
    FULLY_COOPERATIVE    = 3;    


}
