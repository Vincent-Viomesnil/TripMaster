package tourGuide;

import java.util.List;

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

//      TODO: Change this method to no longer return a List of Attractions.
// 	  Instead: Get the closest five tourist attractions to the user - no matter how far away they are.
// 	  Return a new JSON object that contains:
//    	 Name of Tourist attraction,
//       Tourist attractions lat/long,
//       The user's location lat/long,
//       The distance in miles between the user's location and each of the attractions.
//       The reward points for visiting each Attraction.
//          Note: Attraction reward points can be gathered from RewardsCentral

    }
    @RequestMapping("/getNearbyAttractions") 
    public String getNearbyAttractions(@RequestParam String userName) {
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
    	return JsonStream.serialize(tourGuideService.getNearByAttractions(visitedLocation));
    }
    
    @RequestMapping("/getRewards") //Ne pas modifier
    public String getRewards(@RequestParam String userName) {
    	return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    }
    
    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations()  {

    	// TODO: Get a list of every user's most recent location as JSON
    	//- Note: does not use gpsUtil to query for their current location, 
    	//        but rather gathers the user's current location from their stored location history.
    	//
    	// Return object should be the just a JSON mapping of userId to Locations similar to:
    	//     {
    	//        "019b04a9-067a-4c76-8817-ee75088c3822": {"longitude":-48.188821,"latitude":74.84371} 
    	//        ...
    	//     }

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

    //Vérifier user => màj dans la liste des intenaluser (userPreferences)


//    @PutMapping("/updateUserPreferences")
//    public String updateUserPreferences(@RequestParam String userName, @RequestParam int tripDuration,
//                                        @RequestParam int ticketQuantity, @RequestParam int numberOfAdults,
//                                        @RequestParam int numberOfChildren) {
//        UserPreferences userPreferences =
//                tourGuideService.updateUserPreferences(tripDuration, ticketQuantity,numberOfAdults, numberOfChildren);
//        return JsonStream.serialize(userPreferences);
//    }

//    @PutMapping("/updateUserPreferences")
//    public String updateUserPreferences(@RequestParam String userName) {
//        UserPreferences userPreferences =
//                tourGuideService.updateUserPreferences(getUser(userName));
//        return JsonStream.serialize(userPreferences);
//    }

    
    private User getUser(String userName) {
    	return tourGuideService.getUser(userName);
    }
   

}