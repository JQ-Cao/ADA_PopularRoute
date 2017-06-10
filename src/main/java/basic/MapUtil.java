/*
 * Class Name:    MapUtil.java
 * Description:   TODO(类的功能描述)
 * Version:       2014年6月10日 下午6:53:38
 * Author:        Administrator
 * Copyright 2010 Cennavi Corp, All Rights Reserved.
 */
package basic;

import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.datum.DefaultEllipsoid;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.operation.distance.DistanceOp;



/**
 * TODO(这里用一句话描述这个类的作用)
 * 
 * @author Administrator
 * @version 2014年6月10日 下午6:53:38
 */
public class MapUtil {
	public final static int defaultCellSize = 60;

	/**
	 * 按经纬度坐标查找该点所在的二级网格
	 * 
	 * @param lon
	 * @param lat
	 * @return
	 */
	public static int findGrid(double lon, double lat) {
		int xx = (int) (lon - 60);
		int yy = (int) (lat * 60 / 40);
		int x = (int) ((lon - (int) lon) / 0.125);
		int y = (int) (((lat * 60 / 40 - yy)) / 0.125);
		return yy * 10000 + xx * 100 + y * 10 + x;

	}

	/**
	 * 按二级网格号获取二级网格的坐标范围
	 * 
	 * @param gridNo
	 * @return
	 */
	public static Envelope getGridBound(int gridNo) {

		int yy = (int) (gridNo / 10000);
		int xx = (int) ((gridNo - yy * 10000) / 100);
		int y = (int) ((gridNo - yy * 10000 - xx * 100) / 10);
		int x = (int) ((gridNo - yy * 10000 - xx * 100 - y * 10));

		double miny = yy * 40.0 / 60 + y * 5.0 / 60;
		double maxy = yy * 40.0 / 60 + (y + 1) * 5.0 / 60;

		double minx = xx + 60 + x * 7.5 / 60;
		double maxx = xx + 60 + (x + 1) * 7.5 / 60;

		return new Envelope(minx, maxx, miny, maxy);
	}

	/**
	 * 按经纬度坐标查找点所在的二级网格和小网格
	 * 
	 * @param lon
	 * @param lat
	 * @param cellSize
	 * @return
	 */
	public static String findCell(double lon, double lat, int cellSize) {
		int gridNo = findGrid(lon, lat);
		Envelope ev = getGridBound(gridNo);

		double minx = ev.getMinX();
		double miny = ev.getMinY();
		double maxx = ev.getMaxX();
		double maxy = ev.getMaxY();

		try{
			double xdelta = calPointDistance(minx, miny, maxx, miny);
			double ydelta = calPointDistance(minx, miny, minx, maxy);

//		long x_cell_size = Math.round(xdelta / cellSize);
//		long y_cell_size = Math.round(ydelta / cellSize);
			long x_cell_size = (long)Math.floor(xdelta / cellSize);
			long y_cell_size = (long)Math.floor(ydelta / cellSize);
			double spanx = maxx - minx, spany = maxy - miny;

			double x_cell_delta = spanx / x_cell_size;
			double y_cell_delta = spany / y_cell_size;

			int col = (int) ((lon - minx) / x_cell_delta);
			int row = (int) ((lat - miny) / y_cell_delta);
//		long col = Math.round((lon - minx) / x_cell_delta);
//		long row = Math.round((lat - miny) / y_cell_delta);

			return gridNo + "_" + col + "_" + row;

		}catch (Exception e){
			return "";
		}
	}

	public static String findCell(double lon, double lat) {
		return findCell(lon, lat, defaultCellSize);
	}

	public static Envelope getCellBound(String cellID) {
		return getCellBound(cellID, defaultCellSize);
	}

	/**
	 * 获取网格中每个小网格的范围
	 * 
	 * @param cellID
	 * @param cellSize
	 * @return
	 */
	public static Envelope getCellBound(String cellID, int cellSize) {

		String s[] = cellID.split("_");
		int gridNo = Integer.parseInt(s[0]);
		int row = Integer.parseInt(s[1]);
		int col = Integer.parseInt(s[2]);

		Envelope ev = getGridBound(gridNo);

		double minx = ev.getMinX();
		double miny = ev.getMinY();
		double maxx = ev.getMaxX();
		double maxy = ev.getMaxY();


		double deltax = calPointDistance(minx, miny, maxx, miny);
		double deltay = calPointDistance(minx, miny, minx, maxy);

		int xsize = (int) Math.floor(deltax / cellSize);
		long ysize = (int) Math.floor(deltay / cellSize);

		double spanx = maxx - minx, spany = maxy - miny;

		double cxdelta = spanx / xsize;
		double cydelta = spany / ysize;

		double cminx = minx + cxdelta * row;
		double cminy = miny + cydelta * col;
		double cmaxx = minx + cxdelta * (row + 1);
		double cmaxy = miny + cydelta * (col + 1);

		Envelope env = new Envelope(cminx, cmaxx, cminy, cmaxy);
		return env;
	}

	/**
	 * 获取方位角 方位角范围为-180< azimuth <=180 求两点之间的方位角
	 * 
	 * @param sPoint
	 * @param ePoint
	 * @return
	 */
	public static double azimuth(Coordinate sPoint, Coordinate ePoint) {
		return azimuth(sPoint.x, sPoint.y, ePoint.x, ePoint.y);
	}

	/**
	 * 获取方位角 方位角范围为-180< azimuth <=180 求两点之间的方位角
	 * 
	 * @param lon1
	 * @param lat1
	 * @param lon2
	 * @param lat2
	 * @return
	 */
	public static double azimuth(double lon1, double lat1, double lon2,
			double lat2) {
		GeodeticCalculator azimuth = new GeodeticCalculator(DefaultEllipsoid.WGS84);
//		azimuth.setStartingGeographicPoint(lon1, lat1);
//		azimuth.setDestinationGeographicPoint(lon2, lat2);
//		return azimuth.getAzimuth();
		
		try {
			azimuth.setStartingGeographicPoint(lon1, lat1);
			azimuth.setDestinationGeographicPoint(lon2, lat2);
			return azimuth.getAzimuth();
		} catch (Exception e) {
			System.out.println(lon1 + " " + lat1 + " " + lon2 + " " + lat2);
			return -200;
		}
	}

	/**
	 * 两点之间的球面距离
	 * 
	 * @param sp
	 * @param ep
	 * @return
	 */
	public static double calPointDistance(Coordinate sp, Coordinate ep) {
		return calPointDistance(sp.x, sp.y, ep.x, ep.y);
	}

	/**
	 * 两点之间的球面距离
	 * 
	 * @param lng1
	 * @param lat1
	 * @param lng2
	 * @param lat2
	 * @return 两坐标间距离 单位为米
	 */
	public static double calPointDistance(double lng1, double lat1,
			double lng2, double lat2) {
		GeodeticCalculator az = new GeodeticCalculator(DefaultEllipsoid.WGS84);
		az.setStartingGeographicPoint(lng1, lat1);
		az.setDestinationGeographicPoint(lng2, lat2);
		return az.getOrthodromicDistance();
	}


	/**
	 * 点到线段的最近点
	 * 
	 * @param p
	 * @param l
	 * @return
	 */
	public static Coordinate closestPoint2LineSegment(Coordinate p,
			LineSegment l) {
		return l.closestPoint(p);
	}

	/**
	 * JTS计算的两个几何体的最近点,返回后面那几何体到最近点
	 * 
	 * @param p
	 * @param l
	 * @return
	 */
	public static Coordinate closestPoint(Geometry p, Geometry l) {
		Coordinate clsPoint = null;
		clsPoint = DistanceOp.closestPoints(p, l)[1];
		return clsPoint;
	}

	/**
	 * JTS计算的点到折线的最近点
	 * 
	 * @param p
	 * @param l
	 * @return
	 */
	public static Coordinate closestPoint2LineString(Coordinate p, LineString l) {
		Coordinate clsPoint = null;
		clsPoint = DistanceOp.closestPoints(new Point(p, new PrecisionModel(),
				4326), l)[1];
		return clsPoint;
	}

	/**
	 * JTS距离计算的点到折线的最近点
	 * 
	 * @param p
	 * @param ml
	 * @return
	 */
	public static Coordinate closestPoint2MultiLineString(Coordinate p,
			MultiLineString ml) {
		Coordinate clsPoint = null;
		clsPoint = DistanceOp.closestPoints(new Point(p, new PrecisionModel(),
				4326), ml)[1];
		return clsPoint;
	}

	public static Geometry parseWktString(String wktString) throws Exception {
		int srid = 4326;
		GeometryFactory factory = new GeometryFactory(new PrecisionModel(),
				srid);
		WKTReader reader = new WKTReader(factory);
		Geometry geom = reader.read(wktString);
		return geom;
	}

	public static Point getPoint(double lon, double lat) {
		Coordinate p = new Coordinate(lon, lat);
		return new Point(p, new PrecisionModel(), 4326);
	}

	public static Point getPoint(Coordinate p) {
		return new Point(p, new PrecisionModel(), 4326);
	}
	
	public static Polygon getCellPolygon(String cellID){
		Envelope env=getCellBound(cellID);
		
		double deltaX=env.getMaxX()-env.getMinX();
		double deltaY=env.getMaxY()-env.getMinY();
//		double deltaX=0;
//		double deltaY=0;
		
		double minx = env.getMinX()-deltaX;
		double miny = env.getMinY()-deltaY;
		double maxx = env.getMaxX()+deltaX;
		double maxy = env.getMaxY()+deltaY;
		
		Coordinate[] points=new Coordinate[]{
				new Coordinate(minx,miny),
				new Coordinate(maxx,miny),
				new Coordinate(maxx,maxy),
				new Coordinate(minx,maxy),
				new Coordinate(minx,miny),
				};
		
		LinearRing linearRing=new LinearRing(points, new PrecisionModel(), 4326);
		Polygon polygon = new Polygon(linearRing, new PrecisionModel(), 4326);
		return polygon;
	}
	public static Polygon getGPSPolygon(Coordinate gps, String cellID){
		Envelope env=getCellBound(cellID);
		
		double deltaX=(env.getMaxX()-env.getMinX())*(60.0 / defaultCellSize);
		double deltaY=(env.getMaxY()-env.getMinY())*(60.0 / defaultCellSize);
		
//		double minx = env.getMinX()-deltaX;
//		double miny = env.getMinY()-deltaY;
//		double maxx = env.getMaxX()+deltaX;
//		double maxy = env.getMaxY()+deltaY;
		
		double minx = gps.x-deltaX;
		double miny = gps.y-deltaY;
		double maxx = gps.x+deltaX;
		double maxy = gps.y+deltaY;
		Coordinate[] points=new Coordinate[]{
				new Coordinate(minx,miny),
				new Coordinate(maxx,miny),
				new Coordinate(maxx,maxy),
				new Coordinate(minx,maxy),
				new Coordinate(minx,miny),
				};
		
		LinearRing linearRing=new LinearRing(points, new PrecisionModel(), 4326);
		Polygon polygon = new Polygon(linearRing, new PrecisionModel(), 4326);
		return polygon;
	}
	


	private final static double EARTH_RADIUS = 6378.137;//地球半径
	/**
	 * 按经纬度坐标查找该点所在的二级网格.
	 * 
	 * @param lon
	 *            经度
	 * @param lat
	 *            维度
	 * @return int 网格id
	 */
	public static int findMesh(double lon, double lat) {
	
		int xx = (int) (lon - 60);
		int yy = (int) (lat * 60 / 40);
		int x = (int) ((lon - (int) lon) / 0.125);
		int y = (int) (((lat * 60 / 40 - yy)) / 0.125);
		return yy * 10000 + xx * 100 + y * 10 + x;
	}
	
	
	public static void main(String[] args){
		 
		
		String s = "LINESTRING (116.45737 39.99501, 116.45733 39.99445)";
		
		try {
			System.out.println(calPointDistance(116.45737,39.99501,116.45733,39.99445));
//			Geometry geometry = MapUtil.parseWktString(s);
//			System.out.println(geometry);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	
	private static double rad(double d)
	{
	   return d * Math.PI / 180.0;
	}

	/**
	 * 
	 * @param lng1
	 * @param lat1
	 * @param lng2
	 * @param lat2
	 * @return 长度单位m
	 */
	public static double GetDistance(double lng1, double lat1, double lng2, double lat2)
	{
		double dLat = rad(lat1-lat2);
		double dLng = rad(lng1-lng2);
		if(dLat==0 &&dLng==0) return 0;
		double a = Math.sin(dLat/2)*Math.sin(dLat/2) + Math.cos(lat2*Math.PI/180)*Math.cos(lat1*Math.PI/180)*Math.sin(dLng/2)*Math.sin(dLng/2);
		double s = 2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
		// s = Math.round(s * 1) / 1;
		return s * EARTH_RADIUS*1000;
	}


	/** 
	 * TODO(功能描述)
	 * @param meshid
	 * @param gpsX
	 * @param gpsY
	 * @return
	 */
	public static int findMinGrid(int meshid, double gpsX, double gpsY) {
		// TODO Auto-generated method stub
		
		int yy = ( int ) (meshid / 10000);
		int xx = ( int ) ((meshid - yy * 10000) / 100);
		int y = ( int ) ((meshid - yy * 10000 - xx * 100) / 10);
		int x = ( int ) ((meshid - yy * 10000 - xx * 100 - y * 10));

		//二次网格范围
		double miny = yy * 40.0 / 60 + y * 5.0 / 60;
		double maxy = yy * 40.0 / 60 + (y + 1) * 5.0 / 60;
		double minx = xx + 60 + x * 7.5 / 60;
		double maxx = xx + 60 + (x + 1) * 7.5 / 60;

		double spanx = maxx - minx;
		double spany = maxy - miny;
		
		//四分网格边长
		double xCellDelta4 = spanx / 2;
		double yCellDelta4 = spany / 2;

		int x4 = ( int ) ((gpsX - minx) / xCellDelta4);
		int y4 = ( int ) ((gpsY - miny) / yCellDelta4);
		
		//点所在的四分网格左上点经纬度
		double minx4 = minx + x4 * xCellDelta4;
		double miny4 = miny + y4 * yCellDelta4;
		
		//十六分网格边长
		double xCellDelta16 = spanx / 4;
		double yCellDelta16 = spany / 4;
		
		int x16 = ( int ) ((gpsX - minx4) / xCellDelta16);
		int y16 = ( int ) ((gpsY - miny4) / yCellDelta16);

		int no4 = x4 * 2 + y4;
		int no16 = x16 * 2 + y16;
		
		int gridID = meshid * 100 + no4 * 10 + no16;
		
		return gridID;
	}
	
	/**
	 * 按经纬度坐标查找该点所在的二级网格.
	 * 
	 * @param lon 经度
	 * @param lat 维度
	 * @return int 网格号
	 */
	public static int findGlobalGrid(double lon, double lat) {
		int xx = ( int ) (lon + 180);
		int yy = ( int ) ((lat + 90) * 60 / 40);
		int x = ( int ) ((lon - ( int ) lon) / 0.125);
		int y = ( int ) ((((lat + 90) * 60 / 40 - yy)) / 0.125);
		return yy * 100000 + xx * 100 + y * 10 + x;

	}
	
	/**
	 * 全球按二级网格号获取二级网格的坐标范围
	 * 
	 * @param gridNo
	 * @return
	 */
	public static Envelope getGlobalGridBound(int gridNo) {

		int yy = ( int ) (gridNo / 100000);
		int xx = ( int ) ((gridNo - yy * 100000) / 100);
		int y = ( int ) ((gridNo - yy * 100000 - xx * 100) / 10);
		int x = ( int ) ((gridNo - yy * 100000 - xx * 100 - y * 10));


		double miny = yy * 40.0 / 60 - 90 + y * 5.0 / 60;
		double maxy = yy * 40.0 / 60 - 90 + (y + 1) * 5.0 / 60;

		double minx = xx - 180 + x * 7.5 / 60;
		double maxx = xx - 180 + (x + 1) * 7.5 / 60;
		return new Envelope(minx, maxx, miny, maxy);
	}

	/** 
	 * TODO(功能描述)
	 * @param lon
	 * @param lat
	 * @return
	 */
	public static String findGlobalCell(double lon, double lat) {
		// TODO Auto-generated method stub
		int gridNo = findGlobalGrid(lon, lat);
		Envelope ev = getGlobalGridBound(gridNo);

		double minx = ev.getMinX();
		double miny = ev.getMinY();
		double maxx = ev.getMaxX();
		double maxy = ev.getMaxY();

		double xdelta = calPointDistance(minx, miny, maxx, miny);
		double ydelta = calPointDistance(minx, miny, minx, maxy);

//		long x_cell_size = Math.round(xdelta / cellSize);
//		long y_cell_size = Math.round(ydelta / cellSize);
		long x_cell_size = (long)Math.floor(xdelta / defaultCellSize);
		long y_cell_size = (long)Math.floor(ydelta / defaultCellSize);
		double spanx = maxx - minx, spany = maxy - miny;

		double x_cell_delta = spanx / x_cell_size;
		double y_cell_delta = spany / y_cell_size;

		int col = (int) ((lon - minx) / x_cell_delta);
		int row = (int) ((lat - miny) / y_cell_delta);
//		long col = Math.round((lon - minx) / x_cell_delta);
//		long row = Math.round((lat - miny) / y_cell_delta);

		return gridNo + "_" + col + "_" + row;
	}

	/** 
	 * TODO(功能描述)
	 * @param cellID
	 * @return cellID 的范围区域
	 */
	public static Envelope getGlobalCellBound(String cellID) {
		// TODO Auto-generated method stub
		String s[] = cellID.split("_");
		int gridNo = Integer.parseInt(s[0]);
		int row = Integer.parseInt(s[1]);
		int col = Integer.parseInt(s[2]);

		Envelope ev = getGlobalGridBound(gridNo);

		double minx = ev.getMinX();
		double miny = ev.getMinY();
		double maxx = ev.getMaxX();
		double maxy = ev.getMaxY();

		double deltax = calPointDistance(minx, miny, maxx, miny);
		double deltay = calPointDistance(minx, miny, minx, maxy);

		int xsize = (int) Math.floor(deltax / defaultCellSize);
		long ysize = (int) Math.floor(deltay / defaultCellSize);

		double spanx = maxx - minx, spany = maxy - miny;

		double cxdelta = spanx / xsize;
		double cydelta = spany / ysize;

		double cminx = minx + cxdelta * row;
		double cminy = miny + cydelta * col;
		double cmaxx = minx + cxdelta * (row + 1);
		double cmaxy = miny + cydelta * (col + 1);

		Envelope env = new Envelope(cminx, cmaxx, cminy, cmaxy);
		return env;
	}

	/** 
	 * TODO(功能描述)
	 * @param gps
	 * @param env
	 * @return
	 */
	public static Polygon getGPSPolygon(Coordinate gps, Envelope env) {
		// TODO Auto-generated method stub
		double deltaX=(env.getMaxX()-env.getMinX())*(60.0 / defaultCellSize);
		double deltaY=(env.getMaxY()-env.getMinY())*(60.0 / defaultCellSize);
		
//		double minx = env.getMinX()-deltaX;
//		double miny = env.getMinY()-deltaY;
//		double maxx = env.getMaxX()+deltaX;
//		double maxy = env.getMaxY()+deltaY;
		
		double minx = gps.x-deltaX;
		double miny = gps.y-deltaY;
		double maxx = gps.x+deltaX;
		double maxy = gps.y+deltaY;
		Coordinate[] points=new Coordinate[]{
				new Coordinate(minx,miny),
				new Coordinate(maxx,miny),
				new Coordinate(maxx,maxy),
				new Coordinate(minx,maxy),
				new Coordinate(minx,miny),
				};
		
		LinearRing linearRing=new LinearRing(points, new PrecisionModel(), 4326);
		Polygon polygon = new Polygon(linearRing, new PrecisionModel(), 4326);
		return polygon;
	}
	
}
