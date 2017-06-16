package basic;

import astar.MicroSoftWAStar;
import astar.MircoSoftWMap;
import model.*;

import java.util.*;

/**
 * Created by caojiaqing on 16/06/2017.
 */
public class MapMatching {
    //todo mapmatcing process
    public static List<Link> mapMatching(List<GPS> trajectory,Map<String,Set<Link>>cellLinkMap){

        //保存最后的匹配结果
        List<Link> results = new ArrayList<>();


        int gpsInterval = TrajectoryKit.getInterval(trajectory);
//        List<GPS> gpsSeq = TrajectoryKit.filter(trajectory);
        Cell[] cells = TrajectoryKit.GPSList2CellList(trajectory);

        //调整gps采样点的角度，用两点间的角度代替瞬时速度方向，依靠副作用
        TrajectoryKit.modifyGPSAzimuth(trajectory);

        //加载cell的详细信息，依靠副作用
        TrajectoryKit.loadCellDetailInfo(cells,cellLinkMap,gpsInterval);


        //paths 记录分段 匹配到的轨迹
        ArrayList<ArrayList<CaPath>> paths = new ArrayList<>();

        Cell lastcell = cells[0];

        //记录匹配是否成功，整个路径中有任意一段匹配失败则整个路径匹配失败
        boolean flag = true;
        for (int i = 1; i < cells.length; i++) {
            if (lastcell.getPrj() == null || lastcell.getPrj().size() < 1) {
                System.out.println("前一个GPS点计算投影失败!");
                continue;
            }
            //用s记录前一个GPS在Cell内的所有可能投影
            Projection[] s = new Projection[lastcell.getPrj().size()];
            //记录此时访问的cell
            Cell cur = cells[i];
            if (cur.getPrj() == null || cur.getPrj().size() < 1) {
                System.out.println("本GPS点计算投影失败!");
                continue;
            }

            //用e记录本GPS在Cell内的所有可能投影
            Projection[] e = new Projection[cur.getPrj().size()];

            lastcell.getPrj().toArray(s);
            cur.getPrj().toArray(e);

            lastcell = cur;

            //计算投影s,e间所有可能的路径
            ArrayList<CaPath> p = TrajectoryKit.getCaPaths(s, e);


            if (p.size() > 0) {
                for(Iterator<CaPath> it = p.iterator(); it.hasNext();){
                    CaPath caPath = it.next();
                    //所得路径超出常识速度即匹配错误，删除匹配错误的路径
                    if(caPath.getPathsLength()*1000/(e[0].gps.getTime()-s[0].gps.getTime())>(150/3.6)){
                        it.remove();
                    }
                }
                if(p.size()>0){
                    paths.add(p);
                }else {
                    flag = false;
                    System.out.println("匹配失败！前一个GPS坐标lng:" + lastcell.getGps().getLongitude() +",lat:"+ lastcell.getGps().getLatitude()+",本GPS坐标lng:"+cur.getGps().getLongitude() +",lat:"+ cur.getGps().getLatitude());
                    break;
                }

            } else {
                flag = false;
                System.out.println("匹配失败！前一个GPS坐标lng:" + lastcell.getGps().getLongitude() +",lat:"+ lastcell.getGps().getLatitude()+",本GPS坐标lng:"+cur.getGps().getLongitude() +",lat:"+ cur.getGps().getLatitude());
                break;
            }

        }
        if (!flag) {
            return results;
        }


        MircoSoftWMap map = new MircoSoftWMap(paths);

        MicroSoftWAStar astar = new MicroSoftWAStar();
        ArrayList<CaPath> finalPath = new ArrayList<CaPath>();
        List<String> finalNodes = new ArrayList<String>();
        double cost = Double.MAX_VALUE;
        int max = 0;
        for (int i = 0; i < map.getStartNodes().size(); i++) {
            for (int j = 0; j < map.getEndNodes().size(); j++) {
                List<String> nodes = astar.find(map, map.getStartNodes().get(i),
                        map.getEndNodes().get(j));

                if (astar.maxnode > max) {
                    max = astar.maxnode;
                }
                if (cost > astar.getCost()) {
                    cost = astar.getCost();
                    finalNodes = nodes;
                }
            }
        }

        for (int i = 1; i < finalNodes.size(); i++) {
            finalPath.add(map.getCaPath(finalNodes.get(i - 1),
                    finalNodes.get(i)));
        }

        for (Iterator<CaPath> it = finalPath.iterator(); it.hasNext(); ) {
            CaPath caPath = it.next();
            results.addAll(caPath.getPaths());
        }

        return results;
    }
}
