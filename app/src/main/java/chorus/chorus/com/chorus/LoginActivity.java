package chorus.chorus.com.chorus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;

//import com.google.gson.Gson;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by pc on 17/05/2016.
 */
public class LoginActivity  extends AppCompatActivity {
    public static String  PREFS_NAME="mypre";
    public static String PREF_USERNAME="username";
    public static String PREF_PASSWORD="password";
   public  CheckBox chkRememberMe; //      <--- You even not taken CheckBox, SILLY WORKER
   EditText email,passwd;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
     //   setupUI(findViewById(R.id.parent));

    chkRememberMe = (CheckBox) findViewById(R.id.rememberMe);  //      <---  Instantiate CheckBox...

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorvert));
        }

        //  ADD THIS  TO  READ  SAVED  username & password  NEXT-TIME OPENING Application
       /* SharedPreferences loginPreferences = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        email.setText(loginPreferences.getString(PREF_USERNAME, ""));
        passwd.setText(loginPreferences.getString(PREF_PASSWORD, ""));*/
        //textUsername.setHint("username".equals(getResources().getString(R.string.auth_via)) ? R.string.hint_username : R.string.hint_email);
    }
 public void onStart(){
        super.onStart();
        //read username and password from SharedPreferences
        getUser();
    }
    public void login(View view) {
        EditText username = (EditText)findViewById(R.id.email);
        EditText password = (EditText)findViewById(R.id.password);
      new LoginTask(this).execute(username.getText().toString(), password.getText().toString());
    }
   public void getUser(){
        SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        String username = pref.getString(PREF_USERNAME, null);
        String password = pref.getString(PREF_PASSWORD, null);

        if (username != null || password != null) {
           // Log.e("isLogin","yeeeeeeeeeeeeeeeees");
            //Log.e("username",username);
            //Log.e("password",password);
            //directly show logout form
            //fill input boxes with stored login and pass
         email = (EditText) findViewById(R.id.email); //  <---  Instantiate     EditText...
           passwd = (EditText) findViewById(R.id.password); //  <---  Instantiate EditText...
            email.setText(username);
            passwd.setText(password);

            //set the check box to 'checked'

           chkRememberMe.setChecked(true);
        }
    }
    public void rememberMe(String user, String password){
        //save username and password in SharedPreferences
        getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
                .edit()
                .putString(PREF_USERNAME,user)
                .putString(PREF_PASSWORD,password)
                .commit();
    }
class LoginTask extends ApiTask<String, Void, User> {
String pwd;
    LoginTask(Context context) {
        super(context, R.string.dialog_signing_in);
    }

    protected User doInBackground(String... credentials) {
        this.pwd=credentials[1];
        try {
            API api = AppSession.getApi(context);
            User user = api.login(credentials[0], credentials[1]);
            return user;
        } catch (SJBException e) {
            error = e.getMessage();
            return null;
        }
    }
    protected void onPostExecute(User user) {
        if (user != null) {
            AppSession.currentUser = user;

        if(chkRememberMe.isChecked())
                rememberMe(user.email,this.pwd); //save username and password
            else {
               getSharedPreferences(PREFS_NAME,MODE_PRIVATE)
                       .edit()
                       .clear()
                       .commit();
           }
          // new CountryTask(context).execute();
            /*Intent msgIntent = new Intent(context, NotficationService.class);
            msgIntent.putExtra(NotficationService.PARAM_IN_MSG, "test");
            startService(msgIntent);*/
            SharedPreferences  mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(user);
            prefsEditor.putString("user", json);
            prefsEditor.putString("longitude",user.longitude);
            prefsEditor.putString("altitude",user.latitude);
            prefsEditor.putBoolean("isLogin", true);
            prefsEditor.putString("uid", user.id);
            prefsEditor.commit();
            Intent msgIntent = new Intent(context, UpdateUserLocationService.class);
           startService(msgIntent);

          Intent intent = new Intent(context, MenuWrappAcivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);
          context.startActivity(intent);
           finish();
        }

        else{

            showError();
        }

        super.onPostExecute(user);
    }
}
    @Override
    public void onBackPressed() {
        // finish() is called in super: we only override this method to be able to override the transition
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
}