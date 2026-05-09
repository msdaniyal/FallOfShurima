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
     */
    public Game(Guild guild, Difficulty difficulty) {
        this.guild = guild;
        this.difficulty = difficulty;
        this.quests = new ArrayList<>();
        quests.add(new Quest1(difficulty));
        quests.add(new Quest2(difficulty));
        quests.add(new Quest3(difficulty));
        quests.add(new Quest4(difficulty));
        quests.add(new Quest5(difficulty));
        quests.add(new Quest6(difficulty));
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
     */
    public void advanceToNextQuest() {
        currentQuestIndex++;
        if (currentQuestIndex >= 5) {
            if (getCurrentLoyalty() >= getLoyaltyThreshold()) {
                playerWon = true;
                gameOver = true;
            } else {
                quests.get(5).unlock();
            }
        } else {
            quests.get(currentQuestIndex).unlock();
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