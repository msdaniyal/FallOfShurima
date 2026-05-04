package seng201.team0.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Manages the memory picture game used during boss fights.
 * A sequence of images is shown to the player who must recall them in order.
 * Sequence length depends on difficulty:
 * Easy = 4, Normal = 7, Hard = 10.
 * @author Mohammed, Xinyi
 */
public class MemoryGame {

    private Difficulty difficulty;
    private List<Integer> correctSequence;
    private int sequenceLength;
    private Random random;

    /**
     * Constructs a MemoryGame for the given difficulty.
     * @param difficulty The game difficulty
     */
    public MemoryGame(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.random = new Random();
        this.correctSequence = new ArrayList<>();
        if (difficulty.equals(Difficulty.EASY)) {
            this.sequenceLength = 4;
        } else if (difficulty.equals(Difficulty.NORMAL)) {
            this.sequenceLength = 7;
        } else if (difficulty.equals(Difficulty.HARD)) {
            this.sequenceLength = 10;
        }
    }

    /**
     * Generates a new random sequence of image indices without repeats.
     * Indices are in range [0, sequenceLength) so they map directly to
     * the ImageViews shown in the controller.
     * Stores result in correctSequence for later comparison.
     * @return The generated sequence as a list of image indices
     */
    public List<Integer> generateSequence() {
        List<Integer> allIndices = new ArrayList<>();
        for (int i = 0; i < sequenceLength; i++) {
            allIndices.add(i);
        }
        Collections.shuffle(allIndices, random);
        correctSequence = new ArrayList<>(allIndices);
        return new ArrayList<>(correctSequence);
    }

    /**
     * Checks whether the player's input matches the correct sequence in order.
     * @param playerInput The list of image indices the player selected in order
     * @return True if playerInput exactly matches correctSequence
     */
    public boolean checkSequence(List<Integer> playerInput) {
        return correctSequence.equals(playerInput);
    }

    /**
     * @return The current correct sequence
     */
    public List<Integer> getCorrectSequence() {
        return correctSequence;
    }

    /**
     * @return The sequence length for this difficulty
     */
    public int getSequenceLength() {
        return sequenceLength;
    }
}