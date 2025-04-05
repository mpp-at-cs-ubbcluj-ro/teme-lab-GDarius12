package ro.mpp2024.domain;

import java.util.List;

public class AgeGroup extends Entity<Integer> {
    private Integer lower;
    private Integer upper;

    public AgeGroup(Integer lower, Integer upper) {
        this.lower = lower;
        this.upper = upper;
    }

    public Integer getLower() {
        return lower;
    }

    public void setLower(Integer lower) {
        this.lower = lower;
    }

    public Integer getUpper() {
        return upper;
    }

    public void setUpper(Integer upper) {
        this.upper = upper;
    }
}
