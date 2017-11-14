package gov.ca.cwds.neutron.vox.jmx;

import org.quartz.JobDetail;
import org.quartz.JobKey;

import gov.ca.cwds.neutron.atom.AtomLaunchPad;

public interface VoxLaunchPadMBean extends AtomLaunchPad {

  String getRocketName();

  String getTriggerName();

  JobDetail getJd();

  JobKey getJobKey();

}
