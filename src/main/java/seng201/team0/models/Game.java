package seng201.team0.models;

import java.util.ArrayList;
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
    private Adventurer adventurer;

    /**
     * Constructs a Game.
     * @param guild The player's guild created during setup
     * @param difficulty The chosen difficulty
     * TODO: Initialise all 6 quest subclasses and add to quests list
     */
    public Game(Guild guild, Difficulty difficulty) {
        this.guild = guild;
        this.difficulty = difficulty;
        this.quests = new ArrayList<>();
        this.currentQuestIndex = 0;
        this.gameOver = false;
        this.playerWon = false;
    }

    /**
     * Returns the current active quest.
     * @return The quest at currentQuestIndex
     */
    public Quest getCurrentQuest() { return quests.get(currentQuestIndex); }

    /**
     * Advances to the next quest after the current one is completed.
     * TODO: Unlock the next quest by calling quest.unlock()
     * TODO: If currentQuestIndex >= 5 (all required quests done), check loyalty threshold
     * TODO: If loyalty below threshold, unlock Quest 6 (Zoe) instead
     */
    public void advanceToNextQuest() {
        currentQuestIndex++;

        if (currentQuestIndex >= 5) {
            if (getCurrentLoyalty() >= getLoyaltyThreshold()) {
                playerWon = true;
                gameOver = true;
            } else {

            }
        }
    }

    /**
     * Checks all end conditions and updates gameOver and playerWon flags.
     * TODO: Check if all 5 required quests done and loyalty threshold met
     * TODO: Check if Quest 6 was lost (Zoe fight) — gameOver = true, playerWon = false
     */
    public void checkEndCondition() {
        if (guild.isWiped()) {
            gameOver = true;
            playerWon = false;
        }
        if () {
            if () {
                gameOver = true;
                playerWon = false;
            }
        }

    }

    /**
     * @return The player's guild
     */
    public Guild getGuild() { return guild; }

    /**
     * @return The list of all quests
     */
    public List<Quest> getQuests() { return quests; }

    /**
     * @return The chosen difficulty
     */
    public Difficulty getDifficulty() { return difficulty; }

    /**
     * @return True if the game is over
     */
    public boolean isGameOver() { return gameOver; }

    /**
     * @return True if the player won
     */
    public boolean isPlayerWon() { return playerWon; }

    /**
     * @return The loyalty threshold for this difficulty
     */
    public int getLoyaltyThreshold() { return difficulty.getLoyaltyThreshold(); }

    public int getCurrentLoyalty() { return adventurer.getLoyalty(); }
}