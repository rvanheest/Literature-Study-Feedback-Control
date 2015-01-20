package nl.tudelft.rvh.simulation.filters

import nl.tudelft.rvh.simulation.Component

trait Filter extends Component {
	override def update(u: Double): Filter
}