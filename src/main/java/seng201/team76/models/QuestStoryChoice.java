package seng201.team76.models;

/**
 * Shared story-choice data for quest event screens.
 * Controllers display the text; quest models apply the effects.
 */
public class QuestStoryChoice {
    private final String shortChoiceText;
    private final String fullChoiceText;
    private final String resultText;

    /**
     * Creates a story choice.
     *
     * @param shortChoiceText Short text shown on the button
     * @param fullChoiceText Longer text shown as the full order
     * @param resultText Text shown after the choice is applied
     */
    public QuestStoryChoice(String shortChoiceText, String fullChoiceText, String resultText) {
        this.shortChoiceText = shortChoiceText;
        this.fullChoiceText = fullChoiceText;
        this.resultText = resultText;
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
     * Gets the full choice text for older code.
     *
     * @return The full choice text
     */
    public String getChoiceText() {
        return fullChoiceText;
    }

    /**
     * Gets the text shown after the choice.
     *
     * @return The result text
     */
    public String getResultText() {
        return resultText;
    }
}
