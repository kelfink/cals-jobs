package gov.ca.cwds.jobs.facility;

import gov.ca.cwds.jobs.util.jdbc.RowMapper;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Date;

/**
 * @author CWDS Elasticsearch Team
 */
public class FacilityRowMapper implements RowMapper<FacilityRow> {

  @Override
  public FacilityRow mapRow(ResultSet resultSet) throws Exception {
    FacilityRow facility = new FacilityRow();
    mapFields(facility, resultSet);
    return facility;
  }

  private void mapFields(Object object, ResultSet resultSet) throws Exception {
    for (Field field : object.getClass().getDeclaredFields()) {
      mapField(object, field, resultSet);
    }
  }

  private void mapField(Object object, Field field, ResultSet resultSet) throws Exception {
    PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), object.getClass());
    if (field.getType().equals(String.class)) {
      String value = resultSet.getString(field.getName());
      descriptor.getWriteMethod().invoke(object, value == null ? "" : value.trim());
    } else if (field.getType().equals(Date.class)) {
      Date value = resultSet.getTimestamp(field.getName());
      descriptor.getWriteMethod().invoke(object, value);
    }
  }
}
