package model;

import com.vividsolutions.jts.geom.Polygon;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by caojiaqing on 11/06/2017.
 */
public class Cell implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 小网格ID号，二次网格ID+”_“+行号+”_“+列号
     */
    private String cellid;

    /**
     * 二次网格内的路链ID
     */
    private ArrayList<Link> cellLinkIDs;

    /**
     * 网格内的gps在网格内投影到的边
     */
    ArrayList<Projection> prj;

    private Polygon polygon;

    /**
     * 网格内的GPS
     */
    private GPS gps;

    public int hitTimes = 0;

    public ArrayList<Projection> getPrj() {
        return prj;
    }

    public void setPrj(ArrayList<Projection> prj) {
        this.prj = prj;
    }

    public void setCellLinkIDs(ArrayList<Link> cellLinkIDs) {
        this.cellLinkIDs = cellLinkIDs;
    }




    public GPS getGps() {
        return gps;
    }

    public void setGps(GPS gps) {
        this.gps = gps;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public Cell(GPS gps,String cellid,Polygon polygon) {
        super();
        this.cellid = cellid;
        this.gps = gps;
        this.polygon = polygon;
    }
    public Cell(String cellid, ArrayList<Link> cellLinkIDs) {
        super();
        this.cellid = cellid;
        this.cellLinkIDs = cellLinkIDs;
    }

    public String getCellid() {
        return cellid;
    }

    public ArrayList<Link> getCellLinkIDs() {
        return cellLinkIDs;
    }
}
