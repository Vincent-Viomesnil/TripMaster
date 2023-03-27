package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;

public class UserFiveService {


    private RewardsService rewardsService;


    public double getDistance(Attraction attraction, VisitedLocation visitedLocation) {
        Attraction attractionDouble = attraction;
        Location visitedLocationDouble = visitedLocation.location;
        double distance = rewardsService.getDistance(attractionDouble, visitedLocationDouble);

        System.out.println("distance "+ distance + " et attractionDouble " +  attractionDouble + " et visitedLocationDouble " +  visitedLocationDouble);

        return distance;
    }
}
