package src.Player;

import java.util.ArrayList;
import java.util.List;

import src.Graph.Graph;
import src.Graph.Vertex;
import src.Graph.Package;

//  An abstract class of Player
public abstract class Player {

       public static final int NO_OP_NODE_CODE = -1;
       public static final boolean IS_DELIVERING_PACKAGES = true, IS_NOT_DELIVERING_PACKAGES = false;

       private int score;
       private int id;

       private static Graph graph;

       private Vertex location;
       private Vertex prevLocation;
       private List<Package> packages;
       private boolean isDelivering;      //only for Human and Greedy

       private int busyTime; //how many unites of time untill the player arrive at destination (0 = free)

       //Constructor
       public Player (Graph g, Vertex l, int id, boolean isDeliveringPackages){
              this.id = id;
              score = 0;
              isDelivering = isDeliveringPackages;
              graph = g;
              location = l;
              prevLocation = null;
              packages = new ArrayList<>();
       }

       public Vertex getLocation(){
              return this.location;
       }

       public List<Package> getPackages(){
              return packages;
       }

       public int getId(){
              return this.id;
       }

       public Graph getGraph(){
              return Player.graph;
       }
       
       public boolean isHoldingPackages(){
              return this.packages.size() != 0;
       }

       // calculate the next step the player should take based on its strategy
       abstract int getNextStep();
       
       public void noOp(){
              prevLocation = location;
              //might used when I'll add logging mechanism.
       }

       public void travers(){
              //NOTE: greater than one (and not zero) to account for the curent step when the player is choosing his next step
              if(busyTime > 1){   
                     busyTime --;
                     return;
              }

              Player.graph.updateFragileEdge(prevLocation, location);
              if(isDelivering){
                     deliverPackages();
                     pickUpPackages();
              }

              goToDestination();
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
       private void deliverPackages(){
              for(int i=0; i<packages.size(); i++){
                     if(packages.get(i).attemptDelivery()){
                            score++;
                            this.packages.remove(i);
                     }
              }
       }

       //go to the next destination
       private void goToDestination(){
              int destNodeIndex = getNextStep();
              if(destNodeIndex == NO_OP_NODE_CODE){
                     noOp();
                     return;
              }
                     
              prevLocation = location;
              location = graph.getVertex(destNodeIndex);
              busyTime = prevLocation.getNeighborWeight(destNodeIndex);
       }

       public int getScore(){
              return this.score;
       }

       public int getBusyTime(){
              return this.busyTime;
       }


      /*    Methods:
       *      - getNextStep (calculate the next step the player should take based on its strategy)
       * 
       *      - travers (cost: 1 per weight of node traversed)
       *      - no-op (cost: 1)
       */


}
