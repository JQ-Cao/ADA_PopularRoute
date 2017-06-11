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
    protected String id;
    protected String snode;
    protected String enode;
    protected float width;//宽度，单位:米
    protected float length;//长度，单位:米
    protected int speedlimit;//速度，单位:km/h
    protected int direction;
    protected String roadclass;//道路等级R表Kind前两位
    protected String roadtype;//道路属性R表Kind后两位
    protected LineString geometry;
    protected double azimuth;

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
        return link.getId()==id;
    }
}
