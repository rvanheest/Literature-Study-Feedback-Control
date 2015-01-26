package nl.tudelft.rvh.simulation

trait Component {

	def update(u: Double): Component
	def action: Double

	def ++(other: Component): Component = {
		val self = this
		new Component {
			def update(u: Double): Component = {
				val thisComp = self.update(u)
				thisComp ++ other.update(thisComp.action)
			}
			
			def action: Double = other.action
		}
	}
}