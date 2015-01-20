package nl.tudelft.rvh.simulation

trait Component {

	def update(u: Double): Component
	def action: Double
}