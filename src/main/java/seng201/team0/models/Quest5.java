package seng201.team0.models;

import java.util.Arrays;
import java.util.List;

/**
 * Quest 5: Faction War
 *
 * The political fracture arrives. Guild meets guild on open ground.
 * The enemy party is scaled to match the player's party size —
 * represented here as a single Enemy Captain with boosted stats.
 *
 * Boss: Enemy Captain — NONE ability (straight combat, no gimmicks).
 *   The fight is meant to feel like a pure test of what the player has built.
 *
 * After the quest:
 *   - guild.collapseOpposingFaction() is called by updateCharacters
 *     (adventurers from the opposing faction abandon the guild).
 *   - guild.lockParty() is called — no further recruitment or roster changes.
 *
 * Events: Victory in the war briefly unifies the remaining party.
 *
 * @author Mohammed, Xinyi
 */
public class Quest5 extends Quest {

    // ------------------------------------- CONSTRUCTORS -------------------------------------

    /**
     * Constructs Quest 5.
     * @param difficulty The game difficulty
     */
    public Quest5(Difficulty difficulty) {
        super(
                5,
                "Faction War",
                "The final political fracture arrives. The rival faction has declared war. " +
                        "Guild faces guild on open ground — and only one walks away.",
                15,
                difficulty
        );
    }

    // ------------------------------------- ABSTRACT METHOD IMPLEMENTATIONS -------------------------------------

    /**
     * Initialises the enemy captain boss fight.
     *
     * Enemy Captain represents the rival guild as a single high-stat opponent.
     * No special ability — this fight is a straight power check.
     * High gold drop and loyalty effects reflect the scale of the conflict.
     *
     * TODO: In a future iteration, scale enemy captain stats to match
     *       the player's average party attack/defense for a true mirror fight.
     *
     * @return Ordered list of boss fights
     */
    @Override
    protected List<BossFight> initialiseBossFights() {
        Boss enemyCaptain = new Boss(
                "Enemy Captain",
                250, 24, 12,
                400, 12, -15,
                "The rival captain built their guild the same way you did. " +
                        "One of you made better choices.",
                BossAbility.NONE,
                0
        );

        return Arrays.asList(new BossFight(enemyCaptain, 1, getDifficulty()));
    }

    /**
     * Runs expedition events for Quest 5.
     * Victory in the war unifies whoever is still standing — loyalty boost across the board.
     * @param guild The player's guild
     */
    @Override
    public void runEvents(Guild guild) {
        for (Adventurer member : guild.getMainParty()) {
            member.adjustLoyalty(10);
        }
    }

    /**
     * Override updateCharacters to handle Quest 5 post-quest special logic.
     * Collapses opposing faction loyalty, then locks the party.
     * Then runs standard madness affliction and abandoned cleanup.
     * @param guild The player's guild
     */
    @Override
    public void updateCharacters(Guild guild) {
        // Opposing faction members abandon the guild after the war
        guild.collapseOpposingFaction();

        // Party is now locked — no further changes allowed
        guild.lockParty();

        // Apply standard madness and remove any zero-loyalty adventurers
        super.updateCharacters(guild);
    }
}