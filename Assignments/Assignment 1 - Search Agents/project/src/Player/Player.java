package src.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import src.Env.Status;
import src.Graph.Graph;
import src.Graph.Vertex;
import src.Graph.Package;
import src.Graph.Simulator;
import src.Graph.State;

//  An abstract class of Player
public abstract class Player {

       public static final int NO_OP_NODE_CODE = -1;
       public static final int NO_OP_COST = 1;

       public static final int SCORE_NO_GAIN = 0;

       public static final boolean EMULATING = true, LIVE = false;

       public static final boolean IS_DELIVERING_PACKAGES = true, IS_NOT_DELIVERING_PACKAGES = false;
       public static final boolean IS_SEARCH_AGENT = true, IS_NOT_SEARCH_AGENT = false;

       protected Graph graph;
       protected Vertex location;
       private Vertex prevLocation;

       protected int id;
       private int score;
       private boolean isDelivering;      //not for Inerfering player
       private boolean isSearchAgent;

       private List<Package> packages;
       

       private int busyTime; //how many unites of time untill the player arrive at destination (0 = free)

       //Constructor
       public Player (Graph g, Vertex currLocation, Vertex prevLoc, int id, int scr, boolean isDeliveringPackages,
                     List<Package> packageList, int busyTime){

              this.graph = g;
              this. location = currLocation;
              this.prevLocation = prevLoc;
              
              this.id = id;
              this.score = scr;
              this.isDelivering = isDeliveringPackages;
              this.packages = packageList;
	       this.busyTime = busyTime;
       }

       //Constructor with default values
       public Player (Graph g, Vertex l, int id, boolean isDeliveringPackages, boolean isSearch){
              this(g, l, l, id, 0, isDeliveringPackages, new ArrayList<Package>(), 0);
       }

       //Copy Construcor (same graph)
       public Player(Player other){
              this(other, other.graph);
       }

       //Constructor to create a recursive deep copy for simulation
       public Player(Player other, Graph g){
              this(g, g.getVertex(other.getLocation().getIndex()), 
                     (other.prevLocation == null) ? null : g.getVertex(other.prevLocation.getIndex()),
                     other.id, other.score ,other.isDelivering, 
                     new ArrayList<Package>(), other.busyTime); 
       }

       public void deepCopyPackagesFromPlayer(Player other, Graph g){
              this.packages = other.getPackages().stream().map(p -> new Package(p, g)).collect(Collectors.toList());
       }

       public Vertex getLocation(){
              return this.location;
       }
       
       public boolean isDeliveringAgent(){
              return this.isDelivering;
       }

       public List<Package> getPackages(){
              return packages;
       }

       public int getId(){
              return this.id;
       }

       public Graph getGraph(){
              return this.graph;
       }

       public void setGraph(Graph g){
              this.graph = g;
       }
       
       public boolean isHoldingPackages(){
              return this.packages.size() != 0;
       }

       public boolean isHoldAnyUndeliveredPackage(){
              for(Package p : this.packages){
                     if(p.getStatus() != Status.DELIVERED)
                            return false;
              }
              return true;
       }

       // calculate the next step the player should take based on its strategy
       abstract int getNextStep();

       
       // create a copy of player
       public abstract Player clone();

       //create a copy of player and linking it to another graph
       public abstract Player clone(Graph g);

       //get a string description of each player
       public abstract String playerTypeString();
       
       public void noOp(){
              prevLocation = location;
              //might used when I'll add logging mechanism.
       }

       public int travelLogic(){
              if(isDelivering){
                     pickUpPackages();
                     int gain = deliverPackages();
                     return gain;
              } else {
                     return Player.SCORE_NO_GAIN;
              }
       }

       public void travers(){
              if(busyCheck())
                     return;
              
              travelLogic();
              goToDestination(getNextStep());
       }
 
       
       public int emulateStep(int action){
              goToDestination(action);
              return travelLogic();
       }

       public int emulateSingleStep(int action){
              if(busyCheck())
                     return 0;
              
              int gain = travelLogic();
              goToDestination(action);
              return gain;
       }

       //pick up packages at destination
       private void pickUpPackages(){
              List<Package> locationPackagesList = this.location.getPackageList();

              for(int index = 0; index < locationPackagesList.size(); index++){
                     Package p = locationPackagesList.get(index);
                     if(p.attemptPickUp(this)){
                            this.packages.add(p);
                            locationPackagesList.remove(index);
                            index--;
                     } 
              }
       }
       
       //attempt to deliver all current packages
       private int deliverPackages(){
              int gain = 0;

              for(int i=0; i<packages.size(); i++){
                     if(packages.get(i).attemptDelivery()){
                            gain++;
                            this.packages.remove(i);
							i--;
                     }
              }
              this.score += gain;
              return gain;
       }

       //go to the next destination
       private void goToDestination(int action){
              int destNodeIndex = action;

              if(destNodeIndex == NO_OP_NODE_CODE){
                     noOp();
                     return;
              }
                     
              prevLocation = location;
              location = graph.getVertex(destNodeIndex);
              busyTime = prevLocation.getNeighborWeight(destNodeIndex);  
              this.graph.updateFragileEdge(prevLocation, location);   
       }

       private boolean busyCheck(){
              if(this.busyTime > 1){   
                     busyTime --;
                     return true;
              }
              return false;
       }

       public int getScore(){
              return this.score;
       }

       public int getBusyTime(){
              return this.busyTime;
       }

       public boolean isSearchAgent(){
              return this.isSearchAgent;
       }

       public int extractNextActionFromState(State s){
              //edge case for travels of length 0
              if(s.getParent() == Simulator.NO_PARENT)
                     return s.getAction();

              while(s.getParent().getParent() != Simulator.NO_PARENT)
                     s = s.getParent();
                     
              return s.getAction();
       }
}
