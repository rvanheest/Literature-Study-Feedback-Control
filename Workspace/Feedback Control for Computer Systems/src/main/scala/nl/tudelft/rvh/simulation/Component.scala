package nl.tudelft.rvh.simulation

trait Component[I, O] {

	def update(u: I): Component[I, O]
	def action: O

	def ++[Y](other: Component[O, Y]): Component[I, Y] = {
		val self = this
		new Component[I, Y] {
			def update(u: I): Component[I, Y] = {
				val thisComp = self.update(u)
				thisComp ++ other.update(thisComp.action)
			}
			
			def action: Y = other.action
		}
	}
	
	def map[Y](f: O => Y): Component[I, Y] = {
		val self = this
		new Component[I, Y] {
			def update(u: I): Component[I, Y] = {
				val thisComp = self.update(u)
				thisComp.map(f)
			}
			
			def action: Y = f(self.action)
		}
	}

	def flatMap[Y](f: O => Component[I, Y]): Component[I, Y] = {
		val self = this
		new Component[I, Y] {
			def update(u: I): Component[I, Y] = {
				val thisComp = self.update(u)
				thisComp.flatMap(f)
			}
			
			def action: Y = f(self.action).action
		}
	}
}