package server.game;

/**
 * Created by Robert on 24-Feb-17.
 * <p>
 * A location which has been visited by the user and must wait to respawn.
 */

public class Location {

    private double lat;
    private double lng;
    private int type;
    private long timeUsed;

    public Location(double lat, double lng, int type, long visitTime) {
        this.lat = lat;
        this.lng = lng;
        this.type = type;
        timeUsed = visitTime;
    }

    public long getTimeUsed() {
        return timeUsed;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public int getType() {
        return type;
    }
}
