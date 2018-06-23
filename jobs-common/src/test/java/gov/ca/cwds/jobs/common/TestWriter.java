package gov.ca.cwds.jobs.common;

import gov.ca.cwds.jobs.common.util.ConsumerCounter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexander Serbin on 3/28/2018.
 */
public class TestWriter<E> implements BulkWriter<E> {

  private List<E> items = new ArrayList<>();

  @Override
  public void write(List<E> items) {
    this.items.addAll(items);
    ConsumerCounter.addToCounter(items.size());
  }

  public void reset() {
    items = new ArrayList<>();
    ConsumerCounter.reset();
  }

  public List<E> getItems() {
    return items;
  }

}


