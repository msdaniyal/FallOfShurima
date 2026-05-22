package seng201.team76.unittests.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng201.team76.models.Adventurer;
import seng201.team76.models.Faction;
import seng201.team76.models.Guild;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Guild using the current rules:
 * the main character is always present, companions can die/leave permanently,
 * and party size is MC + 1 to 4 companions.
 */
public class GuildTest {

    private Guild guild;
    private Adventurer a1;
    private Adventurer a2;
    private Adventurer a3;
    private Adventurer a4;
    private Adventurer a5;

    @BeforeEach
    void setUp() {
        guild = new Guild("Shurima", 200, Faction.AATROX);
        a1 = makeAdventurer("Baalkux", Faction.AATROX, 20);
        a2 = makeAdventurer("Horazi", Faction.XOLAANI, 25);
        a3 = makeAdventurer("Ibaaros", Faction.AATROX, 20);
        a4 = makeAdventurer("Naafiri", Faction.XOLAANI, 25);
        a5 = makeAdventurer("Zaahen", Faction.NEUTRAL, 20);
    }

    private Adventurer makeAdventurer(String name, Faction faction, int pay) {
        return new Adventurer(name, 100, 10, 5,
                pay, faction, Faction.AATROX, "Test adventurer");
    }

    @Test
    void newGuildStartsWithChosenMainCharacterInParty() {
        assertEquals("Aatrox", guild.getMainCharacter().getName());
        assertEquals(1, guild.getMainParty().size());
        assertTrue(guild.getMainParty().contains(guild.getMainCharacter()));
    }

    @Test
    void xolaaniGuildStartsWithXolaaniAsMainCharacter() {
        Guild xolaaniGuild = new Guild("Blood Guild", 200, Faction.XOLAANI);

        assertEquals("Xolaani", xolaaniGuild.getMainCharacter().getName());
        assertEquals(Faction.XOLAANI, xolaaniGuild.getPlayerFaction());
        assertTrue(xolaaniGuild.getMainParty().contains(xolaaniGuild.getMainCharacter()));
    }

    @Test
    void replaceMainPartyAllowsMainCharacterPlusOneCompanion() {
        List<Adventurer> selection = Arrays.asList(guild.getMainCharacter(), a1);

        assertTrue(guild.replaceMainPartyWithSelection(selection));
        assertEquals(2, guild.getMainParty().size());
        assertSame(guild.getMainCharacter(), guild.getMainParty().get(0));
        assertTrue(guild.getMainParty().contains(a1));
    }

    @Test
    void replaceMainPartyRejectsMainCharacterOnly() {
        List<Adventurer> selection = Arrays.asList(guild.getMainCharacter());

        assertFalse(guild.replaceMainPartyWithSelection(selection));
        assertEquals(1, guild.getMainParty().size());
    }

    @Test
    void replaceMainPartyRejectsMoreThanFiveTotalMembers() {
        List<Adventurer> selection = Arrays.asList(
                guild.getMainCharacter(), a1, a2, a3, a4, a5
        );

        assertFalse(guild.replaceMainPartyWithSelection(selection));
    }

    @Test
    void replaceMainPartyChargesOnlyNewCompanions() {
        int goldBefore = guild.getGold();
        List<Adventurer> selection = Arrays.asList(guild.getMainCharacter(), a1, a2);

        assertTrue(guild.replaceMainPartyWithSelection(selection));

        assertEquals(goldBefore - a1.getPay() - a2.getPay(), guild.getGold());
    }

    @Test
    void mainCharacterCannotBeMovedToReserveOrRetired() {
        guild.addToMainParty(a1);

        assertFalse(guild.moveToReserves(guild.getMainCharacter()));
        assertFalse(guild.retire(guild.getMainCharacter()));
        assertTrue(guild.getMainParty().contains(guild.getMainCharacter()));
    }

    @Test
    void deadCompanionIsRemovedAndMarkedPermanentlyUnavailable() {
        guild.addToMainParty(a1);
        guild.getRecruitPool().add(a1);
        a1.setCurrentHealth(0);

        guild.removeDeadAdventurers();

        assertFalse(guild.getMainParty().contains(a1));
        assertTrue(guild.isPermanentlyUnavailable(a1));
        assertFalse(guild.getRecruitPool().contains(a1));
        assertEquals(1, guild.getDepartedAdventurers().size());
        assertEquals(Guild.FATE_FELL, guild.getDepartedAdventurers().get(0).getFate());
    }

    @Test
    void abandonedCompanionIsRemovedAndMarkedPermanentlyUnavailable() {
        guild.addToMainParty(a1);
        guild.getRecruitPool().add(a1);
        a1.adjustLoyalty(-200);

        guild.removeAbandoned();

        assertFalse(guild.getMainParty().contains(a1));
        assertTrue(guild.isPermanentlyUnavailable(a1));
        assertFalse(guild.getRecruitPool().contains(a1));
        assertEquals(1, guild.getDepartedAdventurers().size());
        assertEquals(Guild.FATE_LEFT, guild.getDepartedAdventurers().get(0).getFate());
    }

    @Test
    void permanentlyUnavailableCompanionCannotBeRecruitedAgain() {
        guild.addToMainParty(a1);
        a1.setCurrentHealth(0);
        guild.removeDeadAdventurers();
        guild.getRecruitPool().add(a1);

        assertFalse(guild.recruit(a1));
        assertFalse(guild.getMainParty().contains(a1));
    }

    @Test
    void mainCharacterDeathDoesNotMarkMainCharacterPermanentlyUnavailable() {
        Adventurer main = guild.getMainCharacter();
        main.setCurrentHealth(0);

        guild.removeDeadAdventurers();

        assertTrue(guild.getMainParty().contains(main));
        assertFalse(guild.isPermanentlyUnavailable(main));
        assertTrue(guild.isMainCharacterDead());
    }

    @Test
    void reviveMainCharacterForMenuRestoresMainCharacterAndKeepsThemFirst() {
        Adventurer main = guild.getMainCharacter();
        guild.addToMainParty(a1);
        main.setCurrentHealth(0);

        guild.reviveMainCharacterForMenu();

        assertFalse(main.isDead());
        assertEquals(main.getMaxHealth(), main.getCurrentHealth());
        assertSame(main, guild.getMainParty().get(0));
    }

    @Test
    void partyLockStopsRecruitingAndMovement() {
        guild.addToMainParty(a1);
        guild.addToReserves(a2);
        guild.getRecruitPool().add(a3);
        guild.lockParty();

        assertFalse(guild.recruit(a3));
        assertFalse(guild.moveToMainParty(a2));
        assertFalse(guild.moveToReserves(a1));
    }
}
