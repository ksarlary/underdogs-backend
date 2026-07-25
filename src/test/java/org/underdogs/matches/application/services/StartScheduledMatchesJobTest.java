package org.underdogs.matches.application.services;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.underdogs.matches.application.gateways.MatchRepository;
import org.underdogs.matches.domain.Match;
import org.underdogs.shared.TimeProvider;

@ExtendWith(MockitoExtension.class)
class StartScheduledMatchesJobTest {

  @Mock private MatchRepository matchRepository;

  @Mock private TimeProvider timeProvider;

  @Mock private Match match;

  private StartScheduledMatchesJob job;

  @BeforeEach
  void setUp() {
    job = new StartScheduledMatchesJob(matchRepository, timeProvider);
  }

  @Test
  void shouldStartScheduledMatchesWhenScheduledTimeHasPassed() {
    Instant nowInstant = Instant.parse("2026-03-21T10:00:00Z");
    LocalDateTime nowDateTime = LocalDateTime.ofInstant(nowInstant, ZoneOffset.UTC);

    when(timeProvider.now()).thenReturn(nowInstant);
    when(matchRepository.findScheduledMatchesToStart(nowDateTime)).thenReturn(List.of(match));

    job.startScheduledMatches();

    verify(match).startLive(nowInstant);
    verify(matchRepository).save(match);
  }

  @Test
  void shouldDoNothingWhenThereAreNoScheduledMatchesToStart() {
    Instant nowInstant = Instant.parse("2026-03-21T10:00:00Z");
    LocalDateTime nowDateTime = LocalDateTime.ofInstant(nowInstant, ZoneOffset.UTC);

    when(timeProvider.now()).thenReturn(nowInstant);
    when(matchRepository.findScheduledMatchesToStart(nowDateTime)).thenReturn(List.of());

    job.startScheduledMatches();

    verify(match, never()).startLive(nowInstant);
    verify(matchRepository, never()).save(match);
  }
}
