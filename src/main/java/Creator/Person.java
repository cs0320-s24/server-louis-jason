package Creator;

public class Person {
  private String State;
  private String color;
  private String earnings;

  public Person(String state, String color, String earnings) {
    this.State = state;
    this.color = color;
    this.earnings = earnings;
  }

  public boolean equal(Person person) {
    if (this.State.equals(person.getState())) {
      if (this.color.equals(person.getColor())) {
        if (this.earnings.equals(person.getEarnings())) {
          return true;
        }
      }
    }
    return false;
  }

  public String getState() {
    return this.State;
  }

  public String getColor() {
    return this.color;
  }

  public String getEarnings() {
    return this.earnings;
  }
}
