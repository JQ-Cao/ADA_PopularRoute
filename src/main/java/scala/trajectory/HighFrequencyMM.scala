package scala.trajectory

import scala.trajectory.`trait`.MapMatching

/**
  * Created by caojiaqing on 02/06/2017.
  */
object HighFrequencyMM extends MapMatching{
  override def GPSTrajectory2LinkTrajectory(trajectory: List[(Double, Double, Long, Int, Int)]): List[(Long, Long, Int, Int)] = {
    null
  }

  override def GPSTrajectory2VertexTrajectory(trajectory: List[(Double, Double, Long, Int, Int)]): List[(Long, Long, Int, Int)] = {
    null
  }
}
