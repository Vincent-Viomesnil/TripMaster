package tourGuide.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserLocation {


private String userid;
private double visitedLatitude;
private double visitedLongitude;


public UserLocation(String userid, double visitedLongitude, double visitedLatitude) {
    this.userid = userid;
    this.visitedLongitude = visitedLongitude;
    this.visitedLatitude = visitedLatitude;
}



}
