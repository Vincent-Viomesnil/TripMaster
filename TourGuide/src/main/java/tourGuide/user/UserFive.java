package tourGuide.user;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserFive {



//private List<VisitedLocation> visitedLocations = new ArrayList<>();
//private List<UserReward> userRewards = new ArrayList<>();
//private rewardCentral.RewardCentral rewardCentral;


private Attraction attraction;
private Location location;
private VisitedLocation visitedLocation;
private String attractionName;
private double attractionLatitude;
private double attractionLongitude;
private double visitedLatitude;
private double visitedLongitude;



    //    The distance in miles between the user's location and each of the attractions
public double getDistanceUserFive(Attraction attraction, VisitedLocation visitedLocation) {
    Attraction attractionDouble = attraction;
    Location visitedLocationDouble = visitedLocation.location;
    double distanceLatitudeUserFive = visitedLocationDouble.latitude - attraction.latitude;
    double distanceLongitudeUserFive = visitedLocationDouble.longitude - attraction.longitude;

    System.out.println("distanceLatitude "+ distanceLatitudeUserFive + " et distanceLongitude " +  distanceLongitudeUserFive);
    return distanceLatitudeUserFive + distanceLongitudeUserFive;
}



//    public User(UUID userId, String userName, String phoneNumber, String emailAddress) {
//        this.userId = userId;
//        this.userName = userName;
//        this.phoneNumber = phoneNumber;
//        this.emailAddress = emailAddress;
//    }


//    public getUserFiveAttractions(List<Attraction>)

    // 	  Instead: Get the closest five tourist attractions to the user - no matter how far away they are.
// 	  Return a new JSON object that contains:
//    	 Name of Tourist attraction,
//         Tourist attractions lat/long,
//         The user's location lat/long,
//         The distance in miles between the user's location and each of the attractions.
//         The reward points for visiting each Attraction.
//            Note: Attraction reward points can be gathered from RewardsCentral
}
