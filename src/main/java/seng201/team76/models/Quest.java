package seng201.team76.models;

import java.util.List;

/**
 * Abstract base class for all quests in the game.
 * Each of the 6 quests extends this class with hardcoded events and boss sequences.
 * Difficulty is stored here so subclasses can pass it through to BossFight.
 */
public abstract class Quest {

    private int id;
    private String name;
    private String loreIntroduction;
    private boolean isUnlocked;
    private boolean isCompleted;
    private int madnessAffliction;
    private Difficulty difficulty;
    private List<BossFight> bossFights;

    /**
     * Creates a quest with its main data.
     *
     * @param id The quest number
     * @param name The quest name
     * @param loreIntroduction The intro text shown for the quest
     * @param madnessAffliction Madness added after the quest
     * @param difficulty The selected difficulty
     */
    public Quest(int id, String name, String loreIntroduction, int madnessAffliction, Difficulty difficulty) {
        this.id = id;
        this.name = name;
        this.loreIntroduction = loreIntroduction;
        this.madnessAffliction = madnessAffliction;
        this.difficulty = difficulty;
        this.isUnlocked = (id == 1);
        this.isCompleted = false;
        this.bossFights = initialiseBossFights();
    }

    /**
     * Builds the boss fights for this quest.
     *
     * @return The boss fights in order
     */
    protected abstract List<BossFight> initialiseBossFights();

    /**
     * Runs any quest events that should happen outside boss fights.
     *
     * @param guild The player's guild
     */
    public abstract void runEvents(Guild guild);

    /**
     * Applies normal post-quest changes to the party.
     *
     * @param guild The player's guild
     */
    public void updateCharacters(Guild guild) {
        for (Adventurer adventurer : guild.getMainParty()) {
            adventurer.updateAfterQuest(0, 0, madnessAffliction);
        }
        guild.removeAbandoned();
    }

    /**
     * Unlocks this quest.
     */
    public void unlock() {
        this.isUnlocked = true;
    }

    /**
     * Marks this quest as completed.
     */
    public void markCompleted() {
        this.isCompleted = true;
    }

    /**
     * Rebuilds every boss fight so a failed quest can be restarted cleanly.
     * Quest story choices remain completed; only the battle sequence resets.
     */
    public void resetBossFights() {
        this.bossFights = initialiseBossFights();
    }

    /**
     * Allows dynamic quests, such as Quest 5's rival guild fight, to replace
     * their generated fight list after reading the player's guild state.
     *
     * @param bossFights The new boss fight list
     */
    protected void setBossFights(List<BossFight> bossFights) {
        this.bossFights = bossFights;
    }

    /**
     * Finds the first boss fight that is not finished yet.
     *
     * @param guild The player's guild
     * @return index of the first unfinished fight, used when returning from the map.
     */
    public int getFirstUnfinishedFightIndex(Guild guild) {
        for (int i = 0; i < bossFights.size(); i++) {
            if (!bossFights.get(i).isFightOver(guild)) {
                return i;
            }
        }
        return bossFights.size();
    }

    /**
     * Gets the quest id.
     *
     * @return The quest id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the quest name.
     *
     * @return The quest name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the quest intro text.
     *
     * @return The lore introduction
     */
    public String getLoreIntroduction() {
        return loreIntroduction;
    }

    /**
     * Checks whether this quest is unlocked.
     *
     * @return true if the quest is unlocked
     */
    public boolean isUnlocked() {
        return isUnlocked;
    }

    /**
     * Checks whether this quest is completed.
     *
     * @return true if the quest is completed
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Gets the madness added after this quest.
     *
     * @return The madness affliction value
     */
    public int getMadnessAffliction() {
        return madnessAffliction;
    }

    /**
     * Gets the difficulty used by this quest.
     *
     * @return The difficulty
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Gets the boss fights for this quest.
     *
     * @return The boss fights in order
     */
    public List<BossFight> getBossFights() {
        return bossFights;
    }
}
