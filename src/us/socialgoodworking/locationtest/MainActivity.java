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

import java.util.List;

import android.location.Criteria;
import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import us.socialgoodworking.locationtest.R;
import us.socialgoodworking.location.*;
import us.socialgoodworking.utility.Logging;

/*
Ask user to turn on gps...
android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
*/

public class MainActivity extends Activity {

	protected static final String TAG = "SGWLocationTest";
	private SGWLocation sgwLocation;
	private TextView txtStatus;
	private TextView txtLocation;
	private TextView txtOther;
	private Criteria criteria;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		txtStatus = (TextView)findViewById(R.id.txtStatus);
		txtLocation = (TextView)findViewById(R.id.txtLocation);
		txtOther = (TextView)findViewById(R.id.txtOther);
		
       	Button start = (Button) findViewById(R.id.btnStart);
       	start.setOnClickListener(clickListener);

       	Button stop = (Button) findViewById(R.id.btnStop);
       	stop.setOnClickListener(clickListener);

       	Button b = (Button) findViewById(R.id.btnEnabled);
       	b.setOnClickListener(clickListener);

       	b = (Button) findViewById(R.id.btnBestProvider);
       	b.setOnClickListener(clickListener);
       	
       	b = (Button) findViewById(R.id.btnBestLocation);
       	b.setOnClickListener(clickListener);
       	
       	criteria = new Criteria();
       	criteria.setAccuracy(SGWLocationConstants.DEFAULT_ACCURACY);
       	criteria.setPowerRequirement(SGWLocationConstants.DEFAULT_POWER);
       	criteria.setCostAllowed(true);
       	
       	sgwLocation = new SGWLocation(getApplicationContext(), SGWLocationClientService.class, 3000, 0);   	
	}
	
    @Override
    protected void onPause() {
        super.onPause();
        Logging.d(TAG, "onPause", "unregisterReceiver");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onLocationUpdate);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Logging.d(TAG, "onResume", "registerReceiver");
        LocalBroadcastManager.getInstance(this).registerReceiver(onLocationUpdate, new IntentFilter(SGWLocationConstants.ACTION_ACTIVE_UPDATE));        
    }
    
    private BroadcastReceiver onLocationUpdate= new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(SGWLocationConstants.ACTION_ACTIVE_UPDATE)) {
				Logging.d(TAG, "onLocationUpdate", "action = " + action);
				if (intent.hasExtra(SGWLocationConstants.EXTRA_KEY_LOCATION)) {
					Location l = (Location)(intent.getExtras().get(SGWLocationConstants.EXTRA_KEY_LOCATION));
					Logging.d(MainActivity.TAG, TAG, ":onHandleIntent: - lat = " + Double.toString(l.getLatitude()) + ", lon = " + Double.toString(l.getLongitude()));
					txtLocation.setText(Double.toString(l.getLatitude()) + ", lon = " + Double.toString(l.getLongitude()));
				}
			}
			else if (action.equals(SGWLocationConstants.ACTION_PROVIDER_DISABLED)) {
				txtStatus.setText("location provider disabled");
				Logging.d(TAG, "onLocationUpdate", "action = " + action);
			}
		}
    };
	
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        	
        	if (v.getId() == R.id.btnStart) {
        		Logging.d(MainActivity.TAG, TAG, ":Starting sgwlocation");
                LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(onLocationUpdate, new IntentFilter(SGWLocationConstants.ACTION_ACTIVE_UPDATE));
        		if (sgwLocation.start()) {
        			txtStatus.setText("Starting library location providers");
            		Logging.d(MainActivity.TAG, TAG, "sgwLocation started");
        		}
        		else {
        			txtStatus.setText("Failed to start SGWLocation");
            		Logging.d(MainActivity.TAG, TAG, "Failed to start SGWLocation");
        		}
        	}
        	else if (v.getId() == R.id.btnStop) {
        		Logging.d(MainActivity.TAG, TAG, ":STOPPING sgwLocation");
        		sgwLocation.stop();
        		LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(onLocationUpdate);
    			txtStatus.setText("Stopping library location providers");
    			txtLocation.setText("");
        	}
        	else if (v.getId() == R.id.btnEnabled) {
        		List<String> p = sgwLocation.getAllEnabledProviders();
        		txtOther.setText("enabled providers: " + (p != null ? p : "null"));
        	}
        	else if (v.getId() == R.id.btnBestProvider) {        	
                String bp = sgwLocation.getBestProvider(criteria, true);
                txtOther.setText(bp == null ? "best provider = null" : "best provider = " + bp);
        	}
        	else if (v.getId() == R.id.btnBestLocation) {
                Location l = sgwLocation.getBestCachedLocation();
                txtOther.setText("lat = " + Double.toString(l.getLatitude()) + ", lon = " + Double.toString(l.getLongitude()) + ", " + l.getProvider());
        	}
        }
    };
	
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rbAccuracyFine:
                if (checked)
                   	criteria.setAccuracy(Criteria.ACCURACY_FINE);
                break;
            case R.id.rbAccuracyCoarse:
                if (checked)
                	criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                break;
            case R.id.rbPowerLow:
                if (checked)
                   	criteria.setPowerRequirement(Criteria.POWER_LOW);
                break;
            case R.id.rbPowerMedium:
                if (checked)
                   	criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
                break;
            case R.id.rbPowerHigh:
                if (checked)
                   	criteria.setPowerRequirement(Criteria.POWER_HIGH);
                break;
        }
    }    
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
