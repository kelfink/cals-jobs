package gov.ca.cwds.jobs.common;

import gov.ca.cwds.jobs.common.util.ConsumerCounter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander Serbin on 3/28/2018.
 */
public class TestWriter<E> implements BulkWriter<E> {

  private static List items = new ArrayList<>();

  @Override
  public void write(List<E> itemsToAdd) {
    items.addAll(itemsToAdd);
    ConsumerCounter.addToCounter(itemsToAdd.size());
  }

  public static void reset() {
    items = new ArrayList<>();
    ConsumerCounter.reset();
  }

  public static List getItems() {
    return items;
  }

}


