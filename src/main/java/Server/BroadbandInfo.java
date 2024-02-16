package Server;

/**
 * This class is used for searching in the Census API. It has helpful get methods and has all the
 * fields if needed to be used when deserializing a JSON.
 */
public class BroadbandInfo {

  private String name;
  private float usage;
  private String county;
  private String state;
  private String countyCode;
  private String stateCode;

  public BroadbandInfo() {}

  // Used for constructing a BroadbandInfo to search in cache
  public BroadbandInfo(String county, String state) {
    this.county = county;
    this.state = state;
  }

  public String getCountyCode() {
    return this.countyCode;
  }

  public String getStateCode() {
    return this.stateCode;
  }

  public String getCountyName() {
    return this.county;
  }

  public String getStateName() {
    return this.state;
  }

  @Override
  public String toString() {
    return this.county + "," + this.state;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof BroadbandInfo)) {
      return false;
    }

    BroadbandInfo oBroadbandInfo = (BroadbandInfo) o;

    System.out.println(o.toString());
    System.out.println(this.toString());

    return this.toString().equals(oBroadbandInfo.toString());
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }
}
