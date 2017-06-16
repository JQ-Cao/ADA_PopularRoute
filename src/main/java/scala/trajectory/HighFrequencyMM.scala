package scala.trajectory

import model.{GPS, Link}

import scala.trajectory.`trait`.MapMatching
import scala.collection.JavaConverters._


/**
  * Created by caojiaqing on 02/06/2017.
  */
object HighFrequencyMM extends MapMatching{
  override def GPSTrajectory2LinkTrajectory(trajectory: List[GPS],cellLinkMap:java.util.HashMap[String,java.util.Set[Link]]): List[Link] = {
    basic.MapMatching.mapMatching(trajectory.asJava,cellLinkMap).asScala.toList
  }

  override def GPSTrajectory2VertexTrajectory(trajectory: List[(Double, Double, Long, Int, Int)]): List[(Long, Long, Int, Int)] = {
    null
  }
}
