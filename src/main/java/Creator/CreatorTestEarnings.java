package Creator;

import java.util.List;

public class CreatorTestEarnings implements CreatorFromRow<Person> {
  public CreatorTestEarnings() {}

  @Override
  public Person create(List<String> row) throws FactoryFailureException {
    Person person = new Person(row.get(0), row.get(1), row.get(2));
    return person;
  }
}
