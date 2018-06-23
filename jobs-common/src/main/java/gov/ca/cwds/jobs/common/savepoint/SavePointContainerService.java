package gov.ca.cwds.jobs.common.savepoint;

import java.nio.file.Path;

/**
 * Created by Alexander Serbin on 2/5/2018.
 */
public interface SavePointContainerService<T extends SavePointContainer> {

  Path getSavePointFile();

  boolean savePointContainerExists();

  T readSavePointContainer();

  void writeSavePointContainer(T savePointContainer);

}
