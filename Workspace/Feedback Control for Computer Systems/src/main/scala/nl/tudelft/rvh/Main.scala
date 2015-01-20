package nl.tudelft.rvh.scala

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.TabPane
import javafx.scene.control.TabPane.TabClosingPolicy
import javafx.stage.Stage
import nl.tudelft.rvh.scala.chapter1.BufferClosed
import nl.tudelft.rvh.scala.chapter1.BufferOpen
import nl.tudelft.rvh.scala.chapter2.CacheCumulative
import nl.tudelft.rvh.scala.chapter2.CacheNonCumulative
import nl.tudelft.rvh.scala.chapter2.CacheSmallCumulative
import nl.tudelft.rvh.scala.chapter2.CacheSmallNonCumulative
import nl.tudelft.rvh.scala.chapter3.CacheDelay
import nl.tudelft.rvh.scala.chapter3.ExampleWithDelay
import nl.tudelft.rvh.scala.chapter4.OnOffController
import nl.tudelft.rvh.scala.chapter4.PIController
import nl.tudelft.rvh.scala.chapter4.PIDController
import nl.tudelft.rvh.scala.chapter4.ProportionalController

class Main extends Application {

	def start(stage: Stage) = {
		val tabPane = new TabPane
		tabPane setTabClosingPolicy TabClosingPolicy.UNAVAILABLE
		tabPane.getTabs addAll(new PIDController, new PIController, new ProportionalController, new OnOffController,	// chapter 4
				new CacheDelay, new ExampleWithDelay,	// chapter 3
				new CacheSmallCumulative, new CacheSmallNonCumulative,
				new CacheCumulative, new CacheNonCumulative,	// chapter 2
				new BufferClosed, new BufferOpen)	// chapter 1
		
		stage setScene new Scene(tabPane, 800, 600)
		stage setTitle "Feedback Control Systems - Simulation framework"
		stage show
	}
}

object Simulation extends App {
	Application.launch(classOf[Main])
}