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
	public void valueConvert() {
		String[] testStrings = new String[] { "2C", "3S", "4D", "5C", "6H", "7C", "8C", "9H", "10C", "JD", "QD", "KC",
				"AC" };
		int[] expectedOutput = new int[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 };
		for (int i = 0; i < testStrings.length; i++) {
			int observedOutput = HandAnalyzer.valueConvert(testStrings[i]);
			assertEquals(expectedOutput[i], observedOutput);
		}
	}

}