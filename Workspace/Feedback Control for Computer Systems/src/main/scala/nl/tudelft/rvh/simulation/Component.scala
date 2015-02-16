package nl.tudelft.rvh.simulation

trait Component[I, O] {

	def update(u: I): Component[I, O]
	def action: O
	def monitor: Map[String, AnyVal]

	def ++[Y](other: Component[O, Y]): Component[I, Y] = {
		val self = this
		new Component[I, Y] {
			def update(u: I): Component[I, Y] = {
				val thisComp = self update u
				val otherComp = other update thisComp.action
				thisComp ++ otherComp
			}

			def action: Y = other.action

			def monitor = self.monitor ++ other.monitor
		}
	}

	def map[Y](f: O => Y): Component[I, Y] = {
		val self = this
		new Component[I, Y] {
			def update(u: I): Component[I, Y] = self update u map f

			def action: Y = f(self action)

			def monitor = self.monitor
		}
	}
}