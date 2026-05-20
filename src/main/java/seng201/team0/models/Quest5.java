package seng201.team0.models;

import java.util.Arrays;
import java.util.List;

/**
 * Quest 5: Faction War.
 * Final required quest before the true-ending loyalty check.
 */
public class Quest5 extends Quest implements StoryDrivenQuest {

    private boolean storyCompleted;

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

    @Override
    public List<QuestStoryEvent> getStoryEvents() {
        return Arrays.asList(
                new QuestStoryEvent(
                        "The War Council",
                        "Commander",
                        "Your followers gather around the map. The rival faction is moving before sunrise.",
                        "/images/Quest5/quest5_war_council.png",
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
                        "/images/Quest5/quest5_envoy.png",
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
                        "/images/Quest5/quest5_saboteurs.png",
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
                        "/images/Quest5/quest5_duel.png",
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
                        "/images/Quest5/quest5_final_rally.png",
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

    @Override
    public String applyStoryChoice(Guild guild, int eventIndex, int choiceIndex) {
        boolean optionA = choiceIndex == 0;
        Adventurer main = guild.getMainCharacter();

        switch (eventIndex) {
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

    @Override
    public String getBattleIntroText() {
        return "The rival captain steps through the dust. The faction war begins now.";
    }

    @Override
    public boolean isStoryCompleted() {
        return storyCompleted;
    }

    @Override
    public void markStoryCompleted() {
        this.storyCompleted = true;
    }

    @Override
    public void runEvents(Guild guild) {
        if (storyCompleted) {
            return;
        }

        // Fallback for direct testing without the story controller.
        for (int i = 0; i < getStoryEvents().size(); i++) {
            applyStoryChoice(guild, i, 0);
        }
        markStoryCompleted();
    }

    @Override
    public void updateCharacters(Guild guild) {
        guild.collapseOpposingFaction();
        guild.lockParty();
        super.updateCharacters(guild);
    }
}
