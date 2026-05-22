package seng201.team76.unittests.models;

import org.junit.jupiter.api.Test;
import seng201.team76.models.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests important BossFight model rules and special boss mechanics.
 */
public class BossFightTest {

    private Adventurer companion(String name, int maxHp, int currentHp, int attack, int defense) {
        Adventurer adventurer = new Adventurer(name, maxHp, attack, defense,
                10, Faction.NEUTRAL, Faction.AATROX, "Test companion");
        adventurer.setCurrentHealth(currentHp);
        return adventurer;
    }

    private Guild guildWith(Adventurer... companions) {
        Guild guild = new Guild("Test Guild", 500, Faction.AATROX);
        for (Adventurer companion : companions) {
            guild.addToMainParty(companion);
        }
        return guild;
    }

    private Boss boss(String name, int attack, int defense, BossAbility ability, int frequency) {
        return new Boss(name, 200, attack, defense,
                50, 5, -5, "Test boss", ability, frequency);
    }

    @Test
    void successfulPlayerAttackDamagesBossAndReturnsAttackResult() {
        Adventurer attacker = companion("Varus", 100, 100, 30, 5);
        Guild guild = guildWith(attacker);
        BossFight fight = new BossFight(boss("Training Boss", 10, 0, BossAbility.NONE, 0), 1, Difficulty.NORMAL);
        int before = fight.getBoss().getCurrentHealth();

        BossFight.AttackResult result = fight.resolvePlayerAttack(attacker, true, false, guild);

        assertTrue(result.isSuccess());
        assertFalse(result.isBlocked());
        assertTrue(result.getDamage() > 0);
        assertEquals(before - result.getDamage(), fight.getBoss().getCurrentHealth());
    }

    @Test
    void failedMainCharacterAttackPenalisesWholePartyLoyalty() {
        Adventurer companion = companion("Varus", 100, 100, 20, 5);
        Guild guild = guildWith(companion);
        Adventurer main = guild.getMainCharacter();
        int mainLoyaltyBefore = main.getLoyalty();
        int companionLoyaltyBefore = companion.getLoyalty();
        BossFight fight = new BossFight(boss("Training Boss", 10, 0, BossAbility.NONE, 0), 1, Difficulty.NORMAL);

        BossFight.AttackResult result = fight.resolvePlayerAttack(main, false, true, guild);

        assertFalse(result.isSuccess());
        assertEquals(0, result.getDamage());
        assertEquals(mainLoyaltyBefore - 5, main.getLoyalty());
        assertEquals(companionLoyaltyBefore - 5, companion.getLoyalty());
    }

    @Test
    void jaxImmuneRoundBlocksDamage() {
        Adventurer attacker = companion("Varus", 100, 100, 50, 5);
        Guild guild = guildWith(attacker);
        Boss jax = boss("Jax", 10, 0, BossAbility.IMMUNE_TURN, 1);
        BossFight fight = new BossFight(jax, 1, Difficulty.NORMAL);
        int before = jax.getCurrentHealth();

        BossFight.AttackResult result = fight.resolvePlayerAttack(attacker, true, false, guild);

        assertTrue(result.isSuccess());
        assertTrue(result.isBlocked());
        assertEquals(0, result.getDamage());
        assertEquals(before, jax.getCurrentHealth());
    }

    @Test
    void khazixIsolationCreatesOneVersusOneAndDisablesChangingMembers() {
        Adventurer weak = companion("Weak", 100, 20, 12, 5);
        Adventurer healthy = companion("Healthy", 100, 90, 12, 5);
        Guild guild = guildWith(weak, healthy);
        BossFight fight = new BossFight(boss("Kha'Zix", 10, 0, BossAbility.ISOLATE, 1), 1, Difficulty.NORMAL);

        fight.triggerBossAbilityIfNeeded(guild);

        assertTrue(fight.isIsolationActive());
        assertEquals(weak, fight.getIsolatedTarget());
        assertFalse(fight.canChangeMember());
        assertTrue(fight.canAdventurerAct(weak));
        assertFalse(fight.canAdventurerAct(healthy));
    }

    @Test
    void khazixIsolationEndsWhenIsolatedCharacterDies() {
        Adventurer weak = companion("Weak", 100, 20, 12, 5);
        Guild guild = guildWith(weak);
        BossFight fight = new BossFight(boss("Kha'Zix", 10, 0, BossAbility.ISOLATE, 1), 1, Difficulty.NORMAL);
        fight.triggerBossAbilityIfNeeded(guild);

        weak.setCurrentHealth(0);
        fight.nextRound();

        assertFalse(fight.isIsolationActive());
        assertTrue(fight.canChangeMember());
    }

    @Test
    void khazixDealsExtraDamageToIsolatedTarget() {
        Adventurer isolated = companion("Isolated", 100, 100, 12, 100);
        Guild guild = guildWith(isolated);
        BossFight fight = new BossFight(boss("Kha'Zix", 10, 0, BossAbility.ISOLATE, 1), 1, Difficulty.NORMAL);
        fight.triggerBossAbilityIfNeeded(guild);

        BossFight.DefenseResult result = fight.resolveDefend(isolated, 1, 2, guild);

        assertFalse(result.isDodged());
        assertTrue(result.getDamage() >= 6,
                "Without isolation this high-defence target would take 1 damage; isolation should increase it.");
    }

    @Test
    void chogathDoesNotDevourWhenTargetAboveNormalThreshold() {
        Adventurer target = companion("Target", 100, 51, 12, 5);
        Guild guild = guildWith(target);
        BossFight fight = new BossFight(boss("Cho'Gath", 10, 0, BossAbility.DEVOUR, 1), 1, Difficulty.NORMAL);

        String message = fight.triggerBossAbilityIfNeeded(guild);

        assertFalse(target.isDead());
        assertNotNull(message);
        assertTrue(message.contains("no one is weak enough"));
    }

    @Test
    void chogathDevoursWhenTargetAtNormalThreshold() {
        Adventurer target = companion("Target", 100, 50, 12, 5);
        Guild guild = guildWith(target);
        BossFight fight = new BossFight(boss("Cho'Gath", 10, 0, BossAbility.DEVOUR, 1), 1, Difficulty.NORMAL);

        fight.triggerBossAbilityIfNeeded(guild);

        assertTrue(target.isDead());
        assertTrue(guild.isPermanentlyUnavailable(target));
        assertFalse(guild.getMainParty().contains(target));
    }

    @Test
    void belvethIgnoresDefenceWhenDealingDamage() {
        Adventurer tank = companion("Tank", 100, 100, 12, 100);
        Guild guild = guildWith(tank);
        Boss belveth = boss("Bel'Veth", 10, 0, BossAbility.NONE, 0);
        BossFight fight = new BossFight(belveth, 1, Difficulty.NORMAL);

        BossFight.DefenseResult result = fight.resolveDefend(tank, 1, 2, guild);

        assertEquals(10, result.getDamage(),
                "Bel'Veth should deal attack * dice difference and ignore the target's defence.");
    }

    @Test
    void zoeSleepStopsCharacterActingAndKillsThemNextRound() {
        Adventurer target = companion("Sleepy", 100, 100, 12, 5);
        Adventurer other = companion("Other", 100, 100, 12, 5);
        Guild guild = guildWith(target, other);
        BossFight fight = new BossFight(boss("Zoe", 10, 0, BossAbility.SLEEP, 1), 1, Difficulty.NORMAL);

        fight.triggerBossAbilityIfNeeded(guild);

        assertEquals(target, fight.getSleepingTarget());
        assertFalse(fight.canAdventurerAct(target));
        assertTrue(fight.canAdventurerAct(other));
        assertEquals(2, fight.getSleepExecutionRound());

        fight.nextRound();
        String message = fight.triggerBossAbilityIfNeeded(guild);

        assertTrue(target.isDead());
        assertFalse(guild.getMainParty().contains(target));
        assertTrue(guild.isPermanentlyUnavailable(target));
        assertNull(fight.getSleepingTarget());
        assertNotNull(message);
        assertTrue(message.contains("falls from full strength to nothing"));
    }

    @Test
    void potionCanOnlyBeUsedOncePerTurnAndResetsNextRound() {
        Adventurer injured = companion("Injured", 100, 40, 12, 5);
        Guild guild = guildWith(injured);
        guild.addSmallPotions(2);
        BossFight fight = new BossFight(boss("Training Boss", 10, 0, BossAbility.NONE, 0), 1, Difficulty.NORMAL);

        BossFight.PotionUseResult first = fight.usePotionThisTurn(guild, ItemType.SINGLE, injured);
        BossFight.PotionUseResult second = fight.usePotionThisTurn(guild, ItemType.SINGLE, injured);
        fight.nextRound();
        BossFight.PotionUseResult third = fight.usePotionThisTurn(guild, ItemType.SINGLE, injured);

        assertTrue(first.isSuccess());
        assertFalse(second.isSuccess());
        assertEquals("You already used a potion this turn.", second.getMessage());
        assertTrue(third.isSuccess());
    }
}
