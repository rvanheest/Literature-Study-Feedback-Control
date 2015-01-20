package nl.tudelft.rvh.scala.simulation

trait Component {

	def update(u: Double): Component
	def action: Double
}