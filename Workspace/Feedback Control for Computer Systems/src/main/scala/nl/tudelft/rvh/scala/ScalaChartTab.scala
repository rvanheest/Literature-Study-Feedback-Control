package nl.tudelft.rvh.scala

import nl.tudelft.rvh.ChartTab
import nl.tudelft.rvh.Tuple
import rx.lang.scala.Observable
import rx.lang.scala.JavaConversions

abstract class ScalaChartTab(tabName: String, chartTitle: String, xName: String, yName: String) extends ChartTab(tabName, chartTitle, xName, yName) {

  def runSimulation(): rx.Observable[Tuple[Number, Number]] = {
    val tuples = simulation().map(t => new Tuple(t._1, t._2))
    JavaConversions.toJavaObservable(tuples)
      .asInstanceOf[rx.Observable[Tuple[Number, Number]]]
  }

  def simulation(): Observable[(Number, Number)]
}