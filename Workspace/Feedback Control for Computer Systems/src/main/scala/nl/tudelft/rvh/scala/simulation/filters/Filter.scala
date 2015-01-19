package nl.tudelft.rvh.scala.simulation.filters

import nl.tudelft.rvh.scala.simulation.Component

trait Filter extends Component {
	override def update(u: Double): Filter
}