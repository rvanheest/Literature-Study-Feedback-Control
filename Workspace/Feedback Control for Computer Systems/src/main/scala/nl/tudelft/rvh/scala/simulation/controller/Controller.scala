package nl.tudelft.rvh.scala.simulation.controller

import nl.tudelft.rvh.scala.simulation.Component

trait Controller extends Component {
	override def update(error: Double): Controller 
}