package model;

/**
 * Created by caojiaqing on 11/06/2017.
 * cell内GPS到cell内link上的投影
 */
public class Projection {
    public String linkID; // required
    public String snode; // required
    public String enode; // required
    public double linkLenth; // required
    public int speedlimit; // required
    public String roadclass; // required
    public String roadtype; // required
    public double x; // required
    public double y; // required
    public double prjDistance; // required
    public double prjDistanceFormSNode; // required
    public double azimuth; // required
    public double azimuthDelta; // required
    public double cost; // required

    public GPS gps;


    public GPS getGps() {
        return gps;
    }

    public void setGps(GPS gps) {
        this.gps = gps;
    }

    public String getLinkID() {
        return linkID;
    }

    public void setLinkID(String linkID) {
        this.linkID = linkID;
    }

    public String getSnode() {
        return snode;
    }

    public void setSnode(String snode) {
        this.snode = snode;
    }

    public String getEnode() {
        return enode;
    }

    public void setEnode(String enode) {
        this.enode = enode;
    }

    public double getLinkLenth() {
        return linkLenth;
    }

    public void setLinkLenth(double linkLenth) {
        this.linkLenth = linkLenth;
    }

    public double getSpeedlimit() {
        return speedlimit;
    }

    public void setSpeedlimit(int speedlimit) {
        this.speedlimit = speedlimit;
    }

    public String getRoadclass() {
        return roadclass;
    }

    public void setRoadclass(String roadclass) {
        this.roadclass = roadclass;
    }

    public String getRoadtype() {
        return roadtype;
    }

    public void setRoadtype(String roadtype) {
        this.roadtype = roadtype;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getPrjDistance() {
        return prjDistance;
    }

    public void setPrjDistance(double prjDistance) {
        this.prjDistance = prjDistance;
    }

    public double getPrjDistanceFormSNode() {
        return prjDistanceFormSNode;
    }

    public void setPrjDistanceFormSNode(double prjDistanceFormSNode) {
        this.prjDistanceFormSNode = prjDistanceFormSNode;
    }

    public double getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(double azimuth) {
        this.azimuth = azimuth;
    }

    public double getAzimuthDelta() {
        return azimuthDelta;
    }

    public void setAzimuthDelta(double azimuthDelta) {
        this.azimuthDelta = azimuthDelta;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    /**
     * 构造函数
     *
     * @param linkID
     * @param snode
     * @param enode
     * @param linkLenth
     * @param speedlimit
     * @param roadclass
     * @param roadtype
     * @param x
     * @param y
     * @param prjDistance
     * @param prjDistanceFormSNode
     * @param azimuth
     * @param azimuthDelta
     * @param cost
     */
    public Projection(String linkID, String snode, String enode,
                      double linkLenth, int speedlimit, String roadclass,
                      String roadtype, double x, double y, double prjDistance,
                      double prjDistanceFormSNode, double azimuth, double azimuthDelta,
                      double cost) {
        super();
        this.linkID = linkID;
        this.snode = snode;
        this.enode = enode;
        this.linkLenth = linkLenth;
        this.speedlimit = speedlimit;
        this.roadclass = roadclass;
        this.roadtype = roadtype;
        this.x = x;
        this.y = y;
        this.prjDistance = prjDistance;
        this.prjDistanceFormSNode = prjDistanceFormSNode;
        this.azimuth = azimuth;
        this.azimuthDelta = azimuthDelta;
        this.cost = cost;
    }

    public Projection(String linkID, String snode, String enode, double x, double y,GPS gps){
        this.linkID = linkID;
        this.snode = snode;
        this.enode = enode;
        this.x = x;
        this.y = y;
        this.gps = gps;
    }
}
