package gov.ca.cwds.jobs.common.exception;

/**
 * Base class of batch job runtime exceptions. Specialized exceptions should extend this class.
 * 
 * @author CWDS API Team
 */
public class JobsException extends RuntimeException {

  public JobsException(String message) {
    super(message);
  }

  public JobsException(Throwable cause) {
    super(cause);
    JobExceptionHandler.handleException(cause);
  }

  public JobsException(String message, Throwable cause) {
    super(message, cause);
    JobExceptionHandler.handleException(message, cause);
  }

}
