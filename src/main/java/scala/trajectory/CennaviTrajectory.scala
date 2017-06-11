package scala.trajectory
import basic.MapUtil
import model.GPS
import org.apache.spark.rdd.RDD

import scala.trajectory.`trait`.MakeTrajectory

/**
  * Created by caojiaqing on 31/05/2017.
  */
object CennaviTrajectory extends MakeTrajectory{
  /**
    *
    * @param rdd lng,lat,time,speed,direction,status,event
    * @return rdd[List[(lng,lat,time,speed,direction)] ]
    */
  override def toTrajectory(rdd: RDD[String]): RDD[List[GPS]] = {
    getLongTrajectory(rdd).flatMap{
      longTrajectory =>
        var trajectoryList = List.empty[List[GPS]]
        var shortTrajectory = List.empty[GPS]
        for(gps<-longTrajectory._2){
          //按照载客情况进行长轨迹切割
          if(gps._6==1){
            shortTrajectory = new GPS(gps._1,gps._2,gps._5,gps._3,gps._4)::shortTrajectory
          }else{
            if(shortTrajectory.nonEmpty)
              trajectoryList = shortTrajectory.reverse::trajectoryList
            shortTrajectory = List.empty[GPS]
          }
        }
        trajectoryList
    }
      //用采样点个数过滤轨迹
      .filter(trajectory=>trajectory.size>2)
      //用两点间速度过滤轨迹，存在两点间平均速度大于200KM/H的轨迹删除掉
      .filter{
      trajectory=>
        speedBetweenGPSCheck(trajectory)
    }
  }


  /**
    *
    * @param rdd       //1456847940,201603012359_190_37_10539261,1,11000001,13331194689,0,0,0,0,132,0,0,50,0,4,1166489105,398915749,1166547729,398926628,1456847991,0,
    *                    整点时间,？,
    * @return    //(车辆唯一编码，List[(lng,lat,time,speed,direction,status,event,过滤标志位)])
    */
  def getLongTrajectory(rdd:RDD[String]):RDD[(String,List[(Double,Double,Long,Int,Int,Int,Int)])] ={
    rdd.map{
      //1456847940,201603012359_190_37_10539261,1,11000001,13331194689,0,0,0,0,132,0,0,50,0,4,1166489105,398915749,1166547729,398926628,1456847991,0,
      line=>val arr =  line.split(",")
        //(车辆唯一编码，((lng,lat,time,speed,direction,status,event,过滤标志位)))
        (arr(4),(arr(17).toDouble/10000000,arr(18).toDouble/10000000,arr(19).toLong,arr(10).toInt,arr(9).toInt,arr(13).toInt,arr(14).toInt,arr(5).toInt))
    }.filter(x=> x._2._8==0).mapValues(x=>(x._1,x._2,x._3,x._4,x._5,x._6,x._7))
      //按照车辆唯一编码进行聚集
      .combineByKey(
        x=> x::Nil,
        (list:List[(Double,Double,Long,Int,Int,Int,Int)],x)=> x::list,
        (list1:List[(Double,Double,Long,Int,Int,Int,Int)],list2:List[(Double,Double,Long,Int,Int,Int,Int)])=> list1:::list2
      ).mapValues(
      //按照采样点时间排序
      gpsList=> gpsList.sortBy(_._3)
    ).mapValues {
      trajectory =>
        //删除完全重复的GPS采样点
        var trajectoryList = List.empty[(Double, Double, Long, Int, Int, Int, Int)]
        var lastTime = 0L
        for(gps<-trajectory){
          if(gps._3!=lastTime){
            lastTime = gps._3
            trajectoryList = gps::trajectoryList
          }
        }
        trajectoryList.reverse
    }
  }

  /**
    *
    * @param trajectory List[(lng,lat,time,speed,direction)]
    * @return
    */
  private def speedBetweenGPSCheck(trajectory: List[GPS]):Boolean = {
    for(i<-1 until trajectory.length){
      val dis = MapUtil.calPointDistance(trajectory(i-1).getLongitude,trajectory(i-1).getLatitude,trajectory(i).getLongitude,trajectory(i).getLatitude)
      if(dis / trajectory(i).getTime - trajectory(i-1).getTime > 55)
        return false
    }
    true
  }
}
