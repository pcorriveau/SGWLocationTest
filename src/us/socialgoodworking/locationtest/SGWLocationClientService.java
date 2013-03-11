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

package us.socialgoodworking.locationtest;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import us.socialgoodworking.location.SGWLocationConstants;
import us.socialgoodworking.utility.Logging;

public class SGWLocationClientService extends IntentService {

	private static final String TAG = "SGWLocationClientService";
	
	/**
	 * Make sure to register the service by adding a <service> element to the manifest.
	 * <application>
	 * 	  <service android:name=".SGWLocationClientService" ></service>
	 * </application>   
	 */
	public SGWLocationClientService() {
		super("SGWLocationClientService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String action = intent.getAction();
		Logging.d(MainActivity.TAG, TAG, "onHandleIntent, action = " + action);
		if (action.equals(SGWLocationConstants.ACTION_ACTIVE_UPDATE)) {
			if (intent.hasExtra(SGWLocationConstants.EXTRA_KEY_LOCATION)) {
				Location l = (Location)(intent.getExtras().get(SGWLocationConstants.EXTRA_KEY_LOCATION));
				Logging.d(MainActivity.TAG, TAG, ":onHandleIntent: - lat = " + Double.toString(l.getLatitude()) + ", lon = " + Double.toString(l.getLongitude()));
				
                Intent i = new Intent(SGWLocationConstants.ACTION_ACTIVE_UPDATE);
                i.putExtra(SGWLocationConstants.EXTRA_KEY_LOCATION, l);
                Logging.d(MainActivity.TAG, TAG, ":onHandleIntent(), sending location update");
                LocalBroadcastManager.getInstance(this).sendBroadcast(i);
			}
		}
		else if (action.equals(SGWLocationConstants.ACTION_PROVIDER_DISABLED)) {
			Logging.d(MainActivity.TAG, TAG, ":onHandleIntent(), provider disabled");
            Intent i = new Intent(SGWLocationConstants.ACTION_PROVIDER_DISABLED);
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
		}
	}
}
