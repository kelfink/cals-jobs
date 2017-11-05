package gov.ca.cwds.neutron.rocket;

import org.hibernate.SessionFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.data.std.ApiGroupNormalizer;
import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.jobs.schedule.FlightRecorder;

public abstract class InitialLoadJdbcRocket<T extends PersistentObject, M extends ApiGroupNormalizer<?>>
    extends BasePersonIndexerJob<T, M> {

  public InitialLoadJdbcRocket(BaseDaoImpl<T> jobDao, ElasticsearchDao esDao,
      String lastJobRunTimeFilename, ObjectMapper mapper, SessionFactory sessionFactory,
      FlightRecorder jobHistory, FlightPlan opts) {
    super(jobDao, esDao, lastJobRunTimeFilename, mapper, sessionFactory, jobHistory, opts);
  }

  @Override
  public String getJdbcOrderBy() {
    return " ORDER BY x.clt_identifier ";
  }

  @Override
  public String getInitialLoadQuery(String dbSchemaName) {
    final StringBuilder buf = new StringBuilder();
    buf.append("SELECT x.* FROM ").append(dbSchemaName).append('.').append(getInitialLoadViewName())
        .append(" x WHERE x.clt_identifier BETWEEN ':fromId' AND ':toId' ");
    if (!getOpts().isLoadSealedAndSensitive()) {
      buf.append(" AND x.CLT_SENSTV_IND = 'N' ");
    }

    buf.append(getJdbcOrderBy()).append(" FOR READ ONLY WITH UR ");
    return buf.toString();
  }

}
