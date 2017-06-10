package model;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

/**
 * Created by caojiaqing on 26/04/2017.
 */
public class Link extends LineSegment{
    private long id;

    public Link(long id, Coordinate start, Coordinate end) {
        super(start, end);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }



    public Coordinate getStart() {
        return super.p0;
    }

    public void setStart(Coordinate start) {
        super.p0 = start;
    }

    public Coordinate getEnd() {
        return super.p1;
    }

    public void setEnd(Coordinate end) {
        super.p1 = end;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Link){
            return ((Link)obj).getId() == this.getId();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
