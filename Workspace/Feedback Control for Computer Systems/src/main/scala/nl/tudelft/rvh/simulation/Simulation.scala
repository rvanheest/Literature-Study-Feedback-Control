package nl.tudelft.rvh.simulation

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.TabPane
import javafx.scene.control.TabPane.TabClosingPolicy
import javafx.stage.Stage
import nl.tudelft.rvh.simulation.AdDelivery.AdStaticTest
import nl.tudelft.rvh.simulation.AdDelivery.AdStepResponse
import nl.tudelft.rvh.simulation.CacheSimulation.CacheClosedLoop
import nl.tudelft.rvh.simulation.CacheSimulation.CacheClosedLoopJumps
import nl.tudelft.rvh.simulation.CacheSimulation.CacheStaticTest
import nl.tudelft.rvh.simulation.CacheSimulation.CacheStepResponse

class Simulation extends Application {

	def start(stage: Stage) = {
		val tabPane = new TabPane
		tabPane setTabClosingPolicy TabClosingPolicy.UNAVAILABLE
		tabPane.getTabs addAll(new AdStepResponse, new AdStaticTest,
				new CacheClosedLoopJumps, new CacheClosedLoop, new CacheStepResponse, new CacheStaticTest,
				new BoilerSim)
		
		stage setScene new Scene(tabPane, 800, 600)
		stage setTitle "Feedback Control Systems - Simulation framework"
		stage show
	}
}

object Simulation extends App {
	Application.launch(classOf[Simulation])
}
