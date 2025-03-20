package src.Graph;

import src.Env.Main;
import src.Env.Status;
import src.Player.Player;


public class Package {
    private Vertex startingLocation, destination;
    private int startTime, endTime;
    private int id;
    private int status;
    private Player agent;
    private Graph graph;


    //Constructor
    public Package(Vertex l, Vertex d, int s, int t, int id, int stat, Player player, Graph g){
        startingLocation = l;
        destination = d;

        startTime = s;
        endTime = t;

        this.id = id;
        status = stat;
        agent = player;
        graph = g;
    }

    public Package(Vertex l, Vertex d, int s, int t, int id, Graph g){
        this(l,d,s,t,id, Status.NOT_SPAWNED, null, g);
    }

    public Package (Package p){
        this(p.startingLocation, p.destination, p.startTime, p.endTime, p.id, p.status, p.agent, p.graph);
    }

    //Constructor to create a recursive deep copy for simulation
    public Package(Package p, Graph g){     
           
        this(g.getVertex(p.getStartingLocationIndex()), g.getVertex(p.getDestinationLocationIndex()), p.startTime, p.endTime,
            p.id, p.status, (p.getPlayer() == null) ? null : g.getPlayers().get(p.getPlayer().getId()), g);        
    }

    public int getStartingTIme(){
        return this.startTime;
    }

    public boolean attemptPublishPackage(){
        if(startTime <= graph.getLocalTime()){
            if(Main.isHumanPlayerDisplay){
                System.out.println("\n***************package #" + this.id + " was publised at location V" + this.startingLocation.getIndex());
            }

            status = Status.PENDING;
            return true;
        }
        return false;
    }

    
    //link the package to a player 
    public boolean attemptPickUp(Player p){    
        if(this.status == Status.PENDING){
            if(Main.isHumanPlayerDisplay){
                System.out.println("\n***************package #" + this.id + " was picked up at location V" + this.startingLocation.getIndex() + 
                    " By player " + p.getId());
            }

            agent = p;
            status = Status.DELIVERING;
            return true;
        }
        return false;
    }

    public boolean canBePickedUp(int time, int location){    
        return (this.startTime < time && this.startingLocation.getIndex() == location && this.status == Status.PENDING);
    }

    public void simulatePickUp(){
        this.status = Status.DELIVERING;
    }

    public boolean attemptDelivery(){
        if(agent.getLocation() == destination && endTime >= graph.getLocalTime()){
            //if(Main.isHumanPlayerDisplay)
            //    System.out.println("Package #" + id + "was picked up by player " + this.agent + "at location V" + this.agent.getLocation().getIndex());
            this.status = Status.DELIVERED;
            return true;
        }

        return false;
    }
    
    public int getId(){
        return this.id;
    }

    public int getDeadline(){
        return this.endTime;
    }

    public int getStatus() {
        return this.status;
    }

    public Player getPlayer() {
        return this.agent;
    }

    public int getStartingLocationIndex(){
        return this.startingLocation.getIndex();
    }

    public int getDestinationLocationIndex(){
        return this.destination.getIndex();
    }


}
