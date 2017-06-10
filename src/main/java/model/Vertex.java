package model;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Created by caojiaqing on 26/04/2017.
 */
public class Vertex extends Coordinate{
    private long id;

    //x:lng,y:lat
    public Vertex(double x, double y, long id) {
        super(x, y);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof Vertex){
            return ((Vertex)other).getId() == this.id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
