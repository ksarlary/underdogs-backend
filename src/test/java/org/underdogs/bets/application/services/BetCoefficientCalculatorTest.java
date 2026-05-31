package org.underdogs.bets.application.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BetCoefficientCalculatorTest {

  private BetCoefficientCalculator calculator;

  @BeforeEach
  void setUp() {
    calculator = new BetCoefficientCalculator();
  }

  @Test
  void shouldReturnDefaultCoefficientWhenThereAreNoBetsYet() {
    double coefficient = calculator.calculate(0, 0);

    assertEquals(2.0, coefficient);
  }

  @Test
  void shouldCalculateCoefficientForPopularTeam() {
    double coefficient = calculator.calculate(1000, 900);

    assertEquals(1.2, coefficient);
  }

  @Test
  void shouldCalculateCoefficientForUnderdogTeam() {
    double coefficient = calculator.calculate(1000, 100);

    assertEquals(6.0, coefficient);
  }

  @Test
  void shouldRoundCoefficientToTwoDecimals() {
    double coefficient = calculator.calculate(333, 111);

    assertEquals(2.53, coefficient, 0.001);
  }

  @Test
  void shouldNeverReturnCoefficientLowerThanMinimum() {
    double coefficient = calculator.calculate(10_000, 10_000);

    assertEquals(1.01, coefficient);
  }

  @Test
  void shouldCalculatePotentialGain() {
    long potentialGain = calculator.calculatePotentialGain(100, 2.5);

    assertEquals(250, potentialGain);
  }

  @Test
  void shouldRoundPotentialGainToNearestLong() {
    long potentialGain = calculator.calculatePotentialGain(99, 1.33);

    assertEquals(132, potentialGain);
  }
}
