package com.yugabyte.com.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CityTripRepository extends JpaRepository<CityTrip, Integer> {
    @Query("SELECT pointsOfInterest FROM CityTrip WHERE cityName=?1 and budget=?2 and region=?3")
    String findPointsOfInterest(String cityName, Integer budget, String region);
}
