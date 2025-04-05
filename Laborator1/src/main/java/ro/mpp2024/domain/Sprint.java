package ro.mpp2024.domain;


public class Sprint extends Entity<Integer> {
    private Float distance;

    public Sprint(Float distance) {
        this.distance = distance;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }
}

