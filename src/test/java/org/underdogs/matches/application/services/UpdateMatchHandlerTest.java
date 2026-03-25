package org.underdogs.matches.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.underdogs.matches.application.gateways.MatchRepository;
import org.underdogs.matches.application.models.UpdateMatchRequest;
import org.underdogs.matches.domain.Match;
import org.underdogs.matches.domain.MatchId;
import org.underdogs.matches.domain.MatchStatus;
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.shared.error.BusinessException;
import org.underdogs.teams.application.gateways.TeamRepository;
import org.underdogs.teams.domain.Game;
import org.underdogs.teams.domain.Team;
import org.underdogs.teams.domain.TeamId;
import org.underdogs.tournaments.application.gateways.TournamentRepository;
import org.underdogs.tournaments.domain.Tournament;
import org.underdogs.tournaments.domain.TournamentId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateMatchHandlerTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    private UpdateMatchHandler handler;

    @BeforeEach
    void setUp() {
        handler = new UpdateMatchHandler(matchRepository, teamRepository, tournamentRepository);
    }

    @Test
    void shouldUpdateMatchToFinishedSuccessfully() {
        Team team1 = Team.create(new TeamId("team-1"), "T1", "T1", Game.LEAGUE_OF_LEGENDS);
        Team team2 = Team.create(new TeamId("team-2"), "Gen.G", "GEN", Game.LEAGUE_OF_LEGENDS);
        Tournament tournament = Tournament.create(
                new TournamentId("tournament-1"),
                "Worlds 2026",
                Game.LEAGUE_OF_LEGENDS,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 11, 5)
        );

        MatchId matchId = new MatchId("match-1");
        Match match = Match.create(
                matchId,
                team1,
                team2,
                tournament,
                Game.LEAGUE_OF_LEGENDS,
                LocalDateTime.of(2026, 10, 10, 18, 0)
        );

        UpdateMatchRequest request = new UpdateMatchRequest(
                null,
                null,
                null,
                null,
                null,
                MatchStatus.FINISHED,
                2,
                1,
                "team-1"
        );

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(teamRepository.findById(new TeamId("team-1"))).thenReturn(Optional.of(team1));

        handler.handle(matchId, request);

        verify(matchRepository).save(match);
        assertEquals(MatchStatus.FINISHED, match.getStatus());
        assertEquals(2, match.getTeam1Score());
        assertEquals(1, match.getTeam2Score());
        assertEquals(team1, match.getWinner());
    }

    @Test
    void shouldThrowWhenMatchNotFound() {
        MatchId matchId = new MatchId("match-1");

        UpdateMatchRequest request = new UpdateMatchRequest(
                null,
                null,
                null,
                null,
                null,
                MatchStatus.FINISHED,
                2,
                1,
                "team-1"
        );

        when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> handler.handle(matchId, request)
        );

        assertEquals(BusinessErrorCodes.MATCH_NOT_FOUND, exception.getCode());
        verify(matchRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenWinnerIsNotPartOfMatch() {
        Team team1 = Team.create(new TeamId("team-1"), "T1", "T1", Game.LEAGUE_OF_LEGENDS);
        Team team2 = Team.create(new TeamId("team-2"), "Gen.G", "GEN", Game.LEAGUE_OF_LEGENDS);
        Team outsider = Team.create(new TeamId("team-3"), "G2", "G2", Game.LEAGUE_OF_LEGENDS);

        Tournament tournament = Tournament.create(
                new TournamentId("tournament-1"),
                "Worlds 2026",
                Game.LEAGUE_OF_LEGENDS,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 11, 5)
        );

        MatchId matchId = new MatchId("match-1");
        Match match = Match.create(
                matchId,
                team1,
                team2,
                tournament,
                Game.LEAGUE_OF_LEGENDS,
                LocalDateTime.of(2026, 10, 10, 18, 0)
        );

        UpdateMatchRequest request = new UpdateMatchRequest(
                null,
                null,
                null,
                null,
                null,
                MatchStatus.FINISHED,
                2,
                1,
                "team-3"
        );

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(teamRepository.findById(new TeamId("team-3"))).thenReturn(Optional.of(outsider));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> handler.handle(matchId, request)
        );

        assertEquals(BusinessErrorCodes.INVALID_MATCH_WINNER, exception.getCode());
        verify(matchRepository, never()).save(any());
    }
}