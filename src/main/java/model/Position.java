package model;

/**
 * Created by caojiaqing on 11/06/2017.
 */
public class Position {
    /**
     * 匹配LinkID
     */
    private long linkID;
    private Double longitude;
    private Double latitude;
    /**
     * gps时间，单位秒
     */
    private Long time;

    /**
     * TODO(功能描述)
     * @param gps
     * @param linkID
     * @return
     */
    public static Position getInstance(GPS gps, Long linkID) {
        // TODO Auto-generated method stub
        Position pos = new Position();
        pos.linkID = linkID;

        pos.setLongitude(gps.getLongitude());
        pos.setLatitude(gps.getLatitude());
        pos.setTime(gps.getTime());

        return pos;
    }

    public Long getTime() {
        return time;
    }
    public void setTime(Long time) {
        this.time = time;
    }

    public long getLinkID() {
        return linkID;
    }
    public void setLinkID(long linkID) {
        this.linkID = linkID;
    }


    /**
     * @return longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * @return latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
