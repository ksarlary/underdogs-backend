package org.underdogs.shared.error;

public final class BusinessErrorCodes {

  private BusinessErrorCodes() {}

  public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
  public static final String USER_ALREADY_EXISTS = "USER_ALREADY_EXISTS";
  public static final String USER_TOO_YOUNG = "USER_TOO_YOUNG";
  public static final String USER_BLOCKED = "USER_BLOCKED";

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

  public static final String INVALID_KIBBLES_AMOUNT = "INVALID_KIBBLES_AMOUNT";
  public static final String INVALID_BET_AMOUNT = "INVALID_BET_AMOUNT";
  public static final String MATCH_NOT_OPEN_FOR_BETS = "MATCH_NOT_OPEN_FOR_BETS";
  public static final String TEAM_NOT_IN_MATCH = "TEAM_NOT_IN_MATCH";
  public static final String BET_ALREADY_EXISTS = "BET_ALREADY_EXISTS";
  public static final String BET_NOT_FOUND = "BET_NOT_FOUND";
  public static final String INVALID_BET_COEFFICIENT = "INVALID_BET_COEFFICIENT";
  public static final String INVALID_POTENTIAL_GAIN = "INVALID_POTENTIAL_GAIN";
  public static final String BET_ALREADY_RESOLVED = "BET_ALREADY_RESOLVED";
  public static final String MATCH_WINNER_REQUIRED = "MATCH_WINNER_REQUIRED";
  public static final String MATCH_RESULT_REQUIRED = "MATCH_RESULT_REQUIRED";
  public static final String MATCH_DRAW_NOT_ALLOWED = "MATCH_DRAW_NOT_ALLOWED";
  public static final String INVALID_MATCH_STATUS_TRANSITION = "INVALID_MATCH_STATUS_TRANSITION";
  public static final String MATCH_NOT_EDITABLE = "MATCH_NOT_EDITABLE";
  public static final String INVALID_MATCH_WINNER_SCORE = "INVALID_MATCH_WINNER_SCORE";
}
