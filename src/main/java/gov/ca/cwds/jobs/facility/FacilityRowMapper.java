package gov.ca.cwds.jobs.facility;


import com.fasterxml.jackson.annotation.JsonProperty;
import gov.ca.cwds.data.model.facility.es.ESFacility;
import gov.ca.cwds.jobs.util.jdbc.RowMapper;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.sql.ResultSet;

/**
 * Created by dmitry.rudenko on 4/28/2017.
 */
public class FacilityRowMapper implements RowMapper<ESFacility> {

    @Override
    public ESFacility mapRow(ResultSet resultSet) throws Exception {
        ESFacility facility = new ESFacility();
        mapFields(facility, resultSet);
        return facility;
    }

    private void mapFields(Object object, ResultSet resultSet) throws Exception {
        for(Field field : object.getClass().getDeclaredFields()){
            mapField(object, field, resultSet);
        }
    }

    private void mapField(Object object, Field field, ResultSet resultSet) throws Exception {
        PropertyDescriptor descriptor = new PropertyDescriptor(field.getName(), object.getClass());
        if(field.getType().equals(String.class)) {
            JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
            String jsonName;
            if(jsonProperty!= null) {
                jsonName = jsonProperty.value();
            }
            else {
                jsonName = field.getName();
            }
            String value = resultSet.getString(jsonName);

            descriptor.getWriteMethod().invoke(object, value == null ? "" : value.trim());
        }
        else {
            Object child = field.getType().newInstance();
            mapFields(child, resultSet);
            descriptor.getWriteMethod().invoke(object, child);
        }
    }
}
