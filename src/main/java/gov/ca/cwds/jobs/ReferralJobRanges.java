package gov.ca.cwds.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.data.persistence.cms.EsPersonReferral;
import gov.ca.cwds.data.persistence.cms.ReplicatedPersonReferrals;

public class ReferralJobRanges {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReferralJobRanges.class);

  private Pair<String, String> splitLine(String line) {
    final String[] pieces = line.split("\t");
    return Pair.of(pieces[0], pieces[1]);
  }

  public List<Pair<String, String>> getPartitionRanges(
      BasePersonIndexerJob<ReplicatedPersonReferrals, EsPersonReferral> job) {
    List<Pair<String, String>> ret = new ArrayList<>();

    final boolean isMainframe = job.isDB2OnZOS();
    final String schema = BasePersonIndexerJob.getDBSchemaName().toUpperCase();
    if (isMainframe
        && (schema.endsWith("RSQ") || schema.endsWith("REP") || schema.endsWith("DSM"))) {

      // ----------------------------
      // z/OS, LARGE data set:
      // ORDER: a,z,A,Z,0,9
      // ----------------------------

      try (@SuppressWarnings("unchecked")
      Stream<String> lines =
          IOUtils.readLines(this.getClass().getResourceAsStream("/referral_ranges.tsv")).stream()) {
        ret = lines.sequential().map(this::splitLine).collect(Collectors.toList());
      } catch (Exception e) {
        LOGGER.error("Oops!", e);
      }

      // Small test range.
      // ret.add(Pair.of("AajKlhI4rg", "AajKlhI4rh"));

    } else if (isMainframe) {
      // ----------------------------
      // z/OS, small data set:
      // ORDER: a,z,A,Z,0,9
      // ----------------------------
      ret.add(Pair.of("aaaaaaaaaa", "9999999999"));
    } else {
      // ----------------------------
      // Linux:
      // ORDER: 0,9,a,A,z,Z
      // ----------------------------
      ret.add(Pair.of("0000000000", "ZZZZZZZZZZ"));
    }

    return ret;
  }

}
