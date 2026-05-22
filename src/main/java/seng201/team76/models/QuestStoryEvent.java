package seng201.team76.models;

import java.util.List;

/**
 * Shared story event data for the two-choice quest screens.
 */
public class QuestStoryEvent {
    private final String title;
    private final String speaker;
    private final String prompt;
    private final String backgroundImagePath;
    private final List<QuestStoryChoice> choices;

    /**
     * Creates one story event for a quest screen.
     *
     * @param title The event title
     * @param speaker The speaker shown above the dialogue
     * @param prompt The main event text
     * @param backgroundImagePath Path to the background image
     * @param choices The choices for this event
     */
    public QuestStoryEvent(String title,
                           String speaker,
                           String prompt,
                           String backgroundImagePath,
                           List<QuestStoryChoice> choices) {
        this.title = title;
        this.speaker = speaker;
        this.prompt = prompt;
        this.backgroundImagePath = backgroundImagePath;
        this.choices = choices;
    }

    /**
     * Gets the event title.
     *
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the event speaker.
     *
     * @return The speaker name
     */
    public String getSpeaker() {
        return speaker;
    }

    /**
     * Gets the prompt text.
     *
     * @return The prompt
     */
    public String getPrompt() {
        return prompt;
    }

    /**
     * Gets the background image path.
     *
     * @return The image path
     */
    public String getBackgroundImagePath() {
        return backgroundImagePath;
    }

    /**
     * Gets the choices for this event.
     *
     * @return The event choices
     */
    public List<QuestStoryChoice> getChoices() {
        return choices;
    }
}
