package org.underdogs.players.application.usecases;

import org.underdogs.players.application.models.CreatePlayerRequest;
import org.underdogs.players.domain.PlayerId;

public interface CreatePlayer {
  PlayerId handle(CreatePlayerRequest request);
}
