package seng201.team76.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Quest 5: Faction War.
 * Final required quest before the true-ending loyalty check.
 */
public class Quest5 extends Quest implements StoryDrivenQuest {

    private boolean storyCompleted;
    private Guild preparedGuild;

    /**
     * Constructs Quest 5.
     *
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
        this.storyCompleted = false;
    }

    @Override
    protected List<BossFight> initialiseBossFights() {
        Boss enemyCaptain = new Boss(
                "Enemy Captain",
                250, 24, 12,
                400, 12, -15,
                "The rival captain built their guild the same way you did. One of you made better choices.",
                BossAbility.NONE,
                0
        );

        return Arrays.asList(new BossFight(enemyCaptain, 1, getDifficulty()));
    }

    /**
     * Quest 5 is built from the player's actual roster. The captain is the
     * main character not chosen at setup. Up to three unrecruited guild members
     * join that rival side as extra boss fights.
     *
     * @param guild The player's guild
     */
    public void prepareEnemyGuild(Guild guild) {
        if (guild == null) {
            return;
        }

        this.preparedGuild = guild;

        List<BossFight> fights = new ArrayList<>();
        int sequence = 1;

        for (Adventurer rivalMember : getUnrecruitedRivals(guild)) {
            fights.add(new BossFight(createBossFromAdventurer(rivalMember, 120, 4, 4), sequence++, getDifficulty()));
        }

        Boss rivalCaptain = createOpposingMainCharacterBoss(guild);
        fights.add(new BossFight(rivalCaptain, sequence, getDifficulty()));

        setBossFights(fights);
    }

    /**
     * Resets the Quest 5 boss fights from the prepared guild.
     */
    @Override
    public void resetBossFights() {
        if (preparedGuild != null) {
            prepareEnemyGuild(preparedGuild);
        } else {
            super.resetBossFights();
        }
    }

    private Boss createOpposingMainCharacterBoss(Guild guild) {
        if (guild.getPlayerFaction() == Faction.AATROX) {
            return new Boss(
                    "Xolaani",
                    185, 26, 6,
                    420, 12, -15,
                    "Xolaani leads the rival faction, relying on blood magic and speed rather than armour.",
                    BossAbility.HEAL_ON_HIT,
                    0
            );
        }

        return new Boss(
                "Aatrox",
                270, 25, 11,
                420, 12, -15,
                "Aatrox leads the rival faction, rejecting Xolaani's blood magic as another prison of flesh.",
                BossAbility.NONE,
                0
        );
    }

    private List<Adventurer> getUnrecruitedRivals(Guild guild) {
        List<Adventurer> roster = createFullCompanionRoster(guild.getPlayerFaction());
        List<Adventurer> rivals = new ArrayList<>();

        for (Adventurer candidate : roster) {
            if (!guild.hasMemberNamed(candidate.getName())
                    && !guild.isPermanentlyUnavailable(candidate)) {
                rivals.add(candidate);
            }

            if (rivals.size() >= 3) {
                break;
            }
        }

        return rivals;
    }

    private List<Adventurer> createFullCompanionRoster(Faction playerFaction) {
        return Arrays.asList(
                new Adventurer("Baalkux", 110, 18, 8, 20, Faction.AATROX, playerFaction, "A brutal Darkin warrior with strong attack power."),
                new Adventurer("Horazi", 90, 20, 5, 25, Faction.XOLAANI, playerFaction, "A celestial marksman with high damage but lower defence."),
                new Adventurer("Ibaaros", 130, 14, 12, 20, Faction.AATROX, playerFaction, "A tough frontline fighter with high health and defence."),
                new Adventurer("Joraal", 120, 15, 11, 20, Faction.AATROX, playerFaction, "A loyal shield-bearer who protects the party."),
                new Adventurer("Naafiri", 85, 22, 4, 25, Faction.XOLAANI, playerFaction, "A fast assassin with very high attack but low defence."),
                new Adventurer("Rhaast", 115, 19, 7, 25, Faction.AATROX, playerFaction, "An aggressive fighter who thrives in dangerous battles."),
                new Adventurer("Taarosh", 150, 12, 14, 20, Faction.AATROX, playerFaction, "A heavy tank with high health and strong defence."),
                new Adventurer("Varus", 95, 21, 6, 25, Faction.XOLAANI, playerFaction, "A ranged attacker with strong burst damage."),
                new Adventurer("Zaahen", 105, 17, 9, 20, Faction.NEUTRAL, playerFaction, "A balanced warrior who is not tied strongly to either side.")
        );
    }

    private Boss createBossFromAdventurer(Adventurer adventurer, int goldDrop, int loyaltyWin, int loyaltyLoss) {
        return new Boss(
                adventurer.getName(),
                adventurer.getMaxHealth() + 35,
                adventurer.getAttack() + 3,
                adventurer.getDefense() + 2,
                goldDrop, loyaltyWin, -Math.abs(loyaltyLoss),
                adventurer.getDescription(),
                BossAbility.NONE,
                0
        );
    }

    /**
     * Gets the story events for Quest 5.
     *
     * @return The story events in order
     */
    @Override
    public List<QuestStoryEvent> getStoryEvents() {
        return Arrays.asList(
                new QuestStoryEvent(
                        "The Darkin Rift",
                        "{enemyMc}",
                        "{bloodMagicConflict} The argument becomes a faction fracture, and the warriors who never joined your guild gather behind {enemyMc}.",
                        "/images/quest5_darkin_rift.png",
                        java.util.Collections.emptyList()
                ),
                new QuestStoryEvent(
                        "The War Council",
                        "Commander",
                        "Your followers gather around the map. The rival faction is moving before sunrise.",
                        "/images/quest5_war_council.png",
                        Arrays.asList(
                                new QuestStoryChoice(
                                        "Direct Assault",
                                        "Promise a direct assault. End the split with strength.",
                                        "Your warriors cheer the certainty, but some fear the cost of pride."),
                                new QuestStoryChoice(
                                        "Measured Plan",
                                        "Build a careful plan with scouts and reserves.",
                                        "The party trusts the planning, but preparations consume gold and time.")
                        )
                ),
                new QuestStoryEvent(
                        "The Rival Envoy",
                        "Rival Envoy",
                        "A rival envoy offers terms: abandon your chosen warrior and the war ends tonight.",
                        "/images/quest5_envoy.png",
                        Arrays.asList(
                                new QuestStoryChoice(
                                        "Reject Terms",
                                        "Reject the offer publicly and stand by your main character.",
                                        "The guild sees you refuse betrayal. Loyalty hardens around your banner."),
                                new QuestStoryChoice(
                                        "Hear Them Out",
                                        "Hear the envoy out and learn the rival army's thinking.",
                                        "You gain useful information, but the party dislikes even entertaining the offer.")
                        )
                ),
                new QuestStoryEvent(
                        "Night Saboteurs",
                        "Scout",
                        "Saboteurs are spotted near the supply carts. You can chase them or protect the camp.",
                        "/images/quest5_saboteurs.png",
                        Arrays.asList(
                                new QuestStoryChoice(
                                        "Chase Them",
                                        "Chase the saboteurs into the dark before they escape.",
                                        "You recover stolen coin, but the chase leaves the camp tense and tired."),
                                new QuestStoryChoice(
                                        "Guard Camp",
                                        "Hold formation and protect the whole camp.",
                                        "The saboteurs escape with some supplies, but the party respects the discipline.")
                        )
                ),
                new QuestStoryEvent(
                        "The Duel Challenge",
                        "Rival Captain",
                        "The rival captain challenges your champion to single combat before the armies clash.",
                        "/images/quest5_duel.png",
                        Arrays.asList(
                                new QuestStoryChoice(
                                        "Accept Duel",
                                        "Accept the duel and let your champion inspire the army.",
                                        "The duel wounds your champion, but the party rallies behind the courage."),
                                new QuestStoryChoice(
                                        "Refuse Duel",
                                        "Refuse the theatre. This is war, not a performance.",
                                        "You deny the rival their spectacle. Some call it wisdom; others call it fear.")
                        )
                ),
                new QuestStoryEvent(
                        "The Final Rally",
                        "Narrator",
                        "The field goes quiet. Every faction mark, every old resentment, every promise now stands in one line.",
                        "/images/quest5_final_rally.png",
                        Arrays.asList(
                                new QuestStoryChoice(
                                        "Mercy Speech",
                                        "Tell the party that the goal is victory, not slaughter.",
                                        "The party remembers why they followed you, though mercy may slow the blade."),
                                new QuestStoryChoice(
                                        "No Mercy",
                                        "Order the party to end the rival faction completely.",
                                        "Fear sharpens the army. The order wins obedience, not love.")
                        )
                )
        );
    }

    /**
     * Applies a Quest 5 story choice to the guild.
     *
     * @param guild The player's guild
     * @param eventIndex The event index
     * @param choiceIndex The selected choice index
     * @return Text shown after the choice
     */
    @Override
    public String applyStoryChoice(Guild guild, int eventIndex, int choiceIndex) {
        if (eventIndex == 0) {
            return "The rival banner rises. The faction war can no longer be delayed.";
        }

        boolean optionA = choiceIndex == 0;
        Adventurer main = guild.getMainCharacter();

        switch (eventIndex - 1) {
            case 0:
                if (optionA) {
                    for (Adventurer member : guild.getMainParty()) {
                        member.adjustLoyalty(6);
                        member.increaseMadness(5);
                    }
                } else {
                    guild.spendGold(35);
                    for (Adventurer member : guild.getMainParty()) {
                        member.adjustLoyalty(8);
                    }
                }
                break;
            case 1:
                if (optionA) {
                    for (Adventurer member : guild.getMainParty()) {
                        member.adjustLoyalty(10);
                    }
                } else {
                    guild.addGold(25);
                    for (Adventurer member : guild.getMainParty()) {
                        member.adjustLoyalty(-4);
                    }
                }
                break;
            case 2:
                if (optionA) {
                    guild.addGold(45);
                    for (Adventurer member : guild.getMainParty()) {
                        member.increaseMadness(4);
                    }
                } else {
                    guild.spendGold(25);
                    for (Adventurer member : guild.getMainParty()) {
                        member.adjustLoyalty(6);
                    }
                }
                break;
            case 3:
                if (optionA) {
                    if (main != null) {
                        main.setCurrentHealth(main.getCurrentHealth() - 18);
                    }
                    for (Adventurer member : guild.getMainParty()) {
                        member.adjustLoyalty(8);
                    }
                } else {
                    for (Adventurer member : guild.getMainParty()) {
                        member.adjustLoyalty(-5);
                    }
                    guild.addGold(20);
                }
                break;
            case 4:
                if (optionA) {
                    for (Adventurer member : guild.getMainParty()) {
                        member.adjustLoyalty(12);
                    }
                } else {
                    for (Adventurer member : guild.getMainParty()) {
                        member.adjustLoyalty(-6);
                        member.increaseMadness(6);
                    }
                    guild.addGold(30);
                }
                break;
            default:
                return "The armies take their positions.";
        }

        return getStoryEvents().get(eventIndex).getChoices().get(choiceIndex).getResultText();
    }

    /**
     * Gets the text shown before the faction war fight.
     *
     * @return The battle intro text
     */
    @Override
    public String getBattleIntroText() {
        return "The rival captain steps through the dust. The faction war begins now.";
    }

    /**
     * Checks whether the Quest 5 story is complete.
     *
     * @return true if the story is complete
     */
    @Override
    public boolean isStoryCompleted() {
        return storyCompleted;
    }

    /**
     * Marks the Quest 5 story as completed.
     */
    @Override
    public void markStoryCompleted() {
        this.storyCompleted = true;
    }

    /**
     * Runs default Quest 5 events for direct tests or fallback loading.
     *
     * @param guild The player's guild
     */
    @Override
    public void runEvents(Guild guild) {
        if (storyCompleted) {
            return;
        }

        // Fallback for direct testing without the story controller.
        for (int i = 0; i < getStoryEvents().size(); i++) {
            if (getStoryEvents().get(i).getChoices() != null && !getStoryEvents().get(i).getChoices().isEmpty()) {
                applyStoryChoice(guild, i, 0);
            }
        }
        markStoryCompleted();
    }

    /**
     * Applies Quest 5 post-quest faction consequences.
     *
     * @param guild The player's guild
     */
    @Override
    public void updateCharacters(Guild guild) {
        guild.collapseOpposingFaction();
        super.updateCharacters(guild);
    }
}
