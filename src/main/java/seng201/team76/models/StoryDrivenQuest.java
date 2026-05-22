package seng201.team76.models;

import java.util.List;

/**
 * Implemented by quests that show story choices before the battlefield.
 */
public interface StoryDrivenQuest {
    /**
     * Gets the story events shown before the battlefield.
     *
     * @return The story events
     */
    List<QuestStoryEvent> getStoryEvents();

    /**
     * Applies one story choice to the guild.
     *
     * @param guild The player's guild
     * @param eventIndex The event index
     * @param choiceIndex The selected choice index
     * @return Text shown after the choice
     */
    String applyStoryChoice(Guild guild, int eventIndex, int choiceIndex);

    /**
     * Gets the text shown before the boss fight starts.
     *
     * @return The battle intro text
     */
    String getBattleIntroText();

    /**
     * Checks whether this quest's story has already been completed.
     *
     * @return true if the story is complete
     */
    boolean isStoryCompleted();

    /**
     * Marks the story section as completed.
     */
    void markStoryCompleted();
}
