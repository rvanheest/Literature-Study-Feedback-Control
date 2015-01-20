package nl.tudelft.rvh.scala.simulation.plant

import nl.tudelft.rvh.scala.simulation.Component

trait Plant extends Component {
	override def update(u: Double): Plant
}