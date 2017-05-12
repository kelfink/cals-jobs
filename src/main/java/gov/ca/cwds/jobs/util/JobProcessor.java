package gov.ca.cwds.jobs.util;


/**
 * @author CWDS Elasticsearch Team
 * 
 * @param <I> input type
 * @param <O> output type
 */
public interface JobProcessor<I, O> {

  O process(I item) throws Exception;

}
