package simulator.windows;

import java.io.File;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import simulator.SimulatorApp;

public class ManagementUI {

    private static VBox vbox;

    private static void makeRow(String name) {
	HBox root = new HBox(10);

	HBox skin = new HBox();
	Text txt = new Text(name);
	skin.setAlignment(Pos.CENTER);
	skin.setPrefWidth(200);
	skin.setBackground(new Background(new BackgroundFill(Color.gray(0.92, 0.7), new CornerRadii(10), null)));
	skin.getChildren().add(txt);

	Button delete = new Button("Delete");
	delete.setOnAction(e -> {
	    StringBuilder string = new StringBuilder(System.getProperty("user.dir"));
	    string.append("/src/main/resources/Saved_Strategies/");
	    string.append(name);
	    File file = new File(string.toString());
	    file.delete();
	    vbox.getChildren().remove(root);
	  //Update the strat list in the main class
	    
	    //Get name without suffix through indexing
	    String base_name = name.substring(0, name.length() - 6);
	    System.out.println(base_name);
	    int position = SimulatorApp.getStrats().indexOf(base_name);
	    //Remove the deleted item from the list
	    SimulatorApp.getStrats().remove(position);
	});

	root.getChildren().addAll(skin, delete);
	vbox.getChildren().add(root);
    }

    public static Stage getStage() {
	Stage stage;

	ScrollPane scroll = new ScrollPane();
	scroll.setVbarPolicy(ScrollBarPolicy.ALWAYS);

	vbox = new VBox(10);
	vbox.setPadding(new Insets(10));
	//Folder where strategy files are contained
	File folder = new File(
	    System.getProperty("user.dir") + "/src/main/resources/Saved_Strategies/");

	//List of all of the files in the folder
	File[] file_list = folder.listFiles();

	for (int i = 0; i < file_list.length; i++) {
	    //Adds the row to the vbox
	    makeRow(file_list[i].getName());
	}
	scroll.setContent(vbox);
	Scene scene = new Scene(scroll, 310, 300);

	stage = new Stage();
	stage.setScene(scene);
	return stage;
    }

}
