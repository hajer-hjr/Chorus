package chorus.chorus.com.chorus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.InputStream;

public class MemberProfilContactActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageButton coeurBtt,plusButt,favButt,addmesg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_profil_contact);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbarTitle();
        TextView textUsername = (TextView) findViewById(R.id.username);
        TextView age = (TextView) findViewById(R.id.age);
        addmesg = (ImageButton) findViewById(R.id.addmessage);
       /* TextView distance = (TextView) findViewById(R.id.distance);
        if((int)Math.round((Double.parseDouble(AppSession.currentMember.distance)))<1)
            distance.setText(+Math.round(Double.parseDouble(AppSession.currentMember.distance)*1000) + " m");
        else
            distance.setText((int)Math.round(Double.parseDouble(AppSession.currentMember.distance)) + " km");*/
        TextView profession = (TextView) findViewById(R.id.profession);
        TextView verset = (TextView) findViewById(R.id.verset);
        profession.setText(AppSession.currentMember.profession);


        favButt = (ImageButton) findViewById(R.id.favButt);
        plusButt = (ImageButton) findViewById(R.id.plusButt);
        coeurBtt = (ImageButton) findViewById(R.id.coeurButt);
//        Log.e("ttt",FavorisAdapter.currentmemb.isInvitations);
        if (AppSession.currentMember != null) {
            age.setText(toString().valueOf(AppSession.currentMember.getAge(AppSession.currentMember.date_naiss)));
            textUsername.setText(AppSession.currentMember.username + ", ");
            profession.setText(AppSession.currentMember.profession);
            verset.setText(decrypt(this, AppSession.currentMember.verset));

         /*   if ((int) Math.round((Double.parseDouble(AppSession.currentMember.distance))) < 1)
                distance.setText("à " + Math.round(Double.parseDouble(AppSession.currentMember.distance) * 1000) + " m");
            else
                distance.setText("à " + (int) Math.round(Double.parseDouble(AppSession.currentMember.distance)) + " km");*/
            //LinearLayout Linear1=(LinearLayout)findViewById(R.id.sexeLayout);
            FrameLayout Linear1 = (FrameLayout) findViewById(R.id.sexeLayout);
            // CircularImageView imageLogo =(CircularImageView) findViewById(R.id.logo);
            ImageView imageLogo = (ImageView) findViewById(R.id.logo);

            //imageLogo.setBorderWidth(0);


            if (AppSession.currentMember.Logo.thumb_file_url != null) {
                Log.e("logo url", this.getResources().getString(R.string.api_base_url) + AppSession.currentMember.Logo.thumb_file_url);

                DownloadImageTask ta = new DownloadImageTask(imageLogo);
                ta.execute(getApplicationContext().getResources().getString(R.string.api_base_url) + "/" + AppSession.currentMember.Logo.thumb_file_url);
                if (AppSession.currentMember.sexe.equals("F"))
                    Linear1.setBackgroundColor(Color.parseColor("#d7266a"));
                else

                    Linear1.setBackgroundColor(Color.parseColor("#3db39d"));

                imageLogo.setVisibility(View.VISIBLE);
            } else {
                imageLogo.setVisibility(View.GONE);
            }
        }
      /*  if (FavorisAdapter.currentmemb.isInvitations=="1")
        {
           if (FavorisAdapter.currentmemb.isType=="1")
            {
                coeurBtt.setImageResource(R.drawable.heart_rose);
                plusButt.setClickable(false);
                coeurBtt.setClickable(false);
            }
            else{
            plusButt.setImageResource(R.drawable.plus_vert);
            plusButt.setClickable(false);
            coeurBtt.setClickable(false);

        }}*/
       // setContentView(R.layout.activity_member_profil_contact);


        ImageButton like_button = (ImageButton) findViewById(R.id.likeBut);
        final TextView likesText = (TextView) findViewById(R.id.likes);

        like_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(android.view.View v) {


                Log.d("myTag","here");

                likesText.setText("new");
                new CountLikesTask(v.getContext()).execute();

                //new LikesTask(v.getContext()).execute();
                //int a = Integer.parseInt(AppSession.currentMember.getIsLikes());
                //Log.d("myTag", String.valueOf(a));


            }


        });

       /* favButt.setClickable(true);

        favButt.setImageResource(R.drawable.etoile_rose);


        favButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FavorisAdapter.currentmemb.isFavoris.equals("0")) {
                    new addFavorisTask(getApplicationContext()).execute(FavorisAdapter.currentmemb.id);
                } else {
                    new deleteFavorisTask(getApplicationContext()).execute(FavorisAdapter.getFavId());
                }
            }
        });*/


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorvert));
            // getWindow().setStatusBarColor(ContextCompat.getColor(this,(AppSession.currentMember.sexe.equals("M"))? R.color.colorvertclair : R.color.colorrose));
        }
        if (FavorisAdapter.currentmemb != null) {

            textUsername.setText(FavorisAdapter.currentmemb.username + ", " + toString().valueOf(FavorisAdapter.currentmemb.getAge(FavorisAdapter.currentmemb.date_naiss)));
            age.setText(FavorisAdapter.currentmemb.equals("M") ? "Homme" : "Femme");
            profession.setText(FavorisAdapter.currentmemb.profession);
            ImageView imageLogo = (ImageView) this.findViewById(R.id.logo);
            LinearLayout Linear1 = (LinearLayout) findViewById(R.id.sexeLayout);
            if (FavorisAdapter.currentmemb.sexe.equals("F"))
                Linear1.setBackgroundColor(Color.parseColor("#d7266a"));
            else
                Linear1.setBackgroundColor(Color.parseColor("#26dfac"));

            if (FavorisAdapter.currentmemb.Logo.thumb_file_url != null) {
                Log.e("logo url", this.getResources().getString(R.string.api_base_url) + FavorisAdapter.currentmemb.Logo.thumb_file_url);

                Picasso.with(getBaseContext()).load(this.getResources().getString(R.string.api_base_url) + "/" + FavorisAdapter.currentmemb.Logo.thumb_file_url).into(imageLogo);

                imageLogo.setVisibility(View.VISIBLE);
            } else {
                imageLogo.setVisibility(View.GONE);
            }


            if (FavorisAdapter.currentmemb.equals("0")) {
                plusButt.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        new addRequestTask(getApplicationContext()).execute(FavorisAdapter.currentmemb.getId(), "2");
                    }

                });
                coeurBtt.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        new addRequestTask(getApplicationContext()).execute(FavorisAdapter.currentmemb.getId(), "1");
                    }

                });

            }
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


            // }


            addmesg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (AppSession.currentUser != null) {
                        //AppSession.selectedCat=0;
                        new MessagesUserListeTask(getApplicationContext()).execute("", AppSession.currentMember.id);
                    } else {

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                }

            });
        }
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
    @Override
    public void onBackPressed() {

        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        //new favRequestTask(getApplicationContext()).execute();
      //  new ContactRequestTask(getApplicationContext()).execute();

        super.onBackPressed();
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
                    FavorisAdapter.currentmemb.isInvitations="1";
                    FavorisAdapter.currentmemb.isType="1";
                }
                else{
                    plusButt.setClickable(false);
                    plusButt.setImageResource(R.drawable.plus_vert);
                    FavorisAdapter.currentmemb.isType="2";
                    FavorisAdapter.currentmemb.isInvitations="2";
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

                favButt.setImageResource(R.drawable.etoile_rose);
                FavorisAdapter.currentmemb.isFavoris="1";

            } else {
                //if (error.equals("NOTHING_FOUND")) error = context.getResources().getString(R.string.error_nothing_found);
                showError();
                // Toast.makeText(context, R.string.success_delete_saved_job, Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(context, R.string.success_save_fav, Toast.LENGTH_SHORT).show();

            super.onPostExecute(isadded);
        }

    }
    class deleteFavorisTask extends ApiTaskNoDialog<String, Void, Boolean> {
        String idfav;
        deleteFavorisTask(Context context) {
                super(context);
        }

        protected Boolean doInBackground(String... credentials) {
            try {
                this.idfav = credentials[0];
                Boolean result = AppSession.getApi(context).deleteFavoris(this.idfav);
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
                FavorisAdapter.currentmemb.isFavoris="0";

            } else {
                //if (error.equals("NOTHING_FOUND")) error = context.getResources().getString(R.string.error_nothing_found);
                showError();
                // Toast.makeText(context, R.string.success_delete_saved_job, Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(context, R.string.success_delete_Favoris, Toast.LENGTH_SHORT).show();

            super.onPostExecute(isadded);
        }

    }
    class ContactRequestTask extends ApiTaskNoDialog<Void, Void,ContactsList> {
        //  String idmember;
        ContactRequestTask(Context context) {
            super(context);
        }

        protected ContactsList doInBackground(Void... credentials) {
            try {
                // this.idmember = credentials[0];
                ContactsList result = AppSession.getApi(context).contactRequest(AppSession.currentContactPage);
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

        public void onPostExecute(ContactsList isadded) {
            AppSession.contacts=null;
            if (isadded != null) {

                AppSession.contacts = isadded.contacts;
                AppSession.currentcontactsNumber = isadded.listingsNumber;
            }

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
               //
                // AppSession.favoris =null;

             //   AppSession.favoris = isadded.favoris;
              //  AppSession.currentfavorisNumber = isadded.listingsNumber;


            }
            Intent i=new Intent(getApplicationContext(),ContactsManagerActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);

            finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
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
    private static String decrypt(Context cont, String value) {
        try {
            return new String(Base64.decode(value, Base64.DEFAULT));
        }
        catch (IllegalArgumentException e) {
            // TODO: handle exception
            return "";
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    //////////////////////////////////////////////////////////////
    class CountLikesTask extends ApiTask<String, Void, Boolean> {

        public CountLikesTask(Context context) {
            super(context,R.string.dialog_registering );
        }

        protected Boolean doInBackground(String... data) {

            try {
                Log.d("before count likes "," ...");
               String result = AppSession.getApi(context).countLikes(AppSession.currentMember.verset);
                Log.d("after count likes ",result);

                return true;
            } catch (Exception e) {
                String error = e.getMessage();
                return false;
            }
        }

        protected void onPostExecute(Boolean res) {

            if (res) {

            }
        }
    }
    //////////


}


