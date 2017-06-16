package scala.trajectory.`trait`

import model.{GPS, Link}

/**
  * Created by caojiaqing on 23/05/2017.
  */
trait MapMatching {
  def GPSTrajectory2LinkTrajectory(trajectory:List[GPS],cellLinkMap:java.util.HashMap[String,java.util.Set[Link]]):List[Link]
  def GPSTrajectory2VertexTrajectory(trajectory:List[(Double, Double, Long, Int, Int)]):List[(Long,Long,Int,Int)]
}
