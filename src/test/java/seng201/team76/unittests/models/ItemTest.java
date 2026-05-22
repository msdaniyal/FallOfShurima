package seng201.team76.unittests.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng201.team76.models.Adventurer;
import seng201.team76.models.Faction;
import seng201.team76.models.Guild;
import seng201.team76.models.Item;
import seng201.team76.models.ItemType;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Item (potion) class.
 * @author Mohammed, Xinyi
 */
public class ItemTest {

    private Guild guild;
    private Adventurer adventurer1;
    private Adventurer adventurer2;
    private Adventurer adventurer3;
    private Item singlePotion;
    private Item partyPotion;
    private Item fullPotion;

    @BeforeEach
    void setUp() {
        guild = new Guild("TestGuild", 500, Faction.AATROX);

        adventurer1 = new Adventurer("A1", 100, 10, 5,
                10, Faction.NEUTRAL, Faction.AATROX, "Test 1");
        adventurer2 = new Adventurer("A2", 100, 10, 5,
                10, Faction.NEUTRAL, Faction.AATROX, "Test 2");
        adventurer3 = new Adventurer("A3", 100, 10, 5,
                10, Faction.NEUTRAL, Faction.AATROX, "Test 3");

        adventurer1.setCurrentHealth(40);
        adventurer2.setCurrentHealth(50);
        adventurer3.setCurrentHealth(60);

        guild.addToMainParty(adventurer1);
        guild.addToMainParty(adventurer2);
        guild.addToMainParty(adventurer3);

        singlePotion = new Item("Sun Vial", ItemType.SINGLE, 30, 50, "Heals one adventurer.");
        partyPotion = new Item("Shurima Elixir", ItemType.PARTY, 20, 80, "Heals the whole party.");
        fullPotion = new Item("Ascended Draught", ItemType.FULL, 0, 150, "Fully restores the party.");
    }

    // ── SINGLE ────────────────────────────────────────────────────────────

    @Test
    void testSinglePotionHealsTarget() {
        singlePotion.use(guild, adventurer1);
        assertEquals(70, adventurer1.getCurrentHealth());
    }

    @Test
    void testSinglePotionDoesNotHealOthers() {
        singlePotion.use(guild, adventurer1);
        assertEquals(50, adventurer2.getCurrentHealth());
        assertEquals(60, adventurer3.getCurrentHealth());
    }

    @Test
    void testSinglePotionClampsAtMaxHealth() {
        adventurer1.setCurrentHealth(90);
        singlePotion.use(guild, adventurer1);
        assertEquals(100, adventurer1.getCurrentHealth());
    }

    @Test
    void testSinglePotionWithNullTargetDoesNothing() {
        singlePotion.use(guild, null);
        assertEquals(40, adventurer1.getCurrentHealth());
    }

    // ── PARTY ─────────────────────────────────────────────────────────────

    @Test
    void testPartyPotionHealsAllMembers() {
        partyPotion.use(guild, null);
        assertEquals(60, adventurer1.getCurrentHealth());
        assertEquals(70, adventurer2.getCurrentHealth());
        assertEquals(80, adventurer3.getCurrentHealth());
    }

    @Test
    void testPartyPotionClampsAtMaxHealth() {
        adventurer3.setCurrentHealth(90);
        partyPotion.use(guild, null);
        assertEquals(100, adventurer3.getCurrentHealth());
    }

    // ── FULL ──────────────────────────────────────────────────────────────

    @Test
    void testFullPotionRestoresAllToMax() {
        fullPotion.use(guild, null);
        assertEquals(100, adventurer1.getCurrentHealth());
        assertEquals(100, adventurer2.getCurrentHealth());
        assertEquals(100, adventurer3.getCurrentHealth());
    }

    @Test
    void testFullPotionWorksWhenAlreadyFullHealth() {
        adventurer1.resetHealth();
        fullPotion.use(guild, null);
        assertEquals(100, adventurer1.getCurrentHealth());
    }

    // ── Getters ───────────────────────────────────────────────────────────

    @Test
    void testGetName() {
        assertEquals("Sun Vial", singlePotion.getName());
    }

    @Test
    void testGetCost() {
        assertEquals(50, singlePotion.getCost());
    }

    @Test
    void testGetHealAmount() {
        assertEquals(30, singlePotion.getHealAmount());
    }

    @Test
    void testGetType() {
        assertEquals(ItemType.SINGLE, singlePotion.getType());
    }
}