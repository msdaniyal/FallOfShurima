package seng201.team76.unittests.models;

import org.junit.jupiter.api.Test;
import seng201.team76.models.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests top-level Game progression and end-condition rules.
 */
public class GameTest {

    private Adventurer companion(String name) {
        return new Adventurer(name, 100, 10, 5,
                10, Faction.NEUTRAL, Faction.AATROX, "Test companion");
    }

    @Test
    void newGameStartsAtQuestOneOnlyQuestOneUnlocked() {
        Guild guild = new Guild("Shurima", 500, Faction.AATROX);
        Game game = new Game(guild, Difficulty.NORMAL);

        assertEquals(0, game.getCurrentQuestIndex());
        assertTrue(game.getQuests().get(0).isUnlocked());
        assertFalse(game.getQuests().get(1).isUnlocked());
        assertFalse(game.isGameOver());
    }

    @Test
    void selectQuestFailsWhenQuestIsLocked() {
        Guild guild = new Guild("Shurima", 500, Faction.AATROX);
        Game game = new Game(guild, Difficulty.NORMAL);

        assertFalse(game.selectQuest(1));
        assertEquals(0, game.getCurrentQuestIndex());
    }

    @Test
    void completingQuestOneUnlocksQuestTwo() {
        Guild guild = new Guild("Shurima", 500, Faction.AATROX);
        Game game = new Game(guild, Difficulty.NORMAL);

        game.advanceToNextQuest(0);

        assertTrue(game.getQuests().get(0).isCompleted());
        assertTrue(game.getQuests().get(1).isUnlocked());
        assertEquals(1, game.getCurrentQuestIndex());
    }

    @Test
    void loyaltyThresholdIsHighSoQuestSixUnlocksAfterQuestFive() {
        Guild guild = new Guild("Shurima", 500, Faction.AATROX);
        guild.addToMainParty(companion("Varus"));
        Game game = new Game(guild, Difficulty.EASY);

        game.advanceToNextQuest(4);

        assertEquals(101, game.getLoyaltyThreshold());
        assertTrue(game.getQuests().get(5).isUnlocked());
        assertEquals(5, game.getCurrentQuestIndex());
        assertFalse(game.isGameOver());
    }

    @Test
    void winningQuestSixSetsHardPathVictory() {
        Guild guild = new Guild("Shurima", 500, Faction.AATROX);
        guild.addToMainParty(companion("Varus"));
        Game game = new Game(guild, Difficulty.NORMAL);
        BossFight finalFight = game.getQuests().get(5).getBossFights().get(0);
        finalFight.getBoss().setCurrentHealth(0);
        finalFight.finishFightIfOver(guild);

        game.advanceToNextQuest(5);

        assertTrue(game.isGameOver());
        assertTrue(game.isPlayerWon());
        assertTrue(game.isCompleted());
        assertEquals("The End", game.getEndingTitle());
        assertEquals("All Darkin are sealed until they return.", game.getEndingDescription());
    }

    @Test
    void losingQuestSixSetsDefeat() {
        Guild guild = new Guild("Shurima", 500, Faction.AATROX);
        Game game = new Game(guild, Difficulty.NORMAL);
        guild.getMainCharacter().setCurrentHealth(0);
        BossFight finalFight = game.getQuests().get(5).getBossFights().get(0);
        finalFight.finishFightIfOver(guild);

        game.advanceToNextQuest(5);

        assertTrue(game.isGameOver());
        assertFalse(game.isPlayerWon());
        assertFalse(game.isCompleted());
        assertEquals("The End", game.getEndingTitle());
        assertEquals("All Darkin are sealed until they return.", game.getEndingDescription());
    }
}
