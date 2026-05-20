package seng201.team0.models;

/**
 * Shared story-choice data for quest event screens.
 * Controllers display the text; quest models apply the effects.
 */
public class QuestStoryChoice {
    private final String shortChoiceText;
    private final String fullChoiceText;
    private final String resultText;

    public QuestStoryChoice(String shortChoiceText, String fullChoiceText, String resultText) {
        this.shortChoiceText = shortChoiceText;
        this.fullChoiceText = fullChoiceText;
        this.resultText = resultText;
    }

    public String getShortChoiceText() {
        return shortChoiceText;
    }

    public String getFullChoiceText() {
        return fullChoiceText;
    }

    public String getChoiceText() {
        return fullChoiceText;
    }

    public String getResultText() {
        return resultText;
    }
}
