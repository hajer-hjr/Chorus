package chorus.chorus.com.chorus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import java.util.Calendar;
import java.util.TimeZone;

public class ShowSearchResultActivity extends AppCompatActivity {
private int selected=0;
    private Toolbar toolbar;
    private TextView address1,heureval,distanceval;
    ImageButton imgBut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_search_result);
        CircleMenu cm = (CircleMenu) findViewById(R.id.menu);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        address1=(TextView)findViewById(R.id.address);
        heureval=(TextView)findViewById(R.id.heureval);
        distanceval=(TextView)findViewById(R.id.distanceval);
        imgBut=(ImageButton)findViewById(R.id.goback);
        imgBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setToolbarTitle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorvertclair));
           // getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorvert));
        }
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        int minute = c.get(Calendar.MINUTE);
        //12 hour format
        int hour = c.get(Calendar.HOUR_OF_DAY);
        String date =  String.valueOf(hour) + ":" + String.valueOf(minute);
        heureval.setText(date);
        distanceval.setText(AppSession.currentUser.distance+" km");
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Double log=Double.parseDouble(mPrefs.getString("longitude","0.00"));
        Double alt=Double.parseDouble(mPrefs.getString("altitude","0.00"));
        new GetCurrentLocationTask(this).execute(alt, log);
        int  count=AppSession.members.size();
        if(AppSession.members.size()<= 10)


        for (int i = 0;i<count; i++) {
            cm.addMenuItem("one", i,AppSession.members.get(i));
         selected=i;
            Log.e("count", String.valueOf(i));
            /*AppSession.currentMember= AppSession.members.get(i);
              cm.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
                   intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                   // overridePendingTransition(R.anim.enter, R.anim.exit);
                }

            });*/

           cm.setListener(new CircleMenu.IMenuListener() {
                @Override
                public void onMenuClick(CircleMenu.MenuCircle item) {
                    Log.e("selected", String.valueOf(selected));
                    AppSession.currentMember=item.membre;
                    if(AppSession.currentMember.isContacts.equals("1")) {
                        // Intent intent = new Intent(getApplicationContext(), SearchResultsActivity.class);
                        Intent intent = new Intent(getApplicationContext(), MemberProfilContactActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        getApplicationContext().startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                    else
                    {

                    Intent intent = new Intent(getApplicationContext(), MemberProfilActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    getApplicationContext().startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);}
                }
            });
        }}
    private void setToolbarTitle() {

       // getSupportActionBar().setTitle("Mon profil");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //  getSupportActionBar().setDisplayUseLogoEnabled(true);
        //  getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
          getSupportActionBar().setLogo(R.drawable.logogris);
        // getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //   getSupportActionBar().setDisplayShowHomeEnabled(true);
        //   getSupportActionBar().setIcon(R.drawable.globe);
    }
    @Override
    public void onBackPressed() {
        // finish() is called in super: we only override this method to be able to override the transition
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
               //NavUtils.navigateUpFromSameTask(this);
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                return true;
            case R.id.action_search:
                /*Intent intent = new Intent(this, SearchParamsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                this.startActivity(intent);
                this.overridePendingTransition(R.anim.enter, R.anim.exit);*/
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class GetCurrentLocationTask extends ApiTaskNoDialog<Double, Void, String> {

        GetCurrentLocationTask(Context context) {
            super(context);
        }

        protected String doInBackground(Double... coords) {
            if (coords[0] != 0 && coords[1] != 0) {
                return SJBLocationService.getStringFromLocation(coords[0], coords[1]);
            } else {
                error = "COULD_NOT_GET_CURRENT_LOCATION";
                return null;
            }
        }

        protected void onPostExecute(String address) {
            if (address != null) {
                address1.setText(address);

               // ((EditText)((SearchActivity)context).findViewById(R.id.location)).setText(address);
            } else {
                if (error.equals("COULD_NOT_GET_CURRENT_LOCATION")) error = context.getResources().getString(R.string.error_could_not_get_current_location);
                showError();
            }
            super.onPostExecute(address);
        }
    }
    }

