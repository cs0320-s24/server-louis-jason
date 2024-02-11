package Creator;

import java.util.List;

public class Creator implements CreatorFromRow<List<String>> {
  public Creator() {}

  @Override
  public List<String> create(List<String> row) throws FactoryFailureException {
    return row;
  }
}
