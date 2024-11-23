package src.Player;

import src.Graph.Graph;
import src.Graph.Vertex;

public class InterferingPlayer extends Player {

    /*  An interfering saboteur agent, that moves and tries to block fragile edges by traversing them.
        The saboteur works as follows: it computes the shortest path to a fragile edge, and moves in that direction.
        If not possible, it does a no-op. 
        The saboteur does not pick up packages. Prefer the lowest-numbered vertices in case of ties. Stupidly ignores other agents.
     * 
     */

     public InterferingPlayer(Graph g, Vertex v, int i){
        super(g, v, i, Player.IS_NOT_DELIVERING_PACKAGES);
     }



    @Override
     int getNextStep() {


        int[][] data = this.getGraph().getCheapestPathToFragileEdge(this.getLocation().getIndex());
            
        if(data == null)
            return Player.NO_OP_NODE_CODE;
        
        int[] destPath = data[Graph.PATH_INDEX];

        return this.getGraph().extractNextStepFromPath(destPath, destPath[0]);
     }
}
