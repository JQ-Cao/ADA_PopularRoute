package scala

import org.apache.spark.{SparkConf, SparkContext}

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

    val trajectoryRDD = CennaviTrajectory.toTrajectory(sc.textFile("/Users/caojiaqing/Repository/Trajectory/23")).collect()


    print("")

    sc.stop()
  }
}
