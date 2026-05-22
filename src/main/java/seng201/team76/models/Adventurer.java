package seng201.team76.models;

/**
 * Represents a playable adventurer in the guild.
 * Extends Character with loyalty, pay, faction, madness and description.
 * @author Mohammed, Xinyi
 */
public class Adventurer extends Character {

    // ------------------------------------- MEMBERS -------------------------------------

    private int loyalty;
    private int pay;
    private int madness;
    private Faction faction;
    private String description;
    private boolean abandoned;

    // ------------------------------------- CONSTRUCTORS  -------------------------------------

    /**
     * Constructs an Adventurer with all required stats.
     * Starting loyalty is derived from the adventurer's faction and the player's chosen faction.
     * @param name name
     * @param maxHealth maxHP
     * @param attack attack
     * @param defense defence
     * @param pay gold per expedition
     * @param faction The faction this adventurer is loyal to
     * @param playerFaction The faction the player chose at setup
     * @param description about the character
     */

    public Adventurer(String name, int maxHealth, int attack, int defense,
                      int pay, Faction faction, Faction playerFaction, String description) {
        super(name, maxHealth, attack, defense);
        this.pay = pay;
        this.faction = faction;
        this.description = description;
        this.loyalty = faction.getStartingLoyalty(playerFaction);
        this.madness = 0;
        this.abandoned = false;
    }

    // ------------------------------------- GETTERS -------------------------------------

    /**
     * Gets the adventurer's current loyalty.
     *
     * @return The adventurer's current loyalty value
     */
    public int getLoyalty() {
        return loyalty;
    }

    /**
     * Gets the gold cost to bring this adventurer on an expedition.
     *
     * @return The gold cost to bring this adventurer on an expedition
     */
    public int getPay() {
        return pay;
    }

    /**
     * Gets the adventurer's current madness.
     *
     * @return The adventurer's current madness value
     */
    public int getMadness() {
        return madness;
    }

    /**
     * Gets the adventurer's faction.
     *
     * @return The adventurer's faction allegiance
     */
    public Faction getFaction() {
        return faction;
    }

    /**
     * Gets the adventurer's description.
     *
     * @return The adventurer's flavour description shown on their character card
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks whether this adventurer has abandoned the guild.
     *
     * @return True if this adventurer has abandoned the guild
     */
    public boolean getAbandoned() {
        return abandoned;
    }

    // ------------------------------------- OTHERS -------------------------------------

    /**
     * Adjusts the adventurer's loyalty by a delta value.
     * Loyalty is clamped between 0 and 100.
     * If loyalty hits 0 the adventurer is marked as abandoned.
     * @param delta The amount to change loyalty by (positive or negative)
     */
    public void adjustLoyalty(int delta) {
        this.loyalty = Math.max(0, Math.min(100, this.loyalty + delta));
        if (this.loyalty == 0) {
            this.abandoned = true;
        }
    }

    /**
     * Increases the adventurer's madness by a given amount.
     * Madness is clamped between 0 and 100.
     * High madness passively reduces loyalty over time.
     * @param amount The amount of madness to add
     */
    public void increaseMadness(int amount) {
        this.madness = Math.min(100, this.madness + amount);
        if (this.madness >= 75) {
            adjustLoyalty(-5);
        }
    }

    /**
     * Updates the adventurer's stats after a quest based on outcome.
     * Called at the end of each quest.
     * @param loyaltyDelta Change in loyalty
     * @param healthDelta Change in current health
     * @param madnessDelta Change in madness
     */
    public void updateAfterQuest(int loyaltyDelta, int healthDelta, int madnessDelta) {
        adjustLoyalty(loyaltyDelta);
        setCurrentHealth(getCurrentHealth() + healthDelta);
        if (madnessDelta > 0) {
            increaseMadness(madnessDelta);
        }
    }

    /**
     * Checks whether this adventurer has enough loyalty.
     *
     * @param threshold The minimum loyalty needed
     * @return True if this adventurer's loyalty is at or above the given threshold
     */
    public boolean isLoyal(int threshold) {
        return loyalty >= threshold;
    }
}
