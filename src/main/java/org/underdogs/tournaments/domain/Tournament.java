package org.underdogs.tournaments.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.underdogs.matches.domain.Match;
import org.underdogs.teams.domain.Game;

@Entity
@Table(name = "tournaments")
@Access(AccessType.FIELD)
public class Tournament {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long technicalId;

  @Embedded private TournamentId id;

  @Column(nullable = false, unique = true, length = 100)
  private String name;

  @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
  @Column(nullable = false, length = 40)
  private Game game;

  @Column(nullable = false)
  private LocalDate startDate;

  @Column(nullable = false)
  private LocalDate endDate;

  @OneToMany(mappedBy = "tournament", fetch = FetchType.LAZY)
  private List<Match> matches = new ArrayList<>();

  protected Tournament() {}

  private Tournament(
      TournamentId id, String name, Game game, LocalDate startDate, LocalDate endDate) {
    this.id = id;
    this.name = name;
    this.game = game;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public static Tournament create(
      TournamentId id, String name, Game game, LocalDate startDate, LocalDate endDate) {
    validateDates(startDate, endDate);
    return new Tournament(id, name, game, startDate, endDate);
  }

  public void update(String name, Game game, LocalDate startDate, LocalDate endDate) {
    String newName = this.name;
    Game newGame = this.game;
    LocalDate newStartDate = this.startDate;
    LocalDate newEndDate = this.endDate;

    if (name != null && !name.isBlank()) {
      newName = name;
    }
    if (game != null) {
      newGame = game;
    }
    if (startDate != null) {
      newStartDate = startDate;
    }
    if (endDate != null) {
      newEndDate = endDate;
    }

    validateDates(newStartDate, newEndDate);

    this.name = newName;
    this.game = newGame;
    this.startDate = newStartDate;
    this.endDate = newEndDate;
  }

  private static void validateDates(LocalDate startDate, LocalDate endDate) {
    if (startDate == null || endDate == null) {
      throw new IllegalArgumentException("Tournament dates cannot be null");
    }
    if (endDate.isBefore(startDate)) {
      throw new IllegalArgumentException("End date cannot be before start date");
    }
  }

  public Long getTechnicalId() {
    return technicalId;
  }

  public TournamentId getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Game getGame() {
    return game;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public List<Match> getMatches() {
    return matches;
  }
}
