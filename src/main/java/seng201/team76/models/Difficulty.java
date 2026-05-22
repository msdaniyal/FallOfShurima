package seng201.team76.models;

/**
 * Enum representing the difficulty settings of the game.
 * Difficulty affects starting gold and the loyalty threshold for the Quest 6 gate.
 * @author Mohammed, Xinyi
 */
public enum Difficulty {

    /** Easy: more gold, lower loyalty threshold. */
    EASY(300, 40),

    /** Normal: balanced starting conditions. */
    NORMAL(200, 60),

    /** Hard: less gold, higher loyalty threshold. */
    HARD(100, 75);

    private final int startingGold;
    private final int loyaltyThreshold;

    /**
     * Constructs a Difficulty with starting gold and loyalty threshold.
     * @param startingGold Gold the guild begins with
     * @param loyaltyThreshold Min loyalty required to avoid Quest 6
     */
    Difficulty(int startingGold, int loyaltyThreshold) {
        this.startingGold = startingGold;
        this.loyaltyThreshold = loyaltyThreshold;
    }

    /**
     * Gets the starting gold for this difficulty.
     *
     * @return The starting gold for this difficulty
     */
    public int getStartingGold() {
        return startingGold;
    }

    /**
     * Gets the loyalty threshold for this difficulty.
     *
     * @return The loyalty threshold required to avoid facing Zoe
     */
    public int getLoyaltyThreshold() { return loyaltyThreshold; }
}
