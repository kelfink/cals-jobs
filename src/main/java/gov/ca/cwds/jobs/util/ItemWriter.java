package gov.ca.cwds.jobs.util;

import java.util.List;

/**
 * Created by dmitry.rudenko on 4/28/2017.
 */
public interface ItemWriter<T> extends JobComponent {

    void write(List<T> items) throws Exception;

}