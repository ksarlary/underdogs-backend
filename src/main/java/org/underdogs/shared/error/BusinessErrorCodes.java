package org.underdogs.shared.error;

public final class BusinessErrorCodes {

  private BusinessErrorCodes() {}

  public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
  public static final String USER_ALREADY_EXISTS = "USER_ALREADY_EXISTS";
  public static final String USER_TOO_YOUNG = "USER_TOO_YOUNG";

  public static final String MISSING_EMAIL = "MISSING_EMAIL";
  public static final String MISSING_USERNAME = "MISSING_USERNAME";
  public static final String MISSING_FIRST_NAME = "MISSING_FIRST_NAME";
  public static final String MISSING_LAST_NAME = "MISSING_LAST_NAME";
  public static final String MISSING_BIRTHDATE = "MISSING_BIRTHDATE";
  public static final String INVALID_BIRTHDATE_FORMAT = "INVALID_BIRTHDATE_FORMAT";

  public static final String TEAM_NOT_FOUND = "TEAM_NOT_FOUND";
  public static final String TEAM_NAME_ALREADY_EXISTS = "TEAM_NAME_ALREADY_EXISTS";
  public static final String TEAM_TAG_ALREADY_EXISTS = "TEAM_TAG_ALREADY_EXISTS";

  public static final String PLAYER_NOT_FOUND = "PLAYER_NOT_FOUND";
  public static final String PLAYER_ALREADY_EXISTS = "PLAYER_ALREADY_EXISTS";

  public static final String INSUFFICIENT_KIBBLES = "INSUFFICIENT_KIBBLES";

  public static final String TOURNAMENT_NOT_FOUND = "TOURNAMENT_NOT_FOUND";
  public static final String TOURNAMENT_ALREADY_EXISTS = "TOURNAMENT_ALREADY_EXISTS";

  public static final String MATCH_NOT_FOUND = "MATCH_NOT_FOUND";
  public static final String INVALID_MATCH_TEAMS = "INVALID_MATCH_TEAMS";
  public static final String INVALID_MATCH_WINNER = "INVALID_MATCH_WINNER";
}
