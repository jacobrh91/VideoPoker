package style_sheets;

public class StyleSheetsLoader {

    public static String getStyleSheet(String sheetName) {
	String sheet = StyleSheetsLoader.class.getResource(sheetName).toExternalForm();
	return sheet;
    }
    
}
