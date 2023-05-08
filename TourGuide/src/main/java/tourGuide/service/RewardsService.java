package tourGuide.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.user.User;
import tourGuide.user.UserReward;

@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

    // proximity in miles
    private int defaultProximityBuffer = 10;
    private int proximityBuffer = defaultProximityBuffer;
    private int attractionProximityRange = 200;
    private GpsUtil gpsUtil;
    private final RewardCentral rewardsCentral;
    private Logger logger = LoggerFactory.getLogger(RewardsService.class);
    ExecutorService executorService = Executors.newFixedThreadPool(100);

    public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
        this.gpsUtil = gpsUtil;
        this.rewardsCentral = rewardCentral;
    }

    public void setProximityBuffer(int proximityBuffer) {
        this.proximityBuffer = proximityBuffer;
    }

    public void setDefaultProximityBuffer() {
        proximityBuffer = defaultProximityBuffer;
    }


//    public CompletableFuture<Void> calculateRewards(User user) {
//        List<Attraction> attractions = gpsUtil.getAttractions();
//        List<VisitedLocation> userVisitedLocations = new ArrayList<>(user.getVisitedLocations());
////        List<UserReward> userRewardList = new ArrayList<>(user.getUserRewards());
//
//        for (VisitedLocation visitedLocation : userVisitedLocations) {
//            for (Attraction attraction : attractions) {
//                if (user.getUserRewards().stream().noneMatch(reward -> reward.attraction.attractionName
//                        .equals(attraction.attractionName))) {
//                    if (nearAttraction(visitedLocation, attraction)) {
//                        UserReward userReward = new UserReward(visitedLocation, attraction, rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId()));
//                        user.addUserReward(userReward);
//                    }
//
//                }
//
//            }
//        }
//
//        return null;
//    }


//	public void calculateRewards(User user) {
//		List<VisitedLocation> userLocations = user.getVisitedLocations();
//	List<Attraction> allAttractions = gpsUtil.getAttractions();
//	List<UserReward> userRewards = user.getUserRewards();
//	// Get the attractions that the user has not visited yet
//	List<Attraction> attractionsToVisit = new CopyOnWriteArrayList<>();
//	for (Attraction attraction : allAttractions) {
//		if (userRewards.stream().noneMatch(r -> r.attraction.attractionName.equals(attraction.attractionName)))
//		{attractionsToVisit.add(attraction);
//		}


//    public void calculateRewards(User user) throws ExecutionException, InterruptedException {
//        List<Attraction> attractions = gpsUtil.getAttractions();
//        List<VisitedLocation> userVisitedLocations = new ArrayList<>(user.getVisitedLocations());
////		List<VisitedLocation> userVisitedLocations = user.getVisitedLocations();
//        List<UserReward> futures = new ArrayList<>();
//        for (VisitedLocation visitedLocation : userVisitedLocations) {
//            for (Attraction attraction : attractions) {
//                CompletableFuture<UserReward> future = CompletableFuture.supplyAsync(()
//                        ->
////				{ if (nearAttraction(visitedLocation, attraction)) {
////					return new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)); }
////					return null; }, executorService); futures.add(future); } }
//                {
//                    if (user.getUserRewards().stream().noneMatch(reward -> reward.attraction.attractionName
//                            .equals(attraction.attractionName))) {
//                        if (nearAttraction(visitedLocation, attraction)) {
//                            user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
//                        }
//                    }
//                    return null;
//                }, executorService);
//                futures.add(future.get());
//            }
//        }
//    }


//	public CompletableFuture<Void> calculateRewards(User user) {
//		List<Attraction> attractions = gpsUtil.getAttractions();
//		List<VisitedLocation> userVisitedLocations = new ArrayList<>(user.getVisitedLocations());
//		return CompletableFuture
//				.supplyAsync(() -> {
//							for (VisitedLocation visitedLocation : userVisitedLocations) {
//								for (Attraction attraction : attractions) {
//									if (user.getUserRewards().stream().noneMatch(reward -> reward.attraction.attractionName
//											.equals(attraction.attractionName))) {
//										if (nearAttraction(visitedLocation, attraction)) {
//											user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
//										}
//									}
//								}
//							}
//		return null;
//                            } 	, executorService);
//
//}


    public void calculateRewards(User user) {
        CompletableFuture.runAsync(() -> {
            CopyOnWriteArrayList<VisitedLocation> userLocations = new CopyOnWriteArrayList<>(user.getVisitedLocations());
            List<Attraction> attractions = gpsUtil.getAttractions();
            for (VisitedLocation visitedLocation : userLocations) {
                for (Attraction attr : attractions) {
                    if (nearAttraction(visitedLocation, attr)) {
                        user.addUserReward(new UserReward(visitedLocation, attr, rewardsCentral.getAttractionRewardPoints(attr.attractionId, user.getUserId())));
                        finalCalculateReward(user,visitedLocation);
                    }
                }
            }
        }, executorService);
    }

    public void finalCalculateReward(User user, VisitedLocation visitedLocation) {
        List<Attraction> attractions = gpsUtil.getAttractions();
        user.addToVisitedLocations(visitedLocation);
        for(Attraction attraction : attractions) {
            if(nearAttraction(visitedLocation, attraction)) {
                user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
            }
        }
    }


    public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
        return (getDistance(attraction, location) < attractionProximityRange);
    }

    public boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
        return (getDistance(attraction, visitedLocation.location) < proximityBuffer);
    }

    private int getRewardPoints(Attraction attraction, User user) {
        return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
    }

    public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
    }

}
