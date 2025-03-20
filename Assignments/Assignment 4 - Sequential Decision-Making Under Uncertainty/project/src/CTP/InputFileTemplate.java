package src.CTP;

/**
 * This class defines constants representing various elements in the input file template 
 * for graph and agent-related definitions.
 */
public class InputFileTemplate {

    /**
     * Number of vertices N in the graph (from V1 to Vn).
     * 
     * <p>Syntax:</p>
     * <pre>
     * #N &lt;Number of vertices&gt;
     * </pre>
     * 
     * <p>Example:</p>
     * <pre>
     * #N 5    ; A graph of 5 vertices V1 to V5
     * </pre>
     */
    public static final String VERTICIES_COUNT = "#N";

    /**
     * Create an edge between two vertices.
     * 
     * <p>Syntax:</p>
     * <pre>
     * #E&lt;Edge ID&gt; &lt;Source vertex&gt; &lt;Destination vertex&gt; &lt;Weight&gt;
     * </pre>
     * 
     * <p>Example:</p>
     * <pre>
     * #E1 1 2 W1  ; Edge 1 from vertex 1 to vertex 2, weight 1
     * </pre>
     */
    public static final String EDGE = "#E";

    /**
     * Package definition with source, destination, start time, and deadline.
     * 
     * <p>Syntax:</p>
     * <pre>
     * #P&lt;Package ID&gt; &lt;Source vertex&gt; &lt;Start time&gt; D &lt;Destination vertex&gt; &lt;Deadline&gt;
     * </pre>
     * 
     * <p>Example:</p>
     * <pre>
     * #P1 V2 0 D V5 10  ; Package 1 at V2 from time 0, deliver to V5 on or before time 10
     * </pre>
     */
    public static final String PACKAGE = "#P";

    /**
     * Blocked edge (not traversable).
     * 
     * <p>Syntax:</p>
     * <pre>
     * #B &lt;Source vertex&gt; &lt;Destination vertex&gt;
     * </pre>
     * 
     * <p>Example:</p>
     * <pre>
     * #B V1 V5  ; Edge from V1 to V5 is always blocked
     * </pre>
     */
    public static final String BLOCKED_EDGE = "#B";

    /**
     * Fragile edge (can only be traversed once).
     * 
     * <p>Syntax:</p>
     * <pre>
     * #F &lt;Source vertex&gt; &lt;Destination vertex&gt;
     * </pre>
     * 
     * <p>Example:</p>
     * <pre>
     * #F V1 V2  ; Edge from V1 to V2 is fragile (can only be traversed once)
     * </pre>
     */
    public static final String FRAGILE_EDGE = "#F";

    /**
     * Normal agent (stupid greedy) starting location.
     * 
     * <p>Syntax:</p>
     * <pre>
     * #A &lt;Start vertex&gt;
     * </pre>
     * 
     * <p>Example:</p>
     * <pre>
     * #A V1  ; Normal agent starts at V1
     * </pre>
     */
    public static final String STUPID_GREEDY_AGENT = "#A";

    /**
     * Human agent starting location.
     * 
     * <p>Syntax:</p>
     * <pre>
     * #H &lt;Start vertex&gt;
     * </pre>
     * 
     * <p>Example:</p>
     * <pre>
     * #H V4  ; "Human" agent starts at V4
     * </pre>
     */
    public static final String HUMAN_AGENT = "#H";

    /**
     * Interfering agent starting location.
     * 
     * <p>Syntax:</p>
     * <pre>
     * #I &lt;Start vertex&gt;
     * </pre>
     * 
     * <p>Example:</p>
     * <pre>
     * #I V5  ; Interfering agent starts at V5
     * </pre>
     */
    public static final String INTERFERING_AGENT = "#I";

    /**
     * A greedy search agent, that picks the move with the best immediate heuristic value to expand next.
     * 
     * <p>Syntax:</p>
     * <pre>
     * #G &lt;Start vertex&gt;
     * </pre>
     * 
     * <p>Example:</p>
     * <pre>
     * #G V6  ; Greedy agent starts at V6
     * </pre>
     */
    public static final String GREEDY_AGENT = "#G";

    /**
     * An agent using A* search, with the same heuristic. 
     * If the number of expansions exceeds a global constant LIMIT (default 10000), it returns "fail" 
     * and performs the "no-op" action.
     * 
     * <p>Constants:</p>
     * <ul>
     * <li><b>DEFAULT_LIMIT</b>: 10000</li>
     * </ul>
     * 
     * <p>Syntax:</p>
     * <pre>
     * #S &lt;Start vertex&gt; &lt;Expansion limit (optional)&gt;
     * </pre>
     * 
     * <p>Examples:</p>
     * <pre>
     * #S V5 2500  ; A* agent starts at V5 with a limit of 2500 expansions
     * #S V10      ; A* agent starts at V5 with the default expansion limit (10000)
     * </pre>
     */
    public static final String A_STAR_AGENT = "#S";

    /**
     * An agent using (fake) real-time A* search. Performs L expansions (user-defined constant, default L=10) 
     * before each move decision.
     * 
     * <p>Constants:</p>
     * <ul>
     * <li><b>DEFAULT_L</b>: 10</li>
     * </ul>
     * 
     * <p>Syntax:</p>
     * <pre>
     * #R &lt;Start vertex&gt; &lt;Expansion limit (optional)&gt;
     * </pre>
     * 
     * <p>Examples:</p>
     * <pre>
     * #R V2 30  ; Real-time A* agent starts at V2 with a limit of 30 expansions per decision
     * #R V6     ; Real-time A* agent starts at V6 with the default expansion limit (10)
     * </pre>
     */
    public static final String REAL_TIME_A_STAR_AGENT = "#R";

    /**
     * Loyalty game mode, using mini-max with alpha-beta pruning. Can work only as a standalone mode, without additional agents.
     * 
     * <p>Constants:</p>
     * <ul>
     * <li><b>DEFAULT_CUTOFF</b>: 1000</li>
     * <li><b>ADVERSARIAL mode</b>: 1</li>
     * <li><b>SEMI_COOPERATIVE mode</b>: 2</li>
     * <li><b>FULLY_COOPERATIVE mode</b>: 3</li>
     * </ul>
     * 
     * <p>Syntax:</p>
     * <pre>
     * #M &lt;P1 location&gt; &lt;P2 location&gt; &lt;Game mode&gt; &lt;CUTOFF limit (optional)&gt;
     * </pre>
     * 
     * <p>Examples:</p>
     * <pre>
     * #M V1 V2 M3 2000  ; Loyalty game mode with P1 starting at V1, P2 starting at V2, FULLY_COOPERATIVE mode, and a CUTOFF limit of 2000
     * #M V1 V2 M1       ; Loyalty game mode with P1 starting at V1, P2 starting at V2, ADVERSARIAL mode, and the default CUTOFF limit
     * </pre>
     */
    public static final String LOYALTY_GAME = "#M";


    /**
     * Represents the probability of a package being at a specific vertex during the low demand season.
     * This probability is doubled for medium demand, and quadrupled for high demand (up to a maximum of 1 obviously).
     * 
     * <p>Constant:</p>
     * <ul>
     * <li><b>DEFAULT_PROBABILITY</b>: 0</li>
     * </ul>
     * 
     * <p>Syntax:</p>
     * <pre>
     * #V &lt;Vertex ID&gt; &lt;Probability&gt;
     * </pre>
     * 
     * <p>Example:</p>
     * <pre>
     * #V V2 0.2  ; Vertex V2 has a 0.2 probability of package presence in the low demand season
     * </pre>
     */
    public static final String VERTEX_PROBABILITY_FOR_PACKAGE = "#V";   //replaces #P from assignment 3's input file example


    /**
     * Represents the global leakage probability for the system.
     * 
     * <p>Constant:</p>
     * <ul>
     * <li><b>DEFAULT_VALUE</b>: 0.1</li>
     * </ul>
     * 
     * <p>Syntax:</p>
     * <pre>
     * #L &lt;Leakage Probability&gt;
     * </pre>
     * 
     * <p>Example:</p>
     * <pre>
     * #L 0.1  ; Global leakage probability is 0.1
     * </pre>
     */
    public static final String GLOBAL_LEAKAGE_PROBABILITY = "#L";

    /**
     * Represents the distribution parameter for the system.
     * 
     * <p>Constant:</p>
     * <ul>
     * <li><b>DEFAULT_VALUE</b>: 0.2</li>
     * </ul>
     * 
     * <p>Syntax:</p>
     * <pre>
     * #Q &lt;Parameter Q Value&gt;
     * </pre>
     * 
     * <p>Example:</p>
     * <pre>
     * #Q 0.2  ; Parameter Q is set to 0.2
     * #Q      ; Paremet is using default value
     * </pre>
     */
    public static final String DISTRIBUTION_PARAMETER = "#Q";
    
    /**
     * Represents the prior distribution over the seasons.
     * 
     * <p>Constant:</p>
     * <ul>
     * <li><b>DEFAULT_DISTRIBUTION</b>: (low, medium, high) =  (0.1, 0.4, 0.5)</li>
     * </ul>
     * 
     * <p>Syntax:</p>
     * <pre>
     * #T &lt;Low Season Probability&gt; &lt;Medium Season Probability&gt; &lt;High Season Probability&gt;
     * </pre>
     * 
     * <p>Example:</p>
     * <pre>
     * #T 0.1 0.4 0.5  ; Prior distribution: 0.1 for low, 0.4 for medium, 0.5 for high demand season
     * </pre>
     */
    public static final String SEASON_DISTRIBUTION = "#S";  //replaces #S from assignment 3's input file example

    /**
     * Represents the starting location of the agent in the MAPD (Canadian Traveler Problem) domain.
     * The agent always begins at a specified vertex in the given undirected graph.
     * 
     * <p>
     * In the input format, this is represented as:
     * <pre>
     * #A V1  ; Agent starts at V1
     * </pre>
     * where "V1" indicates the starting vertex.
     * </p>
     *
     * <p>
     * The agent's task is to navigate the graph while dealing with uncertain edge blockages,
     * attempting to deliver a package in minimal expected time.
     * </p>
     */
    public static final String AGENT_STARTING_LOCATION = "#A";

    /**
     * Represents the number of simulation runs in the MAPD (Canadian Traveler Problem) domain.
     * This parameter defines how many times the agent's navigation through the graph 
     * should be simulated based on randomly generated blockage instances.
     * 
     * <p>
     * In the input format, this could be represented as:
     * <pre>
     * #C 10  ; Run 10 simulations
     * </pre>
     * where "10" specifies the number of independent simulation runs.
     * </p>
     *
     * <p>
     * During each simulation, the agent follows the computed policy while navigating 
     * a graph instance where fragile edges are blocked according to their given probabilities.
     * </p>
     *
     * <p>
     * If not explicitly provided, the default number of simulations is <b>5</b>.
     * </p>
     */
    public static final String SIMULATION_COUNT = "#C";

}
