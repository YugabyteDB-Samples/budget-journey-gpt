package com.yugabyte.com;

import java.util.List;

public class PointsOfInterestResponse {
    private List<PointOfInterest> pointsOfInterest;

    private String error;

    public PointsOfInterestResponse() {
    }

    public List<PointOfInterest> getPointsOfInterest() {
        return pointsOfInterest;
    }

    public void setPointsOfInterest(List<PointOfInterest> pointsOfInterest) {
        this.pointsOfInterest = pointsOfInterest;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
