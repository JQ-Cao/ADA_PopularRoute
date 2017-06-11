package scala.trajectory

import com.vividsolutions.jts.geom.LineString
import com.vividsolutions.jts.io.WKTReader
import model.Link
import org.apache.spark.rdd.RDD

import scala.trajectory.`trait`.LoadRoadnetwork

/**
  * Created by caojiaqing on 11/06/2017.
  * 加载高通csv格式路网数据
  */
object CennaviRoadnetwork extends LoadRoadnetwork{

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
}
