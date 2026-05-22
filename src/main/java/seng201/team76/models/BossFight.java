package seng201.team76.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Represents a single boss fight within a quest.
 *
 * This class keeps combat logic out of the JavaFX controllers.
 * Controllers should call these public methods and only display the results:
 * - resolvePlayerAttack(...)
 * - resolveDefend(...)
 * - resolveBossAttack(...)
 * - triggerBossAbilityIfNeeded(...)
 * - activateSpecialMove()
 *
 * @author Mohammed, Xinyi
 */
public class BossFight {

    private Boss boss;
    private int sequence;
    private Difficulty difficulty;
    private MemoryGame memoryGame;

    private int roundNumber;
    private boolean specialMoveUsed;
    private boolean specialDodgePending;
    private boolean playerWon;
    private boolean rewardsApplied;
    private Random random;

    private Adventurer sleepingTarget;
    private int sleepExecutionRound;
    private Adventurer isolatedTarget;
    private int isolationRoundsRemaining;
    private boolean abilityTriggeredThisRound;
    private boolean potionUsedThisTurn;

    /**
     * Result returned when the player attacks through the memory game.
     */
    public static class AttackResult {
        private final boolean success;
        private final boolean blocked;
        private final int damage;
        private final String message;

        /**
         * Creates an attack result.
         *
         * @param success true if the attack sequence succeeded
         * @param blocked true if the boss blocked the damage
         * @param damage Damage dealt to the boss
         * @param message Message shown to the player
         */
        public AttackResult(boolean success, boolean blocked, int damage, String message) {
            this.success = success;
            this.blocked = blocked;
            this.damage = damage;
            this.message = message;
        }

        /**
         * Checks whether the attack succeeded.
         *
         * @return true if the attack was successful
         */
        public boolean isSuccess() {
            return success;
        }

        /**
         * Checks whether the attack was blocked.
         *
         * @return true if the boss blocked the attack
         */
        public boolean isBlocked() {
            return blocked;
        }

        /**
         * Gets the damage dealt.
         *
         * @return The damage amount
         */
        public int getDamage() {
            return damage;
        }

        /**
         * Gets the result message.
         *
         * @return The message for the controller
         */
        public String getMessage() {
            return message;
        }
    }

    /**
     * Result returned after a boss attack or defend dice roll.
     */
    public static class DefenseResult {
        private final int playerRoll;
        private final int bossRoll;
        private final int damage;
        private final Adventurer target;
        private final boolean dodged;
        private final boolean specialDodged;
        private final String message;

        /**
         * Creates a defense result.
         *
         * @param playerRoll The player's dice roll
         * @param bossRoll The boss dice roll
         * @param damage Damage dealt to the target
         * @param target The adventurer targeted by the boss
         * @param dodged true if the attack was dodged or blocked
         * @param specialDodged true if a special move caused the dodge
         * @param message Message shown to the player
         */
        public DefenseResult(int playerRoll, int bossRoll, int damage, Adventurer target,
                             boolean dodged, boolean specialDodged, String message) {
            this.playerRoll = playerRoll;
            this.bossRoll = bossRoll;
            this.damage = damage;
            this.target = target;
            this.dodged = dodged;
            this.specialDodged = specialDodged;
            this.message = message;
        }

        /**
         * Gets the player's dice roll.
         *
         * @return The player roll
         */
        public int getPlayerRoll() {
            return playerRoll;
        }

        /**
         * Gets the boss dice roll.
         *
         * @return The boss roll
         */
        public int getBossRoll() {
            return bossRoll;
        }

        /**
         * Gets the damage dealt by the boss.
         *
         * @return The damage amount
         */
        public int getDamage() {
            return damage;
        }

        /**
         * Gets the adventurer targeted by the boss.
         *
         * @return The target adventurer
         */
        public Adventurer getTarget() {
            return target;
        }

        /**
         * Checks whether the attack was dodged or blocked.
         *
         * @return true if no normal damage was taken
         */
        public boolean isDodged() {
            return dodged;
        }

        /**
         * Checks whether the special move caused the dodge.
         *
         * @return true if the special move dodged the attack
         */
        public boolean isSpecialDodged() {
            return specialDodged;
        }

        /**
         * Gets the defense result message.
         *
         * @return The message for the controller
         */
        public String getMessage() {
            return message;
        }
    }

    /**
     * Result returned when the player uses a potion during a boss fight.
     */
    public static class PotionUseResult {
        private final boolean success;
        private final String message;
        private final Map<Adventurer, Integer> healedAmounts;

        /**
         * Creates a potion use result.
         *
         * @param success true if a potion was used
         * @param message Message shown to the player
         * @param healedAmounts Amount healed for each adventurer
         */
        public PotionUseResult(boolean success, String message, Map<Adventurer, Integer> healedAmounts) {
            this.success = success;
            this.message = message;
            this.healedAmounts = healedAmounts;
        }

        /**
         * Checks whether the potion was used.
         *
         * @return true if the potion use succeeded
         */
        public boolean isSuccess() {
            return success;
        }

        /**
         * Gets the potion result message.
         *
         * @return The message for the controller
         */
        public String getMessage() {
            return message;
        }

        /**
         * Gets how much each adventurer was healed.
         *
         * @return Map of adventurers to healed amounts
         */
        public Map<Adventurer, Integer> getHealedAmounts() {
            return healedAmounts;
        }
    }

    /**
     * Creates a boss fight.
     *
     * @param boss The boss for this fight
     * @param sequence The fight number inside the quest
     * @param difficulty The selected difficulty
     */
    public BossFight(Boss boss, int sequence, Difficulty difficulty) {
        this.boss = boss;
        this.sequence = sequence;
        this.difficulty = difficulty;
        this.random = new Random();
        this.memoryGame = new MemoryGame(difficulty);
        this.roundNumber = 1;
        this.specialMoveUsed = false;
        this.specialDodgePending = false;
        this.playerWon = false;
        this.rewardsApplied = false;
        this.sleepingTarget = null;
        this.sleepExecutionRound = -1;
        this.isolatedTarget = null;
        this.isolationRoundsRemaining = 0;
        this.abilityTriggeredThisRound = false;
        this.potionUsedThisTurn = false;
    }

    // -------------------------------------------------------------------------
    // Adventurer attack
    // -------------------------------------------------------------------------

    /**
     * Calculates an adventurer's attack damage against the boss.
     *
     * @param adventurer The attacking adventurer
     * @return The damage after stats, loyalty, madness, and difficulty
     */
    public int calcAdventurerDamage(Adventurer adventurer) {
        if (adventurer == null) {
            return 0;
        }

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
        damage += random.nextInt(6) + 1;
        damage *= getDamageMultiplier();
        damage = Math.max(1, damage);

        return applySingleHitDamageCap(damage);
    }

    /**
     * Difficulty should tune pace, not create one-shot bosses.
     * Earlier values (10/7/4) made a normal hit deal over 100 damage.
     *
     * @return The damage multiplier for the current difficulty
     */
    public int getDamageMultiplier() {
        switch (difficulty) {
            case EASY:
                return 3;
            case NORMAL:
                return 2;
            case HARD:
                return 1;
            default:
                return 2;
        }
    }

    /**
     * Prevents a full-health boss being deleted by one successful memory game.
     * The cap is ignored once the boss is already low enough to finish.
     */
    private int applySingleHitDamageCap(int damage) {
        int cap = Math.max(12, boss.getMaxHealth() / 3);

        if (boss.getCurrentHealth() > cap) {
            return Math.min(damage, cap);
        }

        return damage;
    }

    /**
     * Called by BossFightController after the memory game closes.
     *
     * @param attacker The attacking adventurer
     * @param sequenceCorrect Whether the memory game was successful
     * @param isMC True if the attacker is the main character
     * @param guild The player's guild
     * @return AttackResult for the controller to display
     */
    public AttackResult resolvePlayerAttack(Adventurer attacker, boolean sequenceCorrect,
                                            boolean isMC, Guild guild) {
        triggerBossAbilityIfNeeded(guild);

        if (attacker == null || attacker.isDead()) {
            return new AttackResult(false, false, 0, "No fighter is ready to attack.");
        }

        if (!sequenceCorrect) {
            applySequenceFailPenalty(attacker, isMC, guild);
            return new AttackResult(false, false, 0,
                    attacker.getName() + "'s attack failed.");
        }

        if (isImmuneThisRound()) {
            return new AttackResult(true, true, 0,
                    boss.getName() + " counters the strike and takes no damage.");
        }

        int damage = calcAdventurerDamage(attacker);
        boss.setCurrentHealth(boss.getCurrentHealth() - damage);

        return new AttackResult(true, false, damage,
                attacker.getName() + " hits " + boss.getName() + " for " + damage + " damage.");
    }

    /**
     * Backwards-compatible method used by older code.
     *
     * @param attacker The attacking adventurer
     * @param sequenceCorrect true if the memory sequence was correct
     * @param isMC true if the attacker is the main character
     * @param guild The player's guild
     * @return Damage dealt to the boss
     */
    public int playerAttack(Adventurer attacker, boolean sequenceCorrect, boolean isMC, Guild guild) {
        return resolvePlayerAttack(attacker, sequenceCorrect, isMC, guild).getDamage();
    }

    /**
     * Backwards-compatible method used by older code.
     *
     * @param attacker The attacking adventurer
     * @param sequenceCorrect true if the memory sequence was correct
     * @return Damage dealt to the boss
     */
    public int playerAttack(Adventurer attacker, boolean sequenceCorrect) {
        return resolvePlayerAttack(attacker, sequenceCorrect, false, null).getDamage();
    }

    /**
     * Applies loyalty penalties after a failed memory sequence.
     *
     * @param adventurer The adventurer who failed
     * @param isMC true if the adventurer is the main character
     * @param guild The player's guild
     */
    public void applySequenceFailPenalty(Adventurer adventurer, boolean isMC, Guild guild) {
        if (adventurer == null) {
            return;
        }

        if (isMC && guild != null) {
            for (Adventurer member : guild.getMainParty()) {
                member.adjustLoyalty(-5);
            }
        } else {
            adventurer.adjustLoyalty(-10);
        }
    }

    // -------------------------------------------------------------------------
    // Boss attack / defend
    // -------------------------------------------------------------------------

    /**
     * Used when the player chooses DEFEND.
     * The dice controller supplies playerRoll and bossRoll.
     *
     * @param defender The active adventurer defending
     * @param playerRoll Player dice roll
     * @param bossRoll Boss dice roll
     * @param guild Player guild
     * @return DefenseResult for display
     */
    public DefenseResult resolveDefend(Adventurer defender, int playerRoll, int bossRoll, Guild guild) {
        triggerBossAbilityIfNeeded(guild);

        if (defender == null || defender.isDead()) {
            defender = guild == null ? null : findWeakestTarget(guild.getMainParty());
        }

        if (defender == null) {
            return new DefenseResult(playerRoll, bossRoll, 0, null, true, false,
                    "There is no one left to defend.");
        }

        if (specialDodgePending) {
            specialDodgePending = false;
            nextRound();
            return new DefenseResult(playerRoll, bossRoll, 0, defender, true, true,
                    defender.getName() + " evades the attack using the special move.");
        }

        if (playerRoll >= bossRoll) {
            nextRound();
            return new DefenseResult(playerRoll, bossRoll, 0, defender, true, false,
                    defender.getName() + " blocks the attack.");
        }

        int difference = bossRoll - playerRoll;
        int damage = calculateBossDamageFromDifference(defender, difference);

        defender.setCurrentHealth(defender.getCurrentHealth() - damage);
        applyHitPenalties(defender, difference);
        applyVladimirHeal(damage);

        if (guild != null) {
            guild.removeDeadAdventurers();
        }
        nextRound();

        String hitMessage = boss.getName() + " breaks through and deals " + damage + " damage to " + defender.getName() + ".";
        if (isKhazixBoss() && defender.equals(isolatedTarget)) {
            hitMessage += " Isolation makes the strike hit harder.";
        }

        return new DefenseResult(playerRoll, bossRoll, damage, defender, false, false, hitMessage);
    }

    /**
     * Used after the player attacks. The boss rolls automatically.
     *
     * @param guild Player guild
     * @return DefenseResult for display
     */
    public DefenseResult resolveBossAttack(Guild guild) {
        int playerRoll = random.nextInt(6) + 1;
        int bossRoll = random.nextInt(6) + 1;

        Adventurer target = getBossAttackTarget(guild.getMainParty());

        if (target == null) {
            return new DefenseResult(playerRoll, bossRoll, 0, null, true, false,
                    "There is no target left for " + boss.getName() + ".");
        }

        return resolveDefend(target, playerRoll, bossRoll, guild);
    }

    /**
     * Old method kept so older MemoryGameController versions do not break.
     *
     * @param guild The player's guild
     */
    public void bossTurn(Guild guild) {
        resolveBossAttack(guild);
    }

    private int calculateBossDamageFromDifference(Adventurer target, int difference) {
        int damage;

        if (boss.getAbility() == BossAbility.TRUE_DAMAGE || isBelvethBoss()) {
            damage = boss.getAttack() * difference;
        } else {
            damage = (boss.getAttack() * difference) - target.getDefense();
        }

        damage = Math.max(1, damage);

        if (isKhazixBoss() && isIsolationActive() && target.equals(isolatedTarget)) {
            damage = Math.max(damage + 5, (int) Math.ceil(damage * 1.5));
        }

        if (target.equals(sleepingTarget) && !isZoeBoss()) {
            damage *= 10;
            sleepingTarget = null;
            sleepExecutionRound = -1;
        }

        return damage;
    }

    /**
     * Kept for compatibility with existing tests/controllers.
     *
     * @param target The adventurer being attacked
     * @return The boss damage, or 0 if the target wins the roll
     */
    public int calcBossDamage(Adventurer target) {
        int bossRoll = random.nextInt(6) + 1;
        int targetRoll = random.nextInt(6) + 1;

        if (bossRoll <= targetRoll) {
            return 0;
        }

        return calculateBossDamageFromDifference(target, bossRoll - targetRoll);
    }

    /**
     * Applies madness and loyalty penalties after a boss hit.
     *
     * @param target The adventurer who was hit
     * @param difference Difference between boss and player rolls
     */
    public void applyHitPenalties(Adventurer target, int difference) {
        if (target == null || difference <= 0) {
            return;
        }

        target.increaseMadness(difference * 2);
        target.adjustLoyalty(-difference);
    }

    /**
     * Heals Vladimir when his heal-on-hit ability is active.
     *
     * @param damageDealt The damage dealt by the boss
     */
    public void applyVladimirHeal(int damageDealt) {
        if (boss.getAbility() == BossAbility.HEAL_ON_HIT && damageDealt > 0) {
            boss.setCurrentHealth(Math.min(boss.getMaxHealth(), boss.getCurrentHealth() + damageDealt));
        }
    }

    // -------------------------------------------------------------------------
    // Boss ability
    // -------------------------------------------------------------------------

    /**
     * Checks whether the boss ability should trigger this round.
     *
     * @return true if the ability should trigger
     */
    public boolean shouldTriggerAbilityThisRound() {
        return boss.shouldTriggerAbility(roundNumber);
    }

    private boolean isKhazixBoss() {
        return boss != null && boss.getName() != null && boss.getName().equalsIgnoreCase("Kha'Zix");
    }

    private boolean isBelvethBoss() {
        if (boss == null || boss.getName() == null) {
            return false;
        }
        String cleanName = boss.getName().toLowerCase().replaceAll("[^a-z]", "");
        return cleanName.equals("belveth");
    }

    private boolean isZoeBoss() {
        return boss != null && boss.getName() != null && boss.getName().equalsIgnoreCase("Zoe");
    }

    /**
     * Checks whether an adventurer is currently isolated.
     *
     * @return true if isolation is active
     */
    public boolean isIsolationActive() {
        return isolatedTarget != null && !isolatedTarget.isDead() && !boss.isDead();
    }

    /**
     * Checks whether the player can change the active party member.
     *
     * @return true if changing member is allowed
     */
    public boolean canChangeMember() {
        return !isIsolationActive();
    }

    private Adventurer getBossAttackTarget(List<Adventurer> party) {
        if (isIsolationActive()) {
            return isolatedTarget;
        }
        return findWeakestTarget(party);
    }

    private double getDevourThreshold() {
        switch (difficulty) {
            case EASY:
                return 0.30;
            case NORMAL:
                return 0.50;
            case HARD:
                return 0.60;
            default:
                return 0.50;
        }
    }

    private Adventurer findDevourableTarget(List<Adventurer> party) {
        Adventurer lowest = null;
        double threshold = getDevourThreshold();

        for (Adventurer member : party) {
            if (member == null || member.isDead() || member.getMaxHealth() <= 0) {
                continue;
            }

            double hpPercent = (double) member.getCurrentHealth() / member.getMaxHealth();
            if (hpPercent <= threshold) {
                if (lowest == null || member.getCurrentHealth() < lowest.getCurrentHealth()) {
                    lowest = member;
                }
            }
        }

        return lowest;
    }

    /**
     * Triggers the boss ability if this round allows it.
     *
     * @param guild The player's guild
     * @return Message to show, or null if nothing happened
     */
    public String triggerBossAbilityIfNeeded(Guild guild) {
        if (guild == null) {
            return null;
        }

        if (isZoeBoss() && sleepingTarget != null && !sleepingTarget.isDead()
                && sleepExecutionRound > 0 && roundNumber >= sleepExecutionRound) {
            Adventurer doomed = sleepingTarget;
            doomed.setCurrentHealth(0);
            sleepingTarget = null;
            sleepExecutionRound = -1;
            abilityTriggeredThisRound = true;
            guild.removeDeadAdventurers();
            return boss.getName() + " snaps the dream shut. " + doomed.getName()
                    + " falls from full strength to nothing.";
        }

        if (abilityTriggeredThisRound || !shouldTriggerAbilityThisRound()) {
            return null;
        }

        abilityTriggeredThisRound = true;

        if (boss.getAbility() == BossAbility.ISOLATE && isIsolationActive()) {
            return null;
        }

        if (boss.getAbility() == BossAbility.DEVOUR) {
            Adventurer target = findDevourableTarget(guild.getMainParty());
            if (target == null) {
                return boss.getName() + " searches for a weakened target, but no one is weak enough to devour.";
            }
        }

        if (boss.getAbility() == BossAbility.NONE || boss.getAbility() == BossAbility.TRUE_DAMAGE
                || boss.getAbility() == BossAbility.HEAL_ON_HIT) {
            return null;
        }

        if (boss.getAbility() == BossAbility.IMMUNE_TURN) {
            return boss.getName() + " raises his guard. This round, attacks will be countered.";
        }

        applyBossAbility(guild);
        guild.removeDeadAdventurers();

        switch (boss.getAbility()) {
            case ISOLATE:
                return boss.getName() + " isolates " +
                        (isolatedTarget == null ? "a target" : isolatedTarget.getName()) + ".";
            case DEVOUR:
                return boss.getName() + " devours a weakened fighter.";
            case AOE:
                return boss.getName() + " unleashes an attack across the whole party.";
            case SLEEP:
                return boss.getName() + " puts " +
                        (sleepingTarget == null ? "a target" : sleepingTarget.getName())
                        + " to sleep. Next round, the dream will kill them if Zoe is not stopped.";
            default:
                return null;
        }
    }

    /**
     * Applies the boss ability effect to the guild.
     *
     * @param guild The player's guild
     */
    public void applyBossAbility(Guild guild) {
        List<Adventurer> party = guild.getMainParty();

        switch (boss.getAbility()) {
            case ISOLATE:
                if (!isIsolationActive()) {
                    isolatedTarget = findLowestHP(party);
                    isolationRoundsRemaining = isKhazixBoss() ? Integer.MAX_VALUE : 2;
                }
                break;

            case DEVOUR:
                Adventurer devourTarget = findDevourableTarget(party);
                if (devourTarget != null) {
                    devourTarget.setCurrentHealth(0);
                }
                break;

            case AOE:
                for (Adventurer member : party) {
                    if (!member.isDead()) {
                        int damage = Math.max(1, boss.getAttack());
                        member.setCurrentHealth(member.getCurrentHealth() - damage);
                    }
                }
                break;

            case SLEEP:
                sleepingTarget = findLowestHP(party);
                sleepExecutionRound = sleepingTarget == null ? -1 : roundNumber + 1;
                break;

            default:
                break;
        }
    }

    /**
     * Checks whether the boss is immune this round.
     *
     * @return true if attacks should be blocked
     */
    public boolean isImmuneThisRound() {
        return boss.getAbility() == BossAbility.IMMUNE_TURN && boss.shouldTriggerAbility(roundNumber);
    }

    // -------------------------------------------------------------------------
    // Potions / healing
    // -------------------------------------------------------------------------

    /**
     * Checks whether a potion has already been used this turn.
     *
     * @return true if a potion was used this turn
     */
    public boolean isPotionUsedThisTurn() {
        return potionUsedThisTurn;
    }

    /**
     * Checks whether the player can use a potion this turn.
     *
     * @param guild The player's guild
     * @return true if a potion can be used
     */
    public boolean canUsePotionThisTurn(Guild guild) {
        return !potionUsedThisTurn
                && guild != null
                && guild.hasAnyHealingPotions()
                && guild.hasInjuredMainPartyMember()
                && !boss.isDead()
                && !guild.isWiped();
    }

    /**
     * Uses a potion during the active player's turn. This keeps potion inventory,
     * healing amounts, and once-per-turn rules in the model instead of the controller.
     *
     * @param guild The player's guild
     * @param itemType The potion type to use
     * @param target The target for single-target potions
     * @return Result of the potion use
     */
    public PotionUseResult usePotionThisTurn(Guild guild, ItemType itemType, Adventurer target) {
        Map<Adventurer, Integer> healedAmounts = new LinkedHashMap<>();

        if (potionUsedThisTurn) {
            return new PotionUseResult(false, "You already used a potion this turn.", healedAmounts);
        }

        if (guild == null || itemType == null) {
            return new PotionUseResult(false, "No potion can be used right now.", healedAmounts);
        }

        if (!guild.hasInjuredMainPartyMember()) {
            return new PotionUseResult(false, "Everyone is already at full health.", healedAmounts);
        }

        switch (itemType) {
            case SINGLE:
                return useSinglePotion(guild, target, healedAmounts);
            case PARTY:
                return usePartyPotion(guild, healedAmounts);
            case FULL:
                return useFullRestore(guild, healedAmounts);
            default:
                return new PotionUseResult(false, "Unknown potion type.", healedAmounts);
        }
    }

    private PotionUseResult useSinglePotion(Guild guild, Adventurer target, Map<Adventurer, Integer> healedAmounts) {
        if (guild.getSmallPotionCount() <= 0) {
            return new PotionUseResult(false, "No Silver Potions left.", healedAmounts);
        }

        if (target == null || target.isDead()) {
            target = findLowestInjuredMember(guild.getMainParty());
        }

        if (target == null) {
            return new PotionUseResult(false, "No injured fighter needs a Silver Potion.", healedAmounts);
        }

        int before = target.getCurrentHealth();
        target.setCurrentHealth(before + 30);
        int healed = target.getCurrentHealth() - before;

        if (healed <= 0) {
            return new PotionUseResult(false, target.getName() + " is already at full health.", healedAmounts);
        }

        guild.useSmallPotion();
        potionUsedThisTurn = true;
        healedAmounts.put(target, healed);

        return new PotionUseResult(true, "Silver Potion healed " + target.getName() + " for " + healed + " HP.", healedAmounts);
    }

    private PotionUseResult usePartyPotion(Guild guild, Map<Adventurer, Integer> healedAmounts) {
        if (guild.getPartyPotionCount() <= 0) {
            return new PotionUseResult(false, "No Gold Potions left.", healedAmounts);
        }

        for (Adventurer adventurer : guild.getMainParty()) {
            if (!adventurer.isDead() && adventurer.getCurrentHealth() < adventurer.getMaxHealth()) {
                int before = adventurer.getCurrentHealth();
                adventurer.setCurrentHealth(before + 20);
                int healed = adventurer.getCurrentHealth() - before;
                if (healed > 0) {
                    healedAmounts.put(adventurer, healed);
                }
            }
        }

        if (healedAmounts.isEmpty()) {
            return new PotionUseResult(false, "No injured fighters need a Gold Potion.", healedAmounts);
        }

        guild.usePartyPotion();
        potionUsedThisTurn = true;

        return new PotionUseResult(true, "Gold Potion healed the party.", healedAmounts);
    }

    private PotionUseResult useFullRestore(Guild guild, Map<Adventurer, Integer> healedAmounts) {
        if (guild.getFullRestoreCount() <= 0) {
            return new PotionUseResult(false, "No Purple Potions left.", healedAmounts);
        }

        for (Adventurer adventurer : guild.getMainParty()) {
            if (!adventurer.isDead() && adventurer.getCurrentHealth() < adventurer.getMaxHealth()) {
                int before = adventurer.getCurrentHealth();
                adventurer.resetHealth();
                int healed = adventurer.getCurrentHealth() - before;
                if (healed > 0) {
                    healedAmounts.put(adventurer, healed);
                }
            }
        }

        if (healedAmounts.isEmpty()) {
            return new PotionUseResult(false, "No injured fighters need a Purple Potion.", healedAmounts);
        }

        guild.useFullRestore();
        potionUsedThisTurn = true;

        return new PotionUseResult(true, "Purple Potion fully restored the party.", healedAmounts);
    }

    private Adventurer findLowestInjuredMember(List<Adventurer> party) {
        Adventurer target = null;

        for (Adventurer member : party) {
            if (!member.isDead() && member.getCurrentHealth() < member.getMaxHealth()) {
                if (target == null || member.getCurrentHealth() < target.getCurrentHealth()) {
                    target = member;
                }
            }
        }

        return target;
    }

    // -------------------------------------------------------------------------
    // Special move
    // -------------------------------------------------------------------------

    /**
     * Activates the MC's special move.
     * The next boss attack is guaranteed to miss.
     *
     * @return true if successfully activated
     */
    public boolean activateSpecialMove() {
        if (specialMoveUsed) {
            return false;
        }

        specialMoveUsed = true;
        specialDodgePending = true;
        return true;
    }

    // -------------------------------------------------------------------------
    // Targeting and progression
    // -------------------------------------------------------------------------

    /**
     * Finds the living party member with the lowest current HP.
     *
     * @param party The party to search
     * @return The lowest-HP living adventurer, or null if none exist
     */
    public Adventurer findLowestHP(List<Adventurer> party) {
        Adventurer target = null;

        for (Adventurer member : party) {
            if (!member.isDead()) {
                if (target == null || member.getCurrentHealth() < target.getCurrentHealth()) {
                    target = member;
                }
            }
        }

        return target;
    }

    /**
     * Finds the living party member with the lowest defense.
     *
     * @param party The party to search
     * @return The weakest living adventurer, or null if none exist
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
     * Checks whether an adventurer can act this turn.
     *
     * @param adventurer The adventurer to check
     * @return true if the adventurer can act
     */
    public boolean canAdventurerAct(Adventurer adventurer) {
        if (adventurer == null || adventurer.isDead()) {
            return false;
        }

        if (isIsolationActive() && !adventurer.equals(isolatedTarget)) {
            return false;
        }

        return !adventurer.equals(sleepingTarget);
    }

    /**
     * Advances the fight to the next round and clears turn-based flags.
     */
    public void nextRound() {
        roundNumber++;

        if (isolatedTarget != null && (isolatedTarget.isDead() || boss.isDead())) {
            isolatedTarget = null;
            isolationRoundsRemaining = 0;
        } else if (isolationRoundsRemaining > 0 && !isKhazixBoss()) {
            isolationRoundsRemaining--;

            if (isolationRoundsRemaining == 0) {
                isolatedTarget = null;
            }
        }

        if (sleepingTarget != null && sleepingTarget.isDead()) {
            sleepingTarget = null;
            sleepExecutionRound = -1;
        }

        abilityTriggeredThisRound = false;
        potionUsedThisTurn = false;
    }

    /**
     * Checks whether this fight is over.
     *
     * @param guild The player's guild
     * @return true if the boss is dead or the party is wiped
     */
    public boolean isFightOver(Guild guild) {
        return boss.isDead() || guild.isWiped();
    }

    /**
     * Applies fight rewards or penalties if the fight is over.
     *
     * @param guild The player's guild
     */
    public void finishFightIfOver(Guild guild) {
        if (!isFightOver(guild) || rewardsApplied) {
            return;
        }

        rewardsApplied = true;
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

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    /**
     * Gets the memory game used for player attacks.
     *
     * @return The memory game
     */
    public MemoryGame getMemoryGame() {
        return memoryGame;
    }

    /**
     * Gets the boss for this fight.
     *
     * @return The boss
     */
    public Boss getBoss() {
        return boss;
    }

    /**
     * Gets this fight's sequence number in the quest.
     *
     * @return The fight sequence number
     */
    public int getSequence() {
        return sequence;
    }

    /**
     * Gets the current round number.
     *
     * @return The round number
     */
    public int getRoundNumber() {
        return roundNumber;
    }

    /**
     * Checks whether the player won this fight.
     *
     * @return true if the player won
     */
    public boolean isPlayerWon() {
        return playerWon;
    }

    /**
     * Checks whether the special move has been used.
     *
     * @return true if the special move was used
     */
    public boolean isSpecialMoveUsed() {
        return specialMoveUsed;
    }

    /**
     * Gets the adventurer currently affected by sleep.
     *
     * @return The sleeping target, or null
     */
    public Adventurer getSleepingTarget() {
        return sleepingTarget;
    }

    /**
     * Gets the round when sleep executes.
     *
     * @return The sleep execution round, or -1 if none
     */
    public int getSleepExecutionRound() {
        return sleepExecutionRound;
    }

    /**
     * Gets the isolated adventurer.
     *
     * @return The isolated target, or null
     */
    public Adventurer getIsolatedTarget() {
        return isolatedTarget;
    }

    /**
     * Gets how many isolation rounds remain.
     *
     * @return The remaining isolation rounds
     */
    public int getIsolationRoundsRemaining() {
        return isolationRoundsRemaining;
    }
}
