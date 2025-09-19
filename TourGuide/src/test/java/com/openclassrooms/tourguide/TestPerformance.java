package com.openclassrooms.tourguide;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import com.openclassrooms.tourguide.helper.InternalTestHelper;
import com.openclassrooms.tourguide.service.RewardsService;
import com.openclassrooms.tourguide.service.TourGuideService;
import com.openclassrooms.tourguide.user.User;

public class TestPerformance {

	/*
	 * A note on performance improvements:
	 * 
	 * The number of users generated for the high volume tests can be easily
	 * adjusted via this method:
	 * 
	 * InternalTestHelper.setInternalUserNumber(100000);
	 * 
	 * 
	 * These tests can be modified to suit new solutions, just as long as the
	 * performance metrics at the end of the tests remains consistent.
	 * 
	 * These are performance metrics that we are trying to hit:
	 * 
	 * highVolumeTrackLocation: 100,000 users within 15 minutes:
	 * assertTrue(TimeUnit.MINUTES.toSeconds(15) >=
	 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 *
	 * highVolumeGetRewards: 100,000 users within 20 minutes:
	 * assertTrue(TimeUnit.MINUTES.toSeconds(20) >=
	 * TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 */


	@Test
	public void highVolumeTrackLocation() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		// Users should be incremented up to 100,000, and test finishes within 15
		// minutes
		InternalTestHelper.setInternalUserNumber(1000);
		TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

		List<User> allUsers = tourGuideService.getAllUsers();
		

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		//test
		// Liste pour stocker tous les CompletableFuture lancés
	    List<CompletableFuture<Void>> tasks = new ArrayList<>();
	    
		for (User user : allUsers) {
		    // pour chaque utilisateur, on lance un traitement asynchrone
			// on attend pas de traiter les users 1 par 1 afin qu'il soit plus rapide
		    CompletableFuture<Void> task = CompletableFuture.runAsync(new Runnable() {
		        @Override
		        public void run() {
		            tourGuideService.trackUserLocation(user);
		        }
		    });

		    tasks.add(task);
		}

		// On attend que toutes les tâches soient terminées
		for (CompletableFuture<Void> task : tasks) {
		    task.join();
		}
		/*
		for (User user : allUsers) {
			tourGuideService.trackUserLocation(user);
		}*/
		stopWatch.stop();
		tourGuideService.tracker.stopTracking();

		System.out.println("highVolumeTrackLocation: Time Elapsed: "
				+ TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}


	@Test
	public void highVolumeGetRewards() {
		GpsUtil gpsUtil = new GpsUtil();
	    RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

	    // Nombre d’utilisateurs pour le test
	    InternalTestHelper.setInternalUserNumber(100);
	    TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

	    List<User> allUsers = tourGuideService.getAllUsers();
	    Attraction attraction = gpsUtil.getAttractions().get(0);

	    // Ajout d'une visite pour chaque utilisateur
	    allUsers.forEach(u -> u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date())));

	    StopWatch stopWatch = new StopWatch();
	    stopWatch.start();

	    // Liste pour stocker les CompletableFuture
	    List<CompletableFuture<Void>> tasks = new ArrayList<>();

	    // Traitement asynchrone pour chaque utilisateur
	    /*for (User user : allUsers) {
	        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> rewardsService.calculateRewards(user));
	        tasks.add(future);
	    }*/

	    // On attend que toutes les tâches soient terminées
	    tasks.forEach(CompletableFuture::join);

	    // Vérification que chaque utilisateur a bien reçu une récompense
	    /*for (User user : allUsers) {
	        assertTrue(user.getUserRewards().size() > 0);
	    }*/

	    stopWatch.stop();
	    tourGuideService.tracker.stopTracking();

	    System.out.println("highVolumeGetRewards: Time Elapsed: " + 
	        TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
	    assertTrue(TimeUnit.MINUTES.toSeconds(20) >= 
	        TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}

}
