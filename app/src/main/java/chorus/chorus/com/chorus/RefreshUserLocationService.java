package chorus.chorus.com.chorus;

import android.Manifest;
import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class RefreshUserLocationService extends IntentService {
    public static volatile boolean shouldContinue = true;
    private static final String TAG = "BOOMBOOMTESTGPS";
    private String idautreMembre;
    double altitude; // latitude
    double longitude;
    private Handler handler;
    private Runnable runnableCode;
    public RefreshUserLocationService() {
        super("RefreshUserLocationService");
    }

    Timer t = new Timer();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            Log.e("service is running","is running");
            if (AppSession.currentUser != null) {
                Bundle bundle = intent.getExtras();
                altitude=Double.parseDouble(bundle.getString("latitude"));
                longitude=Double.parseDouble(bundle.getString("longitude"));

                        UserLocation location1 = AppSession.getApi(getApplicationContext()).updateUserLocation(longitude, altitude);




            } else {
                Log.e("isnull", "user");
                t.cancel();
                stopSelf();
            }
        } catch (
                SJBException e
                )

        {
            String error = e.getMessage();
        }
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

}



