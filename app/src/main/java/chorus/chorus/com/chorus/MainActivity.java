package chorus.chorus.com.chorus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.google.gson.Gson;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Animation slide_in_left, slide_out_right;
    ViewFlipper viewFlipper;
private Button button1,button2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new CountryTask(getApplicationContext()).execute();
        viewFlipper = (ViewFlipper) this.findViewById(R.id.bckgrndViewFlipper1);

       /* slide_in_left = AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left);
        slide_out_right = AnimationUtils.loadAnimation(this,
                android.R.anim.slide_out_right);*/
        slide_in_left = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in);
        slide_out_right = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out);
        viewFlipper.setInAnimation(slide_in_left);
        viewFlipper.setOutAnimation(slide_out_right);
        //sets auto flipping        viewFlipper.setAutoStart(true);
         viewFlipper.setFlipInterval(8000);
        viewFlipper.startFlipping();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorvert));
        }
        button1=(Button)findViewById(R.id.button12);
        button2=(Button)findViewById(R.id.button22);

        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(),RegistrationActivity.class);
                startActivity(i);
              finish();

            }
        });
      button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i);
            finish();

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // on déclare notre Broadcast Receiver
        if(AppSession.countries==null)
        {
            new CountryTask(getApplicationContext()).execute();
        }
    }
    class CountryTask extends ApiTaskNoDialog<String, Void, ArrayList<Country>> {

        CountryTask(Context context) {
            super(context);
        }
        protected ArrayList<Country> doInBackground(String... data) {
            try {

                ArrayList<Country>  countries=AppSession.getApi(context).getCountries();
                if (countries.size() == 0) {
                    throw new SJBException("NOTHING_FOUND");
                }
                return countries;
            } catch (SJBException e) {
                error = e.getMessage();
                exception = e;
                return null;
            }
        }
        protected void onPostExecute(ArrayList<Country> countries) {
            if (countries!=null) {
                AppSession.countries = countries;

            } else {
                showError();
            }
            super.onPostExecute(countries);
        }
    }
}
