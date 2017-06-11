package scala.trajectory.`trait`

import model.GPS
import org.apache.spark.rdd.RDD

/**
  * Created by caojiaqing on 23/05/2017.
  */
trait MakeTrajectory {
  /**
    *
    * @param rdd lng,lat,time,speed,direction,status,event
    * @return rdd[车辆ID,List[(lng,lat,time,speed,direction)] ]
    */
  def toTrajectory(rdd:RDD[String]):RDD[List[GPS]]
}
