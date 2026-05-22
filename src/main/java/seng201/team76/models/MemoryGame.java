package seng201.team76.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Manages the memory picture game used during boss fights.
 * The controller displays images and timers; this model owns the sequence,
 * player input validation, and success/failure state.
 *
 * Sequence length depends on difficulty:
 * Easy = 3, Normal = 6, Hard = 9.
 *
 * @author Mohammed, Xinyi
 */
public class MemoryGame {

    private final Difficulty difficulty;
    private final List<Integer> correctSequence;
    private final List<Integer> playerInput;
    private final int sequenceLength;
    private final Random random;
    private boolean active;
    private boolean complete;
    private boolean successful;

    /**
     * Creates a memory game for the chosen difficulty.
     *
     * @param difficulty The game difficulty
     */
    public MemoryGame(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.random = new Random();
        this.correctSequence = new ArrayList<>();
        this.playerInput = new ArrayList<>();
        this.sequenceLength = determineSequenceLength(difficulty);
        this.active = false;
        this.complete = false;
        this.successful = false;
    }

    private int determineSequenceLength(Difficulty difficulty) {
        if (difficulty == Difficulty.EASY) {
            return 3;
        }
        if (difficulty == Difficulty.HARD) {
            return 9;
        }
        return 6;
    }

    /**
     * Starts a new memory round and returns the generated sequence.
     * Indices are in range [0, sequenceLength).
     *
     * @return The generated sequence for the player to repeat
     */
    public List<Integer> startRound() {
        List<Integer> allIndices = new ArrayList<>();
        for (int i = 0; i < sequenceLength; i++) {
            allIndices.add(i);
        }

        Collections.shuffle(allIndices, random);
        correctSequence.clear();
        correctSequence.addAll(allIndices);
        playerInput.clear();
        active = true;
        complete = false;
        successful = false;

        return getCorrectSequence();
    }

    /**
     * Backwards-compatible name used by older code.
     *
     * @return The generated sequence for the player to repeat
     */
    public List<Integer> generateSequence() {
        return startRound();
    }

    /**
     * Records one clicked image and validates it immediately.
     * The model, not the controller, decides whether the attack has failed or succeeded.
     *
     * @param imageIndex The clicked image index
     * @return Result showing whether the click was correct and whether the round is complete
     */
    public SelectionResult selectImage(int imageIndex) {
        if (!active || complete) {
            return new SelectionResult(false, complete, successful);
        }

        int position = playerInput.size();
        playerInput.add(imageIndex);

        if (position >= correctSequence.size() || imageIndex != correctSequence.get(position)) {
            completeRound(false);
            return new SelectionResult(false, true, false);
        }

        if (playerInput.size() == correctSequence.size()) {
            completeRound(true);
            return new SelectionResult(true, true, true);
        }

        return new SelectionResult(true, false, false);
    }

    /**
     * Fails the current memory round.
     */
    public void failRound() {
        completeRound(false);
    }

    private void completeRound(boolean successful) {
        this.active = false;
        this.complete = true;
        this.successful = successful;
    }

    /**
     * Checks a full player sequence against the correct sequence.
     *
     * @param playerInput The sequence entered by the player
     * @return true if the sequence matches exactly
     */
    public boolean checkSequence(List<Integer> playerInput) {
        return correctSequence.equals(playerInput);
    }

    /**
     * Gets a copy of the correct sequence.
     *
     * @return The correct image order
     */
    public List<Integer> getCorrectSequence() {
        return new ArrayList<>(correctSequence);
    }

    /**
     * Gets a copy of the player's current input.
     *
     * @return The player's selected image order
     */
    public List<Integer> getPlayerInput() {
        return new ArrayList<>(playerInput);
    }

    /**
     * Gets the number of images in the sequence.
     *
     * @return The sequence length
     */
    public int getSequenceLength() {
        return sequenceLength;
    }

    /**
     * Gets the difficulty used by this memory game.
     *
     * @return The difficulty
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Checks whether a round is currently active.
     *
     * @return true if the player is still entering a sequence
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Checks whether the current round is complete.
     *
     * @return true if the round has ended
     */
    public boolean isComplete() {
        return complete;
    }

    /**
     * Checks whether the completed round was successful.
     *
     * @return true if the player completed the sequence correctly
     */
    public boolean wasSuccessful() {
        return successful;
    }

    /**
     * Result for one memory game image selection.
     */
    public static class SelectionResult {
        private final boolean correctSoFar;
        private final boolean complete;
        private final boolean successful;

        /**
         * Creates a selection result.
         *
         * @param correctSoFar true if the choice was correct so far
         * @param complete true if the round is now complete
         * @param successful true if the whole round was successful
         */
        public SelectionResult(boolean correctSoFar, boolean complete, boolean successful) {
            this.correctSoFar = correctSoFar;
            this.complete = complete;
            this.successful = successful;
        }

        /**
         * Checks whether the latest choice was correct so far.
         *
         * @return true if the player has not made a mistake yet
         */
        public boolean isCorrectSoFar() {
            return correctSoFar;
        }

        /**
         * Checks whether the round is complete.
         *
         * @return true if the round is complete
         */
        public boolean isComplete() {
            return complete;
        }

        /**
         * Checks whether the round was successful.
         *
         * @return true if the player completed the whole sequence
         */
        public boolean isSuccessful() {
            return successful;
        }
    }
}
