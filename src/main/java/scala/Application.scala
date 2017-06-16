package scala

import java.util

import basic.MapUtil
import model.Link
import basic.TrajectoryKit
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.JavaConverters._
import scala.trajectory.{CennaviRoadnetwork, CennaviTrajectory}


/**
  * Created by caojiaqing on 23/05/2017.
  */
object Application {


  def main(args: Array[String]): Unit = {

    val conf = new SparkConf()
    conf.setAppName("ADA trajectory")
    conf.setMaster("local[*]")

    val sc = new SparkContext(conf)

    val roadnetworkRDD = CennaviRoadnetwork.loadRoadnetworkFromCSV(sc.textFile("/Users/caojiaqing/Repository/map/beijing/R-G.csv"))


    val cellLinkMap = new java.util.HashMap[String,java.util.Set[Link]]()
    for(entery<-CennaviRoadnetwork.getCellLinkMap(roadnetworkRDD)){
      cellLinkMap.put(entery._1,entery._2.asJava)
    }

    val trajectoryRDD = CennaviTrajectory.toTrajectory(sc.textFile("/Users/caojiaqing/Repository/Trajectory/23")).collect()


    print("")

    sc.stop()
  }
}
