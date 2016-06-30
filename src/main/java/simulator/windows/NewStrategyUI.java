package simulator.windows;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import card_images.ResourceLoader;

public class NewStrategyUI {

    //fileName: Name of newly created file or null if file not created
    public static String file_name = null;

    public NewStrategyUI() {
	getStage();
    }

    static CornerRadii radii = new CornerRadii(10);
    static Insets inset = new Insets(-2);

    static BackgroundFill color = new BackgroundFill(Color.WHITE, null, null);
    static BackgroundFill color2 = new BackgroundFill(Color.gray(0.92, 0.7), radii, inset);

    static Font boldfont = Font.font("System", FontWeight.BOLD, 12);

    static Stage thestage;
    static Scene nothing_scene, onelow_scene, oneroyal_scene, save_scene;

    private static int win_height = 370;
    private static int win_width = 465;

    private static String[] nothing = new String[] { "Never", //Royal Flush
	"Never", //Straight Flush
	"Never", //Flush
	"Never", //Straight
	"No", //HighTwoRoyal
	"Exchange", //HighRoyal
	"PrioritizeStraightIfOE"//*Special Case
    };

    private static String[] onelow = new String[] { "OneAway", //Royal Flush
	"OneAway", //Straight Flush
	"OneAway", //Flush
	"OneAway", //Straight
	"Yes", "Yes", "KeepPair" //Others
    };

    private static String[] oneroyal = new String[] { "OneAway", //Royal Flush
	"OneAway", //Straight Flush
	"OneAway", //Flush
	"OneAway", //Straight
	"KeepPair" //Others

    };

    private static void createStrategyFile(String name, Stage stage) throws IOException {
	StringBuilder filename = new StringBuilder(System.getProperty("user.dir"));
	filename.append("/src/main/resources/Saved_Strategies/");
	filename.append(name);
	filename.append(".strat");
	File file = new File(filename.toString());
	if (file.exists()) {
	    Stage pop_up = new Stage();

	    Label label1 = new Label("A strategy named");
	    Label label2 = new Label(name);
	    label2.setFont(Font.font("System", FontWeight.BOLD, 15));
	    Label label3 = new Label("already exists!");

	    Button cancel = new Button("Rename");
	    cancel.setOnAction(new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent e) {
		    file_name = null;
		    pop_up.close();
		}
	    });

	    Button replace = new Button("Replace?");
	    replace.setOnAction(new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent e) {
		    file.delete();
		    //Calls this function again after deleting the file
		    try {
			createStrategyFile(name, stage);
		    }
		    catch (IOException e1) {
			e1.printStackTrace();
		    }
		    file_name = name;
		    pop_up.close();
		}
	    });

	    VBox root = new VBox(10);
	    root.setAlignment(Pos.BASELINE_CENTER);
	    HBox hbox = new HBox(10);
	    hbox.setAlignment(Pos.BASELINE_CENTER);
	    hbox.getChildren().addAll(cancel, replace);
	    root.getChildren().addAll(label1, label2, label3, hbox);

	    Scene new_scene = new Scene(root, 175, 140);
	    pop_up.setScene(new_scene);
	    pop_up.showAndWait();
	}
	else {
	    OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));

	    String header = "nothing\tonelow\toneroyal\n";
	    String rf = nothing[0] + '\t' + onelow[0] + '\t' + oneroyal[0] + '\n';
	    String sf = nothing[1] + '\t' + onelow[1] + '\t' + oneroyal[1] + '\n';
	    String f = nothing[2] + '\t' + onelow[2] + '\t' + oneroyal[2] + '\n';
	    String s = nothing[3] + '\t' + onelow[3] + '\t' + oneroyal[3] + '\n';
	    String fifth = nothing[4] + '\t' + onelow[4] + '\t' + oneroyal[4] + '\n';
	    String sixth = nothing[5] + '\t' + onelow[5] + "\t-\n";
	    String seventh = nothing[6] + '\t' + onelow[6] + "\t-\n";
	    String[] lines = { header, rf, sf, f, s, fifth, sixth, seventh };

	    for (int i = 0; i < lines.length; i++) {
		writer.write(lines[i]);
	    }
	    writer.close();
	    file_name = name;
	    thestage.close();
	}
    }

    private static HBox makeRow() {
	HBox bx = new HBox(10);
	bx.setPadding(new Insets(0, 10, 0, 10));
	bx.setAlignment(Pos.BASELINE_LEFT);
	return bx;
    }

    private static ImageView getCard(String card) {
	Image image = ResourceLoader.getImage(card, 90);
	ImageView iv = new ImageView(image);
	return iv;
    }

    private static RadioButton makerb(ToggleGroup group, boolean pressed, String label, String[] modified, int pos,
	String out) {
	RadioButton rb = new RadioButton(label);
	rb.setOnAction(new EventHandler<ActionEvent>() {
	    public void handle(ActionEvent e) {
		modified[pos] = out;
		System.out.println(modified[pos]);
	    }
	});
	group.getToggles().add(rb);
	//Pressed by default if pressed is true
	rb.setSelected(pressed);
	return rb;
    }

    private static Scene nothingScene(Stage stage) {
	HBox head = makeRow();
	head.setAlignment(Pos.CENTER);
	HBox row1 = makeRow();
	HBox row2 = makeRow();
	HBox row3 = makeRow();
	HBox row4 = makeRow();
	HBox row5 = makeRow();
	HBox row6 = makeRow();

	row1.setBackground(new Background(color2));
	row2.setBackground(new Background(color2));
	row3.setBackground(new Background(color2));
	row4.setBackground(new Background(color2));
	row5.setBackground(new Background(color2));
	row6.setBackground(new Background(color2));

	Label lab = new Label("If Hand is Showing:");
	lab.setFont(Font.font(20));
	lab.setBackground(new Background(color2));
	Label lab_big = new Label("Nothing");
	lab_big.setFont(Font.font("Verdana", FontWeight.BOLD, 30));

	Label lb1 = new Label("Go for Royal Flush if:");
	Label lb2 = new Label("Go for Straight-Flush if:");
	Label lb3 = new Label("Go for Flush if:");
	Label lb4 = new Label("Go for Straight if:");
	Label lb5 = new Label("Keep Highest Two\nIf Both are Royal?");
	Label lb6 = new Label("Keep Highest If Royal?");
	Label lb7 = new Label("If Special Case*?");

	Label[] labs = { lb1, lb2, lb3, lb4, lb5, lb6, lb7 };

	for (int i = 0; i < labs.length; i++)
	    labs[i].setFont(boldfont);

	ChoiceBox<String> chocSp = new ChoiceBox<String>();
	chocSp.setItems(FXCollections.observableArrayList("Prioritize Straight If Open\nEnded & Royal Flush Possible",
	    "Prioritize Straight If\nRoyal Flush Possible", "Prioritize Straight\nIf Open Ended", "Prioritize Straight",
	    "Prioritize Flush"));
	chocSp.setValue("Prioritize Straight\nIf Open Ended");

	chocSp.getSelectionModel().selectedIndexProperty().addListener((r, oldValue, newValue) -> {
	    int nV = (int) newValue;
	    switch (nV) {
	    case 0:
		nothing[6] = "Prioritize Straight If Open\n" + "Ended & Royal Flush Possible";
		break;
	    case 1:
		nothing[6] = "Prioritize Straight If " + "Royal Flush Possible";
		break;
	    case 2:
		nothing[6] = "Prioritize Straight If " + "Open\n Ended";
		break;
	    case 3:
		nothing[6] = "Prioritize Straight";
		break;
	    case 4:
		nothing[6] = "Prioritize Flush";
		break;
	    }
	    System.out.println(nothing[6]);
	});
	chocSp.setMinHeight(45);

	//Create Toggle Groups for hand choices
	ToggleGroup grR = new ToggleGroup();
	ToggleGroup grSF = new ToggleGroup();
	ToggleGroup grF = new ToggleGroup();
	ToggleGroup grS = new ToggleGroup();
	ToggleGroup grH2 = new ToggleGroup();
	ToggleGroup grH = new ToggleGroup();

	//Make radio buttons and assign them to their groups
	ToggleButton rbR1 = makerb(grR, false, "One Card Away", nothing, 0, "OneAway");
	ToggleButton rbRN = makerb(grR, true, "Never", nothing, 0, "Never");
	ToggleButton rbSF1 = makerb(grSF, false, "One Away &\nOpen Ended", nothing, 1, "OneAwayOE");
	ToggleButton rbSF2 = makerb(grSF, false, "One Away", nothing, 1, "OneAway");
	ToggleButton rbSFN = makerb(grSF, true, "Never", nothing, 1, "Never");

	ToggleButton rbF1 = makerb(grF, false, "One Away", nothing, 2, "OneAway");
	ToggleButton rbFN = makerb(grF, true, "Never", nothing, 2, "Never");

	ToggleButton rbS1 = makerb(grS, false, "One Away &\nOpen Ended", nothing, 3, "OneAwayOE");
	ToggleButton rbS2 = makerb(grS, false, "One Away", nothing, 3, "OneAway");
	ToggleButton rbSN = makerb(grS, true, "Never", nothing, 3, "Never");

	ToggleButton rbH2Y = makerb(grH2, false, "Yes", nothing, 4, "Yes");
	ToggleButton rbH2N = makerb(grH2, true, "No", nothing, 4, "No");

	ToggleButton rbHY = makerb(grH, false, "Yes", nothing, 5, "Yes");
	ToggleButton rbHN = makerb(grH, true, "Exchange All", nothing, 5, "Exchange");

	head.getChildren().addAll(lab, lab_big);
	row1.getChildren().addAll(rbR1, rbRN);
	row2.getChildren().addAll(rbSF1, rbSF2, rbSFN);
	row3.getChildren().addAll(rbF1, rbFN);
	row4.getChildren().addAll(rbS1, rbS2, rbSN);
	row5.getChildren().addAll(rbH2Y, rbH2N);
	row6.getChildren().addAll(rbHY, rbHN);

	//Creates pop-up explaining the Special Case
	Button special = new Button("*What is a Special Case?");
	special.setOnAction(e -> {
	    Stage pop_up = new Stage();
	    pop_up.setMinWidth(410);
	    pop_up.initModality(Modality.WINDOW_MODAL);
	    pop_up.initOwner(thestage);
	    Label label = new Label("What is a Special Case?");
	    label.setFont(Font.font("Verdana", FontWeight.BOLD, 15));

	    Text text = new Text();
	    text.setText(
		"In some situations, both a flush and a straight " + "are only one card\naway, but the card that needs"
		    + " to be swapped are not the same.\n\nFor Example:");
	    Text text2 = new Text();
	    text2.setText(
		"In cases like these, you must decide if the" + " straight or the flush\ndraw takes priority.\n");

	    HBox ex_cards = new HBox(10);
	    ImageView c1 = getCard("3_of_spades.png");
	    ImageView c2 = getCard("4_of_spades.png");
	    ImageView c3 = getCard("5_of_hearts.png");
	    ImageView c4 = getCard("6_of_spades.png");
	    ImageView c5 = getCard("King_of_spades.png");
	    ex_cards.getChildren().addAll(c1, c2, c3, c4, c5);

	    Button okay = new Button("Ok");
	    okay.setOnAction(f -> {
		pop_up.close();
	    });

	    VBox vbox = new VBox(8);
	    vbox.setAlignment(Pos.TOP_CENTER);
	    vbox.setPadding(new Insets(10, 10, 10, 10));
	    vbox.getChildren().addAll(label, text, ex_cards, text2, okay);

	    Scene scene = new Scene(vbox, 410, 295);
	    pop_up.setScene(scene);
	    pop_up.showAndWait();
	});

	special.setStyle("-fx-font-size: 7pt;");

	Button cancel = new Button("Cancel");
	cancel.setOnAction(e -> {
	    stage.close();
	});

	Button next = new Button("Next");
	next.setOnAction(e -> {
	    thestage.setScene(onelow_scene);
	});

	HBox bottom = new HBox(60);
	bottom.getChildren().addAll(cancel, next);
	bottom.setAlignment(Pos.BOTTOM_RIGHT);

	GridPane grid = new GridPane();
	grid.setBackground(new Background(color));

	grid.setHgap(10);
	grid.setVgap(9);
	grid.setPadding(new Insets(5, 10, 5, 10));

	//First items at top
	grid.add(head, 0, 0, 2, 1);

	grid.add(lb1, 0, 1);
	grid.add(row1, 1, 1);

	grid.add(lb2, 0, 2);
	grid.add(row2, 1, 2);

	grid.add(lb3, 0, 3);
	grid.add(row3, 1, 3);

	grid.add(lb4, 0, 4);
	grid.add(row4, 1, 4);

	grid.add(lb5, 0, 5);
	grid.add(row5, 1, 5);

	grid.add(lb6, 0, 6);
	grid.add(row6, 1, 6);

	grid.add(lb7, 0, 7);
	grid.add(chocSp, 1, 7);

	grid.add(special, 0, 8);
	grid.add(bottom, 1, 9);

	Scene nothing_scene = new Scene(grid, win_width, win_height);
	return nothing_scene;
    }

    private static Scene onelowScene(Stage stage) {
	HBox head = makeRow();
	head.setAlignment(Pos.CENTER);
	HBox row1 = makeRow();
	HBox row2 = makeRow();
	HBox row3 = makeRow();
	HBox row4 = makeRow();
	HBox row5 = makeRow();
	HBox row6 = makeRow();

	row1.setBackground(new Background(color2));
	row2.setBackground(new Background(color2));
	row3.setBackground(new Background(color2));
	row4.setBackground(new Background(color2));
	row5.setBackground(new Background(color2));
	row6.setBackground(new Background(color2));

	Label lab = new Label("If Hand is Showing:");
	lab.setBackground(new Background(color2));

	lab.setFont(Font.font(20));
	Label lab_big = new Label("One Pair: Low");
	lab_big.setFont(Font.font("Verdana", FontWeight.BOLD, 25));

	Label lb1 = new Label("Go for Royal Flush if:");
	Label lb2 = new Label("Go for Straight-Flush if:");
	Label lb3 = new Label("Go for Flush if:");
	Label lb4 = new Label("Go for Straight if:");
	Label lb5 = new Label("Keep Highest Two\nIf Both are Royal?");
	Label lb6 = new Label("Keep Highest if Royal?");
	Label lb7 = new Label("Other choices");

	Label[] labs = { lb1, lb2, lb3, lb4, lb5, lb6, lb7 };

	for (int i = 0; i < labs.length; i++)
	    labs[i].setFont(boldfont);

	ChoiceBox<String> chocSp = new ChoiceBox<String>();
	chocSp.setItems(FXCollections.observableArrayList("Keep Pair", "Exchange All"));
	chocSp.setValue("Keep Pair");
	chocSp.getSelectionModel().selectedIndexProperty().addListener((r, oldValue, newValue) -> {
	    int nV = (int) newValue;
	    switch (nV) {
	    case 0:
		onelow[4] = "Keep Pair";
		break;
	    case 1:
		onelow[4] = "Exchange All";
		break;
	    }
	});

	//Create Toggle Groups for hand choices
	ToggleGroup grR = new ToggleGroup();
	ToggleGroup grSF = new ToggleGroup();
	ToggleGroup grF = new ToggleGroup();
	ToggleGroup grS = new ToggleGroup();
	ToggleGroup grH2 = new ToggleGroup();
	ToggleGroup grH1 = new ToggleGroup();

	//Make radio buttons and assign them to their groups
	ToggleButton rbR1 = makerb(grR, false, "One Card Away", onelow, 0, "OneAway");
	ToggleButton rbRN = makerb(grR, true, "Never", onelow, 0, "Never");
	ToggleButton rbSF1 = makerb(grSF, false, "One Away &\nOpen Ended", onelow, 1, "OneAwayOE");
	ToggleButton rbSF2 = makerb(grSF, false, "One Away", onelow, 1, "OneAway");
	ToggleButton rbSFN = makerb(grSF, true, "Never", onelow, 1, "Never");

	ToggleButton rbF1 = makerb(grF, false, "One Away", onelow, 2, "OneAway");
	ToggleButton rbFN = makerb(grF, true, "Never", onelow, 2, "Never");

	ToggleButton rbS1 = makerb(grS, false, "One Away &\nOpen Ended", onelow, 3, "OneAwayOE");
	ToggleButton rbS2 = makerb(grS, false, "One Away", onelow, 3, "OneAway");
	ToggleButton rbSN = makerb(grS, true, "Never", onelow, 3, "Never");

	ToggleButton rbH2Y = makerb(grH2, false, "Yes", onelow, 4, "Yes");
	ToggleButton rbH2N = makerb(grH2, true, "No", onelow, 4, "No");

	ToggleButton rbHY = makerb(grH1, false, "Yes", onelow, 5, "Yes");
	ToggleButton rbHN = makerb(grH1, true, "No", onelow, 5, "No");

	head.getChildren().addAll(lab, lab_big);
	row1.getChildren().addAll(rbR1, rbRN);
	row2.getChildren().addAll(rbSF1, rbSF2, rbSFN);
	row3.getChildren().addAll(rbF1, rbFN);
	row4.getChildren().addAll(rbS1, rbS2, rbSN);
	row5.getChildren().addAll(rbH2Y, rbH2N);
	row6.getChildren().addAll(rbHY, rbHN);

	Button cancel = new Button("Cancel");
	cancel.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent e) {
		stage.close();
	    }
	});

	Button back = new Button("Back");
	back.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent e) {
		thestage.setScene(nothing_scene);
	    }
	});

	Button next = new Button("Next");
	next.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent e) {
		thestage.setScene(oneroyal_scene);
	    }
	});

	HBox bottom = new HBox(30);
	bottom.getChildren().addAll(cancel, back, next);
	bottom.setAlignment(Pos.BOTTOM_RIGHT);

	GridPane grid = new GridPane();
	grid.setBackground(new Background(color));

	grid.setHgap(10);
	grid.setVgap(15);
	grid.setPadding(new Insets(5, 10, 5, 10));

	//First items at top
	grid.add(head, 0, 0, 2, 1);

	grid.add(lb1, 0, 1);
	grid.add(row1, 1, 1);

	grid.add(lb2, 0, 2);
	grid.add(row2, 1, 2);

	grid.add(lb3, 0, 3);
	grid.add(row3, 1, 3);

	grid.add(lb4, 0, 4);
	grid.add(row4, 1, 4);

	grid.add(lb5, 0, 5);
	grid.add(row5, 1, 5);

	grid.add(lb6, 0, 6);
	grid.add(row6, 1, 6);

	grid.add(lb7, 0, 7);
	grid.add(chocSp, 1, 7);

	grid.add(bottom, 1, 8);

	Scene onelow_scene = new Scene(grid, win_width, win_height);
	return onelow_scene;
    }

    private static Scene oneroyalScene(Stage stage) {
	HBox head = makeRow();
	head.setAlignment(Pos.CENTER);
	HBox row1 = makeRow();
	HBox row2 = makeRow();
	HBox row3 = makeRow();
	HBox row4 = makeRow();

	row1.setBackground(new Background(color2));
	row2.setBackground(new Background(color2));
	row3.setBackground(new Background(color2));
	row4.setBackground(new Background(color2));

	Label lab = new Label("If Hand is Showing:");
	lab.setBackground(new Background(color2));

	lab.setFont(Font.font(20));
	Label lab_big = new Label("One Pair: Royal");
	lab_big.setFont(Font.font("Verdana", FontWeight.BOLD, 25));

	Label lb1 = new Label("Go for Royal Flush if:");
	Label lb2 = new Label("Go for Straight-Flush if:");
	Label lb3 = new Label("Go for Flush if:");
	Label lb4 = new Label("Go for Straight if:");
	Label lb5 = new Label("Other choices");

	Label[] labs = { lb1, lb2, lb3, lb4, lb5 };

	for (int i = 0; i < labs.length; i++)
	    labs[i].setFont(boldfont);

	ChoiceBox<String> chocSp = new ChoiceBox<String>();
	chocSp.setItems(FXCollections.observableArrayList("Keep Pair", "Exchange All"));
	chocSp.setValue("Keep Pair");

	chocSp.getSelectionModel().selectedIndexProperty().addListener((r, oldValue, newValue) -> {
	    int nV = (int) newValue;
	    switch (nV) {
	    case 0:
		oneroyal[4] = "Keep Pair";
		break;
	    case 1:
		oneroyal[4] = "Exchange All";
		break;

	    }
	    System.out.println(oneroyal[4]);
	});

	//Create Toggle Groups for hand choices
	ToggleGroup grR = new ToggleGroup();
	ToggleGroup grSF = new ToggleGroup();
	ToggleGroup grF = new ToggleGroup();
	ToggleGroup grS = new ToggleGroup();

	//Make radio buttons and assign them to their groups
	ToggleButton rbR1 = makerb(grR, false, "One Card Away", oneroyal, 0, "OneAway");
	ToggleButton rbRN = makerb(grR, true, "Never", oneroyal, 0, "Never");
	ToggleButton rbSF1 = makerb(grSF, false, "One Away &\nOpen Ended", oneroyal, 1, "OneAwayOE");
	ToggleButton rbSF2 = makerb(grSF, false, "One Away", oneroyal, 1, "OneAway");
	ToggleButton rbSFN = makerb(grSF, true, "Never", oneroyal, 1, "Never");

	ToggleButton rbF1 = makerb(grF, false, "One Away", oneroyal, 2, "OneAway");
	ToggleButton rbFN = makerb(grF, true, "Never", oneroyal, 2, "Never");

	ToggleButton rbS1 = makerb(grS, false, "One Away &\nOpen Ended", oneroyal, 3, "OneAwayOE");
	ToggleButton rbS2 = makerb(grS, false, "One Away", oneroyal, 3, "OneAway");
	ToggleButton rbSN = makerb(grS, true, "Never", oneroyal, 3, "Never");

	head.getChildren().addAll(lab, lab_big);
	row1.getChildren().addAll(rbR1, rbRN);
	row2.getChildren().addAll(rbSF1, rbSF2, rbSFN);
	row3.getChildren().addAll(rbF1, rbFN);
	row4.getChildren().addAll(rbS1, rbS2, rbSN);

	Button cancel = new Button("Cancel");
	cancel.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent e) {
		stage.close();
	    }
	});

	Button back = new Button("Back");
	back.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent e) {
		thestage.setScene(onelow_scene);
	    }
	});

	Button next = new Button("Next");
	next.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent e) {
		thestage.setScene(save_scene);
	    }
	});

	HBox bottom = new HBox(30);
	bottom.getChildren().addAll(cancel, back, next);
	bottom.setAlignment(Pos.BOTTOM_RIGHT);

	GridPane grid = new GridPane();
	grid.setBackground(new Background(color));

	grid.setHgap(10);
	grid.setVgap(28);
	grid.setPadding(new Insets(5, 10, 5, 10));

	//First items at top
	grid.add(head, 0, 0, 2, 1);

	grid.add(lb1, 0, 1);
	grid.add(row1, 1, 1);

	grid.add(lb2, 0, 2);
	grid.add(row2, 1, 2);

	grid.add(lb3, 0, 3);
	grid.add(row3, 1, 3);

	grid.add(lb4, 0, 4);
	grid.add(row4, 1, 4);

	grid.add(lb5, 0, 5);
	grid.add(chocSp, 1, 5);

	grid.add(bottom, 1, 6);

	Scene oneroyal_scene = new Scene(grid, win_width, win_height);
	return oneroyal_scene;
    }

    private static Scene saveScene(Stage stage) {
	VBox vbox = new VBox(16);
	vbox.setAlignment(Pos.CENTER);
	Label label = new Label("Other Hands");
	label.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
	Text text = new Text(
	    "For a hand with anything higher than a single " + "pair, each strategy will automatically keep "
		+ "those cards and exchange any that do not " + "contribute to the hand.");
	text.setFont(Font.font("Verdana", 17));
	vbox.setBackground(new Background(color));

	Text text2 = new Text("That's it! Name and save your new strategy:");

	TextField name_tf = new TextField("Untitled");
	name_tf.setMaxWidth(150);

	Button cancel = new Button("Cancel");
	cancel.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent e) {
		file_name = null;
		stage.close();
	    }
	});

	Button back = new Button("Back");
	back.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent e) {
		thestage.setScene(oneroyal_scene);
	    }
	});

	Button save = new Button("Save");
	save.setOnAction(new EventHandler<ActionEvent>() {
	    @Override
	    public void handle(ActionEvent e) {
		//Save strategy as a .strat file
		try {
		    createStrategyFile(name_tf.getText(), stage);
		}
		catch (IOException f) {
		    f.printStackTrace();
		}
		file_name = name_tf.getText();
	    }
	});

	save.defaultButtonProperty();
	HBox hbox = new HBox(10);
	hbox.getChildren().addAll(cancel, back, name_tf, save);
	hbox.setAlignment(Pos.CENTER);
	text.setWrappingWidth(400);
	vbox.getChildren().addAll(label, text, text2, hbox);
	vbox.setBackground(new Background(color));
	Scene scene = new Scene(vbox, win_width, win_height);
	return scene;
    }

    public static Stage getStage() {
	thestage = new Stage();
	thestage.setTitle("Strategy Creator");

	nothing_scene = nothingScene(thestage);
	onelow_scene = onelowScene(thestage);
	oneroyal_scene = oneroyalScene(thestage);
	save_scene = saveScene(thestage);

	thestage.setScene(nothing_scene);
	return thestage;
    }
}