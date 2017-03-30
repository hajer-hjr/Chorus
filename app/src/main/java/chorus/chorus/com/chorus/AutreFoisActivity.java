package chorus.chorus.com.chorus;

import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.InputStream;

import static chorus.chorus.com.chorus.R.id.toolbar;

public class AutreFoisActivity extends AppCompatActivity {
    private Toolbar toolbar;
    ImageButton imgBut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autre_fois);
        User user=AppSession.currentUser;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setToolbarTitle();
        imgBut=(ImageButton)findViewById(R.id.goback);
        imgBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });

        setToolbarTitle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorvertclair));
            // getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorvert));
        }
        Membre member=AppSession.currentMember;
        if (member.Logo.thumb_file_url != null) {

            DownloadImageTask ta= new DownloadImageTask((CircularImageView) findViewById(R.id.imageMembre));
            ta.execute(getApplicationContext().getResources().getString(R.string.api_base_url) + "/" + member.Logo.thumb_file_url);
            // imageLogo.setVisibility(View.VISIBLE);
        } else {
            //imageLogo.setVisibility(View.GONE);
        }

        if (user.Logo.thumb_file_url!=null){
            DownloadImageTask ta= new DownloadImageTask((CircularImageView) findViewById(R.id.imageUser));
            ta.execute(getApplicationContext().getResources().getString(R.string.api_base_url) + "/" + user.Logo.thumb_file_url);
        }

    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(getApplicationContext(),MainContacts.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        finish();
       /* new InvitRequestTask(getApplicationContext()).execute();
        new ContactRequestTask(getApplicationContext()).execute();
       new favRequestTask(getApplicationContext()).execute();*/
        super.onBackPressed();


    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
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
    private void setToolbarTitle() {
        getSupportActionBar().setTitle("Contacts");
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

            AppSession.invitations.clear();
            if (isadded!=null) {

                AppSession.invitations=isadded.invitations;
                AppSession.currentinvitationNumber=isadded.listingsNumber;

            }
            Intent intent=new Intent(getApplicationContext(),ContactsManagerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
            finish();
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
            if (isadded!=null) {
                AppSession.contacts=isadded.contacts;
                AppSession.currentcontactsNumber=isadded.listingsNumber;



            } else {
                //if (error.equals("NOTHING_FOUND")) error = context.getResources().getString(R.string.error_nothing_found);
                showError();
            }
            super.onPostExecute(isadded);
        }

    }


    class favRequestTask extends ApiTaskNoDialog<Void, Void,FavorisList> {
        //  String idmember;
        favRequestTask(Context context) {
            super(context);
        }

        protected FavorisList doInBackground(Void... credentials) {
            try {
                // this.idmember = credentials[0];
                FavorisList  result = AppSession.getApi(context).favRequest(AppSession.currentfavPage);
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
            if (isadded!=null) {
                AppSession.favoris=isadded.favoris;
                AppSession.currentfavorisNumber=isadded.listingsNumber;



            } else {

            }
            super.onPostExecute(isadded);
        }

    }


}

