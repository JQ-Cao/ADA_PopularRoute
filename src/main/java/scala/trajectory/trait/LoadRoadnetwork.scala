package scala.trajectory.`trait`

import model.Link
import org.apache.spark.rdd.RDD

/**
  * Created by caojiaqing on 11/06/2017.
  */
trait LoadRoadnetwork {
  def loadRoadnetworkFromCSV(lines:RDD[String]):RDD[Link]
}
