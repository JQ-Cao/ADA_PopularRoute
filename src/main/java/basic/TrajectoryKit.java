package basic;

import model.GPS;

import java.util.ArrayList;
import java.util.List;

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
    public int getInterval(List<GPS> gpsList) {
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

}
