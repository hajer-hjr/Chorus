package chorus.chorus.com.chorus;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

public class SearchParamsActivity extends AppCompatActivity {
    private RangeSeekBar seekBar;
    private Toolbar toolbar;
    private int max_dist;
    private int min_dist;
    private SeekBar volumeControl = null;
    private Switch switchValue1 = null;
    private Switch switchValue2 = null;
    private TextView seckbar_value;
    private  Context mcontext;
    private RangeSeekBar ageControl = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_search_params);
        setContentView(R.layout.activity_seekbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        seckbar_value=(TextView)findViewById(R.id.seack_value);
        // seekBar=(RangeSeekBar)findViewById(R.id.rangeSeekbar) ;
        setSupportActionBar(toolbar);
        mcontext=this;
        setToolbarTitle();
      /*  seekBar.setRangeValues(1, 10);
        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                //Now you have the minValue and maxValue of your RangeSeekbar
                min_dist=minValue;
                max_dist=maxValue;
               //Toast.makeText(getApplicationContext(), minValue + "-" + maxValue, Toast.LENGTH_LONG).show();
            }
        });

// Get noticed while dragging
        seekBar.setNotifyWhileDragging(true);*/


        volumeControl = (SeekBar) findViewById(R.id.volume_bar);
        switchValue1 = (Switch) findViewById(R.id.switch1);
        switchValue2 = (Switch) findViewById(R.id.switch2);
        Log.e("search homme",String.valueOf(AppSession.currentUser.mem_search_homme));
        if(AppSession.currentUser.mem_search_homme==1)
            switchValue1.setChecked(true);
        if(AppSession.currentUser.mem_search_femme==1)
            switchValue2.setChecked(true);
        volumeControl.setProgress(Integer.parseInt(AppSession.currentUser.distance));
        seckbar_value.setText(AppSession.currentUser.distance+" km");

        volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = Integer.parseInt(AppSession.currentUser.distance);

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){

                if(progress<1) {
                    progress = 1;
                    seekBar.setProgress(1);

                }
                seckbar_value.setText(progress+" km");
                progressChanged = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                    /*Toast.makeText(SearchParamsActivity.this,"Distance:"+progressChanged + " km",
                            Toast.LENGTH_SHORT).show();*/
                AppSession.currentUser.distance=String.valueOf(progressChanged);

                SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mcontext);
                String json = mPrefs.getString("user", "");
                Gson gson = new Gson();
                User obj = gson.fromJson(json, User.class);

                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                obj.distance=String.valueOf(progressChanged);
                String json2 = gson.toJson(obj);
                prefsEditor.putString("user", json2);
                prefsEditor.commit();
                Log.e("distance max", AppSession.currentUser.distance);
                new searchParamsTask(mcontext).execute(String.valueOf(progressChanged));
            }
        });
        ageControl = (RangeSeekBar) findViewById(R.id.rangeSeekbar);


        ageControl.setSelectedMinValue(Integer.parseInt(AppSession.currentUser.mem_search_age_min));
        ageControl.setSelectedMaxValue(Integer.parseInt(AppSession.currentUser.mem_search_age_max));

        ageControl.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>(){

            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {

                AppSession.currentUser.mem_search_age_min=String.valueOf(minValue);
                AppSession.currentUser.mem_search_age_max=String.valueOf(maxValue);

                Log.d("MinValue", bar.getSelectedMinValue().toString());
                Log.d("MaxValue", bar.getSelectedMaxValue().toString());

                new searchParamsTaskAge(mcontext).execute(String.valueOf(minValue),String.valueOf(maxValue));

            }

            public void finalValue(Number minValue, Number maxValue) {
                Log.d("CRS=>", String.valueOf(minValue) + " : " + String.valueOf(maxValue));
            }
        });

        switchValue1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String s1Value;

                if(isChecked){
                    s1Value="1";
                }
                else{
                    s1Value="0";
                }
                new searchParamsTaskHomme(mcontext).execute(s1Value);

            }
        });
        switchValue2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String s1Value;

                if(isChecked){
                    s1Value="1";
                }
                else{
                    s1Value="0";
                }
                new searchParamsTaskFemme(mcontext).execute(s1Value);

            }
        });
    }
    private void setToolbarTitle() {

        getSupportActionBar().setTitle("Paramètres");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }
    @Override
    public void onBackPressed() {
        // finish() is called in super: we only override this method to be able to override the transition
        super.onBackPressed();

        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        finish();
    }
    public void saveParams(View view){
        AppSession.currentSearchCriteria.minDistance=min_dist;
        AppSession.currentSearchCriteria.maxDistance=max_dist;


    }

    class searchParamsTask extends ApiTaskNoDialog<String, Void, Boolean> {
        String distance;

        searchParamsTask(Context context) {
            super(context);
        }

        protected Boolean doInBackground(String... credentials) {
            try {
                this.distance = credentials[0];

                Boolean result = AppSession.getApi(context).saveSearchParams(distance);
                //  Log.e("questionnaire",questionnaire.toString());
               /* if (result) {
                    AppSession.currentMember.mem_opt_favoris = "on";
                }*/
                return result;
            } catch (SJBException e) {
                error = e.getMessage();
                return false;
            }
        }
        protected void onPostExecute(Boolean isadded) {
            if (isadded) {

             Toast.makeText(context, "Rayon de recherche modifié avec succés", Toast.LENGTH_SHORT).show();
                Log.e("ok","ok");

            } else {
                //if (error.equals("NOTHING_FOUND")) error = context.getResources().getString(R.string.error_nothing_found);
                showError();
                // Toast.makeText(context, R.string.success_delete_saved_job, Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(isadded);
        }

    }


    class searchParamsTaskAge extends ApiTaskNoDialog<String, Void, Boolean> {
        String MinValue;
        String MaxValue;

        searchParamsTaskAge(Context context) {
            super(context);
        }

        protected Boolean doInBackground(String... credentials) {
            try {
                this.MinValue = credentials[0];
                this.MaxValue = credentials[1];

                Boolean result = AppSession.getApi(context).saveSearchParamsAge(MinValue,MaxValue);
                //  Log.e("questionnaire",questionnaire.toString());
               /* if (result) {
                    AppSession.currentMember.mem_opt_favoris = "on";
                }*/
                return result;
            } catch (SJBException e) {
                error = e.getMessage();
                return false;
            }
        }
        protected void onPostExecute(Boolean isadded) {
            if (isadded) {

                Toast.makeText(context, "Age de recherche modifié avec succés", Toast.LENGTH_SHORT).show();
                Log.e("ok","ok");
                AppSession.currentUser.mem_search_age_min=MinValue;
                AppSession.currentUser.mem_search_age_max=MaxValue;
                SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mcontext);
                String json = mPrefs.getString("user", "");
                Gson gson = new Gson();
                User obj = gson.fromJson(json, User.class);

                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                obj.mem_search_age_min=MinValue;
                obj.mem_search_age_max=MaxValue;
                String json2 = gson.toJson(obj);
                prefsEditor.putString("user", json2);
                prefsEditor.commit();
            } else {
                //if (error.equals("NOTHING_FOUND")) error = context.getResources().getString(R.string.error_nothing_found);
                showError();
                // Toast.makeText(context, R.string.success_delete_saved_job, Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(isadded);
        }

    }
    class searchParamsTaskHomme extends ApiTaskNoDialog<String, Void, Boolean> {
        String serach_homme;

        searchParamsTaskHomme(Context context) {
            super(context);
        }

        protected Boolean doInBackground(String... credentials) {
            try {
                this.serach_homme = credentials[0];

                Boolean result = AppSession.getApi(context).saveSearchParamsHomme(serach_homme);
                //  Log.e("questionnaire",questionnaire.toString());
               /* if (result) {
                    AppSession.currentMember.mem_opt_favoris = "on";
                }*/
                return result;
            } catch (SJBException e) {
                error = e.getMessage();
                return false;
            }
        }
        protected void onPostExecute(Boolean isadded) {
            if (isadded) {

               // Toast.makeText(context, "Rayon de recherche modifié avec succés", Toast.LENGTH_SHORT).show();
              //  Log.e("ok",serach_homme);
                AppSession.currentUser.mem_search_homme=Integer.parseInt(serach_homme);
                SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mcontext);
                String json = mPrefs.getString("user", "");
                Gson gson = new Gson();
                User obj = gson.fromJson(json, User.class);

                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                obj.mem_search_homme=Integer.parseInt(serach_homme);
                String json2 = gson.toJson(obj);
                prefsEditor.putString("user", json2);
                prefsEditor.commit();
                Log.e("distance max", AppSession.currentUser.distance);
            } else {
                //if (error.equals("NOTHING_FOUND")) error = context.getResources().getString(R.string.error_nothing_found);
                showError();
                // Toast.makeText(context, R.string.success_delete_saved_job, Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(isadded);
        }

    }
    class searchParamsTaskFemme extends ApiTaskNoDialog<String, Void, Boolean> {
        String serach_femme;

        searchParamsTaskFemme(Context context) {
            super(context);
        }

        protected Boolean doInBackground(String... credentials) {
            try {
                this.serach_femme = credentials[0];

                Boolean result = AppSession.getApi(context).saveSearchParamsFemme(serach_femme);
                //  Log.e("questionnaire",questionnaire.toString());
               /* if (result) {
                    AppSession.currentMember.mem_opt_favoris = "on";
                }*/
                return result;
            } catch (SJBException e) {
                error = e.getMessage();
                return false;
            }
        }
        protected void onPostExecute(Boolean isadded) {
            if (isadded) {

              //  Toast.makeText(context, "Rayon de recherche modifié avec succés", Toast.LENGTH_SHORT).show();
                Log.e("ok","ok");

                AppSession.currentUser.mem_search_femme=Integer.parseInt(serach_femme);
                SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mcontext);
                String json = mPrefs.getString("user", "");
                Gson gson = new Gson();
                User obj = gson.fromJson(json, User.class);

                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                obj.mem_search_femme=Integer.parseInt(serach_femme);
                String json2 = gson.toJson(obj);
                prefsEditor.putString("user", json2);
                prefsEditor.commit();
            } else {
                //if (error.equals("NOTHING_FOUND")) error = context.getResources().getString(R.string.error_nothing_found);
                showError();
                // Toast.makeText(context, R.string.success_delete_saved_job, Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(isadded);
        }

    }
}
