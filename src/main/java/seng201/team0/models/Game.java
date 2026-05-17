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
    private boolean isCompleted;

    /**
     * Constructs a Game.
     *
     * @param guild      The player's guild created during setup
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
        this.isCompleted = false;
    }

    /**
     * Returns the current active quest.
     *
     * @return The quest at currentQuestIndex
     */
    public Quest getCurrentQuest() {
        return quests.get(currentQuestIndex);
    }

    /**
     * Advances to the next quest after the current one is completed.
     */
    /**
     * Advances to the next quest after a specific quest is completed.
     * Replaying an already completed quest will not unlock more quests.
     *
     * @param completedQuestIndex The index of the quest that was just completed, starting from 0.
     */
    public void advanceToNextQuest(int completedQuestIndex) {
        if (completedQuestIndex < 0 || completedQuestIndex >= quests.size()) {
            return;
        }

        Quest completedQuest = quests.get(completedQuestIndex);

        // If this quest has already been completed before, do not unlock anything new.
        if (completedQuest.isCompleted()) {
            return;
        }

        completedQuest.markCompleted();

        // Only unlock the next quest if the player completed the highest unlocked quest.
        if (completedQuestIndex == currentQuestIndex) {
            int nextQuestIndex = completedQuestIndex + 1;

            if (nextQuestIndex < quests.size()) {
                quests.get(nextQuestIndex).unlock();
                currentQuestIndex = nextQuestIndex;
            }
        }

        checkEndCondition();
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

    public boolean isCompleted() {
        return isCompleted;
    }

    public void markCompleted() {
        this.isCompleted = true;
    }

    /**
     * @return The player's guild
     */
    public Guild getGuild() {
        return guild;
    }

    /**
     * @return The list of all quests
     */
    public List<Quest> getQuests() {
        return quests;
    }

    /**
     * @return The chosen difficulty
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * @return True if the game is over
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * @return True if the player won
     */
    public boolean isPlayerWon() {
        return playerWon;
    }

    /**
     * @return The loyalty threshold for this difficulty
     */
    public int getLoyaltyThreshold() {
        return difficulty.getLoyaltyThreshold();
    }

    /**
     * @return The current loyalty points of the adventurer
     */
    public int getCurrentLoyalty() {
        int totalLoyalty = 0;

        for (Adventurer adventurer : guild.getMainParty()) {
            totalLoyalty += adventurer.getLoyalty();
        }

        return totalLoyalty;
    }
}