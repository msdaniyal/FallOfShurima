package seng201.team76.models;

import java.util.Arrays;
import java.util.List;

/**
 * Quest 3: Void Depths.
 * Four-boss gauntlet with story choices before the fight.
 */
public class Quest3 extends Quest implements StoryDrivenQuest {

    private boolean storyCompleted;

    /**
     * Constructs Quest 3.
     *
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
        this.storyCompleted = false;
    }

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
                "It seeks to understand all life by tearing it apart. Armor means nothing to eyes that see through matter itself.",
                BossAbility.TRUE_DAMAGE,
                0
        );

        Boss chogath = new Boss(
                "Cho'Gath",
                200, 22, 12,
                250, 8, -10,
                "The Terror of the Void grows larger with every life it consumes.",
                BossAbility.DEVOUR,
                3
        );

        Boss belveth = new Boss(
                "Bel'Veth",
                240, 18, 10,
                300, 10, -12,
                "The Empress of the Void does not rage. She consumes worlds with perfect calm.",
                BossAbility.AOE,
                1
        );

        return Arrays.asList(
                new BossFight(khazix, 1, getDifficulty()),
                new BossFight(velkoz, 2, getDifficulty()),
                new BossFight(chogath, 3, getDifficulty()),
                new BossFight(belveth, 4, getDifficulty())
        );
    }

    /**
     * Gets the story events for Quest 3.
     *
     * @return The story events in order
     */
    @Override
    public List<QuestStoryEvent> getStoryEvents() {
        return Arrays.asList(
                new QuestStoryEvent(
                        "The First Echo",
                        "Narrator",
                        "Deep below Icathia, the walls begin repeating your party's voices before anyone speaks.",
                        "/images/Quest3/quest3_echo.png",
                        Arrays.asList(
                                new QuestStoryChoice(
                                        "Answer Back",
                                        "Call into the dark and challenge whatever is listening.",
                                        "The echo answers with names it should not know. The party steels itself, but madness rises."),
                                new QuestStoryChoice(
                                        "Stay Silent",
                                        "Order the party to stay silent and mark the walls instead.",
                                        "The group keeps discipline. Progress is slower, but nobody feeds the echo with fear.")
                        )
                ),
                new QuestStoryEvent(
                        "The Split Passage",
                        "Scout",
                        "Two routes descend: one narrow and safe-looking, one wide and covered in claw marks.",
                        "/images/Quest3/quest3_split_passage.png",
                        Arrays.asList(
                                new QuestStoryChoice(
                                        "Narrow Route",
                                        "Take the narrow route where the creatures cannot surround you.",
                                        "The party squeezes through safely, but the pressure and darkness fray their nerves."),
                                new QuestStoryChoice(
                                        "Claw Route",
                                        "Take the claw-marked route and search for loot from earlier victims.",
                                        "You find abandoned coin and gear, but the signs of slaughter disturb the party.")
                        )
                ),
                new QuestStoryEvent(
                        "The Living Door",
                        "Narrator",
                        "A membrane of purple flesh blocks the final chamber. It opens and closes like an eye.",
                        "/images/Quest3/quest3_living_door.png",
                        Arrays.asList(
                                new QuestStoryChoice(
                                        "Cut It Open",
                                        "Cut through before it can react.",
                                        "The door shrieks and the party rushes in together. The sound follows them into the fight."),
                                new QuestStoryChoice(
                                        "Burn Incense",
                                        "Use supplies to dull the thing's senses before entering.",
                                        "The door relaxes and opens quietly. The party enters shaken, but prepared.")
                        )
                )
        );
    }

    /**
     * Applies a Quest 3 story choice to the guild.
     *
     * @param guild The player's guild
     * @param eventIndex The event index
     * @param choiceIndex The selected choice index
     * @return Text shown after the choice
     */
    @Override
    public String applyStoryChoice(Guild guild, int eventIndex, int choiceIndex) {
        if (eventIndex == 0) {
            return "The party has spoken. The descent continues.";
        }

        boolean optionA = choiceIndex == 0;

        switch (eventIndex - 1) {
            case 0:
                if (optionA) {
                    for (Adventurer member : guild.getMainParty()) {
                        member.adjustLoyalty(6);
                        member.increaseMadness(10);
                    }
                } else {
                    guild.spendGold(15);
                    for (Adventurer member : guild.getMainParty()) {
                        member.adjustLoyalty(4);
                    }
                }
                break;
            case 1:
                if (optionA) {
                    for (Adventurer member : guild.getMainParty()) {
                        member.increaseMadness(8);
                    }
                } else {
                    guild.addGold(45);
                    for (Adventurer member : guild.getMainParty()) {
                        member.increaseMadness(6);
                        member.adjustLoyalty(-3);
                    }
                }
                break;
            case 2:
                if (optionA) {
                    for (Adventurer member : guild.getMainParty()) {
                        member.adjustLoyalty(8);
                        member.increaseMadness(8);
                    }
                } else {
                    guild.spendGold(25);
                    for (Adventurer member : guild.getMainParty()) {
                        member.adjustLoyalty(6);
                        member.setCurrentHealth(member.getCurrentHealth() + 10);
                    }
                }
                break;
            default:
                return "The party descends into the void.";
        }

        return getStoryEvents().get(eventIndex).getChoices().get(choiceIndex).getResultText();
    }

    /**
     * Gets the text shown before the Quest 3 boss gauntlet.
     *
     * @return The battle intro text
     */
    @Override
    public String getBattleIntroText() {
        return "The chamber opens into a living arena. Four predators wait in the dark.";
    }

    /**
     * Checks whether the Quest 3 story is complete.
     *
     * @return true if the story is complete
     */
    @Override
    public boolean isStoryCompleted() {
        return storyCompleted;
    }

    /**
     * Marks the Quest 3 story as completed.
     */
    @Override
    public void markStoryCompleted() {
        this.storyCompleted = true;
    }

    /**
     * Runs default Quest 3 events for direct tests or fallback loading.
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
