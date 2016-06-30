package simulator.cards;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Provides a shuffled standard 52 card deck, with methods to draw 1 or 5 cards and to print a 5 card hand to the
 * command line
 * 
 * @author Jacob Heldenbrand
 *
 */
public class StandardDeck {

    private int topcard;
    private String[] deck;

    public static final String[] DECK = {
	//clubs
	"2C", "3C", "4C", "5C", "6C", "7C", "8C", "9C", "10C", "JC", "QC", "KC", "AC",
	//diamonds
	"2D", "3D", "4D", "5D", "6D", "7D", "8D", "9D", "10D", "JD", "QD", "KD", "AD",
	//hearts
	"2H", "3H", "4H", "5H", "6H", "7H", "8H", "9H", "10H", "JH", "QH", "KH", "AH",
	//spades
	"2S", "3S", "4S", "5S", "6S", "7S", "8S", "9S", "10S", "JS", "QS", "KS", "AS" };

    public StandardDeck() {
	//Each instance starts with a deck field that is shuffled
	ArrayList<String> shuffled_deck = new ArrayList<String>(52);
	for (int i = 0; i < 52; i++) {
	    shuffled_deck.add(DECK[i]);
	}
	Collections.shuffle(shuffled_deck);
	this.deck = shuffled_deck.toArray(new String[shuffled_deck.size()]);
	this.topcard = 0;
    }

    //Draw Card
    public String drawCard() {
	String card = this.deck[topcard];
	this.topcard++;
	return card;
    }

    //Draw Five Cards
    public String[] drawFiveCards() {
	String[] array = new String[5];
	for (int i = 0; i < 5; i++) {
	    array[i] = this.drawCard();
	}
	return array;
    }

    public void resetTopCard() {
	topcard = 0;
    }

    public static void printHand(String[] cards) {
	for (int i = 0; i < cards.length; i++) {
	    if (i != cards.length - 1) System.out.print(cards[i] + ", ");
	    else System.out.println(cards[i]);
	}
    }
}
