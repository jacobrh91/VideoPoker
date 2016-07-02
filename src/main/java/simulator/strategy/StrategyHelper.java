package simulator.strategy;

import simulator.SimulatorApp;
import simulator.cards.HandAnalyzer;
import simulator.cards.StandardDeck;
import simulator.windows.VideoPokerUI;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class StrategyHelper {

	// Instance of video poker Class
	private VideoPokerUI instance;

	// The strategy controlled by this StrategyHelper Class
	private Strategy strategy = null;

	public void setStrategy(Strategy s) {
		strategy = s;
	}

	public Strategy getStrategy() {
		return strategy;
	}

	// Tracks whether the strategy should be plotted or is inactive
	private boolean active = false;

	// Data set
	private XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();

	public XYChart.Series<Number, Number> getSeries() {
		return series;
	}

	public void setSeries(XYChart.Series<Number, Number> series) {
		this.series = series;
	}

	ChoiceBox<String> choice;

	private Color color;

	// Stylistic Fields
	private static DropShadow small_shadow = new DropShadow(1, 2, 2, Color.BLACK);

	private Rectangle box = new Rectangle(27, 27);

	private IntegerProperty win = new SimpleIntegerProperty();

	private IntegerProperty cred = new SimpleIntegerProperty();

	public void setCred(int value) {
		cred.set(value);
	}

	public int getCred() {
		return cred.get();
	};

	private HBox cred_wrapper = new HBox();
	private HBox win_wrapper = new HBox();

	private VBox vbox_choices = new VBox();

	public StrategyHelper(VideoPokerUI inst, Color box_col, Strategy str) {
		instance = inst;

		// initialize both credit and winnings
		cred.setValue(instance.getCreditValue().getValue());
		win.setValue(0);
		color = box_col;

		// Add the initial data point
		addData(inst.getCreditValue().getValue());

	}

	/*
	 * CHOICE BOX
	 */

	// Creates choices for the choice boxes
	private ObservableList<String> boxChoices() {
		ObservableList<String> list = FXCollections.observableArrayList();
		list.add(null);
		for (int i = 0; i < SimulatorApp.getStrats().size(); i++) {
			list.add(SimulatorApp.getStrats().get(i));
		}
		return list;
	}

	public void setNullChoice() {
		strategy = null;
		choice.getSelectionModel().select(0);
	}

	public HBox strategySelector() {
		HBox root = new HBox(8);
		root.setAlignment(Pos.CENTER);

		ObservableList<String> list = boxChoices();

		choice = new ChoiceBox<String>(list);
		choice.setMinWidth(167);
		choice.setMaxWidth(167);
		choice.setEffect(small_shadow);

		choice.setStyle("-fx-focus-color: transparent;" + "-fx-faint-focus-color: transparent");

		Button remove = new Button("X");

		remove.setAlignment(Pos.CENTER);
		remove.setFont(Font.font("VERDANA", FontWeight.BOLD, 12));
		remove.setEffect(small_shadow);

		// Button is invisible by default
		remove.setVisible(false);

		remove.setOnAction(e -> {
			choice.setValue(null);
			remove.setVisible(false);
			setWholeVisible(false);
			strategy = null;
		});

		// Selects null from the start
		choice.getSelectionModel().select(0);
		// Change strategy after selection
		choice.getSelectionModel().selectedItemProperty().addListener((o, oldv, newv) -> {
			try {
				strategy = new Strategy(newv);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			// Manage Plot series
			getSeries().setName(strategy.name);
			// show remove button
			if (newv == null) {
				// Strategy is set to inactive
				active = false;
				// Remove button is invisible
				remove.setVisible(false);
				strategy = null;
				// Trackers are invisible
				setWholeVisible(false);

			} else {
				// Strategy is set to active
				active = true;
				// Remove button is visible
				remove.setVisible(true);
				// Trackers are visible
				setWholeVisible(true);
			}
		});

		// When "strats" is updated, replace choices in the choice boxes
		SimulatorApp.getStrats().addListener(new ListChangeListener<String>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends String> c) {
				// Prevents checking for strats immediately after the array is
				// cleared
				// Which happens each time the choices are reset
				System.out.println("Change in list detected by stratHelper");
				if (SimulatorApp.getStrats().size() != 0) {

					ObservableList<String> new_list = boxChoices();
					String selected = choice.getValue();
					choice.getItems().removeAll(list);
					choice.getItems().addAll(new_list);

					// Keep selected choice selected after reloading choice box
					if (SimulatorApp.getStrats().contains(selected)) {
						choice.getSelectionModel().select(selected);
					}

				}
				else {
					choice.getItems().removeAll(list);
				}
			}
		});

		Rectangle side_box = new Rectangle(23, 23);

		side_box.setFill(color);
		side_box.setStroke(Color.gray(0.7));
		side_box.setStrokeWidth(3);
		side_box.arcHeightProperty().set(10);
		side_box.arcWidthProperty().set(10);
		side_box.setEffect(small_shadow);

		root.getChildren().addAll(side_box, choice, remove);

		return root;
	}

	/*
	 * TRACKERS
	 */

	public HBox creditTracker() {

		box.setFill(color);
		box.setStroke(Color.BLACK);
		box.setStrokeWidth(1.5);

		HBox box_wrapper = new HBox();
		box_wrapper.setPadding(new Insets(0, 2, 0, 5));
		box_wrapper.setAlignment(Pos.CENTER);
		box_wrapper.setEffect(small_shadow);

		box_wrapper.getChildren().add(box);

		// Create borders for VBox
		BorderStroke bs_left_bottom = new BorderStroke(null, null, Color.BLACK, Color.BLACK, null, null,
				BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, null, new BorderWidths(1.5), new Insets(-1.5));

		BorderStroke bs_left = new BorderStroke(null, null, null, Color.BLACK, null, null, null,
				BorderStrokeStyle.SOLID, null, new BorderWidths(1.5), new Insets(-1.5));

		BorderStroke bs_all = new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(5),
				new BorderWidths(1.5), new Insets(-2.5));

		vbox_choices.setAlignment(Pos.CENTER);
		vbox_choices.setMinWidth(72);

		vbox_choices.setVisible(false);

		Text cred_text = new Text(instance.getCreditValue().getValue().toString());
		Text win_text = new Text("0");

		cred_text.setFont(Font.font("Verdana", 16));
		win_text.setFont(Font.font("Verdana", 16));

		// Created so they can be invisible while borders are still visible
		HBox cred_outer = new HBox();
		HBox win_outer = new HBox();

		/*
		 * cred_wrappers hold borders that show even when inside values are
		 * invisible
		 */
		cred_outer.setAlignment(Pos.CENTER);
		win_outer.setAlignment(Pos.CENTER);

		cred_outer.setBorder(new Border(bs_left_bottom));
		win_outer.setBorder(new Border(bs_left));

		cred_outer.getChildren().add(cred_wrapper);
		win_outer.getChildren().add(win_wrapper);

		cred_wrapper.getChildren().add(cred_text);
		win_wrapper.getChildren().add(win_text);

		vbox_choices.getChildren().addAll(cred_outer, win_outer);

		cred.addListener((o, oldV, newV) -> {
			cred_text.setText(cred.getValue().toString());
		});
		win.addListener((o, oldV, newV) -> {
			win_text.setText(win.getValue().toString());
		});

		HBox hbox = new HBox(6);
		hbox.setAlignment(Pos.CENTER);
		hbox.setBackground(new Background(new BackgroundFill(Color.gray(0.85), new CornerRadii(5), new Insets(-2))));
		hbox.setBorder(new Border(bs_all));
		hbox.setEffect(small_shadow);

		hbox.getChildren().addAll(box_wrapper, vbox_choices);
		return hbox;
	}

	// Sets visibility of win values
	public void setBoxVisibe(boolean bool) {
		win_wrapper.setVisible(bool);
	}

	// Sets visibility of credit tracker
	public void setWholeVisible(boolean bool) {
		vbox_choices.setVisible(bool);
	}

	/*
	 * PLOT SECTION
	 */

	// Add data point to series
	public void addData(Number value) {
		if (active) {
			long hand = instance.getHandCount();
			// Add jitter to points to help distinguish lines in the plot
			value = value.doubleValue() + Math.random() * instance.getHighlighted();

			getSeries().getData().add(new XYChart.Data<Number, Number>(hand, value));
		}
	}

	/*
	 * UTILIZE STRATEGY
	 */

	public void useStrategy(VideoPokerUI inst, SimulatorApp m_inst) {
		// Starts by subtracting credits to play hand
		cred.setValue(cred.getValue() - inst.getHighlighted());
		/*
		 * Obtains the same shuffled deck used in VideoInterface instance and
		 * resets the top card count so the hands will match up
		 */
		StandardDeck deck = inst.getDeck();
		deck.resetTopCard();

		String[] hand = deck.drawFiveCards();
		HandAnalyzer instance = new HandAnalyzer();
		String[] new_hand = strategy.applyStrategy(hand, deck);
		String out_come = instance.handAnalysis(new_hand);
		int payout = m_inst.getPayout(out_come);
		win.set(payout);
		cred.set(cred.getValue() + payout);
	}

}