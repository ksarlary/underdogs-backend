package org.underdogs.bets.application.services;

import org.springframework.stereotype.Component;

@Component
public class BetCoefficientCalculator {

  private static final double DEFAULT_COEFFICIENT = 2.0;
  private static final long SEED_POOL = 100L;
  private static final double MIN_COEFFICIENT = 1.01;

  public double calculate(long totalPool, long selectedTeamPool) {
    if (totalPool == 0) {
      return DEFAULT_COEFFICIENT;
    }

    double adjustedTotalPool = totalPool + (2.0 * SEED_POOL);
    double adjustedSelectedTeamPool = selectedTeamPool + SEED_POOL;

    double coefficient = adjustedTotalPool / adjustedSelectedTeamPool;

    return Math.max(MIN_COEFFICIENT, roundToTwoDecimals(coefficient));
  }

  public long calculatePotentialGain(long amount, double coefficient) {
    return Math.round(amount * coefficient);
  }

  private double roundToTwoDecimals(double value) {
    return Math.round(value * 100.0) / 100.0;
  }
}
