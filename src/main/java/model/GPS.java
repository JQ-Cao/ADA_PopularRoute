package model;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Created by caojiaqing on 11/06/2017.
 */
public class GPS implements Serializable {
    private double longitude; // required
    private double latitude; // required
    private double direction; // required
    private long time; // required
    private int speed; // required

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getDirection() {
        return direction;
    }

    public void setDirection(double direction) {
        this.direction = direction;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public GPS(double longitude, double latitude, double direction, long time,
               int speed) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.direction = direction;
        this.time = time;
        this.speed = speed;
    }

    @Override
    public int hashCode() {
        return (this.longitude + "," + this.latitude + "," + this.time).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;
        GPS g = (GPS) obj;
        return g.time == this.time && g.latitude == this.latitude && g.longitude == this.longitude;
    }
}