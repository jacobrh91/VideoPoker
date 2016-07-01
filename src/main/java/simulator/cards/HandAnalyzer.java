package simulator.cards;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides a method to analyze a 5 card poker hand and determine the outcome
 * 
 * @author Jacob Heldenbrand
 * @see StandardDeck
 *
 */
public class HandAnalyzer {

	// hand_outcome is not static, because each call in VideoInterface needs to
	// have its own outcome
	private static String hand_outcome;

	private static boolean flush = false;
	private static boolean straight = false;

	private static int[] card_values = new int[5];
	private static int[] card_suits = new int[5];

	/*
	 * Card value KEY "2" = 2 "3" = 3 "4" = 4 ... "Q" = 12 "K" = 13 "A" = 14
	 */

	/*
	 * Suit KEY 'C' = 1 'D' = 2 'H' = 3 'S' = 4
	 */

	// Converts card number to integer.
	public static int valueConvert(String item) {
		String cardVal = item.substring(0, item.length() - 1);
		int cardFinal;

		switch (cardVal) {
		case "2":
			cardFinal = 2;
			break;
		case "3":
			cardFinal = 3;
			break;
		case "4":
			cardFinal = 4;
			break;
		case "5":
			cardFinal = 5;
			break;
		case "6":
			cardFinal = 6;
			break;
		case "7":
			cardFinal = 7;
			break;
		case "8":
			cardFinal = 8;
			break;
		case "9":
			cardFinal = 9;
			break;
		case "10":
			cardFinal = 10;
			break;
		case "J":
			cardFinal = 11;
			break;
		case "Q":
			cardFinal = 12;
			break;
		case "K":
			cardFinal = 13;
			break;
		case "A":
			cardFinal = 14;
			break;
		default:
			cardFinal = 0;
			break;
		}
		return cardFinal;
	}

	// Saves suit of each card.
	public static int suitConvert(String item) {
		Pattern myPattern = Pattern.compile("([^CDHS]+)([CDHS])");
		Matcher myMatcher = myPattern.matcher(item);
		myMatcher.find();
		String cardSuit = myMatcher.group(2);
		char suitCard = cardSuit.charAt(0);
		int cSuit;
		switch (suitCard) {
		case 'C':
			cSuit = 1;
			break;
		case 'D':
			cSuit = 2;
			break;
		case 'H':
			cSuit = 3;
			break;
		case 'S':
			cSuit = 4;
			break;
		default:
			cSuit = 0;
			break;
		}
		return cSuit;
	}

	// Combines Convert methods for each card in hand, and adds
	// values to card_values and card_suits
	public void processHand(String[] hand) {
		for (int i = 0; i < 5; i++) {
			int values = valueConvert(hand[i]);
			int suit = suitConvert(hand[i]);
			card_values[i] = values;
			card_suits[i] = suit;
		}
	}

	/*
	 * HIERARCHY: -------------- royal flush straight flush four of a kind full
	 * house flush straight three of a kind two pair one pair
	 */
	// Returns most frequent item w/ Format: {card value, frequency}
	public static int[] modeFinder(int a[]) {

		int maxValue = 0;
		int maxCount = 0;

		for (int i = 0; i < a.length; ++i) {
			int count = 0;
			for (int j = 0; j < a.length; ++j) {
				if (a[j] == a[i])
					++count;
			}
			if (count > maxCount) {
				maxCount = count;
				maxValue = a[i];
			}
		}
		int[] maxArray = { maxValue, maxCount };
		return maxArray;
	}

	// Returns minimum integer in list
	public static int minFinder(int a[]) {
		int min = a[0];
		for (int i = 1; i < a.length; i++) {
			if (a[i] < min)
				min = a[i];
		}
		return min;
	}

	public static void straightCheck(int values[]) {
		int min = minFinder(values);
		int foundCount = 0;
		// for each value that should be in straight
		for (int i = min; i < min + 5; i++) {
			// for each card in hand
			for (int j = 0; j < 5; j++) {
				if (values[j] == i) {
					foundCount++;
					// break so don't double count a single number present twice
					break;
				}
			}
		}
		if (foundCount == 5)
			straight = true;
	}

	public String handAnalysis(String[] hand) {

		// Hand outcome reset at beginning of call
		hand_outcome = "Nothing";

		// processHand at very beginning
		processHand(hand);
		// Format: {card value, frequency}
		int[] modeValue = modeFinder(card_values);
		int[] modeSuit = modeFinder(card_suits);
		// Check for flush
		if (modeSuit[1] == 5)
			flush = true;
		// Check for straight
		straightCheck(card_values);
		if (straight && flush) {
			// Check for royalty
			int sum = 0;
			for (int i = 0; i < 5; i++)
				sum += card_values[i];
			// Royalty sum = A + K + Q + J + 10 =
			// 14 + 13 + 12 + 11 + 10 = 60
			if (sum == 60)
				hand_outcome = "Royal Flush";
			else
				hand_outcome = "Straight Flush";
		} else if (straight && !flush) {
			hand_outcome = "Straight";
		} else if (!straight && flush) {
			hand_outcome = "Flush";
		}
		if (modeValue[1] == 4)
			hand_outcome = "Four-of-a-Kind";
		// If three, check for full house
		else if (modeValue[1] == 3) {
			int[] others = new int[2];
			int counter = 0;
			for (int i = 0; i < 5; i++) {
				if (card_values[i] != modeValue[0]) {
					others[counter] = card_values[i];
					counter++;
				}
			}
			if (others[0] == others[1])
				hand_outcome = "Full House";
			else
				hand_outcome = "Three-of-a-Kind";
		}
		// If two, check for two pair
		else if (modeValue[1] == 2) {
			int[] o = new int[3];
			int counter = 0;
			int pair_value = 0;
			for (int i = 0; i < 5; i++) {
				int cValues = card_values[i];
				if (cValues != modeValue[0]) {
					o[counter] = cValues;
					counter++;
				} else
					pair_value = cValues;
			}
			if (o[0] == o[1] || o[0] == o[2] || o[1] == o[2]) {
				hand_outcome = "Two Pairs";
			} else {
				if (pair_value >= 11)
					hand_outcome = "One Pair: Royal";
				else
					hand_outcome = "One Pair: Low";
			}
		}
		// Reset bookkeeping variables
		straight = false;
		flush = false;
		// Return outcome
		return hand_outcome;

	}

}