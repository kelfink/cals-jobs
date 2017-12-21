package gov.ca.cwds.generic.data.persistence.cms;

import static gov.ca.cwds.generic.jobs.util.transform.JobTransformUtils.ifNull;

import gov.ca.cwds.data.std.ApiMarker;
import gov.ca.cwds.rest.api.domain.cms.SystemCodeCache;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parse legacy relationship strings.
 * 
 * @author CWDS API Team
 */
public final class CmsRelationship implements ApiMarker {

  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = LoggerFactory.getLogger(
      CmsRelationship.class);

  private static final Pattern RGX_RELATIONSHIP = Pattern
      .compile("^\\s*([A-Za-z0-9 _-]+)[/]?([A-Za-z0-9 _-]+)?\\s*(\\([A-Za-z0-9 _-]+\\))?\\s*$"); // NOSONAR

  short sysCodeId;
  String primaryRel = "";
  String secondaryRel = "";
  String relContext = "";

  /**
   * Construct a relationship parser from a syscodeid.
   * 
   * @param relCode legacy syscodeid
   */
  public CmsRelationship(final Short relCode) {
    sysCodeId = relCode.shortValue();
    final gov.ca.cwds.rest.api.domain.cms.SystemCode code =
        SystemCodeCache.global().getSystemCode(relCode);
    final String wholeRel = ifNull(code.getShortDescription());

    final Matcher m = RGX_RELATIONSHIP.matcher(wholeRel);
    if (m.matches()) {
      for (int i = 0; i <= m.groupCount(); i++) {
        final String s = m.group(i);
        switch (i) {
          case 1:
            primaryRel = s.trim();
            break;

          case 2:
            secondaryRel = s.trim();
            break;

          case 3:
            relContext = StringUtils.isNotBlank(s)
                ? s.replaceAll("\\(", "").replaceAll("\\)", "").trim() : "";
            break;

          default:
            break;
        }
      }
    } else {
      LOGGER.trace("NO MATCH!! rel={}", wholeRel);
    }

  }

  public short getSysCodeId() {
    return sysCodeId;
  }

  public void setSysCodeId(short sysCodeId) {
    this.sysCodeId = sysCodeId;
  }

  public String getPrimaryRel() {
    return primaryRel;
  }

  public void setPrimaryRel(String primaryRel) {
    this.primaryRel = primaryRel;
  }

  public String getSecondaryRel() {
    return secondaryRel;
  }

  public void setSecondaryRel(String secondaryRel) {
    this.secondaryRel = secondaryRel;
  }

  public String getRelContext() {
    return relContext;
  }

  public void setRelContext(String relContext) {
    this.relContext = relContext;
  }

  @Override
  public String toString() {
    return "CmsRelationship [sysCodeId=" + sysCodeId + ", primaryRel=" + primaryRel
        + ", secondaryRel=" + secondaryRel + ", relContext=" + relContext + "]";
  }

}
