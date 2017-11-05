package gov.ca.cwds.neutron.enums;

public enum NeutronElasticsearchDefaults {

  /**
   * People index settings.
   */
  ES_PEOPLE_INDEX_SETTINGS("/elasticsearch/setting/people-index-settings.json"),

  /**
   * Person document mapping.
   */
  ES_PERSON_MAPPING("/elasticsearch/mapping/map_person_5x_snake.json");

  private final String value;

  private NeutronElasticsearchDefaults(String value) {
    this.value = value;
  }

  @SuppressWarnings("javadoc")
  public String getValue() {
    return value;
  }

}
