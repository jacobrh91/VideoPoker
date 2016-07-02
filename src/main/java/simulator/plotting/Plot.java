package simulator.plotting;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import simulator.SimulatorApp;

public class Plot {

	private static HBox root;

	private static XYChart.Series<Number, Number> series1;
	private static XYChart.Series<Number, Number> series2;
	private static XYChart.Series<Number, Number> series3;
	private static XYChart.Series<Number, Number> series4;
	private static XYChart.Series<Number, Number> series5;
	private static XYChart.Series<Number, Number> series6;

	private static LineChart<Number, Number> chart;

	private static DropShadow small_shadow = new DropShadow(1, 3, 2, Color.BLACK);

	public static void clearPlot() {

		series1.getData().clear();
		series2.getData().clear();
		series3.getData().clear();
		series4.getData().clear();
		series5.getData().clear();
		series6.getData().clear();

	}

	@SuppressWarnings("unchecked")
	public static HBox linePlot(SimulatorApp sim_inst) {
		root = new HBox();
		root.setBackground(new Background(new BackgroundFill(Color.rgb(0, 50, 180), new CornerRadii(11), null)));

		root.setBorder(new Border(
				new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(2))));

		// defining the axes
		final NumberAxis x_axis = new NumberAxis();
		final NumberAxis y_axis = new NumberAxis();

		x_axis.setLabel("Hand");
		y_axis.setLabel("Credits");

		// creating the chart
		chart = new LineChart<Number, Number>(x_axis, y_axis);
		chart.animatedProperty().set(false);

		chart.setCreateSymbols(false);
		chart.legendVisibleProperty().set(false);

		// adding a series
		series1 = sim_inst.getSt1().getSeries();
		series2 = sim_inst.getSt2().getSeries();
		series3 = sim_inst.getSt3().getSeries();
		series4 = sim_inst.getSt4().getSeries();
		series5 = sim_inst.getSt5().getSeries();
		series6 = sim_inst.getSt6().getSeries();

		chart.getData().addAll(series1, series2, series3, series4, series5, series6);

		chart.setPrefWidth(810);
		chart.setEffect(small_shadow);

		root.getChildren().add(chart);
		return root;
	}
}
