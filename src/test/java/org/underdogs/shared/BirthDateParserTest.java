package org.underdogs.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.underdogs.shared.error.BusinessErrorCodes;
import org.underdogs.shared.error.BusinessException;

class BirthDateParserTest {

  private final BirthDateParser parser = new BirthDateParser();

  @Test
  void shouldParseDateWithDashFormat() {
    LocalDate result = parser.parse("12-10-2000");

    assertEquals(LocalDate.of(2000, 10, 12), result);
  }

  @Test
  void shouldParseDateWithDotFormat() {
    LocalDate result = parser.parse("12.10.2000");

    assertEquals(LocalDate.of(2000, 10, 12), result);
  }

  @Test
  void shouldParseDateWithSlashFormat() {
    LocalDate result = parser.parse("12/10/2000");

    assertEquals(LocalDate.of(2000, 10, 12), result);
  }

  @Test
  void shouldParseIsoFormat() {
    LocalDate result = parser.parse("2000-10-12");

    assertEquals(LocalDate.of(2000, 10, 12), result);
  }

  @Test
  void shouldThrowWhenBirthDateIsMissing() {
    BusinessException exception = assertThrows(BusinessException.class, () -> parser.parse(null));

    assertEquals(BusinessErrorCodes.MISSING_BIRTHDATE, exception.getCode());
  }

  @Test
  void shouldThrowWhenBirthDateFormatIsInvalid() {
    BusinessException exception =
        assertThrows(BusinessException.class, () -> parser.parse("hello"));

    assertEquals(BusinessErrorCodes.INVALID_BIRTHDATE_FORMAT, exception.getCode());
  }
}
