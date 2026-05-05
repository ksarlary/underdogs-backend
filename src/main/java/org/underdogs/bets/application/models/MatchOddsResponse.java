package org.underdogs.bets.application.models;

public record MatchOddsResponse(String matchId, TeamOddsResponse team1, TeamOddsResponse team2) {}
