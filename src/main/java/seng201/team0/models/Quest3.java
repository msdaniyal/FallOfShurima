package seng201.team0.models;

import java.util.Arrays;
import java.util.List;

/**
 * Quest 3: Void Depths
 *
 * A four-boss survival gauntlet in the deepest part of the void tunnels.
 * Each boss introduces a unique mechanic — surviving all four is a significant milestone.
 *
 * Boss 1: Kha'Zix   — ISOLATE every 2 rounds (hunts the weakest alone).
 * Boss 2: Vel'Koz   — TRUE_DAMAGE passive (defense is ignored entirely).
 * Boss 3: Cho'Gath  — DEVOUR every 3 rounds (instantly kills lowest-HP member).
 * Boss 4: Bel'Veth  — AOE every round (hits every party member each turn).
 *
 * Events: Madness spikes rapidly in the depths. No choice — just survival.
 *
 * @author Mohammed, Xinyi
 */
public class Quest3 extends Quest {

    // ------------------------------------- CONSTRUCTORS -------------------------------------

    /**
     * Constructs Quest 3.
     * @param difficulty The game difficulty
     */
    public Quest3(Difficulty difficulty) {
        super(
                3,
                "Void Depths",
                "Deeper below, ancient predators awaken. These are not creatures that wound — " +
                        "they consume, isolate, and erase. Survival is no longer guaranteed.",
                20,
                difficulty
        );
    }

    // ------------------------------------- ABSTRACT METHOD IMPLEMENTATIONS -------------------------------------

    /**
     * Initialises the four-boss gauntlet.
     *
     * Kha'Zix (ISOLATE, freq=2):
     *   Every 2 rounds, isolates the lowest-HP adventurer.
     *   That adventurer fights alone for 2 rounds; others cannot attack.
     *   Isolation clears after 2 rounds or when the isolated member dies.
     *
     * Vel'Koz (TRUE_DAMAGE, freq=0):
     *   Passive — boss damage always ignores defense entirely.
     *   damage = bossAttack * diceDifference (no defense subtraction).
     *
     * Cho'Gath (DEVOUR, freq=3):
     *   Every 3 rounds, instantly kills the lowest-HP party member (sets HP to 0).
     *   Guild removes the dead member at end of round.
     *
     * Bel'Veth (AOE, freq=1):
     *   Every round, deals flat bossAttack damage to every living party member.
     *   Does not use dice rolls — pure pressure damage.
     *
     * @return Ordered list of boss fights
     */
    @Override
    protected List<BossFight> initialiseBossFights() {
        Boss khazix = new Boss(
                "Kha'Zix",
                130, 16, 8,
                150, 5, -5,
                "It has evolved to hunt the isolated. Separate from your group and you die alone.",
                BossAbility.ISOLATE,
                2
        );

        Boss velkoz = new Boss(
                "Vel'Koz",
                150, 20, 5,
                180, 6, -6,
                "It seeks to understand all life by tearing it apart. " +
                        "Armor means nothing to eyes that see through matter itself.",
                BossAbility.TRUE_DAMAGE,
                0
        );

        Boss chogath = new Boss(
                "Cho'Gath",
                200, 22, 12,
                250, 8, -10,
                "The Terror of the Void grows larger with every life it consumes. " +
                        "It will feast on the weakest among you first.",
                BossAbility.DEVOUR,
                3
        );

        Boss belveth = new Boss(
                "Bel'Veth",
                220, 18, 10,
                300, 10, -12,
                "The Empress of the Void does not fight — she drowns. " +
                        "There is no single blow to block when everything strikes at once.",
                BossAbility.AOE,
                1
        );

        return Arrays.asList(
                new BossFight(khazix,  1, getDifficulty()),
                new BossFight(velkoz,  2, getDifficulty()),
                new BossFight(chogath, 3, getDifficulty()),
                new BossFight(belveth, 4, getDifficulty())
        );
    }

    /**
     * Runs expedition events for Quest 3.
     * The depths inflict madness on the entire party — no choices, just consequences.
     * @param guild The player's guild
     */
    @Override
    public void runEvents(Guild guild) {
        for (Adventurer member : guild.getMainParty()) {
            member.increaseMadness(10);
        }
    }
}