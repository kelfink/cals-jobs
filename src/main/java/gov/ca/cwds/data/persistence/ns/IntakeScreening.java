package gov.ca.cwds.data.persistence.ns;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import gov.ca.cwds.data.ns.NsPersistentObject;

/**
 * {@link NsPersistentObject} representing a Person.
 * 
 * @author CWDS API Team
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "screenings")
public class IntakeScreening extends NsPersistentObject {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_hotline_contact_id")
  @SequenceGenerator(name = "seq_hotline_contact_id", sequenceName = "seq_hotline_contact_id",
      allocationSize = 50)
  @Column(name = "hotline_contact_id")
  private Long id;

  @Column(name = "SCREENING_ID")
  private String screeningId;

  @Column(name = "REFERENCE")
  private String reference;

  @Column(name = "STARTED_AT")
  private String startedAt;

  @Column(name = "ENDED_AT")
  private String endedAt;

  @Column(name = "INCIDENT_DATE")
  private String incidentDate;

  @Column(name = "LOCATION_TYPE")
  private String locationType;

  @Column(name = "COMMUNICATION_METHOD")
  private String communicationMethod;

  @Column(name = "SCREENING_NAME")
  private String screeningName;

  @Column(name = "SCREENING_DECISION")
  private String screeningDecision;

  @Column(name = "INCIDENT_COUNTY")
  private String incidentCounty;

  @Column(name = "REPORT_NARRATIVE")
  private String reportNarrative;

  @Column(name = "ASSIGNEE")
  private String assignee;

  @Column(name = "ADDITIONAL_INFORMATION")
  private String additionalInformation;

  @Column(name = "SCREENING_DECISION_DETAIL")
  private String screeningDecisionDetail;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "contact_address_id")
  private Address contactAddress;

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "screening")
  private Set<Participant> participants = new HashSet<>(0);

  /**
   * Default constructor
   * 
   * Required for Hibernate
   */
  public IntakeScreening() {
    super();
  }

  /**
   * Constructor
   * 
   * @param reference The reference
   */
  public IntakeScreening(String reference) {
    this.reference = reference;
  }

  // /**
  // * Constructor
  // *
  // * @param reference The reference
  // * @param endedAt The endedAt date
  // * @param incidentCounty The incident county
  // * @param incidentDate The incident date
  // * @param locationType The location type
  // * @param communicationMethod The communication method
  // * @param name The name of the screening
  // * @param responseTime The response time
  // * @param screeningDecision The screening decision
  // * @param startedAt The started at date
  // * @param narrative The narrative
  // * @param contactAddress The contact address
  // * @param participants The list of participants
  // */
  // public IntakeScreening(String reference, Date endedAt, String incidentCounty, Date
  // incidentDate,
  // String locationType, String communicationMethod, String name, String responseTime,
  // String screeningDecision, Date startedAt, String narrative, Address contactAddress,
  // Set<Participant> participants) {
  // super();
  //
  // this.reference = reference;
  // this.endedAt = endedAt;
  // this.incidentCounty = incidentCounty;
  // this.incidentDate = incidentDate;
  // this.locationType = locationType;
  // this.communicationMethod = communicationMethod;
  // this.name = name;
  // this.responseTime = responseTime;
  // this.screeningDecision = screeningDecision;
  // this.startedAt = startedAt;
  // this.narrative = narrative;
  // this.contactAddress = contactAddress;
  //
  // if (participants != null && !participants.isEmpty()) {
  // this.participants.addAll(participants);
  // }
  // }

  /**
   * {@inheritDoc}
   * 
   * @see gov.ca.cwds.data.persistence.PersistentObject#getPrimaryKey()
   */
  @Override
  public Long getPrimaryKey() {
    return getId();
  }

  /**
   * @return the id
   */
  public Long getId() {
    return id;
  }

}
