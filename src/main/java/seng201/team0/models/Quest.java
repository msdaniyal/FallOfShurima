package seng201.team0.models;

import java.util.List;

/**
 * Abstract base class for all quests in the game.
 * Each of the 6 quests extends this class with hardcoded events and boss sequences.
 * Difficulty is stored here so subclasses can pass it through to BossFight.
 * @author Mohammed, Xinyi
 */
public abstract class Quest {

    private int id;
    private String name;
    private String loreIntroduction;
    private boolean isUnlocked;
    private int madnessAffliction;
    private Difficulty difficulty;
    private List<BossFight> bossFights;

    /**
     * Constructs a Quest.
     * @param id The quest number (1-6)
     * @param name The quest display name
     * @param loreIntroduction Flavour text shown at the start of the quest
     * @param madnessAffliction How much madness is added to party members after this quest
     * @param difficulty The game difficulty, passed through to BossFight instances
     */
    public Quest(int id, String name, String loreIntroduction, int madnessAffliction, Difficulty difficulty) {
        this.id = id;
        this.name = name;
        this.loreIntroduction = loreIntroduction;
        this.madnessAffliction = madnessAffliction;
        this.difficulty = difficulty;
        this.isUnlocked = (id == 1); // Only Quest 1 starts unlocked
        this.bossFights = initialiseBossFights();
    }

    // ── Abstract methods ──────────────────────────────────────────────────

    /**
     * Initialises the sequence of boss fights for this quest.
     * Each subclass hardcodes its own boss sequence here.
     * Use getDifficulty() to pass difficulty into each BossFight constructor.
     * @return Ordered list of BossFight objects for this quest
     */
    protected abstract List<BossFight> initialiseBossFights();

    /**
     * Runs the expedition events for this quest.
     * Each subclass hardcodes its own branching choices and outcomes here.
     * Choices directly update adventurer loyalty, health and gold on the guild.
     * @param guild The player's guild
     * TODO: Hook this up to the ExpeditionController to display choices in the GUI.
     * TODO: Each choice should call guild.getMainParty() and update each adventurer.
     * TODO: Example: adventurer.adjustLoyalty(+10) for a good leadership choice.
     * TODO: Example: adventurer.adjustLoyalty(-10) for a selfish or cowardly choice.
     * TODO: Gold changes should call guild.addGold() or guild.spendGold().
     */
    public abstract void runEvents(Guild guild);

    // ── Concrete methods ──────────────────────────────────────────────────

    /**
     * Updates all party members after the quest completes.
     * Applies madness affliction to all main party members.
     * Called after all boss fights are resolved.
     * @param guild The player's guild
     * TODO: Also call guild.removeAbandoned() after this to clean out any loyalty=0 adventurers.
     * TODO: If this is Quest 5, call guild.collapseOpposingFaction() before updateCharacters.
     * TODO: If this is Quest 5, call guild.lockParty() after collapseOpposingFaction.
     */
    public void updateCharacters(Guild guild) {
        for (Adventurer adventurer : guild.getMainParty()) {
            adventurer.updateAfterQuest(0, 0, madnessAffliction);
        }
        guild.removeAbandoned();
    }

    /**
     * Unlocks this quest, making it available to play.
     * Called when the previous quest is completed successfully.
     */
    public void unlock() {
        this.isUnlocked = true;
    }

    // ── Getters ───────────────────────────────────────────────────────────

    /**
     * @return The quest's id number
     */
    public int getId() {
        return id;
    }

    /**
     * @return The quest's display name
     */
    public String getName() {
        return name;
    }

    /**
     * @return The lore introduction text shown at the start of the quest
     */
    public String getLoreIntroduction() {
        return loreIntroduction;
    }

    /**
     * @return Whether this quest is currently unlocked
     */
    public boolean isUnlocked() {
        return isUnlocked;
    }

    /**
     * @return The madness affliction value applied to party members after this quest
     */
    public int getMadnessAffliction() {
        return madnessAffliction;
    }

    /**
     * @return The game difficulty (used by subclasses to construct BossFights)
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * @return The ordered list of boss fights for this quest
     */
    public List<BossFight> getBossFights() { return bossFights; }
}