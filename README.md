# Intro to AI Course Assignments
Assignments from the course "Introduction to Artificial Intelligence" in the CS department of BGU, taught by Prof Eyal Shimoni.

# Lectures
All the lectures are available [here](Lectures).

# Assignments

## Assignments 1 - Multi-Agent Pickup and Delivery in a Hostile Environment
This assignment involves creating a simulation environment and developing agents to solve a complex logistical problem known as the Multi-Agent Pickup and Delivery (MAPD) problem.

The task is to model a warehouse environment as an undirected, weighted graph with vertices (locations) and edges (paths), some of which may be blocked or fragile. The agents in this environment include pickup and delivery robots, which pick up packages from specified locations and deliver them to their destinations, and interfering agents, which aim to block paths by traversing fragile edges, adding an element of challenge.

The simulation dynamics involve agents operating in a quasi-concurrent manner, taking turns to move. The goal is to deliver all packages as quickly as possible while navigating blocked and fragile paths. The performance of the agents is measured based on the number of packages successfully delivered and the time taken.

This assignment provides a practical introduction to multi-agent systems and the challenges of optimizing logistics in dynamic and potentially hostile environments.



## Assignment 2 - Cooperating and adversarial agents in the MAPD problem
This assignment builds on the previous one by introducing intelligent agents that can cooperate or compete in the Multi-Agent Pickup and Delivery (MAPD) problem. The environment remains the same and agents can perform actions such as traversing edges or doing nothing (no-op).

The focus is on two normal pickup and delivery agents, potentially representing competing companies, who aim to deliver as many packages as possible. The assignment explores different game settings:
- **Adversarial:** Agents compete to maximize their score minus the opponent's score using mini-max with alpha-beta pruning.
- **Semi-Cooperative:** Agents maximize their own score, with ties broken cooperatively.
- **Fully Cooperative:** Agents work together to maximize the sum of their scores.

The assignment includes implementing a cutoff and heuristic evaluation function to manage the large game tree. It provides insights into multi-agent systems, focusing on both cooperative and competitive dynamics.



## Assignment 3 - Reasoning Under Uncertainty In The MAPD problem
This assignment involves using Bayesian networks to handle uncertainty. The environment is an undirected graph where the locations of packages and blockages on edges are unknown.

There are three types of variables (Bayesian Network nodes):
-  **season:** a single global variable, indicate the level of demand for packages (low medium or high)
- **package presence:** one for each vertex, indicating if the vertex contain a package or not
- **blockages:** one for each edge, indicating if the edge is blocked or not.

Users input evidence about the environment, such as known blockages, package locations, or the season. This evidence is used to perform probabilistic reasoning to determine:
- The probability that each vertex contains a package.
- The probability that each edge is blocked.
- The distribution of the season variable.
- The probability that a specific path is free from blockages (bonus).
- The path with the highest probability of being free from blockages from a given location to a goal (bonus).




## Assignment 4 - Decision-Making Under Uncertainty in MAPD Problem
This assignment focuses on sequential decision-making under uncertainty using belief-state Markov Decision Processes (MDPs) in the context of the MAPD problem, specifically a stochastic variant known as the Canadian Traveler Problem.
The agent starts at a specified vertex, already holding a package, and must deliver it to a known destination.

The locations of blockages are unknown initially but are revealed when the agent reaches an adjacent vertex. The blockages are assumed to be independent and do not change state once known. The goal is to find a policy that minimizes the expected delivery time of the package. This involves constructing and solving a belief-state MDP to determine the optimal actions under uncertainty.

The entire belief space is stored in memory, and value iteration is used to compute the value function for the belief states. The optimal action for each belief state is maintained during the value iteration process to derive the optimal policy.


# Input File format
The input file format for this assignment is designed to specify the environment and parameters for the Multi-Agent Pickup and Delivery (MAPD) problem. It uses a simple ASCII format with comments beginning with a semicolon (;).  
There's a javadocs file under [here](Assignments/Assignment%202%20-%20Game-Tree%20Search/project/src/Tests/InputFileTemplate.java)

## Vertices
The number of vertices in the graph.  
Syntax: `#N <number>`    
Example: `#N 5 ; number of vertices N in graph (from V1 to Vn)`

## Edges
Each edge is defined with #E, followed by the edge number, the vertices it connects, and the weight of the edge.  
Syntax: `#E<ID number> <first vertex ID> <second vertex ID> W<weight number>`  
Example: `#E2 3 4 W1 ; Edge 2 from vertex 3 to vertex 4, weight 1`  

## Packages
Packages are specified with #P, followed by the package number, the vertex where it appears, the time it appears, the delivery destination, and the delivery deadline.  
Syntax: `#P<Package ID number> V<origin vertex ID> <appearence time> V<destination vertex ID> <delivery deadline time>`  
Example: `#P1 V2 0 D V5 10 ; Package 1 at V2 from time 0, deliver to V5 on or before time 10`  

## Blocked Edges
Permanently blocked edges are specified with #B, followed by the vertices it connects.  
Syntax: `#B V<first vertex ID> V<second vertex ID>`  
Example: `#B V1 V5 ; Edge from V1 to V5 is always blocked`  

## Fragile Edges
Fragile edges, which can only be traversed once, are specified with #F, followed by the vertices it connects.   
Syntax: `#F V<first vertex ID> V<second vertex ID>`  
Example: `#F V1 V2 ; Edge from V1 to V2 is fragile (can only be traversed once)`  

## Agents
Each agent is an entity traveling the map according to a certain rule with a certain goal.  

### Human Agent
i.e. print the state, read the next move from the user, and return it to the simulator. This is used for debugging and evaluating the program.  
Syntax: `#H V<VertexID>`  

### Stupid Greedy Agent
If the agent is not holding a package, it should compute the shortest currently unblocked path to the next vertex with a package to be delivered, and try to follow it.     
If it is holding a package, it should find the shortest path to a delivery location for the package, and try to follow it.   
If holding more than 1 package, attempt to deliver the one with a shorter path to its delivery location.  If there is no such path, do no-op.   
Stupidly ignores other agents.  
Syntax: `#A V<VertexID>`  

### Interfering Saboteur Agent
Moves and tries to block fragile edges by traversing them. it computes the shortest path to a fragile edge, and moves in that direction. If not possible, it does a no-op.   
The saboteur does not pick up packages. Stupidly ignores other agents.  
Syntax: `#I V<VertexID>`  

### Greedy Agent
A greedy search agent, that picks the move with the best immediate heuristic value to expand next.  
Syntax: `#G V<VertexID>`  

### A* Agent
An agent using A* search that picks the move with best immediate heuristic value to expand next. If the number of expansions exceeds a global constant LIMIT (default 10000), it returns "fail" and performs the "no-op" action.  
Syntax: `#S V<VertexID>`  

### Real-Time A* Agent
An agent using (fake) real-time A* doing L (user determined constant, L=10 by default) expansions before each move decision.  
Syntax: `#R V<VertexID>`  

## Loyalty Game Setup - Assignment 2 
Loyalty game mode, using mini-max with alpha-beta pruning. Can work only as a standalone mode, without additional agents.  
M indicate the game mode:  
1. **Adversarial (zero sum game):** each agent aims to maximize its own individual score (number of packages deliverd on time) minus the opposing agent's score. Implemented as an "optimal" agent, using mini-max, with alpha-beta pruning.  
2. **A semi-cooperative game:** each agent tries to maximize its own individual score. The agent disregards the other agent score, except that ties are broken cooperatively.  
3. **A fully cooperative game:** both agents aim to maximize the sum of individual scores.  
Syntax: `#M V<first vertex ID> V<second vertex ID> <Cutoff limit>`  
Example: `#M V1 V2 M3 2000  ; Loyalty game mode with P1 starting at V1, P2 starting at V2, FULLY_COOPERATIVE mode, and a CUTOFF limit of 2000`  

## Package Probability - Assignments 3,4
Represents the probability of a package being at a specific vertex during the low demand season.    
This probability is doubled for medium demand, and quadrupled for high demand (up to a maximum of 1).  
Note: In the assignment paper it is refer to #P, I've switch it to #V to keep each command unique.  
Syntax: `#V V<Vertex ID> <Probability in low season>`    
Example: `#V V2 0.2  ; Vertex V2 has a 0.2 probability of package presence in the low demand season`  

## Global Leakage Probability - Assignments 3,4
Represents the global leakage probability for the system.  
Syntax: `#L <Probability>`  
Example: `#L 0.1  ; Global leakage probability is 0.1`  

## Distribution Paremeter - Assignments 3,4
Represents the distribution parameter for the system.    
The edge variables are noisy-or distributed given the packages at neighboring vertices, with qi = Q * weight of the edge.  
Syntax: `#Q <Floating Point Number>`  
Example: `#Q 0.2  ; Parameter Q is set to 0.2`  

## Season Distribution - Assignments 3,4
Represents the starting location of the agent in the MAPD (Canadian Traveler Problem) domain.  
Syntax: `#S <low season probability> <medium season probability> <high season probability>`  
Example: `#S 0.1 0.4 0.5  ; Prior distribution: 0.1 for low, 0.4 for medium, 0.5 for high demand season`  

## Agent Starting Location - Assignments 3,4
Represents the starting location of the agent in the MAPD (Canadian Traveler Problem) domain. The agent always begins at a specified vertex in the given undirected graph.  
Syntax: `#A V<Vertex ID>`  
Example: `#A V1  ; Agent starts at V1`  

## Simulation Count - Assignments 3,4
Represents the number of simulation runs in the MAPD (Canadian Traveler Problem) domain.  
This parameter defines how many times the agent's navigation through the graph should be simulated based on randomly generated blockage instances.  
Syntax:  `#C <number>`  
Example: `#C 10  ; Run 10 simulations`  
