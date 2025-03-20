package src.Env;

public class Status {
    public static final int 
    NOT_SPAWNED = 0,    //not published yet
    PENDING     = 1,    //pending to be picked up
    DELIVERING  = 2,    //picked up by a player, yet to be delivered
    DELIVERED   = 3;    //deliver to destination
}
