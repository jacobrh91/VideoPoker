package simulator;

import java.io.File;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import simulator.plotting.Plot;
import simulator.strategy.StrategyHelper;
import simulator.windows.ManagementUI;
import simulator.windows.NewStrategyUI;
import simulator.windows.VideoPokerUI;
import style_sheets.StyleSheetsLoader;

public class SimulatorApp extends Application {

	// Initial credits
	private int initial_creds = 100;

	// Instance of the video poker screen
	private VideoPokerUI vP_inst = new VideoPokerUI(initial_creds, this);

	/*
	 * Available strategies: list of all of the strategies found in the
	 * Saved_Strategies folder. If one is added, this list is updated
	 */
	private static ObservableList<String> strats = FXCollections.observableArrayList();

	public static void setStrats(ObservableList<String> strats) {
		SimulatorApp.strats = strats;
	}

	public static ObservableList<String> getStrats() {
		return strats;
	}

	// Stylistic fields
	private Color back_color = Color.rgb(0, 50, 180);
	private Color back = Color.gray(0.8);
	private Color trim = Color.GRAY;
	private Color color1 = Color.GOLD;
	private Color color2 = Color.rgb(205, 155, 29);

	private static DropShadow large_shadow = new DropShadow(9, 5, 3, Color.BLACK);
	private static DropShadow small_shadow = new DropShadow(1, 3, 2, Color.BLACK);

	private BorderStroke border_stroke = new BorderStroke(trim, BorderStrokeStyle.SOLID, new CornerRadii(10),
			new BorderWidths(2));

	/*
	 * Creates 6 instances of the strategy helper class, which controls the
	 * strategy choice boxes, colors, credit trackers, and contains a method
	 * that allows points to be added to the line chart
	 */
	private final StrategyHelper st_helper1 = new StrategyHelper(vP_inst, Color.rgb(228, 26, 28), null);
	private final StrategyHelper st_helper2 = new StrategyHelper(vP_inst, Color.rgb(55, 126, 184), null);
	private final StrategyHelper st_helper3 = new StrategyHelper(vP_inst, Color.rgb(77, 175, 74), null);
	private final StrategyHelper st_helper4 = new StrategyHelper(vP_inst, Color.rgb(152, 78, 163), null);
	private final StrategyHelper st_helper5 = new StrategyHelper(vP_inst, Color.rgb(255, 127, 0), null);
	private final StrategyHelper st_helper6 = new StrategyHelper(vP_inst, Color.rgb(247, 129, 191), null);

	public StrategyHelper getSt1() {
		return st_helper1;
	}

	public StrategyHelper getSt2() {
		return st_helper2;
	}

	public StrategyHelper getSt3() {
		return st_helper3;
	}

	public StrategyHelper getSt4() {
		return st_helper4;
	}

	public StrategyHelper getSt5() {
		return st_helper5;
	}

	public StrategyHelper getSt6() {
		return st_helper6;
	}

	private StrategyHelper[] strat_helpers = new StrategyHelper[] { st_helper1, st_helper2, st_helper3, st_helper4,
			st_helper5, st_helper6 };

	// The output matrix is created here so the winning values can be calculated
	int[][] highlight_matrix = new int[][] { vP_inst.column1, vP_inst.column2, vP_inst.column3, vP_inst.column4,
			vP_inst.column5 };

	/*
	 * Calculates the pay out with the given outcome and the highlighted column
	 * found in the instance of the video poker Class
	 */
	public int getPayout(String outcome) {
		/*
		 * column in highlight matrix, subtract 1 because 0th column contains
		 * the names in the matrix
		 */
		int column = vP_inst.getHighlighted() - 1;
		int row, payout;
		switch (outcome) {
		case "Royal Flush": {
			row = 0;
			break;
		}
		case "Straight Flush": {
			row = 1;
			break;
		}
		case "Four-of-a-Kind": {
			row = 2;
			break;
		}
		case "Full House": {
			row = 3;
			break;
		}
		case "Flush": {
			row = 4;
			break;
		}
		case "Straight": {
			row = 5;
			break;
		}
		case "Three-of-a-Kind": {
			row = 6;
			break;
		}
		case "Two Pairs": {
			row = 7;
			break;
		}
		case "One Pair: Royal": {
			row = 8;
			break;
		}
		default: {
			row = -1;
			break;
		}
		}
		if (row == -1)
			payout = 0;
		else {
			payout = highlight_matrix[column][row];
		}
		return payout;
	}

	// Updates the strategies available from the choice box
	public static void updateStrats() throws URISyntaxException {

		System.out.println(System.getProperty("user.dir"));

		// Folder where strategy files are contained
		File folder = new File(System.getProperty("user.dir") + "/src/main/resources/Saved_Strategies/");

		// If the folder doesn't exist, create it
		if (!folder.exists())
			folder.mkdir();

		// List of all of the files in the folder
		File[] file_list = folder.listFiles();

		// Add all of the names of files with the .strat suffix to the list
		for (int i = 0; i < file_list.length; i++) {
			if (file_list[i].isFile()) {
				String file = file_list[i].getName();
				Pattern r = Pattern.compile("(.+)(\\.strat)");
				Matcher m = r.matcher(file);

				if (m.find()) {
					// Add the strategy if it is not already present
					if (!getStrats().contains(m.group(1))) {
						getStrats().add(m.group(1));
					}
				}
			}
		}
		System.out.print("List refilled: ");
	}

	// Adds increase unit to all of the strategies
	private void increaseCredits() {
		st_helper1.setCred(st_helper1.getCred() + vP_inst.getIncreaseUnit());
		st_helper2.setCred(st_helper2.getCred() + vP_inst.getIncreaseUnit());
		st_helper3.setCred(st_helper3.getCred() + vP_inst.getIncreaseUnit());
		st_helper4.setCred(st_helper4.getCred() + vP_inst.getIncreaseUnit());
		st_helper5.setCred(st_helper5.getCred() + vP_inst.getIncreaseUnit());
		st_helper6.setCred(st_helper6.getCred() + vP_inst.getIncreaseUnit());
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		/*
		 * VIDEO POKER SCREEN
		 */

		VBox screen = vP_inst.videoScreen();
		screen.setBorder(new Border(border_stroke));

		/*
		 * MANAGEMENT
		 */

		// Strategy Text
		Text label = new Text("Strategies:");

		label.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
		label.setFill(color1);
		label.setStroke(color2);
		label.setStrokeWidth(1.5);
		label.setEffect(large_shadow);

		// Buttons
		Button new_strat = new Button("New Strategy");

		// Opens strategy creation window
		new_strat.setOnAction(e -> {
			Stage stage = NewStrategyUI.getStage();
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(primaryStage);
			stage.showAndWait();
			try {
				updateStrats();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		Button manage = new Button("Manage Strategies");

		manage.setOnAction(e -> {
			Stage stage = ManagementUI.getStage();
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(primaryStage);
			stage.showAndWait();
			try {
				updateStrats();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		// Format buttons with same formatting as in the video poker Class
		vP_inst.setButtonStyle(new_strat, 11, 30, 10);
		vP_inst.setButtonStyle(manage, 10, 30, 10);

		// Space for buttons
		HBox buttons = new HBox(10);
		buttons.getChildren().addAll(new_strat, manage);

		// Creates space where the Strategy Text and management buttons go
		VBox label_space = new VBox(10);
		label_space.setBackground(new Background(new BackgroundFill(back_color, new CornerRadii(11), null)));

		label_space.setEffect(large_shadow);
		label_space.setBorder(new Border(border_stroke));
		label_space.setPadding(new Insets(0, 0, 7, 7));

		label_space.getChildren().addAll(label, buttons);

		/*
		 * STRATEGY SELECTION
		 */

		// Creates space where the strategy selection choice boxes go
		VBox strat_section = new VBox(21.5);
		strat_section.setBackground(new Background(new BackgroundFill(back_color, new CornerRadii(11), null)));

		strat_section.setPadding(new Insets(10));
		strat_section.setBorder(new Border(border_stroke));
		strat_section.setEffect(large_shadow);

		/*
		 * Adds rows of the strategy selection boxes for each instance of the
		 * StrategyHelper Class
		 */

		strat_section.getChildren().addAll(st_helper1.strategySelector(), st_helper2.strategySelector(),
				st_helper3.strategySelector(), st_helper4.strategySelector(), st_helper5.strategySelector(),
				st_helper6.strategySelector());

		/*
		 * CREDIT TRACKING
		 */

		Text cred_lbl = new Text("Credits:");

		cred_lbl.setFill(color1);
		cred_lbl.setStroke(color2);
		cred_lbl.setStrokeWidth(1);
		cred_lbl.setEffect(small_shadow);
		cred_lbl.setFont(Font.font("Verdana", FontWeight.BOLD, 13));

		Text win_lbl = new Text("Wins:");

		win_lbl.setFill(color1);
		win_lbl.setStroke(color2);
		win_lbl.setStrokeWidth(1);
		win_lbl.setEffect(small_shadow);
		win_lbl.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

		VBox labels = new VBox(2);
		labels.getChildren().addAll(cred_lbl, win_lbl);
		labels.setAlignment(Pos.CENTER_RIGHT);

		// Creates box for the credit trackers to go
		HBox credits = new HBox(11);
		credits.setBackground(new Background(new BackgroundFill(back_color, new CornerRadii(10), null)));

		credits.setBorder(new Border(border_stroke));
		credits.setPadding(new Insets(12, 5, 12, 2));
		credits.setEffect(large_shadow);

		// Add the labels first
		credits.getChildren().add(labels);

		// Add each credit tracker
		credits.getChildren().addAll(st_helper1.creditTracker(), st_helper2.creditTracker(), st_helper3.creditTracker(),
				st_helper4.creditTracker(), st_helper5.creditTracker(), st_helper6.creditTracker());

		// Update pay out and credits based on turn of the poker screen
		vP_inst.getFlop().addListener((o, oldv, newv) -> {

			/*
			 * Wins should only be visible when the GAME OVER icon in the video
			 * poker screen is showing
			 */

			if (newv.booleanValue() == true) {
				// Win visibility is turned off
				for (int i = 0; i < strat_helpers.length; i++) {
					strat_helpers[i].setBoxVisibe(false);
				}
			} else {
				for (int i = 0; i < strat_helpers.length; i++) {
					/*
					 * For each strategy, if it is not null apply that strategy
					 * to the hand and update its credits based on the results
					 */
					if (strat_helpers[i].getStrategy() != null) {
						strat_helpers[i].useStrategy(vP_inst, this);
					}
				}
				// Win visibility is turned on
				for (int i = 0; i < strat_helpers.length; i++) {
					strat_helpers[i].setBoxVisibe(true);
				}
			}
		});

		// Add listener to determine when the Add Credits button was pressed
		vP_inst.getIncrease_trigger().addListener(e -> {
			increaseCredits();
		});

		/*
		 * PLOT SECTION
		 */

		// The plot is located in an HBox so the border can be formatted easily
		HBox plot = Plot.linePlot(this);

		plot.setEffect(large_shadow);

		Button reset = new Button("Reset");

		vP_inst.setFlicker(reset);

		vP_inst.setButtonStyle(reset, 11, 20, 50);

		StackPane stack_plot = new StackPane();
		stack_plot.getChildren().addAll(plot, reset);

		StackPane.setAlignment(reset, Pos.BOTTOM_LEFT);
		StackPane.setMargin(reset, new Insets(0, 0, 10, 10));

		reset.setOnAction(e -> {
			// Resets all of the trackers
			st_helper1.setCred(initial_creds);
			st_helper2.setCred(initial_creds);
			st_helper3.setCred(initial_creds);
			st_helper4.setCred(initial_creds);
			st_helper5.setCred(initial_creds);
			st_helper6.setCred(initial_creds);
			vP_inst.setCreditValue(initial_creds);

			// Resets the hand count
			vP_inst.setHandCount(0);

			st_helper1.setNullChoice();
			st_helper2.setNullChoice();
			st_helper3.setNullChoice();
			st_helper4.setNullChoice();
			st_helper5.setNullChoice();
			st_helper6.setNullChoice();
			Plot.clearPlot();

		});

		/*
		 * ROOT NODE CONSTRUCTION
		 */

		GridPane root = new GridPane();
		root.setHgap(20);
		root.setVgap(15);

		// Organize layout
		root.add(label_space, 0, 0);
		root.add(screen, 1, 0, 1, 2);
		root.add(strat_section, 0, 1);
		root.add(credits, 0, 2, 2, 1);
		root.add(stack_plot, 0, 3, 2, 1);

		root.setPadding(new Insets(10));
		root.setBackground(new Background(new BackgroundFill(back, null, null)));

		/*
		 * SCENE SECTION
		 */

		Scene scene = new Scene(root, 838, 750);

		// StyleSheet for plot
		// scene.getStylesheets().add(this.getClass().getResource("LinePlotStyle.css").toExternalForm());
		scene.getStylesheets().add(StyleSheetsLoader.getStyleSheet("LinePlotStyle.css"));

		// Fixes Width
		primaryStage.setMinWidth(838);
		primaryStage.setMaxWidth(838);

		primaryStage.setMinHeight(680);

		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ESCAPE) {
					primaryStage.setFullScreen(false);
				}
			}
		});

		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public static void main(String[] args) {
		System.out.println(System.getProperty("user.dir"));
		// Load the strategies for the first time
		try {
			updateStrats();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		launch(args);
	}

}