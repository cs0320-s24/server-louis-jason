package Server;

public class BroadbandInfo {


    private String name;
    private float usage;
    private String county;
    private String state;


    public BroadbandInfo() {}

    // Used for constructing a BroadbandInfo to search in cache
    public BroadbandInfo(String county, String state) {
        this.county = county;
        this.state = state;
    }

    public String getCounty() {
        return this.county;
    }

    public String getState() {
        return this.state;
    }

    @Override
    public String toString() {
        return this.county + this.state;
    }
}
