package seng201.team0.unittests.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng201.team0.models.Boss;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Boss class.
 * @author Mohammed, Xinyi
 */
public class BossTest {

    private Boss jax;
    private Boss weakBoss;

    @BeforeEach
    void setUp() {
        jax = new Boss("Jax", 200, 30, 15,
                100, 10, -15,
                "The greatest warrior in Icathia.");
        weakBoss = new Boss("WeakBoss", 50, 5, 0,
                20, 5, -5,
                "A weak enemy.");
    }

    @Test
    void testGetGoldDrop() {
        assertEquals(100, jax.getGoldDrop());
    }

    @Test
    void testGetLoyaltyEffectOnWin() {
        assertEquals(10, jax.getLoyaltyEffectOnWin());
    }

    @Test
    void testGetLoyaltyEffectOnLoss() {
        assertEquals(-15, jax.getLoyaltyEffectOnLoss());
    }

    @Test
    void testGetLoreDescription() {
        assertEquals("The greatest warrior in Icathia.", jax.getLoreDescription());
    }

    @Test
    void testGetDamageNormal() {
        // attack 30 - partyDefense 10 = 20
        assertEquals(20, jax.getDamage(10));
    }

    @Test
    void testGetDamageReturnsMinimumOne() {
        // attack 5 - partyDefense 100 = negative, should return 1
        assertEquals(1, weakBoss.getDamage(100));
    }

    @Test
    void testGetDamageWhenDefenseIsZero() {
        assertEquals(5, weakBoss.getDamage(0));
    }

    @Test
    void testBossHealthInitialisedCorrectly() {
        assertEquals(200, jax.getMaxHealth());
        assertEquals(200, jax.getCurrentHealth());
    }

    @Test
    void testBossIsDeadWhenHealthZero() {
        jax.setCurrentHealth(0);
        assertTrue(jax.isDead());
    }

}