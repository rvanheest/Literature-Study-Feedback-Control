package nl.tudelft.rvh

import java.io.File

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.concurrent.duration.DurationInt

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
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter
import javax.imageio.ImageIO
import nl.tudelft.rvh.rxjavafx.JavaFxScheduler
import nl.tudelft.rvh.rxscalafx.Observables
import rx.lang.scala.JavaConversions
import rx.lang.scala.Observable
import rx.lang.scala.ObservableExtensions
import rx.lang.scala.schedulers.ComputationScheduler

abstract class SimulationTab(tabName: String, chartTitle: String, xName: String, yName: String)(implicit DT: Double = 1.0) extends Tab(tabName) {

	private val simulate = new Button("Start simulation")
	private val print = new Button("Print data")
	private val save = new Button("Save chart")
	private val clear = new Button("Clear chart")
	private var series = Map[String, Series[Number, Number]]()

	val chart = initChart(chartTitle, xName, yName)
	chart setAnimated false
	chart setCreateSymbols false

	initSetpointSeries()

	print setDisable true
	save setDisable true

	val box = new VBox(chart, bottomBox)
	box.setPadding(new Insets(5, 5, 15, 5))
	VBox.setVgrow(chart, Priority.ALWAYS)

	this setContent box
	
	Observables.fromNodeEvents(simulate, ActionEvent.ACTION)
		.doOnNext(event => {
			simulate setDisable true
			clear setDisable true
			print setDisable true
			save setDisable true
		})
		.flatMap(_ => this.runSimulation
			.observeOn(JavaConversions.javaSchedulerToScalaScheduler(JavaFxScheduler.getInstance))
			.doOnNext {
				case (_, (name, _)) => if (!series.contains(name)) {
					val serie = new Series[Number, Number]
					serie setName name
					chart.getData add serie
					
					series += (name -> serie)
				}
			}
			.doOnNext { case (time, (name, value)) => series(name).getData add new Data(time, value) }
			.doOnCompleted({
				simulate setDisable false
				clear setDisable false
				print setDisable false
				save setDisable false
			}))
		.doOnError(_ printStackTrace)
		.subscribe

	Observables.fromNodeEvents(clear, ActionEvent.ACTION)
		.doOnNext(_ => chart.getData.clear)
		.subscribe(_ => initSetpointSeries)

	Observables.fromNodeEvents(print, ActionEvent.ACTION)
		.flatMap(_ => Observable.from(chart.getData asScala))
		.map(series => series.getData.asScala.map(data => data.getXValue + ", " + data.getYValue)
			.foldLeft(series.getName + ":")((sum, current) => sum + "\n" + current))
		.subscribe(println(_))

	Observables.fromNodeEvents(save, ActionEvent.ACTION)
		.map(_ => chart.snapshot(new SnapshotParameters, null))
		.map(SwingFXUtils.fromFXImage(_, null))
		.flatMap(img => getFile.map(f => ImageIO.write(img, "png", f)))
		.subscribe

	def initChart(title: String, xName: String, yName: String) = {
		val xAxis = new NumberAxis
		val yAxis = new NumberAxis
		val chart = new LineChart(xAxis, yAxis)

		xAxis setLabel xName
		yAxis setLabel yName
		chart setTitle title

		chart
	}

	def initSetpointSeries() = {
		val serie = new Series[Number, Number]
		val name = "Setpoint"
		serie setName name
		chart.getData add serie
		
		series += (name -> serie)

		simulate setDisable true
		clear setDisable true
		print setDisable true
		save setDisable true

		time.map(t => (t * DT, setpoint(t)))
			.onBackpressureBuffer
			.map { case (time, setpoint) => new Data[Number, Number](time, setpoint) }
			.observeOn(JavaConversions.javaSchedulerToScalaScheduler(JavaFxScheduler.getInstance))
			.doOnCompleted({
				simulate setDisable false
				clear setDisable false
				print setDisable false
				save setDisable false
			})
			.subscribe(serie.getData add _)
	}

	def getFile: Observable[File] = Observable(subscriber => {
		val fileChooser = new FileChooser
		fileChooser setTitle "Save image"
		fileChooser.getExtensionFilters add new ExtensionFilter("PNG files (*.png)", "*.png")

		val x = Option(fileChooser showSaveDialog null)
			.map(f => if (f.getPath endsWith ".png") f else new File(f.getPath + ".png"))
			.foreach(subscriber onNext _)
	})

	def bottomBox = new HBox(simulate, clear, print, save)

	def time = Observable interval (50 milliseconds, ComputationScheduler())

	def setpoint(time: Long): Double

	def runSimulation: Observable[(Number, (String, Number))] = {
		time.map(DT *).onBackpressureBuffer.zip(simulation)
			.flatMap { case (t, map) => map.toObservable map ((t, _)) }
			.asInstanceOf[Observable[(Number, (String, Number))]]
	}

	def simulation: Observable[Map[String, AnyVal]]
}
