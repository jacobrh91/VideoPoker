package simulatorTest.cardsTest;

import static org.junit.Assert.*;
import simulator.cards.HandAnalyzer;
import org.junit.Test;

public class HandAnalyzerTest {

	@Test
	public void modeFinderTest() {
		// Find most common number in array: returns [value, frequency]
		int[] test_case = new int[] { 1, 2, 4, 4 };
		int[] expected = new int[] { 4, 2 };
		int[] observed = HandAnalyzer.modeFinder(test_case);

		assertArrayEquals(expected, observed);
	}

	@Test
	public void minFinderTest() {
		// Finds minimum number in array
		int[] test_case = new int[] { 7, 4, 3, 6 };
		int expected = 3;
		int observed = HandAnalyzer.minFinder(test_case);
		assertEquals(expected, observed);
	}

	@Test
	public void valueConvertTest() {
		String[] testCards = new String[] { "2C", "3S", "4D", "5C", "6H", "7C", "8C", "9H", "10C", "JD", "QD", "KC",
				"AC" };
		int[] expectedOutput = new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 };
		for (int i = 0; i < testCards.length; i++) {
			int observedOutput = HandAnalyzer.valueConvert(testCards[i]);
			assertEquals(expectedOutput[i], observedOutput);
		}
	}

	@Test
	public void suitConvertTest() {
		/*
		 * Suit KEY 'C' = 1 'D' = 2 'H' = 3 'S' = 4
		 */
		String[] testCards = new String[] { "2C", "10S", "9H", "3D" };
		int[] expectedOutput = new int[] { 1, 4, 3, 2 };
		for (int i = 0; i < testCards.length; i++) {
			int observedOutput = HandAnalyzer.suitConvert(testCards[i]);
			assertEquals(expectedOutput[i], observedOutput);
		}
	}

	@Test
	public void straightCheckTest() {
		// Cases
		int[] first = new int[] { 3, 4, 5, 6, 7 };
		int[] second = new int[] { 4, 3, 5, 6, 7 };
		int[] third = new int[] { 4, 5, 3, 6, 7 };
		int[] fourth = new int[] { 6, 4, 5, 3, 7 };
		int[] fifth = new int[] { 4, 5, 7, 6, 3 };

		// Tests
		int[][] testValues = new int[][] { first, second, third, fourth, fifth };
		for (int i = 0; i < testValues.length; i++) {
			// Conducts the test
			HandAnalyzer.straightCheck(testValues[i]);
			boolean observed = HandAnalyzer.getStraight();
			assertTrue(observed);
		}
	}

	private static boolean analysis(String[] hand, String expected) {
		HandAnalyzer instance = new HandAnalyzer();
		String observed = instance.handAnalysis(hand);
		if (observed == expected) {
			return true;
		} else {
			return false;
		}
	}

	@Test
	public void handAnalysisTest() {
		String expected;
		String[] hand;

		// Nothing
		expected = "Nothing";

		hand = new String[] { "5C", "6C", "3S", "AS", "KD" };
		assertTrue(analysis(hand, expected));

		// One Pair: Low
		expected = "One Pair: Low";

		hand = new String[] { "5C", "9S", "9S", "10H", "AD" };
		assertTrue(analysis(hand, expected));

		// One Pair: Royal
		expected = "One Pair: Royal";

		hand = new String[] { "QC", "QS", "9S", "10H", "AD" };
		assertTrue(analysis(hand, expected));

		// Two Pairs
		expected = "Two Pairs";

		hand = new String[] { "QC", "QS", "10S", "10H", "AD" };
		assertTrue(analysis(hand, expected));

		// Three-of-a-Kind
		expected = "Three-of-a-Kind";

		hand = new String[] { "QC", "QS", "QD", "10H", "AD" };
		assertTrue(analysis(hand, expected));

		// Full House
		expected = "Full House";

		hand = new String[] { "QC", "QS", "QD", "10H", "10D" };
		assertTrue(analysis(hand, expected));

		// Four-of-a-Kind
		expected = "Four-of-a-Kind";

		hand = new String[] { "QC", "QS", "QD", "QH", "2D" };
		assertTrue(analysis(hand, expected));

		// Flush
		expected = "Flush";

		hand = new String[] { "JC", "QC", "10C", "6C", "2C" };
		assertTrue(analysis(hand, expected));

		// Straight
		expected = "Straight";

		hand = new String[] { "QC", "JS", "10D", "9H", "8D" };
		assertTrue(analysis(hand, expected));

		// Straight Flush
		expected = "Straight Flush";

		hand = new String[] { "QC", "JC", "10C", "9C", "8C" };
		assertTrue(analysis(hand, expected));

		// Royal Flush
		expected = "Royal Flush";

		hand = new String[] { "AH", "KH", "QH", "JH", "10H" };
		assertTrue(analysis(hand, expected));
	}
}