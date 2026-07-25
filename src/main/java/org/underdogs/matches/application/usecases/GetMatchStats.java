package org.underdogs.matches.application.usecases;

import java.util.Map;
import org.underdogs.matches.domain.MatchStatus;

public interface GetMatchStats {
  Map<MatchStatus, Long> handle();
}
