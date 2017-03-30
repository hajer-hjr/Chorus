package chorus.chorus.com.chorus;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

public class FirstActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Gson gson = new Gson();
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String json = mPrefs.getString("user", "");
        User obj = gson.fromJson(json, User.class);
        if (obj != null && mPrefs.getBoolean("islogin", true)) AppSession.currentUser = obj;
        Intent intent;
        if (AppSession.currentUser!=null) {
            intent = new Intent(this, MenuWrappAcivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
        // note we never called setContentView()
    }


}
