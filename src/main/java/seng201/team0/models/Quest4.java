package seng201.team0.models;

import java.util.Arrays;
import java.util.List;

/**
 * Quest 4: Crimson Court
 *
 * A noble mansion untouched by the chaos outside.
 * Inside, Vladimir waits — unhurried, smiling, drinking.
 *
 * Boss: Vladimir — HEAL_ON_HIT passive.
 *   After dealing damage to an adventurer, Vladimir heals himself by the same amount.
 *   The fight is a war of attrition: raw damage output matters less than preventing hits.
 *   BossFightController should call applyVladimirHeal(damage) after each boss hit lands.
 *
 * Events: The mansion rewards careful exploration — finding the treasury before
 *   engaging Vladimir gives the guild a significant gold bonus.
 *
 * @author Mohammed, Xinyi
 */
public class Quest4 extends Quest {

    // ------------------------------------- CONSTRUCTORS -------------------------------------

    /**
     * Constructs Quest 4.
     * @param difficulty The game difficulty
     */
    public Quest4(Difficulty difficulty) {
        super(
                4,
                "Crimson Court",
                "A noble mansion stands untouched by time and war. Candles burn without wax. " +
                        "Portraits smile with too many teeth. Vladimir waits in the dining hall.",
                18,
                difficulty
        );
    }

    // ------------------------------------- ABSTRACT METHOD IMPLEMENTATIONS -------------------------------------

    /**
     * Initialises the Vladimir boss fight.
     *
     * Vladimir — HEAL_ON_HIT (passive, abilityFrequency=0 since shouldTriggerAbility returns
     * true every round for passive abilities):
     *   Every time the boss deals damage > 0, he heals by that amount (capped at max HP).
     *   BossFightController must call bossFight.applyVladimirHeal(damage) immediately after
     *   each non-zero boss damage application.
     *
     * @return Ordered list of boss fights
     */
    @Override
    protected List<BossFight> initialiseBossFights() {
        Boss vladimir = new Boss(
                "Vladimir",
                200, 20, 9,
                300, 10, -10,
                "Every wound you give him fills the chalice he drinks from. " +
                        "He has outlived empires by turning pain into sustenance.",
                BossAbility.HEAL_ON_HIT,
                0
        );

        return Arrays.asList(new BossFight(vladimir, 1, getDifficulty()));
    }

    /**
     * Runs expedition events for Quest 4.
     * The guild finds and raids the mansion treasury before the fight.
     * Gold reward is substantial — the Crimson Court was wealthy.
     * @param guild The player's guild
     */
    @Override
    public void runEvents(Guild guild) {
        // Treasury looted before Vladimir notices
        guild.addGold(75);
        // A moment of dark opulence — minor loyalty boost from shared wealth
        for (Adventurer member : guild.getMainParty()) {
            member.adjustLoyalty(5);
        }
    }
}