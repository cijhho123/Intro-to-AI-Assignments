package src.Graph;

import src.Env.Enviroment;
import src.Env.Main;
import src.Player.Player;

enum Status {
    NOT_SPAWNED,    //not published yet
    PENDING,        //pending to be picked up
    DELIVERING,     //picked up by a player, yet to be delivered
    DELIVERED       //deliver to destination
}

public class Package {
    private Vertex StartingLocation, destination;
    private int startTime, endTime;
    private int id;
    private Status status;
    private Player agent;


    //Constructor
    public Package(Vertex l, Vertex d, int s, int t, int id){
        StartingLocation = l;
        destination = d;

        startTime = s;
        endTime = t;

        this.id = id;
        status = Status.NOT_SPAWNED;
        agent = null;
    }

    public boolean attemptPublishPackage(){
        if(startTime <= Enviroment.globalTime){
            if(Main.isHumanPlayerDisplay){
                System.out.println("\n***************package #" + this.id + " was publised at location V" + this.StartingLocation.getIndex());
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
                System.out.println("\n***************package #" + this.id + " was picked up at location V" + this.StartingLocation.getIndex());
            }

            agent = p;
            status = Status.DELIVERING;
            return true;
        }

        return false;
    }


    public boolean attemptDelivery(){
        if(agent.getLocation() == destination && endTime >= Enviroment.globalTime){
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

    public String getStatus() {
        return this.status.name();
    }

    public Player getPlayer() {
        return this.agent;
    }

    public int getStartingLocationIndex(){
        return this.StartingLocation.getIndex();
    }

    public int getDestinationLocationIndex(){
        return this.destination.getIndex();
    }


}
