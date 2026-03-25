package org.underdogs.matches.application.usecases;

import java.util.List;
import org.underdogs.matches.domain.Match;

public interface SearchMatches {
  List<Match> handle();
}
