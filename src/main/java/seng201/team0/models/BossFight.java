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
    /**
     * TODO:create picture list, address string, create function MemoryPicture
     */
    private Boss boss;
    private int sequence;
    private boolean playerWon;
    private Adventurer mainAdventurer;
    private boolean mcIsBlocking;
    private Random random; //for dice

    /**
     * Constructs a BossFight.
     * @param boss The boss to fight
     * @param sequence The order this fight occurs within the quest (1, 2, 3...)
     * @param mainAdventurer The main character adventurer who can choose to block
     */
    public BossFight(Boss boss, int sequence, Adventurer mainAdventurer) {
        this.boss = boss;
        this.sequence = sequence;
        this.mainAdventurer = mainAdventurer;
        mcIsBlocking = false;
    }


    /**
     * Calculates damage one adventurer deals to the boss.
     * Applies loyalty bonus if loyalty > 70, penalty if loyalty < 30.
     * Applies madness penalty if madness > 75.
     * Adds a random modifier of 1-10.
     * @param adventurer The attacking adventurer
     * @return Damage dealt, minimum 1
     * TODO: Add random.nextInt(10) + 1
     */
    public int calcAdventurerDamage(Adventurer adventurer) {
        int damage;
        damage = adventurer.getAttack();
        if (adventurer.getLoyalty() < 30) {
            damage -= 2;
        } else if (adventurer.getLoyalty() > 70) {
            damage += 2;
        }
        if (adventurer.getMadness() > 75) {
            damage -= 2;
        }
        damage -= boss.getDefense();
        return Math.max(1, damage);
    }

    /**
     * Calculates damage the boss deals to a target adventurer.
     * Adds a random modifier of 1-10.
     * @param target The adventurer being attacked
     * @return Damage dealt, minimum 1
     * TODO: Add random.nextInt(10) + 1
     */
    public int calcBossDamage(Adventurer target) {
        int damage;
        damage = boss.getAttack() - target.getDefense();
        return Math.max(1, damage);
    }

    /**
     * Finds the adventurer with the lowest defense in the main party.
     * This is the boss's default attack target.
     * @param party The current main party
     * @return The adventurer with lowest defense
     */
    public Adventurer findWeakestTarget(List<Adventurer> party) {
        Adventurer weakest = null;
        for (Adventurer adventurer : party) {
            if (!adventurer.isDead()) {
                if (weakest == null || adventurer.getDefense() < weakest.getDefense()) {
                    weakest = adventurer;
                }

            }
        }
        return weakest;
    }

    /**
     * Sets whether the MC is blocking for the current target.
     * Called by BossFightController when the player chooses to block.
     * @param blocking True if MC is blocking, false otherwise
     */
    public void setMcBlocking(boolean blocking) {
        mcIsBlocking = blocking;
    }

    /**
     * Runs the full combat loop to completion.
     * Each round: all living adventurers attack in sequence, then boss attacks weakest target.
     * MC can block for the weakest target, taking the hit instead.
     * Fight ends when boss is dead (player wins) or all adventurers are dead (player loses).
     * After resolution applies gold and loyalty effects.
     * @param guild The player's guild
     */
    public void resolveOutcome(Guild guild) {
        while (!boss.isDead() && !guild.isWiped()) {
            for (Adventurer adventurer: guild.getMainParty()) {
                if (!adventurer.isDead()) {
                    boss.setCurrentHealth(boss.getCurrentHealth() - calcAdventurerDamage(adventurer));
                }
            }
            if (boss.isDead()) {break;}
            Adventurer target = findWeakestTarget(guild.getMainParty());
            if (target == null) {
                break;
            }
            if (mcIsBlocking && mainAdventurer!= null && !mainAdventurer.isDead()) {
                target =  mainAdventurer;
            }
            target.setCurrentHealth(target.getCurrentHealth() - calcBossDamage(target));
            guild.removeDeadAdventurers();
            mcIsBlocking = false;
        }
        playerWon = boss.isDead() && !guild.isWiped();
        if (playerWon) {
            for (Adventurer adventurer : guild.getMainParty()) {
                adventurer.adjustLoyalty(boss.getLoyaltyEffectOnWin());
            }
            guild.addGold(boss.getGoldDrop());
        } else {
            for (Adventurer adventurer : guild.getMainParty()) {
                adventurer.adjustLoyalty(boss.getLoyaltyEffectOnLoss());
            }
        }
    }

    /**
     * @return The boss for this fight
     */
    public Boss getBoss() {
        return boss;
    }

    /**
     * @return The sequence number of this fight within the quest
     */
    public int getSequence() {
        return sequence;
    }

    /**
     * @return True if the player won this boss fight
     */
    public boolean isPlayerWon() {
        return playerWon;
    }

    /**
     * @return True if the MC is currently blocking
     */
    public boolean isMcBlocking() {
        return mcIsBlocking;
    }
}