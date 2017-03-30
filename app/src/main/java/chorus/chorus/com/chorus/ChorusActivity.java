package chorus.chorus.com.chorus;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

import static chorus.chorus.com.chorus.R.id.toolbar;

public class ChorusActivity extends AppCompatActivity {
    private Toolbar toolbar;
    ImageButton imgBut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chorus);
        User user=AppSession.currentUser;
        Membre member=AppSession.currentMember;
        String memname=AppSession.currentMember.lastname;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mem=(TextView) findViewById(R.id.membreName);
       // RunAnimation();
        mem.setText(memname);
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
       if (user.Logo.thumb_file_url != null) {
            ChorusActivity.DownloadImageTask ta = new ChorusActivity.DownloadImageTask((CircularImageView) findViewById(R.id.imageUser));
            ta.execute(getApplicationContext().getResources().getString(R.string.api_base_url) + "/" + user.Logo.thumb_file_url);

        }

        if (member.Logo.thumb_file_url != null) {

            ChorusActivity.DownloadImageTask t= new ChorusActivity.DownloadImageTask((CircularImageView) findViewById(R.id.imageMembre));
            t.execute(getApplicationContext().getResources().getString(R.string.api_base_url) + "/" + member.Logo.thumb_file_url);
            // imageLogo.setVisibility(View.VISIBLE);
        } else {
            //imageLogo.setVisibility(View.GONE);
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
   /* private void RunAnimation()
    {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.scale);
        a.reset();
        TextView tv = (TextView) findViewById(R.id.chorusText);
        TextView t = (TextView) findViewById(R.id.choruss);
        TextView tb = (TextView) findViewById(R.id.ext);
        tv.clearAnimation();
        tv.startAnimation(a);
        t.clearAnimation();
        t.startAnimation(a);
        tb.clearAnimation();
        tb.startAnimation(a);
    }*/
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
                   AppSession.favoris.clear();
                    AppSession.favoris = isadded.favoris;
                    AppSession.currentfavorisNumber = isadded.listingsNumber;


                }
                super.onPostExecute(isadded);
            }

        }


    }

