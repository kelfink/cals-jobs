package gov.ca.cwds.jobs.cap.users;

import com.amazonaws.services.cognitoidp.model.UserType;
import gov.ca.cwds.PerryProperties;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.rest.api.domain.auth.UserAuthorization;
import gov.ca.cwds.service.scripts.IdmMappingScript;

import javax.script.ScriptException;
import java.io.IOException;

public class PerryProperties2 extends PerryProperties {
  //private IdentityManagerConfiguration identityManager;

  public PerryProperties2 (){
    super();
    setIdentityManager(new PerryProperties.IdentityManagerConfiguration());
    try {
      getIdentityManager().setIdmMappingScript(new IdmMappingScript2("config/idm.groovy"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static class IdmMappingScript2 extends IdmMappingScript {

    public IdmMappingScript2(String path) throws IOException {
      super(path);
    }
//
//    @Override
//    public User map(UserType cognitoUser, UserAuthorization userInfo) throws ScriptException {
//      User user = new User();
//      return user;
//    }
  }

}
