package seng201.team76.unittests.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng201.team76.models.Adventurer;
import seng201.team76.models.Faction;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Adventurer class
 * @author Mohammed, Xinyi
 */
public class AdventurerTest {

    private Adventurer aatroxAlly;
    private Adventurer xolaaniAlly;
    private Adventurer neutral;

    @BeforeEach
    void setUp() {
        // Player chose Aatrox
        aatroxAlly = new Adventurer("Joraal", 100, 15, 10,
                20, Faction.AATROX, Faction.AATROX, "Aatrox's most trusted general.");
        xolaaniAlly = new Adventurer("Zaahen", 80, 12, 8,
                15, Faction.XOLAANI, Faction.AATROX, "A spear warrior of Xolaani.");
        neutral = new Adventurer("Rhaast", 90, 14, 7,
                18, Faction.NEUTRAL, Faction.AATROX, "A Darkin warrior.");
    }

    // ── Starting loyalty ──────────────────────────────────────────────────

    @Test
    void testAlliedAdventurerStartsWithHighLoyalty() {
        assertEquals(80, aatroxAlly.getLoyalty());
    }

    @Test
    void testOpposingAdventurerStartsWithLowLoyalty() {
        assertEquals(30, xolaaniAlly.getLoyalty());
    }

    @Test
    void testNeutralAdventurerStartsWithMidLoyalty() {
        assertEquals(50, neutral.getLoyalty());
    }

    // ── adjustLoyalty ─────────────────────────────────────────────────────

    @Test
    void testAdjustLoyaltyIncrease() {
        aatroxAlly.adjustLoyalty(10);
        assertEquals(90, aatroxAlly.getLoyalty());
    }

    @Test
    void testAdjustLoyaltyDecrease() {
        aatroxAlly.adjustLoyalty(-20);
        assertEquals(60, aatroxAlly.getLoyalty());
    }

    @Test
    void testAdjustLoyaltyClampsAtHundred() {
        aatroxAlly.adjustLoyalty(100);
        assertEquals(100, aatroxAlly.getLoyalty());
    }

    @Test
    void testAdjustLoyaltyClampsAtZero() {
        aatroxAlly.adjustLoyalty(-200);
        assertEquals(0, aatroxAlly.getLoyalty());
    }

    @Test
    void testAdjustLoyaltyToZeroSetsAbandoned() {
        aatroxAlly.adjustLoyalty(-200);
        assertTrue(aatroxAlly.getAbandoned());
    }

    @Test
    void testAdventurerNotAbandonedByDefault() {
        assertFalse(aatroxAlly.getAbandoned());
    }

    // ── isLoyal ───────────────────────────────────────────────────────────

    @Test
    void testIsLoyalAboveThreshold() {
        assertTrue(aatroxAlly.isLoyal(50));
    }

    @Test
    void testIsLoyalAtExactThreshold() {
        assertTrue(aatroxAlly.isLoyal(80));
    }

    @Test
    void testIsNotLoyalBelowThreshold() {
        assertFalse(xolaaniAlly.isLoyal(50));
    }

    // ── increaseMadness ───────────────────────────────────────────────────

    @Test
    void testIncreaseMadnessNormal() {
        aatroxAlly.increaseMadness(30);
        assertEquals(30, aatroxAlly.getMadness());
    }

    @Test
    void testIncreaseMadnessClampsAtHundred() {
        aatroxAlly.increaseMadness(200);
        assertEquals(100, aatroxAlly.getMadness());
    }

    @Test
    void testHighMadnessDrainsLoyalty() {
        int loyaltyBefore = aatroxAlly.getLoyalty();
        aatroxAlly.increaseMadness(75);
        assertTrue(aatroxAlly.getLoyalty() < loyaltyBefore);
    }

    @Test
    void testLowMadnessDoesNotDrainLoyalty() {
        int loyaltyBefore = aatroxAlly.getLoyalty();
        aatroxAlly.increaseMadness(50);
        assertEquals(loyaltyBefore, aatroxAlly.getLoyalty());
    }

    // ── updateAfterQuest ──────────────────────────────────────────────────

    @Test
    void testUpdateAfterQuestAppliesLoyaltyDelta() {
        aatroxAlly.updateAfterQuest(10, 0, 0);
        assertEquals(90, aatroxAlly.getLoyalty());
    }

    @Test
    void testUpdateAfterQuestAppliesHealthDelta() {
        aatroxAlly.setCurrentHealth(50);
        aatroxAlly.updateAfterQuest(0, 20, 0);
        assertEquals(70, aatroxAlly.getCurrentHealth());
    }

    @Test
    void testUpdateAfterQuestAppliesMadnessDelta() {
        aatroxAlly.updateAfterQuest(0, 0, 30);
        assertEquals(30, aatroxAlly.getMadness());
    }

    @Test
    void testUpdateAfterQuestIgnoresNegativeMadnessDelta() {
        aatroxAlly.updateAfterQuest(0, 0, -10);
        assertEquals(0, aatroxAlly.getMadness());
    }
}