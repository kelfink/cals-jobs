package gov.ca.cwds.jobs.exception;

/**
 * Base class of batch Neutron job checked exceptions. Checked exceptions should extend this class.
 * 
 * @author CWDS API Team
 */
@SuppressWarnings("serial")
public class NeutronException extends Exception {

  /**
   * Pointless constructor. Use another one.
   */
  @SuppressWarnings("unused")
  private NeutronException() {
    // Default, no-op.
  }

  /**
   * @param message error message
   */
  public NeutronException(String message) {
    super(message);
  }

  /**
   * @param cause original Throwable
   */
  public NeutronException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message error message
   * @param cause original Throwable
   */
  public NeutronException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * @param message error message
   * @param cause original Throwable
   * @param enableSuppression whether or not suppression is enabled or disabled
   * @param writableStackTrace whether or not the stack trace should be writable
   */
  public NeutronException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
