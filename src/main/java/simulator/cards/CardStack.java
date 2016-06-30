package simulator.cards;

import java.util.Hashtable;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import simulator.windows.VideoPokerUI;

/**
 * Creates a stack of 52 cards, and provides method to make all but one of them invisible
 * 
 * @author Jacob Heldenbrand
 * @see VideoInterface
 */
public class CardStack {

    /*
     * Bookkeeping: Cards in stack are in the same order as they are defined in
     * the StandardDeck Class, so those can be used as a way to determine if
     * that card is the one that matches the card wanted in the makeVisible
     * method
     */
    BooleanProperty flop_check = new SimpleBooleanProperty();

    StackPane HOLD_ICON = new StackPane();

    static String[] deck = StandardDeck.DECK;
    Hashtable<String, Image> image_hash;
    Hashtable<Image, String> reverse;

    public StackPane stack = new StackPane();

    BooleanProperty hold_status = new SimpleBooleanProperty(false);

    public final boolean getPos() {
	return hold_status.get();
    }

    public final void setPos(boolean value) {
	hold_status.set(value);
    }

    public CardStack(Hashtable<String, Image> hash_t, BooleanProperty hold_property, VideoPokerUI instance) {

	// Make hold icon
	Rectangle back = new Rectangle(70, 25, Color.GOLD);
	back.setArcHeight(15);
	back.setArcWidth(15);
	back.setStroke(Color.FIREBRICK);
	back.setStrokeWidth(3);
	Text text = new Text("HOLD");
	text.setStroke(Color.rgb(205, 155, 29));
	text.setStrokeWidth(1.2);
	text.setFont(Font.font("Verdana", FontWeight.BOLD, 19));
	text.setTextAlignment(TextAlignment.CENTER);

	HOLD_ICON.getChildren().addAll(back, text);

	// Keeps flop check in sync with video interface
	flop_check.set(instance.getFlop().get());
	instance.getFlop().addListener(e -> {
	    flop_check.set(instance.getFlop().get());
	});

	image_hash = hash_t;

	// Adds shadow to each instance
	DropShadow shadow = new DropShadow(9, Color.BLACK);
	shadow.setOffsetX(5);
	shadow.setOffsetY(3);
	stack.setEffect(shadow);

	DropShadow shadow1 = new DropShadow(1, Color.BLACK);
	shadow1.setOffsetX(2);
	shadow1.setOffsetY(1);

	stack.setAlignment(Pos.CENTER);
	HOLD_ICON.setVisible(false);
	HOLD_ICON.setEffect(shadow1);

	hold_property.addListener((o, oldv, newv) -> {
	    HOLD_ICON.setVisible(newv);
	});

	for (int i = 0; i < 52; i++) {
	    ImageView iv = new ImageView(image_hash.get(deck[i]));
	    // iv.setVisible(false);
	    stack.getChildren().add(iv);
	    // Card is clicked
	    iv.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
		    // changes property, but only if not in the final
		    // turn
		    if (flop_check.get()) {
			if (hold_property.get()) hold_property.set(false);
			else hold_property.set(true);
		    }
		}
	    });
	}
	// Hold Icon is clicked
	HOLD_ICON.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent event) {
		// changes property, but only if not in the final turn
		if (flop_check.get()) {
		    if (hold_property.get()) hold_property.set(false);
		    else hold_property.set(true);
		}
	    }
	});
	// Adds hold icon last so it is at the top of the stack
	stack.getChildren().add(HOLD_ICON);
    }

    public void makeVisible(String card, StackPane instance) {

	// Loop served two purposes:
	// finds position of card to be made visible
	for (int i = 0; i < 52; i++) {
	    // Initially sets each card to be non-visible to turn off prior card
	    instance.getChildren().get(i).setVisible(false);
	    if (card == StandardDeck.DECK[i]) {
		// Turns image of wanted card to visible
		instance.getChildren().get(i).setVisible(true);
	    }
	}
    }

}
