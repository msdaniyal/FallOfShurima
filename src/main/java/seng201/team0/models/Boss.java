package seng201.team0.models;

/**
 * Represents a boss enemy encountered at the end of a quest.
 * Extends Character with gold rewards, loyalty effects,
 * and a special ability that fires on a configurable frequency.
 * @author Mohammed, Xinyi
 */
public class Boss extends Character {

    // ------------------------------------- MEMBERS -------------------------------------

    private int goldDrop;
    private int loyaltyEffectOnWin;
    private int loyaltyEffectOnLoss;
    private String description;
    private BossAbility ability;
    private int abilityFrequency;

    // ------------------------------------- CONSTRUCTORS -------------------------------------

    /**
     * Constructs a Boss with all required properties including a special ability.
     * @param name Boss display name
     * @param maxHealth Boss max HP
     * @param attack Boss attack stat
     * @param defense Boss defense stat
     * @param goldDrop Gold rewarded on defeat
     * @param loyaltyEffectOnWin Loyalty delta applied to all party members on player victory
     * @param loyaltyEffectOnLoss Loyalty delta applied to all party members on player defeat
     * @param loreDescription Flavour text shown during the encounter screen
     * @param ability The boss's special combat ability
     * @param abilityFrequency Every N rounds the ability triggers (0 = every round or N/A)
     */
    public Boss(String name,
                int maxHealth,
                int attack,
                int defense,
                int goldDrop,
                int loyaltyEffectOnWin,
                int loyaltyEffectOnLoss,
                String loreDescription,
                BossAbility ability,
                int abilityFrequency) {

        super(name, maxHealth, attack, defense);
        this.goldDrop = goldDrop;
        this.loyaltyEffectOnWin = loyaltyEffectOnWin;
        this.loyaltyEffectOnLoss = loyaltyEffectOnLoss;
        this.description = loreDescription;
        this.ability = ability;
        this.abilityFrequency = abilityFrequency;
    }

    // ------------------------------------- GETTERS -------------------------------------

    /**
     * @return Gold dropped when this boss is defeated
     */
    public int getGoldDrop() {
        return goldDrop;
    }

    /**
     * @return Loyalty change applied to all party members when the player wins
     */
    public int getLoyaltyEffectOnWin() {
        return loyaltyEffectOnWin;
    }

    /**
     * @return Loyalty change applied to all party members when the player loses
     */
    public int getLoyaltyEffectOnLoss() {
        return loyaltyEffectOnLoss;
    }

    /**
     * @return Flavour text shown on the boss encounter screen
     */
    public String getLoreDescription() {
        return description;
    }

    /**
     * @return This boss's special ability type
     */
    public BossAbility getAbility() {
        return ability;
    }

    /**
     * @return How frequently the ability triggers (every N rounds)
     */
    public int getAbilityFrequency() {
        return abilityFrequency;
    }

    // ------------------------------------- OTHERS -------------------------------------

    /**
     * Returns whether the boss ability should trigger this round.
     * NONE always returns false.
     * TRUE_DAMAGE and HEAL_ON_HIT are passive and always active — returns true every round.
     * All others trigger when round % abilityFrequency == 0 and abilityFrequency > 0.
     * @param round The current combat round number (1-indexed)
     * @return True if the ability fires this round
     */
    public boolean shouldTriggerAbility(int round) {
        if (ability == BossAbility.NONE) {
            return false;
        }
        // Passive abilities are always active — checked inline in BossFight
        if (ability == BossAbility.TRUE_DAMAGE || ability == BossAbility.HEAL_ON_HIT) {
            return true;
        }
        if (abilityFrequency <= 0) {
            return false;
        }
        return round % abilityFrequency == 0;
    }
}