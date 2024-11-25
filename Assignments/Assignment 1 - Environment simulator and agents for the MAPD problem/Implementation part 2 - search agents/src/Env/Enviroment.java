package src.Env;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import src.Graph.AdjacencyMatrixGraph;
import src.Graph.Graph;
import src.Player.HumanPlayer;
import src.Player.InterferingPlayer;
import src.Player.Player;
import src.Player.StupidGreedyPlayer;
import src.Graph.Package;
import src.Graph.Vertex;

public class Enviroment {
    public static int globalTime; 

    public static final int PLAYERS_CAP = 3;
    private int playerCount;    //max players: 3
    private List<Player> players;  

    private List<Package> packages;
    private List<Package> pendinPackages;

    //TODO: need to add Edge ID somehow first.
    private List<Integer> fragileEdges;
    private List<Integer> brokenEdges;

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

            globalTime++;
        }

    }

    private void displayInfo(){
        System.out.println("\n==========================================");
        System.out.println("Global time: " + globalTime);

        System.out.println("");

        for(int i = 0; i < players.size(); i++){
            Player curr = players.get(i);
            System.out.print("\nPlayer #" + (i+1) + " in ");
            if(curr.getBusyTime() > 1)
                System.out.print(curr.getBusyTime() + " units of time will arive to ");
            System.out.print(" Location: V" + curr.getLocation().getIndex() + " Score: " + curr.getScore());
        }

        System.out.println("");
        /*
        for (Package p : packages) {
            System.out.print("\nPackage #" + p.getId() + " status: " + p.getStatus());
            Player e = p.getPlayer(); 
            if(e != null)
                System.out.print(" carried by: " + (e.getId()) + " to destination " + p.getDestinationLocationIndex());
            else 
                System.out.print(" at location V" + p.getStartingLocationIndex());
            System.out.print(" with a deadline of " + p.getDeadline() + "\n");
        }
        */

    }

    private void updatePendingPackages(){
        for(int i = 0; i < pendinPackages.size(); i++){
            if(pendinPackages.get(i).attemptPublishPackage()){
                pendinPackages.remove(i);
                i--;
            }
        }
    }

    // Constructor
    public Enviroment(File input) throws Exception{
        globalTime = 0;

        playerCount = 0;    //max players: 3
        players = new ArrayList<>();  

        packages = new ArrayList<>();
        pendinPackages = new ArrayList<>();

        fragileEdges = new ArrayList<>();
        brokenEdges = new ArrayList<>();

        graph = null;

        try {
            Scanner scanner = new Scanner(input);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                
                if(line.length() == 0 || line.charAt(0) == ';')
                    continue;

                /*TODO - need to optimize this.
                    1. read all the file into memory at once, seperate it by delimeter of new line
                    2. parse the #N first seperately to avoid checks for every line.

                    https://stackoverflow.com/questions/14169661/read-complete-file-without-using-loop-in-java
                */
                String head = line.substring(0, 2);

                if(head.equals("#N"))
                    handleN(line);
                else if(head.equals("#E"))
                    handleE(line);
                else if(head.equals("#P"))
                    handleP(line);
                else if(head.equals("#B"))
                    handleB(line);
                else if(head.equals("#F"))
                    handleF(line);
                else if(head.equals("#F"))
                    handleF(line);
                else if(head.equals("#A"))
                    handleA(line);
                else if(head.equals("#H"))
                    handleH(line);
                else if(head.equals("#I"))
                    handleI(line);
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

        this.graph = new AdjacencyMatrixGraph(size + 1);    // +1 to have V1...Vn
    }

    // #E1 1 2 W1  -  Edge 1 from vertex 1 to vertex 2, weight 1
    private void handleE(String str) throws Exception{
        if(graph == null)
            throw new Exception("Invalid input, must create a graph with #N before adding edges with #E !");

        Scanner s = new Scanner(str);

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
        
        s.close();

        Package p1 = new Package(graph.getVertex(v_src), graph.getVertex(v_dst), start_time, end_time, p);
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

    //#H V4               ; "Human" agent (see below)  starts at V2
    private void handleH(String str){
        Scanner s = new Scanner(str);
        int locationIndex = s.useDelimiter("\\D+").nextInt();
        s.close();

        Vertex vertex = this.graph.getVertex(locationIndex);

        Player p1 = new HumanPlayer(this.graph, vertex, playerCount);
        this.players.add(p1);
        playerCount ++;
    }

    // #I V5               ; Interfering agent (see below) starts at V5
    private void handleI(String str){
        Scanner s = new Scanner(str);

        int locationIndex = s.useDelimiter("\\D+").nextInt();
        Vertex vertex = this.graph.getVertex(locationIndex);
        s.close();

        Player p1 = new InterferingPlayer(this.graph, vertex, playerCount);
        this.players.add(p1);
        playerCount ++;
    }
}


    /*       TODO: things to add
        - Display scores (and logs of actions in general)
        - Record scores (and logs of actions in general)
    * 
    */

/*  Assumptions on theinput text file to make the life easier
    0. Assumes input validity (e.g. no stuff like #N with no number afterwards)
 *  1. Each row contains only one command, and each parameter is sepearted by a single space ( )
 *  2. If a row contains a comment (;), there's at least one space before the command and the ;
 */