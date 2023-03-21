package tourGuide.user;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tripPricer.Provider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserFive {


private String phoneNumber;
private String emailAddress;
private Date latestLocationTimestamp;
private List<VisitedLocation> visitedLocations = new ArrayList<>();
private List<UserReward> userRewards = new ArrayList<>();
private rewardCentral.RewardCentral rewardCentral;

private List<Attraction> attractionList = new ArrayList<>();
private Attraction attraction;

private String attractionName;
private String attractionCity;
private String attractionState;
private double attractionLatitude;
private double attractionLongitude;
private Location location;

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
