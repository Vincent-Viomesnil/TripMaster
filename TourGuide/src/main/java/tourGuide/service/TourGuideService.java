package tourGuide.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.helper.InternalTestHelper;
import tourGuide.tracker.Tracker;
import tourGuide.user.*;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TourGuideService {
	private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
	private final GpsUtil gpsUtil;
	private final RewardsService rewardsService;

	private final TripPricer tripPricer = new TripPricer();
	public final Tracker tracker;
	boolean testMode = true;
	ExecutorService executorService = Executors.newFixedThreadPool(100);
//	public CountDownLatch usersCountDownLatch;
	
	public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService) {
		this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsService;
		
		if(testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			initializeInternalUsers();
//			usersCountDownLatch = new CountDownLatch(this.getAllUsers().size());
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker(this);
		addShutDownHook();
	}
	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}

	public VisitedLocation getUserLocation(User user) throws ExecutionException, InterruptedException {
		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
			user.getLastVisitedLocation() :
			trackUserLocation(user).get();
		return visitedLocation;
	}

//	public VisitedLocation trackUserLocation(User user) throws ExecutionException, InterruptedException {
//		Future<VisitedLocation> visitedLocation = executorService.submit(
//				() -> gpsUtil.getUserLocation(user.getUserId()));
//			user.addToVisitedLocations(visitedLocation);
//			rewardsService.calculateRewards(user);
//			System.out.println("User " + user.getUserName() + " tracked at location " + visitedLocation.get().location);
//			return visitedLocation.get();
//
//	}

//	public VisitedLocation trackUserLocation(User user) {
//
//		CompletableFuture.supplyAsync(() -> {
//					VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());//creation d'une visitedLocation random
//					user.addToVisitedLocations(visitedLocation);//ajout de la visitedLocation dans la List de VisitedLocations du user
//					rewardsService.calculateRewards(user);//Méthode pour calculer un reward
//					System.out.println("User " + user.getUserName() + " tracked at location " + visitedLocation.location);
//					return visitedLocation;
//				}, executorService);
//		return null;
//	}

//public VisitedLocation trackUserLocation(User user) throws ExecutionException, InterruptedException {
//		Future<VisitedLocation> visitedLocation = executorService.submit(() -> gpsUtil.getUserLocation(user.getUserId()));
//		user.addToVisitedLocations(visitedLocation.get());
//		rewardsService.calculateRewards(user);
//		return visitedLocation.get();
//
//}

		public CompletableFuture<VisitedLocation> trackUserLocation(User user)  {
			Locale.setDefault(Locale.US);
				return CompletableFuture
						.supplyAsync(() -> {
							VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
							user.addToVisitedLocations(visitedLocation);
							rewardsService.calculateRewards(user);
							return visitedLocation;
						}, executorService);
//						.thenApply(visitedLocation -> {
//							System.out.println("User " + user.getUserName() + " tracked at location " + visitedLocation.location.toString());
//							return visitedLocation;
//						});

		}

//	public Future<VisitedLocation> trackUserLocation(User user) {
//		Locale.setDefault(Locale.US);
//		return CompletableFuture.supplyAsync(() -> {
//			VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
//			user.addToVisitedLocations(visitedLocation);
//			rewardsService.calculateRewards(user);
//			return visitedLocation;
//		}, executorService);
//	}

	public void awaitTrackUserLocationEnding() {
		executorService.shutdown();
		try {
			if (!executorService.awaitTermination(15, TimeUnit.MINUTES)) {
				executorService.shutdownNow();
			}

		} catch (InterruptedException e) {
			executorService.shutdownNow();
			Thread.currentThread().interrupt();

			executorService = Executors.newFixedThreadPool(100);
		}
	}
//public VisitedLocation trackUserLocation(User user) throws ExecutionException, InterruptedException {
//	Locale.setDefault(Locale.US);
//	CompletableFuture<VisitedLocation> visitedLocationCF = CompletableFuture.supplyAsync(() -> {
//				VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
//				user.addToVisitedLocations(visitedLocation);
//				rewardsService.calculateRewards(user);
//				return visitedLocation;
//			});
//	VisitedLocation visitedLocation = visitedLocationCF.get();
//	return visitedLocation;
//}
	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}
	
	public List<User> getAllUsers() {
		return internalUserMap.values().stream().collect(Collectors.toList());
	}
	
	public void addUser(User user) {
		if(!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}
	
	public List<Provider> getTripDeals(User user) {
		int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(), 
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);

//		providers.addAll(tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
//				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints));
// Si besoin de setter la list de providers à 10
		user.setTripDeals(providers);
		//Provider.class ne peut être modifié
		return providers;
	}



	public UserPreferences updateUserPreferences(String userName, UserPreferences userPreferences){

		User user = getUser(userName);
		user.setUserPreferences(userPreferences);

		return userPreferences;
	}


	public UserPreferences getUserPreferences(String userName){
		return internalUserMap.get(userName).getUserPreferences();
	}


//	public UserPreferences updateUserPreferences(User user){
//		List<UserPreferences> userPreferencesList = new ArrayList<>();
//		for (UserPreferences userPreferences : userPreferencesList) {
//			userPreferences.setTripDuration(user.getUserPreferences().getTripDuration());
//			userPreferences.setTicketQuantity(user.getUserPreferences().getTicketQuantity());
//			userPreferences.setNumberOfAdults(user.getUserPreferences().getNumberOfAdults());
//			userPreferences.setNumberOfChildren(user.getUserPreferences().getNumberOfChildren());
//
//			return userPreferences;
//		}
//		return null;
//	}



//	public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
//		List<Attraction> nearbyAttractions = new ArrayList<>();
//		for (Attraction attraction : gpsUtil.getAttractions()) {
//			if (rewardsService.isWithinAttractionProximity(attraction, visitedLocation.location)) {
//				nearbyAttractions.add(attraction);
//			}
//		}
//		return nearbyAttractions;
//	}
//

//	public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
//		List<Attraction> nearbyFiveAttractions =  new ArrayList<>();
//		for (Attraction attraction :  gpsUtil.getAttractions()) {
//			if (rewardsService.nearAttraction(visitedLocation, attraction)) {
//				nearbyFiveAttractions = gpsUtil.getAttractions()
//						.stream()
//						.limit(5)
//						.collect(Collectors.toList());
//			}
//		}
	public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
		List<Attraction> nearbyFiveAttractions =
			 gpsUtil.getAttractions()
					.stream()
					 .sorted(Comparator.comparing(attraction -> rewardsService.getDistance(visitedLocation.location, attraction)))
//					.sorted(Comparator.comparing(attraction -> rewardsService.nearAttraction(visitedLocation, attraction)))
					.limit(5)
					.collect(Collectors.toList());

		return nearbyFiveAttractions;
	}

	public List<UserLocation> getAllCurrentLocations(List<User> userList) {

		List<UserLocation> allCurrentLocations = new ArrayList<>();
		for (User user : userList) {
			UserLocation userLocation = new UserLocation();
			userLocation.setUserid(user.getUserId().toString());
			userLocation.setVisitedLongitude(user.getLastVisitedLocation().location.longitude);
			userLocation.setVisitedLatitude(user.getLastVisitedLocation().location.latitude);
			allCurrentLocations.add(userLocation);
		}
		return allCurrentLocations;
	}

	public List<UserAttraction> getAttractions(VisitedLocation visitedLocation) {
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);
		List<Attraction> attractionList = tourGuideService.getNearByAttractions(visitedLocation);
		List<UserAttraction> userAttractionList = new ArrayList<>();
		for (Attraction attraction : attractionList) {
			UserAttraction userAttraction1 = new UserAttraction();
			rewardCentral.RewardCentral rewardCentral = new RewardCentral();
			userAttraction1.setAttractionName(attraction.attractionName);
			userAttraction1.setAttractionLatitude(attraction.latitude);
			userAttraction1.setAttractionLongitude(attraction.longitude);
			userAttraction1.setVisitedLatitude(visitedLocation.location.latitude);
			userAttraction1.setVisitedLongitude(visitedLocation.location.longitude);
			userAttraction1.setDistanceUser(rewardsService.getDistance(attraction, visitedLocation.location));
			userAttraction1.setRewardPoints(rewardCentral.getAttractionRewardPoints(attraction.attractionId, visitedLocation.userId));
			userAttractionList.add(userAttraction1);
		}

		return userAttractionList;
	}



   private void addShutDownHook() {
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					tracker.stopTracking();
				}
			});
		}

   /**********************************************************************************
    *
    * Methods Below: For Internal Testing
    *
    **********************************************************************************/
	private static final String tripPricerApiKey = "test-server-api-key";
	// Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
	private final Map<String, User> internalUserMap = new HashMap<>();
	private void initializeInternalUsers() {
		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);
			
			internalUserMap.put(userName, user);
		});
		logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
	}
	
	private void generateUserLocationHistory(User user) {
	IntStream.range(0, 3).forEach(i -> user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()),  getRandomTime())));
	}
	
	private double generateRandomLongitude() {
		double leftLimit = -180;
	    double rightLimit = 180;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}
	
	private double generateRandomLatitude() {
		double leftLimit = -85.05112878;
	    double rightLimit = 85.05112878;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}
	
	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
	    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}

}
