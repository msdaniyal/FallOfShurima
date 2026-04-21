package seng201.team0.unittests.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng201.team0.models.Adventurer;
import seng201.team0.models.Faction;
import seng201.team0.models.Guild;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Guild class.
 * @author Mohammed, Xinyi
 */
public class GuildTest {

    private Guild guild;
    private Adventurer a1, a2, a3, a4, a5, a6;
    private Adventurer aatroxMember, xolaaniMember, neutralMember;

    @BeforeEach
    void setUp() {
        guild = new Guild("Shurima", 200, Faction.AATROX);

        a1 = makeAdventurer("A1", Faction.NEUTRAL);
        a2 = makeAdventurer("A2", Faction.NEUTRAL);
        a3 = makeAdventurer("A3", Faction.NEUTRAL);
        a4 = makeAdventurer("A4", Faction.NEUTRAL);
        a5 = makeAdventurer("A5", Faction.NEUTRAL);
        a6 = makeAdventurer("A6", Faction.NEUTRAL);

        aatroxMember = makeAdventurer("Joraal", Faction.AATROX);
        xolaaniMember = makeAdventurer("Zaahen", Faction.XOLAANI);
        neutralMember = makeAdventurer("Rhaast", Faction.NEUTRAL);
    }

    private Adventurer makeAdventurer(String name, Faction faction) {
        return new Adventurer(name, 100, 10, 5,
                20, faction, Faction.AATROX, "Test adventurer");
    }

    // ── Gold ──────────────────────────────────────────────────────────────

    @Test
    void testAddGold() {
        guild.addGold(100);
        assertEquals(300, guild.getGold());
    }

    @Test
    void testSpendGoldSuccess() {
        assertTrue(guild.spendGold(100));
        assertEquals(100, guild.getGold());
    }

    @Test
    void testSpendGoldFailsWhenNotEnough() {
        assertFalse(guild.spendGold(500));
        assertEquals(200, guild.getGold());
    }

    @Test
    void testSpendGoldExactAmount() {
        assertTrue(guild.spendGold(200));
        assertEquals(0, guild.getGold());
    }

    // ── addToMainParty ────────────────────────────────────────────────────

    @Test
    void testAddToMainPartySuccess() {
        assertTrue(guild.addToMainParty(a1));
        assertEquals(1, guild.getMainParty().size());
    }

    @Test
    void testAddToMainPartyRespectsMaxSize() {
        guild.addToMainParty(a1);
        guild.addToMainParty(a2);
        guild.addToMainParty(a3);
        guild.addToMainParty(a4);
        guild.addToMainParty(a5);
        assertFalse(guild.addToMainParty(a6));
        assertEquals(5, guild.getMainParty().size());
    }

    // ── addToReserves ─────────────────────────────────────────────────────

    @Test
    void testAddToReservesSuccess() {
        assertTrue(guild.addToReserves(a1));
        assertEquals(1, guild.getReserves().size());
    }

    @Test
    void testAddToReservesRespectsMaxSize() {
        guild.addToReserves(a1);
        guild.addToReserves(a2);
        guild.addToReserves(a3);
        guild.addToReserves(a4);
        guild.addToReserves(a5);
        assertFalse(guild.addToReserves(a6));
        assertEquals(5, guild.getReserves().size());
    }

    // ── moveToMainParty ───────────────────────────────────────────────────

    @Test
    void testMoveToMainPartySuccess() {
        guild.addToMainParty(a1);
        guild.addToReserves(a2);
        assertTrue(guild.moveToMainParty(a2));
        assertTrue(guild.getMainParty().contains(a2));
        assertFalse(guild.getReserves().contains(a2));
    }

    @Test
    void testMoveToMainPartyFailsWhenFull() {
        guild.addToMainParty(a1);
        guild.addToMainParty(a2);
        guild.addToMainParty(a3);
        guild.addToMainParty(a4);
        guild.addToMainParty(a5);
        guild.addToReserves(a6);
        assertFalse(guild.moveToMainParty(a6));
    }

    @Test
    void testMoveToMainPartyFailsWhenLocked() {
        guild.addToMainParty(a1);
        guild.addToReserves(a2);
        guild.lockParty();
        assertFalse(guild.moveToMainParty(a2));
    }

    // ── moveToReserves ────────────────────────────────────────────────────

    @Test
    void testMoveToReservesSuccess() {
        guild.addToMainParty(a1);
        guild.addToMainParty(a2);
        assertTrue(guild.moveToReserves(a2));
        assertTrue(guild.getReserves().contains(a2));
        assertFalse(guild.getMainParty().contains(a2));
    }

    @Test
    void testMoveToReservesFailsWhenOnlyOneMember() {
        guild.addToMainParty(a1);
        assertFalse(guild.moveToReserves(a1));
        assertEquals(1, guild.getMainParty().size());
    }

    @Test
    void testMoveToReservesFailsWhenLocked() {
        guild.addToMainParty(a1);
        guild.addToMainParty(a2);
        guild.lockParty();
        assertFalse(guild.moveToReserves(a2));
    }

    // ── recruit ───────────────────────────────────────────────────────────

    @Test
    void testRecruitSuccess() {
        guild.addToMainParty(a1);
        guild.getRecruitPool().add(a2);
        assertTrue(guild.recruit(a2));
        assertFalse(guild.getRecruitPool().contains(a2));
    }

    @Test
    void testRecruitDeductsGold() {
        guild.addToMainParty(a1);
        guild.getRecruitPool().add(a2);
        guild.recruit(a2);
        assertEquals(180, guild.getGold());
    }

    @Test
    void testRecruitFailsWhenNotEnoughGold() {
        Guild poorGuild = new Guild("Poor", 5, Faction.AATROX);
        poorGuild.addToMainParty(a1);
        poorGuild.getRecruitPool().add(a2);
        assertFalse(poorGuild.recruit(a2));
    }

    @Test
    void testRecruitFailsWhenLocked() {
        guild.addToMainParty(a1);
        guild.getRecruitPool().add(a2);
        guild.lockParty();
        assertFalse(guild.recruit(a2));
    }

    @Test
    void testRecruitFailsWhenNotInPool() {
        guild.addToMainParty(a1);
        assertFalse(guild.recruit(a2));
    }

    // ── retire ────────────────────────────────────────────────────────────

    @Test
    void testRetireFromMainPartySuccess() {
        guild.addToMainParty(a1);
        guild.addToMainParty(a2);
        assertTrue(guild.retire(a2));
        assertFalse(guild.getMainParty().contains(a2));
    }

    @Test
    void testRetireFailsWhenOnlyOneMember() {
        guild.addToMainParty(a1);
        assertFalse(guild.retire(a1));
    }

    @Test
    void testRetireFromReservesSuccess() {
        guild.addToMainParty(a1);
        guild.addToReserves(a2);
        assertTrue(guild.retire(a2));
        assertFalse(guild.getReserves().contains(a2));
    }

    @Test
    void testRetireFailsWhenLocked() {
        guild.addToMainParty(a1);
        guild.addToMainParty(a2);
        guild.lockParty();
        assertFalse(guild.retire(a2));
    }

    // ── removeAbandoned ───────────────────────────────────────────────────

    @Test
    void testRemoveAbandonedFromMainParty() {
        guild.addToMainParty(a1);
        guild.addToMainParty(a2);
        a2.adjustLoyalty(-200);
        guild.removeAbandoned();
        assertFalse(guild.getMainParty().contains(a2));
    }

    @Test
    void testRemoveAbandonedFromReserves() {
        guild.addToMainParty(a1);
        guild.addToReserves(a2);
        a2.adjustLoyalty(-200);
        guild.removeAbandoned();
        assertFalse(guild.getReserves().contains(a2));
    }

    // ── collapseOpposingFaction ───────────────────────────────────────────

    @Test
    void testCollapseOpposingFactionRemovesXolaaniMembers() {
        guild.addToMainParty(aatroxMember);
        guild.addToMainParty(xolaaniMember);
        guild.collapseOpposingFaction();
        assertFalse(guild.getMainParty().contains(xolaaniMember));
    }

    @Test
    void testCollapseOpposingFactionKeepsAatroxMembers() {
        guild.addToMainParty(aatroxMember);
        guild.addToMainParty(xolaaniMember);
        guild.collapseOpposingFaction();
        assertTrue(guild.getMainParty().contains(aatroxMember));
    }

    @Test
    void testCollapseOpposingFactionKeepsNeutralMembers() {
        guild.addToMainParty(aatroxMember);
        guild.addToMainParty(neutralMember);
        guild.collapseOpposingFaction();
        assertTrue(guild.getMainParty().contains(neutralMember));
    }

    // ── checkLoyaltyThreshold ─────────────────────────────────────────────

    @Test
    void testCheckLoyaltyThresholdAllAbove() {
        guild.addToMainParty(aatroxMember);
        assertTrue(guild.checkLoyaltyThreshold(50));
    }

    @Test
    void testCheckLoyaltyThresholdSomeBelow() {
        guild.addToMainParty(aatroxMember);
        guild.addToMainParty(xolaaniMember);
        assertFalse(guild.checkLoyaltyThreshold(50));
    }

    // ── isWiped ───────────────────────────────────────────────────────────

    @Test
    void testIsWipedWhenPartyEmpty() {
        assertTrue(guild.isWiped());
    }

    @Test
    void testIsNotWipedWhenPartyHasMembers() {
        guild.addToMainParty(a1);
        assertFalse(guild.isWiped());
    }

    // ── payParty ──────────────────────────────────────────────────────────

    @Test
    void testPayPartySuccess() {
        guild.addToMainParty(a1);
        guild.addToMainParty(a2);
        assertTrue(guild.payParty());
        assertEquals(160, guild.getGold());
    }

    @Test
    void testPayPartyFailsWhenNotEnoughGold() {
        Guild poorGuild = new Guild("Poor", 10, Faction.AATROX);
        poorGuild.addToMainParty(a1);
        poorGuild.addToMainParty(a2);
        assertFalse(poorGuild.payParty());
    }

    // ── partyLocked ───────────────────────────────────────────────────────

    @Test
    void testPartyNotLockedByDefault() {
        assertFalse(guild.isPartyLocked());
    }

    @Test
    void testLockParty() {
        guild.lockParty();
        assertTrue(guild.isPartyLocked());
    }
}