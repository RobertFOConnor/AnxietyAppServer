package server.game;

/**
 * Created by Robert on 03-May-17.
 */

public class Chest {

    private String UID;
    private int itemID;
    private float distance;

    public Chest(String UID, int itemID, float distance) {
        this.UID = UID;
        this.itemID = itemID;
        this.distance = distance;
    }

    public String getUID() {
        return UID;
    }

    public int getItemID() {
        return itemID;
    }

    public float getDistance() {
        return distance;
    }
}