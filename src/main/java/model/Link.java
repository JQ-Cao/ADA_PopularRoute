package model;

import basic.MapUtil;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;

import java.io.Serializable;


/**
 * 道路属性对象
 */
public class Link implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String id;
    private String snode;
    private String enode;
    private float width;//宽度，单位:米
    private float length;//长度，单位:米
    private int speedlimit;//速度，单位:km/h
    //原link方向字段，0表示默认为双方向都可以通行；1表示双方向可以通行；2表示单向通行，通行方向为起点到终点方向；3表示单方向通行，通行方向为终点到起点方向。
    // 但实际保存中该字段已无效，因为双向通行道路已经新生成一条反向路径（id加-1），而单向通行道路已经修改过点序方向
    private int direction;
    private String roadclass;//道路等级R表Kind前两位
    private String roadtype;//道路属性R表Kind后两位
    private LineString geometry;
    private double azimuth;

    public Link(String id, String snode, String enode, int direction, String roadclass, String roadtype,
                float width, float length,int speedlimit,LineString geometry) {
        this.id = id;
        this.snode = snode;
        this.enode = enode;
        this.width = width;
        this.length = length;
        this.speedlimit = speedlimit;
        this.direction = direction;
        this.roadclass = roadclass;
        this.roadtype = roadtype;
        this.geometry = geometry;

        Coordinate coordinates[] = this.geometry.getCoordinates();
        int size = coordinates.length - 1;
        this.azimuth = MapUtil.azimuth(coordinates[size-1], coordinates[size]);
    }

    public double getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(double azimuth) {
        this.azimuth = azimuth;
    }

    public String getId() {
        return id;
    }

    public String getSnode() {
        return snode;
    }

    public String getEnode() {
        return enode;
    }

    public float getWidth() {
        return width;
    }

    public float getLength() {
        return length;
    }

    public int getSpeedlimit() {
        return speedlimit;
    }


    public int getDirection() {
        return direction;
    }


    public String getRoadtype() {
        return roadtype;
    }

    public String getRoadclass() {
        return roadclass;
    }

    public LineString getGeometry() {
        return geometry;
    }

    public boolean isEqual(Link link){
        return link!=null&&link.getId().equals(id);
    }

}
