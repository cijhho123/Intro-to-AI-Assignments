package src.Player;

import java.util.Scanner;

import src.Env.Main;
import src.Graph.Graph;
import src.Graph.Vertex;

public class HumanPlayer extends Player {

    // A human agent, i.e. print the state, read the next move from the user, 
    // and return it to the simulator. This is used for debugging and evaluating the program.

    public HumanPlayer(Graph g, Vertex v, int i) {
        super(g, v, i, Player.IS_DELIVERING_PACKAGES);
    }

    
    public int getNextStep() {
        int[] neighborsWeights = this.getLocation().getAllNeighborsWeights();

        if(Main.isHumanPlayerDisplay){
            System.out.println("\nPlayer at location V"+ this.getLocation().getIndex() +", List of neighbors from current vertex: ");

            for(int i=0; i<neighborsWeights.length; i++)
                if(neighborsWeights[i] > 0)
                    System.out.println("V" + i + " with cost " + neighborsWeights[i]);
        }

        Scanner s = new Scanner(System.in);
        int indexChosen = s.nextInt();
        s.close();
        
        if(indexChosen > neighborsWeights.length || neighborsWeights[indexChosen] < 1){
            System.out.println("invalid action, operation of this turn will be No-OP");
            return Player.NO_OP_NODE_CODE;
        }

        
        
        return indexChosen;
    }

}
