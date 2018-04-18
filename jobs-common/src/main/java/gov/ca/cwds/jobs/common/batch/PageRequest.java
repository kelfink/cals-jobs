package gov.ca.cwds.jobs.common.batch;

/**
 * Created by Alexander Serbin on 3/29/2018.
 */
public class PageRequest {

  private int limit;
  private int offset;
  private int lastId;

  public PageRequest(int offset, int limit) {
    this.offset = offset;
    this.limit = limit;
  }

  public int getLimit() {
    return limit;
  }

  public int getOffset() {
    return offset;
  }

  public void incrementPage() {
    offset += limit;
  }

  public void increment() {
    offset++;
  }

  public void setLastId(int lastId) {
    this.lastId = lastId;
  }

  public int getLastId() {
    return lastId;
  }

  @Override
  public String toString() {
    return "PageRequest{" +
        "limit=" + limit +
        ", offset=" + offset +
        ", lastId=" + lastId +
        '}';
  }
}
