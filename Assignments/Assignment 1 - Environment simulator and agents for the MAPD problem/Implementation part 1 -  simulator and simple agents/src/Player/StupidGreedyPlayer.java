package src.Player;

import java.util.List;

import src.Graph.Graph;
import src.Graph.Package;
import src.Graph.Vertex;

public class StupidGreedyPlayer extends Player {

    /*  A stupid greedy agent, that works as follows: if the agent is not holding a package, it should compute the shortest
        currently unblocked path to the next vertex with a package to be delivered, and try to follow it. If it is holding 
        a package, it should find the shortest path to a delivery location for the package, and try to follow it.
        If holding more than 1 package, attempt to deliver the one with a shorter path to its delivery location.
        If there is no such path, do no-op. Here and elsewhere, if needed, break ties by prefering lower-numbered vertices.
        Stupidly ignores other agents.
     */

        public StupidGreedyPlayer(Graph g, Vertex v, int i){
            super(g, v, i, Player.IS_DELIVERING_PACKAGES);
        }
    
    //TODO: might introduce versioning to the graph, to compute new paths only when necessary
    @Override
    int getNextStep() {
        if(this.isHoldingPackages()){
            int minCost = Integer.MAX_VALUE;
            int locationIndex = -1;
            int[][] minData = null;

            // get the closest package to deliver (that the player currently holds)
            List<Package> packageList = this.getPackages();

            for(int packageIndex = 0; packageIndex < packageList.size(); packageIndex++){
                int[][] currData = this.getGraph().getCheapestPath(this.getLocation().getIndex(), packageList.get(packageIndex).getDestinationLocationIndex());

                int currCost = currData[Graph.COST_INDEX][0];
                if(minCost > currCost || currCost != Graph.NOT_DEFINED_YET){
                    minCost = currCost;
                    locationIndex = packageList.get(packageIndex).getDestinationLocationIndex();
                    minData = currData;
                }
            }

            if(locationIndex == -1 || minData == null)
                return Player.NO_OP_NODE_CODE;
            
            return this.getGraph().extractNextStepFromPath(minData[Graph.PATH_INDEX], minData[Graph.PATH_INDEX][0]);
            
        } else {    //if the player does not hold and packages
            int[][] data = this.getGraph().getCheapestPathToPackage(this.getLocation().getIndex());
            
            if(data == null)
                return Player.NO_OP_NODE_CODE;
            
            int[] destPath = data[Graph.PATH_INDEX];
            
            return this.getGraph().extractNextStepFromPath(destPath, destPath[0]);
        }
         
     }
}
