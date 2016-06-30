package simulator.windows;

import java.util.Hashtable;
import card_images.ResourceLoader;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import simulator.SimulatorApp;
import simulator.cards.CardStack;
import simulator.cards.HandAnalyzer;
import simulator.cards.StandardDeck;

/**
 * Creates a Video Poker Screen for the player to interact with
 * 
 * @author Jacob Heldenbrand
 * @see StandardDeck
 * @see HandAnalyzer
 * @see CardStack
 * 
 */
public class VideoPokerUI {

    /*
     * If simulate is true, do not do anything with the video poker screen
     *  cards. Only simulate the number of hands given in sim_hands
     */
    boolean simulate = false;

    private int sim_hands = 100;

    public void setSimHands(int value) {
	sim_hands = value;
    }

    public int getSimHands() {
	return sim_hands;
    }

    private SimulatorApp sim_inst;
    private long hand_count = 0;

    public void setHandCount(int value) {
	hand_count = value;
    }

    public long getHandCount() {
	return hand_count;
    }

    //Number of coins added on Add Credits Press
    private int increase_unit = 100;

    public void setIncreaseUnit(int value) {
	increase_unit = value;
    }

    public int getIncreaseUnit() {
	return increase_unit;
    }

    /*
     * Pressing the Add Credits button increases this value, which triggers the
     * credits of each strategy to increase
     */
    public IntegerProperty increase_trigger = new SimpleIntegerProperty(0);

    public void setIncrease_trigger(int value) {
	increase_trigger.set(value);
    }

    public IntegerProperty getIncrease_trigger() {
	return increase_trigger;
    }

    public int getIncrease_triggerValue() {
	return increase_trigger.get();
    }

    /*BOOKKEEPING variables: either indicate whether current hand is the first
     * hand, whether or not the hand is the initial or final hand.
     * 
     * Most of these variables are reset as a new hand starts.
     */
    private static boolean first_hand = false;

    private BooleanProperty flop = new SimpleBooleanProperty();

    public BooleanProperty getFlop() {
	return flop;
    }

    public boolean getFlopValue() {
	return flop.get();
    }

    private StandardDeck deck = new StandardDeck();

    public StandardDeck getDeck() {
	return deck;
    }

    public void setDeck(StandardDeck deck) {
	this.deck = deck;
    }

    private String[] initial_hand = getDeck().drawFiveCards();

    //Final is initially identical to the initial hand, but 
    //is changed later based on the hold icons displayed.
    private String[] final_hand = initial_hand;

    //MATRIX layer fields
    private IntegerProperty highlighted = new SimpleIntegerProperty(1);

    public int getHighlighted() {
	return highlighted.get();
    }

    private static String[] names = new String[] { "ROYAL FLUSH\t\t\t",
	    "STRAIGHT FLUSH\t\t\t", "FOUR OF A KIND\t\t\t", "FULL HOUSE\t\t\t",
	    "FLUSH\t\t\t", "STRAIGHT\t\t\t", "THREE OF A KIND\t\t\t",
	    "TWO PAIR\t\t\t", "JACKS OR BETTER\t\t\t" };

    public int[] column1 = new int[] { 250, 50, 25, 9, 6, 4, 3, 2, 1 };
    public int[] column2 = new int[] { 500, 100, 50, 18, 12, 8, 6, 4, 2 };
    public int[] column3 = new int[] { 750, 150, 75, 27, 18, 12, 9, 6, 3 };
    public int[] column4 = new int[] { 1000, 200, 100, 36, 24, 16, 12, 8, 4 };
    public int[] column5 = new int[] { 4000, 250, 125, 45, 30, 20, 15, 10, 5 };

    //LABEL layer fields
    private static Text outcome_display = new Text();
    private static GridPane text_grid = new GridPane();

    private IntegerProperty credit_value = new SimpleIntegerProperty();

    public IntegerProperty getCreditValue() {
	return credit_value;
    }

    public void setCreditValue(int credit_value) {
	this.credit_value.setValue(credit_value);
    }

    private static IntegerProperty winning_row = new SimpleIntegerProperty();
    private static IntegerProperty payout = new SimpleIntegerProperty();

    //CARD layer fields
    private static HBox display_cards = new HBox(15);

    private static CardStack position1;
    private static CardStack position2;
    private static CardStack position3;
    private static CardStack position4;
    private static CardStack position5;
    private static CardStack[] stack_list;

    private static BooleanProperty hold1 = new SimpleBooleanProperty();
    private static BooleanProperty hold2 = new SimpleBooleanProperty();
    private static BooleanProperty hold3 = new SimpleBooleanProperty();
    private static BooleanProperty hold4 = new SimpleBooleanProperty();
    private static BooleanProperty hold5 = new SimpleBooleanProperty();
    private static BooleanProperty[] hold_cards = new BooleanProperty[] { hold1,
	    hold2, hold3, hold4, hold5 };

    //Hashtable with card images
    private static Hashtable<String, Image> IMAGES = new Hashtable<String, Image>();

    private static StackPane c1;
    private static StackPane c2;
    private static StackPane c3;
    private static StackPane c4;
    private static StackPane c5;

    private static StackPane[] instance_list = new StackPane[] { c1, c2, c3, c4,
	    c5 };

    //Stylistic fields
    private static Background matrix_background = new Background(
	    new BackgroundFill(Color.rgb(20, 40, 80), null, null));

    private static Border matrix_border = new Border(
	    new BorderStroke(Color.GOLD, BorderStrokeStyle.SOLID,
		    CornerRadii.EMPTY, new BorderWidths(3)));

    private static Text new_game = new Text("NEW GAME:\nPress DEAL to begin");
    private static Text game_over = new Text("GAME OVER");

    private static FadeTransition ft1 = new FadeTransition(
	    Duration.millis(1000), new_game);
    private static FadeTransition ft2 = new FadeTransition(
	    Duration.millis(1000), game_over);
    private static ScaleTransition st = new ScaleTransition(
	    Duration.millis(1000), outcome_display);

    private static DropShadow large_shadow = new DropShadow(9, 5, 3,
	    Color.BLACK);
    private static DropShadow small_shadow = new DropShadow(1, 3, 2,
	    Color.BLACK);

    private static Font impact24 = Font.font("IMPACT", 24);
    private static Font impact30 = Font.font("IMPACT", FontWeight.BOLD, 30);

    public BackgroundFill highlighted_btn = new BackgroundFill(
	    Color.rgb(205, 155, 29), new CornerRadii(7), new Insets(1));

    public BackgroundFill b_fill = new BackgroundFill(Color.GOLD,
	    new CornerRadii(7), new Insets(1));

    public Border b_border = new Border(
	    new BorderStroke(Color.rgb(205, 155, 29), BorderStrokeStyle.SOLID,
		    new CornerRadii(7), new BorderWidths(3)));

    //GENERAL METHOD(S)

    public VideoPokerUI(int initial_credits, SimulatorApp sim_instance) {
	getCreditValue().set(initial_credits);
	sim_inst = sim_instance;
    }

    private void analyzeHand() {

	String[] hand = final_hand;
	HandAnalyzer instance = new HandAnalyzer();

	String outcome = instance.handAnalysis(hand);

	switch (outcome) {
	case "Royal Flush": {
	    outcome_display.setText("Royal Flush");
	    winning_row.set(0);
	    break;
	}
	case "Straight Flush": {
	    outcome_display.setText("Straight Flush");
	    winning_row.set(1);
	    break;
	}
	case "Four-of-a-Kind": {
	    outcome_display.setText("Four of a Kind");
	    winning_row.set(2);
	    break;
	}
	case "Full House": {
	    outcome_display.setText("Full House");
	    winning_row.set(3);
	    break;
	}
	case "Flush": {
	    outcome_display.setText("Flush");
	    winning_row.set(4);
	    break;
	}
	case "Straight": {
	    outcome_display.setText("Straight");
	    winning_row.set(5);
	    break;
	}
	case "Three-of-a-Kind": {
	    outcome_display.setText("Three of a Kind");
	    winning_row.set(6);
	    break;
	}
	case "Two Pairs": {
	    outcome_display.setText("Two Pair");
	    winning_row.set(7);
	    break;
	}
	case "One Pair: Royal": {
	    outcome_display.setText("Jacks or Better");
	    winning_row.set(8);
	    break;
	}
	default: {
	    winning_row.set(-1);
	    break;
	}
	}
    }

    private static void formatStylisticFields() {
	display_cards.setAlignment(Pos.CENTER);

	//Format scale transition 
	st.setFromX(1);
	st.setFromY(1);
	st.setToX(1.10);
	st.setToY(1.10);
	st.setCycleCount(Animation.INDEFINITE);
	st.setAutoReverse(true);
	st.play();

	//Format fade transitions
	ft1.setFromValue(1);
	ft1.setToValue(0.8);
	ft1.setCycleCount(Animation.INDEFINITE);
	ft1.setAutoReverse(true);
	ft1.play();

	ft2.setFromValue(0);
	ft2.setToValue(1);
	ft2.setCycleCount(Animation.INDEFINITE);
	ft2.setAutoReverse(true);
	ft2.play();

	//Format outcome display Text
	outcome_display.setFont(impact30);
	outcome_display.setFill(Color.FIREBRICK);
	outcome_display.setEffect(small_shadow);
	outcome_display.setTextAlignment(TextAlignment.CENTER);

	outcome_display
		.setStyle("-fx-stroke: darkred;" + "-fx-stroke-width: 1.5px;");

	//Format new game Text display
	new_game.setFont(Font.font("IMPACT", FontWeight.BOLD, 35));
	new_game.setFill(Color.GOLD);
	new_game.setEffect(large_shadow);

	new_game.setStyle(
		"-fx-stroke: firebrick;" + "-fx-stroke-width: 1.5px;");
	new_game.setTextAlignment(TextAlignment.CENTER);

	//Format game over Text display
	game_over.setFont(Font.font("IMPACT", FontWeight.BOLD, 42));
	game_over.setFill(Color.GOLD);

	game_over.setStyle("-fx-stroke: firebrick;" + "-fx-stroke-width: 2px;");
	game_over.setVisible(false);
    }

    //MATRIX MAKING SECTION

    /**
     * Converts text entered as a string into a JavaFX Text Node
     * 
     * @param string
     *            this string is converted into a Text Node
     * @return the resulting Text Node
     * @see Text
     */
    private static Text textProcessor(String string) {
	Text txt = new Text(string);
	txt.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
	txt.setFill(Color.GOLD);
	return txt;
    }

    /**
     * Converts integer input into a JavaFX Text Node
     * 
     * @param integer
     *            this value is converted into a Text Node
     * @return the resulting Text Node
     * @see Text
     */
    private static Text textProcessor(int integer) {
	Integer inte = (Integer) integer;
	Text txt = new Text(inte.toString());
	txt.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
	txt.setFill(Color.GOLD);
	return txt;
    }

    /**
     * Creates a vertical box with the integer values in numbers entered from top-to-bottom with the width provided
     * 
     * @param numbers
     *            this is the array of integers put in the vertical box
     * @param width
     *            this is the width of the column
     * @return a JavaFX VBox object with the numbers present
     * @see VBox
     */
    private static VBox matProcessor(int[] numbers, int width) {

	VBox vbox = new VBox();
	vbox.setMinWidth(width);
	vbox.setPadding(new Insets(5));
	vbox.setBorder(matrix_border);
	vbox.setBackground(matrix_background);
	vbox.setAlignment(Pos.TOP_RIGHT);

	for (int i = 0; i < numbers.length; i++) {
	    Text processed = textProcessor(numbers[i]);
	    vbox.getChildren().add(processed);
	}
	return vbox;
    }

    /**
     * Creates matrix with row names in first column and values in subsequent columns
     * 
     * <p>
     * Automatically creates the columns "names" and "column1", column2", . . . , "column5" columns in the
     * VideoInterface Class
     * </p>
     * 
     * @return JavaFX HBox with VBoxes as the child Nodes
     * @see HBox
     * @see VBox
     */
    private HBox matLayer() {
	Background highlight = new Background(
		new BackgroundFill(Color.FIREBRICK, null, null));
	VBox head = new VBox();
	head.setPadding(new Insets(5));
	head.setBorder(matrix_border);
	head.setBackground(matrix_background);
	for (int i = 0; i < names.length; i++) {
	    Text processed = textProcessor(names[i]);
	    head.getChildren().add(processed);
	}

	VBox first = matProcessor(column1, 60);
	VBox second = matProcessor(column2, 60);
	VBox third = matProcessor(column3, 60);
	VBox fourth = matProcessor(column4, 60);
	VBox fifth = matProcessor(column5, 80);

	//On start, first column is highlighted
	first.setBackground(highlight);

	//Keeps highlighted column in sync 
	highlighted.addListener((v, oldval, newval) -> {
	    int old_val = (int) oldval;
	    int new_val = (int) newval;
	    //un-highlights old value
	    switch (old_val) {
	    case 1:
		first.setBackground(matrix_background);
		break;
	    case 2:
		second.setBackground(matrix_background);
		break;
	    case 3:
		third.setBackground(matrix_background);
		break;
	    case 4:
		fourth.setBackground(matrix_background);
		break;
	    case 5:
		fifth.setBackground(matrix_background);
		break;
	    }
	    //highlights new column
	    switch (new_val) {
	    case 1:
		first.setBackground(highlight);
		break;
	    case 2:
		second.setBackground(highlight);
		break;
	    case 3:
		third.setBackground(highlight);
		break;
	    case 4:
		fourth.setBackground(highlight);
		break;
	    case 5:
		fifth.setBackground(highlight);
		break;
	    }
	});

	/*
	 * Highlights winning row, updates pay out, and gets outcome text to
	 * display if the outcome is not nothing
	 */

	winning_row.addListener((o, oldv, newv) -> {
	    //if -1, outcome is nothing and no row should be highlighted
	    if ((Integer) newv == -1)
		payout.set(0);

	    else {
		//Sets color of winning hand name to WHITE
		Text head_pos = ((Text) head.getChildren().get((Integer) newv));
		head_pos.setFill(Color.WHITE);
		outcome_display.setText(head_pos.getText());

		VBox[] cols = new VBox[] { first, second, third, fourth,
			fifth };

		//Sets color of winning value to WHITE and gets pay out value
		Text position = ((Text) cols[highlighted.get() - 1]
			.getChildren().get((Integer) newv));
		position.setFill(Color.WHITE);
		int value = Integer.parseInt(position.getText());
		payout.set(value);

		//Resets the colors once a new hand occurs
		ChangeListener<Boolean> listen1 = new ChangeListener<Boolean>() {
		    @Override
		    public void changed(
			    ObservableValue<? extends Boolean> observable,
			    Boolean oldValue, Boolean newValue) {
			head_pos.setFill(Color.GOLD);
			position.setFill(Color.GOLD);
		    }
		};
		flop.addListener(listen1);
	    }

	});

	//Creates and fills final matrix for output
	HBox root = new HBox(-3);
	root.setEffect(large_shadow);
	root.getChildren().addAll(head, first, second, third, fourth, fifth);
	return root;
    }

    //LABEL SECTION
    /**
     * Creates the Payout, Credits, and Winning Hand sections of the VideoInterface Scene and adds Listeners to keep
     * their values in sync
     */
    private void getLabels() {

	//Create and format GridPane for LABEL layer
	GridPane grid = new GridPane();
	grid.setPadding(new Insets(0, 5, 0, 12));
	grid.setHgap(10);
	grid.setVgap(0);

	ColumnConstraints col1 = new ColumnConstraints(45);
	ColumnConstraints col2 = new ColumnConstraints(45);
	ColumnConstraints col3 = new ColumnConstraints(235);
	ColumnConstraints col4 = new ColumnConstraints(80);
	ColumnConstraints col5 = new ColumnConstraints(70);
	grid.getColumnConstraints().addAll(col1, col2, col3, col4, col5);

	//Create and format each Text Node
	Text wins = new Text("WIN:");
	Text cred = new Text("CREDITS:");
	Text credits = textProcessor(getCreditValue().get());
	Text winnings = textProcessor(payout.get());

	wins.setFont(impact24);
	cred.setFont(impact24);
	credits.setFont(impact24);
	winnings.setFont(impact24);

	wins.setFill(Color.FIREBRICK);
	cred.setFill(Color.FIREBRICK);
	credits.setFill(Color.FIREBRICK);
	winnings.setFill(Color.FIREBRICK);

	wins.setEffect(small_shadow);
	cred.setEffect(small_shadow);
	credits.setEffect(small_shadow);
	winnings.setEffect(small_shadow);

	//winnings is invisible on the new game screen
	winnings.setVisible(false);

	//react to change in credits
	getCreditValue().addListener((o, oldv, newv) -> {
	    Integer cv1 = getCreditValue().get();
	    credits.setText(cv1.toString());
	});

	//react to change in winnings
	payout.addListener((o, oldv, newv) -> {
	    Integer win1 = payout.get();
	    winnings.setText(win1.toString());
	    getCreditValue().set(getCreditValue().get() + payout.get());
	});

	//Display pay out only on game over screen
	flop.addListener((o, oldv, newv) -> {
	    if (flop.get())
		winnings.setVisible(false);
	    else
		winnings.setVisible(true);
	});

	grid.add(wins, 0, 0);
	grid.add(winnings, 1, 0);
	grid.add(cred, 3, 0);
	grid.add(credits, 4, 0);
	text_grid = grid;
    }

    //CARD LAYER SECTION

    /**
     * Loads all 52 card images into one StackPane, and places a stack set in each of the five card slots.
     * 
     * @see StackPane
     * @see CardStack
     */
    private void loadImages() {

	String[] deck = StandardDeck.DECK;
	for (int i = 0; i < deck.length; i++) {
	    int c_value = HandAnalyzer.valueConvert(deck[i]);
	    int c_suit = HandAnalyzer.suitConvert(deck[i]);

	    String val = null;
	    String suit = null;

	    //Converts integer values of cards into strings
	    switch (c_value) {
	    case 2:
		val = "2";
		break;
	    case 3:
		val = "3";
		break;
	    case 4:
		val = "4";
		break;
	    case 5:
		val = "5";
		break;
	    case 6:
		val = "6";
		break;
	    case 7:
		val = "7";
		break;
	    case 8:
		val = "8";
		break;
	    case 9:
		val = "9";
		break;
	    case 10:
		val = "10";
		break;
	    case 11:
		val = "jack";
		break;
	    case 12:
		val = "queen";
		break;
	    case 13:
		val = "king";
		break;
	    case 14:
		val = "ace";
		break;
	    }

	    //Converts suits of cards in integer format into suit names
	    switch (c_suit) {
	    case 1:
		suit = "clubs";
		break;
	    case 2:
		suit = "diamonds";
		break;
	    case 3:
		suit = "hearts";
		break;
	    case 4:
		suit = "spades";
		break;
	    }
	    //Creates string for name of card image file
	    String string = val + "_of_" + suit + ".png";

	    //Gets
	    Image image = ResourceLoader.getImage(string);
	    
	    /*
	     * Fills a hash table with the images stored by the card name in 
	     * string format
	     */
	    IMAGES.put(deck[i], image);
	}

	/*Places a card stack in each slot, and links the stack with 
	 * the image hash table and the appropriate hold boolean property
	 * (See the CardStack constructor for details)
	 */
	position1 = new CardStack(IMAGES, hold1, this);
	position2 = new CardStack(IMAGES, hold2, this);
	position3 = new CardStack(IMAGES, hold3, this);
	position4 = new CardStack(IMAGES, hold4, this);
	position5 = new CardStack(IMAGES, hold5, this);

	c1 = position1.stack;
	c2 = position2.stack;
	c3 = position3.stack;
	c4 = position4.stack;
	c5 = position5.stack;

	stack_list = new CardStack[] { position1, position2, position3,
		position4, position5 };
    }

    /**
     * Sets the card that is displayed in each card slot on the first hand
     */
    private void getDisplayCards() {

	display_cards.getChildren().clear();

	position1.makeVisible(final_hand[0], c1);
	position2.makeVisible(final_hand[1], c2);
	position3.makeVisible(final_hand[2], c3);
	position4.makeVisible(final_hand[3], c4);
	position5.makeVisible(final_hand[4], c5);

	display_cards.getChildren().addAll(c1, c2, c3, c4, c5);
    }

    /**
     * Displays cards in the video poker interface that are provided in the hand
     * 
     * @param hand
     *            this is the hand of cards to be displayed
     */
    private void setDisplayCards(String[] hand) {
	position1.makeVisible(hand[0], c1);
	position2.makeVisible(hand[1], c2);
	position3.makeVisible(hand[2], c3);
	position4.makeVisible(hand[3], c4);
	position5.makeVisible(hand[4], c5);
    }

    //BUTTON LAYER SECTION

    /**
     * Provides a template to format the buttons used in the button layer of the video poker interface
     * 
     * @param btn
     *            the button to be created and formatted
     * @param size
     *            the size of the text within the button, which also contributes to the button's size
     * @param height
     *            the height of the button
     * @param width
     *            the width of the button
     * @see Button
     */
    public void setButtonStyle(Button btn, double size, double height,
	    double width) {

	Font b_font = Font.font("Verdana", FontWeight.BOLD, size);

	btn.setAlignment(Pos.CENTER);
	btn.setMinHeight(height);
	btn.setMinWidth(width);
	btn.setFont(b_font);
	btn.setBackground(new Background(b_fill));
	btn.setBorder(b_border);
	btn.setEffect(large_shadow);
    }

    //Highlights button on press and resets highlight after
    private void setFlickerNoFlop(Button btn) {
	btn.setOnMousePressed(e -> {
	    if (!getFlopValue()) {
		btn.setBackground(new Background(highlighted_btn));
	    }

	});

	btn.setOnMouseReleased(e -> {
	    if (!getFlopValue()) {
		btn.setBackground(new Background(b_fill));
	    }
	});
    }

    //Highlights button on press and resets highlight after
    public void setFlicker(Button btn) {
	btn.setOnMousePressed(e -> {
	    btn.setBackground(new Background(highlighted_btn));
	});

	btn.setOnMouseReleased(e -> {
	    btn.setBackground(new Background(b_fill));
	});
    }

    /**
     * Creates horizontal box with the buttons of the video poker screen
     * 
     * @return JavaFX HBox with the buttons
     * @see HBox
     */
    private HBox buttonLayer() {

	HBox layer = new HBox(17);
	layer.setAlignment(Pos.CENTER);

	Button add_coin = new Button("ADD CREDITS");
	Button sim = new Button("SIMULATE");
	Button deal = new Button("DEAL");
	Button bet_one = new Button("BET ONE");
	Button max = new Button("BET MAX");

	//Makes the buttons change colors while being pressed
	setFlickerNoFlop(add_coin);
	setFlickerNoFlop(sim);
	setFlicker(deal);
	setFlickerNoFlop(bet_one);
	setFlickerNoFlop(max);

	add_coin.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent e) {
		//Can only add money on the game over screen
		if (!flop.get()) {
		    //Add credits each time it is pressed
		    setCreditValue(
			    getCreditValue().getValue() + getIncreaseUnit());
		    //Increment counter to cause strategy credits to increase
		    setIncrease_trigger(getIncrease_triggerValue() + 1);
		}
	    }
	});

	//Highlights button on press and resets highlight after
	add_coin.setOnMousePressed(e -> {
	    if (!getFlopValue()) {
		add_coin.setBackground(new Background(highlighted_btn));
	    }

	});

	add_coin.setOnMouseReleased(e -> {
	    if (!getFlopValue()) {
		add_coin.setBackground(new Background(b_fill));
	    }
	});

	sim.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent e) {
		//Can only add money on the game over screen
		if (!flop.get()) {
		    //Turn simulate on
		    simulate = true;
		    //Trigger the deal button over and over to cause simulations
		    for (int i = 0; i < 2 * getSimHands(); i++) {
			deal.fire();
		    }
		    //Turn simulate off
		    simulate = false;
		}
	    }
	});

	/*
	 * If deal is pressed by the user
	 */
	deal.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent e) {
		//Signals transition from flop to final
		if (flop.get()) {
		    //Reverse the value of flop after button is pressed
		    flop.set(false);
		    //Replace non-held cards with new cards
		    for (int i = 0; i < 5; i++) {
			if (hold_cards[i].get() == false) {
			    final_hand[i] = getDeck().drawCard();
			    stack_list[i].makeVisible(final_hand[i],
				    instance_list[i]);
			}
		    }
		    /*
		     * Updates outcome_display and highlighted row,
		     * but only if simulate is false
		     */
		    if (!simulate) {
			analyzeHand();
			outcome_display.setVisible(true);
		    }

		    //Update plot by adding new credit value for each series
		    sim_inst.getSt1().addData(sim_inst.getSt1().getCred());
		    sim_inst.getSt2().addData(sim_inst.getSt2().getCred());
		    sim_inst.getSt3().addData(sim_inst.getSt3().getCred());
		    sim_inst.getSt4().addData(sim_inst.getSt4().getCred());
		    sim_inst.getSt5().addData(sim_inst.getSt5().getCred());
		    sim_inst.getSt6().addData(sim_inst.getSt6().getCred());

		    //Display new game and hide game over on first hand
		    if (!first_hand) {
			/*
			 * If not on the first hand, the new game animation is 
			 * stopped, as it will no longer be visible
			 */
			new_game.setVisible(false);
			ft1.stop();
		    }

		}
		//Signals transition to new hand
		else {
		    //Increment the hand value
		    hand_count++;

		    //Set to blank string so "Nothing" is not shown
		    outcome_display.setText("");
		    outcome_display.setVisible(false);

		    //Removes new game icon once past the first screen
		    new_game.setVisible(false);

		    /*
		     * Charge credits, but only if simulate is false
		     */
		    if (!simulate) {
			getCreditValue().set(getCreditValue().get()
				- highlighted.getValue());
		    }

		    //Reset bookkeeping variables
		    flop.set(true);

		    hold1.set(false);
		    hold2.set(false);
		    hold3.set(false);
		    hold4.set(false);
		    hold5.set(false);

		    setDeck(new StandardDeck());
		    initial_hand = getDeck().drawFiveCards();
		    final_hand = initial_hand;
		    setDisplayCards(initial_hand);
		    payout.set(0);
		    winning_row.set(-1);
		}
	    }
	});

	/*
	 * Can only change bet when game over is displayed, and cycles from
	 * highlighting column 1 to column 5
	 * 
	 */
	bet_one.setOnAction(e -> {
	    if (!flop.get()) {
		if (highlighted.get() == 5) {
		    highlighted.set(1);
		} else {
		    highlighted.set(highlighted.get() + 1);
		}
	    }
	});

	//Can only change bet when game over is displayed
	max.setOnAction(e -> {
	    if (!flop.get()) {
		highlighted.set(5);
	    }
	});

	//Style Buttons
	setButtonStyle(sim, 8.5, 32, 85);
	setButtonStyle(add_coin, 8.5, 32, 60);
	setButtonStyle(deal, 14, 42, 70);
	setButtonStyle(bet_one, 8.5, 32, 87);
	setButtonStyle(max, 8.5, 32, 87);

	layer.getChildren().addAll(sim, add_coin, deal, bet_one, max);
	return layer;
    }

    //SCENE CREATOR
    /**
     * Combines the MATRIX, LABEL, CARD, and BUTTON layers into the final video poker interface Node
     * 
     * @return A JavaFX VBox of the video poker screen
     */
    public VBox videoScreen() {

	loadImages();
	formatStylisticFields();

	flop.set(false);

	//Scene screen;
	VBox root = new VBox(10);
	root.setBackground(new Background(new BackgroundFill(
		Color.rgb(0, 50, 180), new CornerRadii(11), null)));
	root.setEffect(large_shadow);

	//Get Matrix Layer
	HBox mat = matLayer();
	mat.setAlignment(Pos.BASELINE_CENTER);
	mat.setMinSize(426, 151);

	//Get Label Layer
	StackPane lLayer = new StackPane();
	lLayer.setPadding(new Insets(-10, 0, -2, 0));

	getLabels();

	lLayer.getChildren().addAll(text_grid, outcome_display);

	//Get Cards Layer		
	getDisplayCards();

	//Add game over label to display cards
	StackPane cLayer = new StackPane();

	//If flop, hide GAME OVER. Else, display with fading transition
	flop.addListener(e -> {
	    if (flop.get()) {
		game_over.setVisible(false);
	    } else {

		game_over.setVisible(true);
	    }
	});

	cLayer.getChildren().addAll(display_cards, game_over, new_game);

	instance_list = new StackPane[] { c1, c2, c3, c4, c5 };

	//Get Buttons Layer
	HBox buttons = buttonLayer();

	//Put it all together
	root.getChildren().addAll(mat, lLayer, cLayer, buttons);
	//screen = new Scene(root, 540, 380);		
	return root;
    }

}