package org.underdogs.players.application.usecases;

import org.underdogs.players.application.models.UpdatePlayerRequest;
import org.underdogs.players.domain.PlayerId;

public interface UpdatePlayer {
  void handle(PlayerId id, UpdatePlayerRequest request);
}
