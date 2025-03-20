package src.CTP;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class State {
    /*      A state object used in the belief-state MDP
        *  
        *   Data Structure:
        *      - vertex index |V|
        *      - current state of blockness 3^|Fragile Edges|
        *      - list of actions
        *      - best action
        *      - Utility value
        *      
    */


    private Enviroment env;

    private final int index;
    private int hashCode = -1;

    private double utility = 0; //time it takes, minimization problem
    
    private final int location;
    private int bestAction = -1;
    private int[] actions;  //neighbor vertices are marked with edge cost, others with (-1)

    private int[] edgeStatus;   //fragile edge status, either FREE, BLOCKED or UNKNOWN, NOTICE: index=0 is the location!
    private boolean isTerminalState = false;
    private boolean isUnreachableState = false;

    //future values (in order for us to skip saving a copy of last iteration)
    private double futureUtility;
    private int futureBestAction;

    public State(int index, int location, Enviroment env, int[] stateArray){
        this.index = index;
        this.location = location;

        this.env = env;
        this.actions = this.env.getVertexActions(this.location);

        this.edgeStatus = Arrays.copyOf(stateArray, stateArray.length);
    }

    public void calculateValueIteration(){
        if(this.isTerminalState || this.isUnreachableState)
            return;

        double minUtility = Double.MAX_VALUE;
        int bestAction = Config.NO_EDGE_COST;

        for(int i=1; i < actions.length; i++){
            if(actions[i] == Config.NO_EDGE_COST)
                continue;
            
            int[] resultState = Arrays.copyOf(this.edgeStatus, this.edgeStatus.length);
            
            
            int fragileEdgeIndex = this.env.getFragileEdgeIndex(this.location, i);

            //if the edge is fragile
            if(fragileEdgeIndex != Config.EDGE_NO_EDGE){
                int fragileEdgeStatus = resultState[fragileEdgeIndex + 1];

                if(fragileEdgeStatus == Status.BLOCKED)
                    continue;

                double actionUtility;
                State resultFreedState, resultBlockedState;

                switch (fragileEdgeStatus) {
                    case Status.FREE:
                        resultFreedState = evaluateFreeEdge(resultState, fragileEdgeIndex, i);
                        actionUtility = resultFreedState.getUtility();
                        actionUtility += this.actions[i];   //edge cost
                        break;

                    case Status.UNKNOWN:
                        //if the edge is free
                        resultFreedState = evaluateFreeEdge(resultState, fragileEdgeIndex, i);
                        //if the edge is blocked
                        resultBlockedState = evaluateBlockedEdge(resultState, fragileEdgeIndex);

                        double blockageProbability = this.env.getBlockageProbability(this.location, i);

                        actionUtility = blockageProbability * resultBlockedState.getUtility() +
                            (1 - blockageProbability) * (resultFreedState.getUtility() + this.actions[i]);
                        break;
                
                    default:
                        System.out.println("ERROR: Fragile Edge status can only be 0,1,2");
                        actionUtility = -1000;
                        break;
                }

                if(minUtility > actionUtility) {
                    minUtility = actionUtility;
                    bestAction = i;
                }

            //if the edge is not fragile
            } else {
                //the edge must be free
                resultState[0] = i;
                int resultFreeStateIndex = State.getIndexFromArray(resultState);
                State resultFreedState = env.getStateFromMap(resultFreeStateIndex);

                double actionUtility = resultFreedState.getUtility() + actions[i];

                if(minUtility > actionUtility) {
                    minUtility = actionUtility;
                    bestAction = i;
                }
            }
        }

        this.futureBestAction = bestAction;
        this.futureUtility = minUtility;
    }

    private State evaluateBlockedEdge(int[] resultState, int fragileEdgeIndex){
        resultState[0] = this.location;
        resultState[fragileEdgeIndex + 1] = Status.BLOCKED;
        int resultBlockedStateIndex = State.getIndexFromArray(resultState);
        State resultBlockedState = env.getStateFromMap(resultBlockedStateIndex);

        return resultBlockedState;
    }

    private State evaluateFreeEdge(int[] resultState, int fragileEdgeIndex, int dest){
        int i = dest;

        resultState[0] = i;
        resultState[fragileEdgeIndex + 1] = Status.FREE;
        int resultFreeStateIndex = State.getIndexFromArray(resultState);
        State resultFreedState = env.getStateFromMap(resultFreeStateIndex);

        return resultFreedState;
    }

    public double updateFutureValues(){
        double change = Math.abs(this.utility - this.futureUtility);

        this.utility = futureUtility;
        this.bestAction = futureBestAction;

        return change;
    }

    public String getStateDescription(){
        String output = "Location: V" + Integer.toString(this.location) + "\n";
        output += "Belief-State: (";
        
        for(int i = 0; i < this.env.getFragileEdgeCount(); i++){
            String s = this.env.getIthFragileEdgeDescription(i);
            output += s + " = " + Status.statusDescrition[this.edgeStatus[i+1]] + " ,";
        }

        output = output.substring(0, output.length()-2) + ")\n";

        if(this.bestAction != Config.NO_EDGE_COST){
            output += "Utility score: " + Double.toString(this.utility) + "\n";
            output += "Best Action: " + Integer.toString(bestAction);
        } else {
            output += "Unreachable state";
        }


        return output;
    }

    public int getIndex(){
        return this.index;
    }

    public int getLocation(){
        return this.location;
    }

    public double getUtility(){
        return this.utility;
    }

    public double getFutureUtility(){
        return this.futureUtility;
    }

    public int getBestAction(){
        return this.bestAction;
    }

    public int[] getActions(){
        return this.actions;
    }

    public void setActions(int[] actions){
        this.actions = actions;
    }

    public void setEdgeStatus(int[] status){
        this.edgeStatus = status;
    }

    public int[] getEdgeStatusCopy(){
        return Arrays.copyOf(edgeStatus, edgeStatus.length);
    }

    public void setTerminalState(){
        this.isTerminalState = true;
        this.utility = 0;
        this.bestAction = 0;
    }

    public boolean isTerminalState(){
        return this.isTerminalState;
    }

    public void setUnreachableState(){
        this.isUnreachableState = true;
    }

    public boolean isUnreachableState(){
        return this.isUnreachableState;
    }

    //HashMap compatability
    //create a hash code from index (State blockages array value) and location
    public int hashCode(){
        if(hashCode != -1)
            return this.hashCode;
        
        this.hashCode = State.hashCode(this.location, this.index);
        return this.hashCode;
    }

    public boolean equals(State s){
        return this.hashCode() == s.hashCode();
    }

    public static int hashCode(int location, int index){
        String locStr = "L"+ Integer.toString(location);
        String indexStr = "I"+Integer.toString(index);

        String combinedString = locStr + indexStr;

        //generate MD5 of the string
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(combinedString.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            int hashCode = Integer.parseInt(hashtext.substring(0, 6), 16); //cutting the string to fit into an integer
            return hashCode;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getIndexFromArray(int[] stateArray){
        int stateIndex = 0;

        //base 3 counting
        int exp = 0;
        for(int i=stateArray.length - 1; i >= 1; i--){
            stateIndex += (Math.pow(3, exp) * stateArray[i]);
            exp++;
        }

        //add shifting for location
        stateIndex += (stateArray[0] - 1) * Math.pow(3, Enviroment.fragileEdgeCount);

        return stateIndex;
    }
}
