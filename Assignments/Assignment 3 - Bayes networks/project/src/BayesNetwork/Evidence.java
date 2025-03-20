package src.BayesNetwork;

public class Evidence {
    //Bayes Nework
    private Enviroment env;
    private int size;

    public static final boolean KNOWN = true, UNKNOWN = false;

    private SEASON_VALUE season;
    private PACKAGE_VALUE[] packages;
    private  BLOCKAGE_VALUE[][] edges;

    //Empty constructor
    public Evidence(Enviroment e){
        this.env = e;
        this.size = e.getSize();

        initialEmptyEvidence();
    }

    //Copy constructor
    public Evidence(Evidence other){
        this.env = other.env;
        this.size = other.size;

        //copy all values
        this.season = other.season;
        
        packages = new PACKAGE_VALUE[size];
        for(int i = 1; i < size; i++){
                packages[i] = other.packages[i];
        }

        edges = new  BLOCKAGE_VALUE[size][size];
        for(int i = 1; i < size; i++){
            for(int j = 1; j <size; j++){
                this.edges[i][j] = other.edges[i][j];
            }
        }   
    }

    public void emptyEvidence(){
        initialEmptyEvidence();
    }

    private void initialEmptyEvidence(){
        season = SEASON_VALUE.UNKNOWN;
        
        packages = new PACKAGE_VALUE[size];
        for(int i = 1; i < size; i++){
             packages[i] = PACKAGE_VALUE.UNKNOWN;
        }

        edges = new  BLOCKAGE_VALUE[size][size];
        for(int i = 1; i < size; i++){
            for(int j = 1; j <size; j++){
                if(env.isEdgeExist(i,j))
                     edges[i][j] =  BLOCKAGE_VALUE.UNKNOWN;
                else
                     edges[i][j] =  BLOCKAGE_VALUE.FREE;
            }
        }
    }

    //getters and setters (queries)
    public void addSeasonToEvidence(SEASON_VALUE s){
        season = s;
    }

    public SEASON_VALUE getSeasonValue(){
        return this.season;
    }

    public boolean isSeasonKnown(){
        return season != SEASON_VALUE.UNKNOWN;
    }


    public void addPackageToEvidence(int v, PACKAGE_VALUE p){
        packages[v] = p;
    }

    public PACKAGE_VALUE getPackageValueAtVertex(int v){
        return this.packages[v];
    }

    public boolean isPackageAtVertexKnown(int v){
        return  packages[v] != PACKAGE_VALUE.UNKNOWN;
    }


    public void addEdgeBlockageToEvidence(int i, int j, BLOCKAGE_VALUE p){
        edges[i][j] = p;
        edges[j][i] = p;
    }

    public BLOCKAGE_VALUE getBlockageValue(int i, int j){
        return this.edges[i][j];
    }

    public boolean isEdgeBlockedKnown(int i, int j){
        return  edges[i][j] != BLOCKAGE_VALUE.UNKNOWN;
    }

    public boolean isKnown(Node n) throws Exception{
        char type = n.getDescription().charAt(0);

        switch (type) {
            case 'S':
                return isSeasonKnown();
            case 'V':
                return isPackageAtVertexKnown(n.getIthIndex(0));
            case 'E':
                return isEdgeBlockedKnown(n.getIthIndex(0), n.getIthIndex(1));        
            default:
                throw new Exception("Illegal query");
        }
    }
}
