package chorus.chorus.com.chorus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

public class MyAccountActivity extends AppCompatActivity {

    String imageURI;
    Button b;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Gson gson = new Gson();


        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String json = mPrefs.getString("user", "");
        User obj = gson.fromJson(json, User.class);
        //Log.e("user id",obj.id);
        if(obj!=null && mPrefs.getBoolean("islogin",true)) AppSession.currentUser=obj;
        if(AppSession.currentUser!=null) {
            setContentView(R.layout.my_account);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (AppSession.currentUser != null) {
               TextView myImageViewText=(TextView) this.findViewById(R.id.username);
                final ImageView imageLogo = (ImageView) this.findViewById(R.id.logo);


                Transformation transformation = new Transformation() {

                    @Override
                    public Bitmap transform(Bitmap source) {
                        int targetWidth = imageLogo.getWidth();

                        double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                        int targetHeight = (int) (targetWidth * aspectRatio);
                        Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                        if (result != source) {
                            // Same bitmap is returned if sizes are the same
                            source.recycle();
                        }
                        return result;
                    }


                    @Override
                    public String key() {
                        return "transformation" + " desiredWidth";
                    }
                };

                myImageViewText.setText(AppSession.currentUser.username );
              if (AppSession.currentUser.Logo.thumb_file_url != null) {

                    Log.e("logo url", this.getResources().getString(R.string.api_base_url) + "/jetunoo/" + AppSession.currentUser.Logo.thumb_file_url);
                    //  imageLogo.setImageUrl(this.getResources().getString(R.string.api_base_url) +"/jetunoo/"+AppSession.currentUser.Logo.thumb_file_url);
                    Picasso.with(getBaseContext()).load(this.getResources().getString(R.string.api_base_url) + "/" + AppSession.currentUser.Logo.thumb_file_url).into(imageLogo);
                    //  Picasso.with(getBaseContext()).load(this.getResources().getString(R.string.api_base_url) + "/jetunoo/" + AppSession.currentUser.Logo.thumb_file_url).fit().centerCrop().into(imageLogo);
                    // imageURI=this.getResources().getString(R.string.api_base_url) + "/jetunoo/" + AppSession.currentUser.Logo.thumb_file_url;
                    imageURI = this.getResources().getString(R.string.api_base_url) + "/" + AppSession.currentUser.Logo.thumb_file_url;
                    Log.e("image uri", imageURI);
                    imageLogo.setVisibility(View.VISIBLE);
                   /* addButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            if (AppSession.currentUser.image_list.size() < 4) {

                                Intent intent = new Intent(MyAccountActivity.this, MesphotosActivity2.class);
                                MyAccountActivity.this.startActivity(intent);

                            } else {

                                Toast.makeText(MyAccountActivity.this, "Attention, vous êtes limité à 4 photos.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
               else {
                   imageLogo.setVisibility(View.GONE);
               }*/
            }

        }
        else{

            Intent intent = new Intent(MyAccountActivity.this, LoginActivity.class);
            startActivity(intent);


        }
    }}
    @Override
    protected void onResume() {
        super.onResume();
        // on déclare notre Broadcast Receiver
        Gson gson = new Gson();
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String json = mPrefs.getString("user", "");
        User obj = gson.fromJson(json, User.class);
        // Log.e("user id",obj.id);
        if(obj!=null && mPrefs.getBoolean("islogin",true)) AppSession.currentUser=obj;
        if(AppSession.currentUser==null)
        {
            Intent intent = new Intent(MyAccountActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            // finish the activity
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
