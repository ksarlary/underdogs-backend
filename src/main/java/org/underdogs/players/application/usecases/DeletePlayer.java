package org.underdogs.players.application.usecases;

import org.underdogs.players.domain.PlayerId;

public interface DeletePlayer {
    void handle(PlayerId id);
}