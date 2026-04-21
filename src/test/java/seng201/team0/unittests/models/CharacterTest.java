package seng201.team0.unittests.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng201.team0.models.Adventurer;
import seng201.team0.models.Faction;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Character base class functionality.
 * Uses Adventurer as a concrete implementation since Character is abstract.
 * @author ClaudeAI
 */
public class CharacterTest {

    private Adventurer character;

    @BeforeEach
    void setUp() {
        character = new Adventurer("TestChar", 100, 10, 5,
                10, Faction.NEUTRAL, Faction.NEUTRAL, "A test character");
    }

    @Test
    void testInitialHealthEqualsMaxHealth() {
        assertEquals(100, character.getCurrentHealth());
        assertEquals(100, character.getMaxHealth());
    }

    @Test
    void testSetCurrentHealthNormal() {
        character.setCurrentHealth(60);
        assertEquals(60, character.getCurrentHealth());
    }

    @Test
    void testSetCurrentHealthClampsAtZero() {
        character.setCurrentHealth(-50);
        assertEquals(0, character.getCurrentHealth());
    }

    @Test
    void testSetCurrentHealthClampsAtMax() {
        character.setCurrentHealth(999);
        assertEquals(100, character.getCurrentHealth());
    }

    @Test
    void testIsDeadWhenHealthIsZero() {
        character.setCurrentHealth(0);
        assertTrue(character.isDead());
    }

    @Test
    void testIsNotDeadWhenHealthAboveZero() {
        character.setCurrentHealth(1);
        assertFalse(character.isDead());
    }

    @Test
    void testResetHealthRestoresToMax() {
        character.setCurrentHealth(30);
        character.resetHealth();
        assertEquals(100, character.getCurrentHealth());
    }

    @Test
    void testGetName() {
        assertEquals("TestChar", character.getName());
    }

    @Test
    void testGetAttack() {
        assertEquals(10, character.getAttack());
    }

    @Test
    void testGetDefense() {
        assertEquals(5, character.getDefense());
    }

    @Test
    void testSetAttack() {
        character.setAttack(20);
        assertEquals(20, character.getAttack());
    }

    @Test
    void testSetDefense() {
        character.setDefense(15);
        assertEquals(15, character.getDefense());
    }
}