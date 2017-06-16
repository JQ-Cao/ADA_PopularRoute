package scala.trajectory

import basic.MapUtil
import com.vividsolutions.jts.geom.LineString
import com.vividsolutions.jts.io.WKTReader
import model.Link
import org.apache.spark.rdd.RDD
import collection.JavaConversions._

import scala.trajectory.`trait`.LoadRoadnetwork

/**
  * Created by caojiaqing on 11/06/2017.
  * 加载高通csv格式路网数据
  */
object CennaviRoadnetwork extends LoadRoadnetwork{

  //todo link 处理为单向，将正向反向双向标记全部处理为唯一正向标记
  override def loadRoadnetworkFromCSV(lines: RDD[String]): RDD[Link] = {
    lines.mapPartitions{
      partitionOfRecords=>
        val rdr = new WKTReader
        partitionOfRecords.map{
          line=>
            val arr = line.split(":")
            new Link(arr(0), arr(1), arr(2), arr(3).toInt, arr(4), arr(5), arr(6).toFloat, arr(7).toFloat, arr(8).toInt, rdr.read(arr(9)).asInstanceOf[LineString])
        }
    }
  }

  def getCellLinkMap(roadRDD: RDD[Link]): Map[String,Set[Link]] ={
    // 构建cellLinkMap
    roadRDD.flatMap{
      road=>
        var res = List.empty[(String,Link)]
        val startPoint = road.getGeometry.getStartPoint
        val endPoint = road.getGeometry.getEndPoint
        val startCell = MapUtil.findCell(startPoint.getX,startPoint.getY)
        val endCell = MapUtil.findCell(endPoint.getX,endPoint.getY)
        if(startCell!=endCell)
          res = (endCell,road)::res
        res = (startCell,road)::res
        res
    }.combineByKey(
      //val orderIds: java.util.List[String] = Seq("SJ001", "SJ002")
      x=> Set(x),
      (set:Set[Link],x)=> set + x,
      (set1:Set[Link],set2:Set[Link])=> set1 ++ set2
    ).collectAsMap().toMap
  }
}
