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

private String attractionName;
private double attractionLatitude;
private double attractionLongitude;
private double visitedLatitude;
private double visitedLongitude;
private double distanceUser;


}
