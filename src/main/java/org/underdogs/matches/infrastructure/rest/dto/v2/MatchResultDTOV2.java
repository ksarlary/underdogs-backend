package org.underdogs.matches.infrastructure.rest.dto.v2;

public record MatchResultDTOV2(MatchInfo match, TeamsInfo teams, WinnerInfo winner) {

  public record MatchInfo(String id, String status) {}

  public record TeamsInfo(TeamResult home, TeamResult away) {}

  public record TeamResult(String id, String name, Integer score) {}

  public record WinnerInfo(String id, String name) {}
}
