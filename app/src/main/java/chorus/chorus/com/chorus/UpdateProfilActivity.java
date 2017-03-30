package chorus.chorus.com.chorus;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.List;

public class UpdateProfilActivity extends AppCompatActivity {
    private Spinner country;
    private Context mContext;
    private DatePicker datePicker;
    private Calendar calendar;
    private EditText date;
    private int year, month, day;
    private String selecteditem;
    private Spinner state;
    private int countryIndex;
    private int stateIndex;
    private String selectestate;
    private int pos;
    private EditText email ;
    private  EditText username ;
    private EditText firstname ;
    private  EditText lastname;
    private  EditText paroisse,verset;
    private Toolbar toolbar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profil);
        Gson gson = new Gson();
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String json = mPrefs.getString("user", "");
        User obj = gson.fromJson(json, User.class);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbarTitle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorvert));
        }
        // Log.e("user id",obj.id);
        if(obj!=null && mPrefs.getBoolean("islogin",true)) AppSession.currentUser=obj;
        /*initialisation de datepicker*/
        if(AppSession.currentUser!=null) {
            date = (EditText) findViewById(R.id.date);
            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            showDate(year, month + 1, day);
            mContext = this;
            country = (Spinner) findViewById(R.id.pays);
            email = (EditText) findViewById(R.id.email);
            username = (EditText) findViewById(R.id.username);
            firstname = (EditText) findViewById(R.id.firstname);
            lastname = (EditText) findViewById(R.id.lastname);
            paroisse = (EditText) findViewById(R.id.paroisse);
            verset = (EditText) findViewById(R.id.verset);
            //tel = (EditText) findViewById(R.id.tel);
            //  not4.setVisibility(View.GONE);
            if (AppSession.currentUser != null) {
                email.setText(AppSession.currentUser.email);
                username.setText(AppSession.currentUser.username);
                firstname.setText(AppSession.currentUser.firstname);
                lastname.setText(AppSession.currentUser.lastname);
                paroisse.setText(AppSession.currentUser.paroisse);
                verset.setText(AppSession.currentUser.verset);
              //  tel.setText(AppSession.currentUser.tel);
                Log.e("countryyyy", AppSession.currentUser.pays);
                countryIndex = getCountryIndexById(AppSession.currentUser.pays);
                //if(countryIndex!=0)
                // stateIndex=getStateIndexById(countryIndex,AppSession.currentUser.state);
                if (AppSession.currentUser.date_naiss != "") {
                    String[] date2 = AppSession.currentUser.date_naiss.split("-");
                    Log.e("date2", AppSession.currentUser.date_naiss);
                    showDate(Integer.parseInt(date2[0]), Integer.parseInt(date2[1]), Integer.parseInt(date2[2]));
                }


            }

            ArrayAdapter<Country> countryArrayAdapter = new ArrayAdapter<Country>(this, android.R.layout.simple_spinner_item, AppSession.countries);
            countryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            country.setAdapter(countryArrayAdapter);
            country.setPrompt(getResources().getString(R.string.hint_country));
            country.setSelection(countryIndex);


            //}

        }
        else
        {


            Intent intent = new Intent(UpdateProfilActivity.this, LoginActivity.class);
            startActivity(intent);



        }
    }
    @Override
    public void onBackPressed() {
        // finish() is called in super: we only override this method to be able to override the transition
        super.onBackPressed();

        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
    private int getCountryIndexById(String id) {
        for (int i = 0; i <AppSession.countries.size(); i++) {
            if (id.equals(AppSession.countries.get(i).id)) {
                return i;
            }
        }
        return 0;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                return true;
            default:
                return true;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // on déclare notre Broadcast Receiver
        Gson gson = new Gson();
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String json = mPrefs.getString("user", "");
        User obj = gson.fromJson(json, User.class);
        //    Log.e("user id",obj.id);
        if(obj!=null && mPrefs.getBoolean("islogin",true)) AppSession.currentUser=obj;
        if(AppSession.currentUser==null)
        {


            Intent intent = new Intent(UpdateProfilActivity.this, LoginActivity.class);
            startActivity(intent);



        }
    }
    public void setDate(View view) {
        showDialog(999);
       /* Toast.makeText(getApplicationContext(), "ca", Toast.LENGTH_SHORT)
             .show();*/
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2+1, arg3);
        }
    };

    private void showDate(int year, int month, int day) {
        date.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }
    /*fonction inscription*/
    public void saveUser(View view) {
        String date1=date.getText().toString();

        String country1=AppSession.countries.get(country.getSelectedItemPosition()).getId();
        new UpdateUserTask(this).execute(email.getText().toString(), username.getText().toString(),firstname.getText().toString(),lastname.getText().toString(),date1,country1,paroisse.getText().toString(),verset.getText().toString());
    }
    private void setToolbarTitle() {

      getSupportActionBar().setTitle("Mon profil");
     //  getSupportActionBar().setDisplayUseLogoEnabled(true);
      //  getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
     //  getSupportActionBar().setLogo(R.drawable.logogris);
       // getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
      //   getSupportActionBar().setDisplayShowHomeEnabled(true);
        //   getSupportActionBar().setIcon(R.drawable.globe);
    }

    class UpdateUserTask extends ApiTask<String, Void, User> {
        private String email;
        private String username;
        private String lastname;
        private String firstname;
        private String date_naiss;
        private String pays;
        private String paroisse;
        private String verset;


        UpdateUserTask(Context context) {
            super(context, R.string.dialog_registering);
        }

        protected User doInBackground(String... data) {
            try {
                this.email = data[0];
                this.username = data[1];
                this.firstname = data[2];
                this.lastname = data[3];
                this.date_naiss = data[4];
                this.pays = data[5];
                this.paroisse = data[6];
                this.verset = data[7];
                return AppSession.getApi(context).updateprofil(this.email, this.username, this.firstname,this.lastname,this.date_naiss,this.pays,this.paroisse,this.verset);
            } catch (SJBException e) {
                error = e.getMessage();
                exception = e;
                return null;
            }
        }

        protected void onPostExecute(User user) {
            if (user!=null) {
                //String authField = "email".equals(context.getResources().getString(R.string.auth_via)) ? this.email : this.username;
                //new LoginTask(context).execute(this.email, this.password);
                AppSession.currentUser.username=user.username;
                AppSession.currentUser.email=user.email;
                AppSession.currentUser.firstname=user.firstname;
                AppSession.currentUser.lastname=user.lastname;
                AppSession.currentUser.date_naiss=user.date_naiss;
                AppSession.currentUser.pays=user.pays;
                AppSession.currentUser.profession=user.profession;
                AppSession.currentUser.origine=user.origine;
                AppSession.currentUser.verset=user.verset;
                SharedPreferences  mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                String json = mPrefs.getString("user", "");
                Gson gson = new Gson();
                User obj = gson.fromJson(json, User.class);

                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                obj.username=user.username;
                obj.email=user.email;
                obj.firstname=user.firstname;
                obj.lastname=user.lastname;
                obj.date_naiss=user.date_naiss;
                obj.pays=user.pays;
                obj.profession=user.profession;
                obj.origine=user.origine;
                obj.verset=user.verset;
                String json2 = gson.toJson(obj);
                prefsEditor.putString("user", json2);
                prefsEditor.commit();
                CharSequence text = "Votre Profil est modifié";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                NavUtils.navigateUpFromSameTask(UpdateProfilActivity.this);
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
              /*  Intent intent = new Intent(context,  MyAccountActivity.class);
                context.startActivity(intent);*/
            } else {
                showError();
            }
            super.onPostExecute(user);
        }
    }}

