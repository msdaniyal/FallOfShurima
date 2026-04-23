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
     *
     */
    private Boss boss;
    private int sequence;
    private boolean playerWon;
    private Adventurer mainAdventurer;
    private boolean mcIsBlocking;
    private boolean rewardsApplied;

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
        this.mcIsBlocking = false;
        this.playerWon = false;
        this.rewardsApplied = false;
    }


    /**
     * Calculates damage one adventurer deals to the boss.
     * Applies loyalty bonus if loyalty > 70, penalty if loyalty < 30.
     * Applies madness penalty if madness > 75.
     * Adds a random modifier of 1-10.
     * @param adventurer The attacking adventurer
     * @return Damage dealt, minimum 1
     */
    public int calcAdventurerDamage(Adventurer adventurer) {
        int damage = adventurer.getAttack();

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
     */
    public int calcBossDamage(Adventurer target) {
        int damage = boss.getAttack() - target.getDefense();
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

    public int playerAttack(Adventurer adventurer, boolean success) {
        if (boss.isDead() || adventurer == null || adventurer.isDead()) {
            return 0;
        }

        int damage = success ? calcAdventurerDamage(adventurer) : 0;
        boss.setCurrentHealth(boss.getCurrentHealth() - damage);
        return damage;
    }

    public Adventurer bossTurn(Guild guild) {
        if (boss.isDead() || guild == null || guild.isWiped()) {
            mcIsBlocking = false;
            return null;
        }

        Adventurer target = findWeakestTarget(guild.getMainParty());
        if (target == null) {
            mcIsBlocking = false;
            return null;
        }

        if (mcIsBlocking && mainAdventurer != null && !mainAdventurer.isDead()) {
            target = mainAdventurer;
        }

        target.setCurrentHealth(target.getCurrentHealth() - calcBossDamage(target));
        guild.removeDeadAdventurers();
        mcIsBlocking = false;
        return target;
    }

    public boolean isFightOver(Guild guild) {
        return boss.isDead() || guild.isWiped();
    }

    public void finishFightIfOver(Guild guild) {
        if (rewardsApplied || guild == null) {
            return;
        }

        if (!isFightOver(guild)) {
            return;
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

        rewardsApplied = true;
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

