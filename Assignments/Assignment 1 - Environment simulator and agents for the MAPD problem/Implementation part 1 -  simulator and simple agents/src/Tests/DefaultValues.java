package src.Tests;

public class DefaultValues {
    static final int VERTICES = 1000;

    static final double EDGE_PROBABILITY_VERY_LOW = 0.002;
    static final double EDGE_PROBABILITY_LOW = 0.005;
    static final double EDGE_PROBABILITY_MEDIUM = 0.012;
    static final double EDGE_PROBABILITY_HIGH = 0.025;
    static final double EDGE_PROBABILITY_DENSE = 0.033;
    static final double EDGE_PROBABILITY_VERY_VERY_DENSE = 0.5;

    static final double FRAGILE_EDGE_PROBABILITY_LOW = 0.0004;
    static final double FRAGILE_EDGE_PROBABILITY_MEDIUM = 0.001;
    static final double FRAGILE_EDGE_PROBABILITY_HIGH = 0.0017;
    static final double FRAGILE_EDGE_PROBABILITY_VERY_HIGH = 0.0025;

    static final double BLOCKED_EDGE_PROBABILITY_LOW = 0.0004;
    static final double BLOCKED_EDGE_PROBABILITY_MEDIUM = 0.001;
    static final double BLOCKED_EDGE_PROBABILITY_HIGH = 0.0017;
    static final double BLOCKED_EDGE_PROBABILITY_VERY_HIGH = 0.0025;


    static final int EDGE_WEIGHT_LOWER_BOUND = 1;
    static final int EDGE_WEIGHT_UPPER_BOUND = 10;

    static final int PACKAGES_COUNT_VERY_LOW = 10;
    static final int PACKAGES_COUNT_LOW = 20;
    static final int PACKAGES_COUNT_MEDIUM = 800;                //80
    static final int PACKAGES_COUNT_HIGH = 200;

    static final int PACKAGES_DEPLOY_TIME_LOWER_BOUND = 0;
    static final int PACKAGES_DEPLOY_TIME_UPPER_BOUND = 500;

    static final int PACKAGES_DELIVERY_TIME_LOWER_BOUND = 100000;   // 15
    static final int PACKAGES_DELIVERY_TIME_UPPER_BOUND = 200000; //2000
    
    static final int HUMAN_PLAYERS_COUNT = 0;
    static final int GREEDY_PLAYERS_COUNT = 2;
    static final int INTERFERING_PLAYERS_COUNT = 2;

    //TODO - add flags to Main() ?
}
