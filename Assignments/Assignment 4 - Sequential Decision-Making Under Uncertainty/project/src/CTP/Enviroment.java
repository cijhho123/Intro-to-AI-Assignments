package src.CTP;

import java.io.File;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.HashMap;

public class Enviroment {
    //Grapg edges
    private int graphSize = 0;
    private int edgeCount = 0;
    public static int fragileEdgeCount = 0;

    private int[][] edgeWeights;

    private int [][] isFragileEdge;
    private double[][] blockageProbability;
    private int statesPerLocation;

    //CTP
    private long totalEdgeWeights;
    private int packageDestinationLocation;
    private int packageStartingLocation;

    private State[] states;

    public static final double DELTA = 0.25;   //stopping when the changes in utility is less than delta
    private static int simulationCount = 5;    //5 is a default value


    public void init(){
        calculateOptimalPolicy();
        initialPrintout();
        simulateGame();

        endGameScreen();
    }

    private void calculateOptimalPolicy(){
        double maxChange = 0;

        do {
            maxChange = 0;

            for(int i = 0; i <states.length; i++){
                states[i].calculateValueIteration();
            }
            
            for(int i = 0; i <states.length; i++){
                maxChange = Math.max(states[i].updateFutureValues(), maxChange);
            }

        }while(maxChange >= DELTA);
    }

    
    private void initialPrintout(){
        System.out.println("\n================   MDP Belief-state:   ================");

        for(int i = 0; i < this.states.length; i++){
            System.out.println(this.states[i].getStateDescription() + "\n\n");
        }
    }

    private void simulateGame(){
        for(int i = 1; i <= simulationCount; i++){
            System.out.println("\n\n\n================   Running a simulation #" + i + "   ================");

            boolean[] simlationBlockages = generateRandomBlockages();

            int[] stateArray = new int[Enviroment.fragileEdgeCount + 1];
            Arrays.fill(stateArray, Status.UNKNOWN);
            stateArray[0] = this.packageStartingLocation;

            State simulationState = this.states[State.getIndexFromArray(stateArray)];

            System.out.println("Agent starts an location V" + this.packageStartingLocation + " and all the fragile edges' status is unknown.");

            while(!simulationState.isTerminalState()){
                int action = simulationState.getBestAction();

                if(isFragileEdge[simulationState.getLocation()][action] == Config.EDGE_NO_EDGE){
                    System.out.println("Agent location: " + simulationState.getLocation() +  " Best action: V" + action +", non-fragile edge. walking on it.");

                    int[] resultEdgesStatus = simulationState.getEdgeStatusCopy();
                    resultEdgesStatus[0] = action;
                    simulationState = this.states[State.getIndexFromArray(resultEdgesStatus)];
                } else {
                    System.out.print("Agent location: " + simulationState.getLocation() +  " Best action: V" + action +", fragile edge. ");

                    int fragileEdgeIndex = this.isFragileEdge[simulationState.getLocation()][action];
                    if(simlationBlockages[fragileEdgeIndex]){
                        System.out.println("It appears to be blocked! transferring to the appropriate state...");

                        int[] resultEdgesStatus = simulationState.getEdgeStatusCopy();
                        resultEdgesStatus[fragileEdgeIndex + 1] = Status.BLOCKED;
                        simulationState = this.states[State.getIndexFromArray(resultEdgesStatus)];
                    } else {
                        System.out.println("It appears to be free, walking on it.");

                        int[] resultEdgesStatus = simulationState.getEdgeStatusCopy();
                        resultEdgesStatus[0] = action;
                        resultEdgesStatus[fragileEdgeIndex + 1] = Status.FREE;
                        simulationState = this.states[State.getIndexFromArray(resultEdgesStatus)];
                    }
                }
            }
            System.out.println("Arrived at package location at V" + this.packageDestinationLocation);
        }
    }


    private boolean[] generateRandomBlockages(){
        boolean[] simlationBlockages = new boolean[fragileEdgeCount];
        for(int i=1; i < this.graphSize; i++){
            for(int j=1; j < i; j++){

                int index = this.isFragileEdge[i][j];
                if(index == Config.EDGE_NO_EDGE)
                    continue;

                simlationBlockages[index] =  (Math.random() < blockageProbability[i][j]);
            }
        }

        return simlationBlockages;
    }

    private void endGameScreen(){
        System.out.println("\n\nFinished the simulations... The program will now exit.");
    }
    
    //constructor
    public Enviroment(File input) throws Exception{
        
        try {
            Scanner scanner = new Scanner(input);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                
                if(line.length() == 0 || line.charAt(0) == ';')
                    continue;

                String head = line.substring(0, 2);

                if(head.equals(InputFileTemplate.VERTICIES_COUNT))
                    handleN(line);
                else if(head.equals(InputFileTemplate.EDGE))
                    handleE(line);
                else if(head.equals(InputFileTemplate.PACKAGE))
                    handleP(line);
                else if(head.equals(InputFileTemplate.FRAGILE_EDGE))
                    handleF(line);
                else if(head.equals(InputFileTemplate.AGENT_STARTING_LOCATION))
                    handleA(line);
                else if(head.equals(InputFileTemplate.SIMULATION_COUNT))
                    handleC(line);
            }

            scanner.close();

            //post processing, fill the states and map them
            createAndMapStates();

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            if(graphSize == 0){
                throw new Exception("illegal input, must provide a graph size using #n <NUMBER>");
            }

            
    }

    private void createAndMapStates(){
        statesPerLocation = (int) Math.pow(3, fragileEdgeCount);  //3 possible values for each fragile edge: FREE, BLOCKED, UNKNOWN
        states = new State[statesPerLocation * (this.graphSize - 1)];   // |States| = 3^|Fragile Edges| * |V|

        int[] stateArray = new int[Enviroment.fragileEdgeCount + 1];
        stateArray[stateArray.length - 1] = -1;  //to include the (0,0,0,0...,0) state
        
        for(int counter = 0; counter < statesPerLocation; counter++){
            //Counting in base 3 to get all the possible combinations in order

            int index = stateArray.length - 1;
            while(stateArray[index] == Status.UNKNOWN){
                stateArray[index] = Status.FREE;
                index--;
            }

            stateArray[index]++;

            for(int location = 1; location < this.graphSize; location++){
                stateArray[0] = location;
                State s = new State(counter, location, this, stateArray);
                states[counter + statesPerLocation * (location - 1)] = s;
            }
        }

        //set terminal states
        for(int i=0; i < statesPerLocation; i++){
            states[i + statesPerLocation * (this.packageDestinationLocation - 1)].setTerminalState();
        }
    }


    // #N <INT> - number of vertices N in graph (from V1 to Vn)
    private void handleN(String str) throws Exception{
        if(graphSize != 0)
            throw new Exception("illegal input, can't provide more than one size for the graph. line: \n" + str);

        Scanner s = new Scanner(str);
        int size = s.useDelimiter("\\D+").nextInt();
        s.close();

        size += 1;// +1 to have V1...Vn
        this.graphSize = size;

        //Graph attributes        
        edgeWeights = new int[graphSize][graphSize];
        isFragileEdge = new int[graphSize][graphSize];
        blockageProbability = new double[this.graphSize][this.graphSize];

        for(int i = 1; i < graphSize; i++){
            for(int j=1; j<graphSize; j++){
                edgeWeights[i][j] = Config.EDGE_NO_EDGE;
                isFragileEdge[i][j] = Config.EDGE_NO_EDGE;
                blockageProbability[i][j] = Config.NON_BLOCKED_PROBABILITY;
            }
        }

        //for state mapping
        for(int i = 1; i < graphSize; i++){
            this.edgeWeights[i][0] = i;
        }
    }

    // #E1 1 2 W1  -  Edge 1 from vertex 1 to vertex 2, weight 1
    private void handleE(String str) throws Exception{
        if(graphSize == 0)
            throw new Exception("Invalid input, must create a graph with #N before adding edges with #E !");

        Scanner s = new Scanner(str);

        @SuppressWarnings("unused") //for backwards compatabillity 
        int e = s.useDelimiter("\\D+").nextInt();
        int v1 = s.useDelimiter("\\D+").nextInt();
        int v2 = s.useDelimiter("\\D+").nextInt();
        int w = s.useDelimiter("\\D+").nextInt();

        s.close();

        edgeWeights[v1][v2] = w;
        edgeWeights[v2][v1] = w;
        edgeCount ++;

        totalEdgeWeights += w;

        /*     we only model fragile edges in the BN. see mail from Eyal:

                        In the BN you model only edges that CAN be fragile,
                        and no others, which means they have to be in the input graph
                        or you somply do not have a variable for them.
        */
    }
    
    //#P1 D V5          ; Package 1 to be delivered at V5 (already carried)
    private void handleP(String str) throws Exception{
        str = str.substring(7);
        Scanner s = new Scanner(str);
        s.useDelimiter("[\\s;]+");

        int vertexID = s.nextInt();
        s.close();

        this.packageDestinationLocation = vertexID;
    }



    //#F V3 V4  0.4      ; Edge from 3 to 4 is fragile, blocked with probability 0.4 
    private void handleF(String str){
        int v1, v2;
        double probability;

        //----
         Pattern pattern = Pattern.compile("V(\\d+) V(\\d+)\\s+([-+]?[0-9]*\\.?[0-9]+)");
         Matcher matcher = pattern.matcher(str);
 
         if (matcher.find()) {
            v1 = Integer.parseInt(matcher.group(1)); 
            v2 = Integer.parseInt(matcher.group(2));
            probability = Double.parseDouble(matcher.group(3));
        }else {
            throw new IllegalArgumentException();
        }

        isFragileEdge[v1][v2] = Enviroment.fragileEdgeCount;
        isFragileEdge[v2][v1] = Enviroment.fragileEdgeCount;

        blockageProbability[v1][v2] = probability;
        blockageProbability[v2][v1] = probability;

        fragileEdgeCount++;
    }


    private void handleA(String str){
        Scanner s = new Scanner(str);
        int index = s.useDelimiter("\\D+").nextInt();

        this.packageStartingLocation = index;
        s.close();
    }

    private void handleC(String str){
        Scanner s = new Scanner(str);
        int count = s.useDelimiter("\\D+").nextInt();

        Enviroment.simulationCount = count;
        s.close();
    }

    public int getSize(){
        return this.graphSize;
    }

    public boolean isEdgeExist(int i, int j){
        return this.edgeWeights[i][j] > 0;
    }

    public int getEdgeCount(){
        return this.edgeCount;
    }

    public int getFragileEdgeCount(){
        return this.fragileEdgeCount;
    }

    public int[] getVertexActions(int index){
        return this.edgeWeights[index];
    }

    //utils
    public int getFragileEdgeIndex(int i, int j){
        return this.isFragileEdge[i][j];    //-1 if is not fragile, otherwise return fragile edge index
    }

    public String getIthFragileEdgeDescription(int i){
        int v1 = -1, v2 = -1;
    
        for(int a=1; a<this.graphSize; a++){
            for(int b=1; b<a; b++){
                if(isFragileEdge[a][b] == i){
                    v1 = b;
                    v2 = a;
                }
            }
        }
        String output = "(V" + Integer.toString(v1) + ",V" + Integer.toString(v2) + ")";
        return output;
    }

    public State getStateFromMap(int index){
        return this.states[index];
    }

    public double getBlockageProbability(int v1, int v2){
        return this.blockageProbability[v1][v2];
    }
}

/*  Assumptions on the input text file to make the life easier
    0. Assumes input validity (e.g. no stuff like #N with no number afterwards)
 *  1. Each row contains only one command, and each parameter is sepearted by a single space ( )
 *  2. If a row contains a comment (;), there's at least one space before the command and the ;
*/