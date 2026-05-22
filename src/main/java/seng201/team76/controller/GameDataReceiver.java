package seng201.team76.controller;

import seng201.team76.models.Game;

/**
 * Implemented by controllers that can receive the shared Game object when loaded from FXML.
 * This avoids reflection and makes navigation fail at compile time if the method changes.
 */
public interface GameDataReceiver {
    /**
     * Receives the shared game object after a screen is loaded.
     *
     * @param game The current game
     */
    void setGameData(Game game);
}
