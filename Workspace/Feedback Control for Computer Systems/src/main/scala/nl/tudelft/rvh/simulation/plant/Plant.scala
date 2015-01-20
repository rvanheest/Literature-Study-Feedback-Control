package nl.tudelft.rvh.simulation.plant

import nl.tudelft.rvh.simulation.Component

trait Plant extends Component {
	override def update(u: Double): Plant
}