package seng201.team76.unittests.models;

import org.junit.jupiter.api.Test;
import seng201.team76.models.Difficulty;
import seng201.team76.models.MemoryGame;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the MemoryGame model.
 */
public class MemoryGameTest {

    @Test
    void sequenceLengthMatchesDifficulty() {
        assertEquals(3, new MemoryGame(Difficulty.EASY).getSequenceLength());
        assertEquals(6, new MemoryGame(Difficulty.NORMAL).getSequenceLength());
        assertEquals(9, new MemoryGame(Difficulty.HARD).getSequenceLength());
    }

    @Test
    void generatedSequenceHasExpectedLengthAndNoRepeats() {
        MemoryGame memoryGame = new MemoryGame(Difficulty.NORMAL);

        List<Integer> sequence = memoryGame.generateSequence();

        assertEquals(6, sequence.size());
        assertEquals(6, sequence.stream().distinct().count());
        for (Integer value : sequence) {
            assertTrue(value >= 0 && value < 6);
        }
    }

    @Test
    void checkSequenceReturnsTrueForExactGeneratedSequence() {
        MemoryGame memoryGame = new MemoryGame(Difficulty.EASY);
        List<Integer> sequence = memoryGame.generateSequence();

        assertTrue(memoryGame.checkSequence(sequence));
    }

    @Test
    void checkSequenceReturnsFalseForWrongOrder() {
        MemoryGame memoryGame = new MemoryGame(Difficulty.NORMAL);
        List<Integer> sequence = memoryGame.generateSequence();
        List<Integer> wrongOrder = new ArrayList<>(sequence);

        if (wrongOrder.size() >= 2) {
            int first = wrongOrder.get(0);
            wrongOrder.set(0, wrongOrder.get(1));
            wrongOrder.set(1, first);
        }

        assertFalse(memoryGame.checkSequence(wrongOrder));
    }

    @Test
    void getCorrectSequenceReturnsTheGeneratedSequence() {
        MemoryGame memoryGame = new MemoryGame(Difficulty.EASY);
        List<Integer> generated = memoryGame.generateSequence();

        assertEquals(generated, memoryGame.getCorrectSequence());
    }
}
