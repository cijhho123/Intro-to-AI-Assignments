package src.BayesNetwork;

import java.io.File;
import java.util.Scanner;

import src.BayesNetwork.Tests.Tests.InputFileTemplate;

import java.util.ArrayList;
import java.util.List;

public class Enviroment {
    //Graph season
    private double[] seasonProbability = new double[3];

    //Grapg edges
    private int graphSize = 0;
    private int edgeCount = 0;
    private int fragileEdgeCount = 0;

    private int[][] edgeWeights;
    private boolean[][] isFragileEdge;

    private double[][] blockageProbability;
        
    //Graph Vertecies
    private double[][] packageProbability;

    //Bayes network parameters
    private double globalLeakage;
    private double distributionParemeter;
    
    
    private Value[][] values = {{SEASON_VALUE.LOW, SEASON_VALUE.MEDIUM, SEASON_VALUE.HIGH}, 
    {PACKAGE_VALUE.EXIST, PACKAGE_VALUE.ABSENT}, {BLOCKAGE_VALUE.BLOCKED, BLOCKAGE_VALUE.FREE}};

    //Bayes Network graph
    private BayesNetwork bn = new BayesNetwork();
    
    //Evidence
    private Evidence evidence;

    //Calculations

    /*  Data Structure:
     *      - 1: current vertex v in knownness value k (|v| * k)
     *      - 2: best action to do from current vertex (a*)
     *      - 3: index to a list of actions from current vertex
     * 
     *  Secondary DS:
     *      - 0: 
     *  
     */


    public void init(){
        initialPrintout();
        intercativeQuerying();
    }

    public void intercativeQuerying(){
        System.out.println("\n================ Interactive Querying ================");
        System.out.println("Supported operations:");
        System.out.println("\t1) Reset - reset the evidence");
        System.out.println("\t2) Add - add a piece of evidence to the evidence list");
        System.out.println("\t3) Reason - perform probabilistic reasoning on the evidence");
        System.out.println("\t4) Quit - exit the program");
        System.out.println("\nPlease choose an option\n");

        Scanner s = new Scanner(System.in);

        /*  Reset evidence list to empty.
            Add piece of evidence to evidence list.
            Do probabilistic reasoning according to items 1, 2, 3, above (also items 4 or 5 if youe are doing the bonus part), and report the results.
            Quit.
         * 
         */
        boolean running = true;

        while(running){
            String str = s.nextLine();
            String[] tokens = str.toLowerCase().split(" ");

            if(tokens.length == 0)
                continue;

            String head = tokens[0];

            switch (head) {
                case "reset":
                    evidence.emptyEvidence();
                    break;
                case "add":
                    handleAddingToEvidence(tokens);
                    break;
                case "reason":
                    performReasoningOnEvidence(tokens);
                    break;
                case "quit":
                    running = false;
                    break;
                default:
                    System.out.println("Unrecognized operation: " + head + "\n\n");
                    break;
            }

        }  
        s.close();  
    }

    /*  add season <value> (low/medium/high)
     *  add package <value> (exist / absent) <Vi> (V1, V2, V3...)
     *  add blockage <value> (exist / absent) <Vi> (V1, V2, V3...) <Vj>  (V1, V2, V3...)
     */
    private void handleAddingToEvidence(String[] tokens){
        String type = tokens[1];

        switch(type){
            case "season":
                addSeasonToEvidence(tokens);
                break;
            case "package":
                addPackageToEvidence(tokens);
                break;
            case "blockage":
                addBlockageToEvidence(tokens);
                break;
            default:
                System.out.println("Illigal command: " + tokens[0] + " " + tokens[1]);
                break;
        }
    }
    
    private void addSeasonToEvidence(String[] tokens){
        switch (tokens[2]) {
            case "low":
                evidence.addSeasonToEvidence(SEASON_VALUE.LOW);
                break;
            case "medium":
                evidence.addSeasonToEvidence(SEASON_VALUE.MEDIUM);
                break;
            case "high":
                evidence.addSeasonToEvidence(SEASON_VALUE.HIGH);
                break;
        
            default:
            System.out.println("Illigal command: " + tokens[0] + " " + tokens[1] + " " + tokens[2]);
        }
    }

    private void addPackageToEvidence(String[] tokens){
        int v = Integer.parseInt(tokens[3].substring(1));

        switch (tokens[2]) {
            case "exist":
                evidence.addPackageToEvidence(v, PACKAGE_VALUE.EXIST);
                break;
            case "absent":
                evidence.addPackageToEvidence(v, PACKAGE_VALUE.ABSENT);
                break;
            default:
                System.out.println("Illigal command: " + tokens[0] + " " + tokens[1] + " " + tokens[2]);
        }
    }

    private void addBlockageToEvidence(String[] tokens){
        int v1 = Integer.parseInt(tokens[3].substring(1));
        int v2 = Integer.parseInt(tokens[4].substring(1));

        switch (tokens[2]) {
            case "exist":
                evidence.addEdgeBlockageToEvidence(v1, v2, BLOCKAGE_VALUE.BLOCKED);
                break;
            case "absent":
                evidence.addEdgeBlockageToEvidence(v1, v2, BLOCKAGE_VALUE.FREE);
                break;
            default:
                System.out.println("Illigal command: " + tokens[0] + " " + tokens[1] + " " + tokens[2]);
        }
    }


    // reason <value> (vertices / edges / season / BONUS)
    private void performReasoningOnEvidence(String[] tokens){
        String type = tokens[1];

        switch (type) {
            case "vertices":
                reasonVertecies();
                break;
            case "edges":
                reasonEdgesBlocked();
                break;
            case "season":
                reasonSeason();
                break;
            case "path":
                reasonCertainPath(tokens);
                break;
            case "bestpath":
                reasonBestPath(tokens);
                break;
            default:
                System.out.println("illegal command: " + tokens[0] + " " + tokens[1]);
                break;
        }
    }

    private void reasonVertecies(){
        Node seasonNode = bn.getRoot().getChildAtIndex(0);
        double[][] verteciesDistribution = new double[this.graphSize-1][2];

        for(int v = 0; v < this.graphSize - 1; v++){
            Node vertexNode = seasonNode.getChildAtIndex(v);
            double[] ditribution = enumerationAsk(vertexNode, evidence, bn);
            verteciesDistribution[v] = ditribution;
        }
        printVerteciesDistribution(verteciesDistribution);
    }

    private void reasonEdgesBlocked(){
        //using topoligic sort to get the edges
        List<Node> nodes = this.bn.getEdgeNodesList();

        double[][] blockageDistribution = new double[nodes.size()][2];
        for(int i = 0; i < nodes.size(); i++){
            Node edgeNode = nodes.get(i);
            double[] distribution = enumerationAsk(edgeNode, evidence, bn);
            blockageDistribution[i] = distribution;
        }

        printBlockageDistribution(blockageDistribution);
    }

    private void reasonSeason(){
        Node seasonNode = bn.getRoot().getChildAtIndex(0);
        double[] ditribution = enumerationAsk(seasonNode, evidence, bn);
    
        PrintSeasonDistribution(ditribution);
    }

    /*  A path is blocked if any of the edges it contain is blocked.
     *  the distributions of blockages in edges are NOT necessarily independent, so we'll use conditional probability
     * 
     *  Example:
     *      for a path V1-V2-V3-V4
     *      p(The path is not blocked) = P(V1-V2 is free | Evidence) * P(V2-V3 is free | Evidence, V1-V2 is free) * 
     *          P(V3-V4 is free | Evidence, V1-V2 and V2-V3 are free) 
     */
    private void reasonCertainPath(String[] tokens){
        if(tokens.length <= 4 ){
            System.out.println("A path must consist of at least two vertices!");
            return;
        }

        List<Integer> indices = getIndicesFromTokens(tokens);
        double pathNotBlockedProbability = calculateFreePathProbability(indices);

        System.out.println("The given path is free with probability of " + pathNotBlockedProbability + " given the evidence.\n");
    }

    private double calculateFreePathProbability(List<Integer> indices){
        Evidence extendedEvidence = new Evidence(this.evidence);
        List<Node> nodes = this.bn.getEdgeNodesList();

        double pathNotBlockedProbability = 1.0;

        for(int i = 0; i < nodes.size(); i++){
            Node edgeNode = nodes.get(i);
            if(  ! checkEdgeRelevance(edgeNode, indices))
                continue;

            double[] distribution = enumerationAsk(edgeNode, extendedEvidence, bn);
            pathNotBlockedProbability *= distribution[1];

            if(pathNotBlockedProbability == 0)
                break;

            extendedEvidence.addEdgeBlockageToEvidence(edgeNode.getIthIndex(0), edgeNode.getIthIndex(1), BLOCKAGE_VALUE.FREE);
        }

        return pathNotBlockedProbability;
    }

    private boolean checkEdgeRelevance(Node edgeNode, List<Integer> indices){
        int v1 = edgeNode.getIthIndex(0);
        int v2 = edgeNode.getIthIndex(1);
        
        int i1 = indices.indexOf(v1);
        int i2 = indices.indexOf(v2);

        if(i1 == -1 || i2 == -1)
            return false;
        
        return Math.abs(i1 - i2) == 1;
    }

    private void reasonBestPath(String[] tokens){
        int startIndex = Integer.parseInt(tokens[2].substring(1));
        int goalIndex = Integer.parseInt(tokens[3].substring(1));

        List<List<Integer>> allPathsList = findAllSimplePaths(startIndex, goalIndex);
        
        double maxProbabilityOfBeingFreePath = -1.0;
        List<Integer> maxProbablityPath = null;

        for(List<Integer> curr : allPathsList){
            double currPathisFreeProbability = calculateFreePathProbability(curr);

            if(currPathisFreeProbability > maxProbabilityOfBeingFreePath){
                maxProbabilityOfBeingFreePath = currPathisFreeProbability;
                maxProbablityPath = curr;
            }
        }

        if(maxProbablityPath == null){
            System.out.println("There is no path between V" + startIndex +" and V" + goalIndex + " \n");
        } else {
            System.out.println("The path with the highest probability of being free is:\n");
            
            boolean isFirst = true;
            for(Integer i :maxProbablityPath){
                if(isFirst){
                    isFirst = false;
                    System.out.print("V" + i);
                } else {
                    System.out.print(" -> V" + i);
                }
            }
            System.out.println("\nWith a probability of " + maxProbabilityOfBeingFreePath);
        }
    }

    /*  Using DFS with backtracking to get all the simple paths between start and goal
     *  maximum length of such path is |V|
     */
    public List<List<Integer>> findAllSimplePaths(int v1, int v2) {
        List<List<Integer>> allPaths = new ArrayList<>();
        boolean[] visited = new boolean[edgeWeights.length];
        List<Integer> path = new ArrayList<>();
        
        DFS(v1, v2, visited, path, allPaths);
        return allPaths;
    }

    private void DFS(int current, int target, boolean[] visited,  List<Integer> path, List<List<Integer>> allPaths) {
        path.add(current);
        visited[current] = true;

        if (current == target) {
            allPaths.add(new ArrayList<>(path));
        } else {
            for (int neighbor = 0; neighbor < edgeWeights.length; neighbor++) {
                if (edgeWeights[current][neighbor] != -1 && !visited[neighbor]) { 
                    DFS(neighbor, target, visited, path, allPaths);
                }
            }
        }

        //backtrack
        visited[current] = false;
        path.remove(path.size() - 1);
    }


    private void printVerteciesDistribution(double[][] dist){
        System.out.println("*** Vertices Distribution: ***");
        for(int i = 0; i < dist.length; i++){
            System.out.println("\tVertex V" + (i+1));
            System.out.println("\t\tP(packge | evidence) = " + dist[i][PACKAGE_VALUE.EXIST.ordinal()]);
            System.out.println("\t\tP(no packge | evidence) = " + dist[i][PACKAGE_VALUE.ABSENT.ordinal()] + "\n");
        }
    }

    private void printBlockageDistribution(double[][] dist){
        System.out.println("*** Edges: ***");

        int edgeCounter = 0;

        for(int i = 1; i < this.edgeWeights.length; i++){
            for(int j = 1; j < i; j++){
                if(this.edgeWeights[i][j] < 0 || !this.isFragileEdge[i][j])
                    continue;
                
                System.out.println("\tEdge V" + j + ", V" + i + ":");
                System.out.println("\t\tP(blocked | evidence) = " + dist[edgeCounter][0]);
                System.out.println("\t\tP(not blocked | evidence) = " + dist[edgeCounter][1] + "\n");

                edgeCounter++;
            }
        }
    }

    private double[] enumerationAsk(Node X, Evidence e, BayesNetwork bn){
        double[] distribution;
        char type = X.getDescription().charAt(0);

        switch (type) {
            case 'S':
                distribution = new double[3];
                break;
            case 'V':
                distribution = new double[2];
                break;
            case 'E':
                distribution = new double[2];
                break;
            default:
                System.out.println("Illigal enumaration attempt: " + X.getDescription());
                return null;
        }

        //check if the query is already in the evidence
        switch (type) {
            case 'S':
                if(e.isSeasonKnown()){
                    distribution[e.getSeasonValue().ordinal()] = 1.0;
                    return distribution;
                }
                break;
            case 'V':
                int index = X.getIthIndex(0);
                if(e.isPackageAtVertexKnown(index)){
                    distribution[e.getPackageValueAtVertex(index).ordinal()] = 1.0;
                    return distribution;
                }
                break;
            case 'E':
                int v1 = X.getIthIndex(0);
                int v2 = X.getIthIndex(1);
            
                if(e.isEdgeBlockedKnown(v1, v2)){
                    distribution[e.getBlockageValue(v1, v2).ordinal()] = 1.0;
                    return distribution;
                }
                break;
        }

        //calculate the distrubiton of every value in the domain
        for(int i = 0; i < distribution.length; i++){

            Evidence extendedEvidence = new Evidence(e);
            switch (type) {
                case 'S':
                    extendedEvidence.addSeasonToEvidence((SEASON_VALUE)values[Value.SEASON][i]);
                    break;
                case 'V':
                    extendedEvidence.addPackageToEvidence(X.getIthIndex(0), (PACKAGE_VALUE)values[Value.VERTEX][i]);
                    break;
                case 'E':
                    extendedEvidence.addEdgeBlockageToEvidence(X.getIthIndex(0), X.getIthIndex(1), 
                        (BLOCKAGE_VALUE)values[Value.BLOCKAGE][i]);
                    break;
            }
            List<Node> vars = this.bn.getTopologicalSortOnItems();
            this.bn.removeBarrenNodes(vars, extendedEvidence);
            distribution[i] = enumerateAll(vars, extendedEvidence);
        }

        //normalize distribution
        Double total = 0.0;

        for(int i = 0; i < distribution.length; i++)
            total += distribution[i];

        //check for division with zero
        if(total == 0)
            return distribution;
        
        for(int i = 0; i < distribution.length; i++)
            distribution[i] = distribution[i] / total;

        return distribution;
    }

    private Double enumerateAll(List<Node> vars, Evidence e){
        if(vars.size() == 0)
            return 1.0;
        
        Node y = vars.remove(0);

        //check if y is already known
        char type = y.getDescription().charAt(0);
        switch (type) {
            case 'S':
                if(e.isSeasonKnown()){
                    Double yProbability =  this.seasonProbability[e.getSeasonValue().ordinal()];
                    List<Node> newVars = deepCopyNodeList(vars);
                    return yProbability * enumerateAll(newVars, e);
                }
                break;
            case 'V':
                int index = y.getIthIndex(0);
                if(e.isPackageAtVertexKnown(index)){
                    Double yProbability = this.packageProbability[index][e.getSeasonValue().ordinal()];
                    if(e.getPackageValueAtVertex(index) == PACKAGE_VALUE.ABSENT)
                        yProbability = 1 - yProbability; 

                    List<Node> newVars = deepCopyNodeList(vars);
                    return yProbability * enumerateAll(newVars, e);
                }
                break;
            case 'E':
                int v1 = y.getIthIndex(0);
                int v2 = y.getIthIndex(1);
            
                if(e.isEdgeBlockedKnown(v1, v2)){
                    int packagesCounter = 0;
                    if(e.getPackageValueAtVertex(v1) == PACKAGE_VALUE.EXIST)
                        packagesCounter++;
                    if(e.getPackageValueAtVertex(v2) == PACKAGE_VALUE.EXIST)
                        packagesCounter++;

                    Double yProbability =  this.blockageProbability[y.getIthIndex(2) - 1][packagesCounter];
                    if(e.getBlockageValue(v1, v2) == BLOCKAGE_VALUE.FREE)
                        yProbability = 1 - yProbability;

                    List<Node> newVars = deepCopyNodeList(vars);
                    return yProbability * enumerateAll(newVars, e);
                }
                break;
        }

        //if y is unknown
        Double sum = 0.0;
        switch (type) {
            case 'S':
                for(SEASON_VALUE sv : SEASON_VALUE.values()){
                    if(sv == SEASON_VALUE.UNKNOWN)
                        continue;

                    Evidence eY = new Evidence(e);
                    eY.addSeasonToEvidence(sv);

                    Double yProbability =  this.seasonProbability[eY.getSeasonValue().ordinal()];

                    //optimization to stop when the probability we are working with equl zero
                    if(yProbability == 0)
                        continue;

                    List<Node> newVars = deepCopyNodeList(vars);
                    sum += yProbability * enumerateAll(newVars, eY);
                }
                return sum;
            case 'V':
                for(PACKAGE_VALUE pv : PACKAGE_VALUE.values()){
                    if(pv == PACKAGE_VALUE.UNKNOWN)
                        continue;
                    
                    Evidence eY = new Evidence(e);
                    eY.addPackageToEvidence(y.getIthIndex(0), pv);
                    
                    Double yProbability =  this.packageProbability[y.getIthIndex(0)][eY.getSeasonValue().ordinal()];
                    if(eY.getPackageValueAtVertex(y.getIthIndex(0)) == PACKAGE_VALUE.ABSENT)
                        yProbability = 1 - yProbability;
                    
                    //optimization to stop when the probability we are working with equal zero
                    if(yProbability == 0)
                        continue;

                    List<Node> newVars = deepCopyNodeList(vars);
                    sum += yProbability * enumerateAll(newVars, eY);
                }
                return sum;
            case 'E':
                int v1 = y.getIthIndex(0);
                int v2 = y.getIthIndex(1);
                
                for(BLOCKAGE_VALUE bv : BLOCKAGE_VALUE.values()){
                    if(bv == BLOCKAGE_VALUE.UNKNOWN)
                        continue;
                    
                    Evidence eY = new Evidence(e);
                    eY.addEdgeBlockageToEvidence(v1, v2, bv);

                    int packagesCounter = 0;
                    if(eY.getPackageValueAtVertex(v1) == PACKAGE_VALUE.EXIST)
                        packagesCounter++;
                    if(eY.getPackageValueAtVertex(v2) == PACKAGE_VALUE.EXIST)
                        packagesCounter++;

                    Double yProbability =  this.blockageProbability[y.getIthIndex(2) - 1][packagesCounter]; 
                    if(bv == BLOCKAGE_VALUE.FREE)
                        yProbability = 1 - yProbability;

                    //optimization to stop when the probability we are working with equl zero
                    if(yProbability == 0)
                        continue;

                    List<Node> newVars = deepCopyNodeList(vars);
                    sum += yProbability * enumerateAll(newVars, eY);
                }
                return sum;
            
            default:
                return Config.UNDEFINED_PROBABILITY;
        }
    }

    
    private void initialPrintout(){
        initialPrintBayesNetworkSetup();
        PrintSeasonDistribution(this.seasonProbability);
        initialPrintouPackagesOnVerteciesDistribution(this.packageProbability);
        initialPrintBlockageDistributin();
    }

    private void initialPrintBayesNetworkSetup(){
        System.out.println("==============     Bayes Network   ==============");
        System.out.println("Global Leakage L: " + this.globalLeakage);
        System.out.println("Distribution paremeter Q: " + this.distributionParemeter+"\n");
    }

    private void PrintSeasonDistribution(double[] arr){
        System.out.println("*** SEASON: ***");
        System.out.println("\tP(low | evidence) = " + arr[Config.SEASON_LOW]);
        System.out.println("\tP(medium | evidence) = " + arr[Config.SEASON_MEDIUM]);
        System.out.println("\tP(high | evidence) = " + arr[Config.SEASON_HIGH] + "\n");
    }

    private void initialPrintouPackagesOnVerteciesDistribution(double[][] arr){
        System.out.println("*** VERTECIES: ***");
        for(int i = 1; i < this.packageProbability.length; i++){
            if(this.packageProbability[i][Config.SEASON_LOW] == 0)
                continue;
            
            System.out.println("\tVertex V" + i);
            System.out.println("\t\tP(packge | low) = " + arr[i][Config.SEASON_LOW]);
            System.out.println("\t\tP(packge | medium) = " + arr[i][Config.SEASON_MEDIUM]);
            System.out.println("\t\tP(packge | high) = " + arr[i][Config.SEASON_HIGH] + "\n");
        }
    }

    private void initialPrintBlockageDistributin(){        
        System.out.println("*** Edges: ***");
        String s = "package at V";

        for(Node node : this.bn.getEdgeNodesList()){
            int v1 = node.getIthIndex(0);
            int v2 = node.getIthIndex(1);
            int index = node.getIthIndex(2);

            System.out.println("\tEdge V" + v1 + ", V" + v2 + ":");
            System.out.println("\t\tP(blocked | no " + s + v1 +", no " + s + v2 + " ) = " + this.blockageProbability[index - 1][0]);
            System.out.println("\t\tP(blocked | no " + s + v1 +", " + s + v2 + " ) = " + this.blockageProbability[index - 1][1]);
            System.out.println("\t\tP(blocked | " + s + v1 +", no " + s + v2 + " ) = " + this.blockageProbability[index - 1][1]);
            System.out.println("\t\tP(blocked | " + s + v1 +", " + s + v2 + " ) = " + this.blockageProbability[index - 1][2] + "\n");
        }
    }


    
    //constructor
    public Enviroment(File input) throws Exception{
        
        try {
            Scanner scanner = new Scanner(input);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                
                if(line.length() == 0 || line.charAt(0) == ';' || line.charAt(0) == ' ')
                    continue;

                String head = line.substring(0, 2);

                if(head.equals(InputFileTemplate.VERTICIES_COUNT))
                    handleN(line);
                else if(head.equals(InputFileTemplate.EDGE))
                    handleE(line);
                else if(head.equals(InputFileTemplate.PACKAGE))
                    handleP(line);
                else if(head.equals(InputFileTemplate.BLOCKED_EDGE))
                    handleB(line);
                else if(head.equals(InputFileTemplate.FRAGILE_EDGE))
                    handleF(line);
                else if(head.equals(InputFileTemplate.SEASON_DISTRIBUTION))
                    handleS(line);
                else if(head.equals(InputFileTemplate.GLOBAL_LEAKAGE_PROBABILITY))
                    handleL(line);
                else if(head.equals(InputFileTemplate.DISTRIBUTION_PARAMETER))
                    handleQ(line);
            }

            scanner.close();

            //post processing
            blockageProbability = new double[fragileEdgeCount][3];
            List<Node> edgeNodes = this.bn.getEdgeNodesList();

            for(Node node : edgeNodes){
                int index = node.getIthIndex(2);
                
                Double qi = this.distributionParemeter * this.edgeWeights[node.getIthIndex(0)][node.getIthIndex(1)];

                blockageProbability[index - 1][0] =  this.globalLeakage;    //both packages are absent
                blockageProbability[index - 1][1] = 1.0 - (qi);             //only one package exist
                blockageProbability[index - 1][2] = 1.0 - (qi * qi);        //both packages exist
            }

            evidence = new Evidence(this);

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            if(graphSize == 0){
                throw new Exception("illegal input, must provide a graph size using #n <NUMBER>");
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
        isFragileEdge = new boolean[graphSize][graphSize];
        packageProbability = new double[graphSize][3];

        for(int i = 0; i < graphSize; i++){
            packageProbability[i][Config.SEASON_LOW] = Config.SEASON_ZERO_PROBABILITY_PACKAGE;
            packageProbability[i][Config.SEASON_MEDIUM] = Config.SEASON_ZERO_PROBABILITY_PACKAGE;
            packageProbability[i][Config.SEASON_HIGH] = Config.SEASON_ZERO_PROBABILITY_PACKAGE;

            for(int j=0; j<graphSize; j++){
                edgeWeights[i][j] = Config.EDGE_NO_EDGE;
                isFragileEdge[i][j] = Config.EDGE_NON_FRAGILE;
            }
        }

        //Bayes network
        //season
        int[] indexesSeason = {1};
        Node seasonNode = new Node("S1", indexesSeason);
        bn.addNode(bn.getRoot(), seasonNode);

        //vertecies
        for(int i = 1; i < graphSize; i++){
            int[] indexes = {i};
            Node vertexNode = new Node("V" + Integer.toString(i), indexes);
            bn.addNode(seasonNode, vertexNode);
        }

        //edges will be added on handleE
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

        /*     we only model fragile edges in the BN. see mail from Eyal:

                        In the BN you model only edges that CAN be fragile,
                        and no others, which means they have to be in the input graph
                        or you somply do not have a variable for them.
        */
    }
    
    //#P1 V2 0.2        ; Vertex V2 probability of package given low demand season 0.2
    private void handleP(String str) throws Exception{
        str = str.substring(5);
        Scanner s = new Scanner(str);
        s.useDelimiter("[\\s;]+");

        int vertexID = s.nextInt();

        double probability = Config.SEASON_ZERO_PROBABILITY_PACKAGE;
        if(s.hasNextDouble()){
            probability = s.nextDouble();
        }
        s.close();

        if(probability < 0 || probability > 1)
            throw new Exception("Probability must be a decimal number between 0 and 1");

        packageProbability[vertexID][SEASON_VALUE.LOW.ordinal()] = Math.min(probability, 1);
        packageProbability[vertexID][SEASON_VALUE.MEDIUM.ordinal()] = Math.min(probability * 2, 1);
        packageProbability[vertexID][SEASON_VALUE.HIGH.ordinal()] = Math.min(probability * 4, 1);
    }

    // #B V1 V5            ; Edge from V1 to V5 is always blocked
    private void handleB(String str){
        Scanner s = new Scanner(str);

        int v1 = s.useDelimiter("\\D+").nextInt();
        int v2 = s.useDelimiter("\\D+").nextInt();

        s.close();

        edgeWeights[v1][v2] = Config.EDGE_BLOCKED;
        edgeWeights[v2][v1] = Config.EDGE_BLOCKED;

        isFragileEdge[v1][v2] = Config.EDGE_NON_FRAGILE;
        isFragileEdge[v2][v1] = Config.EDGE_NON_FRAGILE;
    }

    // #F V1 V2            ; Edge from V1 to V2 is fragile (can only be traversed once)
    private void handleF(String str){
        Scanner s = new Scanner(str);

        int v1 = s.useDelimiter("\\D+").nextInt();
        int v2 = s.useDelimiter("\\D+").nextInt();

        s.close();

        isFragileEdge[v1][v2] = Config.EDGE_FRAGILE;
        isFragileEdge[v2][v1] = Config.EDGE_FRAGILE;

        //Bayes Network model only fragile edges (non-fragile are guarantee )
        fragileEdgeCount++;
        int[] indexes = {v1, v2, fragileEdgeCount};
        Node edgeNode = new Node("E" + Integer.toString(fragileEdgeCount), indexes);

        Node v1Node = bn.getRoot().getChildAtIndex(0).getChildAtIndex(v1-1);
        Node v2Node = bn.getRoot().getChildAtIndex(0).getChildAtIndex(v2-1);

        this.bn.addNode(v1Node, edgeNode);
        this.bn.addNode(v2Node, edgeNode);
    }


    //#L 0.1         ; Global leakage probability 0.1
    //#L             ; using default global leakage
    private void handleL(String str){
        str = str.substring(3);
        Scanner s = new Scanner(str);
        s.useDelimiter("[\\s;]+");

        double leakage = Config.LEAKAGE_DEFAULT_GLOBAL_LEAKAGE;
        if(s.hasNextDouble()){
            leakage = s.nextDouble();
        }

        this.globalLeakage = leakage;

        s.close();
    }

    //#Q 0.2         ; Parameter Q is 0.2
    //#Q             ; Paremeter is using the default value
    private void handleQ(String str){
        str = str.substring(3);
        Scanner s = new Scanner(str);
        s.useDelimiter("[\\s;]+");

        double distribution = Config.Q_DEFAULT_DISTRIBUTION_PAREMETER;
        if(s.hasNextDouble()){
            distribution = s.nextDouble();
        }
        this.distributionParemeter = distribution;

        s.close();
    }

    //#S 0.1 0.4 0.5    ; Prior distribution over season: 0.1 for low, 0.4 for medium, 0.5 for high
    //#S                ; using default values
    private void handleS(String str){
        str = str.substring(3);
        Scanner s = new Scanner(str);

        double low = Config.SEASON_LOW_DEFAULT_PROBABILITY;
        double medium = Config.SEASON_MEDIUM_DEFAULT_PROBABILITY;
        double high = Config.SEASON_HIGH_DEFAULT_PROBABILITY;

        if(s.hasNextDouble())
            low = s.nextDouble();
        if(s.hasNextDouble())
            medium = s.nextDouble();
        if(s.hasNextDouble())
            high = s.nextDouble();

        this.seasonProbability[Config.SEASON_LOW] = low;
        this.seasonProbability[Config.SEASON_MEDIUM] = medium;
        this.seasonProbability[Config.SEASON_HIGH] = high;

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

    //utils
    public List<Node> deepCopyNodeList(List<Node> list){
        List<Node> newList = new ArrayList<Node>();

        for(Node node : list){
            Node newNode = new Node(node);
            newList.add(newNode);
        }
        
        return newList;
    }

    private List<Integer> getIndicesFromTokens(String[] tokens){
        List<Integer> list = new ArrayList<Integer>();
        
        for(int i = 2; i < tokens.length; i++){
            list.add(Integer.parseInt(tokens[i].substring(1)));
        }
        
        return list;
    }

}

/*  Assumptions on the input text file to make the life easier
    0. Assumes input validity (e.g. no stuff like #N with no number afterwards)
 *  1. Each row contains only one command, and each parameter is sepearted by a single space ( )
 *  2. If a row contains a comment (;), there's at least one space before the command and the ;
*/