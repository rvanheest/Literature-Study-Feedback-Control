package nl.tudelft.rvh.simulation.simulations

import scala.concurrent.duration.DurationDouble
import scala.util.Random
import javafx.event.ActionEvent
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import nl.tudelft.rvh.ChartTab
import nl.tudelft.rvh.StaticTestTab
import nl.tudelft.rvh.StepTestTab
import nl.tudelft.rvh.rxscalafx.Observables
import rx.lang.scala.Observable
import rx.lang.scala.schedulers.ComputationScheduler
import nl.tudelft.rvh.simulation.RecursiveFilter
import nl.tudelft.rvh.simulation.AdPublisher
import nl.tudelft.rvh.simulation.Loops

object AdDeliverySimulation {

	class AdStaticTest extends StaticTestTab("Ad Delivery Static Test", "Ad Delivery Static Test", "Price per impression", "Impressions") {
		def seriesName: String = "data"

		def simulation(): Observable[(Double, Int)] = Loops.staticTest(new AdPublisher(100, 2), 20, 100, 10, 5000)
	}

	class AdStepResponse extends StepTestTab("Ad Delivery Step Response", "Ad Delivery Step Response", "x", "y") {

		def seriesName: String = "Ad Delivery step response"

		override def time = super.time take 500
		
		def setpoint(t: Long) = 5.5

		def simulation: Observable[Double] = Loops.stepResponse(time, setpoint, new AdPublisher(100, 2) ++ new RecursiveFilter(0.05))
	}
}