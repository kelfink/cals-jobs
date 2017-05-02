package gov.ca.cwds.dao;

import java.io.Serializable;

import gov.ca.cwds.data.std.ApiPersonAware;

public interface ApiMultiplePersonAware extends Serializable {

  ApiPersonAware[] getPersons();

}
