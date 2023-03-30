package tourGuide.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserLocation {

private double visitedLatitude;
private double visitedLongitude;
private String userid;

public UserLocation(String userid, double visitedLongitude, double visitedLatitude) {
    this.userid = userid;
    this.visitedLongitude = visitedLongitude;
    this.visitedLatitude = visitedLatitude;
}



}
