package seng201.team0.models;

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

    public String getTitle() {
        return title;
    }

    public String getSpeaker() {
        return speaker;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getBackgroundImagePath() {
        return backgroundImagePath;
    }

    public List<QuestStoryChoice> getChoices() {
        return choices;
    }
}
