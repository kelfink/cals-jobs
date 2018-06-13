package gov.ca.cwds.jobs.cap.users;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.jobs.common.ChangedDTO;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ChangedUserDTO implements ChangedDTO<User>, Serializable {

  private static final long serialVersionUID = -4368941604862881357L;

  private User user;
  private RecordChangeOperation recordChangeOperation;

  public ChangedUserDTO(User user, RecordChangeOperation recordChangeOperation) {
    this.user = user;
    this.recordChangeOperation = recordChangeOperation;
  }

  @Override
  public RecordChangeOperation getRecordChangeOperation() {
    return recordChangeOperation;
  }

  @Override
  public User getDTO() {
    return user;
  }

  @Override
  public String getId() {
    return user.getId();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    ChangedUserDTO that = (ChangedUserDTO) o;

    return new EqualsBuilder()
            .append(user, that.user)
            .append(recordChangeOperation, that.recordChangeOperation)
            .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
            .append(user)
            .append(recordChangeOperation)
            .toHashCode();
  }
}
