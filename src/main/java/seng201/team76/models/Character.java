package seng201.team76.models;

/**
 * Template for the Characters in the game
 * @author Mohammed, Xinyi
 */
public abstract class Character {

    private String name;
    private int maxHealth;
    private int currentHealth;
    private int attack;
    private int defense;


    /**
     * Constructs a Character with all required stats.
     * @param name name
     * @param maxHealth The max HP
     * @param attack attack
     * @param defense defence
     */
    public Character(String name, int maxHealth, int attack, int defense) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.attack = attack;
        this.defense = defense;
    }

    // ------------------------------------- GETTERS -------------------------------------

    /**
     * Gets the character's name.
     *
     * @return The character's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the character's maximum health.
     *
     * @return The character's maximum health
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Gets the character's current health.
     *
     * @return The character's current health
     */
    public int getCurrentHealth() {
        return currentHealth;
    }

    /**
     * Gets the character's attack stat.
     *
     * @return The character's attack stat
     */
    public int getAttack() {
        return attack;
    }

    /**
     * Gets the character's defense stat.
     *
     * @return The character's defense stat
     */
    public int getDefense() {
        return defense;
    }

    // ------------------------------------- SETTERS -------------------------------------

    /**
     * Sets the character's current health, clamped between 0 and maxHealth.
     * @param currentHealth The new health value
     */
    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = Math.max(0, Math.min(currentHealth, maxHealth));
    }

    /**
     * Sets the character's attack stat.
     * @param attack The new attack value
     */
    public void setAttack(int attack) {
        this.attack = attack;
    }

    /**
     * Sets the character's defense stat.
     * @param defense The new defense value
     */
    public void setDefense(int defense) {
        this.defense = defense;
    }


    // ------------------------------------- OTHERS -------------------------------------
    /**
     * Checks whether this character has no health left.
     *
     * @return True if the character's current health is 0
     */
    public boolean isDead() {
        return currentHealth <= 0;
    }

    /**
     * Resets current health to max health.
     */
    public void resetHealth() {
        this.currentHealth = maxHealth;
    }
}
