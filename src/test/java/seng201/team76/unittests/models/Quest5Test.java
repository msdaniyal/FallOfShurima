package seng201.team76.unittests.models;

import org.junit.jupiter.api.Test;
import seng201.team76.models.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests Quest 5 rival-guild generation.
 */
public class Quest5Test {

    private Adventurer companion(String name, Faction faction) {
        return new Adventurer(name, 100, 10, 5,
                10, faction, Faction.AATROX, "Test companion");
    }

    @Test
    void aatroxPlayerFacesXolaaniAsFinalCaptain() {
        Guild guild = new Guild("Shurima", 500, Faction.AATROX);
        Quest5 quest5 = new Quest5(Difficulty.NORMAL);

        quest5.prepareEnemyGuild(guild);
        List<BossFight> fights = quest5.getBossFights();
        Boss finalCaptain = fights.get(fights.size() - 1).getBoss();

        assertEquals("Xolaani", finalCaptain.getName());
        assertEquals(BossAbility.HEAL_ON_HIT, finalCaptain.getAbility());
        assertEquals(185, finalCaptain.getMaxHealth(),
                "Xolaani should not be a tank in the final battle.");
        assertEquals(6, finalCaptain.getDefense());
    }

    @Test
    void xolaaniPlayerFacesAatroxAsFinalCaptain() {
        Guild guild = new Guild("Blood Guild", 500, Faction.XOLAANI);
        Quest5 quest5 = new Quest5(Difficulty.NORMAL);

        quest5.prepareEnemyGuild(guild);
        List<BossFight> fights = quest5.getBossFights();
        Boss finalCaptain = fights.get(fights.size() - 1).getBoss();

        assertEquals("Aatrox", finalCaptain.getName());
        assertEquals(BossAbility.NONE, finalCaptain.getAbility());
    }

    @Test
    void recruitedCompanionsDoNotJoinRivalGuild() {
        Guild guild = new Guild("Shurima", 500, Faction.AATROX);
        Adventurer baalkux = companion("Baalkux", Faction.AATROX);
        guild.addToMainParty(baalkux);
        Quest5 quest5 = new Quest5(Difficulty.NORMAL);

        quest5.prepareEnemyGuild(guild);
        List<String> bossNames = quest5.getBossFights().stream()
                .map(fight -> fight.getBoss().getName())
                .collect(Collectors.toList());

        assertFalse(bossNames.contains("Baalkux"));
    }

    @Test
    void deadOrAbandonedFormerCompanionsDoNotJoinRivalGuild() {
        Guild guild = new Guild("Shurima", 500, Faction.AATROX);
        Adventurer horazi = companion("Horazi", Faction.XOLAANI);
        Adventurer ibaaros = companion("Ibaaros", Faction.AATROX);
        guild.addToMainParty(horazi);
        guild.addToMainParty(ibaaros);

        horazi.setCurrentHealth(0);
        guild.removeDeadAdventurers();
        ibaaros.adjustLoyalty(-200);
        guild.removeAbandoned();

        Quest5 quest5 = new Quest5(Difficulty.NORMAL);
        quest5.prepareEnemyGuild(guild);
        List<String> bossNames = quest5.getBossFights().stream()
                .map(fight -> fight.getBoss().getName())
                .collect(Collectors.toList());

        assertFalse(bossNames.contains("Horazi"));
        assertFalse(bossNames.contains("Ibaaros"));
        assertTrue(guild.isPermanentlyUnavailable("Horazi"));
        assertTrue(guild.isPermanentlyUnavailable("Ibaaros"));
    }

    @Test
    void rivalGuildUsesMaximumThreeUnrecruitedCompanionsPlusCaptain() {
        Guild guild = new Guild("Shurima", 500, Faction.AATROX);
        Quest5 quest5 = new Quest5(Difficulty.NORMAL);

        quest5.prepareEnemyGuild(guild);

        assertTrue(quest5.getBossFights().size() <= 4,
                "Quest 5 should contain at most 3 unrecruited rivals plus the opposing main character.");
        assertEquals("Xolaani", quest5.getBossFights()
                .get(quest5.getBossFights().size() - 1)
                .getBoss()
                .getName());
    }
}
