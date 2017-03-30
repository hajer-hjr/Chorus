package chorus.chorus.com.chorus;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RegistrationActivity extends AppCompatActivity {

    private Spinner country;
    private Context mContext;
    ArrayList<String> payss;
    ArrayList<String> states;
    private DatePicker datePicker;
    private Calendar calendar;
    private EditText date;
    private int year, month, day;
    private String selectestate;
    private int pos;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        /*initialisation de datepicker*/
        date = (EditText) findViewById(R.id.date);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);
        mContext = this;
        country = (Spinner) findViewById(R.id.pays);
        ArrayAdapter<Country> countryArrayAdapter = new ArrayAdapter<Country>(this, android.R.layout.simple_spinner_item, AppSession.countries);
        countryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        country.setAdapter(countryArrayAdapter);
        country.setPrompt(getResources().getString(R.string.hint_country));
        country.setSelection(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorvert));
        }
    }
    @SuppressWarnings("deprecation")
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


    @Override
    public void onBackPressed() {
        // finish() is called in super: we only override this method to be able to override the transition
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
    /*fonction inscription*/
    public void register(View view) {
        EditText email= (EditText)findViewById(R.id.email);
        EditText username = (EditText)findViewById(R.id.username);
        EditText password = (EditText)findViewById(R.id.password);
        EditText confirm_password = (EditText)findViewById(R.id.confirm_password);
        EditText firstname = (EditText)findViewById(R.id.firstname);
        EditText lastname = (EditText)findViewById(R.id.lastname);
        EditText tel = (EditText)findViewById(R.id.tel);
        EditText verset = (EditText)findViewById(R.id.verset);
        EditText paroisse = (EditText)findViewById(R.id.paroisse);
       // EditText origine = (EditText)findViewById(R.id.origine);
        RadioGroup sexeRadio = (RadioGroup) findViewById(R.id.radioSex);
        int selectedId = sexeRadio.getCheckedRadioButtonId();
        RadioButton radioSexButton = (RadioButton) findViewById(selectedId);
        String date1=date.getText().toString();
        String pays=AppSession.countries.get(country.getSelectedItemPosition()).getId();
        if(password.getText().toString().length()<6)
            Toast.makeText(this, "Le mot de passe doit contenir au moins 6 caractères", Toast.LENGTH_SHORT).show();
        else

        new RegistrationTask(this).execute(email.getText().toString(),username.getText().toString(),password.getText().toString(),firstname.getText().toString(),lastname.getText().toString(),confirm_password.getText().toString(),date1,radioSexButton.getText().toString(),pays,tel.getText().toString(),paroisse.getText().toString(),verset.getText().toString());
    }
    class ListcategorieTask extends ApiTaskNoDialog<Void, Void, ArrayList<NewsCategorie>> {


        ListcategorieTask(Context context) {
            super(context);
        }

        protected  ArrayList<NewsCategorie> doInBackground(Void... data) {
            try {

                ArrayList<NewsCategorie>categories=AppSession.getApi(context).getNewCetagories();
                return categories;
            } catch (SJBException e) {
                error = e.getMessage();
                exception = e;
                return null;
            }
        }

        protected void onPostExecute(ArrayList<NewsCategorie> categories) {
            if (categories!=null) {
                //String authField = "email".equals(context.getResources().getString(R.string.auth_via)) ? this.email : this.username;
                //new LoginTask(context).execute(this.email, this.password);
                AppSession.newsCategories=categories;
                Intent intent = new Intent(context, RegistrationEtape2Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                showError();
            }
            super.onPostExecute(categories);
        }
    }


    class RegistrationTask extends ApiTask<String, Void, User> {
        private String email;
        private String username;
        private String password;
        private String lastname;
        private String firstname;
        private String confirm_password;
        private String date_naiss;
        private String sexe;
        private String pays;
        private String tel;
        private String paroisse;
        private String verset;

        RegistrationTask(Context context) {
            super(context, R.string.dialog_registering);
        }

        protected User doInBackground(String... data) {
            try {
                this.email = data[0];
                this.username = data[1];
                this.password = data[2];
                this.firstname = data[3];
                this.lastname = data[4];
                this.confirm_password = data[5];
                this.date_naiss = data[6];
                this.sexe = data[7];
                this.pays = data[8];
                this.tel = data[9];
                this.paroisse = data[10];
                this.verset=data[11];
                return AppSession.getApi(context).register(this.email, this.username, this.password, this.firstname,this.lastname,this.confirm_password,this.date_naiss,this.sexe,this.pays,this.tel,this.paroisse,this.verset);
            } catch (SJBException e) {
                error = e.getMessage();
                exception = e;
                return null;
            }
        }

        protected void onPostExecute(User user) {
            if (user!=null) {
                AppSession.currentUser=user;
                SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.commit();
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
               Double mLat=0.0;
                Double mLong=0.0;
                GPSTracker mGPS = new GPSTracker(getBaseContext());
                if(mGPS.canGetLocation()){
                    mLat = mGPS.getLatitude();
                    mLong = mGPS.getLongitude();
                    Log.e("mLat",String.valueOf(mLat));
                    Log.e("mLong",String.valueOf(mLong));
                }

                Gson gson = new Gson();
                String json = gson.toJson(user);
                prefsEditor.putString("user", json);
                prefsEditor.putString("longitude",Double.toString(mLong));
                prefsEditor.putString("altitude",Double.toString(mLat));
                prefsEditor.putBoolean("isLogin", true);
                prefsEditor.putString("uid", user.id);
                prefsEditor.commit();
                Intent msgIntent = new Intent(context, UpdateUserLocationService.class);
                context.startService(msgIntent);
                Intent intent = new Intent(context, MenuWrappAcivity.class);
                intent.putExtra("isNew","1");
                context.startActivity(intent);
                finish();
                //String authField = "email".equals(context.getResources().getString(R.string.auth_via)) ? this.email : this.username;
                //new LoginTask(context).execute(this.email, this.password);
             //  new  ListcategorieTask(getApplicationContext()).execute();
            } else {
                showError();
            }
            super.onPostExecute(user);
        }
    }
    /*enable gps dialog*/
    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AlertDialog);
        alertDialogBuilder.setMessage("Chorus ne peut pas obtenir votre emplacement actuel. Voulez-vous modifier vos paramètres de localisation sans fil?")
                .setCancelable(false)
                .setPositiveButton("Continuer",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Annuler",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
        /*Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setBackgroundColor(Color.parseColor("#319b87"));

        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setBackgroundColor(Color.parseColor("#319b87"));*/

    }
}
