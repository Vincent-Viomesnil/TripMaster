package tourGuide;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.jsoniter.output.JsonStream;

import gpsUtil.location.VisitedLocation;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tourGuide.user.UserAttraction;
import tourGuide.user.UserLocation;
import tourGuide.user.UserPreferences;
import tripPricer.Provider;

@RestController
public class TourGuideController {

    @Autowired
    TourGuideService tourGuideService;

    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    @RequestMapping("/getLocation")
    public String getLocation(@RequestParam String userName) {
        List<UserAttraction> fiveTouristAttractions = tourGuideService.getAttractions(getUser(userName).getLastVisitedLocation());
        return JsonStream.serialize(fiveTouristAttractions);
    }

    @RequestMapping("/getNearbyAttractions")
    public String getNearbyAttractions(@RequestParam String userName) throws ExecutionException, InterruptedException {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return JsonStream.serialize(tourGuideService.getNearByAttractions(visitedLocation));
    }

    @RequestMapping("/getRewards") //Ne pas modifier
    public String getRewards(@RequestParam String userName) {
        return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    }

    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() {
        List<UserLocation> allCurrentLocations = tourGuideService.getAllCurrentLocations(tourGuideService.getAllUsers());
        return JsonStream.serialize(allCurrentLocations);
    }

    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
        List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
        return JsonStream.serialize(providers);
    }

    @PutMapping("/updateUserPreferences/{userName}")
    public UserPreferences updateUserPreferences(@PathVariable String userName, @RequestBody UserPreferences userPreferences) {
        //ResponseEntity
        return tourGuideService.updateUserPreferences(userName, userPreferences);
    }

    @RequestMapping("/getUserPreferences/{userName}")
    public UserPreferences getUserPreferences(@PathVariable String userName) {
        //ResponseEntity
        return tourGuideService.getUserPreferences(userName);
    }

    private User getUser(String userName) {
        return tourGuideService.getUser(userName);
    }


}