package seng201.team0.models;

/**
 * Represents a boss enemy encountered at the end of a quest.
 * Extends Character with gold drop and loyalty effect on win/loss.
 * @author Mohammed, Xinyi
 */
public class Boss extends Character {

    private int goldDrop;
    private int loyaltyEffectOnWin;
    private int loyaltyEffectOnLoss;
    private String description;

    // ------------------------------------- CONSTRUCTORS -------------------------------------

    /**
     * Constructs a Boss with all required properties.
     * @param name name
     * @param maxHealth HP
     * @param attack The boss's attack stat
     * @param defense The boss's defense stat
     * @param goldDrop The amount of gold dropped when the boss is defeated
     * @param loyaltyEffectOnWin Loyalty change applied to all party members on win
     * @param loyaltyEffectOnLoss Loyalty change applied to all party members on loss
     * @param description Flavour text shown during the boss encounter
     */
    public Boss(String name, int maxHealth, int attack, int defense,
                int goldDrop, int loyaltyEffectOnWin, int loyaltyEffectOnLoss,
                String loreDescription) {
        super(name, maxHealth, attack, defense);
        this.goldDrop = goldDrop;
        this.loyaltyEffectOnWin = loyaltyEffectOnWin;
        this.loyaltyEffectOnLoss = loyaltyEffectOnLoss;
        this.description = loreDescription;
    }

    // ------------------------------------- GETTERS -------------------------------------

    /**
     * @return The amount of gold dropped when this boss is defeated
     */
    public int getGoldDrop() {
        return goldDrop;
    }

    /**
     * @return The loyalty change applied to all party members when this boss is defeated
     */
    public int getLoyaltyEffectOnWin() {
        return loyaltyEffectOnWin;
    }

    /**
     * @return The loyalty change applied to all party members when this boss defeats the party
     */
    public int getLoyaltyEffectOnLoss() {
        return loyaltyEffectOnLoss;
    }

    /**
     * @return The lore description shown during the boss encounter screen
     */
    public String getLoreDescription() {
        return description;
    }

    /**
     * Calculates the damage this boss deals to the party.
     * Base damage is attack minus average party defense, minimum 1.
     * @param partyDefense The average defense of the player's party
     * @return The damage dealt
     */
    public int getDamage(int partyDefense) {
        return Math.max(1, getAttack() - partyDefense);
    }

}