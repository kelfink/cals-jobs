package gov.ca.cwds.generic.jobs.util;

/**
 * 
 * @param <I> input type
 * @param <O> output type
 * @author CWDS TPT-2
 */
@FunctionalInterface
public interface JobProcessor<I, O> {

  /**
   * Transform item I into O.
   * 
   * @param item input
   * @return an O
   */
  O process(I item);

}
