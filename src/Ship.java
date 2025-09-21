import java.io.Serializable;

public class Ship implements Serializable {
    private int size;
    private String orientation;
    private boolean[] hits;

    public Ship(int size, String orientation) {
        this.size = size;
        this.orientation = orientation.toUpperCase();
        this.hits = new boolean[size];
    }

    public int getSize() {
        return size;
    }

    public String getOrientation() {
        return orientation;
    }

    public boolean isSunk() {
        for (boolean hit : hits) {
            if (!hit) return false;
        }
        return true;
    }

    public void hit(int index) {
        if (index >= 0 && index < hits.length) {
            hits[index] = true;
        }
    }
}
