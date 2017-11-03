package gov.ca.cwds.jobs.test;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(alphabetic = true)
public class TestNormalizedEntry implements Serializable {

  private String id;

  private String shazbat;

  public TestNormalizedEntry() {
    // default, no-op
  }

  public TestNormalizedEntry(String id, String shazbat) {
    this.id = id;
    this.shazbat = shazbat;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getShazbat() {
    return shazbat;
  }

  public void setShazbat(String shazbat) {
    this.shazbat = shazbat;
  }

}
