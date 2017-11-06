package gov.ca.cwds.jobs.rocket.referral;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.data.persistence.cms.EsPersonReferral;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonReferrals;
import gov.ca.cwds.jobs.BasePersonIndexerJob;
import gov.ca.cwds.jobs.config.FlightPlan;
import gov.ca.cwds.neutron.log.JobLogs;

/**
 * Get key ranges by platform and job size.
 * 
 * @author CWDS API Team
 */
public class ReferralJobRanges {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReferralJobRanges.class);

  /**
   * Split tab delim line of key range, beginning and end.
   * 
   * @param line line to parse
   * @return key pair
   */
  private Pair<String, String> splitLine(String line) {
    final String[] pieces = line.split("\t");
    return Pair.of(pieces[0], pieces[1]);
  }

  private List<Pair<String, String>> limitRange(
      BasePersonIndexerJob<ReplicatedPersonReferrals, EsPersonReferral> job,
      final List<Pair<String, String>> allKeyPairs) {
    List<Pair<String, String>> ret = new ArrayList<>();
    final FlightPlan opts = job.getOpts();
    if (opts.isRangeGiven()) {
      final List<Pair<String, String>> list = new ArrayList<>();

      final int start = ((int) opts.getStartBucket()) - 1;
      final int end = ((int) opts.getEndBucket()) - 1;

      LOGGER.info("KEY RANGES: {} to {}", start + 1, end + 1);
      for (int i = start; i <= end; i++) {
        list.add(allKeyPairs.get(i));
      }

      ret = list;
    }

    return ret;
  }

  /**
   * Get key ranges by platform and job size.
   * 
   * @param job referrals job
   * @return key pairs
   */
  public List<Pair<String, String>> getPartitionRanges(
      BasePersonIndexerJob<ReplicatedPersonReferrals, EsPersonReferral> job) {
    List<Pair<String, String>> ret = new ArrayList<>();

    if (job.isLargeDataSet()) {
      LOGGER.info("z/OS, LARGE data set, ORDER: a,z,A,Z,0,9");

      try (@SuppressWarnings("unchecked")
      final Stream<String> lines =
          IOUtils.readLines(this.getClass().getResourceAsStream("/referral_ranges.tsv")).stream()) {
        ret = lines.sequential().map(this::splitLine).collect(Collectors.toList());
      } catch (Exception e) {
        throw JobLogs.runtime(LOGGER, e, "Failed to load referral ranges!");
      }

      limitRange(job, ret);
    } else if (job.isDB2OnZOS()) {
      LOGGER.info("z/OS, small data set, ORDER: a,z,A,Z,0,9");
      ret.add(Pair.of("aaaaaaaaaa", "9999999999"));
    } else {
      LOGGER.info("Linux, ORDER: 0,9,a,A,z,Z");
      ret.add(Pair.of("0000000000", "ZZZZZZZZZZ"));
    }

    return Collections.unmodifiableList(ret);
  }

}
