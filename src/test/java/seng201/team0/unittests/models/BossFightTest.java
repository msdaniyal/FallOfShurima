package seng201.team0.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for BossFight
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
        // ====== 这里按你的实际构造器改 ======
        boss = createBoss();
        mainAdventurer = createMainAdventurer();
        weakAdventurer = createWeakAdventurer();
        strongAdventurer = createStrongAdventurer();
        guild = createGuild(mainAdventurer, weakAdventurer, strongAdventurer);
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
        // attack = 10, loyalty > 70 gives +2, madness <= 75 gives no penalty
        // boss defense = 3
        // expected = 10 + 2 - 3 = 9
        int damage = bossFight.calcAdventurerDamage(strongAdventurer);
        assertEquals(9, damage);
    }

    @Test
    void calcAdventurerDamageShouldApplyLowLoyaltyPenalty() {
        // attack = 8, loyalty < 30 gives -2, madness <= 75 no penalty
        // boss defense = 3
        // expected = 8 - 2 - 3 = 3
        int damage = bossFight.calcAdventurerDamage(weakAdventurer);
        assertEquals(3, damage);
    }

    @Test
    void calcAdventurerDamageShouldApplyMadnessPenalty() {
        Adventurer crazy = createMadAdventurer();
        // attack = 10, loyalty normal no bonus/penalty, madness > 75 gives -2
        // boss defense = 3
        // expected = 10 - 2 - 3 = 5
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
        // boss attack = 12, strongAdventurer defense = 5
        // expected = 7
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
        assertEquals(weakHealthBefore - bossFight.calcBossDamage(weakAdventurer), weakAdventurer.getCurrentHealth());
        assertFalse(bossFight.isMcBlocking());
    }

    @Test
    void bossTurnShouldAttackMainAdventurerWhenBlocking() {
        int mainHealthBefore = mainAdventurer.getCurrentHealth();

        bossFight.setMcBlocking(true);
        Adventurer attacked = bossFight.bossTurn(guild);

        assertEquals(mainAdventurer, attacked);
        assertEquals(mainHealthBefore - bossFight.calcBossDamage(mainAdventurer), mainAdventurer.getCurrentHealth());
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
        for (Adventurer adventurer : guild.getMainParty()) {
            adventurer.setCurrentHealth(0);
        }
        bossFight.setMcBlocking(true);

        Adventurer attacked = bossFight.bossTurn(guild);

        assertNull(attacked);
        assertFalse(bossFight.isMcBlocking());
    }

    @Test
    void isFightOverShouldReturnTrueWhenBossIsDead() {
        boss.setCurrentHealth(0);
        assertTrue(bossFight.isFightOver(guild));
    }

    @Test
    void isFightOverShouldReturnTrueWhenGuildIsWiped() {
        for (Adventurer adventurer : guild.getMainParty()) {
            adventurer.setCurrentHealth(0);
        }
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
        assertEquals(loyaltyBefore1 + boss.getLoyaltyEffectOnWin(), mainAdventurer.getLoyalty());
        assertEquals(loyaltyBefore2 + boss.getLoyaltyEffectOnWin(), weakAdventurer.getLoyalty());
        assertEquals(loyaltyBefore3 + boss.getLoyaltyEffectOnWin(), strongAdventurer.getLoyalty());
    }

    @Test
    void finishFightIfOverShouldApplyLossPenalty() {
        for (Adventurer adventurer : guild.getMainParty()) {
            adventurer.setCurrentHealth(0);
        }

        int goldBefore = guild.getGold();
        int loyaltyBefore1 = mainAdventurer.getLoyalty();
        int loyaltyBefore2 = weakAdventurer.getLoyalty();
        int loyaltyBefore3 = strongAdventurer.getLoyalty();

        bossFight.finishFightIfOver(guild);

        assertFalse(bossFight.isPlayerWon());
        assertEquals(goldBefore, guild.getGold());
        assertEquals(loyaltyBefore1 + boss.getLoyaltyEffectOnLoss(), mainAdventurer.getLoyalty());
        assertEquals(loyaltyBefore2 + boss.getLoyaltyEffectOnLoss(), weakAdventurer.getLoyalty());
        assertEquals(loyaltyBefore3 + boss.getLoyaltyEffectOnLoss(), strongAdventurer.getLoyalty());
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

    // =========================================================
    // Helper methods
    // 把下面这些构造器替换成你项目里真实的构造器
    // =========================================================

    private Boss createBoss() {
        Boss boss = new Boss("Dragon", 12, 3, 50, 100, 20, -10);
        boss.setCurrentHealth(50);
        return boss;
    }

    private Adventurer createMainAdventurer() {
        Adventurer adventurer = new Adventurer("MC", 9, 4, 40, 20, 100);
        adventurer.setCurrentHealth(40);
        return adventurer;
    }

    private Adventurer createWeakAdventurer() {
        Adventurer adventurer = new Adventurer("Weak", 8, 2, 20, 20, 100);
        adventurer.setCurrentHealth(30);
        return adventurer;
    }

    private Adventurer createStrongAdventurer() {
        Adventurer adventurer = new Adventurer("Strong", 10, 5, 80, 20, 100);
        adventurer.setCurrentHealth(35);
        return adventurer;
    }

    private Adventurer createMadAdventurer() {
        Adventurer adventurer = new Adventurer("Mad", 10, 4, 50, 80, 100);
        adventurer.setCurrentHealth(35);
        return adventurer;
    }

    private Adventurer createVeryWeakAdventurer() {
        Adventurer adventurer = new Adventurer("VeryWeak", 2, 1, 20, 90, 100);
        adventurer.setCurrentHealth(20);
        return adventurer;
    }

    private Adventurer createTankAdventurer() {
        Adventurer adventurer = new Adventurer("Tank", 6, 50, 50, 10, 100);
        adventurer.setCurrentHealth(60);
        return adventurer;
    }

}