package com.yugabyte.com;

import java.util.List;

public class PointsOfInterestResponse {
    private List<PointOfInterest> pointsOfInterest;

    public PointsOfInterestResponse(List<PointOfInterest> pointsOfInterest) {
        this.pointsOfInterest = pointsOfInterest;
    }

    public List<PointOfInterest> getPointsOfInterest() {
        return pointsOfInterest;
    }
}
