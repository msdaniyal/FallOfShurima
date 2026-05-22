package seng201.team76.models;

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
     * Selects an unlocked quest as the active quest before opening its screen.
     * Controllers use this instead of guessing from UI state.
     * @param questIndex zero-based quest index
     * @return true if the quest exists and is unlocked
     */
    public boolean selectQuest(int questIndex) {
        if (questIndex < 0 || questIndex >= quests.size()) {
            return false;
        }

        Quest quest = quests.get(questIndex);
        if (!quest.isUnlocked()) {
            return false;
        }

        currentQuestIndex = questIndex;
        return true;
    }

    /**
     * Gets the active quest index.
     *
     * @return zero-based active quest index
     */
    public int getCurrentQuestIndex() {
        return currentQuestIndex;
    }

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

        if (!completedQuest.isCompleted()) {
            completedQuest.markCompleted();
        }

        // Quest 5 decides the ending path. If loyalty is high enough, the
        // player wins immediately. If not, Zoe/Quest 6 unlocks.
        if (completedQuestIndex == 4) {
            if (guild.checkLoyaltyThreshold(getLoyaltyThreshold())) {
                gameOver = true;
                playerWon = true;
                isCompleted = true;
                currentQuestIndex = 4;
            } else {
                quests.get(5).unlock();
                currentQuestIndex = 5;
                gameOver = false;
                playerWon = false;
            }
            return;
        }

        // Quest 6 is the hard-path final ending. Winning its boss fight wins
        // the game; losing it loses the game.
        if (completedQuestIndex == 5) {
            BossFight finalFight = quests.get(5).getBossFights().get(0);
            gameOver = true;
            playerWon = finalFight.isPlayerWon();
            isCompleted = playerWon;
            currentQuestIndex = 5;
            return;
        }

        if (completedQuestIndex == currentQuestIndex) {
            int nextQuestIndex = completedQuestIndex + 1;
            if (nextQuestIndex < 5) {
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
            return;
        }

        if (quests.get(4).isCompleted()) {
            if (guild.checkLoyaltyThreshold(getLoyaltyThreshold())) {
                gameOver = true;
                playerWon = true;
                isCompleted = true;
            } else if (!quests.get(5).isCompleted()) {
                quests.get(5).unlock();
                currentQuestIndex = 5;
            }
        }

        if (quests.get(5).isCompleted()) {
            BossFight finalFight = quests.get(5).getBossFights().get(0);
            gameOver = true;
            playerWon = finalFight.isPlayerWon();
            isCompleted = playerWon;
        }
    }

    /**
     * Checks whether the game has reached a completed ending.
     *
     * @return true if the game has been completed
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Marks the game as completed.
     */
    public void markCompleted() {
        this.isCompleted = true;
    }

    /**
     * Gets the player's guild.
     *
     * @return The player's guild
     */
    public Guild getGuild() {
        return guild;
    }

    /**
     * Gets all quests in order.
     *
     * @return The list of all quests
     */
    public List<Quest> getQuests() {
        return quests;
    }

    /**
     * Gets the selected difficulty.
     *
     * @return The chosen difficulty
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Checks whether the game is over.
     *
     * @return True if the game is over
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Checks whether the player won.
     *
     * @return True if the player won
     */
    public boolean isPlayerWon() {
        return playerWon;
    }

    /**
     * Gets the loyalty threshold needed for the ending check.
     *
     * @return The loyalty threshold for this difficulty
     */
    public int getLoyaltyThreshold() {
        // Intentionally higher than any normal loyalty value so players reach
        // Quest 6 and meet Zoe after Quest 5.
        return 101;
    }

    /**
     * Adds together the current loyalty of the main party.
     *
     * @return The current loyalty points of the adventurer
     */
    public int getCurrentLoyalty() {
        int totalLoyalty = 0;

        for (Adventurer adventurer : guild.getMainParty()) {
            totalLoyalty += adventurer.getLoyalty();
        }

        return totalLoyalty;
    }

    /**
     * Checks whether the party meets the loyalty threshold.
     *
     * @return True if the true-ending loyalty threshold has been met by every living main-party member.
     */
    public boolean hasMetLoyaltyThreshold() {
        return guild.checkLoyaltyThreshold(getLoyaltyThreshold());
    }

    /**
     * Gets the title shown on the ending screen.
     *
     * @return A short ending message for the result screen.
     */
    public String getEndingTitle() {
        return "The End";
    }

    /**
     * Gets the ending description shown on the result screen.
     *
     * @return Longer ending text for the result screen.
     */
    public String getEndingDescription() {
        return "All Darkin are sealed until they return.";
    }

    /**
     * Checks whether the ending uses the Twilight/Zoe path.
     *
     * @return true when the ending was reached through Zoe's final sealing scene.
     */
    public boolean isTwilightEnding() {
        return currentQuestIndex == 5 || quests.get(5).isCompleted() || quests.get(5).isUnlocked();
    }
}
