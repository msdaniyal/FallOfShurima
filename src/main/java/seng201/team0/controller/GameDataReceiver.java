package seng201.team0.controller;

import seng201.team0.models.Game;

/**
 * Implemented by controllers that can receive the shared Game object when loaded from FXML.
 * This avoids reflection and makes navigation fail at compile time if the method changes.
 */
public interface GameDataReceiver {
    void setGameData(Game game);
}
