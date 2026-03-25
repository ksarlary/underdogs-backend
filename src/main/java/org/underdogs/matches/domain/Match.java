package org.underdogs.matches.domain;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.shared.error.BusinessException;
import org.underdogs.teams.domain.Game;
import org.underdogs.teams.domain.Team;
import org.underdogs.tournaments.domain.Tournament;

@Entity
@Table(name = "matches")
@Access(AccessType.FIELD)
public class Match {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long technicalId;

  @Embedded private MatchId id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "team1_technical_id", nullable = false)
  private Team team1;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "team2_technical_id", nullable = false)
  private Team team2;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "tournament_technical_id", nullable = false)
  private Tournament tournament;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 40)
  private Game game;

  @Column(nullable = false)
  private LocalDateTime scheduledAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private MatchStatus status;

  @Column private Integer team1Score;

  @Column private Integer team2Score;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "winner_technical_id")
  private Team winner;

  protected Match() {}

  private Match(
      MatchId id,
      Team team1,
      Team team2,
      Tournament tournament,
      Game game,
      LocalDateTime scheduledAt,
      MatchStatus status,
      Integer team1Score,
      Integer team2Score,
      Team winner) {
    this.id = id;
    this.team1 = team1;
    this.team2 = team2;
    this.tournament = tournament;
    this.game = game;
    this.scheduledAt = scheduledAt;
    this.status = status;
    this.team1Score = team1Score;
    this.team2Score = team2Score;
    this.winner = winner;
  }

  public static Match create(
      MatchId id,
      Team team1,
      Team team2,
      Tournament tournament,
      Game game,
      LocalDateTime scheduledAt) {
    validateTeams(team1, team2);

    return new Match(
        id, team1, team2, tournament, game, scheduledAt, MatchStatus.SCHEDULED, null, null, null);
  }

  public void update(
      Team team1,
      Team team2,
      Tournament tournament,
      Game game,
      LocalDateTime scheduledAt,
      MatchStatus status,
      Integer team1Score,
      Integer team2Score,
      Team winner) {
    Team newTeam1 = team1 != null ? team1 : this.team1;
    Team newTeam2 = team2 != null ? team2 : this.team2;

    validateTeams(newTeam1, newTeam2);

    this.team1 = newTeam1;
    this.team2 = newTeam2;

    if (tournament != null) {
      this.tournament = tournament;
    }
    if (game != null) {
      this.game = game;
    }
    if (scheduledAt != null) {
      this.scheduledAt = scheduledAt;
    }
    if (status != null) {
      this.status = status;
    }

    this.team1Score = team1Score;
    this.team2Score = team2Score;
    this.winner = winner;

    validateWinner();
  }

  private static void validateTeams(Team team1, Team team2) {
    if (team1 == null || team2 == null) {
      throw new IllegalArgumentException("A match must have two teams");
    }

    if (team1.getId().equals(team2.getId())) {
      throw new BusinessException(
          BusinessErrorCodes.INVALID_MATCH_TEAMS, "A team cannot play against itself");
    }
  }

  private void validateWinner() {
    if (winner != null
        && !winner.getId().equals(team1.getId())
        && !winner.getId().equals(team2.getId())) {
      throw new BusinessException(
          BusinessErrorCodes.INVALID_MATCH_WINNER,
          "Winner must be one of the two teams in the match");
    }
  }

  public Long getTechnicalId() {
    return technicalId;
  }

  public MatchId getId() {
    return id;
  }

  public Team getTeam1() {
    return team1;
  }

  public Team getTeam2() {
    return team2;
  }

  public Tournament getTournament() {
    return tournament;
  }

  public Game getGame() {
    return game;
  }

  public LocalDateTime getScheduledAt() {
    return scheduledAt;
  }

  public MatchStatus getStatus() {
    return status;
  }

  public Integer getTeam1Score() {
    return team1Score;
  }

  public Integer getTeam2Score() {
    return team2Score;
  }

  public Team getWinner() {
    return winner;
  }
}
