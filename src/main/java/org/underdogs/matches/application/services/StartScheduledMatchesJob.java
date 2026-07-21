package org.underdogs.matches.application.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.underdogs.matches.application.gateways.MatchRepository;
import org.underdogs.matches.domain.Match;
import org.underdogs.shared.TimeProvider;

@Service
class StartScheduledMatchesJob {

  private final MatchRepository matchRepository;
  private final TimeProvider timeProvider;

  StartScheduledMatchesJob(MatchRepository matchRepository, TimeProvider timeProvider) {
    this.matchRepository = matchRepository;
    this.timeProvider = timeProvider;
  }

  @Scheduled(fixedDelayString = "${underdogs.matches.auto-start.fixed-delay-ms:60000}")
  @Transactional
  public void startScheduledMatches() {
    Instant nowInstant = timeProvider.now();
    LocalDateTime nowDateTime = LocalDateTime.ofInstant(nowInstant, ZoneOffset.UTC);

    List<Match> matches = matchRepository.findScheduledMatchesToStart(nowDateTime);

    for (Match match : matches) {
      match.startLive(nowInstant);
      matchRepository.save(match);
    }
  }
}
