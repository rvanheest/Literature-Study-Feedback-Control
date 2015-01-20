package nl.tudelft.rvh.simulation

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.TabPane
import javafx.scene.control.TabPane.TabClosingPolicy
import javafx.stage.Stage
import nl.tudelft.rvh.chapter1.BufferClosed
import nl.tudelft.rvh.chapter1.BufferOpen
import nl.tudelft.rvh.chapter1.BufferClosed
import nl.tudelft.rvh.chapter1.BufferOpen

class Simulation extends Application {

	def start(stage: Stage) = {
		val tabPane = new TabPane
		tabPane setTabClosingPolicy TabClosingPolicy.UNAVAILABLE
		tabPane.getTabs addAll(new BufferClosed, new BufferOpen)
		
		stage setScene new Scene(tabPane, 800, 600)
		stage setTitle "Feedback Control Systems - Simulation framework"
		stage show
	}
}

object Simulation extends App {
	Application.launch(classOf[Simulation])
}
