package gov.ca.cwds.jobs;

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
import gov.ca.cwds.jobs.config.JobOptions;

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
        LOGGER.error("Failed to load referral ranges!", e);
      }

      final JobOptions opts = job.getOpts();
      if (opts != null && opts.isRangeGiven()) {
        final List<Pair<String, String>> list = new ArrayList<>();

        final int start = ((int) opts.getStartBucket()) - 1;
        final int end = ((int) opts.getEndBucket()) - 1;

        LOGGER.warn("KEY RANGES: {} to {}", opts.getStartBucket(), opts.getEndBucket());
        for (int i = start; i <= end; i++) {
          list.add(ret.get(i));
        }

        ret = list;
      }

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

    return Collections.unmodifiableList(ret);
  }

}
