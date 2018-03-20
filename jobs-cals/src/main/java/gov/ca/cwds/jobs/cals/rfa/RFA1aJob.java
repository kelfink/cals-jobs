package gov.ca.cwds.jobs.cals.rfa;

import com.google.inject.Inject;
import gov.ca.cwds.cals.inject.CalsnsSessionFactory;
import gov.ca.cwds.jobs.common.job.impl.JobImpl;
import org.hibernate.SessionFactory;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public class RFA1aJob extends JobImpl {

    @Inject
    @CalsnsSessionFactory
    private SessionFactory calsnsSessionFactory;

    @Override
    public void close() {
        super.close();
        calsnsSessionFactory.close();
    }

}
