package gov.ca.cwds.jobs.exception;

/**
 * Base class of batch job runtime exceptions. Specialized exceptions should extend this class.
 * 
 * @author CWDS API Team
 */
@SuppressWarnings("serial")
public class JobsException extends RuntimeException {

  /**
   * Default, no-op ctor.
   */
  public JobsException() {
    // Default, no-op.
  }

  public JobsException(String message) {
    super(message);
  }

  public JobsException(Throwable cause) {
    super(cause);
  }

  public JobsException(String message, Throwable cause) {
    super(message, cause);
  }

  public JobsException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
