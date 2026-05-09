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
     */
    public void checkEndCondition() {
        if (guild.isWiped()) {
            gameOver = true;
            playerWon = false;
        } else if (getCurrentLoyalty() >= getLoyaltyThreshold() && currentQuestIndex == 5) {
            gameOver = true;
            playerWon = true;
        } else if (currentQuestIndex == 5) {
            Quest quest6 = quests.get(5);
            BossFight zoeFight = quest6.getBossFights().get(0);
            if (zoeFight.isFightOver(guild) && !zoeFight.isPlayerWon()) {
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

    /**
     * @return The current loyalty points of the adventurer
     */
    public int getCurrentLoyalty() { return adventurer.getLoyalty(); }
}