package apex.com.main;

import java.util.Comparator;

public class ModelComparer implements Comparator<Model> {
  public int compare(Model o1, Model o2) {
    return o1.getName().compareToIgnoreCase(o2.getName());
  }
}