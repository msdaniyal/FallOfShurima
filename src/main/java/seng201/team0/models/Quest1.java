package seng201.team0.models;

import java.util.Arrays;
import java.util.List;

/**
 * Quest 1: Icathia
 * First major expedition into the ruined city-state.
 *
 * Mini-Boss: Zilean — standard fight, no special ability.
 * Main Boss:  Jax   — IMMUNE_TURN every 2 rounds (attacks ignored that round).
 *
 * Events: Short morale sequence rewarding the party for surviving the ruins.
 *
 * @author Mohammed, Xinyi
 */
public class Quest1 extends Quest {

    // ------------------------------------- CONSTRUCTORS -------------------------------------

    /**
     * Constructs Quest 1.
     */
    public Quest1(Difficulty difficulty) {
        super(
                1,
                "Icathia",
                "The ruins of Icathia whisper with madness. Zilean watches time fracture " +
                        "while Jax waits at the heart of the collapse. Few who enter return unchanged.",
                10,
                difficulty
        );
    }

    // ------------------------------------- ABSTRACT METHOD IMPLEMENTATIONS -------------------------------------

    /**
     * Initialises boss fights for Quest 1.
     *
     * Fight 1 — Zilean (mini-boss): No ability. Warm-up fight.
     * Fight 2 — Jax (main boss):    IMMUNE_TURN every 2 rounds.
     *           On immune rounds all adventurer attacks deal 0 damage regardless
     *           of memory sequence outcome. BossFightController reads isImmuneThisRound().
     *
     * @return Ordered list of boss fights
     */
    @Override
    protected List<BossFight> initialiseBossFights() {
        Boss zilean = new Boss(
                "Zilean",
                80, 12, 6,
                100, 5, -5,
                "The Chronokeeper bends fate itself, rewriting the moments before death.",
                BossAbility.NONE,
                0
        );

        Boss jax = new Boss(
                "Jax",
                140, 18, 10,
                250, 10, -10,
                "The Grandmaster at Arms needs no weapon to prove his worth. He fights alone " +
                        "and counters every assault, waiting for the moment your strikes falter.",
                BossAbility.IMMUNE_TURN,
                2
        );

        return Arrays.asList(
                new BossFight(zilean, 1, getDifficulty()),
                new BossFight(jax,    2, getDifficulty())
        );
    }

    /**
     * Runs expedition events for Quest 1.
     *
     * Surviving Icathia is a small victory. All party members gain loyalty from shared danger.
     * The guild also recovers some gold from scavenged ruins.
     *
     * @param guild The player's guild
     */
    @Override
    public void runEvents(Guild guild) {
        // All party members gain loyalty — shared survival creates bonds
        for (Adventurer member : guild.getMainParty()) {
            member.adjustLoyalty(5);
        }
        // Scavenged relics sell for a modest sum
        guild.addGold(30);
    }
}