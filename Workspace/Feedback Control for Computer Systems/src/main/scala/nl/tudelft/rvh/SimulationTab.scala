package nl.tudelft.rvh

import java.io.File

import scala.collection.JavaConverters.asScalaBufferConverter

import javafx.embed.swing.SwingFXUtils
import javafx.event.ActionEvent
import javafx.geometry.Insets
import javafx.scene.SnapshotParameters
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart.Data
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.Button
import javafx.scene.control.Tab
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter
import javax.imageio.ImageIO
import nl.tudelft.rvh.rxjavafx.JavaFxScheduler
import nl.tudelft.rvh.rxscalafx.Observables
import rx.lang.scala.JavaConversions
import rx.lang.scala.Observable

abstract class SimulationTab(tabName: String, yLabel: String, zLabel: String = "")(implicit DT: Double) extends Tab(tabName) {

	private val print = new Button("Print data")
	private val save = new Button("Save chart")
	
	val (primary, secondary) = simulation
	
	val xAxis = new NumberAxis
	val yAxis = new NumberAxis
	yAxis setLabel yLabel
	
	val baseChart = new LineChart(xAxis, yAxis)
	baseChart.getData add prepareSeries(yLabel, time zip primary)
	baseChart.getData add prepareSeries("Setpoint", time map (t => (t * DT, setpoint(t))))
	
	val chart = new MultiChart(baseChart, Color.RED)
	secondary foreach { obs => chart.addSeries(prepareSeries(zLabel, time zip obs), Color.BLUE) }
	
	val borderPane = new BorderPane(chart)
	borderPane setBottom chart.getLegend
	VBox.setVgrow(borderPane, Priority.ALWAYS)
	
	val box = new VBox(borderPane, bottomBox)
	box setPadding new Insets(5, 5, 15, 5)
	VBox.setVgrow(chart, Priority.ALWAYS)
	
	this setContent box
	
	Observables.fromNodeEvents(print, ActionEvent.ACTION)
		.flatMap(_ => Observable.from(chart.getData))
		.map(series => series.getData.asScala.map(data => data.getXValue + ", " + data.getYValue)
			.foldLeft(series.getName + ":")((sum, current) => sum + "\n" + current))
		.subscribe(println(_))

	Observables.fromNodeEvents(save, ActionEvent.ACTION)
		.map(_ => borderPane.snapshot(new SnapshotParameters, null))
		.map(SwingFXUtils.fromFXImage(_, null))
		.flatMap(img => getFile.map(f => ImageIO.write(img, "png", f)))
		.subscribe
	
	def prepareSeries(name: String, data: Observable[(AnyVal, AnyVal)]) = {
		val series = new Series[Number, Number]
		series setName name
		
		def makeData(x: Number, y: Number) = new Data(x, y)

		data.asInstanceOf[Observable[(Number, Number)]]
			.map((makeData _) tupled _)
			.observeOn(JavaConversions.javaSchedulerToScalaScheduler(JavaFxScheduler.getInstance))
			.subscribe(series.getData add _)

		series
	}
	
	def getFile: Observable[File] = Observable(subscriber => {
		val fileChooser = new FileChooser
		fileChooser setTitle "Save image"
		fileChooser.getExtensionFilters add new ExtensionFilter("PNG files (*.png)", "*.png")

		val x = Option(fileChooser showSaveDialog null)
			.map(f => if (f.getPath endsWith ".png") f else new File(f.getPath + ".png"))
			.foreach(subscriber onNext _)
	})
	
	def bottomBox = new HBox(print, save)
	
	def time: Observable[Long]

	def setpoint(time: Long): Double
	
	def simulation: (Observable[AnyVal], Option[Observable[AnyVal]])
}