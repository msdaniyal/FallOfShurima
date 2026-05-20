package seng201.team0.models;

import java.util.List;

/**
 * Implemented by quests that show story choices before the battlefield.
 */
public interface StoryDrivenQuest {
    List<QuestStoryEvent> getStoryEvents();
    String applyStoryChoice(Guild guild, int eventIndex, int choiceIndex);
    String getBattleIntroText();
    boolean isStoryCompleted();
    void markStoryCompleted();
}
