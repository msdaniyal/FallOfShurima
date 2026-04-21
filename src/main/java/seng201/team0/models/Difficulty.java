package seng201.team0.models;

/**
 * Enum representing the difficulty settings of the game.
 * Difficulty affects starting gold and the loyalty threshold for the Quest 6 gate.
 * @author Mohammed, Xinyi
 */
public enum Difficulty {

    /**
     * TODO: Set starting gold (higher value, e.g. 300)
     * TODO: Set loyalty threshold (lower value, e.g. 40)
     */
    EASY(300, 40),

    /**
     * TODO: Set starting gold (medium value, e.g. 200)
     * TODO: Set loyalty threshold (medium value, e.g. 60)
     */
    NORMAL(200, 60),

    /**
     * TODO: Set starting gold (lower value, e.g. 100)
     * TODO: Set loyalty threshold (higher value, e.g. 75)
     */
    HARD(100, 75);

    private final int startingGold = 0; // TODO: NEED TO FIGURE
    private final int loyaltyThreshold = 0; // TODO: NEED TO FIGURE

    /**
     * TODO: Assign startingGold and loyaltyThreshold
     */
    Difficulty(int startingGold, int loyaltyThreshold) {
        // TODO: Implement
    }

    /**
     * @return The starting gold for this difficulty
     * TODO: Return startingGold
     */
    public int getStartingGold() {
        // TODO: Implement
        return 0;
    }

    /**
     * @return The loyalty threshold required to avoid facing Zoe
     * TODO: Return loyaltyThreshold
     */
    public int getLoyaltyThreshold() {
        // TODO: Implement
        return 0;
    }
}