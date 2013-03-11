/*
 * Copyright (C) 2012 Paul Corriveau
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.socialgoodworking.locationtest.test;

import java.util.List;
import us.socialgoodworking.location.mock.MockLocation;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.test.AndroidTestCase;

/**
 * Make sure that none of the hardware providers (GPS, Network) are enabled before running these tests. 
 */
public class MockLocationTest extends AndroidTestCase {
	private MockLocation mockLocation;
	private static String gpsProvider = LocationManager.GPS_PROVIDER;
	private static String networkProvider = LocationManager.NETWORK_PROVIDER;
	private static String passiveProvider = LocationManager.PASSIVE_PROVIDER;
	private LocationManager locationManager;
	
	// Point 1
	private static double lat = 40.76793169992044;
	private static double lon = -73.98180484771729;
	
	// Point 2
	private static double nextLat = 40.767269460358875;
	private static double nextLon = -73.98184508085251; 

	// This is the bearing (truncated) between points 1 and 2.
	private static int expectedBearing = 182;
	
	// This is the speed (truncated) between points 1 and 2 in 10 seconds. 73.66591659710001 / 10 ~ 7 meters per second
	private static int expectedSpeed = 7;
	
	// This is the distance in meters between points 1 and 2. 
	private static double distance = 73.66591659710001;
	
	private static long timeToTravel = 10000;
	
	private static float speed = 10;
	private static float bearing = 180;

	private static String TAG = "SGWLocationTest";


	protected void setUp () {
		locationManager = (LocationManager)(getContext().getSystemService(Context.LOCATION_SERVICE));
	}
	
	public void testBuildNullMockProvider() {
		try {
			mockLocation = new MockLocation.MockLocationBuilder(getContext(), null).build();
			fail("MockLocation created with null provider name");
		}
		
		catch (IllegalArgumentException iae) {
			assertTrue(true);
		}
	}

	public void testBuildEmptyMockProvider() {
		try {
			mockLocation = new MockLocation.MockLocationBuilder(getContext(), "").build();
			fail("MockLocation created with empty provider name");
		}
		
		catch (IllegalArgumentException iae) {
			assertTrue(true);
		}
	}

	public void testBuildNetworkMockProvider() {
		mockLocation = new MockLocation.MockLocationBuilder(getContext(), networkProvider).build();
		assertTrue(mockLocation != null);

		mockLocation.removeProvider();
		List<String> l = locationManager.getProviders(true);
		assertTrue(l == null || l.get(0).equals(passiveProvider));
		mockLocation = null;
	}

	public void testBuildGpsMockProvider() {
		mockLocation = new MockLocation.MockLocationBuilder(getContext(), gpsProvider).build();
		assertTrue(mockLocation != null);
		
		mockLocation.removeProvider();
		List<String> l = locationManager.getProviders(true);
		assertTrue(l == null || l.get(0).equals(passiveProvider));
		mockLocation = null;
	}
	
	public void testSetUpMockProvider() {
		mockLocation = init(gpsProvider);
		assertTrue(mockLocation.providerEnabled());

		mockLocation.removeProvider();
		List<String> l = locationManager.getProviders(true);
		assertTrue(l == null || l.get(0).equals(passiveProvider));
		mockLocation = null;
	}
	public void testSetMockLocationDoubleDouble() {
		mockLocation = init(gpsProvider);
		assertTrue(mockLocation.providerEnabled());
		
		// Required to make sure location gets updated. See onLocationChange() below.
		locationManager.requestLocationUpdates(gpsProvider, 0, 0, onLocationChange);

		mockLocation.setMockLocation(lat, lon);

		// Give the location manager service time to update the location...
		try {
			Thread.sleep(250);
		} 
		catch (InterruptedException e) {
		}
		
		Location l = locationManager.getLastKnownLocation(gpsProvider);
		assertTrue("location = null", l != null);
		assertTrue("getLastKnownLocation", l.getLatitude() == lat && l.getLongitude() == lon);

		//locationManager.removeUpdates(onLocationChange);
		//mockLocation.removeProvider();
		//mockLocation = null;
	}

	public void testSetMockLocationDoubleDoubleDoubleFloatFloat() {
		mockLocation = init(gpsProvider);
		assertTrue(mockLocation.providerEnabled());

		// Required to make sure location gets updated. See onLocationChange() below.
		locationManager.requestLocationUpdates(gpsProvider, 0, 0, onLocationChange);

		mockLocation.setMockLocation(lat, lon, 0, speed, bearing);

		// Give the location manager service time to update the location...
		try {
			Thread.sleep(250);
		} 
		catch (InterruptedException e) {
		}
		
		Location l = locationManager.getLastKnownLocation(gpsProvider);
		assertTrue("location = null", l != null);
		assertTrue("getLastKnownLocation", l.getLatitude() == lat && l.getLongitude() == lon && l.getSpeed() == speed && l.getBearing() == bearing);
	}

	public void testProviderEnabled() {
		mockLocation = init(gpsProvider);
		assertTrue(mockLocation.providerEnabled());

		mockLocation.removeProvider();
		mockLocation = null;
	}

	public void testRemoveProvider() {
		mockLocation = new MockLocation.MockLocationBuilder(getContext(), gpsProvider).build();
		assertTrue(mockLocation != null);
		
		mockLocation.removeProvider();
		List<String> l = locationManager.getProviders(true);
		assertTrue(l == null || l.get(0).equals(passiveProvider));
		mockLocation = null;
	}

	public void testGetBearingLocationLocation() {
		mockLocation = new MockLocation.MockLocationBuilder(getContext(), gpsProvider).build();
		assertTrue(mockLocation != null);

		Location l = new Location(gpsProvider);
		l.setLatitude(40.76793169992044);
		l.setLongitude(-73.98180484771729);
		
		Location c = new Location(gpsProvider);
		c.setLatitude(40.767269460358875);
		c.setLongitude(-73.98184508085251);

		float bearing = mockLocation.getBearing(l, c);
		assertTrue((int)bearing == expectedBearing);
		
		mockLocation = null;
	}

	public void testGetBearingDoubleDoubleDoubleDouble() {
		mockLocation = new MockLocation.MockLocationBuilder(getContext(), gpsProvider).build();
		assertTrue(mockLocation != null);
		
		int b = (int)mockLocation.getBearing(lat, lon, nextLat, nextLon);
		assertTrue(b == expectedBearing);

		mockLocation = null;
	}

	public void testGetSpeedLocationLocationLong() {
		mockLocation = new MockLocation.MockLocationBuilder(getContext(), gpsProvider).build();
		assertTrue(mockLocation != null);
		
		Location l = new Location(gpsProvider);
		l.setLatitude(40.76793169992044);
		l.setLongitude(-73.98180484771729);
		
		Location c = new Location(gpsProvider);
		c.setLatitude(40.767269460358875);
		c.setLongitude(-73.98184508085251);
		
		int s = (int)mockLocation.getSpeed(l, c, timeToTravel);
		assertTrue((int)s == expectedSpeed);
		
		mockLocation = null;
	}

	public void testGetSpeedDoubleDoubleDoubleDoubleLong() {
		mockLocation = new MockLocation.MockLocationBuilder(getContext(), gpsProvider).build();
		assertTrue(mockLocation != null);
		
		Location l = new Location(gpsProvider);
		l.setLatitude(40.76793169992044);
		l.setLongitude(-73.98180484771729);
		
		Location c = new Location(gpsProvider);
		c.setLatitude(40.767269460358875);
		c.setLongitude(-73.98184508085251);
		
		int s = (int)mockLocation.getSpeed(lat, lon, nextLat, nextLon, timeToTravel);
		assertTrue((int)s == expectedSpeed);
		
		mockLocation = null;
	}

	/************* Helper methods *************/
	private MockLocation init(String providerName) {
		MockLocation ml = new MockLocation.MockLocationBuilder(getContext(), providerName).build();
		
		if ( ml == null)
			fail("MockLocationBuilder.build returned null");

		try {
			ml.setUpMockProvider();
		}
		
		catch (SecurityException se) {
			fail(se.getMessage());
		}
		
		return ml;
	}
	
	// "LocationManagerService.handleLocationChangedLocked checks for the presence of interested Receivers
	//  before updating mLastKnownLocation. If there are no subscribers, it doesn't do the update."
	// See https://code.google.com/p/android/issues/detail?id=4036#c3
	protected LocationListener onLocationChange = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {}

		@Override
		public void onProviderDisabled(String provider) {}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	};
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		if (mockLocation != null) {
			locationManager.removeUpdates(onLocationChange);
			mockLocation.removeProvider();
			mockLocation = null;
		}
	}
}
