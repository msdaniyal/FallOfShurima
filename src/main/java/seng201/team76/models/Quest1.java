package seng201.team76.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Quest 1: Travelling to Icathia.
 *
 * This model stores:
 * 1. The pre-battle story events and hidden consequences.
 * 2. The boss fights used after the story sequence.
 *
 * Game logic for loyalty, madness, gold and HP changes is kept here,
 * not inside the JavaFX controller.
 *
 * @author Mohammed, Xinyi
 */
public class Quest1 extends Quest {

    private final List<StoryEvent> storyEvents;
    private final Random random = new Random();
    private boolean storyCompleted;

    /**
     * Constructs Quest 1.
     *
     * @param difficulty The selected game difficulty
     */
    public Quest1(Difficulty difficulty) {
        super(
                1,
                "Travelling to Icathia",
                "The Emperor has commanded the march to Icathia. " +
                        "The rebellion must be crushed before it spreads across Shurima.",
                10,
                difficulty
        );

        this.storyEvents = createStoryEvents();
        this.storyCompleted = false;
    }

    /**
     * Represents one story scene before the battle.
     */
    public static class StoryEvent {
        private final String title;
        private final String speaker;
        private final String prompt;
        private final String backgroundImagePath;
        private final List<StoryChoice> choices;

        /**
         * Creates a Quest 1 story event.
         *
         * @param title The event title
         * @param speaker The speaker name
         * @param prompt The event text
         * @param backgroundImagePath Path to the background image
         * @param choices The available choices
         */
        public StoryEvent(String title, String speaker, String prompt,
                          String backgroundImagePath, List<StoryChoice> choices) {
            this.title = title;
            this.speaker = speaker;
            this.prompt = prompt;
            this.backgroundImagePath = backgroundImagePath;
            this.choices = choices;
        }

        /**
         * Gets the event title.
         *
         * @return The event title
         */
        public String getTitle() {
            return title;
        }

        /**
         * Gets the speaker name.
         *
         * @return The speaker name
         */
        public String getSpeaker() {
            return speaker;
        }

        /**
         * Gets the event prompt.
         *
         * @return The event prompt
         */
        public String getPrompt() {
            return prompt;
        }

        /**
         * Gets the background image path.
         *
         * @return The background image path
         */
        public String getBackgroundImagePath() {
            return backgroundImagePath;
        }

        /**
         * Gets the choices for this event.
         *
         * @return The event choices
         */
        public List<StoryChoice> getChoices() {
            return choices;
        }
    }

    /**
     * Represents one selectable response to a story event.
     *
     * shortChoiceText is shown on the button.
     * fullChoiceText is shown when hovering over the button.
     *
     * The visible result text deliberately does not show hidden game values
     * such as loyalty or madness.
     */
    public static class StoryChoice {
        private final String shortChoiceText;
        private final String fullChoiceText;
        private final String resultText;

        private final int goldChange;
        private final int loyaltyChange;
        private final int madnessChange;
        private final int randomDamage;

        /**
         * Creates a Quest 1 story choice.
         *
         * @param shortChoiceText Short button text
         * @param fullChoiceText Full choice text
         * @param resultText Result text after choosing
         * @param goldChange Gold change from the choice
         * @param loyaltyChange Loyalty change from the choice
         * @param madnessChange Madness change from the choice
         * @param randomDamage Random damage caused by the choice
         */
        public StoryChoice(String shortChoiceText,
                           String fullChoiceText,
                           String resultText,
                           int goldChange,
                           int loyaltyChange,
                           int madnessChange,
                           int randomDamage) {
            this.shortChoiceText = shortChoiceText;
            this.fullChoiceText = fullChoiceText;
            this.resultText = resultText;
            this.goldChange = goldChange;
            this.loyaltyChange = loyaltyChange;
            this.madnessChange = madnessChange;
            this.randomDamage = randomDamage;
        }

        /**
         * Gets the short button text.
         *
         * @return The short choice text
         */
        public String getShortChoiceText() {
            return shortChoiceText;
        }

        /**
         * Gets the full choice text.
         *
         * @return The full choice text
         */
        public String getFullChoiceText() {
            return fullChoiceText;
        }

        /**
         * Kept for compatibility with any older code.
         *
         * @return full choice text
         */
        public String getChoiceText() {
            return fullChoiceText;
        }

        /**
         * Gets the result text for this choice.
         *
         * @return The result text
         */
        public String getResultText() {
            return resultText;
        }
    }

    private List<StoryEvent> createStoryEvents() {
        List<StoryEvent> events = new ArrayList<>();

        events.add(new StoryEvent(
                "The Imperial March",
                "Narrator",
                "The road to Icathia cuts through burning desert and abandoned villages. " +
                        "Your guild marches with the imperial column, but the soldiers are tired " +
                        "and the heat is breaking morale.",
                "/images/quest1_march.png",
                Arrays.asList(
                        new StoryChoice(
                                "Force the March",
                                "Push through the heat. Icathia must see our speed.",
                                "The column reaches the outer roads before sundown, but the forced pace leaves the camp silent and bitter.",
                                25, -5, 5, 0
                        ),
                        new StoryChoice(
                                "Hold Formation",
                                "Slow the march and keep the army together.",
                                "The army arrives slower, but your warriors keep formation and respect your restraint.",
                                0, 8, 0, 0
                        ),
                        new StoryChoice(
                                "Send Scouts",
                                "Send scouts ahead while the main force rests.",
                                "The scouts return with safer routes marked. Supplies are spent, but the camp rests with fewer doubts.",
                                -15, 5, 0, 0
                        )
                )
        ));

        events.add(new StoryEvent(
                "The Broken Village",
                "Narrator",
                "You pass a ruined Icathian village. The people claim the rebels took their food " +
                        "and weapons. Some imperial soldiers want to punish them for supporting the uprising.",
                "/images/quest1_village.png",
                Arrays.asList(
                        new StoryChoice(
                                "Seize Supplies",
                                "Take supplies for the army. The empire comes first.",
                                "The army takes what it needs. Your stores grow, but your party sees fear in the villagers' eyes.",
                                40, -10, 0, 0
                        ),
                        new StoryChoice(
                                "Protect Villagers",
                                "Leave guards and food behind. We are not butchers.",
                                "You leave food and guards behind. The soldiers grumble, but your party sees honour in the decision.",
                                -25, 12, 0, 0
                        ),
                        new StoryChoice(
                                "Investigate Tracks",
                                "Question the villagers and search for rebel tracks.",
                                "The tracks lead away from the village, but the truth is unclear. Unease spreads through the camp.",
                                0, 5, 5, 0
                        )
                )
        ));

        events.add(new StoryEvent(
                "The Rebel Envoy",
                "Icathian Envoy",
                "At sunset, an Icathian envoy enters your camp under a white banner. " +
                        "He says Zilean seeks time to negotiate, while Jax prepares the warriors for battle.",
                "/images/quest1_envoy.png",
                Arrays.asList(
                        new StoryChoice(
                                "Reject Envoy",
                                "Reject the envoy. The Emperor's command is final.",
                                "The envoy is rejected. The Emperor's law is upheld, but doubt follows your party into the night.",
                                10, -8, 0, 0
                        ),
                        new StoryChoice(
                                "Hear Warning",
                                "Hear the envoy's warning about Icathia's desperation.",
                                "You hear the warning. Zilean fears something worse than rebellion, and his fear does not feel false.",
                                0, 8, 5, 0
                        ),
                        new StoryChoice(
                                "Detain Envoy",
                                "Detain the envoy and send false information back.",
                                "The envoy is detained and false information is sent back. It may help the assault, but some dislike the deception.",
                                20, -5, 0, 0
                        )
                )
        ));

        events.add(new StoryEvent(
                "The Strange Tremor",
                "Scout",
                "During the night, the ground beneath the camp trembles. Purple light flashes far beneath " +
                        "Icathia's walls. Some soldiers swear they heard something breathing under the earth.",
                "/images/quest1_tremor.png",
                Arrays.asList(
                        new StoryChoice(
                                "Ignore the Omen",
                                "Ignore the omen. Fear is the enemy's weapon.",
                                "You ignore the omen and keep the army moving. The camp obeys, but the earth keeps whispering.",
                                15, 0, 10, 0
                        ),
                        new StoryChoice(
                                "Prepare Defences",
                                "Let Zilean's warning trouble you. Prepare for the unknown.",
                                "You prepare for the unknown. Extra supplies are used, but your party feels ready for whatever waits below.",
                                -20, 10, 0, 0
                        ),
                        new StoryChoice(
                                "Inspect the Rift",
                                "Send a small party to inspect the disturbance.",
                                "{target} returns injured from the disturbance. The party learns the danger is real.",
                                0, 5, 5, 10
                        )
                )
        ));

        events.add(new StoryEvent(
                "At the Gates of Icathia",
                "Narrator",
                "At sunrise, Icathia's gates open. Jax stands among the warriors, ready for battle. " +
                        "Behind the walls, Zilean watches the sky as if he has already seen the ending.",
                "/images/quest1_gates.png",
                Arrays.asList(
                        new StoryChoice(
                                "Direct Challenge",
                                "Challenge Jax's warriors head-on.",
                                "{target} is wounded in the first clash, but your party respects the courage of the command.",
                                0, 10, 0, 10
                        ),
                        new StoryChoice(
                                "Guarded Advance",
                                "Advance carefully and protect the formation.",
                                "You advance with discipline and protect the formation. No glory, no panic — just control.",
                                0, 5, 0, 0
                        ),
                        new StoryChoice(
                                "Show No Mercy",
                                "Use fear. Let Icathia see what Shurima sends.",
                                "You let Icathia see the terror of Shurima. The enemy hesitates, but your own party does too.",
                                30, -10, 5, 0
                        )
                )
        ));

        return events;
    }

    /**
     * Applies a story choice to the guild and returns visible result text.
     * Hidden values such as loyalty and madness are updated here but are not
     * exposed directly in the returned text.
     *
     * @param guild The player's guild
     * @param eventIndex The index of the current story event
     * @param choiceIndex The selected choice index
     * @return Player-facing result text
     */
    public String applyStoryChoice(Guild guild, int eventIndex, int choiceIndex) {
        if (eventIndex < 0 || eventIndex >= storyEvents.size()) {
            return "The march continues.";
        }

        StoryEvent event = storyEvents.get(eventIndex);

        if (choiceIndex < 0 || choiceIndex >= event.getChoices().size()) {
            return "The march continues.";
        }

        StoryChoice choice = event.getChoices().get(choiceIndex);

        if (choice.goldChange > 0) {
            guild.addGold(choice.goldChange);
        } else if (choice.goldChange < 0) {
            guild.spendGold(Math.abs(choice.goldChange));
        }

        adjustPartyLoyalty(guild, choice.loyaltyChange);
        increasePartyMadness(guild, choice.madnessChange);

        String result = choice.resultText;

        if (choice.randomDamage > 0) {
            Adventurer target = damageRandomLivingPartyMember(guild, choice.randomDamage);

            if (target != null) {
                result = result.replace("{target}", target.getName());
            } else {
                result = result.replace("{target}", "One warrior");
            }
        }

        return result;
    }

    private void adjustPartyLoyalty(Guild guild, int amount) {
        if (amount == 0) {
            return;
        }

        for (Adventurer member : guild.getMainParty()) {
            member.adjustLoyalty(amount);
        }
    }

    private void increasePartyMadness(Guild guild, int amount) {
        if (amount <= 0) {
            return;
        }

        for (Adventurer member : guild.getMainParty()) {
            member.increaseMadness(amount);
        }
    }

    private Adventurer damageRandomLivingPartyMember(Guild guild, int amount) {
        List<Adventurer> living = new ArrayList<>();

        for (Adventurer member : guild.getMainParty()) {
            if (!member.isDead()) {
                living.add(member);
            }
        }

        if (living.isEmpty()) {
            return null;
        }

        Adventurer target = living.get(random.nextInt(living.size()));
        target.setCurrentHealth(target.getCurrentHealth() - amount);
        return target;
    }

    /**
     * Marks the pre-battle story as completed so returning from the battlefield
     * resumes combat instead of replaying choices and applying consequences again.
     */
    public void markStoryCompleted() {
        this.storyCompleted = true;
    }

    /**
     * Checks whether the Quest 1 story has already reached the battlefield.
     *
     * @return true once the player has already reached the battlefield for Quest 1
     */
    public boolean isStoryCompleted() {
        return storyCompleted;
    }

    /**
     * Gets the Quest 1 pre-battle story events.
     *
     * @return Story events for the pre-battle quest screen.
     */
    public List<StoryEvent> getStoryEvents() {
        return Collections.unmodifiableList(storyEvents);
    }

    /**
     * Gets the text shown before the Quest 1 battle starts.
     *
     * @return Text shown after all story events are complete.
     */
    public String getBattleIntroText() {
        return "The banners of Shurima rise before Icathia.\n\n" +
                "Zilean retreats into the ruined city, searching for a way to undo what is coming.\n\n" +
                "Jax remains at the gate.\n\n" +
                "The battle for Icathia begins.";
    }

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
                "The Grandmaster at Arms counters every assault, waiting for the moment your strikes falter.",
                BossAbility.IMMUNE_TURN,
                2
        );

        return Arrays.asList(
                new BossFight(zilean, 1, getDifficulty()),
                new BossFight(jax, 2, getDifficulty())
        );
    }

    /**
     * Small post-battle survival reward.
     * The main story choices happen before the battle through applyStoryChoice().
     *
     * @param guild The player's guild
     */
    @Override
    public void runEvents(Guild guild) {
        for (Adventurer member : guild.getMainParty()) {
            member.adjustLoyalty(3);
        }

        guild.addGold(15);
    }
}
