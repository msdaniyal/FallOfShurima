package seng201.team0.models;

import java.util.List;
import java.util.Random;

/**
 * Represents a single boss fight within a quest.
 * Turn-based rotation combat — each adventurer attacks in sequence,
 * boss retaliates by targeting the adventurer with lowest defense.
 * The MC can choose to block for a targeted adventurer, taking the hit instead.
 * @author Mohammed, Xinyi
 */
public class BossFight {

    private Boss boss;
    private int sequence;
    private boolean playerWon;
    private Adventurer Adventurer;
    private boolean mcIsBlocking;
    private Random random; //for dice

    /**
     * Constructs a BossFight.
     * @param boss The boss to fight
     * @param sequence The order this fight occurs within the quest (1, 2, 3...)
     * @param Adventurer The main character adventurer who can choose to block
     * TODO: Assign all fields
     * TODO: Set mcIsBlocking to false by default
     */
    public BossFight(Boss boss, int sequence, Adventurer Adventurer) {
        // TODO: Implement constructor
    }

    /**
     * Calculates damage one adventurer deals to the boss.
     * Applies loyalty bonus if loyalty > 70, penalty if loyalty < 30.
     * Applies madness penalty if madness > 75.
     * Adds a random modifier of 1-10.
     * @param adventurer The attacking adventurer
     * @return Damage dealt, minimum 1
     * TODO: Start with adventurer.getAttack()
     * TODO: If loyalty > 70, add 2
     * TODO: If loyalty < 30, subtract 2
     * TODO: If madness > 75, subtract 2
     * TODO: Add random.nextInt(10) + 1
     * TODO: Subtract boss.getDefense()
     * TODO: Return Math.max(1, result)
     */
    public int calcAdventurerDamage(Adventurer adventurer) {
        // TODO: Implement
        return 0;
    }

    /**
     * Calculates damage the boss deals to a target adventurer.
     * Adds a random modifier of 1-10.
     * @param target The adventurer being attacked
     * @return Damage dealt, minimum 1
     * TODO: Start with boss.getAttack()
     * TODO: Subtract target.getDefense()
     * TODO: Add random.nextInt(10) + 1
     * TODO: Return Math.max(1, result)
     */
    public int calcBossDamage(Adventurer target) {
        // TODO: Implement
        return 0;
    }

    /**
     * Finds the adventurer with the lowest defense in the main party.
     * This is the boss's default attack target.
     * @param party The current main party
     * @return The adventurer with lowest defense
     * TODO: Iterate through party and find the one with minimum getDefense()
     * TODO: Skip dead adventurers (isDead())
     */
    public Adventurer findWeakestTarget(List<Adventurer> party) {
        // TODO: Implement
        return null;
    }

    /**
     * Sets whether the MC is blocking for the current target.
     * Called by BossFightController when the player chooses to block.
     * @param blocking True if MC is blocking, false otherwise
     * TODO: Set mcIsBlocking = blocking
     */
    public void setMcBlocking(boolean blocking) {
        // TODO: Implement
    }

    /**
     * Runs the full combat loop to completion.
     * Each round: all living adventurers attack in sequence, then boss attacks weakest target.
     * MC can block for the weakest target, taking the hit instead.
     * Fight ends when boss is dead (player wins) or all adventurers are dead (player loses).
     * After resolution applies gold and loyalty effects.
     * @param guild The player's guild
     * TODO: Loop while !boss.isDead() and !guild.isWiped()
     * TODO: For each living adventurer in mainParty, call calcAdventurerDamage(adventurer)
     *       and apply to boss via boss.setCurrentHealth(boss.getCurrentHealth() - damage)
     * TODO: After party attacks, check if boss.isDead() — if so break
     * TODO: Find weakest target via findWeakestTarget(guild.getMainParty())
     * TODO: If mcIsBlocking and mcAdventurer is alive, MC takes the hit instead of target
     * TODO: Apply calcBossDamage to the actual target (MC or weakest)
     * TODO: Remove dead adventurers from party after each round via guild.removeAbandoned()
     *       (or a separate removeDeadAdventurers helper)
     * TODO: After loop, set playerWon = !guild.isWiped()
     * TODO: If playerWon: apply boss.getLoyaltyEffectOnWin() to all party members
     *                     call guild.addGold(boss.getGoldDrop())
     * TODO: If !playerWon: apply boss.getLoyaltyEffectOnLoss() to all party members
     */
    public void resolveOutcome(Guild guild) {
        // TODO: Implement combat loop
    }

    /**
     * @return The boss for this fight
     */
    public Boss getBoss() {
        // TODO: Return boss
        return null;
    }

    /**
     * @return The sequence number of this fight within the quest
     */
    public int getSequence() {
        // TODO: Return sequence
        return 0;
    }

    /**
     * @return True if the player won this boss fight
     */
    public boolean isPlayerWon() {
        // TODO: Return playerWon
        return false;
    }

    /**
     * @return True if the MC is currently blocking
     */
    public boolean isMcBlocking() {
        // TODO: Return mcIsBlocking
        return false;
    }
}