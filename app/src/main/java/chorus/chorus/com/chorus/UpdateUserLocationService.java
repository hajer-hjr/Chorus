package chorus.chorus.com.chorus;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateUserLocationService extends Service{
    Timer t=new Timer();
    private Double mLat,mLong;
    private Handler handler;
    public static volatile boolean shouldContinue = true;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
         handler = new Handler(Looper.getMainLooper());

        t.schedule(new TimerTask() {

            @Override
            public void run() {
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Log.d("Handlers", "Called on main thread");
                if (AppSession.currentUser != null) {
                    Log.d("hrere", "yes");
                       /* Userlastlocation userLocation=new Userlastlocation(getApplicationContext());
                        Location loc=userLocation.getlocation();*/

                    GPSTracker mGPS = new GPSTracker(getApplicationContext());

                    if(mGPS.canGetLocation()){
                        mLat = mGPS.getLatitude();

                        mLong = mGPS.getLongitude();
                        Log.e("mLat",String.valueOf(mLat));
                        Log.e("mLong",String.valueOf(mLong));

                    }
                    SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                    Double log=Double.parseDouble(mPrefs.getString("longitude","0.00"));
                    Double alt=Double.parseDouble(mPrefs.getString("altitude","0.00"));
                    if(mLong!=null  && mLong!=0.0 && mLat!=null && mLat!=0.0) {
                        if ((!log.equals(mLong)||!log.equals(AppSession.currentUser.longitude)) || (!alt.equals(mLat)||!alt.equals(AppSession.currentUser.latitude))) {
                       // if (!log.equals(mLong) || !alt.equals(mLat)) {

                            Intent msgIntent = new Intent(getApplicationContext(), RefreshUserLocationService.class);

                            SharedPreferences.Editor prefsEditor = mPrefs.edit();
                            Gson gson = new Gson();
                            prefsEditor.putString("longitude", Double.toString(mLong));
                            prefsEditor.putString("altitude", Double.toString(mLat));
                            prefsEditor.putBoolean("isLogin", true);
                            prefsEditor.commit();
                            msgIntent.putExtra("latitude", "" + mLat);
                            msgIntent.putExtra("longitude", "" + mLong);
                            startService(msgIntent);

                        }
                    }


                }
                else{
                    Log.e("isnull", "user");
                    t.cancel();
                    stopSelf();
                }





            }
        }, 1000 );
            }
            }, 100,900000);
      /*  t.schedule(new TimerTask() {

            @Override
            public void run() {


                Log.d("Handlers", "Called on main thread");
                    if (AppSession.currentUser != null) {
                        Log.d("hrere", "yes");
                       /* Userlastlocation userLocation=new Userlastlocation(getApplicationContext());
                        Location loc=userLocation.getlocation();*/

                      /*      GPSTracker mGPS = new GPSTracker(getApplicationContext());

                            if(mGPS.canGetLocation()){
                               mLat = mGPS.getLatitude();
                               mLong = mGPS.getLongitude();

                            }

                        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                        Double log=Double.parseDouble(mPrefs.getString("longitude","0.00"));
                        Double alt=Double.parseDouble(mPrefs.getString("altitude","0.00"));

                            if(log!=mLong|| alt!=mLat)
                            {
                                Intent msgIntent = new Intent(getApplicationContext(), UpdateUserLocationService.class);

                                msgIntent.putExtra("latitude",""+mLat);
                                msgIntent.putExtra("longitude",""+mLong);
                                startService(msgIntent);

                            }


                    }
                    else{
                        Log.e("isnull", "user");
                        t.cancel();
                        stopSelf();
                    }









            }
        }, 100,5000);
*/
        //Here we want this service to continue running until it is explicitly stopped, so return sticky
    return START_STICKY;
}

    private void doStuff() {
        // do something
        if (AppSession.currentUser == null)
            shouldContinue = false;
        // check the condition
        if (shouldContinue == false) {
            Log.e("here", "heeeeere");
            t.cancel();
            stopSelf();
            return;
        }
    }


    class Userlastlocation implements LocationListener {
        private final Context mContext;
        public boolean isGPSEnabled = false;
        private static final int LOCATION_INTERVAL = 1000;
        private static final float LOCATION_DISTANCE = 10f;
        // flag for network status
        boolean isNetworkEnabled = false;

        // flag for GPS status
        boolean canGetLocation = false;
        private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

        // The minimum time between updates in milliseconds
        private static final long MIN_TIME_BW_UPDATES = 1; // 1 minute
      private  Location location; // location
        double latitude; // latitude
        double longitude; // longitud

        Userlastlocation(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public void onLocationChanged(Location location) {
           Log.e("latitude",String.valueOf(location.getLatitude()));
            this.location=location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        public Location getlocation() {
            LocationManager locationManager;
            Log.d("hrere", "yessssssssssssss");
            if ( Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission( mContext, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission( mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("hrere3", "yessssssssssssss");

                return null  ;
            }


            try   {
                Log.d("hrere2", "yessssssssssssss");

                locationManager = (LocationManager) getSystemService(mContext.LOCATION_SERVICE);

                // getting GPS status
                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                // getting network status
                isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                if (!isGPSEnabled && !isNetworkEnabled) {
                    // no network provider is enabled
                    Log.d("hrereNo", "yessssssssssssss");
                } else {

                    this.canGetLocation = true;
                 /*   if (isNetworkEnabled) {

                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                LOCATION_INTERVAL,
                                LOCATION_DISTANCE, this);

                        Log.d("activity", "LOC Network Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                Log.d("activity", "LOC by Network");
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                Log.e("longitude",String.valueOf(longitude));
                            }
                        }
                    }*/
                    // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled) {

                        if (location == null) {

                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    0,
                                    0, this);
                            Log.d("activity", "RLOC: GPS Enabled");
                            if (locationManager != null) {
                                Log.d("hrereYes", "yessssssssssssss");
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    Log.d("activity", "RLOC: loc by GPS");

                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();

                                    Log.e("longitude",String.valueOf(longitude));
                                }
                            }}}}}
            catch (Exception ex)  {
                // Log.e( "Error creating location service:","cvcvcv" );

            }
            return location;
        }

        /**
         * Function to get latitude
         * */
        public double getLatitude(){
            if(location != null){
                latitude = location.getLatitude();
            }

            // return latitude
            return latitude;
        }

        /**
         * Function to get longitude
         * */
        public double getLongitude(){
            if(location != null){
                longitude = location.getLongitude();
            }

            // return longitude
            return longitude;
        }

    }
}
