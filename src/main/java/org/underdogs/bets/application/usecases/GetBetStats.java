package org.underdogs.bets.application.usecases;

import java.util.Map;
import org.underdogs.bets.domain.BetStatus;

public interface GetBetStats {
  Map<BetStatus, Long> handle();
}
