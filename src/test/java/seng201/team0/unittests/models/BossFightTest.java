package seng201.team0.unittests.models;

import seng201.team0.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for BossFight class.
 * @author Mohammed, Xinyi
 */

public class BossFightTest {

    private Boss boss;
    private Adventurer mainAdventurer;
    private Adventurer weakAdventurer;
    private Adventurer strongAdventurer;
    private Guild guild;
    private BossFight bossFight;

    @BeforeEach
    void setUp() {
        boss = createBoss();

        mainAdventurer = createMainAdventurer();
        weakAdventurer = createWeakAdventurer();
        strongAdventurer = createStrongAdventurer();

        guild = new Guild("Test Guild", 100, Faction.AATROX);
        guild.addToMainParty(mainAdventurer);
        guild.addToMainParty(weakAdventurer);
        guild.addToMainParty(strongAdventurer);

        bossFight = new BossFight(boss, 1, mainAdventurer);
    }

    @Test
    void constructorShouldSetFieldsCorrectly() {
        assertEquals(boss, bossFight.getBoss());
        assertEquals(1, bossFight.getSequence());
        assertFalse(bossFight.isPlayerWon());
        assertFalse(bossFight.isMcBlocking());
    }

    @Test
    void calcAdventurerDamageShouldApplyHighLoyaltyBonus() {
        int damage = bossFight.calcAdventurerDamage(strongAdventurer);
        assertEquals(9, damage);
    }

    @Test
    void calcAdventurerDamageShouldApplyLowLoyaltyPenalty() {
        int damage = bossFight.calcAdventurerDamage(weakAdventurer);
        assertEquals(3, damage);
    }

    @Test
    void calcAdventurerDamageShouldApplyMadnessPenalty() {
        Adventurer crazy = createMadAdventurer();
        int damage = bossFight.calcAdventurerDamage(crazy);
        assertEquals(5, damage);
    }

    @Test
    void calcAdventurerDamageShouldNeverGoBelowOne() {
        Adventurer veryWeak = createVeryWeakAdventurer();
        int damage = bossFight.calcAdventurerDamage(veryWeak);
        assertEquals(1, damage);
    }

    @Test
    void calcBossDamageShouldUseBossAttackMinusTargetDefense() {
        int damage = bossFight.calcBossDamage(strongAdventurer);
        assertEquals(7, damage);
    }

    @Test
    void calcBossDamageShouldNeverGoBelowOne() {
        Adventurer tank = createTankAdventurer();
        int damage = bossFight.calcBossDamage(tank);
        assertEquals(1, damage);
    }

    @Test
    void findWeakestTargetShouldReturnAliveLowestDefenseAdventurer() {
        Adventurer target = bossFight.findWeakestTarget(guild.getMainParty());
        assertEquals(weakAdventurer, target);
    }

    @Test
    void findWeakestTargetShouldIgnoreDeadAdventurers() {
        weakAdventurer.setCurrentHealth(0);

        Adventurer target = bossFight.findWeakestTarget(guild.getMainParty());
        assertEquals(mainAdventurer, target);
    }

    @Test
    void setMcBlockingShouldUpdateBlockingState() {
        bossFight.setMcBlocking(true);
        assertTrue(bossFight.isMcBlocking());

        bossFight.setMcBlocking(false);
        assertFalse(bossFight.isMcBlocking());
    }

    @Test
    void playerAttackShouldDealDamageWhenSuccessIsTrue() {
        int bossHealthBefore = boss.getCurrentHealth();

        int damage = bossFight.playerAttack(strongAdventurer, true);

        assertEquals(9, damage);
        assertEquals(bossHealthBefore - damage, boss.getCurrentHealth());
    }

    @Test
    void playerAttackShouldDealZeroWhenSuccessIsFalse() {
        int bossHealthBefore = boss.getCurrentHealth();

        int damage = bossFight.playerAttack(strongAdventurer, false);

        assertEquals(0, damage);
        assertEquals(bossHealthBefore, boss.getCurrentHealth());
    }

    @Test
    void playerAttackShouldDealZeroWhenAdventurerIsNull() {
        int damage = bossFight.playerAttack(null, true);
        assertEquals(0, damage);
    }

    @Test
    void playerAttackShouldDealZeroWhenAdventurerIsDead() {
        strongAdventurer.setCurrentHealth(0);

        int damage = bossFight.playerAttack(strongAdventurer, true);

        assertEquals(0, damage);
    }

    @Test
    void playerAttackShouldDealZeroWhenBossIsDead() {
        boss.setCurrentHealth(0);

        int damage = bossFight.playerAttack(strongAdventurer, true);

        assertEquals(0, damage);
    }

    @Test
    void bossTurnShouldAttackWeakestTargetNormally() {
        int weakHealthBefore = weakAdventurer.getCurrentHealth();

        Adventurer attacked = bossFight.bossTurn(guild);

        assertEquals(weakAdventurer, attacked);
        assertEquals(
                weakHealthBefore - bossFight.calcBossDamage(weakAdventurer),
                weakAdventurer.getCurrentHealth()
        );
        assertFalse(bossFight.isMcBlocking());
    }

    @Test
    void bossTurnShouldAttackMainAdventurerWhenBlocking() {
        int mainHealthBefore = mainAdventurer.getCurrentHealth();

        bossFight.setMcBlocking(true);
        Adventurer attacked = bossFight.bossTurn(guild);

        assertEquals(mainAdventurer, attacked);
        assertEquals(
                mainHealthBefore - bossFight.calcBossDamage(mainAdventurer),
                mainAdventurer.getCurrentHealth()
        );
        assertFalse(bossFight.isMcBlocking());
    }

    @Test
    void bossTurnShouldReturnNullWhenBossIsDead() {
        boss.setCurrentHealth(0);
        bossFight.setMcBlocking(true);

        Adventurer attacked = bossFight.bossTurn(guild);

        assertNull(attacked);
        assertFalse(bossFight.isMcBlocking());
    }

    @Test
    void bossTurnShouldReturnNullWhenGuildIsNull() {
        bossFight.setMcBlocking(true);

        Adventurer attacked = bossFight.bossTurn(null);

        assertNull(attacked);
        assertFalse(bossFight.isMcBlocking());
    }

    @Test
    void bossTurnShouldReturnNullWhenGuildIsWiped() {
        mainAdventurer.setCurrentHealth(0);
        weakAdventurer.setCurrentHealth(0);
        strongAdventurer.setCurrentHealth(0);
        guild.removeDeadAdventurers();

        bossFight.setMcBlocking(true);
        Adventurer attacked = bossFight.bossTurn(guild);

        assertNull(attacked);
        assertFalse(bossFight.isMcBlocking());
    }

    @Test
    void bossTurnShouldRemoveDeadAdventurersFromGuild() {
        weakAdventurer.setCurrentHealth(1);

        Adventurer attacked = bossFight.bossTurn(guild);

        assertEquals(weakAdventurer, attacked);
        assertTrue(weakAdventurer.isDead());
        assertFalse(guild.getMainParty().contains(weakAdventurer));
    }

    @Test
    void isFightOverShouldReturnTrueWhenBossIsDead() {
        boss.setCurrentHealth(0);
        assertTrue(bossFight.isFightOver(guild));
    }

    @Test
    void isFightOverShouldReturnTrueWhenGuildIsWiped() {
        mainAdventurer.setCurrentHealth(0);
        weakAdventurer.setCurrentHealth(0);
        strongAdventurer.setCurrentHealth(0);
        guild.removeDeadAdventurers();

        assertTrue(bossFight.isFightOver(guild));
    }

    @Test
    void isFightOverShouldReturnFalseWhenBothSidesStillAlive() {
        assertFalse(bossFight.isFightOver(guild));
    }

    @Test
    void finishFightIfOverShouldApplyWinRewards() {
        boss.setCurrentHealth(0);

        int goldBefore = guild.getGold();
        int loyaltyBefore1 = mainAdventurer.getLoyalty();
        int loyaltyBefore2 = weakAdventurer.getLoyalty();
        int loyaltyBefore3 = strongAdventurer.getLoyalty();

        bossFight.finishFightIfOver(guild);

        assertTrue(bossFight.isPlayerWon());
        assertEquals(goldBefore + boss.getGoldDrop(), guild.getGold());
        assertEquals(
                Math.min(100, loyaltyBefore1 + boss.getLoyaltyEffectOnWin()),
                mainAdventurer.getLoyalty()
        );
        assertEquals(
                Math.min(100, loyaltyBefore2 + boss.getLoyaltyEffectOnWin()),
                weakAdventurer.getLoyalty()
        );
        assertEquals(
                Math.min(100, loyaltyBefore3 + boss.getLoyaltyEffectOnWin()),
                strongAdventurer.getLoyalty()
        );
    }

    @Test
    void finishFightIfOverShouldApplyLossPenalty() {
        mainAdventurer.setCurrentHealth(0);
        weakAdventurer.setCurrentHealth(0);
        strongAdventurer.setCurrentHealth(0);
        guild.removeDeadAdventurers();

        int goldBefore = guild.getGold();
        int loyaltyBefore1 = mainAdventurer.getLoyalty();
        int loyaltyBefore2 = weakAdventurer.getLoyalty();
        int loyaltyBefore3 = strongAdventurer.getLoyalty();

        bossFight.finishFightIfOver(guild);

        assertFalse(bossFight.isPlayerWon());
        assertEquals(goldBefore, guild.getGold());
        assertEquals(loyaltyBefore1, mainAdventurer.getLoyalty());
        assertEquals(loyaltyBefore2, weakAdventurer.getLoyalty());
        assertEquals(loyaltyBefore3, strongAdventurer.getLoyalty());
    }

    @Test
    void finishFightIfOverShouldNotApplyRewardsTwice() {
        boss.setCurrentHealth(0);

        int goldBefore = guild.getGold();

        bossFight.finishFightIfOver(guild);
        int goldAfterFirst = guild.getGold();

        bossFight.finishFightIfOver(guild);
        int goldAfterSecond = guild.getGold();

        assertEquals(goldBefore + boss.getGoldDrop(), goldAfterFirst);
        assertEquals(goldAfterFirst, goldAfterSecond);
    }

    @Test
    void finishFightIfOverShouldDoNothingWhenFightNotOver() {
        int goldBefore = guild.getGold();
        int loyaltyBefore = mainAdventurer.getLoyalty();

        bossFight.finishFightIfOver(guild);

        assertFalse(bossFight.isPlayerWon());
        assertEquals(goldBefore, guild.getGold());
        assertEquals(loyaltyBefore, mainAdventurer.getLoyalty());
    }

    private Boss createBoss() {
        return new Boss(
                "Dragon",
                50,
                12,
                3,
                20,
                5,
                -5,
                "Test boss"
        );
    }

    private Adventurer createMainAdventurer() {
        return new Adventurer(
                "MC",
                40,
                9,
                4,
                10,
                Faction.AATROX,
                Faction.AATROX,
                "Main adventurer"
        );
    }

    private Adventurer createWeakAdventurer() {
        Adventurer adventurer = new Adventurer(
                "Weak",
                30,
                8,
                2,
                10,
                Faction.XOLAANI,
                Faction.AATROX,
                "Low defense adventurer"
        );
        adventurer.adjustLoyalty(-100);
        adventurer.adjustLoyalty(20);
        return adventurer;
    }

    private Adventurer createStrongAdventurer() {
        Adventurer adventurer = new Adventurer(
                "Strong",
                35,
                10,
                5,
                10,
                Faction.AATROX,
                Faction.AATROX,
                "High loyalty adventurer"
        );
        adventurer.adjustLoyalty(100);
        return adventurer;
    }

    private Adventurer createMadAdventurer() {
        Adventurer adventurer = new Adventurer(
                "Mad",
                35,
                10,
                4,
                10,
                Faction.NEUTRAL,
                Faction.AATROX,
                "Mad adventurer"
        );
        adventurer.increaseMadness(80);
        return adventurer;
    }

    private Adventurer createVeryWeakAdventurer() {
        Adventurer adventurer = new Adventurer(
                "VeryWeak",
                20,
                2,
                1,
                10,
                Faction.XOLAANI,
                Faction.AATROX,
                "Very weak adventurer"
        );
        adventurer.adjustLoyalty(-100);
        adventurer.adjustLoyalty(20);
        adventurer.increaseMadness(80);
        return adventurer;
    }

    private Adventurer createTankAdventurer() {
        return new Adventurer(
                "Tank",
                60,
                6,
                50,
                10,
                Faction.NEUTRAL,
                Faction.AATROX,
                "Tank adventurer"
        );
    }
}