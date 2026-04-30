package seng201.team0.models;

import java.util.Arrays;
import java.util.List;

/**
 * Quest 2: Into the Void
 * The guild descends into the tunnels beneath Icathia.
 * Paranoia and distrust spread through the party as void corruption takes hold.
 * Every event tests whether the guild holds together or fractures under pressure.
 * Boss: Voidlings (three creatures, AOE attack every 3 rounds).
 * Events (5 total, each with 2 meaningful choices):
 *   1. The Sealed Tunnel      — safety vs speed
 *   2. A Lost Companion       — rescue vs survival
 *   3. The Corrupted Member   — compassion vs cold logic
 *   4. The Supply Cache       — share vs hoard
 *   5. The Scout's Warning    — pride vs wisdom
 * Each event method takes a boolean playerChoice:
 *   true  = Option A (the bolder / more loyal choice)
 *   false = Option B (the cautious / self-preserving choice)
  * TODO: Wire each eventN(guild, playerChoice) call in runEvents() to
 *       ExpeditionController so the player's UI selection drives the boolean.
 *
 * @author Mohammed, Xinyi
 */
public class Quest2 extends Quest {

    // ------------------------------------- CONSTRUCTORS -------------------------------------

    /**
     * Constructs Quest 2.
     * @param difficulty The game difficulty
     */
    public Quest2(Difficulty difficulty) {
        super(
                2,
                "Into the Void",
                "The tunnels below breathe with an alien pulse. Something watches from the dark " +
                        "and your party begins to distrust each other. Every shadow hides a choice.",
                15,
                difficulty
        );
    }

    // ------------------------------------- BOSS FIGHTS -------------------------------------

    /**
     * Initialises boss fight for Quest 2.
     * Three Voidlings acting as one unit — AOE damage every 3 rounds.
     * @return Ordered list of boss fights
     */
    @Override
    protected List<BossFight> initialiseBossFights() {
        Boss voidlings = new Boss(
                "Voidlings",
                120, 14, 5,
                150, 8, -8,
                "Three hungry voidlings pour from the cracks in the stone, " +
                        "clawing at everything that breathes.",
                BossAbility.AOE,
                3
        );

        return Arrays.asList(new BossFight(voidlings, 1, getDifficulty()));
    }

    // ------------------------------------- EVENTS -------------------------------------

    /**
     * Event 1 — The Sealed Tunnel.

     * A faster route deeper into the void is found — but it is sealed with runes
     * that pulse with unstable void energy. Breaking through saves time and uncovers
     * a small cache on the other side, but the energy bleeds into the party's minds.
     * The long way around is safe but costs supplies and grinds morale down.
     *
     * Option A — "Break the seal" (bold):
     *   Guild gains gold (+60) from the cache on the other side.
     *   All party members gain madness (+15) from void energy exposure.
     *
     * Option B — "Take the long route" (cautious):
     *   Guild spends gold (-30) on extra supplies for the longer path.
     *   All party members lose loyalty (-5) from the delay and frustration.
     *   No madness gained.
     *
     * TODO: Display in ExpeditionController:
     *   Prompt:  "A sealed tunnel offers a faster route deeper into the void."
     *   Label A: "Break the seal"
     *   Label B: "Take the long route"
     *
     * @param guild        The player's guild
     * @param playerChoice true = Option A (break the seal), false = Option B (long route)
     */
    public void event1SealedTunnel(Guild guild, boolean playerChoice) {
        List<Adventurer> party = guild.getMainParty();

        if (playerChoice) {
            guild.addGold(60);
            for (Adventurer member : party) {
                member.increaseMadness(15);
            }
        } else {
            guild.spendGold(30);
            for (Adventurer member : party) {
                member.adjustLoyalty(-5);
            }
        }
    }

    /**
     * Event 2 — A Lost Companion.
     *
     * A scout fails to return from a side passage. Distant sounds suggest they are
     * still alive — trapped, not dead. Going back risks the whole party; leaving them
     * means abandoning one of your own in the void.
     *
     * Option A — "Go back for them" (loyal):
     *   Guild spends gold (-40) on torches and rope to navigate the passage safely.
     *   All party members gain loyalty (+10) — nobody was left behind.
     *   All party members gain madness (+5) from the stress of the rescue.
     *
     * Option B — "Press on" (pragmatic):
     *   The least loyal adventurer in the party loses loyalty (-25) —
     *   whoever was already wavering loses faith in the guild's values.
     *   All other party members lose loyalty (-8) from guilt and unease.
     *
     * TODO: Display in ExpeditionController:
     *   Prompt:  "Your scout hasn't returned. Sounds echo from the side passage — they're alive."
     *   Label A: "Go back for them"
     *   Label B: "Press on — we can't risk everyone"
     *
     * @param guild        The player's guild
     * @param playerChoice true = Option A (rescue), false = Option B (press on)
     */
    public void event2LostCompanion(Guild guild, boolean playerChoice) {
        List<Adventurer> party = guild.getMainParty();

        if (playerChoice) {
            guild.spendGold(40);
            for (Adventurer member : party) {
                member.adjustLoyalty(10);
                member.increaseMadness(5);
            }
        } else {
            Adventurer leastLoyal = findLeastLoyal(party);
            if (leastLoyal != null) {
                leastLoyal.adjustLoyalty(-25);
            }
            for (Adventurer member : party) {
                if (!member.equals(leastLoyal)) {
                    member.adjustLoyalty(-8);
                }
            }
        }
    }

    /**
     * Event 3 — The Corrupted Member.
     *
     * Void spores have gotten into one adventurer's lungs and mind. They are erratic —
     * muttering to something unseen, lashing out, making wrong calls.
     * The party must decide whether to carry them forward or cut them loose.
     * The afflicted member is whichever adventurer currently has the highest madness.
     *
     * Option A — "Restrain and carry them" (compassionate):
     *   The afflicted member resents the restraint — loyalty -15.
     *   The rest of the party is slowed and unsettled — loyalty -5, madness +10 each.
     *
     * Option B — "Leave them behind" (ruthless):
     *   The afflicted member's loyalty collapses to 0 — abandoned flag is set and they
     *   are removed from the guild via guild.removeAbandoned().
     *   Guild recovers gold (+20) from supplies the member was carrying.
     *   Remaining party members gain loyalty (+5) but madness (+5) from the decision.
     *
     * TODO: Display in ExpeditionController:
     *   Prompt:  "[afflicted.getName()] is losing their mind to the void. They're a danger."
     *   Label A: "Restrain them and carry them out"
     *   Label B: "Leave them — we can't afford this"
     *
     * @param guild        The player's guild
     * @param playerChoice true = Option A (restrain), false = Option B (abandon)
     */
    public void event3CorruptedMember(Guild guild, boolean playerChoice) {
        List<Adventurer> party = guild.getMainParty();
        Adventurer afflicted = findMostMad(party);

        if (playerChoice) {
            if (afflicted != null) {
                afflicted.adjustLoyalty(-15);
            }
            for (Adventurer member : party) {
                if (!member.equals(afflicted)) {
                    member.adjustLoyalty(-5);
                    member.increaseMadness(10);
                }
            }
        } else {
            if (afflicted != null) {
                afflicted.adjustLoyalty(-100); // drives loyalty to 0, sets abandoned flag
            }
            guild.removeAbandoned();
            guild.addGold(20);

            for (Adventurer member : guild.getMainParty()) {
                member.adjustLoyalty(5);
                member.increaseMadness(5);
            }
        }
    }

    /**
     * Event 4 — The Supply Cache.
     *
     * The party finds an untouched cache — food, torches, rope, coin.
     * There is enough to either share generously and boost morale,
     * or ration carefully and pocket the surplus for later.
     *
     * Option A — "Share everything equally" (generous):
     *   All party members gain loyalty (+12) from the gesture of trust.
     *   The paranoia of the void eases briefly — all members gain a small health restore
     *   (+15 HP each, capped at max) as a full meal settles the nerves.
     *
     * Option B — "Ration it strictly, sell the surplus" (pragmatic):
     *   Guild gains gold (+50) from the surplus.
     *   All party members lose loyalty (-8) — they notice the stinginess and resent it.
     *
     * TODO: Display in ExpeditionController:
     *   Prompt:  "A supply cache — food, torches, equipment. More than you need right now."
     *   Label A: "Share everything — the party needs this"
     *   Label B: "Ration carefully and sell the surplus"
     *
     * @param guild        The player's guild
     * @param playerChoice true = Option A (share), false = Option B (ration)
     */
    public void event4SupplyCache(Guild guild, boolean playerChoice) {
        List<Adventurer> party = guild.getMainParty();

        if (playerChoice) {
            for (Adventurer member : party) {
                member.adjustLoyalty(12);
                member.setCurrentHealth(member.getCurrentHealth() + 15);
            }
        } else {
            guild.addGold(50);
            for (Adventurer member : party) {
                member.adjustLoyalty(-8);
            }
        }
    }

    /**
     * Event 5 — The Scout's Warning.
     *
     * A forward scout returns breathless — the Voidlings ahead are more numerous than
     * expected and the tunnel narrows, breaking formation. Push through while adrenaline
     * is high, or fall back and regroup at the cost of time and morale.
     *
     * Option A — "Push through now" (courageous):
     *   The chaos of the narrow tunnel spikes madness (+15) across the party.
     *   Fighting through together steels resolve — loyalty +8 each.
     *   A reward is found in the Voidlings' nest — guild gains gold (+40).
     *
     * Option B — "Fall back and regroup" (cautious):
     *   Guild spends gold (-20) on extra preparation during the delay.
     *   All party members lose loyalty (-10) — the hesitation demoralises the group.
     *   No madness gained.
     *
     * TODO: Display in ExpeditionController:
     *   Prompt:  "The scout reports: more voidlings than expected. The tunnel narrows ahead."
     *   Label A: "Push through — hit them before they're ready"
     *   Label B: "Fall back and regroup — don't walk into a trap"
     *
     * @param guild        The player's guild
     * @param playerChoice true = Option A (push through), false = Option B (fall back)
     */
    public void event5ScoutsWarning(Guild guild, boolean playerChoice) {
        List<Adventurer> party = guild.getMainParty();

        if (playerChoice) {
            guild.addGold(40);
            for (Adventurer member : party) {
                member.increaseMadness(15);
                member.adjustLoyalty(8);
            }
        } else {
            guild.spendGold(20);
            for (Adventurer member : party) {
                member.adjustLoyalty(-10);
            }
        }
    }

    // ------------------------------------- RUN EVENTS -------------------------------------

    /**
     * Runs all five expedition events in sequence.
     *
     * Each event is driven by a boolean playerChoice.
     * Currently all default to true (Option A) as a placeholder.
     *
     * TODO: Replace each hardcoded `true` with the result of a controller call, e.g.:
     *   boolean choice = expeditionController.promptChoice(title, labelA, labelB);
     *
     * Event order:
     *   1. The Sealed Tunnel      (before the descent)
     *   2. A Lost Companion       (mid-tunnel)
     *   3. The Corrupted Member   (deepest point, before boss)
     *   4. The Supply Cache       (found en route)
     *   5. The Scout's Warning    (immediately before the boss fight)
     *
     * @param guild The player's guild
     */
    @Override
    public void runEvents(Guild guild) {
        // TODO: Replace each `true` with real player input from ExpeditionController
        event1SealedTunnel(guild,    true);
        event2LostCompanion(guild,   true);
        event3CorruptedMember(guild, true);
        event4SupplyCache(guild,     true);
        event5ScoutsWarning(guild,   true);
    }

    // ------------------------------------- PRIVATE HELPERS -------------------------------------

    /**
     * Finds the living adventurer with the lowest loyalty in the party.
     * Used in event 2 to identify who feels the abandonment most acutely.
     * @param party The current main party
     * @return The least loyal living adventurer, or null if party is empty
     */
    private Adventurer findLeastLoyal(List<Adventurer> party) {
        Adventurer leastLoyal = null;
        for (Adventurer member : party) {
            if (!member.isDead()) {
                if (leastLoyal == null || member.getLoyalty() < leastLoyal.getLoyalty()) {
                    leastLoyal = member;
                }
            }
        }
        return leastLoyal;
    }

    /**
     * Finds the living adventurer with the highest madness in the party.
     * Used in event 3 to identify the void-corrupted member.
     * @param party The current main party
     * @return The most mad living adventurer, or null if party is empty
     */
    private Adventurer findMostMad(List<Adventurer> party) {
        Adventurer mostMad = null;
        for (Adventurer member : party) {
            if (!member.isDead()) {
                if (mostMad == null || member.getMadness() > mostMad.getMadness()) {
                    mostMad = member;
                }
            }
        }
        return mostMad;
    }
}