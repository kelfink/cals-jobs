package gov.ca.cwds.jobs.cals;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by TPT-2 on 6/13/2017.
 * Calculates date for incremental load
 */
public interface IncrementalLoadDateStrategy {

  /**
   * Must be called inside batch transaction
   * @return date
   */
  LocalDateTime calculateLocalDateTime();

  /**
   * Must be called inside batch transaction
   * @return date
   */
  Date calculateDate();
}
