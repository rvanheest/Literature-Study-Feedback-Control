package nl.tudelft.rvh;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import nl.tudelft.rvh.rxjavafx.JavaFxScheduler;
import nl.tudelft.rvh.rxjavafx.Observables;
import rx.Observable;

public abstract class ChartTab extends Tab {
	
	private final Button simulate = new Button("Start simulation");
	private final Button print = new Button("Print data");
	private final Button save = new Button("Save chart");
	private final Button clear = new Button("Clear chart");

	public ChartTab(String tabName, String chartTitle, String xName, String yName) {
		super(tabName);

		LineChart<Number, Number> chart = this.initChart(chartTitle, xName, yName);
		chart.setAnimated(false);

		this.print.setDisable(true);
		this.save.setDisable(true);

		Observables.fromNodeEvents(this.simulate, ActionEvent.ACTION)
				.doOnNext(event -> {
					this.simulate.setDisable(true);
					this.clear.setDisable(true);
					this.print.setDisable(true);
					this.save.setDisable(true);
				})
				.map(event -> new Series<Number, Number>())
				.doOnNext(series -> series.setName(this.seriesName()))
				.doOnNext(chart.getData()::add)
				.flatMap(series -> this.runSimulation()
						.observeOn(JavaFxScheduler.getInstance())
						.map(dp -> new Data<>(dp.getX(), dp.getY()))
						.doOnNext(series.getData()::add)
						.doOnCompleted(() -> {
							this.simulate.setDisable(false);
							this.clear.setDisable(false);
							this.print.setDisable(false);
							this.save.setDisable(false);
						}))
				.subscribe(t -> {},
						e -> e.printStackTrace(),
						() -> {});
		
		Observables.fromNodeEvents(this.clear, ActionEvent.ACTION)
				.subscribe(event -> chart.getData().clear());

		Observables.fromNodeEvents(this.print, ActionEvent.ACTION)
				.flatMap(event -> Observable.from(chart.getData()))
				.map(series -> series.getData().stream()
						.map(data -> data.getXValue() + "," + data.getYValue())
						.reduce(series.getName() + ":", (sum, current) -> sum + "\n" + current))
				.subscribe(System.out::println);

		Observables.fromNodeEvents(this.save, ActionEvent.ACTION)
				.map(event -> chart.snapshot(new SnapshotParameters(), null))
				.map(img -> SwingFXUtils.fromFXImage(img, null))
				.flatMap(img -> this.getFile()
						.<Boolean> flatMap(f -> Observable.create(subscriber -> {
							try {
								subscriber.onNext(ImageIO.write(img, "png", f));
							}
							catch (IOException e) {
								subscriber.onError(e);
							}
						}))).subscribe();

		this.setContent(new VBox(chart, this.bottomBox()));
	}

	private Observable<File> getFile() {
		return Observable.create(subscriber -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Save Image");
			fileChooser.getExtensionFilters()
					.add(new ExtensionFilter("PNG files (*.png)", "*.png"));
			Optional.ofNullable(fileChooser.showSaveDialog(null))
					.map(f -> !f.getPath().endsWith(".png") ? new File(f.getPath() + ".png") : f)
					.ifPresent(subscriber::onNext);
		});
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

	public HBox bottomBox() {
		return new HBox(this.simulate, this.clear, this.print, this.save);
	}

	public abstract String seriesName();

	public abstract Observable<Tuple<Number, Number>> runSimulation();
}
