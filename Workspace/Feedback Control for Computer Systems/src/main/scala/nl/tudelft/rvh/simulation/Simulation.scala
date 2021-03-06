package nl.tudelft.rvh.simulation

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.TabPane
import javafx.scene.control.TabPane.TabClosingPolicy
import javafx.stage.Stage
import nl.tudelft.rvh.simulation.simulations.AdDeliverySimulation.AdStaticTest
import nl.tudelft.rvh.simulation.simulations.AdDeliverySimulation.AdStepResponse
import nl.tudelft.rvh.simulation.simulations.BoilerSimulation
import nl.tudelft.rvh.simulation.simulations.CacheSimulation.CacheClosedLoop
import nl.tudelft.rvh.simulation.simulations.CacheSimulation.CacheStaticTest
import nl.tudelft.rvh.simulation.simulations.CacheSimulation.CacheStepResponse
import nl.tudelft.rvh.simulation.simulations.ServerScalingSimulation.ServerClosedLoop1
import nl.tudelft.rvh.simulation.simulations.ServerScalingSimulation.ServerClosedLoop2
import nl.tudelft.rvh.simulation.simulations.ServerScalingSimulation.ServerClosedLoop3
import nl.tudelft.rvh.simulation.simulations.ServerScalingSimulation.ServerPoolWithLatencySimulation
import nl.tudelft.rvh.simulation.simulations.ServerScalingSimulation.ServerStaticTest
import nl.tudelft.rvh.simulation.simulations.SimpleCacheSimulation
import nl.tudelft.rvh.simulation.simulations.SimpleCacheStaticTest
import nl.tudelft.rvh.simulation.simulations.SimpleCacheStepResponse
import nl.tudelft.rvh.simulation.simulations.ServerScalingSimulation.MyServerClosedLoop

class Simulation extends Application {

	def start(stage: Stage) = {
		val tabPane = new TabPane
		tabPane setTabClosingPolicy TabClosingPolicy.UNAVAILABLE
		tabPane.getTabs addAll(
				new MyServerClosedLoop)
//				new SimpleCacheSimulation, new SimpleCacheStepResponse, new SimpleCacheStaticTest)
//				new ServerPoolWithLatencySimulation)
//				new ServerClosedLoop3, new ServerClosedLoop2, new ServerClosedLoop1, new ServerStaticTest)
//				new AdStepResponse, new AdStaticTest)
//				new CacheClosedLoop, new CacheStepResponse, new CacheStaticTest)
//				new BoilerSimulation)
		
		stage setScene new Scene(tabPane, 1024, 768)
		stage setTitle "Feedback Control Systems - Simulation framework"
		stage show
	}
}

object Simulation extends App {
	Application.launch(classOf[Simulation])
}
