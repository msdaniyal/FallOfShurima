package seng201.team0.models;

import java.util.List;

/**
 * Top level game state container.
 * Holds the guild, all quests, difficulty and current game progress.
 * @author Mohammed, Xinyi
 */
public class Game {

    private Guild guild;
    private List<Quest> quests;
    private Difficulty difficulty;
    private int currentQuestIndex;
    private boolean gameOver;
    private boolean playerWon;

    /**
     * Constructs a Game.
     * @param guild The player's guild created during setup
     * @param difficulty The chosen difficulty
     * TODO: Initialise all 6 quest subclasses and add to quests list
     * TODO: Set currentQuestIndex to 0
     * TODO: Set gameOver and playerWon to false
     */
    public Game(Guild guild, Difficulty difficulty) {
        // TODO: Implement constructor
    }

    /**
     * Returns the current active quest.
     * @return The quest at currentQuestIndex
     * TODO: Return quests.get(currentQuestIndex)
     */
    public Quest getCurrentQuest() {
        // TODO: Implement
        return null;
    }

    /**
     * Advances to the next quest after the current one is completed.
     * TODO: Increment currentQuestIndex
     * TODO: Unlock the next quest by calling quest.unlock()
     * TODO: If currentQuestIndex >= 5 (all required quests done), check loyalty threshold
     * TODO: If loyalty above threshold, set playerWon = true and gameOver = true
     * TODO: If loyalty below threshold, unlock Quest 6 (Zoe) instead
     */
    public void advanceToNextQuest() {
        // TODO: Implement
    }

    /**
     * Checks all end conditions and updates gameOver and playerWon flags.
     * TODO: Check guild.isWiped() — if true, gameOver = true, playerWon = false
     * TODO: Check if all 5 required quests done and loyalty threshold met
     * TODO: Check if Quest 6 was lost (Zoe fight) — gameOver = true, playerWon = false
     */
    public void checkEndCondition() {
        // TODO: Implement
    }

    /**
     * @return The player's guild
     */
    public Guild getGuild() {
        // TODO: Return guild
        return null;
    }

    /**
     * @return The list of all quests
     */
    public List<Quest> getQuests() {
        // TODO: Return quests
        return null;
    }

    /**
     * @return The chosen difficulty
     */
    public Difficulty getDifficulty() {
        // TODO: Return difficulty
        return null;
    }

    /**
     * @return True if the game is over
     */
    public boolean isGameOver() {
        // TODO: Return gameOver
        return false;
    }

    /**
     * @return True if the player won
     */
    public boolean isPlayerWon() {
        // TODO: Return playerWon
        return false;
    }

    /**
     * @return The loyalty threshold for this difficulty
     * TODO: Return difficulty.getLoyaltyThreshold()
     */
    public int getLoyaltyThreshold() {
        // TODO: Implement
        return 0;
    }
}