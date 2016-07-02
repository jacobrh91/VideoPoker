package simulator.strategy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import simulator.cards.HandAnalyzer;
import simulator.cards.StandardDeck;

public class Strategy {

    /* HIERARCHY:
    * -------------- 
    * royal flush
    * straight flush
    * four of a kind
    * full house
    * flush
    * straight
    * three of a kind
    * two pair
    * one pair
    */

    //Strategy Name
    public String name;

    //If true, hand one card away from the outcome in the name
    private boolean royal_flush;
    private boolean straight_flush_OE = false;
    private boolean straight_flush;
    private boolean straight_OE = false;
    private boolean straight = false;
    private boolean flush;
    private boolean special_case;

    private String straight_outcard;
    private String flush_outcard;
    private int flush_suit = -1; //Suit integer

    private int[] original_values = new int[5]; //Unsorted
    private int[] card_values = new int[5]; //Sorted
    private int[] card_suits = new int[5];

    private String[] oneroyal = new String[] { "OneAway", //Royal Flush
	"OneAway", //Straight Flush
	"OneAway", //Flush
	"OneAway", //Straight
	"KeepPair" //Others

    };

    /*  Options for each choice "level"
     * ----------------------------------------------------------
     *  Royal Flush:    Never    / OneAway (Switch non-suit card)
     *  Straight Flush: Never    / OneAwayOE / OneAway 
     *  Flush:          Never    / OneAway
     *  Straight:       Never    / OneAwayOE / OneAway
     *  Others:         KeepPair / Exchange
     */

    private String[] onelow = new String[] { "OneAway", //Royal Flush
	"OneAway", //Straight Flush
	"OneAway", //Flush
	"OneAway", //Straight
	"Yes", "Yes", "KeepPair" //Others
    };
    /*  Options for each choice "level"
     * ----------------------------------------------------------
     *  Royal Flush:    Never    / OneAway (Switch non-suit card)
     *  Straight Flush: Never    / OneAwayOE / OneAway 
     *  Flush:          Never    / OneAway
     *  Straight:       Never    / OneAwayOE / OneAway
     *  HighTwoRoyal	Yes	 / No
     *  HighRoyal	Yes	 / No
     *  Others:         KeepPair / Exchange
     */

    private String[] nothing = new String[] { "OneAway", //Royal Flush
	"OneAway", //Straight Flush
	"OneAway", //Flush
	"OneAway", //Straight
	"Yes", //HighTwoRoyal
	"Yes", //HighRoyal
	"PrioritizeStraightIfOE"//*Special Case
    };

    /*  Options for each choice "level"
     * -------------------------------------------------------
     * ***Special Case: 
     *  Royal Flush:    Never / OneAway (Switch non-suit card)
     *  Straight Flush: Never / OneAwayOE / OneAway 
     *  Flush:          Never / OneAway
     *  Straight:       Never / OneAwayOE / OneAway
     *  HighTwoRoyal    Yes   / No
     *  HighRoyal       Yes   / Exchange
     *  *Special Case:
     *      One away from flush and one away from straight with different card
     *      Example: 4S, 5S, 6C, 7S, 10S
     *      Options: PrioritizeStraightIfOEAndRoyalPossible
     *               PrioritizeStraightIfRoyalPossible 
     *               PrioritizeStraightIfOE
     *               PrioritizeStraight
     *               PrioritizeFlush
     *      Default: PrioritizeStraightIfOE
     */

    public Strategy(String strat) throws Exception {
	name = strat;
	//Do not try to find the strategy if the null choice is selected
	if (strat != null) {
	    parseStrategy(strat);
	}
    }

    private String getFromMat(List<String[]> matrix, int row, int col) {
	String string = matrix.get(row)[col];
	return string;
    }

    public void parseStrategy(String filename) throws Exception {
	//Turn TSV into string matrix
	ArrayList<String[]> matrix = new ArrayList<String[]>();

	StringBuilder name = new StringBuilder(System.getProperty("user.dir"));
	name.append("/src/main/resources/Saved_Strategies/");
	name.append(filename);
	name.append(".strat");
	File tsv_loc = new File(name.toString());
	if (!tsv_loc.exists()) throw new IOException("Strategy File Not Found");
	FileReader fileReader = new FileReader(tsv_loc);
	BufferedReader bReader = new BufferedReader(fileReader);
	//Skips first line, which only contains column names
	bReader.readLine();
	while (true) {
	    String line = bReader.readLine();
	    //Reached end of file, so break the loop
	    if (line == null) break;
	    else {
		Pattern r = Pattern.compile("[^\n]+");
		Matcher m = r.matcher(line);
		m.find();
		String trimmed_line = m.group();
		String[] items = trimmed_line.split("\t");
		matrix.add(items);
	    }
	}
	bReader.close();
	//Update strategy values
	nothing = new String[] {getFromMat(matrix, 0, 0), getFromMat(matrix, 1, 0), getFromMat(matrix, 2, 0),
	    getFromMat(matrix, 3, 0), getFromMat(matrix, 4, 0), getFromMat(matrix, 5, 0), getFromMat(matrix, 6, 0) };
	onelow = new String[] { getFromMat(matrix, 0, 1), getFromMat(matrix, 1, 1), getFromMat(matrix, 2, 1),
	    getFromMat(matrix, 3, 1), getFromMat(matrix, 4, 1), getFromMat(matrix, 5, 1), getFromMat(matrix, 6, 1) };
	oneroyal = new String[] { getFromMat(matrix, 0, 2), getFromMat(matrix, 1, 2), getFromMat(matrix, 2, 2),
	    getFromMat(matrix, 3, 2), getFromMat(matrix, 4, 2), getFromMat(matrix, 5, 2) };
    }

    //returns highest (highest card may be part of pair)
    private static String keepHighest(String[] args) {
	String highest = args[0];

	int highest_value = 0;
	for (int i = 0; i < args.length; i++) {
	    if (HandAnalyzer.valueConvert(args[i]) > highest_value) {
		highest_value = HandAnalyzer.valueConvert(args[i]);
		highest = args[i];
	    }
	}
	return highest;
    }

    //returns array with top two highest cards
    private static String[] keep2Highest(String[] args) {
	String[] twoHighest = new String[2];
	twoHighest[0] = keepHighest(args);
	String[] newArray = new String[args.length - 1];
	int increment = 0;
	for (int i = 0; i < args.length; i++) {
	    if (args[i] != twoHighest[0]) {
		newArray[increment] = args[i];
		increment++;
	    }
	}
	twoHighest[1] = keepHighest(newArray);
	return twoHighest;
    }

    //If i in list, return true (Strings)
    private static boolean inSearch(String i, String[] list) {
	boolean check = false;
	for (int j = 0; j < list.length; j++) {
	    if (i == list[j]) {
		check = true;
		break;
	    }
	}
	return check;
    }

    //If i in list, return true (Integers)
    static boolean inSearch(int i, int[] list) {
	boolean check = false;
	for (int j = 0; j < list.length; j++) {
	    if (list[j] == i) {
		check = true;
		break;
	    }
	}
	return check;
    }

    private static int[] getPositions(int x, int[] values) {
	//  If x is present, find position(s) of x in y
	if (inSearch(x, values)) {
	    int count = 0;
	    //Determine how many matches present
	    for (int i = 0; i < values.length; i++) {
		if (values[i] == x) {
		    count++;
		}
	    }
	    //Create and fill array with count # of members
	    int[] array = new int[count];
	    int increment = 0;
	    for (int i = 0; i < values.length; i++) {
		if (values[i] == x) {
		    array[increment] = i;
		    increment++;
		}
	    }
	    return array;
	}
	else return new int[] {};
    }

    private static int[] getPositions(String x, String[] values) {
	//  If x is present, find position(s) of x in y
	if (inSearch(x, values)) {
	    int count = 0;
	    //Determine how many matches present
	    for (int i = 0; i < values.length; i++) {
		if (values[i] == x) {
		    count++;
		}
	    }
	    //Create and fill array with count # of members
	    int[] array = new int[count];
	    int increment = 0;
	    for (int i = 0; i < values.length; i++) {
		if (values[i] == x) {
		    array[increment] = i;
		    increment++;
		}
	    }
	    return array;
	}
	else return new int[] {};
    }

    private static String[] swapCard(int position, String[] hand, StandardDeck deck) {
	//Swap card at "position" in "hand" and replace with card from "deck"
	String[] new_hand = new String[hand.length];
	for (int i = 0; i < hand.length; i++) {
	    if (i == position) new_hand[i] = deck.drawCard();
	    else new_hand[i] = hand[i];
	}
	return new_hand;
    }

    //Check if flush is one card away
    private void checkForFlush(String[] test_hand) {
	//Get card suits
	for (int i = 0; i < 5; i++) {
	    card_suits[i] = HandAnalyzer.suitConvert(test_hand[i]);
	}

	//suit_mode: {suit id, frequency}
	int[] suit_mode = HandAnalyzer.modeFinder(card_suits);
	flush_suit = suit_mode[0];

	/*
	 * Check if flush is one card away.
	 * If so, find the card that needs swapped.
	 */
	if (suit_mode[1] == 4) {
	    flush = true;

	    //Find the card with the wrong suit
	    for (int i = 0; i < 5; i++) {
		if (card_suits[i] != suit_mode[0]) flush_outcard = test_hand[i];
	    }
	}
    }

    private String evaluateStraightCase(String[] test_hand, Integer _case, int lowest, int new_lowest) {
	String potential_outcard = null;

	switch (_case) {
	case 11:
	    //Find the card not part of the case 1 straight
	    for (int i = 0; i < 5; i++) {
		if (card_values[i] != lowest & card_values[i] != lowest + 1 & card_values[i] != lowest + 2
		    & card_values[i] != lowest + 3) {
		    /*
		     * Look for position of the card with the outcard 
		     * value in the original, unsorted, list.
		     */
		    potential_outcard = test_hand[getPositions(card_values[i], original_values)[0]];
		}
	    }
	    break;
	case 12:
	    //Find the card not part of the case 1 straight
	    for (int i = 0; i < 5; i++) {
		if (card_values[i] != new_lowest & card_values[i] != new_lowest + 1 & card_values[i] != new_lowest + 2
		    & card_values[i] != new_lowest + 3) {
		    /*
		     * Look for position of the card with the outcard 
		     * value in the original, unsorted, list.
		     */
		    potential_outcard = test_hand[getPositions(card_values[i], original_values)[0]];
		}
	    }
	    break;
	case 21:
	    //Find the card not part of the case 2 straight  123X5
	    for (int i = 0; i < 5; i++) {
		if (card_values[i] != lowest & card_values[i] != lowest + 1 & card_values[i] != lowest + 2
		    & card_values[i] != lowest + 4) {
		    potential_outcard = test_hand[getPositions(card_values[i], original_values)[0]];
		}
	    }
	    break;
	case 22:
	    //Find the card not part of the case 2 straight
	    for (int i = 0; i < 5; i++) {

		if (card_values[i] != new_lowest & card_values[i] != new_lowest + 1 & card_values[i] != new_lowest + 2
		    & card_values[i] != new_lowest + 4) {
		    potential_outcard = test_hand[getPositions(card_values[i], original_values)[0]];
		}
	    }
	    break;
	case 31:
	    //Find the card not part of the case 3 straight
	    for (int i = 0; i < 5; i++) {
		if (card_values[i] != lowest & card_values[i] != lowest + 1 & card_values[i] != lowest + 3
		    & card_values[i] != lowest + 4) {
		    potential_outcard = test_hand[getPositions(card_values[i], original_values)[0]];
		}
	    }
	    break;
	case 32:
	    //Find the card not part of the case 3 straight
	    for (int i = 0; i < 5; i++) {

		if (card_values[i] != new_lowest & card_values[i] != new_lowest + 1 & card_values[i] != new_lowest + 3
		    & card_values[i] != new_lowest + 4) {

		    potential_outcard = test_hand[getPositions(card_values[i], original_values)[0]];
		}
	    }
	    break;
	case 41:
	    //Find the card not part of the case 4 straight
	    for (int i = 0; i < 5; i++) {
		if (card_values[i] != lowest & card_values[i] != lowest + 2 & card_values[i] != lowest + 3
		    & card_values[i] != lowest + 4) {
		    potential_outcard = test_hand[getPositions(card_values[i], original_values)[0]];
		}
	    }
	    break;
	case 42:
	    //Find the card not part of the case 4 straight
	    for (int i = 0; i < 5; i++) {
		if (card_values[i] != new_lowest & card_values[i] != new_lowest + 2 & card_values[i] != new_lowest + 3
		    & card_values[i] != new_lowest + 4) {
		    potential_outcard = test_hand[getPositions(card_values[i], original_values)[0]];
		}
	    }
	    break;
	case 5:
	    //Find the card not part of the case 5 straight
	    for (int i = 0; i < 5; i++) {
		if (card_values[i] != new_lowest & card_values[i] != new_lowest + 1 & card_values[i] != new_lowest + 2
		    & card_values[i] != new_lowest + 3) {
		    potential_outcard = test_hand[getPositions(card_values[i], original_values)[0]];
		}
	    }
	    break;
	}

	return potential_outcard;

    }

    private void checkForStraight(String[] test_hand) {
	//Get values for each card
	for (int i = 0; i < 5; i++) {
	    card_values[i] = HandAnalyzer.valueConvert(test_hand[i]);
	    original_values[i] = HandAnalyzer.valueConvert(test_hand[i]);
	}

	//Sort the cards from lowest to highest
	Arrays.sort(card_values);

	//These are used to search for all but case 5 straight draws
	int lowest = card_values[0];
	boolean second = inSearch(lowest + 1, card_values);
	boolean third = inSearch(lowest + 2, card_values);
	boolean fourth = inSearch(lowest + 3, card_values);
	boolean fifth = inSearch(lowest + 4, card_values);

	/*
	 * In the fifth case, because lowest not in straight, 
	 * we consider the second value the first card in the straight
	 */
	int new_lowest = card_values[1];
	boolean new_second = inSearch(new_lowest + 1, card_values);
	boolean new_third = inSearch(new_lowest + 2, card_values);
	boolean new_fourth = inSearch(new_lowest + 3, card_values);
	boolean new_fifth = inSearch(new_lowest + 4, card_values);

	/*
	 * When different than -1, both of these straight_case variables signal
	 * that a straight has been found
	 */
	int straight_case1 = -1; //Case of first straight
	int straight_case2 = -1; //Case of second straight

	//Search for Straight case 1:	1234X
	//When lowest start of straight		Example: 2S, 3S, 4S, 5H, 9D
	if (second & third & fourth) {
	    straight = true;

	    if (straight_case1 != -1 & straight_case1 != 11) straight_case2 = 11;

	    else straight_case1 = 11;

	    //If the lowest is not 2, then it is open ended
	    if (lowest != 2) straight_OE = true;
	}
	//When lowest not part of straight  Example: 3S, 6H, 7H, 8H, 9H
	if (new_second & new_third & new_fourth) {
	    straight = true;

	    if (straight_case1 != -1 & straight_case1 != 12) straight_case2 = 12;

	    else straight_case1 = 12;

	    //If the lowest is not 2, then it is open ended
	    if (new_lowest != 2) straight_OE = true;
	}

	//Search for Straight case 2:	123X5
	if (second & third & fifth) {
	    straight = true;

	    if (straight_case1 != -1 & straight_case1 != 21) straight_case2 = 21;

	    else {
		straight_case1 = 21;
	    }
	}

	if (new_second & new_third & new_fifth) {
	    straight = true;
	    if (straight_case1 != -1 & straight_case1 != 22) straight_case2 = 22;

	    else {
		straight_case1 = 22;
	    }
	}

	//Search for Straight case 3:	12X45
	if (second & fourth & fifth) {
	    straight = true;

	    if (straight_case1 != -1 & straight_case1 != 31) straight_case2 = 31;

	    else {
		straight_case1 = 31;
	    }
	}

	if (new_second & new_fourth & new_fifth) {
	    straight = true;

	    if (straight_case1 != -1 & straight_case1 != 32) straight_case2 = 32;

	    else {
		straight_case1 = 32;
	    }
	}

	//Search for Straight case 4: 1X345
	if (third & fourth & fifth) {
	    straight = true;

	    if (straight_case1 != -1 & straight_case1 != 41) straight_case2 = 41;

	    else {
		straight_case1 = 41;
	    }
	}

	if (new_third & new_fourth & new_fifth) {
	    straight = true;

	    if (straight_case1 != -1 & straight_case1 != 42) straight_case2 = 42;

	    else {
		straight_case1 = 42;
	    }
	}

	//Search for Straight case 5: X2345
	if (new_second & new_third & new_fourth) {
	    straight = true;

	    if (straight_case1 != -1 & straight_case1 != 5) straight_case2 = 5;
	    else {
		straight_case1 = 5;
	    }

	    //If the highest is not an Ace (14) then it is open ended
	    if (new_lowest + 3 != 14) straight_OE = true;
	}

	//If no straight is present, the straight_case2 variable will remain at -1

	/*
	 * Determine where the straight out-card is.
	 * If a pair is present, make sure the out-card selection does not
	 * interfere with a straight-flush draw.
	 */

	boolean pair = false;
	int pair_value = 0;
	//Is a pair present
	//mode Format: {value, frequency}
	int[] mode = HandAnalyzer.modeFinder(card_values);
	if (mode[1] == 2) {
	    pair = true;
	    pair_value = mode[0];
	}

	//Find straight out-card if straight is possible
	if (straight) {

	    //If pair present
	    if (pair) {
		//Find pair locations
		int[] positions = getPositions(pair_value, original_values);
		//If flush, pick card not part of flush draw
		if (flush) {
		    //if first card in pair is in flush draw suit, pick other
		    if (card_suits[positions[0]] == flush_suit) {
			straight_outcard = test_hand[positions[1]];
		    }
		    //if first card not in suit, it is the out-card
		    else straight_outcard = test_hand[positions[0]];
		}
		else {
		    straight_outcard = test_hand[positions[1]];
		}
	    }
	    //If pair is NOT present

	    /*
	     * Multiple straight draws can be found:
	     * 		For Example: 8H, JH, 10H, 7D, QH
	     * 		 7, 8, 9, 10, J AND 8, 9, 10, J, Q
	     * 		
	     * 		We want to make sure we pick the one that does not mess up
	     * 		the flush draw if possible.
	     * 		
	     * 		Otherwise, we just pick the one with the highest ending
	     */

	    else {

		//Evaluates the first straight found
		String s_outcard_1 = evaluateStraightCase(test_hand, straight_case1, lowest, new_lowest);

		//If no other straight is found, use out-card of first straight
		if (straight_case2 == -1) {
		    straight_outcard = s_outcard_1;
		}
		//If second straight is found, use highest unless flush draw
		//is interrupted
		else {
		    String s_outcard_2 = evaluateStraightCase(test_hand, straight_case2, lowest, new_lowest);
		    if (flush) {
			int suit_one = HandAnalyzer.suitConvert(s_outcard_1);
			/*
			 * If first card does not disrupt suit draw, the other
			 * card must. So, choose the first card.
			 * 
			 * Otherwise, choose the second card.
			 */
			if (flush_suit != suit_one) {
			    straight_outcard = s_outcard_1;
			}
			else straight_outcard = s_outcard_2;
		    }
		    else {
			//Pick the straight draw with the highest value

			//Highest values for each straight draw
			int max1 = 0;
			int max2 = 0;

			//Get highest values
			for (int i = 0; i < 5; i++) {
			    String card = test_hand[i];
			    int value = HandAnalyzer.valueConvert(card);
			    if (card != s_outcard_1) {
				if (value > max1) max1 = value;
			    }
			    if (card != s_outcard_2) {
				if (value > max2) max2 = value;
			    }
			}

			//Find out which is highest, and set straight out-card
			if (max1 > max2) straight_outcard = s_outcard_1;
			else straight_outcard = s_outcard_2;
		    }
		}
	    }
	}
    }

    private void checkForSpecialCase() {
	if (straight & flush & straight_outcard != flush_outcard) special_case = true;
	else special_case = false;
    }

    private void checkForRoyalFlush(String[] test_hand) {
	if (flush & straight & !special_case) {
	    //Set straight_flush to true
	    straight_flush = true;
	    int[] in_card_values = new int[4];
	    int increment = 0;
	    //get cards in straight and flush draw
	    for (int i = 0; i < 5; i++) {
		if (test_hand[i] != flush_outcard) {
		    in_card_values[increment] = HandAnalyzer.valueConvert(test_hand[i]);
		    increment++;
		}
	    }
	    int royal_count = 0;
	    if (inSearch(10, in_card_values)) royal_count++;
	    if (inSearch(11, in_card_values)) royal_count++;
	    if (inSearch(12, in_card_values)) royal_count++;
	    if (inSearch(13, in_card_values)) royal_count++;
	    if (inSearch(14, in_card_values)) royal_count++;
	    //Finally, check to see whether 4 out of 5 royal cards are present
	    if (royal_count == 4) {
		royal_flush = true;
	    }
	    else royal_flush = false;
	}
	else royal_flush = false;
    }

    public String[] applyStrategy(String[] hand, StandardDeck deck) {
	/*
	 * returns new hand after applying instance of Strategy to "hand" 
	 * drawing cards from "deck"
	 */

	//Begins by reseting all of the bookkeeping variables
	royal_flush = false;
	straight_flush_OE = false;
	straight_flush = false;
	straight_OE = false;
	straight = false;
	flush = false;
	special_case = false;

	straight_outcard = null;
	flush_outcard = null;
	flush_suit = -1;

	//Set various potentials
	checkForFlush(hand);
	checkForStraight(hand);
	checkForSpecialCase();
	checkForRoyalFlush(hand);

	String[] new_hand = new String[5];
	HandAnalyzer instance = new HandAnalyzer();
	String outcome = instance.handAnalysis(hand);
	int[] value_mode = HandAnalyzer.modeFinder(card_values);

	String[] highest_two = keep2Highest(hand);
	String highest_card = keepHighest(hand);

	//get positions of both straight and flush out-cards if applicable
	Integer s_out_pos = null;
	Integer f_out_pos = null;
	if (straight) s_out_pos = getPositions(straight_outcard, hand)[0];
	if (flush) f_out_pos = getPositions(flush_outcard, hand)[0];

	//Keep hand if any of these outcomes are present
	if (outcome == "Royal Flush" || outcome == "Straight Flush" || outcome == "Flush" || outcome == "Straight"
	    || outcome == "Four-of-a-Kind" || outcome == "Full House") new_hand = hand;

	//Keep three cards, replace other two
	else if (outcome == "Three-of-a-Kind") {
	    for (int i = 0; i < 5; i++) {
		int value = HandAnalyzer.valueConvert(hand[i]);
		if (value == value_mode[0]) new_hand[i] = hand[i];
		else new_hand[i] = deck.drawCard();
	    }
	}
	//Keep the two pairs
	else if (outcome == "Two Pairs") {

	    String[] other_three = new String[3];

	    int a = 0; //tracker for filling in the new hand
	    int b = 0; //tracker for filling in "other_three"

	    //Get cards from first pair found
	    for (int i = 0; i < 5; i++) {
		int value = HandAnalyzer.valueConvert(hand[i]);
		//If value part of first pair found, add to final hand
		if (value == value_mode[0]) {
		    new_hand[a] = hand[i];
		    a++;
		}
		else {
		    other_three[b] = hand[i];
		    b++;
		}
	    }

	    //Find the next pair in the other three cards and save those as well
	    int[] three_values = new int[3];
	    for (int i = 0; i < 3; i++) {
		three_values[i] = HandAnalyzer.valueConvert(other_three[i]);
	    }
	    int[] three_value_mode = HandAnalyzer.modeFinder(three_values);

	    for (int i = 0; i < 3; i++) {
		if (three_values[i] == three_value_mode[0]) {
		    new_hand[a] = other_three[i];
		    a++;
		}
		else {
		    new_hand[a] = deck.drawCard();
		    a++;
		}
	    }
	}

	else if (outcome == "One Pair: Royal" || outcome == "One Pair: Low") {
	    int pair_value = value_mode[0];

	    //One pair is Royal
	    if (pair_value >= 11) {
		//Go for royal flush
		if (oneroyal[0].equals("OneAway") & royal_flush) {
		    new_hand = swapCard(f_out_pos, hand, deck);
		}
		//Go for straight flush
		else if (oneroyal[1].equals("OneAwayOE") & straight_flush_OE) {
		    new_hand = swapCard(f_out_pos, hand, deck);
		}
		else if (oneroyal[1].equals("OneAway") & straight_flush) {
		    new_hand = swapCard(s_out_pos, hand, deck);
		}
		//Go for flush
		else if (oneroyal[2].equals("OneAway") & flush) {
		    new_hand = swapCard(f_out_pos, hand, deck);
		}
		//Go for straight
		else if (oneroyal[3].equals("OneAwayOE") & straight_OE) {
		    new_hand = swapCard(s_out_pos, hand, deck);
		}
		else if (oneroyal[3].equals("OneAway") & straight) {
		    new_hand = swapCard(s_out_pos, hand, deck);
		}
		//Others
		else if (oneroyal[4].equals("KeepPair")) {
		    String[] n_hand = new String[5];
		    for (int i = 0; i < 5; i++) {
			if (card_values[i] == pair_value) {
			    n_hand[i] = hand[i];
			}
			else n_hand[i] = deck.drawCard();
		    }
		    new_hand = n_hand;
		}
		//Exchange
		else {
		    new_hand = deck.drawFiveCards();
		}
	    }
	    //One pair is not Royal
	    else {
		//Go for royal flush
		if (onelow[0].equals("OneAway") & royal_flush) {
		    new_hand = swapCard(f_out_pos, hand, deck);
		}
		//Go for straight flush
		else if (onelow[1].equals("OneAwayOE") & straight_flush_OE) {
		    new_hand = swapCard(f_out_pos, hand, deck);
		}
		else if (onelow[1].equals("OneAway") & straight_flush) {
		    new_hand = swapCard(f_out_pos, hand, deck);
		}
		//Go for flush
		else if (onelow[2].equals("OneAway") & flush) {
		    new_hand = swapCard(f_out_pos, hand, deck);
		}
		//Go for straight
		else if (onelow[3].equals("OneAwayOE") & straight_OE) {
		    new_hand = swapCard(s_out_pos, hand, deck);
		}
		else if (onelow[3].equals("OneAway") & straight) {
		    new_hand = swapCard(s_out_pos, hand, deck);
		}
		//Keep highest two if both royal
		else if (onelow[4].equals("Yes") & HandAnalyzer.valueConvert(highest_two[0]) >= 11
		    & HandAnalyzer.valueConvert(highest_two[1]) >= 11) {
		    for (int p = 0; p < 5; p++) {
			if (hand[p] == highest_two[0] || hand[p] == highest_two[1]) {
			    new_hand[p] = hand[p];
			}
			else new_hand[p] = deck.drawCard();
		    }
		}
		//Keep highest if royal
		else if (onelow[5].equals("Yes") & HandAnalyzer.valueConvert(highest_card) >= 11) {
		    for (int p = 0; p < 5; p++) {
			if (hand[p] == highest_card) {
			    new_hand[p] = hand[p];
			}
			else new_hand[p] = deck.drawCard();
		    }
		}
		//Others
		else if (onelow[6].equals("KeepPair")) {
		    String[] n_hand = new String[5];
		    for (int i = 0; i < 5; i++) {
			if (card_values[i] == pair_value) {
			    n_hand[i] = hand[i];
			}
			else n_hand[i] = deck.drawCard();
		    }
		    new_hand = n_hand;
		}
		//Exchange
		else {
		    new_hand = deck.drawFiveCards();
		}
	    }
	}
	else if (outcome == "Nothing") {
	    if (special_case) {
		if (nothing[6] == "Prioritize Straight if OE and Royal Possible" & royal_flush & straight_OE) {
		    new_hand = swapCard(s_out_pos, hand, deck);
		}
		else if (nothing[6].equals("Prioritize Straight if Royal Possible") & royal_flush & straight) {
		    new_hand = swapCard(s_out_pos, hand, deck);
		}
		else if (nothing[6].equals("Prioritize Straight if OE") & straight_OE) {
		    new_hand = swapCard(s_out_pos, hand, deck);
		}
		else if (nothing[6].equals("Prioritize Straight") & straight) {
		    new_hand = swapCard(s_out_pos, hand, deck);
		}
		//Go for flush
		else {
		    new_hand = swapCard(f_out_pos, hand, deck);
		}
	    }
	    else {
		//Go for royal flush
		if (nothing[0].equals("One Card Away") & royal_flush) {
		    new_hand = swapCard(f_out_pos, hand, deck);
		}
		//Go for straight flush
		else if (nothing[1].equals("One Card Away OE") & straight_flush_OE) {
		    new_hand = swapCard(f_out_pos, hand, deck);
		}
		else if (nothing[1].equals("One Card Away") & straight_flush) {
		    new_hand = swapCard(s_out_pos, hand, deck);
		}
		//Go for flush
		else if (nothing[2].equals("One Card Away") & flush) {
		    new_hand = swapCard(f_out_pos, hand, deck);
		}
		//Go for straight
		else if (nothing[3].equals("One Card Away OE") & straight_OE) {
		    new_hand = swapCard(s_out_pos, hand, deck);
		}
		else if (nothing[3].equals("One Card Away") & straight) {
		    new_hand = swapCard(s_out_pos, hand, deck);
		}
		//Keep highest two if royal
		else if (nothing[4].equals("Yes") & HandAnalyzer.valueConvert(highest_two[0]) >= 11
		    & HandAnalyzer.valueConvert(highest_two[1]) >= 11) {
		    for (int p = 0; p < 5; p++) {
			if (hand[p] == highest_two[0] || hand[p] == highest_two[1]) {
			    new_hand[p] = hand[p];
			}
			else new_hand[p] = deck.drawCard();
		    }
		}
		//Keep highest if royal
		else if (nothing[5].equals("Yes") & HandAnalyzer.valueConvert(highest_card) >= 11) {
		    for (int p = 0; p < 5; p++) {
			if (hand[p] == highest_card) {
			    new_hand[p] = hand[p];
			}
			else new_hand[p] = deck.drawCard();
		    }
		}
		//Exchange
		else {
		    new_hand = deck.drawFiveCards();
		}
	    }
	}
	return new_hand;
    }
}