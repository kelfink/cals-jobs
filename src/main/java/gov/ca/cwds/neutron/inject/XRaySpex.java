package gov.ca.cwds.neutron.inject;

import com.google.inject.Inject;

import gov.ca.cwds.neutron.launch.LaunchCommandSettings;

public class XRaySpex implements AtomCommandControlManager {

  private final LaunchCommandSettings settings;

  @Inject
  public XRaySpex(final LaunchCommandSettings settings) {
    this.settings = settings;
  }

  @Override
  public void initCommandControl() {
    // do something
  }

  public LaunchCommandSettings getSettings() {
    return settings;
  }

}
