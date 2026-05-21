package seng201.team0.models;

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

    protected abstract List<BossFight> initialiseBossFights();

    public abstract void runEvents(Guild guild);

    public void updateCharacters(Guild guild) {
        for (Adventurer adventurer : guild.getMainParty()) {
            adventurer.updateAfterQuest(0, 0, madnessAffliction);
        }
        guild.removeAbandoned();
    }

    public void unlock() {
        this.isUnlocked = true;
    }

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
     */
    protected void setBossFights(List<BossFight> bossFights) {
        this.bossFights = bossFights;
    }

    /**
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

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLoreIntroduction() {
        return loreIntroduction;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public int getMadnessAffliction() {
        return madnessAffliction;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public List<BossFight> getBossFights() {
        return bossFights;
    }
}
