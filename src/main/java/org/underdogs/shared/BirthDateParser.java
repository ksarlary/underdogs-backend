package org.underdogs.shared;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.stereotype.Component;
import org.underdogs.shared.error.BusinessException;

@Component
public class BirthDateParser {

  private static final List<DateTimeFormatter> SUPPORTED_FORMATS =
      List.of(
          DateTimeFormatter.ofPattern("dd-MM-yyyy"),
          DateTimeFormatter.ofPattern("dd.MM.yyyy"),
          DateTimeFormatter.ofPattern("dd/MM/yyyy"),
          DateTimeFormatter.ISO_LOCAL_DATE);

  public LocalDate parse(String rawBirthDate) {
    if (rawBirthDate == null || rawBirthDate.isBlank()) {
      throw new BusinessException(
          "MISSING_BIRTHDATE", "Birth date is missing from the identity provider");
    }

    for (DateTimeFormatter formatter : SUPPORTED_FORMATS) {
      try {
        return LocalDate.parse(rawBirthDate, formatter);
      } catch (DateTimeParseException ignored) {
      }
    }

    throw new BusinessException(
        "INVALID_BIRTHDATE_FORMAT",
        "Birth date format is invalid. Supported formats: dd-MM-yyyy, dd.MM.yyyy, dd/MM/yyyy, yyyy-MM-dd");
  }
}
