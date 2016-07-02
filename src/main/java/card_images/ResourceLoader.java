package card_images;

import java.net.URL;

import javafx.scene.image.Image;

public class ResourceLoader {

	public static Image getImage(String fileName) {
		URL resource = ResourceLoader.class.getResource(fileName);
		Image image = new Image(resource.toExternalForm(), 125, 125, true, true);
		return image;
	}

	public static Image getImage(String fileName, int size) {
		URL resource = ResourceLoader.class.getResource(fileName);
		Image image = new Image(resource.toExternalForm(), size, size, true, true);
		return image;
	}

}
