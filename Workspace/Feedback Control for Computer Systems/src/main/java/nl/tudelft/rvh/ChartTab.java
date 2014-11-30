package nl.tudelft.rvh;

import javafx.event.ActionEvent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import nl.tudelft.rvh.rxjavafx.Observables;
import rx.Observable;

public abstract class ChartTab extends Tab {

	public ChartTab(String tabName, String chartTitle, String xName, String yName) {
		super(tabName);

		LineChart<Number, Number> chart = this.initChart(chartTitle, xName, yName);
		chart.setCreateSymbols(false);

		Button button = new Button("Start simulation");
		Observables.fromNodeEvents(button, ActionEvent.ACTION)
				.doOnNext(event -> chart.getData().clear())
				.map(event -> new Series<Number, Number>())
				.doOnNext(series -> series.setName(this.seriesName()))
				.doOnNext(chart.getData()::add)
				.flatMap(series -> this.runSimulation()
						.doOnNext(data -> data.setNode(new HoveredThresholdNode("("
								+ data.getXValue() + ", " + data.getYValue() + ")")))
						.doOnNext(series.getData()::add))
				.subscribe();

		this.setContent(new VBox(chart, button));
	}

	private LineChart<Number, Number> initChart(String title, String xName, String yName) {
		NumberAxis xAxis = new NumberAxis();
		NumberAxis yAxis = new NumberAxis();

		xAxis.setLabel(xName);
		yAxis.setLabel(yName);

		LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
		chart.setTitle(title);

		return chart;
	}

	public abstract String seriesName();

	public abstract Observable<Data<Number, Number>> runSimulation();

	private class HoveredThresholdNode extends StackPane {

		private HoveredThresholdNode(String value) {
			Observables.fromNodeEvents(this, MouseEvent.MOUSE_ENTERED)
					.map(event -> this.createDataThresholdLabel(value))
					.doOnNext(this.getChildren()::setAll)
					.doOnNext(label -> this.toFront())
					.subscribe();
			Observables.fromNodeEvents(this, MouseEvent.MOUSE_EXITED)
					.subscribe(event -> this.getChildren().clear());
		}

		private Label createDataThresholdLabel(String value) {
			Label label = new Label(value);
			label.getStyleClass().addAll("default-color0",
					"chart-line-symbol", "chart-series-line");
			label.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
			label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);

			return label;
		}
	}
}
