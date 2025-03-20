package src.Tests;

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

}
