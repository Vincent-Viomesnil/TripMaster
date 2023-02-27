package tourGuide;

import static org.junit.Assert.*;

import java.util.*;

import gpsUtil.location.Location;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tourGuide.user.UserReward;

public class TestRewardsService {

	@BeforeClass
	public static void setUpAllTests() {
		Locale.setDefault(Locale.US);
	}

	@Test
	public void userGetRewards() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsUtil.getAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		tourGuideService.trackUserLocation(user);
		List<UserReward> userRewards = user.getUserRewards();
		tourGuideService.tracker.stopTracking();
		assertTrue(userRewards.size() == 1);
	}
	
	@Test
	public void isWithinAttractionProximity() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		Attraction attraction = gpsUtil.getAttractions().get(0);
		Location location = new Location(33.817595, -117.922008); // Location settée similaire à l'attraction numero 1
		assertTrue(rewardsService.isWithinAttractionProximity(attraction, location));
	}

	@Test
	public void isNotWithinAttractionProximity() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		Attraction attraction = gpsUtil.getAttractions().get(0);
		Location location = new Location(-70, 120); // Location settée avec des données opposées à l'attraction numero 1
		assertFalse(rewardsService.isWithinAttractionProximity(attraction, location));
	}
	
//	@Ignore // Needs fixed - can throw ConcurrentModificationException
	@Test
	public void nearAllAttractions() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		rewardsService.setProximityBuffer(100);
		//gpsUtil et rewardsCentral = no field display

		InternalTestHelper.setInternalUserNumber(1);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
//add
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		List<VisitedLocation> userLocations = user.getVisitedLocations();
//		List<Attraction> attractions = gpsUtil.getAttractions();
		Attraction attraction = gpsUtil.getAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));

//		tourGuideService.trackUserLocation(user);

		rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));
		List<UserReward> userRewards = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0));

		tourGuideService.tracker.stopTracking();
//		private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
//			return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
//		}
		assertEquals(gpsUtil.getAttractions().size(), userRewards.size());

//		assertThat(gpsUtil.getAttractions().(userRewards));
		// gpsUtil.getAttractions().size() = 26
	}

	@Test
	public void nearAttraction() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		rewardsService.setProximityBuffer(100);


		InternalTestHelper.setInternalUserNumber(1);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		Attraction attraction = gpsUtil.getAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));

		rewardsService.calculateRewards(user);
		List<UserReward> userRewards = tourGuideService.getUserRewards(user);

		tourGuideService.tracker.stopTracking();

		assertTrue(rewardsService.nearAttraction(user.getLastVisitedLocation(),attraction));

	}
	
}
