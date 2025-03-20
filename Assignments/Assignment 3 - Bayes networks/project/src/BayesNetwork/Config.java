package src.BayesNetwork;


public class Config {

    //Edge
    public static final int 
        EDGE_NO_EDGE = -1,
        EDGE_BLOCKED = -2;

    public static final boolean
        EDGE_FRAGILE = true,
        EDGE_NON_FRAGILE = false;

    //Season
    public static final int
        SEASON_LOW = 0,
        SEASON_MEDIUM = 1,
        SEASON_HIGH = 2,
        SEASON_UNKNOWN = 3;

    public static final double
        SEASON_ZERO_PROBABILITY_PACKAGE = 0.0,
        SEASON_DEFAULT_PROBABILITY_PACKAGE = 0.1;

    public static final double
        SEASON_LOW_DEFAULT_PROBABILITY = 0.2,
        SEASON_MEDIUM_DEFAULT_PROBABILITY = 0.3,
        SEASON_HIGH_DEFAULT_PROBABILITY = 0.5;


    //Leakage
    public static final double
        LEAKAGE_DEFAULT_GLOBAL_LEAKAGE = 0.1;
    
    //Q Distribution paremeter
    public static final double
        Q_DEFAULT_DISTRIBUTION_PAREMETER = 0.2;

    //magic numbers
    public static final double
        UNDEFINED_PROBABILITY = -1.0;
}
