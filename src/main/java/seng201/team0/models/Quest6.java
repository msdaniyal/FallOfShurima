package seng201.team0.models;

import java.util.Arrays;
import java.util.List;

/**
 * Quest 6: Twilight of Sleep
 *
 * The final encounter. Only guilds who failed the loyalty threshold reach this quest.
 * Zoe is the consequence of fracture — she appears when the party is already broken.
 *
 * Boss: Zoe — SLEEP every 2 rounds.
 *   Puts the lowest-HP adventurer to sleep.
 *   The sleeping adventurer skips their attack turn.
 *   The next boss hit on the sleeping target deals 10x damage.
 *   Sleep is consumed after the 10x hit lands.
 *
 * Events: Reality bends. Madness peaks. There is no gold here — only consequences.
 *
 * @author Mohammed, Xinyi
 */
public class Quest6 extends Quest {

    private boolean realityDistortionApplied;

    // ------------------------------------- CONSTRUCTORS -------------------------------------

    /**
     * Constructs Quest 6.
     * @param difficulty The game difficulty
     */
    public Quest6(Difficulty difficulty) {
        super(
                6,
                "Twilight of Sleep",
                "Reality bends like a half-remembered dream. Somewhere above the stars, " +
                        "Zoe laughs. One nap is all it takes to never wake again.",
                25,
                difficulty
        );
        this.realityDistortionApplied = false;
    }

    // ------------------------------------- ABSTRACT METHOD IMPLEMENTATIONS -------------------------------------

    /**
     * Initialises the Zoe boss fight.
     *
     * Zoe — SLEEP (abilityFrequency=2):
     *   Every 2 rounds:
     *     1. BossFight.applyBossAbility() marks the lowest-HP party member as sleepingTarget.
     *     2. That adventurer skips their attack turn (BossFightController skips them in the loop).
     *     3. The next calcBossDamage() call targeting the sleeping member multiplies damage x10.
     *     4. Sleep is cleared after the 10x hit lands.
     *
     *   NOTE: Sleep and the 10x hit are NOT the same round. Sleep is set on round N,
     *   and the 10x damage fires on whichever round the boss next attacks that target.
     *
     * @return Ordered list of boss fights
     */
    @Override
    protected List<BossFight> initialiseBossFights() {
        Boss zoe = new Boss(
                "Zoe",
                260, 22, 11,
                500, 15, -20,
                "The Aspect of Twilight does not fight with anger. She fights with curiosity. " +
                        "She puts you to sleep because she wants to see what happens next.",
                BossAbility.SLEEP,
                2
        );

        return Arrays.asList(new BossFight(zoe, 1, getDifficulty()));
    }

    /**
     * Runs final expedition events.
     * No gold rewards — this is a punishment quest.
     * Madness spikes sharply as reality distorts.
     * @param guild The player's guild
     */
    @Override
    public void runEvents(Guild guild) {
        if (realityDistortionApplied) {
            return;
        }

        realityDistortionApplied = true;
        for (Adventurer member : guild.getMainParty()) {
            member.increaseMadness(15);
        }
    }

    public String getFinalIntroText() {
        return "Zoe appears only when the party is too fractured for the true ending. Win here, or lose everything.";
    }
}