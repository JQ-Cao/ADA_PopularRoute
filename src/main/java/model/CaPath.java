package model;

import java.util.ArrayList;

/**
 * Created by caojiaqing on 11/06/2017.
 */
public class CaPath {
    private static final long serialVersionUID = 1L;

    //投影起点
    Projection st;

    //投影终点
    Projection ed;

    //投影间路径
    ArrayList<Link> paths;

    //路径的长度
    double pathsLength;

    /*
     * path的权重
     * 暂时考虑长度和转向cost
     */
    double cost;

    /**
     *
     * @param st
     * @param ed
     * @param paths
     * @param pathsLength
     */
    public CaPath(Projection st, Projection ed,
                  ArrayList<Link> paths, double pathsLength){

        this.st = st;
        this.ed = ed;
        this.paths = paths;
        this.pathsLength = pathsLength;
        if(st.linkID.equals("")||ed.linkID.equals("")){
            System.out.println("投影所在的link不能为空！");
        }else{
            if (st.linkID.equalsIgnoreCase( ed.linkID)) {
                //st,ed在同一个link上
                this.pathsLength = ed.prjDistanceFormSNode - st.prjDistanceFormSNode;
            } else {
                this.pathsLength = this.pathsLength + st.linkLenth - st.prjDistanceFormSNode + ed.prjDistanceFormSNode;
                adjustPathDistance(st, ed, paths);
            }
            if(this.pathsLength < 0) {
                this.pathsLength = 1d;//不允许后退
            }

            //计算cost
            if (null == this.paths || this.paths.size() == 0) {
                this.cost = this.pathsLength + turnCost(this.st.azimuth, this.ed.azimuth);
            } else {
                this.cost = this.pathsLength + turnCost(this.st.azimuth, this.paths.get(0).getAzimuth());
                for (int i = 0; i < this.paths.size() - 1; i++) {
                    this.cost += turnCost(this.paths.get(i).getAzimuth(), this.paths.get(i+1).getAzimuth());
                }
                this.cost += turnCost(this.paths.get(this.paths.size() - 1).getAzimuth(), this.ed.azimuth);
            }
        }
    }

    /**
     * TODO(功能描述)
     * @param azimuthSt
     * @param azimuthEd
     * @return
     */
    private double turnCost(double azimuthSt, double azimuthEd) {
        // TODO Auto-generated method stub
        if (azimuthSt - azimuthEd >= Constant.leftHandAzimuthDis && azimuthSt - azimuthEd <= 180.0) {
            return Constant.leftHandCost;
        }
        return 0.0;
    }

    /**处理双向同行，做 U turn 的时候路径长度计算不正确的问题。
     * @param st
     * @param ed
     * @param paths
     */
    private  void adjustPathDistance(Projection st, Projection ed, ArrayList<Link> paths){
        if (paths != null && paths.size() > 0) {
            try {
                String linkid1 = st.linkID ;
                String linkid2 = paths.get(0).getId() ;
                Long gridID1 = Math.abs(Long.parseLong(linkid1));
                Long gridID2 = Math.abs(Long.parseLong(linkid2));

                if (gridID1==gridID2) {
                    double distance = this.pathsLength - paths.get(0).getLength() + st.prjDistanceFormSNode;
                    if (distance>1d) {
                        this.pathsLength = distance;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return the st
     */
    public Projection getSt() {
        return st;
    }

    /**
     * @param st the st to set
     */
    public void setSt(Projection st) {
        this.st = st;
    }

    /**
     * @return the ed
     */
    public Projection getEd() {
        return ed;
    }

    /**
     * @param ed the ed to set
     */
    public void setEd(Projection ed) {
        this.ed = ed;
    }

    /**
     * @return the paths
     */
    public ArrayList<Link> getPaths() {
        return paths;
    }

    /**
     * @param paths the paths to set
     */
    public void setPaths(ArrayList<Link> paths) {
        this.paths = paths;
    }

    /**
     * @return the pathsLength
     */
    public double getPathsLength() {
        return pathsLength;
    }

    /**
     * @param pathsLength the pathsLength to set
     */
    public void setPathsLength(double pathsLength) {
        this.pathsLength = pathsLength;
    }

    /**
     * @return the cost
     */
    public double getCost() {
        return cost;
    }

    /**
     * @param cost the cost to set
     */
    public void setCost(double cost) {
        this.cost = cost;
    }
}
