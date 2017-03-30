package chorus.chorus.com.chorus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class InvitationProfilActivity extends AppCompatActivity {
    private Toolbar toolbar;
    String type;
    ImageButton favButt;

    private ImageButton coeurBtt,plusButt;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation_profil);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbarTitle();
        TextView textUsername = (TextView) findViewById(R.id.username);
        TextView age = (TextView) findViewById(R.id.age);
        TextView verset = (TextView) findViewById(R.id.verset);
        TextView distance = (TextView) findViewById(R.id.distance);
        TextView profession = (TextView) findViewById(R.id.profession);
        favButt =(ImageButton)findViewById(R.id.favButt) ;
        final String is= AppSession.currentMember.getIsFavoris();
        favButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (is.equals("0")){
                    new addFavorisTask(getApplicationContext()).execute(AppSession.currentMember.getId());
                }     }
        });
        coeurBtt=(ImageButton)findViewById(R.id.coeurButt);
        plusButt=(ImageButton)findViewById(R.id.plusButt);
        type = (String) getIntent().getSerializableExtra("type");
        final String idInvitation=(String) getIntent().getSerializableExtra("idInvit");
        coeurBtt.setClickable(true);
        plusButt.setClickable(true);
        if(type.equals("1")) {
            coeurBtt.setImageResource(R.drawable.heart_rose);
        }else{

            plusButt.setImageResource(R.drawable.plus_vert);
        }
        plusButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(type.equals("2")){
                    new InvitationProfilActivity.addContactTask(getApplicationContext()).execute(AppSession.currentMember.getId(),type);
                    new InvitationProfilActivity.DeleteInvitTask(getApplicationContext()).execute(idInvitation);
                }
                else{
                    new InvitationProfilActivity.DeleteInvitTask(getApplicationContext()).execute(idInvitation);
                    Intent i=new Intent(getApplicationContext(),AutreFoisActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                    finish();
                }
                new InvitRequestTask(getApplicationContext()).execute();
                new ContactRequestTask(getApplicationContext()).execute();
                new favRequestTask(getApplicationContext()).execute();
            }
        });
        coeurBtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(type.equals("1")){
                    new InvitationProfilActivity.addContactTask(getApplicationContext()).execute(AppSession.currentMember.getId(),type);
                    new InvitationProfilActivity.DeleteInvitTask(getApplicationContext()).execute(idInvitation);
                }
                else{
                    new InvitationProfilActivity.DeleteInvitTask(getApplicationContext()).execute(idInvitation);
                    Intent i=new Intent(getApplicationContext(),AutreFoisActivity.class);
                    startActivity(i);
                    finish();

                }
                new InvitRequestTask(getApplicationContext()).execute();
                new ContactRequestTask(getApplicationContext()).execute();
                new favRequestTask(getApplicationContext()).execute();
            }
        });
       /* coeurBtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorvert));
            // getWindow().setStatusBarColor(ContextCompat.getColor(this,(AppSession.currentMember.sexe.equals("M"))? R.color.colorvertclair : R.color.colorrose));
        }
        if (AppSession.currentMember != null) {
            verset.setText(AppSession.currentMember.verset);
            textUsername.setText(AppSession.currentMember.username+", "+toString().valueOf(AppSession.currentMember.getAge(AppSession.currentMember.date_naiss)));
            profession.setText(AppSession.currentMember.profession);
            if((int)Math.round((Double.parseDouble(AppSession.currentMember.distance)))<1)
                distance.setText(Math.round(Double.parseDouble(AppSession.currentMember.distance)*1000) + " m");
            else
                distance.setText((int)Math.round(Double.parseDouble(AppSession.currentMember.distance)) + " km");
            ImageView imageLogo = (ImageView) this.findViewById(R.id.logo);
            LinearLayout Linear1=(LinearLayout)findViewById(R.id.sexeLayout);
            if(AppSession.currentMember.sexe.equals("F") )
                Linear1.setBackgroundColor(Color.parseColor("#d7266a"));
            else
                Linear1.setBackgroundColor(Color.parseColor("#26dfac"));

            if (AppSession.currentMember.Logo.thumb_file_url != null) {
                Log.e("logo url", this.getResources().getString(R.string.api_base_url) + AppSession.currentMember.Logo.thumb_file_url);

                Picasso.with(getBaseContext()).load(this.getResources().getString(R.string.api_base_url) + "/" + AppSession.currentMember.Logo.thumb_file_url).into(imageLogo);

                imageLogo.setVisibility(View.VISIBLE);
            } else {
                imageLogo.setVisibility(View.GONE);
            }

         /*   coeurBtt.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    new InvitationProfilActivity.addRequestTask(getApplicationContext()).execute(AppSession.currentMember.getId(),"1");
                }

            });
            plusButt.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    new InvitationProfilActivity.addRequestTask(getApplicationContext()).execute(AppSession.currentMember.getId(),"2");
                }

            });*/
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
        Intent intent=new Intent(getApplicationContext(),MainContacts.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        finish();
        PageFragment.searchInvitationsAdapter.notifyDataSetChanged();       /* new InvitRequestTask(getApplicationContext()).execute();
        new ContactRequestTask(getApplicationContext()).execute();
       new favRequestTask(getApplicationContext()).execute();*/
        super.onBackPressed();


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
    class addContactTask extends ApiTaskNoDialog<String, Void, Boolean> {
        String idmember;
        String type;
        addContactTask(Context context) {
            super(context);
        }

        protected Boolean doInBackground(String... credentials) {
            try {
                this.idmember = credentials[0];
                this.type = credentials[1];
                Boolean result = AppSession.getApi(context).saveRequest(this.idmember,this.type);
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
                coeurBtt.setClickable(false);
                plusButt.setClickable(false);

                Intent i=new Intent(getApplicationContext(),ChorusActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();


            }

            //  Toast.makeText(context, R.string.success_save_request, Toast.LENGTH_SHORT).show();
            super.onPostExecute(isadded);
        }

    }

    class DeleteInvitTask extends ApiTaskNoDialog<String, Void, Boolean> {
        String idInvit;
        DeleteInvitTask(Context context) {
            super(context);
        }

        protected Boolean doInBackground(String... credentials) {
            try {
                this.idInvit = credentials[0];
                Boolean result = AppSession.getApi(context).DeleteInvit(this.idInvit);
                return result;
            } catch (SJBException e) {
                error = e.getMessage();
                return false;
            }
        }
        protected void onPostExecute(Boolean isadded) {
            if (isadded) {


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

                favButt.setClickable(false);
                favButt.setImageResource(R.drawable.etoile_rose);
                AppSession.currentMember.isFavoris="1";



            } else {
                //if (error.equals("NOTHING_FOUND")) error = context.getResources().getString(R.string.error_nothing_found);
                showError();
                // Toast.makeText(context, R.string.success_delete_saved_job, Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(isadded);
        }

    }
    class InvitRequestTask extends ApiTaskNoDialog<Void, Void,InvitationsList> {
        //  String idmember;
        InvitRequestTask(Context context) {
            super(context);
        }

        protected InvitationsList doInBackground(Void... credentials) {
            try {
                // this.idmember = credentials[0];
                InvitationsList result = AppSession.getApi(context).invitRequest(AppSession.currentInvitPage);
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
        public void onPostExecute(InvitationsList isadded) {

            AppSession.invitations=null;
            if (isadded!=null) {


                AppSession.invitations=isadded.invitations;
                AppSession.currentinvitationNumber=isadded.listingsNumber;

            }

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

                AppSession.favoris = isadded.favoris;
                AppSession.currentfavorisNumber = isadded.listingsNumber;


            }
            super.onPostExecute(isadded);
        }

    }



}


