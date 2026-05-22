package seng201.team76.unittests.models;

import org.junit.jupiter.api.Test;
import seng201.team76.models.Boss;
import seng201.team76.models.BossAbility;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Boss model, including the special-ability timing rules.
 */
public class BossTest {

    @Test
    void bossConstructorStoresCombatRewardsAndAbility() {
        Boss boss = new Boss(
                "Jax", 200, 30, 15,
                100, 10, -15,
                "The greatest warrior in Icathia.",
                BossAbility.IMMUNE_TURN,
                2
        );

        assertEquals("Jax", boss.getName());
        assertEquals(200, boss.getMaxHealth());
        assertEquals(30, boss.getAttack());
        assertEquals(15, boss.getDefense());
        assertEquals(100, boss.getGoldDrop());
        assertEquals(10, boss.getLoyaltyEffectOnWin());
        assertEquals(-15, boss.getLoyaltyEffectOnLoss());
        assertEquals("The greatest warrior in Icathia.", boss.getLoreDescription());
        assertEquals(BossAbility.IMMUNE_TURN, boss.getAbility());
        assertEquals(2, boss.getAbilityFrequency());
    }

    @Test
    void noneAbilityNeverTriggers() {
        Boss boss = new Boss("Enemy Captain", 100, 10, 5,
                20, 1, -1, "No power", BossAbility.NONE, 1);

        assertFalse(boss.shouldTriggerAbility(1));
        assertFalse(boss.shouldTriggerAbility(2));
    }

    @Test
    void passiveAbilitiesTriggerEveryRound() {
        Boss velkoz = new Boss("Vel'Koz", 100, 10, 5,
                20, 1, -1, "Passive true damage", BossAbility.TRUE_DAMAGE, 0);
        Boss vladimir = new Boss("Vladimir", 100, 10, 5,
                20, 1, -1, "Passive healing", BossAbility.HEAL_ON_HIT, 0);

        assertTrue(velkoz.shouldTriggerAbility(1));
        assertTrue(velkoz.shouldTriggerAbility(5));
        assertTrue(vladimir.shouldTriggerAbility(1));
        assertTrue(vladimir.shouldTriggerAbility(5));
    }

    @Test
    void activeAbilityTriggersOnlyOnFrequency() {
        Boss chogath = new Boss("Cho'Gath", 100, 10, 5,
                20, 1, -1, "Devour", BossAbility.DEVOUR, 3);

        assertFalse(chogath.shouldTriggerAbility(1));
        assertFalse(chogath.shouldTriggerAbility(2));
        assertTrue(chogath.shouldTriggerAbility(3));
        assertFalse(chogath.shouldTriggerAbility(4));
        assertTrue(chogath.shouldTriggerAbility(6));
    }

    @Test
    void activeAbilityWithZeroFrequencyDoesNotTrigger() {
        Boss invalidActive = new Boss("Bad Ability", 100, 10, 5,
                20, 1, -1, "Invalid", BossAbility.SLEEP, 0);

        assertFalse(invalidActive.shouldTriggerAbility(1));
        assertFalse(invalidActive.shouldTriggerAbility(10));
    }
}
