package seng201.team76.unittests.models;

import org.junit.jupiter.api.Test;
import seng201.team76.models.Faction;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Faction enum.
 * @author Mohammed, Xinyi
 */
public class FactionTest {

    @Test
    void testAatroxAlliedLoyalty() {
        assertEquals(80, Faction.AATROX.getStartingLoyalty(Faction.AATROX));
    }

    @Test
    void testAatroxOpposedLoyalty() {
        assertEquals(30, Faction.AATROX.getStartingLoyalty(Faction.XOLAANI));
    }

    @Test
    void testXolaaniAlliedLoyalty() {
        assertEquals(80, Faction.XOLAANI.getStartingLoyalty(Faction.XOLAANI));
    }

    @Test
    void testXolaaniOpposedLoyalty() {
        assertEquals(30, Faction.XOLAANI.getStartingLoyalty(Faction.AATROX));
    }

    @Test
    void testNeutralLoyaltyWithAatroxPlayer() {
        assertEquals(50, Faction.NEUTRAL.getStartingLoyalty(Faction.AATROX));
    }

    @Test
    void testNeutralLoyaltyWithXolaaniPlayer() {
        assertEquals(50, Faction.NEUTRAL.getStartingLoyalty(Faction.XOLAANI));
    }

    @Test
    void testNeutralLoyaltyWithNeutralPlayer() {
        assertEquals(50, Faction.NEUTRAL.getStartingLoyalty(Faction.NEUTRAL));
    }
}