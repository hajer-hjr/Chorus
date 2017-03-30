package chorus.chorus.com.chorus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
//import com.loopj.android.image.SmartImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by pc on 20/05/2016.
 */
public class MemberProfilActivity  extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageButton coeurBtt,plusButt,favButt;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.member_profil);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbarTitle();

        TextView   profession = (TextView) findViewById(R.id.profession);
        TextView   distance = (TextView) findViewById(R.id.distance);
        TextView verset=(TextView) findViewById(R.id.verset);
        verset.setText(AppSession.currentMember.verset);
        String s=String.valueOf(AppSession.currentMember.verset);
        Log.d("eeeeeee",s);
        profession.setText(AppSession.currentMember.profession);
        TextView textUsername = (TextView) findViewById(R.id.username);
        TextView age = (TextView) findViewById(R.id.age);
         favButt =(ImageButton)findViewById(R.id.favButt) ;
        plusButt=(ImageButton)findViewById(R.id.plusButt);
        coeurBtt=(ImageButton)findViewById(R.id.coeurButt);
        if(AppSession.currentMember.isFavoris.equals("1"))
        {
            favButt.setImageResource(R.drawable.etoile_rose);
        }
        else{
            favButt.setImageResource(R.drawable.favoris_gris);
        }
        if(AppSession.currentMember.isInvitations.equals("1")) {

           if (AppSession.currentMember.isType.equals("1")) {

                coeurBtt.setImageResource(R.drawable.heart_rose);
                coeurBtt.setClickable(false);
                plusButt.setClickable(false);
            }
            if (AppSession.currentMember.isType.equals("2")) {

                plusButt.setImageResource(R.drawable.plus_vert);
                coeurBtt.setClickable(false);
                plusButt.setClickable(false);
            }
        }

        favButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppSession.currentMember.isFavoris.equals("0")){
                new addFavorisTask(getApplicationContext()).execute(AppSession.currentMember.getId());
            }
                else{

                  new deleteFavorisTask(getApplicationContext()).execute(AppSession.currentMember.getId());
                }

            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
           getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorvert));
           // getWindow().setStatusBarColor(ContextCompat.getColor(this,(AppSession.currentMember.sexe.equals("M"))? R.color.colorvertclair : R.color.colorrose));
        }
        if (AppSession.currentMember != null) {

            age.setText(toString().valueOf(AppSession.currentMember.getAge(AppSession.currentMember.date_naiss)));
            textUsername.setText(AppSession.currentMember.username + ", " );
            profession.setText(AppSession.currentMember.profession);
            if((int)Math.round((Double.parseDouble(AppSession.currentMember.distance)))<1)
            distance.setText(Math.round(Double.parseDouble(AppSession.currentMember.distance)*1000) + " m");
            else
                distance.setText((int)Math.round(Double.parseDouble(AppSession.currentMember.distance)) + " km");
            CircularImageView imageLogo =(CircularImageView) findViewById(R.id.logo);
            imageLogo.setBorderWidth(0);
            LinearLayout Linear1=(LinearLayout)findViewById(R.id.sexeLayout);
            if(AppSession.currentMember.sexe.equals("F") )
                Linear1.setBackgroundColor(Color.parseColor("#d7266a"));


                Linear1.setBackgroundColor(Color.parseColor("#3db39d"));

            if (AppSession.currentMember.Logo.thumb_file_url != null) {
                Log.e("logo url", this.getResources().getString(R.string.api_base_url) + AppSession.currentMember.Logo.thumb_file_url);


                MemberProfilActivity.DownloadImageTask ta = new MemberProfilActivity.DownloadImageTask(imageLogo);
                ta.execute(getApplicationContext().getResources().getString(R.string.api_base_url) + "/" + AppSession.currentMember.Logo.thumb_file_url);
                imageLogo.setVisibility(View.VISIBLE);
            } else {
                imageLogo.setVisibility(View.GONE);
            }

            coeurBtt.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(AppSession.currentMember.isInvitations.equals("0")) {
                        new addRequestTask(getApplicationContext()).execute(AppSession.currentMember.getId(), "1");
                    }     }

            });
            plusButt.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(AppSession.currentMember.isInvitations.equals("0")) {
                        new addRequestTask(getApplicationContext()).execute(AppSession.currentMember.getId(), "2");
                    }
                }

            });
            /* if (AppSession.currentUser != null && !AppSession.currentMember.id.equals(AppSession.currentUser.id))

            {
                if (AppSession.currentMember.image_list.size() > 0) {
                   showPhoto.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), MembrePhotoActivity.class);
                            startActivity(intent);
                        }

                    });
                }
                else {
                    showPhoto.setVisibility(View.INVISIBLE);
                }

            }
            else{
                showPhoto.setVisibility(View.INVISIBLE);


            }*/


        }}
    @Override
    public void onBackPressed() {
        // finish() is called in super: we only override this method to be able to override the transition
        super.onBackPressed();
      //  PageFragment.favAdapter.notifyDataSetChanged();
        if(getIntent().getSerializableExtra("fromFav")!=null)
        {
            if(getIntent().getSerializableExtra("fromFav").equals("1"))
            {
                PageFragment.favAdapter.notifyDataSetChanged();
            }
        }

        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profil, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_home:

                return true;
            case R.id.action_search:

                return true;
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                return true;
            default:
                return true;
        }
    }
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
    protected void onResume(){
        super.onResume();
        // on déclare notre Broadcast Receiver
        Gson gson = new Gson();
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String json = mPrefs.getString("user", "");
        User obj = gson.fromJson(json, User.class);
        if (obj != null && mPrefs.getBoolean("islogin", true)) AppSession.currentUser = obj;
       //String json2 = mPrefs.getString("membre", "");
       // Membre obj2 = gson.fromJson(json2, Membre.class);
        // Log.e("membre id",obj2.id);

    }

    // add a member to favoris*/

    /*add flash member*/
    class addRequestTask extends ApiTaskNoDialog<String, Void, Boolean> {
        String idmember;
        String type;
        addRequestTask(Context context) {
            super(context);
        }

        protected Boolean doInBackground(String... credentials) {
            try {
                this.idmember = credentials[0];
                this.type = credentials[1];
                Boolean result = AppSession.getApi(context).sendRequest(this.idmember,this.type);

                return result;
            } catch (SJBException e) {
                error = e.getMessage();
                return false;
            }
        }
        protected void onPostExecute(Boolean isadded) {
            if (isadded) {
                if(this.type.equals("1")) {
                    coeurBtt.setClickable(false);
                    coeurBtt.setImageResource(R.drawable.heart_rose);
                    AppSession.currentMember.isInvitations="1";
                    AppSession.currentMember.isType="1";
                }
                else{
                    plusButt.setClickable(false);
                    plusButt.setImageResource(R.drawable.plus_vert);
                    AppSession.currentMember.isType="2";
                    AppSession.currentMember.isInvitations="1";
                }
                Toast.makeText(context, R.string.success_save_request, Toast.LENGTH_SHORT).show();

            } else {
                //if (error.equals("NOTHING_FOUND")) error = context.getResources().getString(R.string.error_nothing_found);
                showError();
                // Toast.makeText(context, R.string.success_delete_saved_job, Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(isadded);
        }

    }
    class addFavorisTask extends ApiTaskNoDialog<String, Void, Boolean> {
        String idmember;
        addFavorisTask(Context context) {
            super(context);
        }
        protected void onPreExecute() {
            favButt.setClickable(false);
        }

        protected Boolean doInBackground(String... credentials) {
            try {
                this.idmember = credentials[0];
                Boolean result = AppSession.getApi(context).saveFavoris(this.idmember);
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

               // favButt.setClickable(false);
                favButt.setImageResource(R.drawable.etoile_rose);
                AppSession.currentMember.isFavoris="1";

            } else {
                //if (error.equals("NOTHING_FOUND")) error = context.getResources().getString(R.string.error_nothing_found);
                showError();
                // Toast.makeText(context, R.string.success_delete_saved_job, Toast.LENGTH_SHORT).show();
            }
            favButt.setClickable(true);
            Toast.makeText(context, R.string.success_save_fav, Toast.LENGTH_SHORT).show();

            super.onPostExecute(isadded);
        }

    }
    class favRequestTask extends ApiTaskNoDialog<Void, Void, FavorisList> {
        //  String idmember;
        favRequestTask(Context context) {
            super(context);
        }

        protected FavorisList doInBackground(Void... credentials) {
            try {
                // this.idmember = credentials[0];
                FavorisList result = AppSession.getApi(context).favRequest(AppSession.currentfavPage);
                //  Log.e("questionnaire",questionnaire.toString());
               /* if (result) {
                    AppSession.currentMember.mem_opt_favoris = "on";
                }*/
                return result;
            } catch (SJBException e) {
                error = e.getMessage();
                return null;
            }
        }

        public void onPostExecute(FavorisList isadded) {

            if (isadded != null) {
                AppSession.favoris.clear();
                AppSession.favoris = isadded.favoris;
                AppSession.currentfavorisNumber = isadded.listingsNumber;


            }
            super.onPostExecute(isadded);
        }

    }

    class deleteFavorisTask extends ApiTaskNoDialog<String, Void, Boolean> {
        String idfav;
        deleteFavorisTask(Context context) {
            super(context);
        }
        protected void onPreExecute() {
            favButt.setClickable(false);
        }
        protected Boolean doInBackground(String... credentials) {
            try {
                this.idfav = credentials[0];
                Boolean result = AppSession.getApi(context).deleteFromFavoris(this.idfav);
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

                favButt.setImageResource(R.drawable.favoris_gris);
                AppSession.currentMember.isFavoris="0";
               // FavorisAdapter.currentmemb.isFavoris="0";
                Toast.makeText(context, R.string.success_delete_Favoris, Toast.LENGTH_SHORT).show();
                  //  int position=AppSession.favoris.indexOf(AppSession.currentMember);

                    //Log.e("position ",String.valueOf(FavorisFragment.slectedpos));
                for(int i=0;i<AppSession.favoris.size();i++)
                {
                    if(AppSession.favoris.get(i).membre.getId().equals(AppSession.currentMember.getId())) {
                        AppSession.favoris.remove(i);
                        break;
                    }
                }





            } else {
                //if (error.equals("NOTHING_FOUND")) error = context.getResources().getString(R.string.error_nothing_found);
                showError();
                // Toast.makeText(context, R.string.success_delete_saved_job, Toast.LENGTH_SHORT).show();
            }
            favButt.setClickable(true);

            super.onPostExecute(isadded);
        }

    }
    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


}


