package tourGuide.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserAttraction {

private String attractionName;
private double attractionLatitude;
private double attractionLongitude;
private double visitedLatitude;
private double visitedLongitude;
private double distanceUser;
private int rewardPoints;


}
