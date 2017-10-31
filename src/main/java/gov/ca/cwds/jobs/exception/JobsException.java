package gov.ca.cwds.jobs.exception;

/**
 * Base class for runtime exceptions. Specialized exceptions should extend this class.
 * 
 * @author CWDS API Team
 */
@SuppressWarnings("serial")
public class JobsException extends RuntimeException {

  /**
   * Pointless constructor. Use another one.
   */
  @SuppressWarnings("unused")
  private JobsException() {
    // Default, no-op.
  }

  /**
   * @param message error message
   */
  public JobsException(String message) {
    super(message);
  }

  /**
   * @param cause original Throwable
   */
  public JobsException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message error message
   * @param cause original Throwable
   */
  public JobsException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message error message
   * @param cause original Throwable
   * @param enableSuppression whether or not suppression is enabled or disabled
   * @param writableStackTrace whether or not the stack trace should be writable
   */
  public JobsException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
