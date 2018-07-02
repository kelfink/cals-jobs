package gov.ca.cwds.jobs.common.savepoint;

import gov.ca.cwds.jobs.common.mode.JobMode;
import java.nio.file.Path;

/**
 * Created by Alexander Serbin on 2/5/2018.
 */
public interface SavePointContainerService<S extends SavePoint, J extends JobMode> {

  Path getSavePointFile();

  boolean savePointContainerExists();

  SavePointContainer<? extends S, J> readSavePointContainer(
      Class<? extends SavePointContainer<? extends S, J>> savePointContainerClass);

  void writeSavePointContainer(SavePointContainer<? extends S, J> savePointContainer);

}
