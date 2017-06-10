package scala.trajectory.`trait`

/**
  * Created by caojiaqing on 23/05/2017.
  */
trait MapMatching {
  def GPSTrajectory2LinkTrajectory(trajectory:List[(Double, Double, Long, Int, Int)]):List[(Long,Long,Int,Int)]
  def GPSTrajectory2VertexTrajectory(trajectory:List[(Double, Double, Long, Int, Int)]):List[(Long,Long,Int,Int)]
}
