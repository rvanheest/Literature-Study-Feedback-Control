package nl.tudelft.rvh.simulation.controller

import nl.tudelft.rvh.simulation.Component

trait Controller extends Component {
	override def update(error: Double): Controller 
}