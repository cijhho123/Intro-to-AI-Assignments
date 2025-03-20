package src.Env;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import src.Graph.AdjacencyMatrixGraph;
import src.Graph.Graph;
import src.Player.AStarPlayer;
import src.Player.GreedyPlayer;
import src.Player.HumanPlayer;
import src.Player.InterferingPlayer;
import src.Player.Player;
import src.Player.RealTimeAStarPlayer;
import src.Player.StupidGreedyPlayer;
import src.Graph.Package;
import src.Graph.Vertex;
import src.Tests.InputFileTemplate;

public class Enviroment {
    private int globalTime; 
    private static int MAX_DEADLINE = 0;

    private int playerCount; 
    private List<Player> players;  

    private List<Package> packages;
    private List<Package> pendinPackages;

    public Graph graph;

    
    public void gameLoop(){
        while(Main.RUNNING){
            //update game state
            updatePendingPackages();

            //display game info
            displayInfo();

            //move the players one by one in order
            for(Player p : players)
                p.travers();

            advanceEnvTime();
            gameOverCheck();
        }

        playerClosingMove();
        displayGameOverScreen();
    }

    private void displayInfo(){
        System.out.println("\n==========================================");
        System.out.println("Global time: " + globalTime);

        System.out.println("");

        for(int i = 0; i < players.size(); i++){
            Player curr = players.get(i);
            System.out.print("\nPlayer #" + (i+1) +" "+ curr.playerTypeString() +" in ");
            if(curr.getBusyTime() > 1)
                System.out.print(curr.getBusyTime() + " units of time will arive to ");
            System.out.print(" Location: V" + curr.getLocation().getIndex() + " Score: " + curr.getScore());
        }

        System.out.println("");
    }

    private void advanceEnvTime(){
        globalTime++;
        this.graph.advanceLocalTime(1);
    }

    public void printPackageInfo(){
        for (Package p : packages) {
            System.out.print("\nPackage #" + p.getId() + " status: " + p.getStatus());
            Player e = p.getPlayer(); 
            if(e != null)
                System.out.print(" carried by: " + (e.getId()) + " to destination " + p.getDestinationLocationIndex());
            else 
                System.out.print(" at location V" + p.getStartingLocationIndex());
            System.out.print(" with a deadline of " + p.getDeadline() + "\n");
        }
    }

    public void displayGameOverScreen(){
        System.out.println("\n\nThe game is over!\n**********   Score table:    **********");
        for(int i=0; i<playerCount; i++){
            System.out.println("Player " + i + " of type " + players.get(i).playerTypeString() + 
                " with score: " + players.get(i).getScore());
        }
    }

    private void gameOverCheck(){   
        if(Enviroment.MAX_DEADLINE < this.globalTime)
            Main.RUNNING = false;

        //check if players have any more deliverable packages
        for(Player p : this.players){
            for(Package pkg : p.getPackages()){
                if(pkg.getDeadline() > this.globalTime && this.graph.isAccessible(p.getLocation().getIndex(), pkg.getDestinationLocationIndex()))
                    return;
            }
        }

        //check for unclaimed packages
        for(Package pkg : this.packages){
            if(pkg.getStatus() == Status.NOT_SPAWNED || pkg.getStatus() == Status.PENDING){
                for(Player p : this.players){
                    if(p.isDeliveringAgent() && this.graph.isAccessible(p.getLocation().getIndex(), pkg.getStartingLocationIndex()) &&
                    this.graph.isAccessible(p.getLocation().getIndex(), pkg.getDestinationLocationIndex()))
                        return;
                }
            }
        }

            Main.RUNNING = false;
    }

    private void playerClosingMove(){
        for(Player p : this.players){
            p.travelLogic();
        }
    }

    public List<Player> getPlayers(){
        return this.players;
    }

    private void updatePendingPackages(){
        for(int i = 0; i < pendinPackages.size(); i++){
            if(pendinPackages.get(i).attemptPublishPackage()){
                pendinPackages.remove(i);
                i--;
            }
        }
    }

    public List<Package> getPackages(){
        return this.packages;
    }

    public int getCurrentTime() {
        return globalTime;
    }

    // Constructor
    public Enviroment(File input) throws Exception{
        globalTime = 0;

        playerCount = 0;    //max players: 3
        players = new ArrayList<>();  

        packages = new ArrayList<>();
        pendinPackages = new ArrayList<>();

        graph = null;

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
                else if(head.equals(InputFileTemplate.BLOCKED_EDGE))
                    handleB(line);
                else if(head.equals(InputFileTemplate.FRAGILE_EDGE))
                    handleF(line);
                else if(head.equals(InputFileTemplate.STUPID_GREEDY_AGENT))
                    handleA(line);
                else if(head.equals(InputFileTemplate.HUMAN_AGENT))
                    handleH(line);
                else if(head.equals(InputFileTemplate.INTERFERING_AGENT))
                    handleI(line);
                else if(head.equals(InputFileTemplate.GREEDY_AGENT))
                    handleG(line);
                else if(head.equals(InputFileTemplate.A_STAR_AGENT))
                    handleS(line);
                else if(head.equals(InputFileTemplate.REAL_TIME_A_STAR_AGENT))
                    handleR(line);
            }

            scanner.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(graph == null){
                throw new Exception("Illigal input, must provide a graph size using #n <NUMBER>");
            }
    }

    // #N <INT> - number of vertices N in graph (from V1 to Vn)
    private void handleN(String str) throws Exception{
        if(graph != null)
            throw new Exception("Illigal input, can't provide more than one size for the graph. line: \n" + str);

        Scanner s = new Scanner(str);
        int size = s.useDelimiter("\\D+").nextInt();
        s.close();

        this.graph = new AdjacencyMatrixGraph(size + 1, this);    // +1 to have V1...Vn
    }

    // #E1 1 2 W1  -  Edge 1 from vertex 1 to vertex 2, weight 1
    private void handleE(String str) throws Exception{
        if(graph == null)
            throw new Exception("Invalid input, must create a graph with #N before adding edges with #E !");

        Scanner s = new Scanner(str);

        @SuppressWarnings("unused") //for backwards compatabillity 
        int e = s.useDelimiter("\\D+").nextInt();
        int v1 = s.useDelimiter("\\D+").nextInt();
        int v2 = s.useDelimiter("\\D+").nextInt();
        int w = s.useDelimiter("\\D+").nextInt();

        s.close();

        graph.addEdge(v1, v2, w);
    }
    
    //#P1 V2 0 D V5 10  ; Package 1 at V2 from time 0, deliver to V5 on or before time 10
    private void handleP(String str){
        Scanner s = new Scanner(str);

        int p = s.useDelimiter("\\D+").nextInt();

        int v_src = s.useDelimiter("\\D+").nextInt();
        int start_time = s.useDelimiter("\\D+").nextInt();
        
        int v_dst = s.useDelimiter("\\D+").nextInt();
        int end_time = s.useDelimiter("\\D+").nextInt();

        Enviroment.MAX_DEADLINE = Math.max(Enviroment.MAX_DEADLINE, end_time);
        
        s.close();

        Package p1 = new Package(graph.getVertex(v_src), graph.getVertex(v_dst), start_time, end_time, p, this.graph);
        graph.getVertex(v_src).addPackage(p1);
        this.packages.add(p1);

        pendinPackages.add(p1);
    }

    // #B V1 V5            ; Edge from V1 to V5 is always blocked
    private void handleB(String str){
        Scanner s = new Scanner(str);

        int v1 = s.useDelimiter("\\D+").nextInt();
        int v2 = s.useDelimiter("\\D+").nextInt();

        s.close();

        graph.blockEdge(v1,v2);

    }

    // #F V1 V2            ; Edge from V1 to V2 is fragile (can only be traversed once)
    private void handleF(String str){
        Scanner s = new Scanner(str);

        int v1 = s.useDelimiter("\\D+").nextInt();
        int v2 = s.useDelimiter("\\D+").nextInt();

        s.close();

        graph.setFragileEdge(v1,v2);

    }

    //#A V1               ; Normal agent starts at V1
    private void handleA(String str){
        Scanner s = new Scanner(str);

        int locationIndex = s.useDelimiter("\\D+").nextInt();
        Vertex vertex = this.graph.getVertex(locationIndex);
        s.close();

        Player p1 = new StupidGreedyPlayer(this.graph, vertex, playerCount);
        this.players.add(p1);
        playerCount ++;
    }

    //#H V4               ; "Human" agent  starts at V2
    private void handleH(String str){
        Scanner s = new Scanner(str);
        int locationIndex = s.useDelimiter("\\D+").nextInt();
        s.close();

        Vertex vertex = this.graph.getVertex(locationIndex);

        Player p1 = new HumanPlayer(this.graph, vertex, playerCount);
        this.players.add(p1);
        playerCount ++;
    }

    // #I V5               ; Interfering agent  starts at V5
    private void handleI(String str){
        Scanner s = new Scanner(str);

        int locationIndex = s.useDelimiter("\\D+").nextInt();
        Vertex vertex = this.graph.getVertex(locationIndex);
        s.close();

        Player p1 = new InterferingPlayer(this.graph, vertex, playerCount);
        this.players.add(p1);
        playerCount ++;
    }

    // #G V5               ; Greedy agent  starts at V5
    private void handleG(String str){
        Scanner s = new Scanner(str);

        int locationIndex = s.useDelimiter("\\D+").nextInt();
        Vertex vertex = this.graph.getVertex(locationIndex);
        s.close();

        Player p1 = new GreedyPlayer(this.graph, vertex, playerCount);
        this.players.add(p1);
        playerCount ++;
    }

    // #S V5 2500               ; A* agent start in V5 with limit of 2500 exapnsions 
    // #S V10                   ; A* agent start in V5 with default expansion limit (default 10000)
    private void handleS(String str){
        Scanner s = new Scanner(str);

        int locationIndex = s.useDelimiter("\\D+").nextInt();
        Vertex vertex = this.graph.getVertex(locationIndex);

        int limit = AStarPlayer.DEFAULT_LIMIT;
        if(s.hasNextInt()){
            limit = s.useDelimiter("\\D+").nextInt();
        }

        s.close();

        Player p1 = new AStarPlayer(this.graph, vertex, playerCount, limit);
        this.players.add(p1);
        playerCount ++;
    }

    // #R V2 30                 ; Real-Time A* agent start in V2 with limit of 30 exapnsions before making a decision
    // #R v6                    ; Real-Time A* agent start in V2 with default limit (default L = 10)  
    private void handleR(String str){
        Scanner s = new Scanner(str);

        int locationIndex = s.useDelimiter("\\D+").nextInt();
        Vertex vertex = this.graph.getVertex(locationIndex);

        int limit = RealTimeAStarPlayer.DEFAULT_EXPANSIONS;
        if(s.hasNextInt()){
            limit = s.useDelimiter("\\D+").nextInt();
        }

        s.close();

        Player p1 = new RealTimeAStarPlayer(this.graph, vertex, playerCount, limit);
        this.players.add(p1);
        playerCount ++;
    }
}

/*  Assumptions on the input text file to make the life easier
    0. Assumes input validity (e.g. no stuff like #N with no number afterwards)
 *  1. Each row contains only one command, and each parameter is sepearted by a single space ( )
 *  2. If a row contains a comment (;), there's at least one space before the command and the ;
 */