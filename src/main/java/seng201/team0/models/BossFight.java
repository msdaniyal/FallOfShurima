package seng201.team0.models;

import java.util.List;
import java.util.Random;

/**
 * Represents a single boss fight within a quest.
 *
 * Attack mechanic: memory picture game — player must recall the correct image sequence.
 * Defense mechanic: opposing D6 dice rolls — difference determines damage.
 * MC special move: once per fight, guaranteed dodge.
 *
 * Boss abilities handled here:
 *   IMMUNE_TURN  — flagged each round; BossFightController skips damage application that turn.
 *   TRUE_DAMAGE  — calcBossDamage ignores adventurer defense.
 *   ISOLATE      — tracks an isolatedTarget; only that adventurer can act for 2 rounds.
 *   DEVOUR       — instantly kills the lowest-HP adventurer.
 *   AOE          — hits every living party member each turn.
 *   HEAL_ON_HIT  — Vladimir heals by the damage he deals.
 *   SLEEP        — marks lowest-HP target; next boss hit on them deals 10x damage.
 *
 * @author Mohammed, Xinyi
 */
public class BossFight {

    // ------------------------------------- MEMBERS -------------------------------------

    private Boss boss;
    private int sequence;
    private Difficulty difficulty;
    private MemoryGame memoryGame;

    private int roundNumber;
    private boolean specialMoveUsed;
    private boolean playerWon;
    private Random random;

    // Ability state
    private Adventurer sleepingTarget;
    private Adventurer isolatedTarget;
    private int isolationRoundsRemaining;

    // ------------------------------------- CONSTRUCTORS -------------------------------------

    /**
     * Constructs a BossFight.
     * @param boss The boss for this encounter
     * @param sequence The order this fight occurs within the quest (1, 2, 3…)
     * @param difficulty The game difficulty — controls memory sequence length and damage multiplier
     */
    public BossFight(Boss boss, int sequence, Difficulty difficulty) {
        this.boss = boss;
        this.sequence = sequence;
        this.difficulty = difficulty;
        this.random = new Random();
        this.memoryGame = new MemoryGame(difficulty);
        this.roundNumber = 1;
        this.specialMoveUsed = false;
        this.sleepingTarget = null;
        this.isolatedTarget = null;
        this.isolationRoundsRemaining = 0;
    }

    // ------------------------------------- ADVENTURER ATTACK -------------------------------------

    /**
     * Calculates damage one adventurer deals to the boss after a CORRECT memory sequence.
     *
     * Base formula:
     *   damage = (adventurer.attack ± loyalty/madness modifiers) - boss.defense + random(1-10)
     *   damage *= getDamageMultiplier()
     *
     * Loyalty bonus: loyalty > 70 → +2 attack. Penalty: loyalty < 30 → -2 attack.
     * Madness penalty: madness > 75 → -2 attack.
     * Minimum 1 damage.
     *
     * @param adventurer The attacking adventurer
     * @return Damage dealt to the boss, minimum 1
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
        damage += random.nextInt(10) + 1;
        damage *= getDamageMultiplier();

        return Math.max(1, damage);
    }

    /**
     * Returns the attack damage multiplier based on difficulty.
     * Easy = x10, Normal = x7, Hard = x4.
     * @return The damage multiplier
     */
    public int getDamageMultiplier() {
        switch (difficulty) {
            case EASY:   return 10;
            case NORMAL: return 7;
            case HARD:   return 4;
            default:     return 7;
        }
    }

    // ------------------------------------- BOSS ATTACK -------------------------------------

    /**
     * Calculates damage the boss deals to a target using opposing D6 dice rolls.
     *
     * Both roll a D6.
     * If targetRoll >= bossRoll → full dodge, returns 0.
     * Otherwise:
     *   difference = bossRoll - targetRoll
     *   TRUE_DAMAGE: damage = bossAttack * difference         (ignores defense)
     *   Default:     damage = (bossAttack * difference) - target.defense
     *   SLEEP active on target: damage *= 10, then sleep is cleared.
     *   Minimum 1 damage if not a dodge.
     *
     * NOTE: For HEAL_ON_HIT (Vladimir), call applyVladimirHeal(damage) after this.
     * NOTE: For AOE (Bel'Veth), call applyAoe(guild) instead — this method is not used.
     *
     * @param target The adventurer being attacked
     * @return Damage dealt (0 if dodged)
     */
    public int calcBossDamage(Adventurer target) {
        int bossRoll = random.nextInt(6) + 1;
        int targetRoll = random.nextInt(6) + 1;

        if (targetRoll >= bossRoll) {
            return 0; // full dodge
        }

        int difference = bossRoll - targetRoll;
        int damage;

        if (boss.getAbility() == BossAbility.TRUE_DAMAGE) {
            damage = boss.getAttack() * difference;
        } else {
            damage = (boss.getAttack() * difference) - target.getDefense();
        }

        damage = Math.max(1, damage);

        if (target.equals(sleepingTarget)) {
            damage *= 10;
            sleepingTarget = null; // sleep consumed after 10x hit
        }

        return damage;
    }

    /**
     * Returns the dice difference from the last boss attack for use in applyHitPenalties.
     * Used internally — BossFightController should call calcBossDamage and track the rolls itself.
     * Kept as a helper for resolveOutcome.
     */
    private int rollDiceDifference() {
        int bossRoll = random.nextInt(6) + 1;
        int targetRoll = random.nextInt(6) + 1;
        return Math.max(0, bossRoll - targetRoll);
    }

    /**
     * Applies madness and loyalty penalties after a successful boss hit.
     * madness increase = difference * 2
     * loyalty decrease = difference
     * @param target The adventurer who was hit
     * @param difference The dice roll difference (bossRoll - targetRoll)
     */
    public void applyHitPenalties(Adventurer target, int difference) {
        target.increaseMadness(difference * 2);
        target.adjustLoyalty(-difference);
    }

    // ------------------------------------- BOSS ABILITIES -------------------------------------

    /**
     * Applies the boss's active ability for this round.
     * Called at the start of a round when boss.shouldTriggerAbility(roundNumber) is true.
     *
     * IMMUNE_TURN: Sets a flag read by BossFightController to block damage this round.
     *              No state change here — controller checks isImmuneThisRound().
     * ISOLATE:     Sets isolatedTarget to lowest-HP member for 2 rounds.
     * DEVOUR:      Instantly kills lowest-HP member (sets HP to 0).
     * AOE:         Deals flat boss attack damage to every living party member.
     * SLEEP:       Marks lowest-HP member as sleeping; next boss hit on them is 10x.
     * HEAL_ON_HIT: Passive — handled inside applyVladimirHeal after calcBossDamage.
     * TRUE_DAMAGE: Passive — handled inside calcBossDamage.
     *
     * @param guild The player's guild
     */
    public void applyBossAbility(Guild guild) {
        List<Adventurer> party = guild.getMainParty();

        switch (boss.getAbility()) {

            case ISOLATE:
                isolatedTarget = findLowestHP(party);
                isolationRoundsRemaining = 2;
                break;

            case DEVOUR:
                Adventurer devourTarget = findLowestHP(party);
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
                break;

            default:
                // NONE, TRUE_DAMAGE, HEAL_ON_HIT, IMMUNE_TURN handled elsewhere
                break;
        }
    }

    /**
     * Returns whether this round is an IMMUNE_TURN for Jax.
     * BossFightController should call this before applying adventurer damage.
     * If true, skip applying damage even if the memory sequence was correct.
     * @return True if boss is immune to damage this round
     */
    public boolean isImmuneThisRound() {
        return boss.getAbility() == BossAbility.IMMUNE_TURN
                && boss.shouldTriggerAbility(roundNumber);
    }

    /**
     * Vladimir ability — heals the boss by the amount of damage he just dealt.
     * Call this immediately after calcBossDamage returns a non-zero value.
     * @param damageDealt The damage the boss dealt this turn
     */
    public void applyVladimirHeal(int damageDealt) {
        if (boss.getAbility() == BossAbility.HEAL_ON_HIT && damageDealt > 0) {
            boss.setCurrentHealth(
                    Math.min(boss.getMaxHealth(), boss.getCurrentHealth() + damageDealt)
            );
        }
    }

    // ------------------------------------- TARGETING HELPERS -------------------------------------

    /**
     * Finds the living adventurer with the lowest current HP.
     * Used for Devour (Cho'Gath), Sleep (Zoe), and Isolate (Kha'Zix).
     * @param party The current main party
     * @return Adventurer with lowest current HP, or null if all dead
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
     * Finds the living adventurer with the lowest defense.
     * Used as the default boss attack target.
     * @param party The current main party
     * @return Adventurer with lowest defense, or null if all dead
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

    // ------------------------------------- SEQUENCE FAIL PENALTY -------------------------------------

    /**
     * Applies loyalty penalty when a player fails a memory sequence.
     * If MC fails: whole party loses 5 loyalty.
     * If party member fails: only that member loses 10 loyalty.
     * @param adventurer The adventurer who failed the sequence
     * @param isMC True if the failing adventurer is the MC
     * @param guild The player's guild
     */
    public void applySequenceFailPenalty(Adventurer adventurer, boolean isMC, Guild guild) {
        if (isMC) {
            for (Adventurer member : guild.getMainParty()) {
                member.adjustLoyalty(-5);
            }
        } else {
            adventurer.adjustLoyalty(-10);
        }
    }

    // ------------------------------------- SPECIAL MOVE -------------------------------------

    /**
     * Activates the MC's special move — guaranteed dodge for this round.
     * Can only be used once per fight.
     * @return True if activated, false if already used
     */
    public boolean activateSpecialMove() {
        if (!specialMoveUsed) {
            specialMoveUsed = true;
            return true;
        }
        return false;
    }

    // ------------------------------------- ROUND PROGRESSION -------------------------------------

    /**
     * Advances the fight to the next round.
     * Decrements isolation counter if active and clears it when expired.
     */
    public void nextRound() {
        roundNumber++;
        if (isolationRoundsRemaining > 0) {
            isolationRoundsRemaining--;
            if (isolationRoundsRemaining == 0) {
                isolatedTarget = null;
            }
        }
    }

    // ------------------------------------- FULL COMBAT LOOP -------------------------------------

    /**
     * Runs the full combat loop to completion.
     *
     * Each round:
     * 1. Check if boss ability triggers (boss.shouldTriggerAbility(roundNumber)).
     *    - Call applyBossAbility(guild) if so.
     * 2. Each living adventurer plays the memory game to attack the boss.
     *    - If ISOLATE is active: only isolatedTarget can act.
     *    - If SLEEP is active on this adventurer: they skip their turn.
     *    - If IMMUNE_TURN: correct sequence still plays but deals 0 damage.
     *    - Correct sequence: calcAdventurerDamage() applied to boss HP.
     *    - Wrong sequence: applySequenceFailPenalty() called.
     * 3. Boss attacks:
     *    - AOE: handled inside applyBossAbility, no additional attack.
     *    - Otherwise: boss attacks findWeakestTarget().
     *      - MC special move active: guaranteed dodge.
     *      - Otherwise: calcBossDamage(); applyHitPenalties() if damage > 0.
     *      - HEAL_ON_HIT: applyVladimirHeal(damage) if damage > 0.
     * 4. guild.removeDeadAdventurers().
     * 5. nextRound().
     *
     * Fight ends when boss HP ≤ 0 (player wins) or guild.isWiped() (player loses).
     *
     * @param guild The player's guild
     * @param mcAdventurer The MC adventurer
     *
     * TODO: Coordinate with BossFightController for memory game UI per adventurer turn.
     * TODO: Coordinate with BossFightController for special move button (calls activateSpecialMove()).
     * TODO: BossFightController should call isImmuneThisRound() before applying attack damage.
     */
    public void resolveOutcome(Guild guild, Adventurer mcAdventurer) {
        while (!boss.isDead() && !guild.isWiped()) {

            // 1. Boss ability trigger
            if (boss.shouldTriggerAbility(roundNumber)) {
                applyBossAbility(guild);
            }

            if (guild.isWiped()) break;

            // 2. Party attacks
            List<Adventurer> party = guild.getMainParty();
            for (Adventurer adventurer : party) {
                if (adventurer.isDead()) continue;

                // Isolation: only isolatedTarget acts while isolation is active
                if (isolatedTarget != null && !adventurer.equals(isolatedTarget)) {
                    continue;
                }

                // Sleep: sleeping adventurer skips their attack
                if (adventurer.equals(sleepingTarget)) {
                    continue;
                }

                // TODO: Show memory game UI via BossFightController, get playerInput
                List<Integer> correctSequence = memoryGame.generateSequence();
                boolean correct = false; // TODO: Replace with memoryGame.checkSequence(playerInput)

                if (correct && !isImmuneThisRound()) {
                    int damage = calcAdventurerDamage(adventurer);
                    boss.setCurrentHealth(boss.getCurrentHealth() - damage);
                } else if (!correct) {
                    boolean isMC = adventurer.equals(mcAdventurer);
                    applySequenceFailPenalty(adventurer, isMC, guild);
                }
                // correct + immune turn: no damage, no penalty

                if (boss.isDead()) break;
            }

            if (boss.isDead()) break;

            // 3. Boss attacks (AOE already handled in applyBossAbility)
            if (boss.getAbility() != BossAbility.AOE || !boss.shouldTriggerAbility(roundNumber)) {
                Adventurer target = findWeakestTarget(guild.getMainParty());
                if (target != null) {
                    boolean guaranteed_dodge = target.equals(mcAdventurer) && specialMoveUsed;

                    if (!guaranteed_dodge) {
                        int bossRoll = random.nextInt(6) + 1;
                        int targetRoll = random.nextInt(6) + 1;

                        if (bossRoll > targetRoll) {
                            int difference = bossRoll - targetRoll;
                            int damage;
                            if (boss.getAbility() == BossAbility.TRUE_DAMAGE) {
                                damage = boss.getAttack() * difference;
                            } else {
                                damage = (boss.getAttack() * difference) - target.getDefense();
                            }
                            damage = Math.max(1, damage);

                            if (target.equals(sleepingTarget)) {
                                damage *= 10;
                                sleepingTarget = null;
                            }

                            target.setCurrentHealth(target.getCurrentHealth() - damage);
                            applyHitPenalties(target, difference);
                            applyVladimirHeal(damage);
                        }
                    }
                }
            }

            guild.removeDeadAdventurers();
            nextRound();
        }

        // Resolve win/loss
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

    // ------------------------------------- GETTERS -------------------------------------

    /**
     * @return The memory game instance for this fight
     */
    public MemoryGame getMemoryGame() {
        return memoryGame;
    }

    /**
     * @return The boss for this fight
     */
    public Boss getBoss() {
        return boss;
    }

    /**
     * @return The sequence number of this fight within the quest (1-indexed)
     */
    public int getSequence() {
        return sequence;
    }

    /**
     * @return The current round number
     */
    public int getRoundNumber() {
        return roundNumber;
    }

    /**
     * @return True if the player won this boss fight
     */
    public boolean isPlayerWon() {
        return playerWon;
    }

    /**
     * @return True if the MC special move has already been used this fight
     */
    public boolean isSpecialMoveUsed() {
        return specialMoveUsed;
    }

    /**
     * @return The currently sleeping adventurer, or null if none
     */
    public Adventurer getSleepingTarget() {
        return sleepingTarget;
    }

    /**
     * @return The currently isolated adventurer, or null if none
     */
    public Adventurer getIsolatedTarget() {
        return isolatedTarget;
    }

    /**
     * @return How many rounds of isolation remain
     */
    public int getIsolationRoundsRemaining() {
        return isolationRoundsRemaining;
    }
}