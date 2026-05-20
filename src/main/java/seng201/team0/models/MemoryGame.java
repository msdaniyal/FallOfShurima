package seng201.team0.models;

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
     */
    public List<Integer> generateSequence() {
        return startRound();
    }

    /**
     * Records one clicked image and validates it immediately.
     * The model, not the controller, decides whether the attack has failed or succeeded.
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

    public void failRound() {
        completeRound(false);
    }

    private void completeRound(boolean successful) {
        this.active = false;
        this.complete = true;
        this.successful = successful;
    }

    public boolean checkSequence(List<Integer> playerInput) {
        return correctSequence.equals(playerInput);
    }

    public List<Integer> getCorrectSequence() {
        return new ArrayList<>(correctSequence);
    }

    public List<Integer> getPlayerInput() {
        return new ArrayList<>(playerInput);
    }

    public int getSequenceLength() {
        return sequenceLength;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean wasSuccessful() {
        return successful;
    }

    public static class SelectionResult {
        private final boolean correctSoFar;
        private final boolean complete;
        private final boolean successful;

        public SelectionResult(boolean correctSoFar, boolean complete, boolean successful) {
            this.correctSoFar = correctSoFar;
            this.complete = complete;
            this.successful = successful;
        }

        public boolean isCorrectSoFar() {
            return correctSoFar;
        }

        public boolean isComplete() {
            return complete;
        }

        public boolean isSuccessful() {
            return successful;
        }
    }
}
