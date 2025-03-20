package src.BayesNetwork;

//A compact way to abstract the specific enum we are working with.
//see: https://stackoverflow.com/questions/71004128/how-to-make-an-arraylist-with-multiple-enums-in-java
public interface Value {
    //Values order
    public static final int
    SEASON = 0,
    VERTEX = 1,
    BLOCKAGE = 2,
    UNDEFINED = 3;  //for items that shouldn't be in the BN network but must have a values (not unknown, null-ish)
};

enum SEASON_VALUE implements Value{
    LOW,
    MEDIUM,
    HIGH,
    UNKNOWN
}

enum PACKAGE_VALUE implements Value{
    EXIST,
    ABSENT,
    UNKNOWN
}

enum BLOCKAGE_VALUE implements Value{
    BLOCKED,    //the edge is blocked
    FREE,       //the edge is free (unblocked)
    UNKNOWN
}
