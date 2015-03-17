package nl.tudelft.rvh

import scala.collection.JavaConverters.asScalaBufferConverter
import javafx.scene.chart.XYChart.Series
import javafx.scene.layout.StackPane
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.layout.HBox
import javafx.collections.FXCollections
import scala.collection.mutable.HashMap
import javafx.scene.control.Label
import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.beans.InvalidationListener
import javafx.geometry.Side
import java.util.function.Consumer

class MultiChart(baseChart: LineChart[Number, Number], lineColor: Color, strokeWidth: Double = 3) extends StackPane {

	private val backgroundCharts = FXCollections.observableArrayList[LineChart[Number, Number]]
	private val chartColorMap = new HashMap[LineChart[Number, Number], Color]

	private val yAxisWidth = 60
	private val yAxisSeparation = 20

	chartColorMap += baseChart -> lineColor

	styleBaseChart(baseChart)
	styleChartLine(baseChart, lineColor)
	setFixedAxisWidth(baseChart)

	setAlignment(Pos.BOTTOM_LEFT)

	backgroundCharts addListener new InvalidationListener {
		def invalidated(observable: javafx.beans.Observable) = rebuildChart
	}

	rebuildChart

	private def styleBaseChart(baseChart: LineChart[Number, Number]) = {
		baseChart setCreateSymbols false
		baseChart setLegendVisible false
		baseChart.getXAxis setAnimated false
		baseChart.getYAxis setAnimated false
	}

	private def setFixedAxisWidth(chart: LineChart[Number, Number]) = {
		chart.getYAxis setPrefWidth yAxisWidth
		chart.getYAxis setMaxWidth yAxisWidth
	}

	private def rebuildChart = {
		getChildren clear

		getChildren add baseChart

		baseChart.minWidthProperty bind widthProperty.subtract((yAxisWidth + yAxisSeparation) * backgroundCharts.size)
		baseChart.prefWidthProperty bind widthProperty.subtract((yAxisWidth + yAxisSeparation) * backgroundCharts.size)
		baseChart.maxWidthProperty bind widthProperty.subtract((yAxisWidth + yAxisSeparation) * backgroundCharts.size)

		backgroundCharts forEach new Consumer[LineChart[Number, Number]] {
			def accept(chart: LineChart[Number, Number]) = {
				val hBox = new HBox(chart)
				hBox setAlignment Pos.BOTTOM_LEFT
				hBox.prefHeightProperty bind heightProperty.multiply(0.5)
				hBox.maxHeightProperty bind heightProperty.multiply(0.5)
				hBox.minHeightProperty bind heightProperty.multiply(0.5)
				hBox.prefWidthProperty bind widthProperty

				chart.minWidthProperty bind widthProperty.subtract((yAxisWidth + yAxisSeparation) * backgroundCharts.size)
				chart.prefWidthProperty bind widthProperty.subtract((yAxisWidth + yAxisSeparation) * backgroundCharts.size)
				chart.maxWidthProperty bind widthProperty.subtract((yAxisWidth + yAxisSeparation) * backgroundCharts.size)
				
				chart.translateXProperty bind baseChart.getYAxis.widthProperty
				chart.getYAxis setTranslateX (yAxisWidth + yAxisSeparation) * backgroundCharts.indexOf(chart)

				getChildren add hBox
			}
		}
	}

	def addSeries(series: Series[Number, Number], lineColor: Color) = {
		val xAxis = new NumberAxis
		val yAxis = new NumberAxis

		// style x-axis
		xAxis setLabel baseChart.getXAxis.getLabel
		xAxis setAutoRanging false
		xAxis setVisible false
		xAxis setOpacity 0
		xAxis.lowerBoundProperty bind baseChart.getXAxis.asInstanceOf[NumberAxis].lowerBoundProperty
		xAxis.upperBoundProperty bind baseChart.getXAxis.asInstanceOf[NumberAxis].upperBoundProperty
		xAxis.tickUnitProperty bind baseChart.getXAxis.asInstanceOf[NumberAxis].tickUnitProperty

		// style y-axis
		yAxis setSide Side.RIGHT
		yAxis setLabel series.getName

		// create chart
		val lineChart = new LineChart(xAxis, yAxis)
		lineChart setAnimated false
		lineChart setLegendVisible false
		lineChart.getData add series

		// style background of chart
		styleChartLine(lineChart, lineColor)
		val contentBackground = lineChart lookup ".chart-content" lookup ".chart-plot-background"
		contentBackground setStyle "-fx-background-color: transparent;"

		lineChart setVerticalZeroLineVisible false
		lineChart setHorizontalZeroLineVisible false
		lineChart setVerticalGridLinesVisible false
		lineChart setHorizontalGridLinesVisible false
		lineChart setCreateSymbols false

		setFixedAxisWidth(lineChart)

		chartColorMap += lineChart -> lineColor
		backgroundCharts add lineChart
	}

	private def toRGBCode(color: Color): String = String.format("#%02X%02X%02X",
		Integer.valueOf((color.getRed * 255).toInt),
		Integer.valueOf((color.getGreen * 255).toInt),
		Integer.valueOf((color.getBlue * 255).toInt))

	private def styleChartLine(chart: LineChart[Number, Number], color: Color) = {
		chart.getYAxis lookup ".axis-label" setStyle "-fx-text-fill: " + toRGBCode(color) + ";"
		chart lookup ".chart-series-line" setStyle "-fx-stroke: " + toRGBCode(color) + "; -fx-stroke-width: " + strokeWidth + "px;"
	}

	def getLegend = {
		val hBox = new HBox

		val label = new Label(baseChart.getYAxis getLabel)
		label setStyle "-fx-text-fill: " + toRGBCode(chartColorMap(baseChart)) + ";"
		hBox.getChildren add label

		backgroundCharts forEach new Consumer[LineChart[Number, Number]] {
			def accept(chart: LineChart[Number, Number]) = {
				val label = new Label(chart.getYAxis getLabel)
				label setStyle "-fx-text-fill: " + toRGBCode(chartColorMap(chart)) + ";"
				hBox.getChildren add label
			}
		}

		hBox setAlignment Pos.CENTER
		hBox setSpacing 20
		hBox setStyle "-fx-padding: 0 10 20 10"

		hBox
	}
	
	def getData = baseChart.getData.asScala ++ backgroundCharts.asScala.flatMap(_.getData asScala)
}