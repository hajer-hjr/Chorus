package chorus.chorus.com.chorus;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class RefreshTchattService extends IntentService {
  ///  private static final String ACTION_NEW_MESSAGE = "com.jetunoo.jetunoo.action.NEW_MESSAGE";
  public static volatile boolean shouldContinue = true;
    private String idautreMembre;
    public RefreshTchattService() {
        super("RefreshTchattService");
    }
    Timer t=new Timer();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        return START_REDELIVER_INTENT;
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        doStuff();
        idautreMembre = intent.getStringExtra("idautreMembre");

        t.schedule(new TimerTask() {

            @Override
            public void run() {


                Log.e("is runnning", "service is running");
                try {
                if(AppSession.currentUser!=null) {

                    MessagesResult messageResult = AppSession.getApi(getApplicationContext()).UserMessagesList2(idautreMembre);
                    if (messageResult != null) {
                        AppSession.usermessages.addAll(messageResult.messages);
                        AppSession.currentUserMessagesNumber += messageResult.listingsNumber;
                        // maintenant on transmet le résultat
                        // on pourrait avoir un Handler, BroadCast, Notification, etc.
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.putExtra("resultCode", Activity.RESULT_OK);
                        broadcastIntent.setAction(DiscussActivity.MyReceiver.ACTION_NEW_MESSAGE);
                        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        // broadcastIntent.putExtra(SOURCE_URL, result.toString());
                        sendBroadcast(broadcastIntent);
                       /* for (int i = 0; i < messageResult.messages.size(); i++) {
                          Log.e("message", messageResult.messages.get(i).mes_subject);
                        }*/
                    }
                }
                    else {
                    Log.e("isnull","user");
                    t.cancel();
                    stopSelf();
                }

                } catch (SJBException e) {
                    String error = e.getMessage();
                }



            }
        }, 100,5000);


    }
    private void doStuff() {
        // do something
        if (AppSession.currentUser == null)
            shouldContinue = false;
        // check the condition
        if (shouldContinue == false) {
            Log.e("here","heeeeere");
           t.cancel();
            stopSelf();
            return;
        }
    }
}
