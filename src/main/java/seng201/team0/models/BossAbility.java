package seng201.team0.models;

/**
 * Enum representing the special ability a boss can use in combat.
 * Each value corresponds to a unique mechanic handled in BossFight.
 * @author Mohammed, Xinyi
 */
public enum BossAbility {

    /** No special ability. Standard combat only. */
    NONE,

    /**
     * Jax: On ability turns, all adventurer attacks deal 0 damage.
     * Handled by BossFightController — flag the round as immune,
     * skip applying damage even if the memory sequence is correct.
     */
    IMMUNE_TURN,

    /**
     * Vel'Koz: All boss damage ignores adventurer defense entirely.
     * damage = bossAttack * diceDifference (no defense subtraction).
     */
    TRUE_DAMAGE,

    /**
     * Kha'Zix: Isolates the party member with the lowest current HP.
     * That adventurer fights alone for 2 rounds — others cannot attack.
     * Isolation ends after 2 rounds or when the isolated member dies.
     */
    ISOLATE,

    /**
     * Cho'Gath: Instantly kills the party member with the lowest current HP.
     * Sets their health to 0 directly.
     */
    DEVOUR,

    /**
     * Bel'Veth: Deals boss attack damage to every living party member each turn.
     * Does not use dice rolls — flat damage to all.
     */
    AOE,

    /**
     * Vladimir: After dealing damage to an adventurer,
     * heals himself by the same amount dealt, capped at max HP.
     */
    HEAL_ON_HIT,

    /**
     * Zoe: Puts the lowest-HP adventurer to sleep.
     * The sleeping adventurer skips their attack turn.
     * The next boss hit against the sleeping target deals 10x damage.
     * Sleep is cleared after the 10x hit lands.
     */
    SLEEP
}