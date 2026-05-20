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
     * @return zero-based active quest index
     */
    public int getCurrentQuestIndex() {
        return currentQuestIndex;
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

    /**
     * @return True if the true-ending loyalty threshold has been met by every living main-party member.
     */
    public boolean hasMetLoyaltyThreshold() {
        return guild.checkLoyaltyThreshold(getLoyaltyThreshold());
    }

    /**
     * @return A short ending message for the result screen.
     */
    public String getEndingTitle() {
        if (playerWon && quests.get(4).isCompleted() && !quests.get(5).isCompleted()) {
            return "True Ending: The Guild Holds";
        }

        if (playerWon) {
            return "Victory: Twilight Survived";
        }

        return "Defeat: The Guild Falls";
    }

    /**
     * @return Longer ending text for the result screen.
     */
    public String getEndingDescription() {
        if (playerWon && quests.get(4).isCompleted() && !quests.get(5).isCompleted()) {
            return "Your party survived the faction war with enough loyalty to avoid Zoe's punishment path.";
        }

        if (playerWon) {
            return "Your party failed the loyalty threshold, but survived Zoe and claimed the hard-path victory.";
        }

        if (guild.isWiped()) {
            return "Every adventurer in the main party has fallen.";
        }

        return "Zoe broke the party that was already fractured.";
    }
}
