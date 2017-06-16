package basic;

import com.vividsolutions.jts.geom.*;
import model.*;
import scala.xml.Null;

import java.util.*;

import static model.Constant.averageInterval;
import static model.Constant.projectionNum_HF;
import static model.Constant.projectionNum_LF;

/**
 * Created by caojiaqing on 11/06/2017.
 */
public class TrajectoryKit {

    /**
     * 计算gps点频率
     *
     * @param gpsList
     * @return 采样频率
     */
    public static int getInterval(List<GPS> gpsList) {
        // TODO Auto-generated method stub

        Long timedis = gpsList.get(gpsList.size() - 1).getTime()
                - gpsList.get(0).getTime();
        return (int) (timedis / (gpsList.size() - 1));
    }

    /**
     * 通过角度过滤GPS点
     *
     * @param gps
     * @return List<GPS>
     */
    public static List<GPS> filter(List<GPS> gps) {
        // List<GPS> unsafe = new ArrayList<GPS>();
        if (gps.size() < 4) {
            return gps;
        }
        List<GPS> rst = new ArrayList<>();
        GPS last = gps.get(0);
        rst.add(last);
        last = gps.get(1);
        rst.add(last);
        int k = 1;
        for (int i = 2; i < gps.size(); i++) {
            GPS cur = gps.get(i);
            double a1 = MapUtil.azimuth(last.getLongitude(), last.getLatitude(),
                    cur.getLongitude(), cur.getLatitude());

            // 处理角度计算异常
            if (a1 < -180 || a1 > 180) {
                System.out.println("Error azimuth:" + last.getLongitude() + " "
                        + last.getLatitude() + " " + cur.getLongitude()
                        + " "
                        + cur.getLatitude());
                continue;
            }
            for (int j = k; j < i; j++) {
                GPS vet = gps.get(j);
                double a2 = MapUtil.azimuth(last.getLongitude(), last.getLatitude(),
                        vet.getLongitude(), vet.getLatitude());
                // 处理角度计算异常
                if (a2 < -180 || a2 > 180) {
                    System.out.println("Error azimuth:" + last.getLongitude() + " "
                            + last.getLatitude() + " " + vet.getLongitude() + " "
                            + vet.getLatitude());
                    continue;
                }

                double angle = Math.abs(a2 - a1);
                if (angle > 180) {
                    angle = angle - 180;
                }
                // double err = dist * angle;

                if (angle > 10.0) {
                    GPS v = gps.get(i - 1);
                    double dist = MapUtil.calPointDistance(last.getLongitude(),
                            last.getLatitude(), v.getLongitude(), v.getLatitude());
                    if (dist > 30) {
                        rst.add(v);
                        last = v;
                        k = i;
                        break;
                    }
                }
                double dist = MapUtil.calPointDistance(last.getLongitude(),
                        last.getLatitude(), vet.getLongitude(), vet.getLatitude());
                if (dist > 100) {
                    rst.add(vet);
                    last = vet;
                    k = j + 1;
                    break;
                }
            }
            if (i == (gps.size() - 1)) {
                rst.add(cur);
            }
        }
        return rst;
    }


    /**
     * 调整gps采样点的角度，用两点间的角度代替瞬时速度方向
     * @param trajectory
     */
    public static void modifyGPSAzimuth(List<GPS> trajectory){
        GPS lastgps = trajectory.get(0);
        for (int i = 0; i < trajectory.size(); i++) {
            GPS gps = trajectory.get(i);
            // 用两点连线的方位角代替点GPS的角度
            if (i > 0) {
                double azm = MapUtil.azimuth(lastgps.getLongitude(),
                        lastgps.getLatitude(), gps.getLongitude(), gps.getLatitude());
                // 处理角度计算异常
                if (azm < -180 || azm > 180) {
                    System.out.println("Error azimuth:" + lastgps.getLongitude()
                            + " " + lastgps.getLatitude() + " " + gps.getLongitude()
                            + " " + gps.getLatitude());
                    continue;
                }
                lastgps.setDirection(azm);
                // 起始点用第二点的方位角近似
                if (i == (trajectory.size() - 1)) {
                    gps.setDirection(azm);
                }
                lastgps = gps;
            }
        }
    }


    /**
     * gpsList 处理成 cellList
     * @param trajectory
     * @return
     */
    public static Cell[] GPSList2CellList(List<GPS> trajectory){
        Cell[] cells = new Cell[trajectory.size()];
        for (int i = 0; i < trajectory.size(); i++) {
            GPS gps = trajectory.get(i);

            //gps映射到cell
            String cell = MapUtil.findCell(gps.getLongitude(),
                    gps.getLatitude());

            Envelope env = MapUtil.getCellBound(cell);
            //cell区域内的坐标
            Polygon polygon = MapUtil.getGPSPolygon(new Coordinate(
                    gps.getLongitude(), gps.getLatitude()), env);
            cells[i] = new Cell(gps, cell, polygon);
        }
        return cells;
    }

    /**
     * 加载cell的详细信息
     * @param cells
     * @param cellLinkMap
     * @param gpsInterval
     */
    public static void loadCellDetailInfo(Cell[] cells, Map<String,Set<Link>> cellLinkMap,int gpsInterval){
        for (int i = 0; i < cells.length; i++) {
            Cell cell = cells[i];

            // 获取link信息
            List<Link> linkList = new ArrayList<>(cellLinkMap.get(cells[i].getCellid()));

            cell.setCellLinkIDs((ArrayList<Link>) linkList);
            cell.setPrj(getGpsProjections(cell.getGps(), cell.getCellLinkIDs(),
                    cell.getPolygon(), gpsInterval));
        }
    }



    public static ArrayList<CaPath> getCaPaths(Projection[] startProjectionArr,
                                        Projection[] endProjectionArr) {
        ArrayList<CaPath> paths = null;
        if (startProjectionArr != null && endProjectionArr != null
                && startProjectionArr.length > 0 && endProjectionArr.length > 0) {
            try {
                //记录起点投影所在的link的lastPoint的ID
                ArrayList<String> sNodes = new ArrayList<>();
                //记录终点投影所在的link的lastPoint的ID
                ArrayList<String> eNodes = new ArrayList<>();

                //记录起点投影
                ArrayList<Projection> sidsP = new ArrayList<Projection>();
                //记录终点投影
                ArrayList<Projection> eidsP = new ArrayList<Projection>();

                paths = new ArrayList<CaPath>();

                for (int i = 0; i < startProjectionArr.length; i++) {
                    for (int j = 0; j < endProjectionArr.length; j++) {
                        sNodes.add(startProjectionArr[i].getEnode());
                        sidsP.add(startProjectionArr[i]);

                        eNodes.add(endProjectionArr[j].getEnode());
                        eidsP.add(endProjectionArr[j]);
                    }
                }

                List<List<Link>> neo4JPaths = getNeo4JPaths(sNodes, eNodes);

                if (neo4JPaths != null) {
                    for (int i = 0; i < neo4JPaths.size(); i++) {
                        List<Link> neoPath = neo4JPaths.get(i);
                        double pathLength = calLinksCost(neoPath);
                        CaPath path = new CaPath(sidsP.get(i),
                                eidsP.get(i), new ArrayList<>(
                                neoPath), pathLength);
                        paths.add(path);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return paths;
    }


    //计算links的总长度
    private static double calLinksCost(List<Link> links){
        double cost = 0.0;
        for(Link link:links){
            cost += link.getLength();
        }
        return cost;
    }


    //todo 寻路算法
    public static List<Link> getLinksByToNode(String sNode,String eNode){
        return null;
    }



    //寻路
    private static List<List<Link>> getNeo4JPaths(List<String> sNodes,
                                          List<String> eNodes)
            throws Exception {
        List<List<Link>> paths = new ArrayList<>();
        if (sNodes != null && eNodes != null) {
            for (int i = 0; i < sNodes.size(); i++) {
                List<Link> path = getLinksByToNode(sNodes.get(i), eNodes.get(i));
                paths.add(path);

            }
        }
        return paths;
    }



    /**
     * 按cell对应的Link集合获取GPS到这些Link集合的投影集合，适合标准的带方位角的浮动车数据
     *
     * @param gps
     * @param links
     * @param cellPolygon
     * @param gpsInterval
     * @return
     */
    private static ArrayList<Projection> getGpsProjections(GPS gps,
                                                    ArrayList<Link> links, Polygon cellPolygon, int gpsInterval) {
        // ArrayList<Gps2Projection> prjs = null;
        ArrayList<Projection> pjsArr = new ArrayList<Projection>();
        if (links.size() > 0) {
            for (int i = 0; i < links.size(); i++) {
                Projection gpPrj = null;
                try {
                    gpPrj = getGpsProjection(gps, links.get(i), cellPolygon,gpsInterval);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                //
                if (gpPrj != null) {
                    gpPrj.setGps(gps);
                    pjsArr.add(gpPrj);
                } else {
                     System.out.println(links.get(i).getId()+" ,return null");
                }
            }
        }

        if (gpsInterval <= averageInterval) {
            // 高频点
            if (pjsArr.size() > projectionNum_HF) {
                adjustProjections(gps, pjsArr, projectionNum_HF);
            }
        } else {
            // 低频点
            if (pjsArr.size() > projectionNum_LF) {
                adjustProjections(gps, pjsArr, projectionNum_LF);
            }
        }

        return pjsArr;
    }




    /**
     * 计算GPS到Link的投影，计算方位角，适合标准带方位角浮动车数据
     *
     * @param gps
     * @param link
     * @param cellPolygon
     * @param gpsInterval
     * @return
     * @throws Exception
     */
    private static Projection getGpsProjection(GPS gps, Link link,
                                        Polygon cellPolygon, int gpsInterval) throws Exception {
        LineString lineString = link.getGeometry();

        if (!lineString.intersects(cellPolygon)) {
            return null;
        }

        Point point = MapUtil.getPoint(new Coordinate(gps.getLongitude(), gps
                .getLatitude()));
        Coordinate cs = MapUtil.closestPoint(point, lineString);

        Point prjp = MapUtil.getPoint(cs);

        double prjDistance = MapUtil.calPointDistance(point.getCoordinate(),
                prjp.getCoordinate());

        Coordinate coordinates[] = lineString.getCoordinates();
        int size = coordinates.length - 1;

        // 如果投影点是link的起点或者终点，则返回null
        if (equal(prjp.getCoordinate(), coordinates[size]) || equal(prjp.getCoordinate(), coordinates[0])) {
            return null;
        }

        double distance, prjDistanceFormSNode = 0;
        double azimuth = 0;

        int choose = 0;
        for (int i = 0; i < size; i++) {

            distance = MapUtil.calPointDistance(coordinates[i],
                    coordinates[i + 1]);

            LineSegment lineSegment = new LineSegment(coordinates[i],
                    coordinates[i + 1]);
            Coordinate cur = MapUtil.closestPoint2LineSegment(
                    prjp.getCoordinate(), lineSegment);
            if (equal(prjp.getCoordinate(), cur)) {
                distance = MapUtil.calPointDistance(coordinates[i],
                        prjp.getCoordinate());
                prjDistanceFormSNode = prjDistanceFormSNode + distance;
                choose = i;
                break;
            }
            choose = i;
            prjDistanceFormSNode = prjDistanceFormSNode + distance;
        }
        azimuth = MapUtil.azimuth(coordinates[choose], coordinates[choose + 1]);

        double azimuthDelta = gps.getDirection() - azimuth;

        if (azimuthDelta > 180)
            azimuthDelta = 360 - azimuthDelta;
        else if (azimuthDelta < -180)
            azimuthDelta = 360 + azimuthDelta;


        azimuthDelta = Math.abs(azimuthDelta);
        if (azimuthDelta > 90) {
             System.out.println("gps:"+gps.getDirection()+",zai:"+azimuth);
            return null;
        }


        double cost = 0.0;
        /* 计算link的权值 */
        // if(gps.getDirection()>-1){
        // cost = -this.pointWeighter.computeWeight(gps,azimuthDelta,
        // prjDistance, null);
        // }else{
        // TODO: 2016/8/10  记录投影置信度 可以优化
        cost = prjDistance;
        // }

        Projection projection = new Projection(link.getId(), link.getSnode(),
                link.getEnode(), link.getLength(), link.getSpeedlimit(),
                link.getRoadclass(), link.getRoadtype(), prjp.getX(),
                prjp.getY(), prjDistance, (int) prjDistanceFormSNode, azimuth,
                azimuthDelta, cost);
        return projection;

    }



    //调整GPS 投影点的个数
    private static void adjustProjections(GPS gps, List<Projection> prjs, int maxNum) {
        double maxCost = -1.0, minCost = Double.MAX_VALUE;
        double maxAng = -1, minAng = 190;
        for (Projection p : prjs) {
            if (p.getCost() > maxCost) {
                maxCost = p.getCost();
            }
            if (p.getCost() < minCost) {
                minCost = p.getCost();
            }

            if (p.getAzimuthDelta() > maxAng) {
                maxAng = p.getAzimuthDelta();
            }
            if (p.getAzimuthDelta() < minAng) {
                minAng = p.getAzimuthDelta();
            }
        }
        final double amin = minCost;
        final double aspan = maxCost - minCost;
        final double bmin = minAng;
        final double bspan = maxAng - minAng;
        // 最多保留maxNum个候选

        Collections.sort(prjs, new Comparator<Projection>() {

            public int compare(Projection o1, Projection o2) {
                // TODO Auto-generated method stub
                double a1 = (o1.getCost() - amin) / aspan;
                // o1.getAzimuthDelta()
                double a2 = (o2.getCost() - amin) / aspan;

                double c1 = (o1.getAzimuthDelta() - bmin) / bspan;
                // o1.getAzimuthDelta()
                double c2 = (o2.getAzimuthDelta() - bmin) / bspan;
                double p1 = ((o1.getPrjDistanceFormSNode() > 0) && (o1
                        .getPrjDistanceFormSNode() < o1.getLinkLenth())) ? 0.5
                        : 1.0;
                double p2 = ((o2.getPrjDistanceFormSNode() > 0) && (o2
                        .getPrjDistanceFormSNode() < o2.getLinkLenth())) ? 0.5
                        : 1.0;

                double dfs1 = Math.abs(p1 - 0.5) * 2;
                double dfs2 = Math.abs(p2 - 0.5) * 2;
                double k1 = 0.4 * a1 + 0.3 * c1 + 0.3 * dfs1;
                double k2 = 0.4 * a2 + 0.3 * c2 + 0.3 * dfs2;
                if (k1 < k2)
                    return -1;
                if (k1 > k2)
                    return 1;
                return 0;
            }

        });

        for (int i = prjs.size() - 1; i >= maxNum; i--) {
            prjs.remove(i);
        }
    }


    //误差较小的坐标点可以认为是相等的点
    private static boolean equal(Coordinate a, Coordinate b) {
        return (Math.abs(a.x - b.x) < 0.0000001)
                && (Math.abs(a.y - b.y) < 0.0000001);
    }

}
