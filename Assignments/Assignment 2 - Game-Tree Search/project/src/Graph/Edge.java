package src.Graph;

public class Edge {
    private int u,  v;  // u < v
    private int weight;
    private boolean isFragile, isBroken;

    public Edge(int v1, int v2, int w, boolean f, boolean b){
        this.u = Math.min(v1, v2);
        this.v = Math.max(v1, v2);

        this.weight = w;

        this.isFragile = f;
        this.isBroken = b;
    }

    public Edge(Edge e){
        this(e.u, e.v, e.weight, e.isFragile, e.isBroken);
    }

    public Edge(int v1, int v2, int w){
        this(v1, v2, w, false, false);
    }

    public Edge(int v1, int v2){
        this(v1, v2, 0, false, false);
    }

    @Override
    public boolean equals(Object e){
        if(this == e)
            return true;
        
        if(! (e instanceof Edge))
            return false;
            
        return this.u == ((Edge) e).u && this.v == ((Edge) e).v;
    }

    @Override
    public int hashCode() {
        String description = Integer.toString(u) + " " + Integer.toString(v);
        return description.hashCode();
    }

    public boolean isFragileEdge(){
        return this.isFragile;
    }

    public boolean isBrokenEdge(){
        return this.isBroken;
    }

    public void setFragile(boolean isFragileEdge){
        this.isFragile = isFragileEdge;
    }

    public void setBrokenEdge(boolean isBrokenEdge){
        this.isBroken = isBrokenEdge;
        
        if(isBrokenEdge){
            this.isFragile = false;
            this.weight = Vertex.BLOCKED;
        }
    }
    
    public void setWeight(int w){
        this.weight = w;
    }

    public int getWeight(){
        return this.weight;
    }

    public int getV1(){
        return this.u;
    }

    public int getV2(){
        return this.v;
    }
}
