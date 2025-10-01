# Technologies

> Java 17  
> Spring Boot 3.X  
> JUnit 5  

# How to have gpsUtil, rewardCentral and tripPricer dependencies available ?

> Run : 
- mvn install:install-file -Dfile=/libs/gpsUtil.jar -DgroupId=gpsUtil -DartifactId=gpsUtil -Dversion=1.0.0 -Dpackaging=jar  
- mvn install:install-file -Dfile=/libs/RewardCentral.jar -DgroupId=rewardCentral -DartifactId=rewardCentral -Dversion=1.0.0 -Dpackaging=jar  
- mvn install:install-file -Dfile=/libs/TripPricer.jar -DgroupId=tripPricer -DartifactId=tripPricer -Dversion=1.0.0 -Dpackaging=jar

UML

```mermaid
classDiagram
    class User {
      - UUID userId
      - String userName
      - List visitedLocations
      - List userRewards
    }
    class VisitedLocation {
      - Location location
      - Date timeVisited
    }
    class Attraction {
      - String attractionName
      - Location location
    }
    class UserReward {
      - VisitedLocation visitedLocation
      - Attraction attraction
      - int rewardPoints
    }
    class TourGuideService {
      + trackUserLocation(user)
      + getNearbyAttractions(user)
    }
    class GpsUtil {
      + getUserLocation(userId)
      + getAttractions()
    }
    class RewardsService {
      + calculateRewards(user)
    }
    class Tracker {
      + run()
      + stopTracking()
    }

    User --> VisitedLocation : 1..*
    User --> UserReward : 0..*
    UserReward --> Attraction : 1
    UserReward --> VisitedLocation : 1
    TourGuideService --> GpsUtil : uses
    TourGuideService --> RewardsService : uses
    TourGuideService --> Tracker : controls
    RewardsService --> Attraction : uses
    RewardsService --> UserReward : creates
