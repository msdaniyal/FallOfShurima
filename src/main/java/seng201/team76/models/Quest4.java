package seng201.team76.models;

import java.util.Arrays;
import java.util.List;

/**
 * Quest 4: Crimson Court.
 * Story choices lead into Vladimir's HEAL_ON_HIT fight.
 */
public class Quest4 extends Quest implements StoryDrivenQuest {

    private boolean storyCompleted;

    /**
     * Constructs Quest 4.
     *
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
        this.storyCompleted = false;
    }

    @Override
    protected List<BossFight> initialiseBossFights() {
        Boss vladimir = new Boss(
                "Vladimir",
                200, 20, 9,
                300, 10, -10,
                "Every wound you give him fills the chalice he drinks from. He has outlived empires by turning pain into sustenance.",
                BossAbility.HEAL_ON_HIT,
                0
        );

        return Arrays.asList(new BossFight(vladimir, 1, getDifficulty()));
    }

    /**
     * Gets the story events for Quest 4.
     *
     * @return The story events in order
     */
    @Override
    public List<QuestStoryEvent> getStoryEvents() {
        return Arrays.asList(
                new QuestStoryEvent(
                        "The Crimson Offer",
                        "Vladimir",
                        "{quest4Offer} {quest4BattleReason}",
                        "/images/Quest4/quest4_blood_magic_offer.png",
                        java.util.Collections.emptyList()
                ),
                new QuestStoryEvent(
                        "The Blood Gate",
                        "Narrator",
                        "The mansion gate opens without a hand touching it. Blood-red lanterns ignite one by one along the path.",
                        "/images/Quest4/quest4_gate.png",
                        Arrays.asList(
                                new QuestStoryChoice(
                                        "Enter Boldly",
                                        "Walk through the front gate and show no fear.",
                                        "The party marches in with pride, but the mansion marks every heartbeat."),
                                new QuestStoryChoice(
                                        "Use Side Entry",
                                        "Circle around and enter through the servants' passage.",
                                        "You spend time and supplies finding a quieter way in, but the party trusts the caution.")
                        )
                ),
                new QuestStoryEvent(
                        "The Portrait Hall",
                        "Narrator",
                        "Painted nobles turn their eyes toward the party. Some portraits whisper offers of wealth.",
                        "/images/Quest4/quest4_portraits.png",
                        Arrays.asList(
                                new QuestStoryChoice(
                                        "Burn Them",
                                        "Burn the talking portraits before they poison the party's thoughts.",
                                        "The hall screams as canvas and old magic burn. The party is disturbed, but united."),
                                new QuestStoryChoice(
                                        "Listen Closely",
                                        "Listen to the portraits and take the secrets they offer.",
                                        "The portraits reveal a hidden vault, but their voices crawl under everyone's skin.")
                        )
                ),
                new QuestStoryEvent(
                        "The Red Feast",
                        "Servant",
                        "A dining table waits, perfectly set. The food smells warm, but the cups are filled with something too dark to be wine.",
                        "/images/Quest4/quest4_feast.png",
                        Arrays.asList(
                                new QuestStoryChoice(
                                        "Eat Carefully",
                                        "Eat only the safe-looking food. The party needs strength.",
                                        "The meal restores the party, but the mansion's magic stains the moment."),
                                new QuestStoryChoice(
                                        "Destroy Feast",
                                        "Destroy the feast. Nothing in this place is a gift.",
                                        "The party goes hungry, but nobody doubts your judgement.")
                        )
                ),
                new QuestStoryEvent(
                        "The Crimson Treasury",
                        "Narrator",
                        "Gold and relics sit behind glass cases. Some are trophies. Some are warnings.",
                        "/images/Quest4/quest4_treasury.png",
                        Arrays.asList(
                                new QuestStoryChoice(
                                        "Loot Everything",
                                        "Take the treasury. The guild needs resources.",
                                        "The guild grows rich, but the greed feels watched."),
                                new QuestStoryChoice(
                                        "Share Relics",
                                        "Take only what the party can carry and divide it fairly.",
                                        "The guild still profits, and the party respects the restraint.")
                        )
                ),
                new QuestStoryEvent(
                        "The Hall of Cups",
                        "Vladimir",
                        "Vladimir's voice drifts from the hall ahead: 'Every empire spills. The wise learn to drink.'",
                        "/images/Quest4/quest4_hall_of_cups.png",
                        Arrays.asList(
                                new QuestStoryChoice(
                                        "Prepare Wards",
                                        "Spend supplies preparing wards before facing him.",
                                        "The party loses supplies, but enters the hall protected and ready."),
                                new QuestStoryChoice(
                                        "Charge In",
                                        "Charge in before he finishes speaking.",
                                        "The sudden assault seizes momentum, but the reckless rush rattles the weaker hearts.")
                        )
                )
        );
    }

    /**
     * Applies a Quest 4 story choice to the guild.
     *
     * @param guild The player's guild
     * @param eventIndex The event index
     * @param choiceIndex The selected choice index
     * @return Text shown after the choice
     */
    @Override
    public String applyStoryChoice(Guild guild, int eventIndex, int choiceIndex) {
        if (eventIndex == 0) {
            return "Vladimir's offer hangs in the air like a fresh wound.";
        }

        boolean optionA = choiceIndex == 0;

        switch (eventIndex - 1) {
            case 0:
                if (optionA) {
                    guild.addGold(25);
                    for (Adventurer member : guild.getMainParty()) {
                        member.increaseMadness(5);
                    }
                } else {
                    guild.spendGold(15);
                    for (Adventurer member : guild.getMainParty()) {
                        member.adjustLoyalty(5);
                    }
                }
                break;
            case 1:
                if (optionA) {
                    for (Adventurer member : guild.getMainParty()) {
                        member.adjustLoyalty(8);
                        member.increaseMadness(6);
                    }
                } else {
                    guild.addGold(40);
                    for (Adventurer member : guild.getMainParty()) {
                        member.increaseMadness(10);
                    }
                }
                break;
            case 2:
                if (optionA) {
                    for (Adventurer member : guild.getMainParty()) {
                        member.setCurrentHealth(member.getCurrentHealth() + 20);
                        member.increaseMadness(8);
                    }
                } else {
                    for (Adventurer member : guild.getMainParty()) {
                        member.adjustLoyalty(6);
                    }
                }
                break;
            case 3:
                if (optionA) {
                    guild.addGold(80);
                    for (Adventurer member : guild.getMainParty()) {
                        member.adjustLoyalty(-5);
                    }
                } else {
                    guild.addGold(30);
                    for (Adventurer member : guild.getMainParty()) {
                        member.adjustLoyalty(10);
                    }
                }
                break;
            case 4:
                if (optionA) {
                    guild.spendGold(20);
                    for (Adventurer member : guild.getMainParty()) {
                        member.adjustLoyalty(5);
                        member.setCurrentHealth(member.getCurrentHealth() + 10);
                    }
                } else {
                    guild.addGold(20);
                    for (Adventurer member : guild.getMainParty()) {
                        member.increaseMadness(8);
                    }
                }
                break;
            default:
                return "The mansion doors close behind the party.";
        }

        return getStoryEvents().get(eventIndex).getChoices().get(choiceIndex).getResultText();
    }

    /**
     * Gets the text shown before the Vladimir fight.
     *
     * @return The battle intro text
     */
    @Override
    public String getBattleIntroText() {
        return "The dining hall doors open. Vladimir raises his cup and smiles.";
    }

    /**
     * Checks whether the Quest 4 story is complete.
     *
     * @return true if the story is complete
     */
    @Override
    public boolean isStoryCompleted() {
        return storyCompleted;
    }

    /**
     * Marks the Quest 4 story as completed.
     */
    @Override
    public void markStoryCompleted() {
        this.storyCompleted = true;
    }

    /**
     * Applies Quest 4 post-quest changes and locks the party.
     *
     * @param guild The player's guild
     */
    @Override
    public void updateCharacters(Guild guild) {
        super.updateCharacters(guild);
        guild.lockParty();
    }

    /**
     * Runs default Quest 4 events for direct tests or fallback loading.
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
}
